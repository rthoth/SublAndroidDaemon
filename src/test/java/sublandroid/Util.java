package sublandroid;

import sublandroid.daemon.*;
import sublandroid.messages.*;

import java.io.*;
import java.net.*;

import com.alibaba.fastjson.TypeReference;

import static com.alibaba.fastjson.JSON.writeJSONStringTo;
import static com.alibaba.fastjson.JSON.parseObject;
import static sublandroid.Path.*;

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

		public final Daemon daemon;

		public Context(String path) throws IOException {
			outputWriter = new PipedWriter();
			outputReader = new PipedReader(outputWriter);

			inputWriter = new PipedWriter();
			inputReader = new PipedReader(inputWriter);

			writer = new BufferedWriter(outputWriter);
			reader = new BufferedReader(inputReader);

			daemon = new Daemon(join(path));
		}

		private Runner runner = null;

		public Runner run() {
			synchronized (this) {
				if (runner == null)
					runner = new Runner(daemon, outputReader, inputWriter);
			}

			return runner;
		}

		@Override
		public void close() {
			IOUtils.close(daemon);
			IOUtils.close(reader);
			IOUtils.close(writer);
		}
	}

	public static class ClientContext implements AutoCloseable {

		public final Daemon daemon;
		public final int port;
		public final Socket socket;
		public final BufferedWriter writer;
		public final BufferedReader reader;

		public ClientContext(String path, int port) throws Throwable {
			daemon = new Daemon(path);
			this.port = port;

			daemon.listen(port);

			Thread.sleep(500);

			socket = new Socket(InetAddress.getLoopbackAddress(), port);

			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}

		@Override
		public void close() {
			daemon.close();
			IOUtils.close(writer);
			IOUtils.close(reader);
			IOUtils.close(socket);
		}

	}

	public static class Runner implements Runnable {

		private final Daemon daemon;
		private final Reader reader;
		private final Writer writer;
		private final Thread thread;


		public Runner(final Daemon daemon, final Reader reader, final Writer writer) {
			this.reader = reader;
			this.writer = writer;
			this.daemon = daemon;
			this.thread = new Thread(this);
			this.thread.start();
		}

		public void run() {
			try {
				this.daemon.talk(reader, writer);
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
		final String line = reader.readLine();
		System.err.println(line);
		final char status = line.charAt(0);

		switch(status) {
			case 'S':
				return parseObject(line.substring(1), clazz);

			case 'E':
				throw new CommandFailed(parseObject(line.substring(1), MFailure.class));

			default:
				throw new IllegalStateException(String.valueOf(status));
		}
	}

	public static <T> T read(BufferedReader reader, TypeReference<T> type) throws IOException {
		final String line = reader.readLine();
		System.err.println(line);
		
		final char status = line.charAt(0);
		final T object = parseObject(line.substring(1), type);

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