/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

public class BaseTableRepository implements TableRepository {
	private TTable table;
	public BaseTableRepository (String[] header1, String[] header2, String[][] cells){
		table = new BaseTable(header1, header2, cells);
	}
	public TTable getTable() throws Exception {
		return table;
	}

}
