package sublandroid.command;

import sublandroid.core.*;
import sublandroid.messages.*;
import sublandroid.plugin.*;

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

	protected static final Pattern SEMANTIC_ERROR = Pattern.compile("^[^\\s]+\\s[^\\s]+\\sin\\sclass");


	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection)	{
		final Context context = Context.from(connection);

		ModelInvocation<BuildStatus> invocation = context.plugin(BuildStatusPlugin.class)
		                              .model(BuildStatus.class, GRADLE_TASK);

		final MJavaCompile message = new MJavaCompile();

		BuildStatus buildStatus = invocation.get();

		return message;
	}
}