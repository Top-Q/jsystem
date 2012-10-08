/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.guiMapping;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.summary.HtmlSummaryReporter;


/**
 * Mapping Jsystem GUI
 * @author Yaron
 * This class is used a GUI mapping reader tool. it reads properties from the  JSystemMappingProperties.
 * the properties are used by the other clases to work with the TAS GUI objects
 * when one of the GUI objects are changed, the only change that is needed is in the file
 */
public class JsystemMapping  {

	Properties properties = new Properties();
	private static Logger log = Logger.getLogger(HtmlSummaryReporter.class.getName());

	File file = new File("JSystemMapping.properties");
	
	static JsystemMapping mapping;

	public JsystemMapping() {
		
		InputStream stream =null;
		try {
			stream = 
				getClass().getClassLoader().getResourceAsStream("jsystem/guiMapping/JSystemMapping.properties");                
			if (stream == null){
				throw new RuntimeException("Jar file was not found");
			}
			properties.load(stream);
		} catch (Exception e) {
			log.log(Level.SEVERE, "JSystem Mapping file was not found");
		}finally {
			try{stream.close();}catch(Exception e){};
		}
}
	
	public static JsystemMapping getInstance(){
		if (mapping == null){
			 mapping = new JsystemMapping();
		}
		return mapping;
	}
	
	/*
	 * Main Frame
	 */
	public String getExitButton () {return returnStringProperties("EXIT_BUTTON");}
	
	
	/*
	 * T-Run GUI
	 * 
	 */
	public String getJSyetemMain(){return properties.getProperty("JSYSTEM_MAIN").trim();}  //This method cannot throw an
																						   //that is why it is as different
															   //implementation
	public String getMainToolbar () {return returnStringProperties("MAIN_TOOLBAR");}
	public String getAgentToolbar () {return returnStringProperties("AGENT_TOOLBAR");}
	public String getFlowControlToolbar () {return returnStringProperties("FLOW_CONTROL_TOOLBAR");}
	public String getSourceControlToolbar () {return returnStringProperties("SOURCE_CONTROL_TOOLBAR");}
	public String getEditSutButton () {return returnStringProperties("EDIT_SUT");}
	public String getViewProcessedSutButton () {return returnStringProperties("PROCESSED_SUT");}
	public String getCheckSystemObjectStatus () {return returnStringProperties("CHECK_STATUS");}
	public String getSystemObjectBrowserButton () {return returnStringProperties("SYSTEM_OBJECT_BROWSER");}
	public String getEditScenarioButton () {return returnStringProperties("EDIT_SCENARIO");}
	public String getBuildingBlockInformationToolTip() {return returnStringProperties("BUILDING_BLOCK_INFORMATION_TOOL_TIP");}
	public String getFilterToolTip() {return returnStringProperties("TESTS_TREE_FILTER_TOOL_TIP");}
	public String getTestsTreeName() {return returnStringProperties("TESTS_TREE_NAME");}

	
	public String getCutButton () {return returnStringProperties("CUT_TESTS");}
	public String getCopyButton () {return returnStringProperties("COPY_TESTS");}
	public String getPasteButton () {return returnStringProperties("PASTE_TESTS");}
	public String getPasteAfterButton () {return returnStringProperties("PASTE_AFTER_TESTS");}
	
