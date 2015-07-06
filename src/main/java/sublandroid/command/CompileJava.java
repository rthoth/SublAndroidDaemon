package sublandroid.command;

import sublandroid.messages.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.gradle.tooling.*;

public class CompileJava extends Command {

	public static final String COMMAND = "compileJava";

	protected static final String CANNOT_FIND_SYMBOL = "cannot find symbol";

	protected static final Pattern DETAIL_PATTERN = Pattern.compile("^\\W*([^:]+):\\s+(.+)$");

	protected static final Pattern ERROR_PATTERN = Pattern.compile("^([^:]+):(\\d+):\\s+([^:]+):\\s+(.+)$");

	protected static final String GRADLE_TASK = "compileDebugJava";

	protected static final String LINE_BREAK = "[\\r\\n]+";
	
	protected static final Pattern SEMANTIC_ERROR = Pattern.compile("^[^\\s]+\\s[^\\s]+\\sin\\sclass");


	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection)	{
		final Context context = Context.from(connection);

		context.buildLauncher.forTasks(GRADLE_TASK);

		final MJavaCompile message = new MJavaCompile();

		try {
			context.buildLauncher.run();
		} catch (BuildException buildEx) {

			final String errOut = new String(context.error.toByteArray());

			final String[] lines = errOut.split(LINE_BREAK);

			for (int i=0; i<lines.length; i++) {
				final Matcher matcher = ERROR_PATTERN.matcher(lines[i]);

				if (matcher.matches()) {
					final String fileName = matcher.group(1);
					final int lineNumber = Integer.parseInt(matcher.group(2));
					final String kind = matcher.group(3);
					final String what = matcher.group(4);
					final String where = lines[++i];
					i++;

					if (CANNOT_FIND_SYMBOL.equals(what)) {
						final Matcher symbolMatcher = DETAIL_PATTERN.matcher(lines[++i]);
						final Matcher locationMatcher = DETAIL_PATTERN.matcher(lines[++i]);

						final String symbol = symbolMatcher.matches() ? symbolMatcher.group(2) : "";
						final String location = locationMatcher.matches() ? locationMatcher.group(2) : "";
						
						final String description = String.format("%s %s in %s", what, symbol, location);

						message.addJavaFailure(fileName, lineNumber, kind, what, where, description);

					} else if (SEMANTIC_ERROR.matcher(what).find()) {

						LinkedList<String> details = new LinkedList<>();

						for (Matcher detailMatcher; (i + 1) < lines.length;) {
							
							detailMatcher = DETAIL_PATTERN.matcher(lines[++i]);
							
							if (detailMatcher.matches()) {
								details.add(String.format("(%s: %s)", detailMatcher.group(1), detailMatcher.group(2)));
							} else {
								--i;
								break;
							}
						}

						final StringBuilder description = new StringBuilder();

						while (!details.isEmpty()) {
							description.append(details.poll());
							if (!details.isEmpty())
								description.append(", ");
						}

						message.addJavaFailure(fileName, lineNumber, kind, what, where, description.toString());
					} else {
						message.addJavaFailure(fileName, lineNumber, kind, what, where);
					}

				}
			}


			if (message.failures == null)
				throw buildEx;
		}

		return message;
	}

}