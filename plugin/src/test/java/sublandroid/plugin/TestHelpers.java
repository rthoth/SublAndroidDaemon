package sublandroid.plugin;

import java.io.*;

import org.gradle.tooling.*;

public class TestHelpers {

	public static class T3<A, B, C> {

		public final A a;
		public final B b;
		public final C c;

		public T3(A a, B b, C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}


	public static class Context {

		public final File directory;
		public final File initScript;
		public final ProjectConnection projConnection;

		public Context(File directory, File initScript) {
			this.directory = directory;
			this.initScript = initScript;

			this.projConnection = GradleConnector.newConnector()
			                      .forProjectDirectory(directory).connect();
		}


		public <T> T3<ModelBuilder<T>, ByteArrayOutputStream, ByteArrayOutputStream> 
		model(Class<T> clazz, String...tasks) {


			final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			final ByteArrayOutputStream errStream = new ByteArrayOutputStream();


			String initScriptPath;
			try {
				initScriptPath = this.initScript.getCanonicalPath();
			} catch (IOException ioException) {
				throw new GradleConnectionException("initscript exception", ioException);
			}


			final ModelBuilder<T> builder = projConnection.model(clazz)
			.forTasks(tasks)
			.setStandardError(System.err)
			.setStandardOutput(System.out)
			.withArguments("-I", initScriptPath);

			return new T3<>(builder, outStream, errStream);
		}

		public static Context from(String directory, String initScript) {
			return new Context(new File(directory), new File(initScript));
		}
	}

	public static final String GRADLE_EXCEPTION_FILE = "test-data/catchException.gradle";
}