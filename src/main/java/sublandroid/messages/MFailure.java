package sublandroid.messages;

public class MFailure extends Message {
	public String message;
	public String type;
	public MFailure cause;

	public MFailure() {

	}

	public MFailure(Throwable throwable) {
		message = throwable.getMessage();
		type = throwable.getClass().getCanonicalName();

		if (throwable.getCause() != null)
			cause = new MFailure(throwable.getCause());
	}
}