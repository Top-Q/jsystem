/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn.analyzers;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnection;

/**
 * An analyzer that verifies that there is no output to the executed command.
 * For example: in windows the mkdir command doesn't have any output when it
 * succeeds. This analyzer analyzes the result returned by the Cli entity
 * and can verify the the output of the command is indeed empty.
 * 
 * The analyzer can also get an array of 'except' strings, in this case
 * it will verify that the output is empty except one of the given strings;
 * that is if one of the except string will be returned the analysis will
 * still pass.
 * In our example, if the mkdir will try to create a directory that already exists
 * the following message will be returned: 'A sub-directory or file that already exists.'
 * if given to the analyzer in the except array this message will be ignored.
 * 
 * In order for the analyzer to work, please set the suppressEcho to true
 * in the CliCommand.
 * 
 * Please note that this analyzer will not work well on devices with
 * bursting messages.
 * 
 * 
 * @author goland
 */
public class NoOutputAnalyzer extends AnalyzerParameterImpl {
	private String[] except;
	private CliConnection cliConnection;

	public NoOutputAnalyzer(CliConnection cliConnection,String[] except){
		this.cliConnection = cliConnection;
		this.except = except;
	}

	public NoOutputAnalyzer(CliConnection cliConnection){
		this(cliConnection,null);
	}
	
	public void analyze() {
		status = true;
		title = "Operation did not return any text";	
		try {
			String prompt = getPromptLine().toLowerCase();
			String retStringAfterClean = cleanWhiteSpaces(testAgainst.toString().toLowerCase());
			retStringAfterClean = cleanExpected(retStringAfterClean, except);
			String res = retStringAfterClean.substring(0,findLastIndexOfPrompt(retStringAfterClean,prompt)).trim();
			if (!"".equals(res)) {
				status = false;
				title = "Returned value is not empty";
				message = "Returned buffer is "+testAgainst.toString();
			}
		}catch (Exception e){
			status = false;
			title = "Error in analyzis";
			message = "Failed analyzing whether the returned buffer is empty." + e.getMessage();
		}
	}

	/**
	 * Looks for the index of the prompt in <code>res</code>.
	 * In some machine part of the prompt is a time stamp, for these cases,
	 * if prompt is not found it is cut into half until prompt is found in result.
	 */
	private int findLastIndexOfPrompt(String res, String prompt){
		while (res.lastIndexOf(prompt) ==-1){
			prompt = prompt.substring(0,prompt.length()/2+1);
			if (prompt.length() == 1){
				throw new RuntimeException("Could not find prompt in operation results");
			}
		}		
		return res.lastIndexOf(prompt);
	}

	/**
	 * Sends an empty command. The result of the empty command is the remote machine
	 * full prompt.
	 */
	private String getPromptLine() 	throws Exception {
		CliCommand command = new CliCommand();
		command.setCommand("");
		cliConnection.command(command);
		return cleanWhiteSpaces(command.getResult());
	}
	private String cleanWhiteSpaces(String txt) {
		return txt.replaceAll("\\s", "");
	}
	
	private  String cleanExpected(String txt,String[] expected){
		if (expected == null){
			return txt;
		}
		for(int i=0; i<expected.length;i++){
			expected[i] = cleanWhiteSpaces(expected[i].toLowerCase());
			txt = txt.replaceAll(expected[i], "");
		}
		return txt.toLowerCase();
	}


}
