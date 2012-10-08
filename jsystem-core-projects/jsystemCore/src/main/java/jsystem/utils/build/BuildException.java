/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.build;

public class BuildException extends Exception {

	private static final long serialVersionUID = -2643824443556373540L;

	public BuildException() {
		super();
	}

	public BuildException(String message) {
		super(message);
	}

	String antFailString = null;

	public String getAntFailString() {
		return antFailString;
	}

	public void setAntFailString(String antFailString) {
		this.antFailString = antFailString;
	}
}
