package sublandroid;

import static java.lang.String.format;

public class Log {

	public static void println(String msg, Object... args) {
		System.out.println(format(msg, args));
	}

}