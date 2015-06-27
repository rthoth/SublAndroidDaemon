package sublandroid;

import sublandroid.messages.*;
import sublandroid.command.*;

import static java.lang.String.format;
import java.io.*;
import java.net.*;

import org.gradle.tooling.*;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.writeJSONStringTo;



public class Connector implements AutoCloseable {

	private static class Server implements Runnable, AutoCloseable {

		private final Connector connector;
		private final int port;
		private ServerSocket serverSocket;
		private Socket clientSocket;

		private Writer clientWriter;
		private Reader clientReader;

		public Server(final Connector connector, final int port) {
			this.connector = connector;
			this.port = port;
		}

		@Override
		public void close() {

			IOUtils.close(serverSocket);
			IOUtils.close(clientSocket);
			IOUtils.close(clientWriter);
			IOUtils.close(clientReader);
		}

		@Override
		public void run() {
			try {
				serverSocket = new ServerSocket(port, 1, InetAddress.getLoopbackAddress());
				while (true) {
					connector.println("SublAndroid listen @ %d", serverSocket.getLocalPort());
					clientSocket = serverSocket.accept();
					connector.println("A new sublandroid developer!");

					clientReader = new InputStreamReader(clientSocket.getInputStream());
					clientWriter = new OutputStreamWriter(clientSocket.getOutputStream());
					connector.listen(clientReader, clientWriter);
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}
		}
	}

	protected static void println(String msg, Object... args) {
		System.out.println(format(msg, args));
	}

	public static void main(String args[]) {
		if (args.length == 0)
			throw new IllegalArgumentException("Need a gradle project folder");

		try {
			boolean debug = false;
			if (args.length > 2)
				debug = "debug".equals(args[2]);

			final Connector connector = new Connector(args[0], debug);

			int port = 0;
			if (args.length > 1)
				port = Integer.parseInt(args[1]);

			connector.listen(port);

		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	protected ProjectConnection projectConnection = null;

	private BufferedReader reader = null;
	private Server server = null;
	private Thread serverThread = null;
	private BufferedWriter writer = null;

	public Connector(String file) throws IOException {
		this(file, false);
	}

	public Connector(String file, boolean debug) throws IOException {
		if (file == null)
			throw new NullPointerException("File Path is null");

		defineDebug(debug);

		fromDirectory(new File(file));
	}

	public Connector(File file, boolean debug) throws IOException {
		if (file == null)
			throw new NullPointerException("File is null");

		defineDebug(debug);

		fromDirectory(file);
	}

	@Override
	public void close() {
		println("Closing...");
		
		if (server != null)
			server.close();

		projectConnection.close();
		IOUtils.close(reader);
		IOUtils.close(writer);

		println("Closed");
	}

	private void defineDebug(boolean debug) {
		if (debug) {
			try {
				File outFile = new File(System.getProperty("java.io.tmpdir"), "sublandroid.out.log");
				File errFile = new File(System.getProperty("java.io.tmpdir"), "sublandroid.err.log");

				System.setOut(new PrintStream(new FileOutputStream(outFile, true)));
				System.setErr(new PrintStream(new FileOutputStream(errFile, true)));
			} catch (FileNotFoundException ex) {

			}
		}
	}

	private void execute(final Command command, final MCommand mCommand) {
		Message message = null;
		boolean success = true;

		try {
			println("Trying %s", mCommand.command);
			message = command.execute(mCommand, projectConnection);
			println("Executed %s", mCommand.command);
		} catch (Throwable throwable) {
			success = false;
			message = new MFailure(throwable);
		}

		response(success, message);
	}

	private void fromDirectory(File directory) throws IOException {
		if (!directory.isDirectory())
			throw new IOException(format("%s must be a directory", directory.getCanonicalPath()));

		try {
			projectConnection = GradleConnector.newConnector().forProjectDirectory(directory).connect();
		} catch (RuntimeException exception) {
			println(format("Trying start gradle at %s", directory.getCanonicalPath()));
		}
	}

	public void response(boolean success, Message message) {
		try {

			if (success)
				writer.write('S');
			else
				writer.write('E');

			writeJSONStringTo(message, writer);
			writer.write('\n');
			writer.flush();
		} catch (Throwable throwable) {
			throw new Error(throwable);
		}
	}

	public synchronized void listen(final int port) {

		if (server != null)
			throw new IllegalStateException();

		server = new Server(this, port);
		serverThread = new Thread(server, "SublAndroidListener");
		serverThread.start();

	}

	public synchronized void listen(Reader reader, Writer writer) throws IOException {

		this.reader = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
		this.writer = (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);

		while(true) {
			try {
				run(this.reader.readLine());
			} catch (Throwable throwable) {
				throw throwable;
			}
		}
	}

	private void run(final String line) throws IOException {
		final MCommand mCommand = parseObject(line, MCommand.class);
		println("Searching %s", mCommand.command);

		Command command = null;
		switch(mCommand.command) {
			case Hello.COMMAND:
				command = new Hello();
				break;
			case ShowTasks.COMMAND:
				command = new ShowTasks();
				break;
		}

		if (command != null)
			execute(command, mCommand);
		else
			response(false, new MFailure(format("Command %s not found", mCommand.command), "CommandNotFoundException"));

	}

}