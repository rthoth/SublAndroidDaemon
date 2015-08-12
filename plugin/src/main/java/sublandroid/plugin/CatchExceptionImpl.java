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
import org.gradle.api.internal.TaskInternal;
import org.gradle.api.logging.*;
import org.gradle.api.tasks.*;
import org.gradle.tooling.provider.model.*;

/**
 * Implementation
 */
public class CatchExceptionImpl implements 
CatchException, TaskExecutionListener, TaskExecutionGraphListener, Serializable {

	private static final Logger ACTION_LOGGER = Logging.getLogger("sublandroid.CatchException.ProxyAction");
	private static final Logger LOGGER = Logging.getLogger("sublandroid.CatchException");
	private static final Logger VALIDATOR_LOGGER = Logging.getLogger("sublandroid.CatchException.Validator");
	private static final String MODEL_NAME = CatchException.class.getName();

	public class ModelBuilder implements ToolingModelBuilder {

		@Override
		public Object buildAll(String modelName, Project project) {

			LOGGER.debug("Trying create model {}", modelName);

			if (MODEL_NAME.equals(modelName))
				return CatchExceptionImpl.this;
			
			throw new IllegalArgumentException(modelName);
		}

		@Override
		public boolean canBuild(String modelName) {
			if (MODEL_NAME.equals(modelName)) {

				LOGGER.debug("Can create model {}", modelName);
				return true;

			} else {

				LOGGER.debug("Can't create model {}", modelName);
				return false;
			}
		}
	}

	private class ProxyAction implements Action<Task> {

		protected final Action<? super Task> action;
		protected final Task task;

		public ProxyAction(final Action<? super Task> action, final Task task) {
			this.action = action;
			this.task = task;

			ACTION_LOGGER.debug("New for {} in {}", action, task.getPath());
		}

		@Override
		public void execute(final Task task) {

			if (status == Status.Ok) {
				try {
					action.execute(task);
				} catch (StopActionException | StopExecutionException stopException) {
					throw stopException;
				} catch (Throwable throwable) {
					LOGGER.info("Build just invalid, action error!");
					handleActionError(task, throwable);
					throw new StopExecutionException();
				}
			} else {
				LOGGER.info("Build already invalid status...skipping!");
				throw new StopExecutionException("CatchException says nooooooo!");
			}
		}
	}

	// Argh...Something actins needs context
	private class ProxyActionContextAware extends ProxyAction implements ContextAwareTaskAction {

		protected final ContextAwareTaskAction contextAware;

		public ProxyActionContextAware(final Action<? super Task> action, final Task task) {
			super(action, task);
			contextAware = (ContextAwareTaskAction) action;
		}

		@Override
		public void contextualise(final TaskExecutionContext context) {
			ACTION_LOGGER.debug("Setting context to {}", contextAware);
			contextAware.contextualise(context);
		}
	}

	// Gradle validates tasks before invoke actions...
	private class ProxyValidator implements TaskValidator {

		private final TaskValidator validator;

		public ProxyValidator(TaskValidator validator) {
			this.validator = validator;
			VALIDATOR_LOGGER.debug("New to {}", validator);
		}

		@Override
		public void validate(TaskInternal task, Collection<String> messages) {

			if (status == Status.Ok) {
				List<String> validationMessages = new LinkedList<>();
				try {
					this.validator.validate(task, validationMessages);
				} catch (Throwable throwable) {
					VALIDATOR_LOGGER.info("Build just failed, unexpected validation error");
					handleUnexpectedValidationError(task, throwable);
					return;
				}

				if (!validationMessages.isEmpty()) {
					VALIDATOR_LOGGER.info("Build just failed, validation error");
					handleValidationErrors(task, validationMessages);
				}

			} else {
				VALIDATOR_LOGGER.info("Build already invalid, skipping...");
			}
		}
	}

	private Throwable error = null;

	private List<String> errors = new LinkedList<>();

	private String failedTaskName = null;

	private String failedTaskPath = null;

	private Status status = Status.Ok;

	private List<String> tasks = new LinkedList<>();

	public CatchExceptionImpl(final Project project) {
		Gradle gradle = project.getGradle();
		TaskExecutionGraph graph = gradle.getTaskGraph();

		graph.addTaskExecutionGraphListener(this);
		graph.addTaskExecutionListener(this);
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
	public List<String> getTasks() {
		return tasks;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void afterExecute(Task task, TaskState taskState) {
		// NOP
	}

	@Override
	public void beforeExecute(Task task) {
		// NOP
	}


	private void failedTask(Task task) {
		failedTaskName = task.getName();
		failedTaskPath = task.getPath();
	}

	public void graphPopulated(final TaskExecutionGraph graph) {
		for (Task task : graph.getAllTasks()) {

			if (task instanceof DefaultTask) {
				
				final DefaultTask defTask = (DefaultTask) task;
				List<TaskValidator> oldValidators = new ArrayList<>(defTask.getValidators());
				List<TaskValidator> newValidators = new ArrayList<>(oldValidators.size());

				for (TaskValidator oldValidator : oldValidators) {
					newValidators.add(new ProxyValidator(oldValidator));
				}

				defTask.getValidators().clear();
				defTask.getValidators().addAll(newValidators);
			}

			List<Action<? super Task>> oldActions = task.getActions();
			List<Action<? super Task>> newActions = new ArrayList<>(oldActions.size());

			ProxyAction newAction;

			for (Action<? super Task> action : oldActions) {
				if (action instanceof ContextAwareTaskAction)
					newAction = new ProxyActionContextAware(action, task);
				else
					newAction = new ProxyAction(action, task);

				newActions.add(newAction);
			}

			task.setActions(newActions);
			// Remove validators...
		}
	}

	protected void handleActionError(Task task, Throwable throwable) {
		if (status == Status.Ok) {
			status = Status.ActionError;
			error = throwable;
			failedTask(task);
		}
	}

	protected void handleUnexpectedValidationError(Task task, Throwable throwable) {
		if (status == Status.Ok) {
			status = Status.UnexpectedValidationError;
			failedTask(task);
		}

		error = throwable;
	}

	protected void handleValidationErrors(Task task, List<String> messages) {
		if (status == Status.Ok) {
			status = Status.ValidationError;
			failedTask(task);
		}

		errors.addAll(messages);
	}

	protected void put(Task task) {

		List<Action<? super Task>> oldActions = task.getActions();
		List<Action<? super Task>> newActions = new ArrayList<>(oldActions.size());

		ProxyAction newAction;
		for (Action<? super Task> action : oldActions) {


			if (action instanceof ContextAwareTaskAction)
				newAction = new ProxyActionContextAware(action, task);
			else
				newAction = new ProxyAction(action, task);

			newActions.add(newAction);
		}

		task.setActions(newActions);
	}

}