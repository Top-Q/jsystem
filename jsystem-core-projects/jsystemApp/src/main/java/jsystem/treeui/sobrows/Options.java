/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

public class Options {
	public enum Access {
		PRIVATE, NO, PROTECTED, PUBLIC;
	}

	public static String getAccessString(Access access) {
		switch (access) {
		case PRIVATE:
			return "private ";
		case PROTECTED:
			return "protected ";
		case PUBLIC:
			return "public ";
		default:
			return "";
		}
	}

}
