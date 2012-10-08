/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

/**
 * In order to use the table analyzer this base table should be the base class.
 * On init you should supply the headers and the cells of the table.
 * To use it you use the <code>BasicTableRepository</code> has the testAgainstObject.
 * 
 * @author guyarieli
 *
 */
public class BaseTable extends TableBasic {
	public BaseTable(String[] header1, String[] header2, String[][] cells){
		this.header1 = header1;
		this.header2 = header2;
		this.cells = cells;
		if(cells != null && cells.length > 0){
			numberOfFields = cells[0].length;
		} else {
			numberOfFields = 0;
		}
	}
	@Override
	public int getHeaderFieldIndex(String fieldName) throws Exception {
		for (int i = 0; i < numberOfFields; i++) {
			if (fieldName.equals(header1[i])) {
				return i;
			}
			if (fieldName.equals(header2[i])) {
				return i;
			}
			if (fieldName.equals(header1[i] + " " + header2[i])) {
				return i;
			}
			if (fieldName.equals(header1[i] + "/" + header2[i])) {
				return i;
			}
			if (fieldName.equals(header1[i].replace('/', ' ') + header2[i])) {
				return i;
			}
		}
		throw new Exception("Header field: " + fieldName + " wasn't found\r\n");
	}

	@Override
	protected void initHeaders() throws Exception {

	}

	public void initTable(String tableString) throws Exception {

	}

}
