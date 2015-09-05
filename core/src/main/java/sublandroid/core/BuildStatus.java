package sublandroid.core;

import java.util.*;

import org.gradle.tooling.model.*;

/**
 * Capture any gradle errors for any tasks, well this tries!
 */
public interface BuildStatus extends Model  {

	public static interface Status extends java.io.Serializable {

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

		String code();
	}
	
	public Throwable getError();

	public List<String> getErrors();

	public String getFailedTaskName();

	public String getFailedTaskPath();

	public Status getStatus();

	public List<String> getTasks();

}