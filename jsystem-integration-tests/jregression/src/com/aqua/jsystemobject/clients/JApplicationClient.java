package com.aqua.jsystemobject.clients;

import java.util.Vector;
import java.util.Map.Entry;

import utils.Commons;

import jsystem.framework.FrameworkOptions;

import com.aqua.jsystemobject.JSystemEnvControllerOld;
import com.aqua.jsystemobject.handlers.JServerHandlers;

public class JApplicationClient extends BaseClient {

	
	/**
	 * needed for jsystemEnvController getSystemClient for the new instance method
	 */
	public JApplicationClient() {
		super();
		handler = JServerHandlers.APPLICATION;
	}
	
	public JApplicationClient(Process serverProcess, int port, String userDir) {
		super(serverProcess, port, userDir);
		handler = JServerHandlers.APPLICATION;
	}
	
	@Override
	String getHandlerName() {
		return handler.getHandlerClassName();
	}
	
	/**
	 * will call the local method that accepts string with 
	 * empty string
	 * @throws Exception
	 */
	public void launch() throws Exception{
		launch("");
	}
	
	/**
	 * calls a method to refresh the remote reports button.
	 * @param value
	 * @throws Exception 
	 */
	public void refreshReportsButton(boolean value) throws Exception{
		callHandleXml("refresh the reports button", "refreshReportsButton", value);
	}
	
	/**
	 * launch runner with sut file as parameter
	 */
	public void launch(String sutFile) throws Exception {
		callHandleXml("launch application", "launch", sutFile);
		Thread.sleep(2000);
	}
	
	/**
	 * launch runner
	 * 
	 * @param disableZip	if True will disable zip
	 * @throws Exception
	 */
	public void launch(boolean disableZip) throws Exception {
		callHandleXml("launch application", "launch", disableZip);
		Thread.sleep(2000);
	}
	
	/**
	 * launch runner
	 * 
	 * @param sutFile	the sut to start with
	 * @param disableZip	if True will disable zip	
	 * @throws Exception
	 */
	public void launch(final String sutFile,boolean disableZip) throws Exception {
		callHandleXml("launch application", "launch", sutFile,disableZip);
		Thread.sleep(2000);
	}
	
