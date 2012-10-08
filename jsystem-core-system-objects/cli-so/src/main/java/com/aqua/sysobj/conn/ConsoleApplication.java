/*
 * Created on Aug 1, 2005
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.Properties;

import jsystem.framework.analyzer.AnalyzerParameter;

/**
 * @author inna.tsiplakov
 *
 */
public class ConsoleApplication extends Application {
	public void init() throws Exception{
		super.init();
	}
	protected void handleCliCommand(String title, CliCommand command, Properties p) throws Exception{
		conn.getConsole().command(command);
		setTestAgainstObject(command.getResult());
		if(command.isFailed()){
			report.report(getName() + ": " + title + ", " + command.getFailCause(), command.getResult(), false);
			Exception e = command.getThrown();
			if (e != null){
				throw e;
			} else {
				throw new Exception("console command failed");
			}
		}
		report.report(getName() + ": " + title, command.getResult(), true);
		AnalyzerParameter[] analyzers = command.getAnalyzers();
		if (analyzers != null){
			for (int i = 0; i < analyzers.length; i++){
				analyze(analyzers[i], true);
			}
		}
	}

}
