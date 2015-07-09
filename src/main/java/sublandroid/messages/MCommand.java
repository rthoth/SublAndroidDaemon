package sublandroid.messages;

import java.util.*;

public class MCommand {
	public String command = null;
	public transient String projectPath = null;
	public Map<String, Object> arguments = null;

	public MCommand() {

	}

	public MCommand(String command) {
		this.command = command;
	}

	public MCommand(String command, Map<String, Object> arguments) {
		this(command);
		this.arguments = arguments;
	}

	public static MCommand from(String command) {
		return new MCommand(command);
	}

	public static MCommand from(String command, Map<String, Object> arguments) {
		return new MCommand(command, arguments);
	}
}