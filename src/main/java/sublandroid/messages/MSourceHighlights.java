package sublandroid.messages;

import java.util.*;

public class MSourceHighlights extends Message {


	public List<MHighlight> failures = null;

	public MSourceHighlights() {

	}

	public MSourceHighlights addFailure(MHighlight highlight) {

		if (failures == null)
			failures = new LinkedList<>();

		failures.add(highlight);

		return this;
	}

	public MSourceHighlights addFailures(final Collection<MHighlight> failures) {
		if (this.failures == null)
			this.failures = new LinkedList<>(failures);
		else
			this.failures.addAll(failures);

		return this;
	}

}