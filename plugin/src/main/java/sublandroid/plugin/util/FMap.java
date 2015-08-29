package sublandroid.plugin.util;

import java.io.*;

import org.gradle.api.*;

public class FMap {

	private interface Block<R> {
		R apply() throws Throwable;
	}

	protected final File baseDir;
	protected final File buildDir;

	public FMap(Project project, String baseName) {
		buildDir = project.getBuildDir();
		baseDir = new File(buildDir, baseName);
		baseDir.mkdirs();
	}

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
		};

		return value;
	}

}