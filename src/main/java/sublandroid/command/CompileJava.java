package sublandroid.command;

import sublandroid.core.*;
import sublandroid.messages.*;
import sublandroid.plugin.*;
import sublandroid.utils.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.gradle.tooling.*;

import static sublandroid.Log.*;
import static sublandroid.core.BuildStatus.*;
import static sublandroid.utils.Java.searchJavaHighlights;

public class CompileJava extends Command {

	public static final String COMMAND = "compileJava";

	protected static final String GRADLE_TASK = "compileDebugJava";

	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection)	{

		final ModelInvocation<BuildStatus> invocation = Gradle.from(connection)
		 .plugins(BuildStatusPlugin.class).model(BuildStatus.class, GRADLE_TASK);

		final MSourceHighlights message = new MSourceHighlights();

		BuildStatus buildStatus = invocation.get();

		println("BuildStatus: %s", buildStatus.getStatus().code());

		if (buildStatus.getStatus().code() != Status.Ok.code()) {

			List<MHighlight> highlights = new InvocationReader(invocation)
			                               .read(new XmlOutputReader(), new JavaOutputReader());
		}

		return message;
	}
}