	public String getPlayButton () {return returnStringProperties("PLAY_BUTTON");}
	public String getStopButton () {return returnStringProperties("STOP_BUTTON");}
	public String getExportButton () {return returnStringProperties("EXPORT_BUTTON");}
	public String getImportButton () {return returnStringProperties("IMPORT_BUTTON");}
	public String getClearScenarioButton () {return returnStringProperties("CLEAR_SCENARIO_BUTTON");}
	public String getCopyScenarioButton () {return returnStringProperties("COPY_SCENARIO_BUTTON");}
	public String getDeleteScenarioWindow () {return returnStringProperties("DELETE_SCENARIO_WINDOW");}
	public String getResetToDefaultWindow () {return returnStringProperties("RESET_TO_DEFAULT");}
	public String getPublishEventButton () {return returnStringProperties("NOTIFICATION_EVENT_BUTTON");}
	public String getChangeSutEventButton () {return returnStringProperties("CHANGE_SUT_EVENT");}
	public String getAddTestsButton () {return returnStringProperties("ADD_TESTS_BUTTON");}
	public String getAddTestsDialog () {return returnStringProperties("ADD_TESTS_DIALOG");}
	public String getRemoveTestsButton () {return returnStringProperties("REMOVE_TESTS_BUTTON");}
	public String getScenarioRedoButton () {return returnStringProperties("SCENARIO_EDIT_REDO");}
	public String getScenarioUndoButton () {return returnStringProperties("SCENARIO_EDIT_UNDO");}
	public String getMoveTestUpButton () {return returnStringProperties("TEST_MOVE_UP_BUTTON");}
	public String getMoveTestDownButton () {return returnStringProperties("TEST_MOVE_DOWN_BUTTON");}
	public String getMoveTestToBottomButton () {return returnStringProperties("TEST_MOVE_TO_BOTTOM_BUTTON");}
	public String getMoveTestToTopButton () {return returnStringProperties("TEST_MOVE_TO_TOP_BUTTON");}
	public String getPauseButton () {return returnStringProperties("PAUSE_BUTTON");}
	public String getRefreshButton () {return returnStringProperties("REFRESH_BUTTON");}
	public String getFixtureFailToButton () {return returnStringProperties("FIXTURE_FAIL_BUTTON");}
	public String getFixtureGoToButton () {return returnStringProperties("FIXTURE_GOTO_BUTTON");}
	public String getPublishButton () {return returnStringProperties("PUBLISH_BUTTON");}
	public String getRefreshPublishButton () {return returnStringProperties("REFRESH_PUBLISH_BUTTON");}
	public String getFixtureSetCurrentButton () {return returnStringProperties("FIXTURE_SET_CURRENT");}
	public String getLogButton () {return returnStringProperties("LOG_BUTTON");}
	public String getSwitchProjectButton () {return returnStringProperties("SWITCH_PROJECT_BUTTON");}
	public String getNewScenarioButton () {return returnStringProperties("NEW_SCENARIO_BUTTON");}
	public String getOpenScenarioButton () {return returnStringProperties("OPEN_SCENARIO_BUTTON");}
	public String getTestTreeTab () {return returnStringProperties("TEST_TREE_TAB");}
	public String getFixtureTAB () {return returnStringProperties("FIXTURE_TAB");}	
	public String getPublisherTAB () {return returnStringProperties("PUBLISHER_TAB");}
	public String getInitReportMenu () {return returnStringProperties("INIT_REPORT_MENU");}
	public String getInitReportDialogTitle () {return returnStringProperties("INIT_REPORTS_DIALOG");}
	public String getHelpMenu () {return returnStringProperties("HELP_MENU");}
	public String getTestSelection () {return returnStringProperties("TEST_SELECTION");}
	public String getTestUnSelection () throws Exception {return returnStringProperties("TEST_UNSELECTION");} 
	public int 	  getFreezeOnFailCheckbox () {return returnIntProperty("FREEZ_ON_FAIL_CHECKBOX");}
	public int    getRepeatCheckbox () {return returnIntProperty("REPEAT_CHECKBOX");}
	public int    getRepeatAmountEdit () {return returnIntProperty("REPEAT_AMOUNT_EDIT");}
	public String getExportWin () {return returnStringProperties("EXPORT_MAIN");}	
	public String getExportNextButton () {return returnStringProperties("EXPORT_NEXT_BUTTON");}
	public String getExportTestsCheckbox() {return returnStringProperties("EXPORT_TESTS_CHECKBOX");}
	public String getExportScenariosCheckbox() {return returnStringProperties("EXPORT_SCENARIOS_CHECKBOX");}
	public String getExportRunnerCheckbox () {return returnStringProperties("EXPORT_RUNNER_CHECKBOX");}
	public String getExportLogCheckbox () {return returnStringProperties("EXPORT_LOG_CHECKBOX");}
	public String getExportSutCheckbox () {return returnStringProperties("EXPORT_SUT_CHECKBOX");}
	public String getExportLibCheckbox () {return returnStringProperties("EXPORT_LIB_CHECKBOX");}
	public String getExportJdkCheckbox () {return returnStringProperties("EXPORT_JDK_CHECKBOX");}
	public String getExportFinishButton () {return returnStringProperties("EXPORT_FINISH_BUTTON");}
	public String getDialogSelectWin () {return returnStringProperties("DIALOG_SELECT_WIN");}
	public String getDialogSelectYesButton () {return returnStringProperties("DIALOG_SELECT_YES_BUTTON");}
	public String getDialogSelectOKButton () {return returnStringProperties("DIALOG_SELECT_OK_BUTTON");}
	public String getOpenReportApplicationButton () {return returnStringProperties("OPEN_REPORTS_APPLICATION");}
	public String getToggleDebugOptionButton () {return returnStringProperties("TOGGLE_DEBUG_OPTION");}

