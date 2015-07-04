package sublandroid.command;

import sublandroid.messages.*;

import java.io.*;
import java.util.regex.*;
import org.gradle.tooling.*;

public class CompileJava extends Command {

	public static final String COMMAND = "compileJava";


	protected static final String CANNOT_FIND_SYMBOL = "cannot find symbol";
	protected static final Pattern METHOD_IN_CLASS_PATTERN = Pattern.compile("method\\s+[^\\s]+\\sin\\sclass");
	protected static final Pattern CONSTRUCTOR_IN_CLASS_PATTERN = Pattern.compile("constructor\\s+[^\\s]+\\sin\\sclass");

	protected static final String NL_PATTERN = "[\r\n]+";

	protected static final Pattern JAVA_ERROR_PATTERN = Pattern.compile("^([^:]+):(\\d+):\\s*([^:]+):\\s*(.*)$");
	
	protected static final Pattern DETAIL_PATTERN = Pattern.compile("^\\W*([^:]+):\\s*(.+)$");


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
					final String where = lines[++i];
					i++;

					if (CANNOT_FIND_SYMBOL.equals(what)) {
						final Matcher symbolMatcher = DETAIL_PATTERN.matcher(lines[++i]);
						final Matcher locationMatcher = DETAIL_PATTERN.matcher(lines[++i]);

						final String symbol = symbolMatcher.matches() ? symbolMatcher.group(2) : "";
						final String location = locationMatcher.matches() ? locationMatcher.group(2) : "";
						
						final String description = String.format("%s %s in %s", what, symbol, location);

						message.addJavaFailure(fileName, lineNumber, kind, what, where, description);

					} else if (METHOD_IN_CLASS_PATTERN.matcher(what).find() || CONSTRUCTOR_IN_CLASS_PATTERN.matcher(what).find()) {
						final Matcher det1Matcher = DETAIL_PATTERN.matcher(lines[++i]);
						final Matcher det2Matcher = DETAIL_PATTERN.matcher(lines[++i]);
						final Matcher det3Matcher = DETAIL_PATTERN.matcher(lines[++i]);

						String lbl1, val1, lbl2, val2, lbl3, val3;

						lbl1 = lbl2 = lbl3 = val1 = val2 = val3 = "";

						if (det1Matcher.find()) {
							lbl1 = det1Matcher.group(1);
							val1 = det1Matcher.group(2);
						}

						if (det2Matcher.find()) {
							lbl2 = det2Matcher.group(1);
							val2 = det2Matcher.group(2);
						}

						if (det3Matcher.find()) {
							lbl3 = det3Matcher.group(1);
							val3 = det3Matcher.group(2);
						}

						final String description = String.format("(%s: %s), (%s: %s), (%s: %s)", lbl1, val1, lbl2, val2, lbl3, val3);

						message.addJavaFailure(fileName, lineNumber, kind, what, where, description);
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