	/**
	 * extract remote zip file
	 * @param envZipPath path to zip file to extruct
	 * @throws Exception
	 */
	public void extract(String envZipPath) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(envZipPath);
		callHandleXml("extract", "extract", v);
		Thread.sleep(2000);
	}
	
	/**
	 * call the activateExportWizard function in the JApplicationHandler
	 * 
	 * @param jarPath
	 * @param compiledOutput
	 * @param systemObjects
	 * @param exportRunner
	 * @throws Exception
	 */
	public void activateExportWizard(String jarPath, boolean compiledOutput, boolean exportRunner,
		boolean exportJdk, boolean exportLog) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(jarPath);
		v.addElement(Boolean.valueOf(compiledOutput));
		v.addElement(Boolean.valueOf(exportRunner));
		v.addElement(Boolean.valueOf(exportJdk));
		v.addElement(Boolean.valueOf(exportLog));
		callHandleXml("activateExportWizard", "activateExportWizard", v);
	}
	
	/**
	 * 
	 * @return true if the play button is enabled
	 * @throws Exception
	 */
	public Boolean checkIfPlayIsEnabled() throws Exception{
		return (Boolean)callHandleXml("check if play is enabled", "checkIfPlayIsEnabled");
	}
	
	/**
	 * push the play button blocking
	 * calls play with true
	 * don't call waitForExecutionEnd after call to play.
	 * @throws Exception
	 */
	public void play() throws Exception {
		play(true);
	}

	/**
	 * if called with true -> then play() and waitForRunEnd()
	 * if called with false -> then don't forget to call waitForRunEnd() when finished.
	 */
	public void play(boolean block) throws Exception {
		callHandleXml("play", "play", block);
	}
	
	/**
	 * will activate the save scenario operation on the remote runner.
	 * @throws Exception
	 */
	public void saveScenario()throws Exception{
		callHandleXml("save scenario", "saveScenario");
	}
	
	
	/**
	 * use to set JSystemProperty value Properties names : tests.dir tests.src
	 * htmlReportDir shutdown.threads lib.path reporter.classes sutClassName
	 * sutFile fixture.return sysobj.close html.dir.old reporter.addtime
	 * html.tree ant.home ant.notify.disable repeat
	 */
	@SuppressWarnings("deprecation")
	public void setJSystemProperty(FrameworkOptions key, String value) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(key.toString());
		v.add(value);
		callHandleXml("set jsystem property " + key + " to " + value, "setJSystemProperty", v);
	}
	
	public void setInitialJsystemProperties() throws Exception{
		for (Entry<FrameworkOptions, String> entry : Commons.getBaseJsystemProperties().entrySet()){
			setJSystemProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public void refresh() throws Exception{
		callHandleXml("refresh the runner", "refresh");
	}
	
	/**
	 * calls the getJSystemProperty on the handler.
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public String getJSystemProperty(FrameworkOptions key) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(key.toString());
		return (String) callHandleXml("get jsystem property: " + key, "getJSystemProperty", v);
	}
	
	
	/**
	 * calls the waitForWarningDialog in JApplicationHandler
	 */
	public void waitForWarningDialog() throws Exception {
		callHandleXml("Wait for warning dialog", "waitForWarningDialog");
		Thread.sleep(500);
	}
	
	/**
	 * close remote runner
	 */
	public void exit() throws Exception {
		if (maskExit) {
			return;
		}
		callHandleXml("exit Application", "exit");
		Thread t = new Thread() {
			public void run() {
				if (serverProcess != null) {
					try {
						serverProcess.waitFor();
					} catch (InterruptedException e) {
					}
				}
			}
		};
		t.start();
		t.join(10000);
		if (t.isAlive()) {
			t.interrupt();
		}
		JSystemEnvControllerOld.setUseExistingServer(false);
	}

	/**
	 * call the remote menu exiting of runner
	 * @param exit
	 * @throws Exception
	 */
	public void exitThroughMenu(boolean exit) throws Exception {
		Vector<Object> v = new Vector<Object>();
		v.addElement(exit);
		callHandleXml("Exit through menu", "exitThroughMenu", v);
	}
	
	/**
	 * make the test wait for this run 
	 * to end before continuing to the next step.
	 * @throws Exception
	 */
	public void waitForRunEnd() throws Exception{//waitForRunEnd/waitForExecutionEnd
		waitForRunEnd(1);
	}
	
	/**
	 * wait for amount of runs to end
	 * @param amount the number of runs to wait for run to end.
	 * @throws Exception
	 */
	public void waitForRunEnd(int amount) throws Exception {
		callHandleXml("wait for run end", "waitForRunEnd", amount);
	}
	
	/**
	 * call the stop method on the handler
	 * @throws Exception
	 */
	public void stop() throws Exception {
		callHandleXml("stop", "stop");
	}

	/**
	 * call the playPause method on handler
	 * @throws Exception
	 */
	public void playPause() throws Exception {
		callHandleXml("pause", "playPause");
	}
	
	/**
	 * call the gracefulStop on the handler
	 * @throws Exception
	 */
	public void gracefulStop() throws Exception {
		callHandleXml("stop", "gracefulStop");
	}

	
	/**
	 * calls the waitForRunEndUntilLeftRepeatAmountIs on handler
	 * @param amount
	 * @throws Exception
	 */
	public void waitForRunEndUntilLeftRepeatAmountIs(int amount) throws Exception {
		callHandleXml("wait for run end", "waitForRunEndUntilLeftRepeatAmountIs", amount);
	}
	
	public String getDBProperty(String key) throws Exception{
		return (String)callHandleXml("get remote DB property", "getDBProperty",key);
	}
	
	/**
	 * changes a specific value in the remote db.property to the specified value.
	 * @param propertyKey
	 * @param newVal
	 * @return
	 * @throws Exception 
	 */
	public boolean changeDbProperty(String propertyKey, String newVal) throws Exception{
		return (Boolean) callHandleXml("change a property value in db.properties", "changeDbProperty",propertyKey, newVal);
	}
	
	/**
	 * call the changesut func on handler
	 * @param sutName
	 * @throws Exception
	 */
	public void changeSut(String sutName) throws Exception {
		callHandleXml("change sut: " + sutName, "changeSut", sutName);
	}
	
	/**
	 * 
	 * @param name
	 * @throws Exception
	 */
	public void selectSut(String name) throws Exception {
		callHandleXml("Select sut " + name, "selectSut", name);
	}
	
	/**
	 * calls the openSutEditor in handler
	 * @param expectError
	 * @param close
	 * @throws Exception
	 */
	public void openSutEditor(boolean expectError, boolean close) throws Exception {
		callHandleXml("Open sut editor", "openSutEditor", expectError,close);
	}
	
	public Object chooseMenuItem(String...menuItems) throws Exception {
		return chooseMenuItem(true, menuItems);
	}

	public Object chooseMenuItem(boolean pushLastLevel, String...menuItems) throws Exception {
		// Passing array via XMLRPC requires a Vector
		return callHandleXml("Choose Menu Item", "chooseMenuItem", pushLastLevel, vectorize(menuItems));
	}
	
	public Object setToolbarView(String toolbarName) throws Exception {
		return callHandleXml("Set Toolbar View", "setToolbarView", toolbarName);
	}
	
	public Boolean getToolbarViewState(String toolbarName) throws Exception {
		return (Boolean)callHandleXml("Get Toolbar View State", "getToolbarViewState", toolbarName);
	}
	public Boolean getFlowControlToolbarState() throws Exception {
		return (Boolean)callHandleXml("Get Flow Control Toolbar View State", "getFlowControlToolbarState");
	}
	public Boolean getAgentToolbarState() throws Exception {
		return (Boolean)callHandleXml("Get Agent Toolbar View State", "getAgentToolbarState");
	}
	public Boolean getMainToolbarState() throws Exception {
		return (Boolean)callHandleXml("Get Main Toolbar View State", "getMainToolbarState");
	}
	public void initReporters() throws Exception {
		callHandleXml("init reporters", "initReporters");
		Thread.sleep(3000);
	}
	public Boolean isLoopButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether Loop button enabled", "isLoopButtonEnabled");
	}
	public Boolean isIfButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether if button enabled", "isIfButtonEnabled");
	}
	public Boolean isElseIfButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether else-if button enabled", "isElseIfButtonEnabled");
	}
	public Boolean isSwitchButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether switch button enabled", "isSwitchButtonEnabled");
	}
	public Boolean isCaseButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether case button enabled", "isCaseButtonEnabled");
	}
	public synchronized Boolean isRemoveTestsButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether Remove Tests button enabled", "isRemoveTestsButtonEnabled");
	}
	public synchronized Boolean isMoveTestsDownButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether Move Down button enabled", "isMoveTestsDownButtonEnabled");
	}
	public synchronized Boolean isMoveTestsUpButtonEnabled() throws Exception {
		return (Boolean)callHandleXml("Check whether Move Up button enabled", "isMoveTestsUpButtonEnabled");
	}
}
