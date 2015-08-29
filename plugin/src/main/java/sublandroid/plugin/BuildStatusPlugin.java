package sublandroid.plugin;

import javax.inject.Inject;
import java.util.*;

import sublandroid.core.*;

import org.gradle.api.*;
import org.gradle.api.logging.*;
import org.gradle.tooling.provider.model.*;


public class BuildStatusPlugin implements Plugin<Project> {

	private final ToolingModelBuilderRegistry modelBuilderRegistry;

	@Inject
	public BuildStatusPlugin(ToolingModelBuilderRegistry modelBuilderRegistry) {
		this.modelBuilderRegistry = modelBuilderRegistry;
	}

	@Override
	public void apply(final Project project) {
		final BuildStatusImpl model = new BuildStatusImpl(project);

		modelBuilderRegistry.register(model.new ModelBuilder());
	}
}