/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.tabletext;

import jsystem.extensions.analyzers.tabletext.BaseTableRepository;
import junit.framework.SystemTestCase;
import jsystem.extensions.analyzers.tabletext.TableCellValue;

/**
 * @author Shay Rubin
 * Here is an example of how to analyze a table(which result from a CLI command) by extracting its
 * header and cells,
 * For the header we use an array of strings called header.
 * For the cells we use a two dimensional array of strings.
 * after the extracting we use the command: 
 *  setTestAgainsObject(new BaseTableRepository(header,header,cells)
 *  Now we can use Aqua analyzers 
 */
public class BaseTableRepositoryTest extends SystemTestCase {

	public void test_Analyze() throws Exception {
	String [][] cells;
	String [] header = null;
	String [] columns;
	int countLines =0;
	
	String tableString = "root>>showUlConn\n"+  // showUlConn - the command of the cli 
	"Ul connection table:\n"+    // part of the command result                                               
		"drvIdx | fwIdx | cid   | type          | Use   | Active | capacity\n"+ // the header of the table
		" 0    | 0     | 16    | Basic         | 1     | 1      | 128\n"+
		" 1    | 1     | 528   | Primary       | 1     | 1      | 128\n"+
		" 3    | 3     | 1552  | Traffic       | 1     | 1      | 128\n";
	
//	Reaching to the start of the header 
	int idx = tableString.indexOf("drvIdx");
	
//	Taking the table from the header beginning
	tableString = tableString.substring(idx);
	
//	Splitting the table according to the new lines
	String [] lines = tableString.split("[\\n\\r]");
	
//	Splitting the header by char |
	header = lines[0].split("\\|");
	
	for(int i= 0 ;i < header.length;i++){
//	Truncate the spaces of each header cell
		header[i]=header[i].trim();
	}
	
	for(int i= 1 ;i < lines.length ;i++){
		 if(lines[i].trim().equals("")){
             continue;
		  }	
//		 counting the lines of the table without unnecessary space lines
		 countLines++; 
	}		
//	Allocation of the Two dimensional array of cells according to table's lines and headr length
	cells = new String[countLines][header.length];
	for(int i= 1 ;i < lines.length ; i++){
		 if(lines[i].trim().equals("")){
            continue;
		  } 	
		 columns = lines[i].trim().split("\\|");
		 for(int j =0 ; j < columns.length ; j++)
		 {
//			 Truncate the cell from spaces 
			 columns[j] = columns[j].trim();
//			 Adding the right cell to it's right allocation 
			 cells[i-1][j]=columns[j];
		 }
	}
    TableCellValue tableCellValue = new TableCellValue("drvIdx", "0", "cid", "16");
    tableCellValue.setTestAgainst(new BaseTableRepository(header,header,cells));// the important line
    tableCellValue.analyze();
    assertTrue("Cid should be true", tableCellValue.getStatus());
	}
}
