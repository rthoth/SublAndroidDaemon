package sublandroid.command;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.*;

import sublandroid.Log;
import sublandroid.core.*;
import sublandroid.messages.*;
import sublandroid.plugin.*;

import org.gradle.tooling.*;
import org.gradle.tooling.model.*;
import org.gradle.api.tasks.*;

import sublandroid.core.BuildStatus.Error;
import static sublandroid.utils.Xml.searchXmlHighlights;

public class CompileResource extends Command {

	public static final String COMMAND = "compileResource";

	public static final String GRADLE_TASK = "generateDebugSources";

	public Message execute(MCommand mCommand, ProjectConnection connection) {

		final ModelInvocation<BuildStatus> invocation = Gradle.from(connection)
		 .plugins(BuildStatusPlugin.class).model(BuildStatus.class, GRADLE_TASK);

		final MSourceHighlights message = new MSourceHighlights();
		BuildStatus buildStatus = invocation.get();

		Log.println("BuildStatus: %s", buildStatus.getStatus().code());

		//println(invocation.getStandardOut());

		if (buildStatus.getStatus().code() != BuildStatus.Status.Ok.code()) {

			//Log.println(buildStatus.getError());

			List<MHighlight> highlights = searchXmlHighlights(invocation);

			if (highlights != null && !highlights.isEmpty())
				message.addFailures(highlights);
		}

		return message;
	}

}