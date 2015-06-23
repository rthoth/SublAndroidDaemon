package sublandroid;

import sublandroid.messages.*;
import sublandroid.command.*;

import static java.lang.String.format;
import java.io.*;

import org.gradle.tooling.*;
import static com.alibaba.fastjson.JSON.parseObject;
import static com.alibaba.fastjson.JSON.writeJSONStringTo;
import static com.alibaba.fastjson.serializer.SerializerFeature.*;



public class Connector {

	protected static void println(String msg, Object... args) {
		System.out.println(format(msg, args));
	}

	private boolean listen = false;
	private BufferedReader reader = null;
	private BufferedWriter writer = null;

	protected ProjectConnection projectConnection = null;

	public static void main(String args[]) {
		try {
			new Connector(args[0]);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public Connector(String file) throws IOException {
		if (file == null)
			throw new NullPointerException("File Path is null");

		fromDirectory(new File(file));
	}

	public Connector(File file) throws IOException {
		if (file == null)
			throw new NullPointerException("File is null");

		fromDirectory(file);
	}

	private void fromDirectory(File directory) throws IOException {
		if (!directory.isDirectory())
			throw new IOException(format("%s must be a directory", directory.getCanonicalPath()));

		try {
			projectConnection = GradleConnector.newConnector().forProjectDirectory(directory).connect();
		} catch (RuntimeException exception) {
			System.out.println(format("Trying start gradle at %s", directory.getCanonicalPath()));
		}
	}

	public synchronized void listen(Reader reader, Writer writer) throws IOException {
		if (listen)
			throw new IllegalStateException();

		listen = true;
		this.reader = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
		this.writer = (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);

		while(true) {
			try {
				run(this.reader.readLine());
			} catch (Throwable throwable) {

			}
		}
	}

	private void execute(final Command command, final MCommand mCommand) {
		Message message = null;
		try {
			message = command.execute(mCommand, projectConnection);
		} catch (Throwable throwable) {
			message = new MFailure(throwable);
		}

		try {
			writeJSONStringTo(message, writer);
			writer.write('\n');
			writer.flush();
		} catch (Throwable throwable) {
			throw new Error(throwable);
		}
	}

	private void run(final String line) throws IOException {
		final MCommand mCommand = parseObject(line, MCommand.class);

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
	}

}