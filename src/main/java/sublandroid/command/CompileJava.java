package sublandroid.command;

import sublandroid.core.*;
import sublandroid.messages.*;
import sublandroid.plugin.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.gradle.tooling.*;

import static sublandroid.core.BuildStatus.*;
import static sublandroid.GradleUtils.searchJavaHighlights;

public class CompileJava extends Command {

	public static final String COMMAND = "compileJava";

	protected static final String CANNOT_FIND_SYMBOL = "cannot find symbol";

	protected static final Pattern DETAIL_PATTERN = Pattern.compile("^\\W*([^:]+):\\s+(.+)$");

	protected static final Pattern ERROR_PATTERN = Pattern.compile("^([^:]+):(\\d+):\\s+([^:]+):\\s+(.+)$");

	protected static final String GRADLE_TASK = "compileDebugJava";
	protected static final String GRADLE_TASK_CLEAN = "cleanCompileDebugJava";

	protected static final Pattern SEMANTIC_ERROR = Pattern.compile("^[^\\s]+\\s[^\\s]+\\sin\\sclass");


	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection)	{

		final ModelInvocation<BuildStatus> invocation = Gradle.from(connection)
		                                                 .plugins(BuildStatusPlugin.class)
		                                                 .model(BuildStatus.class, GRADLE_TASK_CLEAN, GRADLE_TASK);

    	final MSourceHighlights message = new MSourceHighlights();

    	BuildStatus buildStatus = invocation.get();

    	println("BuildStatus: %s", buildStatus.getStatus().code());
    	println(invocation.getStandardOut());

    	if (buildStatus.getStatus().code() != Status.Ok.code()) {
    		List<MHighlight> highlights = searchJavaHighlights(invocation.getStandardErr());
    		println(highlights.toString());
    		if (!highlights.isEmpty())
    			message.addFailures(highlights);
    	}

    	return message;
	}
}