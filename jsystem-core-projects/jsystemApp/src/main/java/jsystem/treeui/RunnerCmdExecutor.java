/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.actionItems.SwitchProjectAction;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class RunnerCmdExecutor implements ExecutionListener{
	private String[] args;
	private final String project = "-project";
	private final String scenario = "-scenario";
	private final String sut = "-setup";
	private String projectName;
	private boolean proceed;
	private String scenarioName;
	private String sutName;
	/**
	 *  the separator between runs
	 */
	private final String argsSeperator = "&&";
	private static Logger log = Logger.getLogger(RunnerCmdExecutor.class.getName());

	public RunnerCmdExecutor(String[] args){
		this.args = args;
	}

	public void init(){
		ListenerstManager.getInstance().addListener(this);//to listen on run ends.
		String argList=null;
		if(args.length > 1){ // not a file-multiple projects execution
			argList = argsToString();
		}else{
			try{
				argList = readFileContent(args[0]);
			}catch (Exception e) {
				log.warning("Failed reading file "+args[0]+" content");
			}
			if(StringUtils.isEmpty(argList)){
				log.info("file is empty");
				return;
			}
		}
		String[] argsLines = argList.split(argsSeperator);
		String[] arguments = null;

		for(int k = 0; k < argsLines.length; k++){
			String line = argsLines[k];
			proceed = false;
			
			if(line.split("\\#\\?\\$").length == 1){ //nothing split since the command came from a file
				arguments = argsFromFileToString(line).split("\\#\\?\\$");
			}else{
				arguments = line.split("\\#\\?\\$"); //For command line command
			}
			for (int j = 0; j < arguments.length; j++) {

				if(arguments[j].trim().equalsIgnoreCase(project)){
					projectName = arguments[++j];
					log.info("the project name is: "+projectName);
				}
				else if (arguments[j].equalsIgnoreCase(scenario)){
					scenarioName = arguments[++j];
					log.info("the scenario name is: "+scenarioName);
				}
				else if (arguments[j].equalsIgnoreCase(sut)){
					log.info("the sut name is: "+sutName);
					sutName = arguments[++j];
				}
				//if asked to exit, only exit if it's the last scenario to run.
				else if (arguments[j].trim().equals("-exit")){
					if(k == argsLines.length - 1){
						JSystemProperties.getInstance().setExitOnRunEnd(true);
					}
				}
				else{
					scenarioName = arguments[j];
				}
			}
			//if not mentioned, project name will be taken from jsystem.properties, 
			//last project that was opened, or default.
			if(StringUtils.isEmpty(projectName)){
				projectName = JSystemProperties.getInstance().getPreference("tests.dir");
			}
			if (scenarioName != null) {
				try {
					//after changing project it calls refresh that takes currently used sut which is
					//not one from the new project and looks to load it (fails). 
					//so setting the current sut to be the one we want to run.
					if(sutName != null){
						JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, sutName);
					}
					SwitchProjectAction.getInstance().changeTestDir(projectName,sutName,true);

					TestRunner.treeView.tableController.loadScenario(scenarioName, true);

					// play the scenario
					PlayAction.getInstance().actionPerformed(null);
					synchronized (this) {
						while(!proceed){
							wait();
						}
					}

				} catch (Exception e) {
					ErrorPanel.showErrorDialog("Execution failure", StringUtils.getStackTrace(e), ErrorLevel.Error);
				}
			}
			log.info("Runner was init successfully.");
		}
	}
	
	
	private String readFileContent(String fileName) throws Exception{
		return (FileUtils.read(fileName));
	}

	private String argsToString(){
		StringBuffer sb = new StringBuffer();
		for(String str : args){
			sb.append(str+"#?$");
		}
		return sb.toString();
	}
	
	private String argsFromFileToString(String line){
		StringBuffer sb = new StringBuffer();
		String[] firstSplit = line.split("\"");
		for(int i = 0 ; i < firstSplit.length; i++){
			if(firstSplit[i].trim().startsWith("-")){
				String[] secondSplit = firstSplit[i].trim().split(" +");
				for(int j = 0 ; j < secondSplit.length; j++){
					sb.append(secondSplit[j].trim()+"#?$");
				}
			}else{
				sb.append(firstSplit[i].trim()+"#?$");
			}
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	public void remoteExit(){}

	public void remotePause(){}

	public synchronized void executionEnded(String scenarioName){
		proceed = true;
		notifyAll();
	}

	public void errorOccured(String title,String message,ErrorLevel level){}

	public void endRun(){}

	public void addWarning(Test test){}

	public void startTest(TestInfo testInfo){}

	public void startTest(Test test){}

	public void addError(Test test, Throwable t){}

	public void addFailure(Test test, AssertionFailedError t){}  

	public void endTest(Test test){}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}
}
