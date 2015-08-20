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

	public Message execute(MCommand mCommand, ProjectConnection connection) {
		return null;
	}

}