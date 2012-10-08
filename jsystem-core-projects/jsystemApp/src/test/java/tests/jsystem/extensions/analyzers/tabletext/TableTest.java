/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.tabletext;

import jsystem.extensions.analyzers.tabletext.Table;
import jsystem.extensions.analyzers.tabletext.TableHeaders;
import junit.framework.SystemTestCase;

public class TableTest extends SystemTestCase{
	String[] header = {"Interface", "Port VLAN ID", "Acceptable", "Filtering", "GVRP", "Priority"};
	String[] headerFail = {"Interface", "Port VLAN IDx", "Acceptable", "Filtering", "GVRP", "Priority"};
    String text =
            "show vlan port all\r\n" +
            "\r\n" +
            "          Port    Acceptable   Ingress             Default\r\n" +
            "Interface VLAN ID Frame Types  Filtering    GVRP   Priority\r\n" +
            "--------- ------- ------------ ----------- ------- --------\r\n" +
            "0/1       1       Admit All    Disable     Disable     0\r\n" +
            "0/2       1       Admit All    Disable     Disable     0\r\n" +
            "0/3       1       Admit All    Disable     Disable     0\r\n" +
            "0/4       1       Admit All    Disable     Disable     0\r\n" +
            "0/5       1       Admit All    Disable     Disable     0\r\n" +
            "0/6       1       Admit All    Disable     Disable     0\r\n" +
            "0/7       1       Admit All    Disable     Disable     0\r\n" +
            "0/8       1       Admit All    Disable     Disable     0\r\n" +
            "0/9       1       Admit All    Disable     Disable     0\r\n" +
            "0/10      1       Admit All    Disable     Disable     0\r\n" +
            "2/1       1       Admit All    Disable     Disable     0\r\n" +
            "2/2       1       Admit All    Disable     Disable     0\r\n" +
            "2/3       1       Admit All    Disable     Disable     0\r\n" +
            "2/4       1       Admit All    Disable     Disable     0\r\n" +
            "2/5       1       Admit All    Disable     Disable     0\r\n" +
            "2/6       1       Admit All    Disable     Disable     0\r\n" +
            "2/7       1       Admit All    Disable     Disable     0\r\n" +
            "2/8       1       Admit All    Disable     Disable     0\r\n" +
            "2/9       1       Admit All    Disable     Disable     0\r\n" +
            "--More-- or (q)uit\r\n" +
            "2/10      1       Admit All    Disable     Disable     0\r\n" +
            "\r\n" +
            "(SEABRIDGE Switching) #";

    Table table;
    public void setUp() throws Exception{
        table = new Table(text);
    }

    public void testHeaders() throws Exception {
        assertEquals("Wrong field index for <Port>", 1, table.getHeaderFieldIndex("Port"));
        assertEquals("Wrong field index for <Interface>", 0, table.getHeaderFieldIndex("Interface"));
        assertEquals("Wrong field index for <Port VLAN ID>", 1, table.getHeaderFieldIndex("Port VLAN ID"));
        assertEquals("Wrong field index for <GVRP>", 4, table.getHeaderFieldIndex("GVRP"));
    }
    public void testCells() throws Exception{
        assertEquals("Cell 0/2 should be Admit All", "Admit All" ,table.getCell(0, 2));
    }
    public void testFindCell() throws Exception{
        assertEquals("Cell 0/2 should be Admit All", "Admit All" ,table.getCell(0, 2));
    }

    public void testGetColumn() throws Exception{
        String[] filteringColumn =table.getColumn(table.getHeaderFieldIndex("Filtering"));
        assertEquals("Field should be : Disable", "Disable", filteringColumn[5] );        
    }
    public void testTableHeaders() throws Exception{
    	TableHeaders tableHeader = new TableHeaders(header);
    	tableHeader.setTestAgainst(text);
    	tableHeader.analyze();
    	assertTrue("Analyze fail unexpectedly", tableHeader.getStatus());
    }

    public void testTableHeadersFail() throws Exception{
    	TableHeaders tableHeader = new TableHeaders(headerFail);
    	tableHeader.setTestAgainst(text);
    	tableHeader.analyze();
    	assertFalse("Analyze fail unexpectedly", tableHeader.getStatus());
    }

}
