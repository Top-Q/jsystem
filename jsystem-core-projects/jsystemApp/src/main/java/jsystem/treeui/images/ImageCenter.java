/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.images;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

/**
 * Used as an Image bank to easy retrieve of images.
 * 
 * User: garieli Date: Jan 14, 2004
 */
public class ImageCenter {

	public static final String ICON_EMPTY = "empty.gif";

	public static final String ICON_CHECK = "check.gif";
	
	public static final String ICON_UNCHECK = "uncheck.gif";

	public static final String ICON_BLUE = "bullet_blue.png";

	public static final String ICON_GREEN = "bullet_green.png";

	public static final String ICON_RED = "bullet_red.png";

	public static final String ICON_SMALL_LOGO = "jsystem_ico.gif";

	public static final String ICON_SMALL_OK = "ok.gif";

	public static final String ICON_SMALL_ERROR = "error.gif";

	public static final String ICON_RUN = "run.gif";

	public static final String ICON_STOP = "stop.gif";

	public static final String ICON_PAUSE = "pause.gif";

	public static final String ICON_REFRESH = "refresh.gif";

	public static final String ICON_NEW = "report.gif";
	
	public static final String ICON_INIT_REPORTS = "init_reports.gif";

	public static final String ICON_REPORTER = "reportsapp.gif";
	
	public static final String ICON_JAR = "jar_obj.gif";

	public static final String ICON_TEST_PASS = "add_correction.gif";

	public static final String ICON_TEST_FAIL = "fail_obj.gif";

	public static final String ICON_PATH = "logical_package_obj.gif";

	public static final String ICON_DIR = "pack_empty_co.gif";

	public static final String ICON_TEST_CASE = "innerclass_public_obj.gif";

	public static final String ICON_UP = "select_prev.gif";

	public static final String ICON_DOWN = "select_next.gif";
	
	public static final String ICON_TO_TOP = "moveToTop.gif";
	
	public static final String ICON_TO_BOTTOM = "moveToBottom.gif";

	public static final String ICON_R_PASS = "r_pass.gif";

	public static final String ICON_R_FAIL = "r_fail.gif";

	public static final String ICON_R_WARNING = "r_warning.gif";

	public static final String ICON_REFRESH_REPORTS = "refresh_nav.gif";

	public static final String ICON_SAVE = "save_edit.gif";

	public static final String ICON_IGNORE = "ignore.gif";

	public static final String ICON_DELETE = "delete_obj.gif";
	
	public static final String ICON_DELETE_SCENARIO = "delete_scenario.gif";

	public static final String ICON_IGNORE_MSG = "ignore_message.gif";

	public static final String ICON_IMPORT = "import.gif";

	public static final String ICON_PUBLISH = "publish.gif";

	public static final String ICON_LOGO = "jsystem_ico.gif";

	public static final String ICON_COMMANT = "addCommant.gif";

	public static final String ICON_CHANGE_TESTS_DIR = "classDir.gif";

	public static final String ICON_FILTER_TESTS_TREE = "filter.gif";
	
	public static final String ICON_SORT_TESTS_TREE = "sort.gif";

	public static final String ICON_FILTER_SUCCESS = "filterFailed.gif";

	public static final String ICON_EDIT = "edit.gif";
	
	public static final String ICON_VIEW_PROCESSED_SUT = "view_processed_sut.gif";

	public static final String ICON_ADD = "addToScenario.gif";

	public static final String ICON_SAVE_AS = "save_as.gif";
	
	public static final String ICON_SAVE_SCENARIO = "save.gif";

	public static final String ICON_CLEAR = "clear.gif";

	public static final String ICON_BUG = "debug.gif";

	public static final String ICON_CODE = "code.gif";

	public static final String ICON_CHANGE_SUT = "changesut.gif";

	public static final String ICON_ADD_IF = "changesut.gif";

	public static final String ICON_GRAPH = "graph.GIF";

	public static final String ICON_EDIT_SCENARIO = "editscenario.gif";

	public static final String ICON_SUSPEND = "suspend.gif";

	public static final String ICON_SELECT_SCENARIO = "scenario.gif";

	public static final String ICON_TEST = "test.gif";

	public static final String ICON_TEST_ERR = "testerr.gif";

	public static final String ICON_TEST_FAILER = "testfail.gif";

	public static final String ICON_TEST_OK = "testok.gif";
	
	public static final String ICON_TEST_WARNING = "testwarning.gif";

	public static final String ICON_TEST_RUN = "testrun.gif";

	public static final String ICON_SCENARIO = "tsuite.gif";

