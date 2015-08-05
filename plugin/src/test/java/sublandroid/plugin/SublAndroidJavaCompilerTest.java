package sublandroid.plugin;

import org.testng.annotations.*;

import static sublandroid.plugin.Utils.*;

public class SublAndroidJavaCompilerTest {

	@Test
	public void collectError() {
		final Context ctx = new Context("test-data/projects/java-error-01");
		final Result result = ctx.execute("sublandroidCompileJava");

		
	}

}