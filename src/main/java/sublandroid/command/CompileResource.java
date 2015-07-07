package sublandroid.command;

import java.io.*;

import sublandroid.messages.*;

import org.gradle.tooling.*;


public class CompileResource extends Command {

	public static final String COMMAND = "compileResource";

	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection) {
		Context context = Context.from(connection);

		context.buildLauncher.forTasks("generateDebugSources");

		context.buildLauncher.run();

		return new MResourceCompile();
	}

}