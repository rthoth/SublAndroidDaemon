package sublandroid.messages;

import java.util.*;

public class MJavaCompile extends Message {

	public static class JavaSourceFailure {
		public String fileName;
		public int lineNumber;
		public String kind;
		public String what;
		public String how;

		public JavaSourceFailure() {

		}

		public JavaSourceFailure(String fileName, int lineNumber, String kind, String what, String how) {
			this.fileName = fileName;
			this.lineNumber = lineNumber;
			this.kind = kind;
			this.what = what;
			this.how = how;
		}
	}


	public List<JavaSourceFailure> failures = null;

	public MJavaCompile() {

	}

	public MJavaCompile addJavaFailure(String fileName, int lineNumber, String kind, String what, String how) {
		final JavaSourceFailure failure = new JavaSourceFailure(fileName, lineNumber, kind, what, how);

		if (failures == null)
			failures = new LinkedList<>();

		failures.add(failure);

		return this;
	}

}