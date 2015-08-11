package sublandroid.core;

public interface Status extends java.io.Serializable {

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
