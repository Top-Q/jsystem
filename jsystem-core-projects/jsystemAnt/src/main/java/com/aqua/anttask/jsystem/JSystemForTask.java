/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.anttask.jsystem;

import org.apache.tools.ant.BuildException;

/**
 * A wrapper for the Ant for to allow replacing of reference parameters
 * 
 * @author Nizan Freedman and Itai Agmon
 * 
 */
public class JSystemForTask extends PropertyReaderTask {

	public void execute() throws BuildException {

		if (!JSystemAntUtil.doesContainerHaveEnabledTests(getUuid())) {
			return;
		}
		setList(getParameterFromProperties("list", getList()));
		setParam(getParameterFromProperties("loop value", getParam()));
		super.execute();
	}

}
