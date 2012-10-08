/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

/**
 * The object that collect all the code and return it as string.
 * 
 * @author guy.arieli
 * 
 */
public class Code {
	/**
	 * The holder of all the code
	 */
	private StringBuffer code = new StringBuffer();

	/**
	 * The current indentation, it's used in add line and can be changed by
	 * shiftRight and shiftLeft methods.
	 */
	int indent = 0;

	/**
	 * Add a line to the code. First add the indentation, the the line and then
	 * enter.
	 * 
	 * @param line
	 *            the line to add
	 */
	public void addLine(String line) {
		for (int i = 0; i < indent; i++) {
			code.append("\t");
		}
		code.append(line);
		code.append("\n");
	}

	/**
	 * Add a string contain more then one line . It's first splited and then
	 * added line by line.
	 * 
	 * @param multiLines
	 *            the multi-lines string
	 */
	public void addMultiLines(String multiLines) {
		if (multiLines == null) {
			return;
		}
		String[] lines = multiLines.split("\n");
		for (int i = 0; i < lines.length; i++) {
			addLine(lines[i]);
		}
	}

	/**
	 * Shift the indentation to the right
	 * 
	 */
	public void shiftRight() {
		indent++;
	}

	/**
	 * Shift the indentation to the left (back).
	 * 
	 */
	public void shiftLeft() {
		indent--;
	}

	/**
	 * Return the all code as string
	 */
	public String toString() {
		return code.toString();
	}
}