	public static final String ICON_SCENARIO_ERROR = "tsuiteerror.gif";

	public static final String ICON_SCENARIO_FAIL = "tsuitefail.gif";

	public static final String ICON_SCENARIO_OK = "tsuiteok.gif";
	
	public static final String ICON_SCENARIO_WARNING = "tsuiteWarning.gif";

	public static final String ICON_SCENARIO_RUN = "tsuiterun.gif";

	public static final String ICON_SCENARIO_NEW = "new_testsuite.gif";

	public static final String ICON_FOR_LOOP = "loop.gif";
	
	public static final String ICON_DATA_DRIVEN = "data_driven.gif";

	public static final String ICON_IF_CONDITION = "ifCondition.gif";
	
	public static final String ICON_SWITCH = "switch.gif";
	
	public static final String ICON_EXPORT_WIZ = "export_wiz.gif";

	public static final String ICON_IMPORT_WIZ = "import_wiz.gif";

	public static final String ICON_SO_GEN = "sogen.gif";

	public static final String ICON_JSYSTEM = "jsystem_ico.gif";

	public static final String ICON_SUT = "sut.gif";

	public static final String ICON_SUT_FAILED = "";

	public static final String ICON_SUT_ERROR = "SUTFailed.gif";

	public static final String ICON_SUT_PASSED = "SUTPassed.gif";

	public static final String ICON_SUT_RUNNING = "SUTRunning.gif";
	
	public static final String ICON_SUT_EDIT = "SUT_edit.gif";
	
	public static final String ICON_SUT_SAVE = "SUT_Save.gif";

	public static final String ICON_FIXTURE = "fixture.gif";

	public static final String ICON_FIXTURE_RUNNING = "fixtureRunning.gif";

	public static final String ICON_FIXTURE_PASSED = "fixturePassed.gif";

	public static final String ICON_FIXTURE_FAILD = "fixtureFailed.gif";

	public static final String ICON_CURRENT_FIXTURE = "current_fixture.gif";

	public static final String ICON_RUN_TOOLBAR_BG = "runBackground.gif";

	public static final String ICON_TABBES_TOOLBAR_BG = "tabsBackround.gif";

	public static final String ICON_REPORT_FAIL = "reportFail.gif";
	
	public static final String ICON_REPORT_WARNING = "reportWarning.gif";

	public static final String ICON_REPORT_PASS = "reportPass.gif";
	
	public static final String ICON_REPORT_ERROR = "reportError.gif";

	public static final String ICON_TOP_TOOLBAR_BG = "topToolbarBg.gif";

	public static final String ICON_SCEANRIO_TOOLBAR_BG = "scenarioToolbarBg.gif";

	public static final String ICON_BUTTON_BG = "buttonBg.gif";

	public static final String ABOUT_DIALOG_LEFT_IMAGE = "AboutDialogLeftImage.jpg";

	public static final String ICON_TABLE_HEADER = "tableTitle.gif";

	public static final String ABOUT_DIALOG_LOGO = "jsystem_logo.gif";

	public static final String ICON_ERROR = "error_icon.png";

	public static final String ICON_WARNING = "warning_icon.png";

	public static final String ICON_TEST_TREE_BG = "tree_downRightImage.jpg";

	public static final String ICON_SCENARIO_TREE_BG = "tree_downLeftImage.jpg";

	public static final String ICON_CURRENT_TAB = "tab_current.gif";

	public static final String ICON_REGULAR_TAB = "tab_regular.gif";

	public static final String ICON_INFO = "info_icon.png";
	
	public static final String ICON_SETUP = "setup.gif";
	
	public static final String ICON_DEVICE = "device.gif";
	
	public static final String ICON_DEVICE_ARRAY = "device_array.gif";
	
	public static final String ICON_DEVICE_ARRAY_EXTENTION = "device_array_extention.gif";

	public static final String ICON_DEVICE_EXTENTION = "device_extention.gif";
	
	public static final String ICON_DEVICE_PROPERTY = "device_property.gif";
	
	public static final String ICON_DEVICE_MAIN = "device_main.gif";
	
	public static final String ICON_DEVICE_PROPERTY_OPTIONAL = "device_property_optional.gif";
	
	public static final String ICON_SCRIPT_RUN = "script_run.gif";

	public static final String ICON_SCRIPT_OK = "script_ok.gif";
	
	public static final String ICON_SCRIPT_ERR = "script_err.gif";
	
	public static final String ICON_SCRIPT_FAIL = "script_fail.gif";
	
	public static final String ICON_SCRIPT = "script.gif";

