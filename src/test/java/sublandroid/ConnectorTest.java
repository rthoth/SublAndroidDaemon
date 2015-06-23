package sublandroid;

import sublandroid.messages.*;
import static sublandroid.Path.*;
import static sublandroid.Util.*;

import java.io.*;

import org.testng.annotations.*;

import com.alibaba.fastjson.TypeReference;

import static org.assertj.core.api.Assertions.*;


public class ConnectorTest {

	public static final String PROJECT_01 = "./test-data/simple-01";
	public static final long timeOutDefault = 5000;

	@Test(timeOut=timeOutDefault)
	public void helloCommand() throws Exception {
		Context context = new Context(PROJECT_01);

		context.run();

		send(MCommand.from("start"), context.writer);

		MHello response = read(context.reader, MHello.class);

		assertThat(response.message).isEqualTo("Woohoo!");
		assertThat(response.gradleVersion).isEqualTo("2.4");
	}

	@Test(timeOut=timeOutDefault)
	public void tasksCommand() throws Exception {
		Context context = new Context(PROJECT_01);
		context.run();

		send(MCommand.from("showTasks"), context.writer);

		MList<MTask> tasks = read(context.reader, LIST_TASKS);
	}


}