package sublandroid.messages;

import java.util.*;

public class MJavaCompile extends Message {


	public List<MHighlight> failures = null;

	public MJavaCompile() {

	}

	public MJavaCompile addJavaFailure(MHighlight highlight) {

		if (highlight != null) {
			if (failures == null)
				failures = new LinkedList<>();

			failures.add(highlight);
		}

		return this;
	}

}