	public static final String ICON_REMOTEAGENT_OK = "connection_Yes.gif";
	
	public static final String ICON_REMOTEAGENT_PROBLEM = "connection_No.gif";
	
	public static final String ICON_REMOTEAGENT_NOTCONNECTED = "connection_grey.gif";
	
	public static final String ICON_REMOTEAGENT_INSTALL = "InstallIcon.gif";

	public static final String ICON_REMOTEAGENT_CONNECT = "Set.gif";
	
	public static final String ICON_SCENARIO_AS_TEST = "scenario_as_test.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_RUN = "scenario_as_test_run.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_PASS = "scenario_as_test_pass.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_FAIL = "scenario_as_test_fail.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_WARNING = "scenario_as_test_warning.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_ERROR = "scenario_as_test_err.gif";

	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_ISSUE = "scenarioAstestKnownIssue.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_RUN = "scenario_as_test_run.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_PASS = "scenarioAstestKnownIssueOK.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_WARNING = "scenarioAstestKnownIssueWarning.gif";


	public static final String TEST_KNOWN_ISSUE = "testKnownIssue.gif";
	
	public static final String TEST_KNOWN_ISSUE_RUN = "testKnownIssueRun.gif";
	
	public static final String TEST_KNOWN_ISSUE_PASS = "testKnownIssueOK.gif";
	
	public static final String TEST_KNOWN_ISSUE_WARNING = "testKnownIssueWarning.gif";


	public static final String ICON_NAV_FORWARD = "nav_forward.gif";

	public static final String ICON_NAV_BACKWARD = "nav_backward.gif";
	
	public static final String ICON_REDO_EDIT = "redo_edit.gif";

	public static final String ICON_UNDO_EDIT = "undo_edit.gif";
	
	
	public static final String ICON_SCENARIO_AS_TEST_NEGETIVE = "scenarioAstestNegative.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_NEGETIVE_RUN = "scenarioAstestNegativeRun.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_NEGETIVE_ERROR = "scenarioAstestNegativeError.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_NEGETIVE_FAIL = "scenarioAstestNegativeFail.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_NEGETIVE_PASS = "scenarioAstestNegativeOk.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_NEGETIVE_WARNING = "scenarioAstestNegativeWarning.gif";
	
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE = "scenarioAstestKnownAndNegative.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_RUN = "scenarioAstestKnownAndNegativeRun.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_PASS = "scenarioAstestKnownAndNegativeOk.gif";
	
	public static final String ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_WARNING = "scenarioAstestKnownAndNegativeWarning.gif";
	
	
	
	public static final String ICON_TEST_NEGETIVE = "testNegative.gif";
	
	public static final String ICON_TEST_NEGETIVE_RUN = "testNegativeRun.gif";
	
	public static final String ICON_TEST_NEGETIVE_ERROR = "testNegativeError.gif";
	
	public static final String ICON_TEST_NEGETIVE_FAIL = "testNegativeFail.gif";
	
	public static final String ICON_TEST_NEGETIVE_PASS = "testNegativeOk.gif";
	
	public static final String ICON_TEST_NEGETIVE_WARNING = "testNegativeWarning.gif";
	
	
	public static final String ICON_TEST_KNOWN_AND_NEGETIVE = "testKnownAndNegative.gif";
	
	public static final String ICON_TEST_KNOWN_AND_NEGETIVE_RUN = "testKnownAndNegativeRun.gif";
	
	public static final String ICON_TEST_KNOWN_AND_NEGETIVE_PASS = "testKnownAndNegativeOk.gif";
	
	public static final String ICON_TEST_KNOWN_AND_NEGETIVE_WARNING = "testKnownAndNegativeWarning.gif";
	
	
	/*
	 * Source Control Related
	 */
	public static final String ICON_SC_COMMIT="commit.gif";
	
	public static final String ICON_SC_UPDATE="update.gif";
	
	public static final String ICON_SC_ADD = "add_correction.gif";
	
	public static final String ICON_SC_REVERT="revert.gif";
	
	public static final String ICON_SC_SCENARIO_COMMITED="scenario_commited.gif";
	
	public static final String ICON_SC_SCENARIO_ADDED="scenario_added.gif";
	
	public static final String ICON_SC_SCENARIO_NOT_SYNC="scenario_not_sync.gif";
	
	public static final String ICON_SC_SCENARIO_NOT_VERSIONED="scenario_not_versioned.gif";
	
	public static final String ICON_SC_SUT_COMMITED="sut_commited.gif";
	
	public static final String ICON_SC_SUT_ADDED="sut_added.gif";
	
