package ajsc.utils;

import java.io.PrintWriter;
import java.io.StringWriter;


public class AjscUtil {

	public static String getStackTrace(Throwable aThrowable) {
		final StringWriter result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
