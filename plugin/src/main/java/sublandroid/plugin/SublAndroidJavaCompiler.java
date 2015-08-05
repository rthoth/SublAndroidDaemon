package sublandroid.plugin;

import org.gradle.api.*;
import org.gradle.api.tasks.*;

public class SublAndroidJavaCompiler extends DefaultTask {

	public static final String TASK_NAME = "sublandroidCompileJava";
	public static final String ANDROID_COMPILE_JAVA_TASK_NAME = "compileDebugSources";

	public static SublAndroidJavaCompiler inject(final TaskContainer container) {
		final SublAndroidJavaCompiler task = container.create(TASK_NAME, SublAndroidJavaCompiler.class);

		container.whenTaskAdded(new Action() {
			public void execute(Object object) {
				Task cTask = (Task) object;

				if (ANDROID_COMPILE_JAVA_TASK_NAME.equals(cTask.getName()))
					cTask.finalizedBy(task);
			}
		});

		return task;
	}


	@TaskAction
	public void collectResult() {
		TaskState state = getState();

		System.out.println("Oieee");
		System.out.println(state.getFailure());

		if (2 > 0)
			throw new RuntimeException("Oiiasda");
	}
}