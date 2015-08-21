package sublandroid.messages;

import java.util.*;

public class MSourceHighlights extends Message {


	public List<MHighlight> failures = null;

	public MSourceHighlights() {

	}

	public MSourceHighlights addFailure(MHighlight highlight) {

		if (highlight != null) {
			if (failures == null)
				failures = new LinkedList<>();

			failures.add(highlight);
		}

		return this;
	}

}