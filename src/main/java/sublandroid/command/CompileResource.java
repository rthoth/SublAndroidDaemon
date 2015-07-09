package sublandroid.command;

import java.io.*;
import java.nio.file.*;
import java.util.regex.*;

import sublandroid.messages.*;

import org.gradle.tooling.*;
import org.gradle.tooling.model.*;
import org.gradle.api.tasks.*;


public class CompileResource extends Command {

	public static final String COMMAND = "compileResource";

	protected static final String GENERATE_DEBUG_SRCS = "generateDebugSources";

	protected static final String PREPARE_DEBUG_DEPS = "prepareDebugDependencies";

	protected static final String WHAT_TASK_FAILED_PATTERN = "Execution\\sfailed\\sfor\\stask\\s'([^']+)'\\.";
	protected static final Pattern WHAT_TASK_FAILED = Pattern.compile(WHAT_TASK_FAILED_PATTERN);

	protected static final String MANIFEST_ERROR_PATTERN = "[^;]+; lineNumber: (\\d+); columnNumber: \\d+; (.+)";
	protected static final Pattern MANIFEST_ERROR = Pattern.compile(MANIFEST_ERROR_PATTERN);
	
	protected String projectPath = null;
	protected MResourceCompile resourceCompile = new MResourceCompile();
	protected Context ctx = null;

	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection) {
		ctx = Context.from(connection);
		projectPath = mCommand.projectPath;
		ctx.tasks(GENERATE_DEBUG_SRCS);

		try {
			ctx.run();

		} catch (BuildException buildException) {
			searchFailures();
		}

		return resourceCompile;
	}

	protected void manifestError(String cause) {
		final Matcher manifestError = MANIFEST_ERROR.matcher(cause);
		if (manifestError.matches()) {

			Path filePath = FileSystems.getDefault().getPath(projectPath, "src", "main", "AndroidManifest.xml");
			int lineNumber = Integer.parseInt(manifestError.group(1));
			String what = manifestError.group(2);

			resourceCompile.addFailure(new MHighlight(filePath.toString(), lineNumber, "error", what, null));
		} else {
			throw new IllegalArgumentException("Invalid manifest error line: " + cause);
		}
	}

	protected void searchFailures() {
		String[] lines = LINE_BREAK_PATTERN.split(new String(ctx.error.toByteArray()));

		for (int i=0; i<lines.length; i++) {
			final Matcher whatTaskMatcher = WHAT_TASK_FAILED.matcher(lines[i]);

			if (whatTaskMatcher.matches()) {
				final String failedTask = whatTaskMatcher.group(1);

				if (failedTask.endsWith(PREPARE_DEBUG_DEPS)) {
					manifestError(lines[++i]);
					return;
				}
			}
		}


		throw new IllegalStateException("No errors!");
	}

}