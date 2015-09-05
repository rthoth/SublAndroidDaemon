package sublandroid.command;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.*;

import sublandroid.core.*;
import sublandroid.messages.*;
import sublandroid.plugin.*;

import org.gradle.tooling.*;
import org.gradle.tooling.model.*;
import org.gradle.api.tasks.*;

import static sublandroid.utils.Xml.searchXmlHighlights;

public class CompileResource extends Command {

	public static final String COMMAND = "compileResource";

	public static final String GRADLE_TASK = "generateDebugSources";

	public Message execute(MCommand mCommand, ProjectConnection connection) {

		final ModelInvocation<BuildStatus> invocation = Gradle.from(connection)
		 .plugins(BuildStatusPlugin.class).model(BuildStatus.class, GRADLE_TASK);

		final MSourceHighlights message = new MSourceHighlights();
		BuildStatus buildStatus = invocation.get();

		println("BuildStatus: %s", buildStatus.getStatus().code());

		if (buildStatus.getStatus().code() != BuildStatus.Status.Ok.code()) {
			List<MHighlight> highlights = searchXmlHighlights(invocation);

			if (!highlights.isEmpty())
				message.addFailures(highlights);
		}

		return message;
	}

}