	public String getScenarioSelectWin () {return returnStringProperties("SCENARIO_SELECT_WIN");}
	public String getScenarioDialogSelectButton () {return returnStringProperties("SELECT_SCENARIO_DIALOG_BUTTON");}
	public String getScenarioCopyWin () {return returnStringProperties("COPY_SCENARIO_WIN");}
	public String getScenarioCopyButton () {return returnStringProperties("SCENARIO_COPY_BUTTON");}
	public String getSwitchProjectWin () {return returnStringProperties("SWITCH_PROJECT_MAIN");}
	public String getSwitchProjectSaveButton () {return returnStringProperties("SWITCH_PROJECT_SELECT_BUTTON");}
	public String getSUTWin () {return returnStringProperties("SUT_WIN");}
	public String getSUTSelectButton () {return returnStringProperties("SUT_SELECT_BUTTON");}
	public String getNewScenarioWin () {return returnStringProperties("NEW_SCENARIO_WIN");}	
	public String getNewScenarioSaveButton() {return returnStringProperties("NEW_SCENARIO_SAVE_BUTTON");}		
	public String getSaveScenarioButton() {return returnStringProperties("SAVE_SCENARIO_BUTTON");}
	public String getFreezeDialogWin () {return returnStringProperties("FREEZE_DIALOG_WIN");}	
	public String getFreezeDialogButton () {return returnStringProperties("FREEZE_DIALOG_BUTTON");}
	public String getWarningDialogWin () {return returnStringProperties("DIALOG_WARNING_WIN");}
	public String getWaitExportProcessDialogWin() {return returnStringProperties("DIALOG_WAIT_EXPORT_PROCESS_WIN");}
	public String getErrorDialogWin() {return returnStringProperties("DIALOG_ERROR_WIN");}
	public String getPublisherWin () {return returnStringProperties("PUBLISHER_DIALOG_WIN");}	
	public int    getPublisherDescriptionField () {return returnIntProperty("PUBLISHER_DESCRIPTION_FIELD");}
	public int    getPublisherSUTField () {return returnIntProperty("PUBLISHER_SUT_FIELD");}
	public int    getPublisherVersionField () {return returnIntProperty("PUBLISHER_VERSION_FIELD");}
	public int    getPublisherBuildField () {return returnIntProperty("PUBLISHER_BUILD_FIELD");}
	public int    getPublisherStationField () {return returnIntProperty("PUBLISHER_STATION_FIELD");}
	public String getPublisherSaveButton () {return returnStringProperties("PUBLISHER_SAVE_BUTTON");}
	public String getPublisherCancelButton () {return returnStringProperties("PUBLISHER_CANCEL_BUTTON");}
	public String getScenarioChangeWin () {return returnStringProperties("SCENARIO_CHANGE_WIN");}	
	public String getScenarioChangeButton () {return returnStringProperties("SCENARIO_NO_BUTTON");}
	public String getSystemMessageButton(){return returnStringProperties("SYSTEM_MESSAGE_BUTTON");}
	public String getTestCommentMenuItem(){return returnStringProperties("TEST_MENU_COMMENT_ITEM");}
	public String getTestMapMenuItem(){return returnStringProperties("TEST_MENU_MAP");}
	public String getTestUnmapMenuItem(){return returnStringProperties("TEST_MENU_UNMAP");}
	public String getTestUnmapAllMenuItem(){return returnStringProperties("TEST_MENU_UNMAP_ALL");}
	public String getTestMapAllMenuItem(){return returnStringProperties("TEST_MENU_MAP_ALL");}
	public String getTestMoveDownMenuItem(){return returnStringProperties("TEST_MENU_MOVE_DOWN");}
	public String getTestMoveUpMenuItem(){return returnStringProperties("TEST_MENU_MOVE_UP");}
	public String getTestMoveToBottomMenuItem(){return returnStringProperties("TEST_MENU_MOVE_TO_BOTTOM");}
	public String getTestMoveToTopMenuItem(){return returnStringProperties("TEST_MENU_MOVE_TO_TOP");}
	public String getScenarioNavigateForward(){return returnStringProperties("SCENARIO_NAVIGATE_FORWARD");}
	public String getScenarioNavigateBackword(){return returnStringProperties("SCENARIO_NAVIGATE_BACKWORD");}
	public String getNavigateToSubScenario(){return returnStringProperties("NAVIGATE_TO_SUB_SCENARIO");}
	public String getRefreshRunnerDialog(){return returnStringProperties("REFRESH_RUNNER_DIALOG");}
	public String getTestDeleteMenuItem(){return returnStringProperties("TEST_MENU_DELETE");}
	public String getScenarioEditOnlyLocallyItem(){return returnStringProperties("MARK_EDIT_ONLY_LOCALLY");}
	public String getScenarResetToDefault(){return returnStringProperties("RESET_TO_DEFAULT");}
	

