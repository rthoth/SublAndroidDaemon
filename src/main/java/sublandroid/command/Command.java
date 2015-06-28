package sublandroid.command;

import java.io.*;

import sublandroid.messages.*;
import org.gradle.tooling.*;

public abstract class Command {

	protected static class Tuple3<T1, T2, T3> {
		public final T1 t1;
		public final T2 t2;
		public final T3 t3;

		public Tuple3(T1 t1, T2 t2, T3 t3) {
			this.t1 = t1;
			this.t2 = t2;
			this.t3 = t3;
		}
	}
	
	public abstract Message execute(MCommand mCommand, ProjectConnection connection);

	protected void println(String message, Object... objects) {
		System.out.println(String.format(message, objects));
	}

	protected Tuple3<BuildLauncher, ByteArrayOutputStream, ByteArrayOutputStream> buildLauncher(ProjectConnection connection) {
		final BuildLauncher build = connection.newBuild();

		final ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
		final ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		build.setStandardError(errBytes);
		build.setStandardOutput(outBytes);

		return new Tuple3(build, outBytes, errBytes);

	}
}