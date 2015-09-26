package sublandroid.utils;

import sublandroid.core.BuildStatus;
import sublandroid.core.BuildStatus.Error;
import sublandroid.command.Command.ModelInvocation;
import sublandroid.core.BuildStatus;
import sublandroid.messages.*;

import java.util.*;

/**
 * Reader for Gradle output
 *
 * It searches any xml warning or errors
 */
public class XmlOutputReader extends OutputReader<ModelInvocation<BuildStatus>> {

	private boolean hasError = false;
	private boolean syntaxError = false;

	private static final String RuntimeException_TypeName = "java.lang.RuntimeException";
	private static final String SAXParseException_TypeName = "org.xml.sax.SAXParseException";
	
	@Override public void errorLine(int number, String line) {

		if (hasError) {
			System.out.println(number + ": " + line);
		}
	}

	@Override public boolean hasError() {
		return hasError;
	}

	@Override public List<MHighlight> lastHighlights() {
		return null;
	}

	@Override public boolean onApply() {
		hasError = false;
		syntaxError = false;

		BuildStatus buildStatus = invocation.get();
		if (buildStatus.getStatus().code() != BuildStatus.Status.Ok.code()) {
			hasError = true;

			for (Error error = buildStatus.getError(); error != null; error = error.getCause()) {

				final String errorType = error.getType();
				if (SAXParseException_TypeName.equals(errorType)) {
					System.out.println(error.getMessage());
					break;
				}

				if (RuntimeException_TypeName.equals(errorType)) {
					System.out.println(error.getMessage());
					break;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override public String toString() {
		return "XmlOutputReader";
	}

}