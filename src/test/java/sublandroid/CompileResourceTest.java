package sublandroid;

import sublandroid.messages.*;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.Path.*;
import static sublandroid.help.Helper.*;

public class CompileResourceTest {

	@Test(timeOut=10000)
	public void noErrors() throws Throwable {

		try (Client client = new Client(JAVA_SINTAX_ERROR, 5000 + (int) (Math.random() * 100))) {
			
			client.send(MCommand.from("compileResource"));

			/*MResourceCompile resourceCompile = read(client.reader, MResourceCompile.class);

			assertThat(resourceCompile).isNotNull();*/

			MSourceHighlights resourceCompile = client.read(MSourceHighlights.class);
			assertThat(resourceCompile).isNotNull();
		}
	}

	@Test(timeOut=10000)
	public void androidManifestError() throws Throwable {
		try (Client client = new Client(RESOURCE_ERROR_01, 3455)) {
			client.send(MCommand.from("compileResource"));

			MSourceHighlights highlights = client.read(MSourceHighlights.class);
			assertThat(highlights).isNotNull();
		}
	}

}