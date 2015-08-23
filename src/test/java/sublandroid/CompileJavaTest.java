package sublandroid;

import sublandroid.messages.*;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.Util.*;
import static sublandroid.Path.*;

public class CompileJavaTest {

	public void assertFileName(MHighlight highlight, String fileName) {
		assertThat(highlight.fileName).endsWith(fileName);
	}

	public void assertLine(MHighlight highlight, int line) {
		assertThat(highlight.lineNumber).isEqualTo(line);
	}

	public void assertWhat(MHighlight highlight, String what) {
		assertThat(highlight.what).isEqualTo(what);
	}

	public void assertWhere(MHighlight highlight, String where) {
		assertThat(highlight.where).isEqualTo(where);
	}

	public void assertDescription(MHighlight highlight, String description) {
		assertThat(highlight.description).isEqualTo(description);
	}
	
	@Test(timeOut= 20000)
	public void detectSyntaxJavaErrors() throws Throwable {
		try (ClientContext context = new ClientContext(JAVA_SINTAX_ERROR, 54321)) {
			
			send(MCommand.from("compileJava"), context.writer);

			MSourceHighlights result = read(context.reader, MSourceHighlights.class);

			assertThat(result).isNotNull();
			assertThat(result.failures).isNotNull();
			assertThat(result.failures).hasSize(2);

			MHighlight failure = result.failures.get(0);

			final String Main_java = absolutePath(JAVA_SINTAX_ERROR,"src","main","java","org","error","Maine.java");

			assertThat(failure.fileName).isEqualTo(Main_java);
			assertThat(failure.lineNumber).isEqualTo(12);
			assertThat(failure.kind).isEqualTo("error");
			assertThat(failure.what).isEqualTo("illegal start of expression");
			assertThat(failure.where).isEqualTo("        super.onCreate(savedInstanceState,);");

			final String App_java = absolutePath(JAVA_SINTAX_ERROR, "src", "main", "java", "com", "app", "App.java");
			failure = result.failures.get(1);

			assertThat(failure.fileName).isEqualTo(App_java);
			assertThat(failure.lineNumber).isEqualTo(7);
			assertThat(failure.kind).isEqualTo("error");
			assertThat(failure.what).isEqualTo("';' expected");
			assertThat(failure.where).isEqualTo("		throw new RuntimeException()");
		}
	}

	@Test(timeOut=20000)
	public void detectSemanticJavaErrors() throws Throwable {
		try (ClientContext context = new ClientContext(JAVA_SEMANTIC_ERROR, 12098)) {
			send(MCommand.from("compileJava"), context.writer);

			MSourceHighlights result = read(context.reader, MSourceHighlights.class);

			assertThat(result).isNotNull();
			assertThat(result.failures).isNotNull();
			assertThat(result.failures).hasSize(8);

			assertFileName(result.failures.get(0), "Application.java");
			assertFileName(result.failures.get(1), "Application.java");
			assertFileName(result.failures.get(2), "Application.java");
			assertFileName(result.failures.get(3), "Boot.java");
			assertFileName(result.failures.get(4), "Boot.java");
			assertFileName(result.failures.get(5), "Boot.java");
			assertFileName(result.failures.get(6), "Boot.java");
			assertFileName(result.failures.get(7), "Boot.java");


			assertLine(result.failures.get(0), 5);
			assertLine(result.failures.get(1), 6);
			assertLine(result.failures.get(2), 7);
			assertLine(result.failures.get(3), 9);
			assertLine(result.failures.get(4), 11);
			assertLine(result.failures.get(5), 13);
			assertLine(result.failures.get(6), 22);
			assertLine(result.failures.get(7), 24);

			for (MHighlight highlight : result.failures) {
				assertThat(highlight.kind).isEqualTo("error");
			}

			assertWhat(result.failures.get(0), "cannot find symbol");
			assertWhat(result.failures.get(1), "package java.notfound does not exist");
			assertWhat(result.failures.get(2), "package java.notfound2 does not exist");
			assertWhat(result.failures.get(3), "method aMethod in class Application cannot be applied to given types;");
			assertWhat(result.failures.get(4), "cannot find symbol");
			assertWhat(result.failures.get(5), "method aMethod in class Application cannot be applied to given types;");
			assertWhat(result.failures.get(6), "constructor Application in class Application cannot be applied to given types;");
			assertWhat(result.failures.get(7), "method aMethod in class Application cannot be applied to given types;");

			assertWhere(result.failures.get(0), "import java.util.NotFoundException;");
			assertWhere(result.failures.get(1), "import java.notfound.*;");
			assertWhere(result.failures.get(2), "import java.notfound2.NotFoundException;");
			assertWhere(result.failures.get(3), "		app.aMethod(\"\", 1, false);");
			assertWhere(result.failures.get(4), "		app.hello();");
			assertWhere(result.failures.get(5), "		app.aMethod(\"\", true);");
			assertWhere(result.failures.get(6), "		Application app3 = new Application(true);");
			assertWhere(result.failures.get(7), "		app3.aMethod(false, true);");

			assertDescription(result.failures.get(0), "cannot find symbol class NotFoundException in package java.util");
			assertDescription(result.failures.get(1), null);
			assertDescription(result.failures.get(2), null);
			assertDescription(result.failures.get(3), "(required: String,int), (found: String,int,boolean), (reason: actual and formal argument lists differ in length)");
			assertDescription(result.failures.get(4), "cannot find symbol method hello() in variable app of type Application");
			assertDescription(result.failures.get(5), "(required: String,int), (found: String,boolean), (reason: actual argument boolean cannot be converted to int by method invocation conversion)");
			assertDescription(result.failures.get(6), "(required: no arguments), (found: boolean), (reason: actual and formal argument lists differ in length)");
			assertDescription(result.failures.get(7), "(required: String,int), (found: boolean,boolean), (reason: actual argument boolean cannot be converted to String by method invocation conversion)");
		}
	}
}