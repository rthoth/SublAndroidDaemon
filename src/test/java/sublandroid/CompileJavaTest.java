package sublandroid;

import sublandroid.messages.*;
import sublandroid.messages.MJavaCompile.JavaSourceFailure;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.Util.*;
import static sublandroid.Path.*;

public class CompileJavaTest {
	
	@Test(timeOut= 20000)
	public void detectCompileError() throws Throwable {
		try (ClientContext context = new ClientContext(JAVA_SINTAX_ERROR, 54321)) {
			
			send(MCommand.from("compileJava"), context.writer);

			MJavaCompile result = read(context.reader, MJavaCompile.class);

			assertThat(result).isNotNull();
			assertThat(result.failures).isNotNull();
			assertThat(result.failures).hasSize(1);

			JavaSourceFailure failure1 = result.failures.get(0);

			final String Main_java = absolutePath(JAVA_SINTAX_ERROR,"src","main","java","com","app","Main.java");

			assertThat(failure1.fileName).isEqualTo(Main_java);
			assertThat(failure1.lineNumber).isEqualTo(12);
			assertThat(failure1.kind).isEqualTo("error");
			assertThat(failure1.what).isEqualTo("illegal start of expression");
			assertThat(failure1.how).isEqualTo("super.onCreate(savedInstanceState,);");
		}
	}
}