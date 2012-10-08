/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.reportdb;

public class TestInfo {
	String className;

	String name;

	int status;

	String documentation;

	String failCause;

	long startTime = 0;

	long endTime = 0;

	String steps;

	String graphXml;

	String params;

	int count;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getGraphXml() {
		return graphXml;
	}

	public void setGraphXml(String graphXml) {
		this.graphXml = graphXml;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getPackageName() {
		return className;
	}

	public void setPackageName(String className) {
		this.className = className;
	}

	public String getFailCause() {
		return failCause;
	}

	public void setFailCause(String failCause) {
		this.failCause = failCause;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getSteps() {
		return steps;
	}

	public void setSteps(String steps) {
		this.steps = steps;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Test name: " + name);
		buf.append("\n");
		buf.append("Class name: " + className);
		buf.append("\n");
		return buf.toString();
	}
}