	public String getScenarioMarkAsKnownIssueMenuItem(){return returnStringProperties("SCENARIO_MENU_MARK_AS_KNOWN_ISSUE");}
	public String getScenarioUnMarkAsKnownIssueMenuItem(){return returnStringProperties("SCENARIO_MENU_UNMARK_AS_KNOWN_ISSUE");}
	
	public String getMarkAsNegativeTestMenuItem(){return returnStringProperties("MENU_MARK_AS_NEGATIVE_TEST");}
	public String getUnMarkAsNegativeTestMenuItem(){return returnStringProperties("MENU_UNMARK_AS_NEGATIVE_TEST");}

	public String getHideInHTMLMenuItem(){return returnStringProperties("MENU_HIDE_IN_HTML");}
	public String getShowInHTMLMenuItem(){return returnStringProperties("MENU_SHOW_IN_HTML");}
	public String getUpdateMeaningfulNameMenuItem(){return returnStringProperties("MENU_UPDATE_MEANINGFUL_NAME");}
	
	public String getScenarioMarkAsTestMenuItem(){return returnStringProperties("SCENARIO_MENU_MARK_AS_TEST");}
	public String getScenarioUnMarkAsTestMenuItem(){return returnStringProperties("SCENARIO_MENU_UNMARK_AS_TEST");}
	public String getRecursiveReferenceCheckBox(){return returnStringProperties("REFERENCE_CHECKBOX");}
	public String getRecursiveRegularCheckBox(){return returnStringProperties("REGULAR_RECURSIVE_CHECKBOX");}
	public String getRecursiveDialogTitle(){return returnStringProperties("RECURSIVE_DIALOG_TITLE");}
	public String getRecursiveDialogApprove(){return returnStringProperties("RECURSIVE_DIALOG_APPROVE");}
	public String getRecursiveDialogReject(){return returnStringProperties("RECURSIVE_DIALOG_REJECT");}
	
	
	// JSystem Properties items
	public String getJSystemPropertiesMenuItem(){return returnStringProperties("DEFINE_JSYSTEM_PROPERTIES");}
	public String getJSystemPropertiesTabPanelName(){return returnStringProperties("JSYSTEM_PROPERTIES_TABBED_PANEL_NAME");}
	public String getJSystemPropertiesContentPanelName(){return returnStringProperties("JSYSTEM_PROPERTIES_CONTENT_PANEL_NAME");}
	public String getUpdateJSystemPropertyValueButtonName(){return returnStringProperties("UPDATE_JSYSTEM_PROPERTIES_VALUE_BUTTON_NAME");}
	public String getJSystemPropertyFileChooserName(){return returnStringProperties("JSYSTEM_PROPERTY_FILE_CHOOSER_NAME");}
	public String getJSystemPropertiesSaveButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_SAVE_BUTTON_NAME");}
	public String getJSystemPropertiesSystemDefaultButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_SYSTEM_DEFAULT_BUTTON_NAME");}
	public String getJSystemPropertiesCancleButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_CANCLE_BUTTON_NAME");}
	public String getListDialogName(){return returnStringProperties("JSYSTEM_PROPERTY_LIST_DIALOG_NAME");}
	public String getJSystemPropertiesConfirmRestartDialogTitle(){return returnStringProperties("JSYSTEM_PROPERTY_CONFIRM_RESTART_DIALOG_TITLE");}
	public String getJSystemPropertiesConfirmRestartYesButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_CONFIRM_RESTART_YES_BUTTON_NAME");}
	public String getJSystemPropertiesConfirmRestartNoButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_CONFIRM_RESTART_NO_BUTTON_NAME");}
	public String getJSystemPropertiesConfirmRestoreDefaultButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_CONFIRM_RESTORE_DEFAULTS_YES_BUTTON_NAME");}
	public String getJSystemPropertiesRestoreDefaultsDiallogTitle(){return returnStringProperties("JSYSTEM_PROPERTY_RESTORE_DEFAULTS_DIALOG_TITLE");}
	public String getJSystemPropertiesUpdateContentButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_UPDATE_CONTENT_BUTTON_NAME");}
	public String getJSystemPropertiesSelectFileButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_SELECT_FILE_BUTTON_NAME");}
	public String getJSystemPropertieslongDescriptionLabel(){return returnStringProperties("JSYSTEM_PROPERTY_LONG_DESCRIPTION_LABLE");}
	public String getListDialogCancelButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_LIST_DIALOG_CANCEL_BUTTON_NAME");}
	public String getListDialogSetButtonName(){return returnStringProperties("JSYSTEM_PROPERTY_LIST_DIALOG_SET_BUTTON_NAME");}
	public String getJSystemPropertiesTableColumnPropertyName(){return returnStringProperties("JSYSTEM_PROPERTY_TABLE_COLUMN_PROPERTY_NAME");}
	public String getJSystemPropertiesTableColumnDescription(){return returnStringProperties("JSYSTEM_PROPERTY_TABLE_COLUMN_DESCRIPTION");}
	public String getJSystemPropertiesTableColumnValue(){return returnStringProperties("JSYSTEM_PROPERTY_TABLE_COLUMN_VALUE");}
	public String getJSystemPropertiesTableColumnDefaultValue(){return returnStringProperties("JSYSTEM_PROPERTY_TABLE_COLUMN_DEFAULT_VALUE");}

	
	/*
	 * View Menu
	 */
	public String getViewTestCodeButton(){return returnStringProperties("VIEW_TEST_CODE");}
	
