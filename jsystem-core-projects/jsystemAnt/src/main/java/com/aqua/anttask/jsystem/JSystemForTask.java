/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import java.util.Properties;

import jsystem.utils.StringUtils;
import net.sf.antcontrib.logic.ForTask;

import org.apache.tools.ant.BuildException;

/**
 * A wrapper for the Ant for to allow replacing of reference parameters
 * 
 * @author Nizan
 *
 */
public class JSystemForTask extends ForTask {

	String uuid;
	String scenarioString;

	public void setFullUuid(String uuid){
		this.uuid = uuid;
	}

	public void setParentName(String name){
		if (name.startsWith(".")){
			name = name.substring(1);
		}
		scenarioString = name;
	}

	public void execute() throws BuildException{
		
		if (!JSystemAntUtil.doesContainerHaveEnabledTests(uuid)){
			return;
		}
		
		Properties p = JSystemAntUtil.getPropertiesValue(scenarioString, uuid);
		
		String list = JSystemAntUtil.getParameterValue("list", "", p);
		if (!StringUtils.isEmpty(list)){
			setList(list);
		}
		
		String param = JSystemAntUtil.getParameterValue("loop value", "", p);
		if (!StringUtils.isEmpty(param)){
			setParam(param);
		}
		
		super.execute();
	}
}
