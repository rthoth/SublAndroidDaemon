package sublandroid;

import sublandroid.messages.*;

import org.gradle.tooling.*;

public class Start extends Command {
	public static final String COMMAND = "start";


	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection) {
		Started started = new Started();
		started.message = "Woohoo!";
		return started;
	}
}