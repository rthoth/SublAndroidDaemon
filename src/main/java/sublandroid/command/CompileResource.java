package sublandroid.command;

import sublandroid.Log;
import sublandroid.core.*;
import sublandroid.messages.*;
import sublandroid.plugin.*;
import sublandroid.utils.*;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;
import java.util.*;

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

		if (buildStatus.getStatus().code() != BuildStatus.Status.Ok.code()) {

			InvocationReader<ModelInvocation<BuildStatus>> invocationReader = new InvocationReader<>(invocation);

			List<MHighlight> highlights = invocationReader.read(new XmlOutputReader());

			Log.println("--->" + invocation.getErrString() + "<---");
			Log.println("--->" + invocation.getOutString() + "<---");
		}

		return message;
	}

}