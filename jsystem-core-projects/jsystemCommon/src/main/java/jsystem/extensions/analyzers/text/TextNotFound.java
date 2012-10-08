/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

/**
 * Verify that a text is not found. Support regular expression.
 */
public class TextNotFound extends FindText {

	/**
	 * @see jsystem.extensions.analyzers.text.FindText#FindText(String)
	 */
	public TextNotFound(String notFound) {
		super(notFound);
	}

	/**
	 * @see jsystem.extensions.analyzers.text.FindText#FindText(String, boolean)
	 */
	public TextNotFound(String notFound, boolean isRegExp) {
		super(notFound, isRegExp);
	}

	/**
	 * @see jsystem.extensions.analyzers.text.FindText#FindText(String, boolean, boolean)
	 */
	public TextNotFound(String toFind, boolean isRegExp, boolean caseSensitive) {
		super(toFind, isRegExp, caseSensitive);
	}
	
	/**
	 * @see jsystem.extensions.analyzers.text.FindText#FindText(String, boolean, boolean, int)
	 */
	public TextNotFound(String toFind, boolean isRegExp, boolean caseSensitive, int group) {
		super(toFind, isRegExp, caseSensitive, group);
	}
	
	public void analyze() {
		super.analyze();
		status = !status;
	}
	
}
