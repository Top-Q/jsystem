/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.analyzers;

import jsystem.framework.analyzer.AnalyzerParameterImpl;
import jsystem.utils.StringUtils;

/**
 * Simple analyzer example
 * @author goland
 */
public class SimpleTextFinder extends AnalyzerParameterImpl {
	private String txtToFind;
	int result;
	
	public SimpleTextFinder(String txtToFind){
		this.txtToFind = txtToFind;
	}
	public void analyze() {
		String txt = testAgainst.toString();
		if (StringUtils.isEmpty(txt)){
			title = "No text was given to analyzer";
			status= false;
			return;
		}

		if (txt.indexOf(txtToFind) > 0){
			title = "Text " + txtToFind + " was found";
			status = true;
			return;
		}
		
		title = "Text " + txtToFind + " was not found";
		message = txtToFind;
		status = false;
		result = 5;
	}

	public int getResult(){
		return result;
	}
}
