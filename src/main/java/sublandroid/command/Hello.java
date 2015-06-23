package sublandroid.command;

import sublandroid.messages.*;

import org.gradle.tooling.*;
import org.gradle.tooling.model.build.*;

public class Hello extends Command {
	public static final String COMMAND = "start";


	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection) {
		BuildEnvironment buildEnv = connection.getModel(BuildEnvironment.class);


		return new MHello(buildEnv.getGradle().getGradleVersion());
	}
}