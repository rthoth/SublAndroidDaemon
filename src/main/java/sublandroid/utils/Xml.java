package sublandroid.utils;

import java.util.*;
import java.io.*;

import sublandroid.command.Command;
import sublandroid.core.*;
import sublandroid.messages.*;

import static sublandroid.Log.*;
import static sublandroid.core.BuildStatus.Error;

/**
 * Process any Android XML results
 */
public abstract class Xml extends Source {

	public static List<MHighlight> searchXmlHighlights(final Command.ModelInvocation<BuildStatus> modelInvocation) {
		final ByteArrayOutputStream outputStream = modelInvocation.getStandardErr();
		final String[] lines = NL_PATTERN.split(new String(outputStream.toByteArray()));

		return searchXmlHighlights(modelInvocation, lines);
	}

	public static List<MHighlight> searchXmlHighlights(
	 Command.ModelInvocation<BuildStatus> modelInvocation, final String... lines)  {

		BuildStatus buildStatus = modelInvocation.get();

		List<MHighlight> errors = new LinkedList<>();

		if (buildStatus.getStatus().code() != BuildStatus.Status.Ok.code()) {
			println(modelInvocation.getStandardErr());
		}

		return errors;
	}
}