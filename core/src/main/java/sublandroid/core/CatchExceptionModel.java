package sublandroid.core;

import java.util.*;

public interface CatchExceptionModel {
	
	public Throwable getError();

	public List<String> getErrors();

	public String getFailedTaskName();

	public String getFailedTaskPath();

	public Status getStatus();

	public List<String> getTasks();

}