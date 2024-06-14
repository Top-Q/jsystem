/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.clients;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.Notification;
import javax.management.NotificationListener;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.report.DefaultReporterImpl.ReportLevel;
import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.notifications.RunnerNotification;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * Dispatches jsystem events to registered listeners.<br>
 * Each method is invoked by a corresponding {@link RunnerNotification}.
 *  
 * @author goland
 */
public class AgentClientListenersManager implements JSystemListeners,NotificationListener  {
	private static Logger log = Logger.getLogger(AgentClientListenersManager.class.getName());
	private Set<Object> listeners;
	
	public AgentClientListenersManager(){
		listeners = new HashSet<Object>();
	}

	public void addListener(Object listener) {
		listeners.add(listener);
	}

	public void removeListener(Object listener) {
		listeners.remove(listener);
	}

	
	public void addError(Test test, String message, String stack) {
		invokeMethod("addError",new Object[]{test,message,stack},new Class[]{Test.class,String.class,String.class});

	}

	public void addFailure(Test test, String message, String stack,
			boolean analyzerException) {
		invokeMethod("addFailure",new Object[]{test,message,stack,analyzerException},new Class[]{Test.class,String.class,String.class,boolean.class});

	}


	public void blockReporters(boolean block) {
		invokeMethod("blockReporters",new Object[]{block},new Class[]{boolean.class});
	}

	public boolean getLastTestFailed() {
		return false;
	}

	public boolean isPause() {
		return false;
	}


	public void setDate(String date) {
		invokeMethod("setDate",new Object[]{date},new Class[]{String.class});
	}

	public void aboutToChangeTo(Fixture fixture) {
		invokeMethod("aboutToChangeTo",new Object[]{fixture},new Class[]{Fixture.class});

	}

	public void endFixturring() {
		invokeMethod("endFixturring",null,null);

	}

	public void fixtureChanged(Fixture fixture) {
		invokeMethod("fixtureChanged",new Object[]{fixture},new Class[]{Fixture.class});

	}

	public void startFixturring() {
		invokeMethod("startFixturring",null,null);

	}

	public void sutChanged(String sutName) {
		invokeMethod("sutChanged",new Object[]{sutName},new Class[]{String.class});

	}

	public void scenarioChanged(Scenario current,ScenarioChangeType type) {
		invokeMethod("scenarioChanged",new Object[]{current,type},new Class[]{Scenario.class,ScenarioChangeType.class});
	}

	public void scenarioDirectoryChanged(File directory) {
		invokeMethod("scenarioDirectoryChanged",new Object[]{directory},new Class[]{File.class});

	}

	public void addWarning(Test test) {
		invokeMethod("addWarning",new Object[]{test},new Class[]{Test.class});

	}

	public void endRun() {
		invokeMethod("endRun",null,null);

	}

	public void startTest(TestInfo testInfo) {
		invokeMethod("startTest",new Object[]{testInfo},new Class[]{TestInfo.class});

	}

	public void addError(Test arg0, Throwable arg1) {
		invokeMethod("addError",new Object[]{arg0,arg1},new Class[]{Test.class,Throwable.class});

	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		invokeMethod("addFailure",new Object[]{arg0,arg1},new Class[]{Test.class,AssertionFailedError.class});

	}

	public void endTest(Test arg0) {
		invokeMethod("endTest",new Object[]{arg0},new Class[]{Test.class});

	}

	public void startTest(Test arg0) {
		invokeMethod("startTest",new Object[]{arg0},new Class[]{Test.class});

	}

	public void addLink(String title, String link) {
		invokeMethod("addLink",new Object[]{title,link},new Class[]{String.class,String.class});

	}

	public void addProperty(String key, String value) {
		invokeMethod("addProperty",new Object[]{key,value},new Class[]{String.class,String.class});

	}

	public void clearReportsBuffer() {
		invokeMethod("clearReportsBuffer",null,null);

	}

	public void endReport() {
		invokeMethod("endReport",null,null);

	}

	public void endReport(String steps, String failCause) {
		invokeMethod("endReport",new Object[]{steps,failCause},new Class[]{String.class,String.class});

	}

	public String getCurrentTestFolder() {
		return null;
	}
	
	public String getLastReportFile() {
		return null;
	}

	public List<ReportElement> getReportsBuffer() {
		return null;
	}

	public boolean isFailToPass() {
		return false;
	}

