package sublandroid.command;

import java.io.*;

import sublandroid.messages.*;

import org.gradle.tooling.*;

public abstract class Command {

	protected static class Context {

		public static Context from(ProjectConnection connection) {
			return new Context(connection);
		}

		public BuildLauncher buildLauncher;
		public ByteArrayOutputStream output = new ByteArrayOutputStream();
		public ByteArrayOutputStream error = new ByteArrayOutputStream();
		public ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);

		public Context(final ProjectConnection connection) {
			buildLauncher = connection.newBuild();
			buildLauncher.setStandardError(error);
			buildLauncher.setStandardOutput(output);
			buildLauncher.setStandardInput(input);
		}

		public Context run() {
			buildLauncher.run();
			return this;
		}

		public Context tasks(String... tasks) {
			buildLauncher.forTasks(tasks);
			return this;
		}

	}
	
	public abstract Message execute(MCommand mCommand, ProjectConnection connection);

	protected void println(String message, Object... objects) {
		System.out.println(String.format(message, objects));
	}
}