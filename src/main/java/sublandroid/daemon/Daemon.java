package sublandroid.daemon;

import sublandroid.IOUtils;
import sublandroid.messages.*;
import sublandroid.command.*;
import static sublandroid.Log.*;

import static java.lang.String.format;
import java.io.*;
import java.net.*;

import org.gradle.tooling.*;

import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.writeJSONStringTo;



public class Daemon implements AutoCloseable {

	public static void main(String args[]) {
		if (args.length == 0)
			throw new IllegalArgumentException("Need a gradle project folder");

		try {
			boolean debug = false;
			if (args.length > 2)
				debug = "debug".equals(args[2]);

			final Daemon daemon = new Daemon(args[0], debug);

			int port = 0;
			if (args.length > 1)
				port = Integer.parseInt(args[1]);

			daemon.listen(port);

		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	protected ProjectConnection projectConnection = null;
	protected String projectPath = null;
	protected Commands commands = new Commands();

	private BufferedReader reader = null;
	private Listener listener = null;
	private Thread listenerThread = null;
	private BufferedWriter writer = null;

	public Daemon(String file) throws IOException {
		this(file, false);
	}

	public Daemon(String file, boolean debug) throws IOException {
		if (file == null)
			throw new NullPointerException("File Path is null");

		defineDebug(debug);

		fromDirectory(new File(file));
	}

	public Daemon(File file, boolean debug) throws IOException {
		if (file == null)
			throw new NullPointerException("File is null");

		defineDebug(debug);

		fromDirectory(file);
	}

	@Override
	public void close() {
		println("Closing...");
		
		if (listener != null)
			listener.close();

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

	private void execute(final Command command, final MCommand commandMessage) {
		Message message = null;
		boolean success = true;

		try {
			println("Trying %s", commandMessage.command);
			message = command.execute(commandMessage, projectConnection);
			println("Executed %s", commandMessage.command);

		} catch (Throwable throwable) {
			println("Failed %s", commandMessage.command);
			throwable.printStackTrace();
			
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

		this.projectPath = directory.getCanonicalPath();
	}

	public void listen(final int port) {

		if (listener != null)
			throw new IllegalStateException("Daemon already has a listener");

		try {
			listener = new Listener(this, port);
		} catch (Throwable throwable) {
			throw new IllegalStateException("Failed listen at " + port, throwable);
		}

		listenerThread = new Thread(listener, "SublAndroid-Listener");
		listenerThread.start();
	}

	private void response(boolean success, Message message) {
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

	private void run(final String line) throws IOException {
		MCommand commandMessage = null;

		try {
			commandMessage = parseObject(line, MCommand.class);

		} catch (Throwable throwable) {
			println("Invalid input: %s", line);
			throwable.printStackTrace();
			return;
		}

		commandMessage.projectPath = projectPath;

		println("Searching %s", commandMessage.command);

		Command command = commands.search(commandMessage);

		if (command != null)
			execute(command, commandMessage);
		else {
			println("Not found %s", commandMessage.command);
			response(false, new MFailure(format("Command %s not found", commandMessage.command), "CommandNotFoundException"));
		}

	}

	public synchronized void talk(Reader reader, Writer writer) throws IOException {

		this.reader = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
		this.writer = (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);

		while (true) {
			run(this.reader.readLine());
		}
	}
}