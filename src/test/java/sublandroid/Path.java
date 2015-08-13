package sublandroid;

import java.io.*;

public class Path {

	public static final String RESOURCE_ERROR_01 = "./test-data/projects/resource-error-01";
	public static final String PROJECT_01 = "./test-data/projects/simple-01";
	public static final String JAVA_SINTAX_ERROR = "./test-data/projects/java-syntax-error";
	public static final String JAVA_ERRORS_01 = "./test-data/projects/java-errors-01";


	private static String fix(String path) {
		return path.replace('/', File.separatorChar);
	}

	public static String absolutePath(String base, String... parts) {
		final String finalInput = join(base, parts);

		try {
			return new File(finalInput).getCanonicalPath();
		} catch (IOException ioExc) {
			throw new RuntimeException("absolutePath for ", ioExc);
		}
	}


	public static String join(String base, String... parts) {
		StringBuilder path = new StringBuilder().append(fix(base));

		for (int i=0,l=parts.length; i<l; i++) {
			path.append(File.separatorChar).append(fix(parts[i]));
		}

		return path.toString();
	}

}