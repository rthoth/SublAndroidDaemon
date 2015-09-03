package sublandroid;

import sublandroid.messages.*;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.help.Helper.*;
import static sublandroid.help.MHighlightHelper.*;
import static sublandroid.Path.*;

public class CompileJavaTest {

	@Test(timeOut= 20000)
	public void detectSyntaxJavaErrors() throws Throwable {
		try (Client client = new Client(JAVA_SINTAX_ERROR, 54321)) {
			
			client.send(MCommand.from("compileJava"));

			MSourceHighlights result = client.read(MSourceHighlights.class);

			assertThat(result).isNotNull();
			assertThat(result.failures).isNotNull();
			assertThat(result.failures).hasSize(2);

			MHighlight failure = result.failures.get(0);

			final String Main_java = absolutePath(JAVA_SINTAX_ERROR,"src","main","java","org","error","Main.java");

			assertThat(failure.fileName).isEqualTo(Main_java);
			assertThat(failure.lineNumber).isEqualTo(12);
			assertThat(failure.kind).isEqualTo("error");
			assertThat(failure.what).isEqualTo("';' expected");
			assertThat(failure.where).isEqualTo("        super.onCreate(savedInstanceState)");

			final String App_java = absolutePath(JAVA_SINTAX_ERROR, "src", "main", "java", "org", "error", "App.java");
			failure = result.failures.get(1);

			assertThat(failure.fileName).isEqualTo(App_java);
			assertThat(failure.lineNumber).isEqualTo(6);
			assertThat(failure.kind).isEqualTo("error");
			assertThat(failure.what).isEqualTo("illegal start of expression");
			assertThat(failure.where).isEqualTo("		return,;");
		}
	}

	@Test(timeOut=20000)
	public void detectSemanticJavaErrors() throws Throwable {
		try (Client client = new Client(JAVA_SEMANTIC_ERROR, 12098)) {
			client.send(MCommand.from("compileJava"));

			MSourceHighlights result = client.read(MSourceHighlights.class);

			assertThat(result).isNotNull();
			assertThat(result.failures).isNotNull();
			assertThat(result.failures).hasSize(7);

			assertFileName(result.failures.get(0), "Main.java");
			assertFileName(result.failures.get(1), "Main.java");
			assertFileName(result.failures.get(2), "Main.java");
			assertFileName(result.failures.get(3), "Main.java");
			assertFileName(result.failures.get(4), "Some.java");
			assertFileName(result.failures.get(5), "Some.java");
			assertFileName(result.failures.get(6), "Some.java");


			assertLine(result.failures.get(0), 6);
			assertLine(result.failures.get(1), 15);
			assertLine(result.failures.get(2), 20);
			assertLine(result.failures.get(3), 24);
			assertLine(result.failures.get(4), 4);
			assertLine(result.failures.get(5), 8);
			assertLine(result.failures.get(6), 13);

			for (MHighlight highlight : result.failures) {
				assertThat(highlight.kind).isEqualTo("error");
			}

			assertWhat(result.failures.get(0), "interface expected here");
			assertWhat(result.failures.get(1), "cannot find symbol");
			assertWhat(result.failures.get(2), "cannot return a value from method whose result type is void");
			assertWhat(result.failures.get(3), "cannot find symbol");
			assertWhat(result.failures.get(4), "missing method body, or declare abstract");
			assertWhat(result.failures.get(5), "cannot find symbol");
			assertWhat(result.failures.get(6), "method call in class Some cannot be applied to given types;");

			assertWhere(result.failures.get(0), "public class Main extends Activity implements Some");
			assertWhere(result.failures.get(1), "        doSomethingg();");
			assertWhere(result.failures.get(2), "    	return \"\";");
			assertWhere(result.failures.get(3), "    	new Create();");
			assertWhere(result.failures.get(4), "	public String doSomething();");
			assertWhere(result.failures.get(5), "		main.onCreatee(null);");
			assertWhere(result.failures.get(6), "		call(5);");

			assertDescription(result.failures.get(0), null);
			assertDescription(result.failures.get(1), "cannot find symbol method doSomethingg() in class Main");
			assertDescription(result.failures.get(2), null);
			assertDescription(result.failures.get(3), "cannot find symbol class Create in class Main");
			assertDescription(result.failures.get(4), null);
			assertDescription(result.failures.get(5), "cannot find symbol method onCreatee(<null>) in variable main of type Main");
			assertDescription(result.failures.get(6), "(required: no arguments), (found: int), (reason: actual and formal argument lists differ in length)");
		}
	}
}