package sublandroid;

import java.io.*;
import java.net.*;
import java.util.*;

import sublandroid.core.*;

import org.gradle.api.*;

public class InitScript {

	public static final File SUBLANDROID_CORE_JAR = findJar(BuildStatus.class);

	public static File findJar(Class<?> clazz) {

		final String classFileName = clazz.getCanonicalName().replace('.', '/') + ".class";
		
		ClassLoader cl = clazz.getClassLoader();
		if (cl == null)
			cl = ClassLoader.getSystemClassLoader();

		final URL url = cl.getResource(classFileName);
		if (url == null)
			throw new IllegalStateException("No jar file for " + clazz.getCanonicalName());

		final int index = url.getPath().indexOf('!');
		if (index < 0)
			throw new IllegalStateException("Invalid path " + url.getPath());

		try {
			return new File(new URL(url.getPath().substring(0, index)).getFile());
		} catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private List<File> jars = null;
	private List<Class<? extends Plugin<?>>> plugins = null;
	private File file = null;

	public synchronized String fileName() {
		try {
			if (file == null) {
				file = File.createTempFile("init.", ".gradle");

				StringBuilder builder = new StringBuilder();

				if (jars != null) {
					builder.append("initscript {\n\tdependencies {\n\t\tclasspath files(");
					Iterator<File> iterator = jars.iterator();
					while (iterator.hasNext()) {
						builder.append("\n\t\t\t'")
						 .append(iterator.next().getCanonicalPath())
						 .append("'");

						if (iterator.hasNext())
							builder.append(",");
					}
					builder.append("\n\t\t)\n\t}\n}");
				}

				if (plugins != null) {
					builder.append("\n\nallprojects {");
					for (Class<?> plugin : plugins) {
						builder.append("\n\t")
						 .append("apply plugin: ")
						 .append(plugin.getCanonicalName());
					}
					builder.append("\n}");
				}

				PrintStream printStream = new PrintStream(new FileOutputStream(file));
				printStream.print(builder.toString());
				printStream.flush();
				printStream.close();
			}

			return file.getCanonicalPath();

		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

	}

	public boolean isNecessary() {
		return (jars != null && !jars.isEmpty()) || (plugins != null && !plugins.isEmpty());
	}

	public void jars(Collection<File> jars) {
		this.jars = new ArrayList<>();
		this.jars.add(SUBLANDROID_CORE_JAR);
		this.jars.addAll(jars);
	}

	public void plugins(Collection<Class<? extends Plugin<?>>> plugins) {
		this.plugins = new ArrayList(plugins);
	}

}