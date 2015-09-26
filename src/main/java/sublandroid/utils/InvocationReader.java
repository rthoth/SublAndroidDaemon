package sublandroid.utils;

import java.util.*;
import java.io.*;

import sublandroid.Log;
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

		if (reader.apply(invocation))
			outputReaders.add(reader);

		for (OutputReader outputReader : readers) {
			if (outputReader.apply(invocation))
				outputReaders.add(outputReader);
		}

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(invocation.getErr())
		);

		String line;

		try {
			line = bufferedReader.readLine();
		} catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		List<MHighlight> currentHighlights;

		for (OutputReader outputReader : readers) {
			if (outputReader.hasError()) {
				currentHighlights = outputReader.lastHighlights();
				if (currentHighlights != null)
					highlights.addAll(currentHighlights);
			}
		}
		for (int number = 1; line != null; number++) {
			for (OutputReader outputReader : outputReaders)

				try {

					outputReader.errorLine(number, line);
					if (outputReader.hasError()) {
						currentHighlights = outputReader.lastHighlights();
						if (currentHighlights != null)
							highlights.addAll(currentHighlights);
					}

				} catch (Throwable throwable) {
					throw new InvocationReaderException(
						String.format("OutputReader %s failed at %d", outputReader, number),
						throwable);
				}

			try {
				line = bufferedReader.readLine();
			} catch (IOException ioException) {
				throw new InvocationReaderException("Error in line " + (number + 1), ioException);
			}
		}

		return highlights;
	}


	public static class InvocationReaderException extends RuntimeException {

		public InvocationReaderException(String message, Throwable cause) {
			super(message, cause);
		}
	}

}