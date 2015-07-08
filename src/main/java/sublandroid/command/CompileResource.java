package sublandroid.command;

import java.io.*;

import sublandroid.messages.*;

import org.gradle.tooling.*;
import org.gradle.api.tasks.*;


public class CompileResource extends Command {

	public static final String COMMAND = "compileResource";

	protected static final String PREPARE_DEBUG_DEPS = "prepareDebugDependencies";

	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection) {
		Context context = Context.from(connection);

		context.tasks("generateDebugSources");

		MResourceCompile resourceCompile = new MResourceCompile();

		try {
			context.run();
		} catch (BuildException buildException) {
			processErrors(resourceCompile, context, buildException);
		}

		return null;
	}

	protected void processErrors(MResourceCompile resourceCompile, Context ctx, BuildException buildException) {
		Throwable cause = buildException.getCause();
		while (cause != null) {
			System.out.println(cause.getClass().getName());
			/*if (cause instanceof RuntimeException) {
				RuntimeException taskException = (RuntimeException) cause;

				if (PREPARE_DEBUG_DEPS.equals(taskException.getName())) {
					processManifestError(resourceCompile, ctx, taskException);
				}
			}*/

			cause = cause.getCause();
		}
	}

	protected void processManifestError(MResourceCompile resourceCompile, Context ctx, RuntimeException taskException) {

	}

}