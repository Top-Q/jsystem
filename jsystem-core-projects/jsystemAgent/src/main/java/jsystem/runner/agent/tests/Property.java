package jsystem.runner.agent.tests;

public class Property {
	
	private String key;
	private String value;

	public String toString() {
		return key + "=" + value + "/SEP/";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
