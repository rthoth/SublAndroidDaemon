package sublandroid.plugin.util;

import java.io.*;

import org.gradle.api.*;

/**
 * File Note
 * 
 */
public class FNote {

	/**
	 * Code block "function"
	 */
	private interface Block<R> {
		R apply() throws Throwable;
	}

	// where store notes
	protected final File file;

	public FNote(Project project, String baseName) {
		file = new File(project.getBuildDir(), baseName);
	}

	/**
	 */
	public FNote write(final String note) {
		sync(new Block<Void>() {
			public Void apply() throws Throwable {

				try (RandomAccessFile randomFile = new RandomAccessFile(file, "rw")) {

					long fileLength = randomFile.length();
					System.out.println(fileLength + "kkkkkkkkkkkkkkkkkkkkk");
					int size;
					long seek;

					if (fileLength < 4) {
						size = 1;
						seek = 4;
					} else {
						size = randomFile.readInt() + 1;
						seek = fileLength;
					}

					randomFile.seek(0);
					randomFile.writeInt(size);

					randomFile.seek(seek); // end...

					randomFile.writeUTF(note);

					return null;
				}
			}
		});

		return this;
	}

	public String[] read() {
		return sync(new Block<String[]>() {

			@Override
			public String[] apply() throws Throwable {
				try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
					int size = input.readInt();
					String[] notes = new String[size];
					for (int i=0; i<size; i++)
						notes[i] = input.readUTF();

					return notes;
				}
			}

		});
	}

	private <T> T sync(Block<T> block) {

		T value;
		synchronized (FNote.class) {
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