package sublandroid.plugin;

import java.io.*;

import sublandroid.core.*;

import org.gradle.tooling.*;

import org.testng.annotations.*;

import static sublandroid.core.BuildStatus.Error;
import static sublandroid.plugin.TestHelpers.*;
import static org.assertj.core.api.Assertions.*;

public class BuildStatusPluginTest {

	@Test
	public void catchJavaError() {

		Context ctx = Context.from("test-data/projects/java-error-01", GRADLE_EXCEPTION_FILE);

		T3<ModelBuilder<BuildStatus>, ByteArrayOutputStream, ByteArrayOutputStream> t3;

		t3 = ctx.model(BuildStatus.class, "clean", "compileDebugJava");

		BuildStatus catchException = t3.a.get();

		assertThat(catchException.getStatus()).isNotNull();
		assertThat(catchException.getStatus().code()).isEqualTo("ActionError");
		assertThat(catchException.getFailedTaskName()).isEqualTo("compileDebugJava");

		Error error = catchException.getError();
		assertThat(error).isNotNull();

		for (Error cause = error; cause != null; cause = cause.getCause()) {
			System.out.println("--------\n" + cause.getMessage() + "\n--------");
		}

		// error.printStackTrace();
	}

	@Test
	public void catchXmlError() {
		Context ctx = Context.from("test-data/projects/manifest-error-01", GRADLE_EXCEPTION_FILE);

		System.out.println(ctx.directory);

		T3<ModelBuilder<BuildStatus>, ByteArrayOutputStream, ByteArrayOutputStream> t3;

		t3 = ctx.model(BuildStatus.class, "clean", "check");

		BuildStatus buildStatus = t3.a.get();

		assertThat(buildStatus.getStatus()).isNotNull();
		assertThat(buildStatus.getStatus().code()).isEqualTo("ActionError");
		assertThat(buildStatus.getFailedTaskName()).isEqualTo("processDebugResources");

		Error cause = buildStatus.getError();

		assertThat(cause).isNotNull();

		for (; cause != null; cause = cause.getCause()) {
			System.out.println("-------\n" + cause.getMessage() + "\n------");
		}
	}

}