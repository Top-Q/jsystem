/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

/**
 * @deprecated
 * @see jsystem.extensions.analyzers.text.FindText
 */
public class IsTextFound extends FindText {
	
	boolean isFound = false;

	/**
	 * @see jsystem.extensions.analyzers.text.FindText#FindText(String, boolean)
	 */
	public IsTextFound(String toFind, boolean isRegExp) {
		super(toFind, isRegExp);
	}

	public void analyze() {
		super.analyze();
		isFound = status;
	}

	public boolean isFound() {
		return isFound;
	}

}
