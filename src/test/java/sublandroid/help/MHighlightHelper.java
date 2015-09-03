package sublandroid.help;

import sublandroid.messages.*;

import static org.assertj.core.api.Assertions.*;

public class MHighlightHelper {
	public static void assertFileName(MHighlight highlight, String fileName) {
		assertThat(highlight.fileName).endsWith(fileName);
	}

	public static void assertLine(MHighlight highlight, int line) {
		assertThat(highlight.lineNumber).isEqualTo(line);
	}

	public static void assertWhat(MHighlight highlight, String what) {
		assertThat(highlight.what).isEqualTo(what);
	}

	public static void assertWhere(MHighlight highlight, String where) {
		assertThat(highlight.where).isEqualTo(where);
	}

	public static void assertDescription(MHighlight highlight, String description) {
		assertThat(highlight.description).isEqualTo(description);
	}
}