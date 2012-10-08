package com.aqua;

import junit.framework.SystemTestCase;
/**
 */
public class MultiSelectParameterExample extends SystemTestCase {	
	
	private String[] multiSelect = {"option1"};
	private String[] testAnother = {"dan"};
	
	public void testVerifyFolderCreateTime() throws Exception{
		for (String s:multiSelect){
			report.report("Val is " + s);
		}
	}
	
	public void testVerifyFolderCreateTimeTest() throws Exception{
		for (String s:testAnother){
			report.report("Val is " + s);
		}
	}
	
	
	public String[] getTestArrayWithOptionsOptions(){
		return new String[]{"dan","golan","nizan"};
	}
	public String[] getTestArrayWithOptions(){
		return testAnother;
	}
	public void setTestArrayWithOptions(String[] testArrayWithOptions){
		testAnother = testArrayWithOptions;
	}
	
	public String[] getStringArrayWithOptionsOptions() {
		return new String[]{"option1","option2","option3"};
	}
	public String[] getStringArrayWithOptions() {
		return multiSelect;
	}
	public void setStringArrayWithOptions(String[] stringArrayWithOptions) {
		this.multiSelect = stringArrayWithOptions;
	}
}


