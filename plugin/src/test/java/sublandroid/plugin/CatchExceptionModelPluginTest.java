package sublandroid.plugin;

import java.io.*;

import sublandroid.core.*;

import org.gradle.tooling.*;

import org.testng.annotations.*;

import static sublandroid.plugin.TestHelpers.*;
import static org.assertj.core.api.Assertions.*;

public class CatchExceptionModelPluginTest {

	@Test
	public void catchJavaError() {

		Context ctx = Context.from("test-data/projects/java-error-01", GRADLE_EXCEPTION_FILE);

		T3<ModelBuilder<CatchExceptionModel>, ByteArrayOutputStream, ByteArrayOutputStream> t3;

		t3 = ctx.model(CatchExceptionModel.class, "check");

		CatchExceptionModel catchException = t3.a.get();

		assertThat(catchException.getStatus().isValidationError()).isTrue();
		assertThat(catchException.getFailedTaskName()).isEqualTo("processReleaseResources");
	}

}