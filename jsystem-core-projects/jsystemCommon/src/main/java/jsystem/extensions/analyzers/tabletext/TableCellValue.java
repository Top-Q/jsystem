/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * TableCellValue test that a spesific cell value in a table is as expected.
 * To define the table cell 3 parameters are used:
 * 1. The table header of the key cell (keyHeader).
 * 2. The key value (keyFieldValue).
 * 3. The table header of the value cell (valueHeader).
 * 
 * for example in the following table I would like to check that port 0/2 is 'Enable' so, 
 * the keyHeader will be 'Port', the keyFieldValue will be '0/2' and the valueHeader will be 'Status'.
 * The expected value will be 'Enable'.
 * 
 * 
 * Port   Status  
 * ------ -------- 
 * 0/1    Disable
 * 0/2    Enable
 * 
 * 
 *  
 * @author guy.arieli
 *
 */
public class TableCellValue extends AnalyzerParameterImpl{
    String keyHeader;
    String keyFieldValue;
    String valueHeader;
    String expectedValue;
    int numOfRow = -1;
    boolean keySensitive = true;
    protected TTable table;
    private int keyHeaderIndex = -1;
    
    public TableCellValue(String keyHeader, String keyFieldValue, String valueHeader, String expectedValue, boolean keySensitive) throws Exception {
        this.keyHeader = keyHeader;
        this.keyFieldValue = keyFieldValue;
        this.valueHeader = valueHeader;
        this.expectedValue = expectedValue;
        this.keySensitive = keySensitive;
    }
    
    public TableCellValue(int keyHeaderIndex, String keyFieldValue, String valueHeader, String expectedValue, boolean keySensitive) throws Exception {
    	this.keyHeaderIndex = keyHeaderIndex;
        this.keyFieldValue = keyFieldValue;
        this.valueHeader = valueHeader;
        this.expectedValue = expectedValue;
        this.keySensitive = keySensitive;
    	
    }
    
    public TableCellValue(String keyHeader, String keyFieldValue, String valueHeader, String expectedValue) throws Exception {
    	this(keyHeader,keyFieldValue,valueHeader,expectedValue, true);
    }
    
    public TableCellValue(int numOfRow, String keyHeader, String keyFieldValue, String valueHeader, String expectedValue) throws Exception {
    	this(keyHeader,keyFieldValue,valueHeader,expectedValue, true);
    	this.numOfRow = numOfRow;
    }
     


   public void analyze() {
        title = "TableCellValue: KeyHeader:  " + keyHeader + " keyFieldValue: " + keyFieldValue + " valueHeader: " + valueHeader + " expectedValue: " + expectedValue;
        StringBuffer sb = new StringBuffer(
                "KeyHeader:  " + keyHeader +
                "\nkeyFieldValue: " + keyFieldValue +
                "\nvalueHeader: " + valueHeader +
                "\nexpectedValue: " + expectedValue + "\n");
        int rowNumber = 0;
        try {
        	
        	if(testAgainst instanceof TableRepository) {
        		table = ((TableRepository)testAgainst).getTable();
        	} else {
                table = new Table((String)testAgainst);
        	}
        } catch (Exception e) {
            sb.append("\r\nUnable to init table: " + e.getMessage());
            sb.append(table.getTableString());
            message = sb.toString();
            status = false;
            return;
        }
        try {
        	if(keyHeaderIndex == -1)
        		rowNumber = table.getFirstRowIndex(keyHeader, keyFieldValue);
        	else
        		rowNumber = table.getFirstRowIndex(keyHeaderIndex, keyFieldValue);
        } catch (Exception e) {
            sb.append("\r\nUnable to find key row: " + e.getMessage());
            sb.append(table.getTableString());
            message = sb.toString();
            status = false;
            return;
        }
        int columnNumber = 0;
        try {
            columnNumber = table.getHeaderFieldIndex(valueHeader);
        } catch (Exception e) {
            sb.append("\r\nUnable to header field: " + e.getMessage());
            sb.append(table.getTableString());
            message = sb.toString();
            status = false;
            return;
        }
       
    	/**
    	 * If the table is without keyheader then , we will iterate over all the values of the first column.
         * Else it will look only one time for the requested value .
    	 */

        if ((table.isRealKeyHeader(keyHeader, testAgainst)) || (numOfRow != -1)) {
        	if (numOfRow != -1)
        		rowNumber = numOfRow;
            String actualCell = table.getCell(rowNumber, columnNumber);
            sb.append("Actual: " + actualCell + "\n");
            sb.append(table.getTableString());
            message = sb.toString();
            if(keySensitive){
                status = actualCell.matches(expectedValue);
            } else {
            	status = actualCell.toLowerCase().matches(expectedValue.toLowerCase());
            }
             if (status == true){
            	 return;
             }
    	}	
    	else{
    		 int keyHeaderColumnNumber = 0;
    	        try {
    	        	keyHeaderColumnNumber = table.getHeaderFieldIndex(keyHeader);
    	        } catch (Exception e) {
    	            sb.append("\r\nUnable to header field: " + e.getMessage());
    	            sb.append(table.getTableString());
    	            message = sb.toString();
    	            status = false;
    	            return;
    	        }
    		rowNumber = 0;
    		 for (int i = 0; i < table.getNumberOfRows(); i++, rowNumber++)
    	        {	 
    			 		 String actualKeyField = table.getCell(rowNumber, keyHeaderColumnNumber);
    		             String actualCell = table.getCell(rowNumber, columnNumber);
    		             sb.append("Actual: " + actualCell + "\n");
    		             sb.append(table.getTableString());
    		             message = sb.toString();
    		             if(keySensitive){
    		            	 if (actualCell.matches(expectedValue))
    		            		 status = actualKeyField.matches(keyFieldValue);
    		             } else {
    		            	 if (actualCell.matches(expectedValue.toLowerCase()))
    		            		 status = actualKeyField.matches(keyFieldValue.toLowerCase());
    		             }
    		             if (status == true){
    		            	 return;
    		             }
    	        }
    	}
        
       
    }
}
