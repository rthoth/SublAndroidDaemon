package sublandroid.plugin;

import org.gradle.api.*;
import org.gradle.api.tasks.*;

public class SublAndroidPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		final TaskContainer container = project.getTasks();
		
		SublAndroidJavaCompiler.inject(container);
	}

}