package sublandroid.plugin;

import java.util.*;
import java.io.*;

import sublandroid.core.*;

import org.gradle.api.*;
import org.gradle.api.internal.tasks.ContextAwareTaskAction;
import org.gradle.api.internal.tasks.TaskExecutionContext;
import org.gradle.api.execution.*;
import org.gradle.api.logging.*;
import org.gradle.api.tasks.*;

public class CatchExceptionModelImpl implements CatchExceptionModel, TaskExecutionListener, Serializable {

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
			ACTION_LOGGER.info("Trying execute {} for {}", action, task.getPath());

			try {
				action.execute(task);
			} catch (Throwable throwable) {
				//throw new RuntimeException(throwable);
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

	private static final Logger ACTION_LOGGER = Logging.getLogger("sublandroid.CatchExceptionModel.NotFailAction");
	private static final Logger LOGGER = Logging.getLogger("sublandroid.CatchExceptionModel");

	private List<String> tasks = new LinkedList<>();
	private transient boolean notSkip = true;

	@Override
	public List<String> getTasks() {
		return tasks;
	}

	@Override
	public void afterExecute(Task task, TaskState taskState) {
		// NOP
	}

	@Override
	public void beforeExecute(Task task) {
		/*LOGGER.info("Execute real actions for {}", task.getPath());

		final Entry entry = entries.poll();

		assert entry.task == task;

		for (Action<? super Task> action : entry.actions) {
			LOGGER.info("Trying execute {} of {}", action, task.getPath());
			action.execute(task);
		}*/
	}

	protected void handleError(Throwable throwable) {
		if (notSkip && throwable != null) {
			notSkip = false;

			LOGGER.info("Ops, um erro!", throwable);
		}
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