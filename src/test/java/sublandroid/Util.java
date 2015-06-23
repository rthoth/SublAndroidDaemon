package sublandroid;

import sublandroid.messages.*;
import static sublandroid.Path.*;

import java.io.*;

import com.alibaba.fastjson.TypeReference;

import static com.alibaba.fastjson.JSON.writeJSONStringTo;
import static com.alibaba.fastjson.JSON.parseObject;

public abstract class Util {

	public static TypeReference<MList<MTask>> LIST_TASKS = new TypeReference<MList<MTask>> () {

	};


	public static class Context {

		private final PipedWriter outputWriter;
		private final PipedReader outputReader;

		private final PipedWriter inputWriter;
		private final PipedReader inputReader;

		public final BufferedWriter writer;
		public final BufferedReader reader;

		public final Connector connector;

		public Context(String path) throws IOException {
			outputWriter = new PipedWriter();
			outputReader = new PipedReader(outputWriter);

			inputWriter = new PipedWriter();
			inputReader = new PipedReader(inputWriter);

			writer = new BufferedWriter(outputWriter);
			reader = new BufferedReader(inputReader);

			connector = new Connector(join(path));
		}

		private Runner runner = null;

		public Runner run() {
			synchronized (this) {
				if (runner == null)
					runner = new Runner(connector, outputReader, inputWriter);
			}

			return runner;
		}
	}

	public static class Runner implements Runnable {

		private final Connector connector;
		private final Reader reader;
		private final Writer writer;
		private final Thread thread;


		public Runner(final Connector connector, final Reader reader, final Writer writer) {
			this.reader = reader;
			this.writer = writer;
			this.connector = connector;
			this.thread = new Thread(this);
			this.thread.start();
		}

		public void run() {
			try {
				this.connector.listen(reader, writer);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}

	public static void send(MCommand message, Writer writer) throws IOException {
		writeJSONStringTo(message, writer);
		writer.write('\n');
		writer.flush();
	}

	public static <T> T read(BufferedReader reader, Class<T> clazz) throws IOException {
		return parseObject(reader.readLine(), clazz);
	}

	public static <T> T read(BufferedReader reader, TypeReference<T> type) throws IOException {
		final String line = reader.readLine();
		System.err.println(line);
		return parseObject(line, type);
	}

}