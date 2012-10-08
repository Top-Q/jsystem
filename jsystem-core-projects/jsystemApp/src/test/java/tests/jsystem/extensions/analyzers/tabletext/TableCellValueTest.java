/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.extensions.analyzers.tabletext;

import junit.framework.SystemTestCase;
import jsystem.extensions.analyzers.tabletext.TableCellValue;

public class TableCellValueTest extends SystemTestCase{
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

    public void testTableCellValue() throws Exception{
        TableCellValue tableCellValue = new TableCellValue("Interface", "0/1", "Filtering", "Disable");
        tableCellValue.setTestAgainst(text);
        tableCellValue.analyze();
        assertTrue("Status should be true", tableCellValue.getStatus());
         tableCellValue = new TableCellValue("Interface", "0/1", "Filtering", "Enable");
        tableCellValue.setTestAgainst(text);
        tableCellValue.analyze();
        assertFalse("Status should be false", tableCellValue.getStatus());
    }
    
    //In order to run this test right, temporary erase 'interface' key header
    public void testTableCellValueWithHeaderIndex() throws Exception{
    	report.step("table cell value test without key header (only index)");
        TableCellValue tableCellValue = new TableCellValue(0, "0/1", "Filtering", "Disable", true);
        tableCellValue.setTestAgainst(text);
        tableCellValue.analyze();
        assertTrue("Status should be true", tableCellValue.getStatus());
     }
}
