package sublandroid.core;

import java.util.*;

import org.gradle.tooling.model.*;

/**
 * Capture any gradle errors for any tasks, well this tries!
 */
public interface CatchException extends Model  {

	public static interface Status extends java.io.Serializable {

		static final Status ActionError = new Status() {
			public String status() { return "ActionError"; }
		};

		static final Status Ok = new Status() {
			public String status() { return "Ok"; }
		};

		static final Status UnexpectedValidationError = new Status() {
			public String status() { return "UnexpectedValidationError"; }
		};

		static final Status ValidationError = new Status() {
			public String status() { return "ValidationError"; }
		};


		String status();

	}
	
	public Throwable getError();

	public List<String> getErrors();

	public String getFailedTaskName();

	public String getFailedTaskPath();

	public Status getStatus();

	public List<String> getTasks();

}