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

		public MHighlight(
			String language, String fileName, int lineNumber, String kind,
			String what, String where) {

			this(language, fileName, lineNumber, kind, what, where, null);
		}
		

		public MHighlight(
			String language, String fileName, int lineNumber, String kind,
			String what, String where, String description) {

			this.language = language;
			this.fileName = fileName;
			this.lineNumber = lineNumber;
			this.kind = kind;
			this.what = what;
			this.where = where;
			this.description = description;
		}

		@Override
		public String toString() {
			return String.format("%s: %d: %s, %s, %s(%s)", fileName, lineNumber, kind, what, where, description);
		}
	}