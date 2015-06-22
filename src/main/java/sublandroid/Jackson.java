package sublandroid;

import com.fasterxml.jackson.core.*;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.*;

public abstract class Jackson {

	public static final JsonFactory FACTORY = new JsonFactory();

	static {
		FACTORY.disable(AUTO_CLOSE_TARGET);
	}

}