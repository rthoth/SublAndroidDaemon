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

	protected static final Pattern LINE_BREAK_PATTERN = Pattern.compile("[\\r\\n]+");

	public abstract Message execute(MCommand mCommand, ProjectConnection connection);
	
	/**
	 * Gradle pre-invocation
	 */
	public static class Gradle {

		public static Gradle from(ProjectConnection connection) {
			return new Gradle(connection);
		}

		private final ProjectConnection connection;
		private final Collection<File> jars;
		private final Collection<Class<? extends Plugin<?>>> plugins;

		private Gradle(final ProjectConnection connection) {
			this(connection, null, null);
		}

		private Gradle(
			final ProjectConnection connection,
			Collection<File> jars,
			Collection<Class<? extends Plugin<?>>> plugins) {

			this.connection = connection;
			this.jars = jars;
			this.plugins = plugins;
		}

		public <T extends Model> ModelInvocation<T> model(Class<T> modelClass, String... tasks) {

			final InitScript initScript = new InitScript();
			initScript.jars(jars);
			initScript.plugins(plugins);

			return new ModelInvocation(
				initScript, modelClass, connection, tasks
			);
		}

		public Gradle plugins(Class<? extends Plugin<?>>... pluginClasses) {

			final TreeSet<File> jars = new TreeSet<>();
			final List<Class<? extends Plugin<?>>> plugins = new ArrayList<>(pluginClasses.length);

			for (Class<? extends Plugin<?>> pluginClass : pluginClasses) {
				final File jar = InitScript.findJar(pluginClass);

				if (!jars.contains(jar))
					jars.add(InitScript.findJar(pluginClass));

				plugins.add(pluginClass);
			}

			return new Gradle(connection, jars, plugins);
		}
	}

	/**
	 * Gradle invocation
	 */
	public static class Invocation {

		protected final ProjectConnection connection;
		protected final InitScript initScript;
		protected final String[] tasks;
		
		private ByteArrayOutputStream standardErr = null;
		private ByteArrayOutputStream standardOut = null;

		protected boolean invoked = false;

		protected Invocation(InitScript initScript, ProjectConnection connection, String... tasks) {
			this.initScript = initScript;
			this.connection = connection;
			this.tasks = tasks;
		}

		public ByteArrayOutputStream getStandardErr() {
			return standardErr;
		}

		public ByteArrayOutputStream getStandardOut() {
			return standardOut;
		}

		protected <R extends LongRunningOperation> R setup(R operation) {
			if (!invoked) {
				operation.setStandardError(standardErr = new ByteArrayOutputStream());
				operation.setStandardOutput(standardOut = new ByteArrayOutputStream());

				if (initScript.isNecessary())
					operation.withArguments("--init-script", initScript.fileName(), "--info");
			}

			return operation;
		}
	}

	/**
	 * Gradle invocation with model
	 */
	public static class ModelInvocation<T extends Model> extends Invocation {
		
		private final Class<T> modelClass;
		private T model;

		public ModelInvocation(InitScript initScript, Class<T> modelClass,
			ProjectConnection connection, String... tasks) {

			super(initScript, connection, tasks);
			this.modelClass = modelClass;
		}

		public synchronized T get() {
			if (invoked)
				return model;
			else {
				final ModelBuilder<T> builder = setup(connection.<T> model(modelClass));
				invoked = true;
				builder.forTasks(tasks);

				return model = builder.get();
			}
		}
	}
}