/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.tabletext;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * The same as TableCellValue but is able to check multi celles. 
 * @author guy.arieli
 * Example:
 * For this table:
 * Index    Source IP                Destination IP             
	18        2.1.1.1                  1.1.1.1     
	Use:
 * new TableMultiCellValue("Index", 18,	new String[] {"Source IP", "Destination IP"}, new String[] {"1.1.1.1", "2.2.2.2"})
 *
 */
public class TableMultiCellValue extends AnalyzerParameterImpl{
    String keyHeader;
    String[] valueHeaders;
    String keyFieldValue;
    String[] expectedValues;
    protected TTable table;

    public TableMultiCellValue(String keyHeader, String keyFieldValue, String[] valueHeaders, String[] expectedValues) throws Exception {
        this.keyHeader = keyHeader;
        this.valueHeaders = valueHeaders;
        this.keyFieldValue = keyFieldValue;
        this.expectedValues = expectedValues;
    }
    public void analyze() {
        title = "TableMultiCellValue";
        if (valueHeaders.length != expectedValues.length){
            message = "TableMultiCellValue: keys and expected arrays should be in the same size";
            status = false;
            return;
        }
        try {
        	
        	if(testAgainst instanceof TableRepository) {
        		table = ((TableRepository)testAgainst).getTable();
        	} else {
                table = new Table((String)testAgainst);
        	}
        } catch (Exception e) {
        	message = "\r\nUnable to init table: " + e.getMessage() + (table.getTableString());
            status = false;
            return;
        }
        if (table.isRealKeyHeader(keyHeader, testAgainst)){
	        for (int i = 0; i <valueHeaders.length; i++){
	            try {
	            	TableCellValue tcv = new TableCellValue(keyHeader, keyFieldValue, valueHeaders[i], expectedValues[i]);
	            	tcv.setTestAgainst(testAgainst);
	                tcv.analyze();
	                if (!tcv.getStatus()){
	                    message = "fail to find the expected value: " + expectedValues[i] + " line: " + i;
	                    status = false;
	                    return;
	                 }
	            }catch (Exception e){
	                throwable = e;
	                status = false;
	                return;
	            }
	            status = true;
	        }
        }else{
            for (int i = 0; i < table.getNumberOfRows(); i++) {
            	for (int j = 0; j <valueHeaders.length; j++){
            		try {
            			TableCellValue tcv = new TableCellValue(i, keyHeader, keyFieldValue, valueHeaders[j], expectedValues[j]);
		                tcv.setTestAgainst(testAgainst);
		                tcv.analyze();
		                if (!tcv.getStatus()){
		                    message = "fail to find the expected value: " + expectedValues[j] + " line: " + j;
		                    status = false;
		                    break;
		                }
		            }catch (Exception e){
		                throwable = e;
		                status = false;
		                return;
		            }
		            status = true;
            	}
            	if (status) {
					return;
				}
            }
        }
    }
}