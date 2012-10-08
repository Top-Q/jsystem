/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.io.File;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Michael Oziransky
 */
public class RunnerCmd {
	private String alias;
	private String projectPath;
	private String sutFile;
	private String scenarioFile;
	private boolean saveRunProperties;
	private Date schedule;
	private int repetition;
	private boolean dependOnPrevious;
	private boolean freezeOnFail;
	private boolean stopSuiteExecution;
	private boolean stopEntireExecution;
	
	public RunnerCmd() {
		repetition = 0;
		dependOnPrevious = false;
		saveRunProperties = false;
		freezeOnFail = false;
		schedule = null;
		alias = "";
	}
	
	public String toString() {
		return alias;
	}
	
	public String getProjectPath() {
		return projectPath;
	}
	
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	
	public String getSutFile() {
		return sutFile;
	}
	
	public String getSutFullPath() {
		File file = new File(projectPath + "/" + sutFile);
		return file.getAbsolutePath();
	}
	
	public String getSutName() {
		return (new File(sutFile).getName());
	}
	
	public void setSutFile(String sutFile) {
		this.sutFile = sutFile;
	}
	
	public String getScenarioFile() {
		return scenarioFile;
	}

	public String getScenarioName() {
		return scenarioFile.split("\\.")[0];
	}
	
	public void setScenarioFile(String scenarioFile) {
		this.scenarioFile = scenarioFile;
	}
	
	public boolean isSaveRunProperties() {
		return saveRunProperties;
	}
	
	public void setSaveRunProperties(boolean saveRunProperties) {
		this.saveRunProperties = saveRunProperties;
	}
	
	public Date getSchedule() {
		return schedule;
	}
	
	public void setSchedule(Date schedule) {
		this.schedule = schedule;
	}
	
	public int getRepetition() {
		return repetition;
	}
	
	public void setRepetition(int repetition) {
		this.repetition = repetition;
	}
	
	public boolean isDependOnPrevious() {
		return dependOnPrevious;
	}

	public void setDependOnPrevious(boolean dependOnPrevious) {
		this.dependOnPrevious = dependOnPrevious;
	}

	public boolean isFreezeOnFail() {
		return freezeOnFail;
	}

	public void setFreezeOnFail(boolean freezeOnFail) {
		this.freezeOnFail = freezeOnFail;
	}

	public boolean isStopSuiteExecution() {
		return stopSuiteExecution;
	}

	public void setStopSuiteExecution(boolean stopExecution) {
		this.stopSuiteExecution = stopExecution;
	}
	
	public boolean isStopEntireExecution() {
		return stopEntireExecution;
	}

	public void setStopEntireExecution(boolean stopEntireExecution) {
		this.stopEntireExecution = stopEntireExecution;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void toElement(Element createElement, Document doc) {
		createElement.setAttribute("repetitions", Integer.toString(repetition));
		createElement.setAttribute("saveRunProperties", Boolean.toString(saveRunProperties));
		createElement.setAttribute("dependOnPrevious", Boolean.toString(dependOnPrevious));
		createElement.setAttribute("freezeOnFail", Boolean.toString(freezeOnFail));
		createElement.setAttribute("stopSuiteExecution", Boolean.toString(stopSuiteExecution));
		createElement.setAttribute("stopEntireExecution", Boolean.toString(stopEntireExecution));
		createElement.setAttribute("alias", alias);
		Element projPathElement = doc.createElement("projectPath");
		projPathElement.setTextContent(projectPath);
		createElement.appendChild(projPathElement);
		Element sutFileElement = doc.createElement("sutFile");
		sutFileElement.setTextContent(sutFile);
		createElement.appendChild(sutFileElement);
		Element scenarioNameElement = doc.createElement("scenarioName");
		scenarioNameElement.setTextContent(scenarioFile);
		createElement.appendChild(scenarioNameElement);
	}
}