	public boolean isFailToWarning() {
		return false;
	}

	public void report(String title, String message, boolean status,
			boolean bold) {
		invokeMethod("report",new Object[]{title,message,status,bold},new Class[]{String.class,String.class,boolean.class,boolean.class});

	}

	public void report(String title, String message, boolean status) {
		invokeMethod("report",new Object[]{title,message,status},new Class[]{String.class,String.class,boolean.class});

	}

	public void report(String title, boolean status) {
		invokeMethod("report",new Object[]{title,status},new Class[]{String.class,boolean.class});

	}

	public void report(String title) {
		invokeMethod("report",new Object[]{title},new Class[]{String.class});
	}

	public void report(String title, Throwable t) {
		invokeMethod("report",new Object[]{title,t},new Class[]{String.class,Throwable.class});

	}

	public void report(String title, String message, int status, boolean bold) {
		invokeMethod("report",new Object[]{title,message,status,bold},new Class[]{String.class,String.class,int.class,boolean.class});
	}

	public void report(String title, String message, int status, boolean bold,
			boolean html, boolean step, boolean link) {
		invokeMethod("report",new Object[]{title,message,status,bold,html,step,link},new Class[]{String.class,String.class,int.class,boolean.class,boolean.class,boolean.class,boolean.class});
	}

	public void report(String title, String message, int status, boolean bold,
			boolean html, boolean step, boolean link, long time) {
		invokeMethod("report",new Object[]{title,message,status,bold,html,step,link,time},new Class[]{String.class,String.class,int.class,boolean.class,boolean.class,boolean.class,boolean.class,long.class});
	}

	public void report(ReportElement report) {
		invokeMethod("report",new Object[]{report},new Class[]{ReportElement.class});
	}

	public void report(String title, int status) {
		invokeMethod("report",new Object[]{title,status},new Class[]{String.class,int.class});
	}

	public void report(String title, String message, int status) {
		invokeMethod("report",new Object[]{title,message,status},new Class[]{String.class,String.class,int.class});
	}
	
	public void report(String title, ReportAttribute attribute){
		report(title, null, Reporter.PASS, attribute);
	}
	
	
	public void report(String title, String message, ReportAttribute attribute){
		report(title, message, Reporter.PASS, attribute);
	}
    
	public void report(String title, String message, int status, ReportAttribute attribute){
		report(title, message, status, attribute == ReportAttribute.BOLD, attribute == ReportAttribute.HTML, attribute == ReportAttribute.STEP, attribute == ReportAttribute.LINK);
	}

	public void reportHtml(String title, String html, boolean status) {
		invokeMethod("reportHtml",new Object[]{title,html,status},new Class[]{String.class,String.class,boolean.class});
	}
	public void saveFile(String fileName, byte[] content) {
		invokeMethod("saveFile",new Object[]{fileName,content},new Class[]{String.class,byte[].class});
	}

	public void setData(String data) {
		invokeMethod("setData",new Object[]{data},new Class[]{String.class});
	}

	public void setFailToPass(boolean failToPass) {
		invokeMethod("setFailToPass",new Object[]{failToPass},new Class[]{boolean.class});
	}

	public void setFailToWarning(boolean failToWarning) {
		invokeMethod("setFailToWarning",new Object[]{failToWarning},new Class[]{boolean.class});
	}

	public void setSilent(boolean status) {
		invokeMethod("setSilent",new Object[]{status},new Class[]{boolean.class});
	}
	
	public void setTimeStamp(boolean enable) {
		invokeMethod("setTimeStamp",new Object[]{enable},new Class[]{boolean.class});
	}

	public int showConfirmDialog(String title, String message, int optionType,
			int messageType) {
		return (Integer)invokeMethod("showConfirmDialog",new Object[]{title,message,optionType,messageType},new Class[]{String.class,String.class,int.class,int.class});
	}

	public void startBufferingReports() {
		invokeMethod("startBufferingReports",null,null);
	}

	public void startBufferingReports(boolean printBufferdReportsInRunTime) {
		invokeMethod("startBufferingReports",new Object[]{printBufferdReportsInRunTime},null);
	}
	
	public void startLevel(String level, int place) throws IOException {
		invokeMethod("startLevel",new Object[]{level,place},new Class[]{String.class,int.class});
	}
	
	public void startLevel(String level) throws IOException {
		startLevel(level, Reporter.CurrentPlace);
	}

