package sublandroid.utils;

import java.util.*;
import java.io.*;

import sublandroid.command.Invocation;
import sublandroid.messages.MHighlight;



public class InvocationReader<I extends Invocation> {

	protected final I invocation;

	public InvocationReader(I invocation) {
		this.invocation = invocation;
	}


	public List<MHighlight> read(final OutputReader reader, OutputReader... readers) {

		ArrayList<OutputReader> outputReaders = new ArrayList<>(readers.length + 1);

		outputReaders.add(reader);

		for (OutputReader reader : readers)
			outputReaders.add(reader);

		InputStreamReader inputStreamReader = new InputStreamReader(invocation.getStandardErr());
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line = bufferedReader.readLine();
		while (line != null) {
			for (OutputReader reader : outputReaders)
				reader.errorLine(line);
		}
	}

}