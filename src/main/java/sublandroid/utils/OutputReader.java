package sublandroid.utils;

import sublandroid.command.Command.Invocation;
import sublandroid.messages.*;

import java.util.*;

public abstract class OutputReader<I extends Invocation> {

	/**
	 * Current Gradle invocation result
	 */
	protected I invocation = null;

	/**
	 * Define current gradle invocation
	 */
	public boolean apply(I invocation) {
		this.invocation = invocation;
		return onApply();
	}

	/**
	 * Read output error line from invocation
	 */
	public abstract void errorLine(int number, String line);

	/**
	 */
	public abstract boolean hasError();

	/**
	 * 
	 */
	public abstract List<MHighlight> lastHighlights();

	/**
	 * On apply
	 */
	public abstract boolean onApply();
}