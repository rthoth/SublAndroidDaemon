package sublandroid.plugin;

import javax.inject.Inject;
import java.util.*;

import sublandroid.core.*;

import org.gradle.api.*;
import org.gradle.api.logging.*;
import org.gradle.tooling.provider.model.*;


public class CatchExceptionPlugin implements Plugin<Project> {

	private static final Logger LOGGER = Logging.getLogger("sublandroid.CatchExceptionPlugin");

	private final ToolingModelBuilderRegistry modelBuilderRegistry;

	@Inject
	public CatchExceptionPlugin(ToolingModelBuilderRegistry modelBuilderRegistry) {
		this.modelBuilderRegistry = modelBuilderRegistry;
	}

	@Override
	public void apply(final Project project) {

		final CatchExceptionImpl model = new CatchExceptionImpl(project);
		modelBuilderRegistry.register(model.new ModelBuilder());
	}
}