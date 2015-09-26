package sublandroid.utils;

import java.util.*;
import sublandroid.command.Command.ModelInvocation;
import sublandroid.core.BuildStatus;
import sublandroid.messages.*;

/**
 * Process any java errors, warnings whatelse
 */
public class JavaOutputReader extends OutputReader<ModelInvocation<BuildStatus>> {

	@Override public void errorLine(int number, String line) {
	}

	@Override public boolean hasError() {
		return true;
	}

	@Override public List<MHighlight> lastHighlights() {
		return null;
	}

	@Override public boolean onApply() {
		return false;
	}

	@Override public String toString() {
		return "JavaOutputReader";
	}
}