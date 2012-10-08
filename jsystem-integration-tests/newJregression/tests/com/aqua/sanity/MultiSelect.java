package com.aqua.sanity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class MultiSelect extends JSysTestCase4UseExistingServer {

	private String[] multiSelect = {"one"};
	private String[] myParam;
	
	public MultiSelect(){
		super();
	}
	
	@Before
	public void setUp()throws Exception{
		super.setUp();
	}
	
	@Test
	public void testSimpleMultiSelect()throws Exception{
		for(String s : multiSelect){
			report.report("the string is: "+s);
		}
	}
	
	@Test
	public void testJustTest()throws Exception{
		
	}
	
//	public String[] getMultiSelectOptionsOptions()throws Exception{
//		return new String[]{"one","two","three"};
//	}
	
	public void setMultiSelectOptions(String[] options)throws Exception{
		this.multiSelect = options;
	}
	


	public String[] getMyParam() {
		return myParam;
	}

	public void setMyParam(String[] myParam) {
		this.myParam = myParam;
	}
	
	public String[] getMyParamOptions()throws Exception{
		return new String[]{"aaa","bbb","ccc"};
	}

	public String[] getMultiSelectOptions()throws Exception{
		return this.multiSelect;
	}
	
	@After
	public void tearDown()throws Exception{
		super.tearDown();
	}
}
