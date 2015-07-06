package sublandroid.command;

import sublandroid.messages.MCommand;

public class Commands {

	public Command search(MCommand mCommand) {
		switch(mCommand.command) {
			case Hello.COMMAND:
				return new Hello();

			case ShowTasks.COMMAND:
				return new ShowTasks();

			case CompileJava.COMMAND:
				return new CompileJava();

			case CompileResource.COMMAND:
				return new CompileResource();
		}

		return null;
	}
}