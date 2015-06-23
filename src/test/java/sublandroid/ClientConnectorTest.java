package sublandroid;

import sublandroid.messages.*;
import static sublandroid.Path.*;
import static sublandroid.Util.*;

import java.io.*;

import org.testng.annotations.*;

import com.alibaba.fastjson.TypeReference;

import static org.assertj.core.api.Assertions.*;


public class ClientConnectorTest {

	@Test(timeOut=5000)
	public void helloCommand() throws Throwable {
		try (ClientContext context = new ClientContext(PROJECT_01, 12345)) {

			send(MCommand.from("hello"), context.writer);

			MHello hello = read(context.reader, MHello.class);

			assertThat(hello.message).isEqualTo("Woohoo!");
		}
	}

}