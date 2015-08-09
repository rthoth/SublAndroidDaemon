package sublandroid.plugin;

import javax.inject.Inject;
import java.util.*;

import sublandroid.core.*;

import org.gradle.api.*;
import org.gradle.api.logging.*;
import org.gradle.tooling.provider.model.*;


public class CatchExceptionModelPlugin implements Plugin<Project> {

	private static final Logger LOGGER = Logging.getLogger("sublandroid.CatchExceptionModelPlugin");

	private final ToolingModelBuilderRegistry modelBuilderRegistry;

	@Inject
	public CatchExceptionModelPlugin(ToolingModelBuilderRegistry modelBuilderRegistry) {
		this.modelBuilderRegistry = modelBuilderRegistry;
	}

	@Override
	public void apply(final Project project) {
		/*
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
		*/

		final CatchExceptionModelImpl model = new CatchExceptionModelImpl(project);
		modelBuilderRegistry.register(model.new ModelBuilder());
	}
}