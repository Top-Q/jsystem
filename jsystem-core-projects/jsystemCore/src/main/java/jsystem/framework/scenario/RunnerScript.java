/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import jsystem.framework.scripts.ScriptExecutor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Used to identfy script tests
 * 
 * @author guy.arieli
 * 
 */
public class RunnerScript extends RunnerTest {
	protected ScriptExecutor executor;

	public RunnerScript(ScriptExecutor executor) {
		super(executor.getClass().getName(), "execute");
		this.executor = executor;
	}

	public ScriptExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ScriptExecutor executor) {
		this.executor = executor;
	}

	public String getTestName() {
		return executor.getTestName();
	}

	/**
	 * Add the SCRIPT_TAG and the SCRIPT_PATH
	 */
	public void addPrivateTags(Document doc, Element jsystem) {
		Element p = doc.createElement("sysproperty");
		p.setAttribute("key", RunningProperties.SCRIPT_TAG);
		p.setAttribute("value", executor.getTagName());
		jsystem.appendChild(p);

		Element p1 = doc.createElement("sysproperty");
		p1.setAttribute("key", RunningProperties.SCRIPT_PATH);
		p1.setAttribute("value", executor.getFilePath());
		jsystem.appendChild(p1);
	}

	protected void loadParametersFromClass() {
		executor.initParamsFromFile();
		parameters = executor.getParameters();
	}
	public void loadParametersAndValues() {
		if (parameters != null) {
			return;
		}
		loadParametersFromClass();
	}
	
	public RunnerScript cloneTest() throws Exception {
		RunnerScript test = new RunnerScript(executor);
		return test;
	}

}
