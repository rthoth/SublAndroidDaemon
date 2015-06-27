package sublandroid;

import sublandroid.messages.*;
import static sublandroid.Path.*;

import java.io.*;
import java.net.*;

import com.alibaba.fastjson.TypeReference;

import static com.alibaba.fastjson.JSON.writeJSONStringTo;
import static com.alibaba.fastjson.JSON.parseObject;

public abstract class Util {

	public static TypeReference<MList<MTask>> LIST_TASKS = new TypeReference<MList<MTask>> () {

	};

	public static class CommandFailed extends RuntimeException {

		public Object object;

		public CommandFailed(Object object) {
			super(object.toString());
			this.object = object;
		}

		public <T> T get() {
			return (T) object;
		}
	}

	public static class Context implements AutoCloseable {

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

			connector = new Connector(join(path), true);
		}

		private Runner runner = null;

		public Runner run() {
			synchronized (this) {
				if (runner == null)
					runner = new Runner(connector, outputReader, inputWriter);
			}

			return runner;
		}

		@Override
		public void close() {
			IOUtils.close(connector);
			IOUtils.close(reader);
			IOUtils.close(writer);
		}
	}

	public static class ClientContext implements AutoCloseable {

		public final Connector connector;
		public final int port;
		public final Socket socket;
		public final BufferedWriter writer;
		public final BufferedReader reader;

		public ClientContext(String path, int port) throws Throwable {
			connector = new Connector(path, true);
			this.port = port;

			connector.listen(port);

			Thread.sleep(500);

			socket = new Socket(InetAddress.getLoopbackAddress(), port);

			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		@Override
		public void close() {
			connector.close();
			IOUtils.close(writer);
			IOUtils.close(reader);
			IOUtils.close(socket);
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
		final char status = (char) reader.read();
		final T object = parseObject(reader.readLine(), clazz);

		switch(status) {
			case 'S':
				return object;

			case 'E':
				throw new CommandFailed(object);

			default:
				throw new IllegalStateException(String.valueOf(status));
		}
	}

	public static <T> T read(BufferedReader reader, TypeReference<T> type) throws IOException {
		final char status = (char) reader.read();
		final T object = parseObject(reader.readLine(), type);

		switch(status) {
			case 'S':
				return object;

			case 'E':
				throw new CommandFailed(object);

			default:
				throw new IllegalStateException(String.valueOf(status));
		}
	}

}