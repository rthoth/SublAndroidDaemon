package sublandroid;

import sublandroid.messages.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;

public abstract class Util {

	public static ObjectMapper OBJECT_MAPPER = new ObjectMapper(Jackson.FACTORY);

	public static void send(MCommand message, Writer writer) throws IOException {
		OBJECT_MAPPER.writeValue(writer, message);
		writer.write('\n');
		writer.flush();
	}

	public static <T> T read(BufferedReader reader, Class<T> clazz) throws IOException {
		return OBJECT_MAPPER.readValue(reader.readLine(), clazz);
	}

}