/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

/**
 * This enum describe the data type of each jsystem property.
 * The data type is used to load a compatible editor to edit the property on the jsystem dialog.
 * 
 * @author Dror 
 */
public enum DataType {
	DIRECTORY,
	FILE,
	LIST,
	MULTY_SELECT_LIST,
	SEARCH_AND_LIST,
	SEARCH_AND_MULTY_SELECT_LIST,
	TEXT,
	PASSWORD,
	NUMERIC,
	EMAIL,
	MAIL_LIST,
	BOOLEAN,
}
