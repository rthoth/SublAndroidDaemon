package sublandroid.plugin;

import java.io.*;

import org.gradle.tooling.*;

public class Utils {

	public static class Context {

		public final ProjectConnection connection;

		public Context(String path) {
			final GradleConnector connector = GradleConnector.newConnector();
			connector.forProjectDirectory(file(path));
			connection = connector.connect();
		}

		public Result execute(String... tasks) {
			return new Result(connection.newBuild(), tasks);
		}
	}

	public static class Result {

		public final OutputStream errStream;
		public final BuildLauncher launcher;
		public final OutputStream outStream;
		public final String[] tasks;

		public Result(final BuildLauncher launcher, String... tasks) {

			try {
				this.launcher = launcher;
				this.tasks = tasks;

				launcher.forTasks(tasks);

				outStream = new ByteArrayOutputStream();
				errStream = new ByteArrayOutputStream();

				launcher.setStandardError(errStream);
				launcher.setStandardOutput(outStream);

				final File init = new File("test-data", "init.gradle");

				launcher.withArguments("--init-script", init.getCanonicalPath());

				launcher.run();
			} catch (Throwable throwable) {
				throw new RuntimeException("Result Exception", throwable);
			}
		}
	}


	public static File file(final String path) {
		final String fixed = path.replace('\\', '/').replace('/', File.separatorChar);

		return new File(fixed);
	}

}