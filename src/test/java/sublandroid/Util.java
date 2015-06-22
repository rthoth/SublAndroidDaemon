package sublandroid;

import sublandroid.messages.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;

public abstract class Util {

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


	public static ObjectMapper OBJECT_MAPPER = new ObjectMapper(Jackson.FACTORY);

	public static void send(MCommand message, Writer writer) throws IOException {
		OBJECT_MAPPER.writeValue(writer, message);
		writer.write('\n');
		writer.flush();
	}

	public static <T> T read(BufferedReader reader, Class<T> clazz) throws IOException {
		return OBJECT_MAPPER.readValue(reader.readLine(), clazz);
	}

}