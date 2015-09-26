package sublandroid.core;

import java.util.*;

import org.gradle.tooling.model.*;

/**
 * Capture any gradle errors for any tasks, well this tries!
 */
public interface BuildStatus extends Model  {
	
	/**
	 * Throwable caught
	 */
	public Error getError();

	/**
	 * Validation errros
	 */
	public List<String> getErrors();

	public String getFailedTaskName();

	public String getFailedTaskPath();

	public Status getStatus();

	public List<String> getTasks();

	static interface Error extends java.io.Serializable {

		Error getCause();

		String getMessage();

		StackTraceElement[] getStackTrace();

		String getType();
	}

	static interface Status extends java.io.Serializable {

		String code();

		static final Status ActionError = new Status() {
			public String code() { return "ActionError"; }
		};

		static final Status Ok = new Status() {
			public String code() { return "Ok"; }
		};

		static final Status UnexpectedValidationError = new Status() {
			public String code() { return "UnexpectedValidationError"; }
		};

		static final Status ValidationError = new Status() {
			public String code() { return "ValidationError"; }
		};
	}

}