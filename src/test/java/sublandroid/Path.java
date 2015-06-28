package sublandroid;

import java.io.*;

public class Path {

	public static final String PROJECT_01 = "./test-data/simple-01";
	public static final String JAVA_SINTAX_ERROR = "./test-data/java-syntax-error";

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