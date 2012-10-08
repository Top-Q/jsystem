package com.aqua.testConstructs;


//@TestBeanClass(include={"testString"},model=TestDataModel.class)
public class StringBean {

	private String testString="test string";
	public StringBean() {
		// TODO Auto-generated constructor stub
	}
	
	public String getTestString() {
		return testString;
	}
	public void setTestString(String testString) {
		this.testString = testString;
	}
	
	
}
