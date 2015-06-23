package sublandroid.messages;

import org.gradle.tooling.model.*;

public class MTask extends Message {

	public String description;
	public String name;
	public boolean isPublic;

	public MTask() {
		
	}

	public MTask(GradleTask gradleTask) {
		description = gradleTask.getDescription();
		name = gradleTask.getName();
		isPublic = gradleTask.isPublic();
	}

}