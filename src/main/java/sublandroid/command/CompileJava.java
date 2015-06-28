package sublandroid.command;

import sublandroid.messages.*;

import java.io.*;
import java.util.regex.*;
import org.gradle.tooling.*;

public class CompileJava extends Command {

	public static final String COMMAND = "compileJava";

	protected static final Pattern JAVA_ERROR_PATTERN = Pattern.compile("^([^:]*):(\\d+):\\s*([^:]+):\\s+(.*)$\\s+(.*)$", Pattern.MULTILINE);

	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection)	{
		final Tuple3<BuildLauncher, ByteArrayOutputStream, ByteArrayOutputStream> tuple = buildLauncher(connection);

		tuple.t1.forTasks("compileDebugJava");

		final MJavaCompile message = new MJavaCompile();

		try {
			tuple.t1.run();
		} catch (BuildException buildEx) {
			Matcher matcher = JAVA_ERROR_PATTERN.matcher(new String(tuple.t3.toByteArray()));

			while (matcher.find()) {
				final String fileName = matcher.group(1);
				final int lineNumber = Integer.parseInt(matcher.group(2));
				final String kind = matcher.group(3);
				final String what = matcher.group(4);
				final String how = matcher.group(5);

				message.addJavaFailure(fileName, lineNumber, kind, what, how);
			}
		}

		return message;
	}

}