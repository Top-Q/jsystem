/*
 * Created on 22/11/2006
 *
 * Copyright 2005 AQUA Software, LTD. All rights reserved.
 * AQUA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.aqua.jsystemobject;




public interface JSystem {
	public static String SCENARIO = "scenario";
	public static String RANDOM = "Random";
	public static String SCRIPT = "script";
	public static enum JSystemEvents {
		AddChangeSutTestEvent, SetTestParametr,SetFreezeOnFail,CheckFreezeOnFailException,Play,PlayWithBlock,
		ClearCurrentScenario,CheckRepeatStatus,AddTestEvent,ChangeHtmlReportDirProperty,SelectScenarioEvent,
		DeleteEvent,ChangeSutEvent;
	}
	
	public String launch() throws Exception;
	public int extract(String envZipPath) throws Exception;
	public int exit() throws Exception;
	public boolean play(boolean block) throws Exception;
	/**
	 * press the stop button and then press the stop immediately button on the gracefulstop dialog
	 */
	public int stop() throws Exception;
	/**
	 * press the stop button and wait for graceful stop dialog to close
	 */
	public int gracefulStop() throws Exception;
	public int setJSystemProperty(String key, String value) throws Exception;
	public String getJSystemProperty(String key) throws Exception;
	public String getUserDir() throws Exception;
	public int setUserDir(String userDir) throws Exception;	
	public int createScenario(String name) throws Exception;
	
	
	public int cleanCurrentScenario() throws Exception;
	public int selectSenario(String scenarioName) throws Exception;
	public int initReporters() throws Exception;
	public int addTest(String node, String parentNode, boolean equals) throws Exception;	
	public int deleteTest(int testIndex) throws Exception;
	public int deleteTest(String testName) throws Exception;
	public int moveTestUp(int testIndex) throws Exception;
	public int moveTestDown(int testIndex) throws Exception;
	public int filterSuccess() throws Exception;
	public int copyScenario(String newScenarioName) throws Exception;
	public String scenarioElement(int iIndex) throws Exception; 
	public String getReportDir() throws Exception;
	public int playPause() throws Exception;
	public int changeSut(String sutName) throws Exception;
	public int refresh() throws Exception;
	public int setFreezeOnFail(boolean freeze)throws Exception;
	public boolean checkIsRepeatSet(boolean status)throws Exception;
	public int  setReapit(boolean reapit)throws Exception ;
	public int  setRepAmount(int amount)throws Exception;
	public int  showLog()throws Exception;
	public int  editSut()throws Exception;
	public boolean  changeTestDir(String dir,String sutPath,boolean sutComboMustOpen)throws Exception;
	public int checkTest(int testIndex, boolean ckeck) throws Exception;
	public int waitForFreezeDialog() throws Exception;
	
	public String getCurrentFixture() throws Exception;
	public int setDisableFixture(boolean disable) throws Exception;	
	public int goToFixture(String fixtureName) throws Exception;
	public int setCurrentFixture(String fixturename) throws Exception;
	public int failToFixture(String fixtureName) throws Exception;
	public int setTestParameter(int testIndex, String tab, String paramName, String value,boolean isCombo) throws Exception;
	int activateExportWizard(final String jarPath, final boolean exportTests, final boolean exportScenarios,
			final boolean exportRunner, final boolean exportLog, final boolean exportSut, final boolean exportLib,
			final boolean exportJdk) throws Exception ;
	public int publishReport(String description , String SUT , String version ,String build,  String station) throws Exception;
	public int waitForMessage(String msgType)throws Exception;
	public int publishReportWithoutSaving()throws Exception;
	public int mapTest(int testIdx) throws Exception ;
	public int unmapTest(int testIdx) throws Exception ;
	public int CollapseExpandScenario(int testIndex) throws Exception;
	public String verifyParameterseExist(String parm,int testIndex , String tab) throws Exception;
}
