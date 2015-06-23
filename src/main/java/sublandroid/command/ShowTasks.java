package sublandroid.command;

import sublandroid.messages.*;

import org.gradle.tooling.*;
import org.gradle.tooling.model.*;

public class ShowTasks extends Command {

	public static final String COMMAND = "showTasks";

	@Override
	public Message execute(MCommand mCommand, ProjectConnection connection) {
		GradleProject gradleProject = connection.getModel(GradleProject.class);

		MList tasks = new MList<MTask>();


		for (GradleTask task : gradleProject.getTasks().getAll()) {
			tasks.add(new MTask(task));
		}

		return tasks;
	}

}