	public static final String ICON_SC_SUT_NOT_SYNC="sut_not_sync.gif";
	
	public static final String ICON_SC_SUT_NOT_VERSIONED="sut_not_versioned.gif";

	
	
	public static final String MENU_ICON_NEGETIVE = "negetiveTestIcon.gif";
	
	public static final String MENU_ICON_CANCEL_NEGETIVE = "cancelNegetiveTestIcon.gif";
	
	public static final String MENU_ICON_KNOWN_ISSUE = "knownIssueIcon.gif";
	
	public static final String MENU_ICON_CANCEL_KNOWN_ISSUE = "cancelKnownIssueIcon.gif";
	
	public static final String MENU_ICON_CUT_TESTS = "cutTests.gif";
	public static final String MENU_ICON_COPY_TESTS = "copyTests.gif";
	public static final String MENU_ICON_PASTE_TESTS = "pasteTests.gif";
	
	private static final String[] iconList = { ICON_EMPTY, ICON_BLUE, ICON_GREEN, ICON_RED,
			ICON_SMALL_LOGO, ICON_SMALL_OK, ICON_SMALL_ERROR, ICON_RUN, ICON_STOP, ICON_PAUSE, ICON_REFRESH, ICON_NEW,
			ICON_JAR, ICON_TEST_PASS, ICON_TEST_FAIL, ICON_PATH, ICON_DIR, ICON_TEST_CASE, ICON_UP, ICON_DOWN, ICON_TO_TOP, ICON_TO_BOTTOM,
			ICON_R_PASS, ICON_R_FAIL, ICON_R_WARNING, ICON_REFRESH_REPORTS, ICON_SAVE, ICON_IGNORE, ICON_DELETE,ICON_DELETE_SCENARIO,
			ICON_IGNORE_MSG, ICON_IMPORT, ICON_PUBLISH, ICON_LOGO, ICON_COMMANT, ICON_CHANGE_TESTS_DIR,
			ICON_FILTER_SUCCESS, ICON_FILTER_TESTS_TREE,ICON_SORT_TESTS_TREE, ICON_EDIT, ICON_VIEW_PROCESSED_SUT,
			ICON_ADD, ICON_SAVE_AS,ICON_SAVE_SCENARIO, ICON_CLEAR, ICON_BUG,
			ICON_CODE, ICON_CHANGE_SUT, ICON_GRAPH, ICON_EDIT_SCENARIO, ICON_SUSPEND,
			ICON_SELECT_SCENARIO, ICON_TEST, ICON_TEST_ERR, ICON_TEST_FAILER, ICON_TEST_OK, ICON_TEST_RUN,
			ICON_SCENARIO, ICON_SCENARIO_ERROR, ICON_SCENARIO_FAIL, ICON_SCENARIO_OK, ICON_SCENARIO_RUN,
			ICON_SCENARIO_NEW, ICON_EXPORT_WIZ, ICON_IMPORT_WIZ, ICON_SO_GEN, ICON_JSYSTEM, ICON_SUT, ICON_SUT_FAILED,
			ICON_SUT_PASSED, ICON_SUT_RUNNING, ICON_SUT_EDIT, ICON_SUT_SAVE, ICON_SUT_ERROR, ICON_FIXTURE, ICON_RUN_TOOLBAR_BG,
			ICON_TABBES_TOOLBAR_BG, ICON_REPORT_FAIL, ICON_REPORT_PASS, ICON_TOP_TOOLBAR_BG, ICON_SCEANRIO_TOOLBAR_BG,
			ICON_BUTTON_BG, ABOUT_DIALOG_LEFT_IMAGE, ICON_TABLE_HEADER, ABOUT_DIALOG_LOGO, ICON_CURRENT_FIXTURE,
			ICON_ERROR, ICON_TEST_TREE_BG, ICON_FIXTURE_FAILD, ICON_FIXTURE_PASSED, ICON_FIXTURE_RUNNING,
			ICON_SCENARIO_TREE_BG, ICON_WARNING, ICON_INFO, ICON_SETUP, ICON_DEVICE, ICON_DEVICE_ARRAY, 
			ICON_DEVICE_ARRAY_EXTENTION, ICON_DEVICE_EXTENTION, ICON_DEVICE_PROPERTY, ICON_DEVICE_MAIN,
			ICON_DEVICE_PROPERTY_OPTIONAL,ICON_SCRIPT_RUN, ICON_SCRIPT_OK, ICON_SCRIPT_ERR, 
			ICON_SCRIPT_FAIL, ICON_SCRIPT,ICON_REPORTER,ICON_REMOTEAGENT_OK,ICON_REMOTEAGENT_PROBLEM,ICON_REMOTEAGENT_NOTCONNECTED,ICON_REMOTEAGENT_INSTALL,ICON_REMOTEAGENT_CONNECT,ICON_CHECK,
			ICON_TEST_WARNING, ICON_SCENARIO_WARNING, ICON_REPORT_ERROR, ICON_REPORT_WARNING, ICON_FOR_LOOP, ICON_DATA_DRIVEN, ICON_IF_CONDITION, ICON_SWITCH,ICON_INIT_REPORTS,
			ICON_SCENARIO_AS_TEST,ICON_SCENARIO_AS_TEST_RUN,ICON_SCENARIO_AS_TEST_PASS,ICON_SCENARIO_AS_TEST_FAIL,ICON_SCENARIO_AS_TEST_WARNING, ICON_SCENARIO_AS_TEST_ERROR,ICON_NAV_FORWARD, ICON_NAV_BACKWARD,
			ICON_REDO_EDIT, ICON_UNDO_EDIT,ICON_SCENARIO_AS_TEST_KNOWN_ISSUE,ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_RUN,ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_PASS,ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_WARNING,
			TEST_KNOWN_ISSUE,TEST_KNOWN_ISSUE_RUN,TEST_KNOWN_ISSUE_PASS,TEST_KNOWN_ISSUE_WARNING,
			ICON_TEST_NEGETIVE,ICON_TEST_NEGETIVE_RUN,ICON_TEST_NEGETIVE_ERROR,ICON_TEST_NEGETIVE_FAIL,
			ICON_TEST_NEGETIVE_PASS,ICON_TEST_NEGETIVE_WARNING,
			ICON_SCENARIO_AS_TEST_NEGETIVE,ICON_SCENARIO_AS_TEST_NEGETIVE_RUN,ICON_SCENARIO_AS_TEST_NEGETIVE_ERROR,
			ICON_SCENARIO_AS_TEST_NEGETIVE_FAIL,ICON_SCENARIO_AS_TEST_NEGETIVE_PASS,ICON_SCENARIO_AS_TEST_NEGETIVE_WARNING,
			ICON_TEST_KNOWN_AND_NEGETIVE,ICON_TEST_KNOWN_AND_NEGETIVE_RUN,ICON_TEST_KNOWN_AND_NEGETIVE_PASS,ICON_TEST_KNOWN_AND_NEGETIVE_WARNING,
			ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE,ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_PASS,ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_RUN,ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_WARNING,
			MENU_ICON_NEGETIVE,MENU_ICON_KNOWN_ISSUE,MENU_ICON_CANCEL_KNOWN_ISSUE,MENU_ICON_CANCEL_NEGETIVE,ICON_UNCHECK,MENU_ICON_CUT_TESTS,MENU_ICON_COPY_TESTS,MENU_ICON_PASTE_TESTS,
			ICON_SC_COMMIT,ICON_SC_REVERT,ICON_SC_UPDATE,ICON_SC_SCENARIO_COMMITED,ICON_SC_SCENARIO_ADDED,ICON_SC_SCENARIO_NOT_VERSIONED,ICON_SC_SCENARIO_NOT_SYNC,ICON_SC_SUT_COMMITED,ICON_SC_SUT_ADDED,ICON_SC_SUT_NOT_VERSIONED,ICON_SC_SUT_NOT_SYNC};

	private static ImageCenter imageCenter = null;

	private static final String IMAGE_DIR = "jsystem/treeui/images/";

	private HashMap<String, ImageIcon> iconMap = new HashMap<String, ImageIcon>();

	public static ImageCenter getInstance() {
		if (imageCenter == null) {
			imageCenter = new ImageCenter();
		}
		return imageCenter;
	}

	private ImageCenter() {
		ImageIcon icon = null;
		ClassLoader c = this.getClass().getClassLoader();
		// add the icons to the hash
		for (int i = 0; i < iconList.length; i++) {
			URL url = c.getResource(IMAGE_DIR + iconList[i]);
			if (url == null) {
				continue;
			}
			icon = new ImageIcon(url);
			iconMap.put(iconList[i], icon);
		}
	}

	public Image getAwtImage(String name) {
		ClassLoader c = this.getClass().getClassLoader();

		URL url = c.getResource(IMAGE_DIR + name);

		return Toolkit.getDefaultToolkit().getImage(url);

	}

	public URL getImageUrl(String name) {
		return this.getClass().getClassLoader().getResource(IMAGE_DIR + name);
	}

	public ImageIcon getImage(String name) {
		return (ImageIcon) iconMap.get(name);
	}
}
