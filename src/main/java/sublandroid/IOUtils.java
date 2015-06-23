package sublandroid;

import java.io.*;

public abstract class IOUtils {

	public static void close(AutoCloseable closeable) {
		try {
			if (closeable != null)
				closeable.close();
		} catch (Throwable throwable) {

		}
	}

}