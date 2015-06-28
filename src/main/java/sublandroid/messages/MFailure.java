package sublandroid.messages;

public class MFailure extends Message {
	public String message;
	public String type;
	public MFailure cause = null;

	public MFailure() {

	}

	public MFailure(Throwable throwable) {
		message = throwable.getMessage();
		type = throwable.getClass().getCanonicalName();

		if (throwable.getCause() != null)
			cause = new MFailure(throwable.getCause());
	}

	public MFailure(String message, String type) {
		this.message = message;
		this.type = type;
	}

	private String getDescription() {
		return String.format("%s(%s)", type, message);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(getDescription());

		for (MFailure cause = this.cause; cause != null; cause = cause.cause) {
			builder.append(" --> ").append(cause.getDescription());
		}

		return builder.toString();
	}
}