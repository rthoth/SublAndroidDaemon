package sublandroid;

import java.io.*;
import java.net.*;
import java.util.*;

import sublandroid.core.*;

import org.gradle.api.*;

public class InitScript {

	public static final File SUBLANDROID_CORE_JAR = findJar(BuildStatus.class);

	public static File findJar(Class<?> clazz) {

		final String classFileName = clazz.getCanonicalName() + ".class";
		
		ClassLoader cl = clazz.getClassLoader();
		if (cl == null)
			cl = ClassLoader.getSystemClassLoader();

		final URL url = cl.getResource(classFileName);
		if (url == null)
			throw new IllegalStateException("No jar file for " + clazz.getCanonicalName());

		final int index = url.getPath().indexOf('!');
		if (index < 0)
			throw new IllegalStateException("Invalid path " + url.getPath());

		return new File(url.getPath().substring(0, index));
	}

	private List<File> jars = null;
	private List<Class<? extends Plugin<Project>>> plugins = null;

	public void jars(Collection<File> jars) {
		this.jars = new ArrayList<>(jars);
	}

	public void plugins(Collection<Class<? extends Plugin<Project>>> plugins) {
		this.plugins = new ArrayList(plugins);
	}

}