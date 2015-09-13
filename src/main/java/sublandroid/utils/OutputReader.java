package sublandroid.utils;

import sublandroid.command.Command.Invocation;
import sublandroid.messages.*;

import java.util.*;

public abstract class OutputReader {

	public abstract List<MHighlight> errorLine(int lineNumber, String line);

}