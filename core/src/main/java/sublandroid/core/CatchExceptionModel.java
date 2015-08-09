package sublandroid.core;

import java.util.*;

public interface CatchExceptionModel {

	public interface Status {
		boolean isActionError();
		boolean isOk();
		boolean isUnexpectedValidationError();
		boolean isValidationError();
	}

	public Throwable getError();

	public List<String> getErrors();

	public String getFailedTaskName();

	public String getFailedTaskPath();

	public Status getStatus();

	public List<String> getTasks();

}