	/*
	 * Tools Menu
	 */
	public String getInitReportsMenuItem(){return returnStringProperties("INIT_REPORTS");}
	
	public String getShowJarListMenuItem(){return returnStringProperties("SHOW_JAR_LIST");}
	public String getShowMultiScenarioMenuItem(){return returnStringProperties("MULTI_SCENARIO");}
	public String getDefineDatabasePropertiesMenuItem(){return returnStringProperties("DEFINE_DATABASE_PROPERTIES");}
	
	
	/*
	 * DB Properties Dialog
	 */
	public String getDbPropertiesPortTextField(){return returnStringProperties("DB_PROP_PORT_TEXT_FIELD");}
	public String getDbPropertiesPasswordField(){return returnStringProperties("DB_PROP_PASSWORD_FIELD");}
	public String getDbPropertiesTypeJComboBox(){return returnStringProperties("DB_PROP_TYPE_COMBO");}
	public String getDbPropertiesDriverCombobox(){return returnStringProperties("DB_PROP_DRIVER_COMBO");}
	public String getDbPropertiesHostTextField(){return returnStringProperties("DB_PROP_HOST_TEXT_FIELD");}
	public String getDbPropertiesNameTextField(){return returnStringProperties("DB_PROP_NAME_TEXT_FIELD");}
	public String getDbPropertiesUserTextField(){return returnStringProperties("DB_PROP_USER_TEXT_FIELD");}
	public String getDbPropertiesServerIpTextField(){return returnStringProperties("DB_PROP_SERVERIP_TEXT_FIELD");}
	public String getDbPropertiesOkButton(){return returnStringProperties("DB_PROP_OK_BUTTON");}
	
	/*
	 * RemoteAgentUIComponents
	 */
	public String getConnectToAgentButton(){return returnStringProperties("CONNECT_TO_AGENT");}
	public String getInstallNewAgentButton(){return returnStringProperties("INSTALL_NEW_AGENT");}
	public String getAgentStatusButton(){return returnStringProperties("AGENT_STATUS");}
	
