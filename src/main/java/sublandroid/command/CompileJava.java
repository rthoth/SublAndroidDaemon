package sublandroid.command;

import sublandroid.messages.*;

import java.io.*;
import java.util.regex.*;
import org.gradle.tooling.*;

public class CompileJava extends Command {

	public static final String COMMAND = "compileJava";

	protected static final String NL_PATTERN = "[\r\n]+";
	protected static final Pattern JAVA_ERROR_PATTERN = Pattern.compile("^([^:]*):(\\d+):\\s*([^:]+):\\s*(.*)$");


	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection)	{
		final Context context = Context.from(connection);

		context.buildLauncher.forTasks("compileDebugJava");

		final MJavaCompile message = new MJavaCompile();

		try {
			context.buildLauncher.run();
		} catch (BuildException buildEx) {

			final String errOut = new String(context.error.toByteArray());

			final String[] lines = errOut.split(NL_PATTERN);

			for (int i=0; i<lines.length; i++) {
				final Matcher matcher = JAVA_ERROR_PATTERN.matcher(lines[i]);

				if (matcher.matches()) {
					final String fileName = matcher.group(1);
					final int lineNumber = Integer.parseInt(matcher.group(2));
					final String kind = matcher.group(3);
					final String what = matcher.group(4);
					final String how = lines[++i];
					i++;
					message.addJavaFailure(fileName, lineNumber, kind, what, how);
				}
			}


			if (message.failures == null)
				throw buildEx;
		}

		return message;
	}

}