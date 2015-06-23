package sublandroid.command;

import sublandroid.messages.*;
import org.gradle.tooling.*;

public abstract class Command {
	public abstract Message execute(MCommand mCommand, ProjectConnection connection);
}