	public void startLevel(String level, EnumReportLevel place)
			throws IOException {
		invokeMethod("startLevel",new Object[]{level,place},new Class[]{String.class,EnumReportLevel.class});
	}

	public void startReport(String methodName, String parameters) {
		invokeMethod("startReport",new Object[]{methodName,parameters},new Class[]{String.class,String.class});

	}

	public void startReport(String methodName, String parameters,String classDoc, String testDoc) {
		invokeMethod("startReport",new Object[]{methodName,parameters,classDoc,testDoc},new Class[]{String.class,String.class,String.class,String.class});
	}

	public void step(String stepDescription) {
		invokeMethod("step",new Object[]{stepDescription},new Class[]{String.class});
	}

	public void stopBufferingReports() {
		invokeMethod("stopBufferingReports",null,null);
	}

	public void stopLevel() throws IOException {
		invokeMethod("stopLevel",null,null);
	}

	public void errorOccured(String title, String message, ErrorLevel level) {
		invokeMethod("errorOccured",new Object[]{title,message,level},new Class[]{String.class,String.class,ErrorLevel.class});
	}
	public void executionEnded(String scenarioName) {
		invokeMethod("executionEnded",new Object[]{scenarioName},new Class[]{String.class});
	}

	public void remoteExit() {
		invokeMethod("remoteExit",null,null);
	}

	public void remotePause() {
		invokeMethod("remotePause",null,null);
	}
	
	@Override
	public void initReporters(){
		invokeMethod("init",null,null);
	}

	@Override
	public void saveState(Test t) throws Exception {
	}
	
	public void handleNotification(Notification notification, Object handback) {
		log.fine("Got event: " + notification.getType() + " " + notification.getSource());
		if ((notification instanceof RunnerNotification)){
			((RunnerNotification)notification).invokeDispatcher(this);
		}
		invokeMethod("handleNotification",new Object[]{notification,handback},new Class[] {Notification.class,Object.class});
	}

	private Object invokeMethod(String methodName,Object[] objects,Class<?>[] classes){
		Method method =null;
		Object retVal = null;
		Object[] listenersObj = listeners.toArray(new Object[0]);
		for (Object o:listenersObj){
			try {
				method = 
					o.getClass().getMethod(methodName,classes);
			}catch (Exception e) {
				log.fine( methodName + " was not found in " + o.getClass().getName());
				continue;
			}		
			try {
				retVal = method.invoke(o,objects);
			}catch (Throwable t){
				log.log(Level.WARNING,"Failed executing method" + methodName,t);
			}
		}
		return retVal;
	}

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

	@Override
	public void closeAllLevels() throws IOException {
		invokeMethod("startLevel",new Object[]{},new Class[]{});
	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues,
			Parameter[] newValues) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContainerProperties(int ancestorLevel, String key,
			String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSilent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flushReporters() {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void jira(String title, String jiraId) {
		report(title + EnumBadge.JIRA.setText(jiraId).get());		
	}

	@Override
	public void info(String title, String info) {
		report(title + EnumBadge.INFO.setText(info).get());		
	}

	@Override
	public void info(String title) {
		report(title + EnumBadge.INFO.setText(EnumBadge.INFO.name().toLowerCase()).get());		
	}

	@Override
	public void workaround(String title) {
		report(title + EnumBadge.WORKAROUND.get());		
	}

	@Override
	public void debug(String title) {
		report(title + EnumBadge.DEBUG.get());
	}

	@Override
	public void bug(String title) {
		report(title + EnumBadge.BUG.get());		
	}

	@Override
	public void result(String title, int status) {
		report(title + EnumBadge.RESULT.get(), status);
	}

	@Override
	public void result(String title) {
		result(title, Reporter.PASS);
	}

	@Override
	public void result(String title, boolean status) {
		result(title, status ? Reporter.PASS : Reporter.FAIL);
	}

	@Override
	public void result(String title, ReportAttribute reportAttribute) {
		report(title + EnumBadge.RESULT.get(), reportAttribute);
	}
	
	@Override
	public ReportLevel reportLevel(String level) {
		return (ReportLevel) invokeMethod("reportLevel", new Object[]{level},new Class[]{String.class});
	}
	
	@Override
	public ReportLevel reportLevel(String level, EnumBadge badge) {
		return (ReportLevel) invokeMethod("reportLevel", new Object[]{level,badge},new Class[]{String.class,EnumBadge.class});
	}
	
}
