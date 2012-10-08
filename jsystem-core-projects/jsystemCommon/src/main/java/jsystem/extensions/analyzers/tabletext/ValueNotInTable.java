/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

/**
 * The that the value is not found in the specified table column. The oposit to
 * ValueInTable class.
 * 
 * @author guy.arieli
 * 
 */
public class ValueNotInTable extends ValueInTable {
	public ValueNotInTable(String headerName, String value) throws Exception {
		super(headerName, value);
	}

	public ValueNotInTable(String headerName, String value, boolean isRegExp) throws Exception {
		super(headerName, value, isRegExp);
	}

	public void analyze() {
		super.analyze();
		title = "ValueNotInTable: Header name:  " + headerName + " value: " + value;
		status = !status;

	}

}
