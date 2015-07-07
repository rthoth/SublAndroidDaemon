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

		context.tasks(GRADLE_TASK);

		final MJavaCompile message = new MJavaCompile();

		try {
			context.run();
		} catch (BuildException buildEx) {

			processJavaErrors(message, context);

			if (message.failures == null)
				throw buildEx;
		}

		return message;
	}

	protected int cannotFindSymbol(final MHighlight highlight, int line, String[] lines) {
			final Matcher symbolMatcher = DETAIL_PATTERN.matcher(lines[++line]);
			final Matcher locationMatcher = DETAIL_PATTERN.matcher(lines[++line]);

			final String symbol = symbolMatcher.matches() ? symbolMatcher.group(2) : "";
			final String location = locationMatcher.matches() ? locationMatcher.group(2) : "";
			
			highlight.description = String.format("%s %s in %s", highlight.what, symbol, location);

			return 2;
	}

	protected int semanticError(final MHighlight highlight, int line, String[] lines) {
		LinkedList<String> details = new LinkedList<>();
		int i = line;

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

		highlight.description = description.toString();

		return i - line;
	}

	protected void processJavaErrors(final MJavaCompile message, final Context ctx) {
		final String errOut = new String(ctx.error.toByteArray());

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

				MHighlight highlight = new MHighlight(fileName, lineNumber, kind, what, where);



				if (CANNOT_FIND_SYMBOL.equals(what))
					i += cannotFindSymbol(highlight, i, lines);
				
				else if (SEMANTIC_ERROR.matcher(what).find())
					i += semanticError(highlight, i, lines);
					

				message.addJavaFailure(highlight);
			}
		}
	}
}