package sublandroid.utils;

import sublandroid.command.Command;
import sublandroid.core.BuildStatus;
import sublandroid.messages.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public abstract class Java extends Source {

	public static class JavaHighlight extends MHighlight {
		public JavaHighlight() {
			language = "Java";
		}
	}

	protected static final String CANNOT_FIND_SYMBOL = "cannot find symbol";

	protected static final Pattern JAVA_FILE_ERROR_PATTERN = Pattern.compile("^([^:]+):(\\d+):\\s+([^:]+):\\s+(.+)$");
	protected static final Pattern JAVA_DETAIL_PATTERN = Pattern.compile("^\\W*([^:]+):\\s+(.+)$");
	protected static final Pattern JAVA_SEMANTIC_ERROR = Pattern.compile("^[^\\s]+\\s[^\\s]+\\sin\\sclass");

	public static List<MHighlight> searchJavaHighlights(Command.ModelInvocation<BuildStatus> modelInvocation) {
		return searchJavaHighlights(modelInvocation.getErr());
	}

	private static List<MHighlight> searchJavaHighlights(InputStream inputStream) {
		Reader reader = new InputStreamReader(inputStream);
		final StringBuilder builder = new StringBuilder();
		char[] buffer = new char[1024];

		try {
			int length = reader.read(buffer);
			
			while (length > 0) {
				builder.append(buffer, 0, length);
				length = reader.read(buffer);
			}
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}


		return searchJavaHighlights(builder.toString());
	}

	private static List<MHighlight> searchJavaHighlights(String output) {
		return searchJavaHighlights(NL_PATTERN.split(output));
	}

	private static List<MHighlight> searchJavaHighlights(String... lines) {
		LinkedList<MHighlight> highlights = new LinkedList<>();

		for (int i=0; i<lines.length; i++) {
			String line = lines[i];
			final Matcher matcher = JAVA_FILE_ERROR_PATTERN.matcher(line);

			if (matcher.matches()) {

				final MHighlight highlight = new JavaHighlight();
				highlights.add(highlight);

				highlight.fileName = matcher.group(1);
				highlight.lineNumber = Integer.parseInt(matcher.group(2));
				highlight.kind = matcher.group(3);
				highlight.what = matcher.group(4);
				highlight.where = lines[++i];
				
				i++;

				if (CANNOT_FIND_SYMBOL.equals(highlight.what))
					i += javaCannotFindSymbol(highlight, i, lines);

				else if (JAVA_SEMANTIC_ERROR.matcher(highlight.what).find())
					i += javaSemanticError(highlight, i, lines);
			}
		}

		return highlights;
	}

	private static int javaCannotFindSymbol(final MHighlight highlight, int line, String[] lines) {
		final Matcher symbolMatcher = JAVA_DETAIL_PATTERN.matcher(lines[++line]);
		final Matcher locationMatcher = JAVA_DETAIL_PATTERN.matcher(lines[++line]);

		final String symbol = symbolMatcher.matches() ? symbolMatcher.group(2) : "";
		final String location = locationMatcher.matches() ? locationMatcher.group(2) : "";
		
		highlight.description = String.format("%s %s in %s", highlight.what, symbol, location);

		return 2;
	}

	protected static int javaSemanticError(final MHighlight highlight, int line, String[] lines) {
		LinkedList<String> details = new LinkedList<>();
		int i = line;

		for (Matcher detailMatcher; (i + 1) < lines.length;) {
			
			detailMatcher = JAVA_DETAIL_PATTERN.matcher(lines[++i]);
			
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

}