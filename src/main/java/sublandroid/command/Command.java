package sublandroid.command;

import sublandroid.*;
import sublandroid.core.*;
import sublandroid.messages.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.gradle.api.*;
import org.gradle.tooling.*;
import org.gradle.tooling.model.*;

public abstract class Command {
	protected static class GradleInvocation {

		private final InitScript initScript = new InitScript();
		private final ProjectConnection connection;

		public GradleInvocation(
			Collection<File> jars,
			Collection<Class<? extends Plugin<Project>>> plugins,
			ProjectConnection connection
		) {

			initScript.jars(jars);
			initScript.plugins(plugins);
			this.connection = connection;
		}

		public <T extends Model> ModelInvocation<T> model(Class<T> modelClass, String... tasks) {
			return new ModelInvocation(
				initScript, modelClass, connection, tasks
			);
		}

	}

	protected static class Context {

		public static Context from(ProjectConnection connection) {
			return new Context(connection);
		}

		private final ProjectConnection connection;

		public Context(final ProjectConnection connection) {
			this.connection = connection;
		}

		public GradleInvocation plugin(Class<? extends Plugin<Project>>... pluginClasses) {

			final TreeSet<File> jars = new TreeSet<>();
			final List<Class<? extends Plugin<Project>>> plugins = new ArrayList<>(pluginClasses.length);

			for (Class<? extends Plugin<Project>> pluginClass : pluginClasses) {
				final File jar = InitScript.findJar(pluginClass);

				if (!jars.contains(jar))
					jars.add(InitScript.findJar(pluginClass));

				plugins.add(pluginClass);
			}

			return new GradleInvocation(jars, plugins, connection);
		}
	}

	protected static class ModelInvocation<T extends Model> {

		private final InitScript initScript;
		private final Class<T> modelClass;
		private final ProjectConnection connection;
		private final String[] tasks;

		private boolean getted = false;

		public ByteArrayOutputStream standardOut = null;
		public ByteArrayOutputStream standardErr = null;

		public ModelInvocation(
			InitScript initScript,
			Class<T> modelClass,
			ProjectConnection connection,
			String... tasks
		) {
			this.initScript = initScript;
			this.modelClass = modelClass;
			this.connection = connection;
			this.tasks = tasks;
		}

		public synchronized T get() {
			if (getted)
				throw new IllegalStateException();

			getted = true;

			final ModelBuilder<T> builder = connection.<T> model(modelClass);
			builder.setStandardOutput(standardOut = new ByteArrayOutputStream());
			builder.setStandardError(standardErr = new ByteArrayOutputStream());


			return builder.get();
		}
	}

	protected static final Pattern LINE_BREAK_PATTERN = Pattern.compile("[\\r\\n]+");

	public abstract Message execute(MCommand mCommand, ProjectConnection connection);

	protected void println(String message, Object... objects) {
		System.out.println(String.format(message, objects));
	}
}