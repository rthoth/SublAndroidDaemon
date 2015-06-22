package sublandroid;

import sublandroid.messages.*;

import java.io.*;

import org.testng.annotations.*;

import static org.assertj.core.api.Assertions.*;
import static sublandroid.Path.*;
import static sublandroid.Util.*;

import com.fasterxml.jackson.databind.*;

public class ConnectorTest {


	@Test(timeOut=1000)
	public void loadValidDirectory() throws Exception {
		final Connector connector = new Connector(join("./test-data/simple-01"));

		final PipedWriter outputWriter = new PipedWriter();
		final PipedReader outputReader = new PipedReader(outputWriter);

		final PipedWriter inputWriter = new PipedWriter();
		final BufferedReader inputReader = new BufferedReader(new PipedReader(inputWriter));

		final Runner runner = new Runner(connector, outputReader, inputWriter);

		send(MCommand.from("start"), outputWriter);

		Started response = read(inputReader, Started.class);

		assertThat(response.message).isEqualTo("Woohoo!");
	}

}