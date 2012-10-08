package com.aqua.jsystemobjects;

public enum ClientHandlerType {
	
	APPLICATION("application"),
	SCENARIO("scenario"),
	TESTS_TREE("testTree"),
	REMOTEINFO("remoteinfo"),
	REPORTER("reporter");
	
	private String type;
	private ClientHandlerType(String type){
		this.type = type;
	}
	
	public String getType(){
		return type;
	}
}
