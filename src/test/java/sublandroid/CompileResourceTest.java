package sublandroid;

import sublandroid.messages.*;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.Util.*;
import static sublandroid.Path.*;

public class CompileResourceTest {

	@Test(timeOut=10000)
	public void noErrors() throws Throwable {

		try (ClientContext ctx = new ClientContext(JAVA_SINTAX_ERROR, 5000 + (int) (Math.random() * 100))) {
			send(MCommand.from("compileResource"), ctx.writer);

			MResourceCompile resourceCompile = read(ctx.reader, MResourceCompile.class);

			assertThat(resourceCompile).isNotNull();
		}
	}

	@Test(timeOut=10000)
	public void androidManifestError() throws Throwable {
		try (ClientContext ctx = new ClientContext(RESOURCE_ERROR_01, 3455)) {
			send(MCommand.from("compileResource"), ctx.writer);

			MResourceCompile resourceCompile = read(ctx.reader, MResourceCompile.class);

			assertThat(resourceCompile.failures).hasSize(2);
		}
	}

}