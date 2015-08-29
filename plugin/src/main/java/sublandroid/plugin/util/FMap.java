package sublandroid.plugin.util;

import java.io.*;

import org.gradle.api.*;

/**
 * File "Map"
 * 
 */
public class FMap {

	/**
	 * Code block "function"
	 */
	private interface Block<R> {
		R apply() throws Throwable;
	}

	// where store key-values
	protected final File baseDir;

	public FMap(Project project, String baseName) {
		baseDir = new File(project.getBuildDir(), baseName);
		baseDir.mkdirs();
	}

	/**
	 * Put a key-value entry
	 * @param  key   [description]
	 * @param  value [description]
	 * @return       [description]
	 */
	public FMap put(final String key, final String value) {
		sync(new Block<Void>() {
			public Void apply() throws Throwable {

				RandomAccessFile randomFile = new RandomAccessFile(new File(baseDir, key), "rw");

				int newLength;
				long fileLength = randomFile.length();
				long seekPos = fileLength;

				if (fileLength == 0) {
					newLength = 1;
					seekPos = 4;
				} else
					newLength = randomFile.readInt() + 1;

				randomFile.writeInt(newLength);
				randomFile.seek(seekPos);

				randomFile.writeUTF(value);

				randomFile.close();

				return null;
			}
		});

		return this;
	}

	private <T> T sync(Block<T> block) {

		T value;
		synchronized (FMap.class) {
			try {
				value = block.apply();

			} catch (RuntimeException runtimeException) {
				throw runtimeException;

			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}

		return value;
	}

}