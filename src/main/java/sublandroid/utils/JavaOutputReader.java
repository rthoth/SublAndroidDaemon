package sublandroid.utils;

import java.util.*;
import sublandroid.messages.*;

/**
 * Process any java errors, warnings whatelse
 */
public class JavaOutputReader extends OutputReader {

	@Override
	public List<MHighlight> errorLine(int lineNumber, String line) {
		return null;
	}

	@Override public String toString() {
		return "JavaOutputReader";
	}
}