/*
 * Created on 16/05/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests;

import java.io.File;
import java.util.Random;

import systemobject.tests.Device1;

import com.aqua.excel.ExcelFile;

import jsystem.framework.analyzer.AnalyzerParameterImpl;
import jsystem.framework.report.Summary;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;


public class MyTest extends SystemTestCase implements DIR {
	public void testSummary() {
		Summary.getInstance().setTempProperty("xxx", "yyy");
		System.out.println(Summary.getInstance().getProperties().toString());
	}

	public void testInternalTest() throws Exception {
		for (int i = 0; i < 10; i++) {
			report.startReport("JustATest" + i, null);
			if (i == 2 || i == 5) {
				report.report("this one failed", null, false);
			}
			Thread.sleep(1000);
			report.report("Hello");
			report.endReport();
		}
	}

	public void testBigFileZip() {
		byte[] file = new byte[2000000];
		for (int i = 0; i < file.length; i++) {
			file[i] = 34;
		}
		report.saveFile("test.cap", file);
	}

	public void testFail() throws Exception {
		throw new Exception("fail");
	}

	class MyAna extends AnalyzerParameterImpl {

		public void analyze() {
			title = "ddd";
			status = false;
		}

	}

	public void testAnalyze() throws Exception {
		Device1 d = (Device1) system.getSystemObject("device1");
		d.setTestAgainstObject("ddd");
		d.analyze(new MyAna(), false, false);
	}

	public void testSteps() {
		report.report("!DOCTYPE HTML<h2>my big message</h2>");
	}
	
	public void testRandomTest() throws Exception{
		Random rand = new Random(System.currentTimeMillis());
		if(rand.nextBoolean()){
			report.report("Test failed", null, false);
			throw new Exception("this one failed");
		}
	}
	
	public void testLinkCapFile() throws Exception{
		
		report.saveFile("ddd.cap", FileUtils.readBytes(new File("c:\\browsToJsystem.cap")));
		report.addLink("cap file", "ddd.cap");
	}
	
	public void testUnicode(){
		report.report("hello world ����", "��� ���", true);
	}
	
	public void testExcle() throws Exception{
		ExcelFile excel = ExcelFile.getInstance("MyExcel.xls", true);
		excel.addHeader(new String[] {"Packet size", "Rate"});
		excel.addRow(new String[]{"3", "4"});
		excel.addRow(new String[]{"3", "4"});
		report.addLink("my excel", "MyExcel.xls");
	}
}
