package sublandroid.messages;

import java.util.*;

public class MJavaCompile extends Message {


	public List<MHighlight> failures = null;

	public MJavaCompile() {

	}

	public MJavaCompile addJavaFailure(
		String fileName, int lineNumber, String kind,
		String what, String where) {

		return addJavaFailure(fileName, lineNumber, kind, what, where, null);
	}

	public MJavaCompile addJavaFailure(
		String fileName, int lineNumber, String kind,
		String what, String where, String description) {

		final MHighlight failure = new MHighlight(
			fileName, lineNumber, kind, what, where, description
		);

		if (failures == null)
			failures = new LinkedList<>();

		failures.add(failure);

		return this;
	}

}