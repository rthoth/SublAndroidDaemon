package sublandroid.messages;


public class MHighlight extends Message {
		public String language;
		public String fileName;
		public int lineNumber;
		public String kind;
		public String what;
		public String where;
		public String description;

		public MHighlight() {

		}

		@Override
		public String toString() {
			return String.format("%s: %d: %s, %s, %s(%s)", fileName, lineNumber, kind, what, where, description);
		}
	}