/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Sequential;

/**
 * A wrapper for the Ant switch to allow replacing of reference parameters<br>
 * <B>Note: should be updated when Ant contrib jars are updated!</B>
 * 
 * @author Nizan
 *
 */
public class JSystemSwitch {

	private String value;
	private Vector cases;
	private Sequential defaultCase;
	private boolean caseInsensitive;

	String uuid;
	String scenarioString;

	static Logger log = Logger.getLogger(JSystemSwitch.class.getName());


	public void setFullUuid(String uuid){
		this.uuid = uuid;
	}

	public void setParentName(String name){
		if (name.startsWith(".")){
			name = name.substring(1);
		}
		scenarioString = name;
	}

	public void loadParameters(){
		setValue(JSystemAntUtil.getParameterValue(scenarioString, uuid, "Value", value));
		int sz = cases.size();
		for (int i=0;i<sz;i++){
			Case c = (Case)(cases.elementAt(i));
			c.setValue(JSystemAntUtil.getParameterValue(c.scenarioString, c.uuid, "Value", c.value));
		}
	}

	/***
	 * Default Constructor
	 */
	public JSystemSwitch()
	{
		cases = new Vector();
	}

	public void execute()
	throws BuildException
	{
		if (!JSystemAntUtil.doesContainerHaveEnabledTests(uuid)){
			return;
		}
		
		loadParameters();
		log.log(Level.INFO,"Switch on \"" + value + "\"");

		if (value == null)
			throw new BuildException("Value is missing");
		if (cases.size() == 0 && defaultCase == null)
			throw new BuildException("No cases supplied");

		Sequential selectedCase = defaultCase;

		int sz = cases.size();
		for (int i=0;i<sz;i++)
		{
			Case c = (Case)(cases.elementAt(i));

			String cvalue = c.value;
			if (cvalue == null) {
				throw new BuildException("Value is required for case.");
			}
			String mvalue = value;

			if (caseInsensitive)
			{
				cvalue = cvalue.toUpperCase();
				mvalue = mvalue.toUpperCase();
			}

			if (cvalue.equals(mvalue) && c != defaultCase)
				selectedCase = c;
		}

		if (selectedCase == null) {
			throw new BuildException("No case matched the value " + value
					+ " and no default has been specified.");
		}
		selectedCase.perform();
	}

	/***
	 * Sets the value being switched on
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	public void setCaseInsensitive(boolean c)
	{
		caseInsensitive = c;
	}

	public final class Case extends Sequential
	{
		private String value;

		String params;
		String uuid;
		String scenarioString;

		public Case()
		{
			super();
		}

		public void setFullUuid(String uuid){
			this.uuid = uuid;
		}

		public void setParentName(String name){
			if (name.startsWith(".")){
				name = name.substring(1);
			}
			scenarioString = name;
		}

		public void setValue(String value)
		{
			this.value = value;
		}


		public void execute()
		throws BuildException
		{
			super.execute();
		}

		public boolean equals(Object o)
		{
			boolean res = false;
			Case c = (Case)o;
			if (c.value.equals(value))
				res = true;
			return res;                
		}
	}

	/***
	 * Creates the &lt;case&gt; tag
	 */
	public JSystemSwitch.Case createCase()
	throws BuildException
	{
		JSystemSwitch.Case res = new JSystemSwitch.Case();
		cases.addElement(res);
		return res;
	}

	/***
	 * Creates the &lt;default&gt; tag
	 */
	public void addDefault(Sequential res)
	throws BuildException
	{
		if (defaultCase != null)
			throw new BuildException("Cannot specify multiple default cases");

		defaultCase = res;
	}
}