	/*
	 * Reports system GUI
	 */
	public String getReportsURL(){return returnStringProperties("REPORTS_URL");}
	public String getReportsFilterURL(){return returnStringProperties("REPORS_FILTER_URL");}
	public String getRunReportButton(){return returnStringProperties("RUN_REPORT");}
	public String getQueryUserCombo(){return returnStringProperties("QUERY_USER_COMBO");}
	public String getQuerySetupCombo(){return returnStringProperties("QUERY_SETUP_COMBO");}
	public String getQueryScenarioCombo(){return returnStringProperties("QUERY_SCENARIO_COMBO");}
	public String getQueryBuildCombo(){return returnStringProperties("QUERU_BUILD_COMBO");}
	public String getQueryTimeCombo(){return returnStringProperties("QUERY_TIME_COMBO");}
	public String getQueryDescriptionField(){return returnStringProperties("QUERY_DESCRIPTION_FIELD");}
	public String getQuerySubmitButton(){return returnStringProperties("QUERY_SUBMIT_BUTTON");}
	public String getCustomReportButton(){return returnStringProperties("CUSTOM_REPORT_BUTTON");}
	public String getRunsSelectorCheckbox(){return returnStringProperties("RUNS_SELECTOR");}
	public String getRunsOptionCombo(){return returnStringProperties("RUNS_OPTIONS_COMBO");}
	public String getRunsDeleteComboOption(){return returnStringProperties("RUNS_DELETE_OPTION");}
	public String getRunsSubmitButton(){return returnStringProperties("RUNS_OPERAION_SUBMIT");}
	public String getReportConfirmMsg(){return returnStringProperties("REPORT_CONFIRM_MSG");}
	public String getRunnAllSelector(){return returnStringProperties("RUN_ALL_TESTS_SELECT");}
	public String getQueryClearButton(){return returnStringProperties("QUERY_CLEAR_BUTTON");}
	public String getCheckBoxControl(){return returnStringProperties("CHECK_BOX_CONTROL");}
	public String getRunTestCompare(){return returnStringProperties("RUN_TEST_COMPARE");}
	public String getReportType(){return returnStringProperties("REPORT_TYPE");}
	public String getGracefulDialog(){return returnStringProperties("DIALOG_GRACEFUL_TITLE");}
	public String getLoadScenarioDialog(){return returnStringProperties("DIALOG_LOAD_SCENARIO_TITLE");}
	public String getStopImmediatelyButton(){return returnStringProperties("DIALOG_STOP_IMMEDIATELY_BUTTON");}
	public String getNumOfTestToAddSpinner(){return returnStringProperties("NUMBER_OF_TESTS_TO_ADD");}
	public String getSaveFailedSequences(){return returnStringProperties("SAVE_FAILED_SEQUENCES");}
	
	/*
	 * Flow control related 
	 */
	public String getLoopButton(){return returnStringProperties("LOOP_BUTTON");}
	public String getSwitchButton(){return returnStringProperties("SWITCH_BUTTON");}
	public String getCaseButton(){return returnStringProperties("CASE_BUTTON");}
	public String getIfButton(){return returnStringProperties("IF_BUTTON");}
	public String getElseIfButton(){return returnStringProperties("ELSEIF_BUTTON");}
	
	
	/*
	 * Source control related
	 */
	public String getConnectToSourceContorl(){return returnStringProperties("CONNECT_TO_SC_BUTTON");}
	public String getCommitScenarioButton(){return returnStringProperties("COMMIT_SCENARIO_BUTTON");}
	public String getAddScenarioButton(){return returnStringProperties("ADD_SCENARIO_BUTTON");}
	public String getUpdateScenarioButton(){return returnStringProperties("UPDATE_SCENARIO_BUTTON");}
	public String getRevertScenarioButton(){return returnStringProperties("REVERT_SCENARIO_BUTTON");}
	public String getScenarioSCStatusButton(){return returnStringProperties("SCENARIO_SC_STATUS_BUTTON");}
	
	public String getCommitSutButton(){return returnStringProperties("COMMIT_SUT_BUTTON");}
	public String getAddSutButton(){return returnStringProperties("ADD_SUT_BUTTON");}
	public String getUpdateSutButton(){return returnStringProperties("UPDATE_SUT_BUTTON");}
	public String getRevertSutButton(){return returnStringProperties("REVERT_SUT_BUTTON");}
	public String getSutSCStatusButton(){return returnStringProperties("SUT_SC_STATUS_BUTTON");}

	/**
	 * This method is used to return properties from the properties file
	 * formated as trimmed strings
	 * @param propertyName
	 * @return
	 * @throws Exception
	 */
	public String returnStringProperties(String propertyName){
			
			try{
				properties.getProperty(propertyName).trim();
			}catch(Exception e){
				log.log(Level.SEVERE,"Property "+propertyName+" was not found in the JsystemMappingFile");
			}

     return properties.getProperty(propertyName).trim();
	}
	/**
	 * This method is used to retrun properties from the properties file
	 * formated as integers 
	 * @param propertyName
	 * @return
	 * @throws Exception
	 */
	
	public int returnIntProperty(String propertyName){
			
			try{
				properties.getProperty(propertyName).trim();
			}catch(Exception e){
				log.log(Level.SEVERE,"Property "+propertyName+" was not found in the JsystemMappingFile");
			}

	  return Integer.parseInt(properties.getProperty(propertyName).trim());
	}


	
}
