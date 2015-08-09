package sublandroid.plugin;

import javax.inject.Inject;
import java.util.*;

import sublandroid.core.*;

import org.gradle.api.*;
import org.gradle.api.execution.*;
import org.gradle.api.invocation.*;
import org.gradle.api.logging.*;
import org.gradle.tooling.provider.model.*;


public class CatchExceptionModelPlugin implements Plugin<Project> {

	private static final String MODEL_CLASS_NAME = CatchExceptionModel.class.getName();
	private static final Logger LOGGER = Logging.getLogger("sublandroid.CatchExceptionModelPlugin");

	private static class CatchExceptionModelBuilder implements ToolingModelBuilder {

		@Override
		public boolean canBuild(String modelName) {
			return MODEL_CLASS_NAME.equals(modelName);
		}

		@Override
		public Object buildAll(String modelName, Project project) {
			return project.getExtensions().getByName(MODEL_CLASS_NAME);
		}
	}

	private final ToolingModelBuilderRegistry modelBuilderRegistry;

	@Inject
	public CatchExceptionModelPlugin(ToolingModelBuilderRegistry modelBuilderRegistry) {
		this.modelBuilderRegistry = modelBuilderRegistry;
	}

	@Override
	public void apply(final Project project) {
		
		final Gradle gradle = project.getGradle();
		final TaskExecutionGraph executionGraph = gradle.getTaskGraph();


		final CatchExceptionModelImpl model = new CatchExceptionModelImpl();

		project.getExtensions().add(MODEL_CLASS_NAME, model);
		executionGraph.addTaskExecutionListener(model);

		modelBuilderRegistry.register(new CatchExceptionModelBuilder());

		executionGraph.addTaskExecutionGraphListener(new TaskExecutionGraphListener() {

			@Override
			public void graphPopulated(final TaskExecutionGraph graph) {
				List<Task> tasks = graph.getAllTasks();

				for (Task task : tasks) {
					model.put(task);
				}
			}
		});
	}
}