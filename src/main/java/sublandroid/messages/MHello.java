package sublandroid.messages;

import java.util.*;

public class MHello extends Message {

	public Date date = new Date();
	public String message = "Woohoo!";
	public String gradleVersion = null;

	public MHello() {
		
	}

	public MHello(String gradleVersion) {
		this.gradleVersion = gradleVersion;
	}

}