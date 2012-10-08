package manualTests;

import jsystem.framework.TestProperties;
import jsystem.framework.graph.Graph;
import junit.framework.SystemTestCase;

import com.aqua.excel.ExcelFile;

public class Sanity1 extends SystemTestCase {

	int Num = 5;

	int num2 = 55;

	int wwwi = 46;

	String wwws = "64";

	String s1 = "test";

	String t1 = "send";

	String s5 = "second s";

	

	
	/**
	 * Execute Num 1. aaa 2. bbb
	 * 
	 * @params.exclude  num2
	 */	
	@TestProperties(name = "Test value Num: ${S1}")
	public void testLongTextReport() {
		String s1 = "";
		for (int i = 0; i <= 100; i++)
			s1 = s1 + i + " ";

		report.step("This is a very long text: " + s1);
	}

	/**
	 * from the eclipse too
	 * fffdddgdgdf
	 */
	@TestProperties(name = "Test with ${num} reports")
	public void testWithAlotOfReports() {
		if (Num<0) report.step("You cannot run " + Num + " reports. Please enter a positive number. 10X");
		for (int i = 1; i <= Num; i++)
			report.step("Report No.: " + i);
	}

	public void testEmptyReport() {
		report.step("");
		report.addProperty("test", "5");
		report.reportHtml("title", "body", true);
	}

	public void _testIllegalTestName() {
		report.step("This is not a test");
	}

	public void testWith2Numbers2() {
		report.step("The name of this test has digits");
	}

	 public void testSimulateSetLinkToExcelAndGraph() throws Exception {
		  ExcelFile excel = ExcelFile.getInstance("excel_check", false);
		  Graph graph = new Graph("graph_temp", "example");
		  
		  excel.addRow(new String[]{"1 ,2, 3,"});
		  graph.add("Red", 50);
		  graph.add("Blue", 50);
		  graph.add("Black", 50);
		  graph.add("Green", 50);
		  excel.addHeader(new String[]{" "});
		  excel.show();
		  graph.show();
		 }

	

	public int getNum() {
		return Num;
	}

	public void setNum(int num) {
		Num = num;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public String getS5() {
		return s5;
	}

	public void setS5(String s5) {
		this.s5 = s5;
	}

	public String getT1() {
		return t1;
	}

	public void setT1(String t1) {
		this.t1 = t1;
	}

	public int getWwwi() {
		return wwwi;
	}

	public void setWwwi(int wwwi) {
		this.wwwi = wwwi;
	}

	public String getWwws() {
		return wwws;
	}

	public void setWwws(String wwws) {
		this.wwws = wwws;
	}


}
