package sublandroid.utils;

import java.util.*;
import java.io.*;

import sublandroid.command.Command.Invocation;
import sublandroid.messages.MHighlight;

public class InvocationReader<I extends Invocation> {

	protected final I invocation;

	public InvocationReader(I invocation) {
		this.invocation = invocation;
	}

	public List<MHighlight> read(final OutputReader reader, OutputReader... readers) {

		LinkedList<MHighlight> highlights = new LinkedList<>();
		ArrayList<OutputReader> outputReaders = new ArrayList<>(readers.length + 1);

		outputReaders.add(reader);

		for (OutputReader outputReader : readers)
			outputReaders.add(outputReader);

		InputStreamReader inputStreamReader = new InputStreamReader(invocation.getErr());
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String line;

		try {
			line = bufferedReader.readLine();
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		for (int lineNumber = 1; line != null; lineNumber++) {
			for (OutputReader outputReader : outputReaders)
				highlights.addAll(outputReader.errorLine(lineNumber, line));

			try {
				line = bufferedReader.readLine();
			} catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		return highlights;
	}

}