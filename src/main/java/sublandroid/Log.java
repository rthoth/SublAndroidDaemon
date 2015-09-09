package sublandroid;

import java.io.*;

import static sublandroid.core.BuildStatus.Error;

import static java.lang.String.format;

public class Log {

	public static void println(ByteArrayOutputStream byteArrayOutputStream) {
		println(new String(byteArrayOutputStream.toByteArray()));
	}

	public static void println(String msg) {
		System.out.println(msg);
	}

	public static void println(String msg, Object... args) {
		System.out.println(format(msg, args));
	}

	public static void println(Error error) {
		Error cause = error.getCause();
		StackTraceElement causeFrame = null;

		if (cause != null)
			causeFrame = stopFrame(cause.getStackTrace());

		println("Error: %s - %s", error.getType(), error.getMessage());

		StackTraceElement[] stack = error.getStackTrace();
		int lastFrame = stack.length;

		loop: while (error != null) {

			for (int i=0 ; i < lastFrame; i++) {
				if (stack[i].equals(causeFrame)) {

					println(format("Caused by %s - %s", cause.getType(), cause.getMessage()));
					error = cause;
					stack = error.getStackTrace();
					cause = error.getCause();

					if (cause != null)
						causeFrame = stopFrame(cause.getStackTrace());

					lastFrame = i;

					continue loop;
				}

				println('\t' + format(stack[i].toString()));
			}

			error = null;
		}

	}


	private static StackTraceElement stopFrame(StackTraceElement[] stack) {
		int last = stack.length - 1;
		return (last >= 0) ? stack[last] : null;
	}

}