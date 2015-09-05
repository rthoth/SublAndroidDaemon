package sublandroid.utils;

import java.util.*;
import java.io.*;

import sublandroid.command.Command;
import sublandroid.core.*;
import sublandroid.messages.*;

public abstract class Xml extends Source {

	public static List<MHighlight> searchXmlHighlights(final Command.ModelInvocation<BuildStatus> modelInvocation) {
		final ByteArrayOutputStream outputStream = modelInvocation.getStandardErr();
		final String[] lines = NL_PATTERN.split(new String(outputStream.toByteArray()));

		return searchXmlHighlights(modelInvocation, lines);
	}

	public static List<MHighlight> searchXmlHighlights(
	 Command.ModelInvocation<BuildStatus> modelInvocation, final String... lines)  {


		return null;
	}
}