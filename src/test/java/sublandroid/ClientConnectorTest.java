package sublandroid;

import sublandroid.messages.*;

import static sublandroid.Path.*;
import static sublandroid.help.Helper.*;

import java.io.*;

import org.testng.annotations.*;

import com.alibaba.fastjson.TypeReference;

import static org.assertj.core.api.Assertions.*;


public class ClientConnectorTest {

	@Test(timeOut=10000)
	public void helloCommand() throws Throwable {
		try (Client client = new Client(PROJECT_01, 12345)) {

			client.send(MCommand.from("hello"));

			MHello hello = client.read(MHello.class);

			assertThat(hello.message).isEqualTo("Woohoo!");
		}
	}


	@Test(timeOut=5000, expectedExceptions=CommandFailed.class)
	public void unknowCommand() throws Throwable {
		try(Client client = new Client(PROJECT_01, 12346)) {

			client.send(MCommand.from("unknowCommand"));

			client.read(Object.class);
		}
	}

}