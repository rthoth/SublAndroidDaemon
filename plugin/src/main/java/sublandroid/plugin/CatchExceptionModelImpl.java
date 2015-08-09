package sublandroid.plugin;

import java.util.*;
import java.io.*;

import sublandroid.core.*;

import org.gradle.api.*;
import org.gradle.api.execution.*;
import org.gradle.api.invocation.*;
import org.gradle.api.internal.tasks.ContextAwareTaskAction;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.internal.tasks.execution.TaskValidator;
import org.gradle.api.logging.*;
import org.gradle.api.tasks.*;
import org.gradle.tooling.provider.model.*;

public class CatchExceptionModelImpl implements 
CatchExceptionModel, TaskExecutionListener, Serializable, TaskExecutionGraphListener {

	private static final Logger ACTION_LOGGER = Logging.getLogger("sublandroid.CatchExceptionModel.NotFailAction");
	private static final Logger LOGGER = Logging.getLogger("sublandroid.CatchExceptionModel");
	private static final String MODEL_NAME = CatchExceptionModel.class.getName();

	public enum StatusImpl implements Status {
		ActionError(true, false, false, false),
		Ok(false, true, false, false),
		UnexpectedValidationError(false, false, true, false),
		ValidationError(false, false, false, true);

		private final boolean actionError;
		private final boolean ok;
		private final boolean unexpectedValidationError;
		private final boolean validationError;

		private StatusImpl(boolean actionError, boolean ok, boolean unexpectedValidationError, boolean validationError) {
			this.actionError = actionError;
			this.ok = ok;
			this.unexpectedValidationError = unexpectedValidationError;
			this.validationError = validationError;
		}

		public boolean isActionError() {
			return actionError;
		}

		public boolean isOk() {
			return ok;
		}

		public boolean isUnexpectedValidationError() {
			return unexpectedValidationError;
		}

		public boolean isValidationError() {
			return validationError;
		}
	}

	public class ModelBuilder implements ToolingModelBuilder {

		@Override
		public Object buildAll(String modelName, Project project) {
			if (MODEL_NAME.equals(modelName))
				return CatchExceptionModelImpl.this;
			
			throw new IllegalArgumentException(modelName);
		}

		@Override
		public boolean canBuild(String modelName) {
			return MODEL_NAME.equals(modelName);
		}
	}

	private class NotFailAction implements Action<Task> {

		protected final Action<? super Task> action;
		protected final Task task;

		public NotFailAction(final Action<? super Task> action, final Task task) {
			this.action = action;
			this.task = task;

			ACTION_LOGGER.info("New for {} in {}", action, task.getPath());
		}

		@Override
		public void execute(final Task task) {

			if (status == StatusImpl.Ok) {
				try {
					action.execute(task);

				} catch (StopActionException | StopExecutionException stopException) {
					throw stopException;

				} catch (Throwable throwable) {
					status = StatusImpl.ActionError;
					error = throwable;
					failedTask(task);
					throw new StopExecutionException();
				}
			} else {
				LOGGER.info("Invalid status...skiping!");
				throw new StopExecutionException();
			}
		}
	}

	// Argh...
	private class NotFailActionContext extends NotFailAction implements ContextAwareTaskAction {

		protected final ContextAwareTaskAction contextAware;

		public NotFailActionContext(final Action<? super Task> action, final Task task) {
			super(action, task);
			contextAware = (ContextAwareTaskAction) action;
		}

		@Override
		public void contextualise(final TaskExecutionContext context) {
			ACTION_LOGGER.info("Context {} for {}", context, contextAware);
			contextAware.contextualise(context);
		}
	}

	private List<String> tasks = new LinkedList<>();

	private Throwable error = null;

	private List<String> errors = new LinkedList<>();

	private String failedTaskName = null;

	private String failedTaskPath = null;

	private Status status = StatusImpl.Ok;

	private transient HashMap<Task, List<TaskValidator>> validatorsMap = new HashMap<>();

	public CatchExceptionModelImpl(final Project project) {
		Gradle gradle = project.getGradle();
		TaskExecutionGraph graph = gradle.getTaskGraph();

		graph.addTaskExecutionGraphListener(this);
		graph.addTaskExecutionListener(this);
	}

	@Override
	public List<String> getTasks() {
		return tasks;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Throwable getError() {
		return error;
	}

	@Override
	public List<String> getErrors() {
		return errors;
	}

	@Override
	public String getFailedTaskName() {
		return failedTaskName;
	}

	@Override
	public String getFailedTaskPath() {
		return failedTaskPath;
	}

	@Override
	public void afterExecute(Task task, TaskState taskState) {
		// NOP
	}

	@Override
	public void beforeExecute(Task task) {

		List<TaskValidator> validators = validatorsMap.get(task);

		if (validators != null && status == StatusImpl.Ok) {

			LOGGER.info("Validating {} ...", task.getPath());

			for (TaskValidator taskValidator : validators) {
				ArrayList<String> messages = new ArrayList<>();

				try {
					taskValidator.validate((DefaultTask) task, messages);
				} catch (Throwable throwable) {
					LOGGER.info("UnexpectedValidationError", throwable);
					handleUnexpectedValidationError(task, throwable);
					break;
				}

				if (!messages.isEmpty()) {
					LOGGER.info("ValidationError");
					handleValidationError(task, messages);
					break;
				} else
					LOGGER.info("Valid!");
			}
		}
	}


	private void failedTask(Task task) {
		failedTaskName = task.getName();
		failedTaskPath = task.getPath();
	}

	public void graphPopulated(final TaskExecutionGraph graph) {
		for (Task task : graph.getAllTasks()) {

			List<TaskValidator> validators = null;

			if (task instanceof DefaultTask) {
				final DefaultTask defTask = (DefaultTask) task;
				validators = new ArrayList<>(defTask.getValidators());
				defTask.getValidators().clear();
			}

			List<Action<? super Task>> oldActions = task.getActions();
			List<Action<? super Task>> newActions = new ArrayList<>(oldActions.size());

			NotFailAction newAction;

			for (Action<? super Task> action : oldActions) {
				if (action instanceof ContextAwareTaskAction)
					newAction = new NotFailActionContext(action, task);
				else
					newAction = new NotFailAction(action, task);

				newActions.add(newAction);
			}

			task.setActions(newActions);
			validatorsMap.put(task, validators);
			// Remove validators...
		}
	}

	protected void handleUnexpectedValidationError(Task task, Throwable throwable) {
		if (status == StatusImpl.Ok) {
			status = StatusImpl.UnexpectedValidationError;
			failedTask(task);
		}

		error = throwable;
	}

	protected void handleValidationError(Task task, List<String> messages) {
		if (status == StatusImpl.Ok) {
			status = StatusImpl.ValidationError;
			failedTask(task);
		}

		errors.addAll(messages);
	}

	protected void put(Task task) {

		List<Action<? super Task>> oldActions = task.getActions();
		List<Action<? super Task>> newActions = new ArrayList<>(oldActions.size());

		NotFailAction newAction;
		for (Action<? super Task> action : oldActions) {


			if (action instanceof ContextAwareTaskAction)
				newAction = new NotFailActionContext(action, task);
			else
				newAction = new NotFailAction(action, task);

			newActions.add(newAction);
		}

		task.setActions(newActions);
	}

}