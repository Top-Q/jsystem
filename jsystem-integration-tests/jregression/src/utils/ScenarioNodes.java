package utils;

public enum ScenarioNodes {
	
	TARGET("target"),
	ANT("jsystem-ant"),
	TEST("test"),
	ANTFILE("antfile"),
	JSYSTEM("jsystem"),
	ANTCALL("antcallback");
	
	private String name;
	private ScenarioNodes(String nodeName){
		this.name = nodeName;
	}
	
	public String getName(){
		return name;
	}
}
