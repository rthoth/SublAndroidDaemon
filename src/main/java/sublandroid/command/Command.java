package sublandroid.command;

import java.io.*;

import sublandroid.messages.*;

import org.gradle.tooling.*;

public abstract class Command {

	protected static class Context {

		public BuildLauncher buildLauncher;
		public ByteArrayOutputStream output = new ByteArrayOutputStream();
		public ByteArrayOutputStream error = new ByteArrayOutputStream();
		public ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);


		public static Context from(ProjectConnection connection) {
			final Context context = new Context();

			final BuildLauncher buildLauncher = connection.newBuild();
			context.buildLauncher= buildLauncher;
			buildLauncher.setStandardError(context.error);
			buildLauncher.setStandardOutput(context.output);
			buildLauncher.setStandardInput(context.input);

			return context;
		}

	}
	
	public abstract Message execute(MCommand mCommand, ProjectConnection connection);

	protected void println(String message, Object... objects) {
		System.out.println(String.format(message, objects));
	}
}