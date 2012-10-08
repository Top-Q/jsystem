package com.aqua.jsystemobjects;

public enum TestType {
	SCENARIO("scenario"),
	RANDOM("random"),
	SCRIPT("script");
	
	private String type = "";
	private TestType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
}
