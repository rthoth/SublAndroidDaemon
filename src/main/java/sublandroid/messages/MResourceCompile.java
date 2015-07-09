package sublandroid.messages;

import java.util.*;

public class MResourceCompile extends Message {

	public List<MHighlight> failures = null;


	public MResourceCompile addFailure(MHighlight highlight) {
		if (highlight != null) {

			if (failures == null)
				failures = new LinkedList<>();

			failures.add(highlight);
		}

		return this;
	}

}