package sublandroid.help;

import sublandroid.IOUtils;
import sublandroid.daemon.*;
import sublandroid.messages.*;

import java.io.*;
import java.net.*;

import com.alibaba.fastjson.TypeReference;

import static com.alibaba.fastjson.JSON.writeJSONStringTo;
import static com.alibaba.fastjson.JSON.parseObject;
import static sublandroid.Path.*;

public abstract class Helper {

	public static TypeReference<MList<MTask>> LIST_TASKS = 
	 new TypeReference<MList<MTask>> () {};

	public static class CommandFailed extends RuntimeException {
		public CommandFailed(Object object) {
			super(object.toString());
		}
	}

	public static class Client implements AutoCloseable {

		public final Daemon daemon;
		public final int port;
		public final Socket socket;
		public final BufferedWriter writer;
		public final BufferedReader reader;

		public Client(String path) throws Throwable {
			this(path, (int) (2048 + Math.random() * 500));
		}

		public Client(String path, int port) throws Throwable {
			daemon = new Daemon(path);
			this.port = port;

			daemon.listen(port);

			BufferedReader reader = null;
			BufferedWriter writer = null;
			Socket socket = null;

			for (int i=0; i<20; i++) {

				Thread.sleep(50);

				try {
					socket = new Socket(InetAddress.getLoopbackAddress(), port);
					writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					break;
				} catch (IOException IOException) { }
			}

			this.socket = socket;
			this.reader = reader;
			this.writer = writer;
		}

		@Override
		public void close() {
			daemon.close();
			IOUtils.close(writer);
			IOUtils.close(reader);
			IOUtils.close(socket);
		}

		public <T> T read(Class<T> clazz) throws IOException {
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

		public <T> T read(TypeReference<T> type) throws IOException {
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

		public void send(MCommand message) throws IOException {
			writeJSONStringTo(message, writer);
			writer.write('\n');
			writer.flush();
		}
	}

}