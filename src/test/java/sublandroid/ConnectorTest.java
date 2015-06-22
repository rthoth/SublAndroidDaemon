package sublandroid;

import sublandroid.messages.*;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.Path.*;
import static sublandroid.Util.*;

import com.fasterxml.jackson.databind.*;

public class ConnectorTest {

	private static class Runner implements Runnable {

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
				System.out.println("Starting @ " + Thread.currentThread().getName());
				this.connector.listen(reader, writer);
			} catch (Throwable throwable) {
				System.out.println("Ops...");
				throwable.printStackTrace();
			}
		}
	}


	@Test(timeOut=1000)
	public void loadValidDirectory() throws Exception {
		final Connector connector = new Connector(join("./test-data/simple-01"));

		final PipedWriter outputWriter = new PipedWriter();
		final PipedReader outputReader = new PipedReader(outputWriter);

		final PipedWriter inputWriter = new PipedWriter();
		final BufferedReader inputReader = new BufferedReader(new PipedReader(inputWriter));


		System.out.println("Test @ " + Thread.currentThread().getName());
		final Runner runner = new Runner(connector, outputReader, inputWriter);

		send(MCommand.from("start"), outputWriter);

		Started response = read(inputReader, Started.class);

		assertThat(response.message).isEqualTo("Woohoo!");
	}

}