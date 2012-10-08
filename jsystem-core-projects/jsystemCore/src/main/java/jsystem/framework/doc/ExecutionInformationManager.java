/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.doc;

import java.util.ArrayList;

import jsystem.extensions.report.html.summary.Table;
import jsystem.framework.report.ListenerstManager;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * Support documentation summary model. It enable system objects and tests
 * to add information that will be presented in the end of the test and will
 * show additional layer of information regarding the test execution.
 * This layer of information can be used to add manual execution instructions and
 * other objects information.
 * <p>
 * To use it do the following:<br>
 * <code>ExecutionInformationManager.getInstance().addInformation(getName(), "Launch a telnet session");<code>
 * @author guy.arieli
 *
 */
public class ExecutionInformationManager implements TestListener{
	private static ExecutionInformationManager execInfoManager =null;
	
	/**
	 * Singleton method that return an instance of the execution information manager.
	 * @return singleton object.
	 */
	public static ExecutionInformationManager getInstance(){
		if(execInfoManager == null){
			execInfoManager = new ExecutionInformationManager();
			ListenerstManager.getInstance().addListener(execInfoManager);
			execInfoManager.startTest(null);
		}
		return execInfoManager;
	}
	/**
	 * Hold all the steps information
	 */
	private ArrayList<InformationElement>steps = new ArrayList<InformationElement>();
	
	/**
	 * Will be set to true if the current test is using this module.
	 */
	private boolean informationAdded = false;
	private ExecutionInformationManager(){
		//
	}
	
	/**
	 * Add the information that will be used
	 * @param source the source of the information (like the system object name).
	 * @param description the step description.
	 */
	public void addInformation(String source, String description){
		informationAdded = true;
		steps.add(new InformationElement(source, description, System.currentTimeMillis()));
	}
	public void addError(Test test, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	public void addFailure(Test test, AssertionFailedError t) {
		// TODO Auto-generated method stub
		
	}

	public void endTest(Test test) {
		/*
		 * If an information was added to the test
		 * will generate the information table and will add it as a link.
		 */
		if(informationAdded){
			String[][] table = new String[steps.size() + 1][];
			long testStartTime = steps.get(0).getTime();
			table[0] = new String[] {"Time (sec)", "Source", "Description"};
			for(int i = 0; i < steps.size(); i++){
				InformationElement element = steps.get(i);
				table[i+1] = new String[]{Long.toString((element.getTime() - testStartTime)/ 1000), element.getSource(), element.getDescription()};
			}
			Table t = new Table(table);
			ListenerstManager.getInstance().reportHtml("Test information", t.toString(), true);
		}
		informationAdded = false;
	}

	public void startTest(Test test) {
		/*
		 * Add the test start element
		 */
		steps.add(new InformationElement(null, "Test started", System.currentTimeMillis()));
		informationAdded = false;
	}
	
	
}

class InformationElement {
	String source;
	String description;
	long time;
	public InformationElement(String source, String description, long time){
		this.source = source;
		this.description = description;
		this.time = time;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}
