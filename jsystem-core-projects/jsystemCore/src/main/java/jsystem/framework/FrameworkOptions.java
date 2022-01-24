/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import jsystem.framework.GeneralEnums.CmdExecutor;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.scenario.PresentationDefinitions.ParametersOrder;

/**
 * Hold all the options of the jsystem framework. It's used by JSystemProperties
 * in getPreferance.
 * 
 * @author guy.arieli
 * 
 * Parameters:
 * String string - the name of the property
 * GROUP group - The name of the Tas that holds this property in the JSystem properties dialog
 * String description - Short description for this specific property
 * TYPE dataType - an Enumerator describe the editor to be use in order to edit the 
 * 		property value in the jsystem property dialog 
 * boolean - requierReloadRunner - If true, after changing this property via the dialog, 
 * 		the runner will be reloaded
 * String[] reserve - An array of strings that hold extra information for some of the properties:
 * 		- In case the property contain a fixed number of possible values, these values are hold 
 * 			in the reserve array
 * 		- In case we need to search classes in the system, this array holds the names of the classes 
 * 			to be search, when the user press the update button in the "jsystem property dialog"
 */
public enum FrameworkOptions {

	/**
	 * tag for the status of the logger (can be set to disable) can be
	 */
	LOGGER_STATUS(
			"logger", 
			"Tag - status of the logger", 
			DataType.BOOLEAN, 
			true,
			false),

	/**
	 * tag for the tests classes folder
	 */
	TESTS_CLASS_FOLDER(
			"tests.dir", 
			"Tests classes folder (binaries)", 
			DataType.DIRECTORY, 
			"./classes",
			false),

	/**
	 * tag for the tests source folder
	 */
	TESTS_SOURCE_FOLDER(
			"tests.src", 
			"Tests source folder (source code)", 
			DataType.DIRECTORY, 
			"./tests",
			false),

	/**
	 * tag for Maven resources folder. For Ant projects it will hold the same value as TESTS_SOURCE_FOLDER
	 */
	RESOURCES_SOURCE_FOLDER(
			"resources.src", 
			"Resources source folder (support for Maven structured projects)", 
			DataType.DIRECTORY, 
			"./src/main/resources",
			false),

	/**
	 * tag for the log folder
	 */
	LOG_FOLDER(
			"htmlReportDir", 
			"Html logs folder", 
			DataType.DIRECTORY,
			"./Log",
			false),


	/**
	 * Tag for the command to execute excel
	 */
	EXCEL_COMMAND(
			"excel.command", 
			"Excel Scenario execution command file", 
			DataType.FILE, 
			"",
			false),

	/**
	 * Directory to zip old html reports (the default is old folder)
	 */
	HTML_OLD_DIRECTORY(
			"html.dir.old", 
			"Old html reports zip directory", 
			DataType.DIRECTORY, 
			"./Old",
			false),

	/**
	 * Disable the zip of html report on system exit
	 */
	HTML_ZIP_DISABLE(
			"html.zip.disable", 
			"Html report backup (zipping) disable", 
			DataType.BOOLEAN, 
			false,
			false),


	/**
	 * Set the tree structure of the old html zip folders
	 */
	HTML_OLD_PATH(
			"old.path",
			"old html zip folders tree structure settings",
			DataType.DIRECTORY, 
			"",
			false),

	/**
	 * If set to true will save the zip file only in the tree
	 */
	HTML_ZIP_TREE_ONLY(
			"html.tree", 
			"Old Html zip folder structure form", 
			DataType.BOOLEAN, 
			false,
			false),

	/**
	 * A default css to use for the HTML instead of the JSystem default one
	 */
	HTML_CSS_PATH(
			"css.path", 
			"Html report css path", 
			DataType.TEXT, 
			"jsystem/extensions/report/html/resources/default.css",
			false),

	/**
	 * The color for the warning in the html report (default: FF6600)
	 */
	HTML_LOG_PARAMS_IN_LEVEL(
			"log.parameters.in.level", 
			"Signals the JSystem reports to log parameters in a seperate level", 
			DataType.BOOLEAN, 
			false,
			false),

	HTML_PACKAGE_LIST (
			"html.package.list",
			"Points to a properties file which contains the links that will be shown in the upper left area of the html report",
			DataType.TEXT,
			false,
			false
			),

	/**
	 * Disable the summary report
	 */
	HTML_SUMMARY_DISABLE(
			"summary.disable", 
			"Disable the summary report", 
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * Disable the summary report
	 */
	SAVE_REPORTERS_ON_RUN_END(
			"save.reporters.on.run.end", 
			"When set to true, reporters (HTML,XML,summary) are saved only on run end and before publish.", 
			DataType.BOOLEAN,
			false,
			false),

	SAVE_REPORTERS_INTERVAL(
			"flush.reporters.interval",
			"How often to flush reports when save.reporters.on.run.end is turned on in seconds",
			DataType.NUMERIC,
			10,
			false),


	/**
	 * A list of shutdown thread (will be executed on system exit)
	 */
	SHUTDOWN_THREADS(
			"shutdown.threads", 
			"A list of shutdown threads", 
			DataType.SEARCH_AND_MULTY_SELECT_LIST, 
			"DefaultShutdownHook",
			false, 
			new String[] { "java.lang.Thread" }),

	/**
	 * Add time stamp to html report
	 */
	HTML_ADD_TIME(
			"reporter.addtime",
			"Add time stamp to the html report",
			DataType.BOOLEAN,
			true,
			true),

	/**
	 * List of the reporters classes
	 */
	REPORTERS_CLASSES(
			"reporter.classes", 
			"The list of reporters classes", 
			DataType.SEARCH_AND_MULTY_SELECT_LIST, 
			"",
			true, 
			new String[] { "jsystem.framework.report.TestReporter" }),

	REPORTER_DELETE_CURRENT(
			"reporter.delete.current", 
			"Delete current report when init the reports", 
			DataType.BOOLEAN, 
			"",
			true 
			),


	/**
	 * Set the scenario editor
	 */
	SCENARIO_EDITOR(
			"scenario.editor", 
			"The external scenario editor program", 
			DataType.DIRECTORY, 
			"",
			false),

	SCENARIO_NAME_HOOK (
			"scenario.name.hook",
			"A hook for manipulating scenario name",
			DataType.SEARCH_AND_LIST, 
			"",
			false, 
			new String[]{"jsystem.extensions.scenarionamehook.ScenarioNameHook"}),


	CACHE_SCENARIO_PROPERTIES (
			"cache.scenario.properties",
			"Enables the scenarios properties caching mechanis. " +
					"NOTICE: This is an experimental feature, use it on your own risk.  ",
					DataType.BOOLEAN,
					false,
					false),

	HALT_ON_SCENARIO_ERROR (
			"halt.on.scenario.error",
			"",
			DataType.BOOLEAN, 
			false,
			false),


	/**
	 * XML editor
	 */
	XML_EDITOR(
			"xml.editor", 
			"The editor for xml files (sut)", 
			DataType.SEARCH_AND_LIST, 
			"",
			false, 
			new String[] { "Search criteria for XML editor" }),


	/**
	 * Used SUT file
	 */
	USED_SUT_FILE(
			"sutFile",
			"Used SUT file",
			DataType.BOOLEAN,
			"",
			false),

	/**
	 * SUT class name
	 */
	SUT_CLASS_NAME(
			"sutClassName",
			"SUT class name",
			DataType.SEARCH_AND_LIST,
			"SutImpl",
			false),

	/**
	 * Directory of the SUT files
	 */
	SUT_DIR(
			"sut.dir",
			"SUT files directory",
			DataType.DIRECTORY,
			"",
			false),

	/**
	 * Freeze on fail
	 */
	FREEZE_ON_FAIL(
			"freeze.fail", 
			"Freeze on test failure", 
			DataType.BOOLEAN, 
			false,
			true),


	/**
	 * Html browser name
	 */
	HTML_BROWSER(
			"browser", 
			"Html browser name", 
			DataType.FILE, 
			"",
			false),


	/**
	 * List of packages to exclude on class loading
	 */
	LOAD_EXCLUDE(
			"load.exclude", 
			"Packages to exclude on class loading", 
			DataType.TEXT, 
			"",
			false),


	/**
	 * List of packages to include on class loading
	 */
	LOAD_INCLUDE(
			"load.include", 
			"Packages to include on class loading", 
			DataType.TEXT, 
			"",
			false),

	/**
	 * parameters for the test vm
	 */
	TEST_VM_PARMS(
			"test.vm.params", 
			"Test Parameters for the VM command line", 
			DataType.TEXT,
			"",
			false),


	/**
	 * Execute the tests in debug mode
	 */
	TESTS_DEBUG(
			"test.debug",
			"Execute the tests in debug mode",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * Lib dirs
	 */
	LIB_DIRS(
			"lib.dirs", 
			"Library search path", 
			DataType.DIRECTORY, 
			"./Lib",
			false),


	/**
	 * disable loader
	 */
	LOADER_DISABLE(
			"loader.disable", 
			"Disable internal classloader", 
			DataType.BOOLEAN, 
			false,
			false),

	/**
	 * run mode
	 */
	RUN_MODE(
			"run.mode", 
			"Run mode", 
			DataType.LIST,
			"Drop every run",
			false,
			new String[] { RunMode.DROP_EVERY_RUN.toString(),RunMode.DROP_EVERY_TEST.toString(),RunMode.DROP_EVERY_SCENARIO.toString()}),

	/**
	 * disable loader
	 */
	RUN_BACKGROUND(
			"run.background", 
			"Run in background mode (no po-pup messages, etc.)", 
			DataType.BOOLEAN, 
			false,
			false),

	/**
	 * SUT editor
	 */
	SUT_EDITOR(
			"sut.editor", 
			"SUT editor", 
			DataType.FILE, 
			"",
			false),


	/**
	 * The name of the file to add the std.out to
	 */
	STDOUT_FILE_NAME(
			"stdout.file.name", 
			"Print standard output to file", 
			DataType.FILE,
			"",
			false),

	STDOUT_FILE_APPEND(
			"stdout.file.append", 
			"Is appened to output file", 
			DataType.BOOLEAN,
			false,
			false),


	/**
	 * Disable the print to the console
	 */
	CONSOLE_DISABLE(
			"console.disable", 
			"Disable printing to the console", 
			DataType.BOOLEAN,
			false,
			false),


	/**
	 * Repeat
	 */
	REPEAT_ENABLE(
			"repeat",
			"Check Repeat checkbox",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * edit sub scenarios properties
	 */
	SUB_SCENARIO_EDIT(
			"subscenarios.edit",
			"Edit sub scenarios properties",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * Disable the search for scenario in old format
	 */
	DISABLE_OLD_SCENARIO_CONVERT(
			"convert.old.scenarios", 
			"Disable searching for scenarios in the old format",
			DataType.LIST,
			"true",
			false,
			new String[] { "true","false","never","never-5.1"}),


	/**
	 * if true, enables the option to see fixtures on test tree.
	 */
	FIXTURES_ON_TEST_TREE(
			"fixtures.showOnTree",
			"Display fixtures in the tests tree.",
			DataType.BOOLEAN,
			true,
			false),

	/**
	 * where the scenario editor (spreadsheet app) is located
	 */
	SCENARIO_EDITOR_LOCATION(
			"scenario.editor.location", 
			"Scenario editor (spreadsheet app) location", 
			DataType.DIRECTORY, 
			"",
			false),


	/**
	 * the name of the application to run.
	 */
	SCENARIO_EDITOR_APP(
			"scenario.editor.app", 
			"The external scenario editor application", 
			DataType.FILE, 
			"",
			false),


	/**
	 * Set the maxtimeout to wait on system exit were -1 is infinit, 0 is
	 * imidiatly
	 */
	EXIT_TIMEOUT(
			"exit.timeout", 
			"Timeout for waiting on system exit", 
			DataType.NUMERIC, 
			"",
			false),


	/**
	 * List of used scripts engine
	 */
	SCRIPT_ENGINES(
			"script.engines", 
			"List of used script engines", 
			DataType.SEARCH_AND_LIST,
			"",
			false, 
			new String[] { "Search criteria for scripts engine" }),


	/**
	 * Demo feature enabled
	 */
	DEMO_ENABLE(
			"demo.enable", 
			"Demo feature enabled", 
			DataType.BOOLEAN, 
			false,
			false),

	/**
	 * If set to true, meaningful name will be ignored in scenario tree
	 */
	IGNORE_MEANINGFUL_NAME(
			"ignoreMeaningfulName", 
			"Ignore test meaningful name", 
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * see description in GUIFrameworkOptions
	 */
	SCENARIO_AS_TEST(
			"enable.mark.scenario.as.test", 
			"Enable marking scenario as test", 
			DataType.BOOLEAN,
			true,
			false),
	/**
	 * see description in GUIFrameworkOptions
	 */
	RESTORE_DEFAULTS(
			"enable.restore.parameters.defaults", 
			"Enables the restore to default menu item", 
			DataType.BOOLEAN,
			false,
			false),


	/**
	 * current scenario
	 */

	CURRENT_SCENARIO(
			"currentScenario",
			"Current scenario",
			DataType.FILE,
			"",
			false),

	/**
	 * mail host server, jsystem using it to send email
	 */
	MAIL_HOST(
			"mail.host",
			"Mail SMTP host server",
			DataType.TEXT,
			"",
			false),

	/**
	 * the account name of the user that sends the mail
	 */
	MAIL_FROM_ACCOUNT_NAME(
			"mail.from",
			"The Sender's user account mail",
			DataType.TEXT,
			"",
			false),

	/**
	 * the user name of the sending mail
	 */
	MAIL_FROM_USER_NAME(
			"mail.user",
			"The Sender's user account name",
			DataType.TEXT,
			"",
			false),

	/**
	 * the password of the sending account
	 */
	MAIL_FROM_PASSWORD(
			"mail.password",
			"The Sender's user account password",
			DataType.PASSWORD,
			"",
			false),

	/**
	 * the SMTP port to send mail from
	 */
	MAIL_SMTP_PORT(
			"mail.port",
			"The Sender's SMTP port",
			DataType.NUMERIC,
			"",
			false),

	/**
	 * is the mail secured (uses ssl?)
	 */
	MAIL_SSL(
			"mail.ssl",
			"Is the Mail connection secured?",
			DataType.BOOLEAN,
			"",
			false),

	/**
	 * send mail list (separated by ";")
	 */
	MAIL_SEND_TO(
			"mail.sendTo",
			"Send To mail list",
			DataType.MAIL_LIST,
			"",
			false),

	/**
	 * the mail subject
	 */
	MAIL_SUBJECT(
			"mail.subject",
			"Mail subject",
			DataType.TEXT,
			"",
			false),
	/**
	 * the mail header
	 */
	MAIL_HEADER(
			"mail.header",
			"Mail header",
			DataType.TEXT,
			"",
			false),

	/**
	 * file attachments list (separated by ";")
	 */
	MAIL_ATTACHMENTS(
			"mail.attachments",
			"File attachments list",
			DataType.TEXT,
			"",
			false),

	/**
	 * List of agents that the runner application worked with.
	 */
	AGENT_LIST(
			"agent.client.list",
			"List of agents",
			DataType.SEARCH_AND_MULTY_SELECT_LIST,
			"",
			false),


	/**
	 * Runner agent id
	 */
	AGENT_ID("agent.server.id", "JRunner agent id", DataType.TEXT, "", false),
	/**
	 * runner agent web port
	 */
	AGENT_WEB_PORT("agent.server.web.port", "JRunner agent web port",
			DataType.NUMERIC, 8383, false),

	/**
	 * runner engine ftp port
	 */
	AGENT_FTP_PORT(
			"agent.server.ftp.port",
			"JRunner engine ftp port",
			DataType.NUMERIC,
			2121,
			false),

	/**
	 * Allow user to change agent during run.
	 */
	CHANGE_AGENT_DURING_RUN(
			"allow.change.agent.during.run",
			"Allow changing of the agents during Execution",
			DataType.BOOLEAN,
			"",
			true),

	/**
	 * runner distributed execution plug-in
	 */
	DISTRIBUTED_EXECUTION_PLUGIN(
			"distributed.execution.plugin",
			"Distributed execution plug-in class",
			DataType.SEARCH_AND_LIST,
			"jsystem.runner.agent.clients.DefaultDistributedExecutionPlugin",
			false,
			new String[]{"jsystem.framework.distributedexecution.DistributedExecutionPlugin"}),

	/**
	 * runner distributed execution plug-in
	 */
	AGENT_NOTIFICATION_LEVEL(
			"agent.notification.level",
			"The level of the notifications that are sent by the agent. Can ",
			DataType.LIST,
			"ALL",
			false,
			new String[]{"ALL","ALL_ONLY_TITLE","NO_REPORT","NO_FAIL","NO_TEST_INDICATION"}),

	/**
	 * flag which signals the system to ignore distributed execution.
	 */
	IGNORE_DISTRIBUTED_EXECUTION(
			"ignore.distributed.execution",
			"Ignore distributed execution",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * flag which signals the system the synchronize agents without opening a dialog box.			
	 */
	AGENT_AUTO_SYNC(
			"agent.auto.sync",
			"Synchronize agent automatically",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * Limit the number of building blocks in a scenario
	 */
	MAX_BUILDING_BLOCKS_NUMBER(
			"max.building.blocks.number",
			"Limit the number of building blocks in a scenario",
			DataType.NUMERIC,
			500,
			false),		

	/**
	 * Tag for the command to execute excel
	 */
	CUSTOMER_PRODUCT(
			"customer.product", 
			"Customer products versions", 
			DataType.TEXT, 
			"",
			false),	

	/**
	 * if True then SystemObjects will be located and used by the sut planner Action
	 */
	SUT_PLANNER(
			"sut.planner", 
			"Sut Planner enable", 
			DataType.BOOLEAN, 
			false,
			false),
	/**
	 * If set to false will disable the display of test code in the report
	 */
	TEST_CODE_ENABLE(
			"test.code.enable",
			"Show the test code in the report",
			DataType.BOOLEAN,
			true,
			true
			),
	/**
	 * When set to false the default value and current value of parameter will
	 * not be displayed as part of the parameter options. Every test parameter
	 * has default and current values. This property is relevant only in a state
	 * were the parameters has Options. In this case by default the default and
	 * current parameter value is added to the option (if not already included.
	 * By changing this property to false no values will be added to the
	 * parameter options
	 */
	ADD_DEFAULTS_CURRENT_TO_PARAM(
			"add.default.param.option",
			"Add default parameter value and current value to the parameter options (if not exist)",
			DataType.BOOLEAN,
			true,
			true
			),


	SCENARIO_STATE_LISTENER (
			"scenario.state.listener",
			"",
			DataType.SEARCH_AND_LIST, 
			"jsystem.undoredo.UserActionManager",
			true, 
			new String[] { "jsystem.undoredo.UserActionManager" }),

	PLANNER_JARS_INCLUDE (
			"planner.jars.include",
			"",
			DataType.TEXT,
			"",
			false
			),

	SCEANRIO_AS_TEST_TERMINATE_ON_FAIL (
			"scenario.as.test.fail.terminate",
			"Terminate Scenario as Test if any test fails",
			DataType.BOOLEAN,
			true,
			false
			),

	FILTER_SUT_IN_ASSETS_TREE(
			"filter.sut.tree",
			"",
			DataType.BOOLEAN,
			"",
			false
			),

	TESTS_JAR_NAME_PREFIX(
			"tests.jar.name.prefix",
			"The name prefix of jar files that will be scanned for building blocks",
			DataType.TEXT,
			"so-",
			true
			),

	SORT_ASSETS_TREE(
			"sort.tests.tree",
			"",
			DataType.BOOLEAN,
			"",
			false
			),

	/**
	 * confirmation dialog on unsaved changes
	 */
	AUTO_SAVE_NO_CONFIRMATION(
			"auto.save.no.confirmation",
			"save unsaved changed without asking for confirmation",
			DataType.BOOLEAN,
			false,
			false),

	//=====================================================================
	/**
	 * interval time set between auto saving of scenario.
	 */
	AUTO_SAVE_INTERVAL(
			"auto.save.interval",
			"the time in seconds to wait before automatically saving changes\n"+
					"only supports interval change from the UI jsystem.properties window",
					DataType.NUMERIC,
					0,
					false
			),

	//=====================================================================			
	/**
	 * confirmation dialog on delete item
	 */
	AUTO_DELETE_NO_CONFIRMATION(
			"auto.delete.no.confirmation",
			"delete item without asking for confirmation",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * last export wizard zip file
	 */
	LAST_EXPORT_FILE(
			"last.export.file",
			"Last zip file to which export project was performed",
			DataType.FILE,
			false,
			false),

	/**
	 * last import wizard zip file
	 */
	LAST_IMPORT_FILE(
			"last.import.file",
			"Last zip file from which import project was performed",
			DataType.FILE,
			false,
			false),

	/**
	 * SUT reader for handling sut
	 */
	SUT_READER_CLASS(
			"sut.reader.class", 
			"Sut reader to use for Sut parsing", 
			DataType.SEARCH_AND_LIST, 
			"",
			true, 
			new String[] { "jsystem.framework.sut.SutReader" }),



	/**
	 * Order of parameters on parameter panel
	 */
	PARAMETERS_ORDER_DEFAULT(
			"parameters.order.default",
			"Parameters panel default order",
			DataType.LIST,
			"defaultOrder",
			true,
			new String[] { ParametersOrder.defaultOrder.toString(),ParametersOrder.ascending.toString(),ParametersOrder.descending.toString()}),

	/**
	 * Allow editing in runner
	 */
	RUNNER_ALOW_EDIT(
			"runner.allow.edit",
			"Allow editing in runner",
			DataType.BOOLEAN,
			false,
			true),

	/**
	 * Disable stop button
	 */
	RUNNER_DISABE_STOP(
			"runner.disable.stop_button",
			"Disable stop button",
			DataType.BOOLEAN,
			false,
			false),

	/**
	 * Indicates whether run properties should be saved between at the beginning of 
	 * each new execution
	 */
	SAVE_RUN_PROPERTIES(
			"save.run.properties.before.execution",
			"Indicates whether run properties should be saved before execution",
			DataType.BOOLEAN,
			false,
			false),

	CMD_LINE_EXECUTER (
			"cmd.line.executor",
			"Type of command line executor",
			DataType.LIST,
			CmdExecutor.ADVANCED_EXECUTOR.toString(),
			false, 
			new String[]{CmdExecutor.SIMPLE_EXECUTOR.toString(), CmdExecutor.ADVANCED_EXECUTOR.toString()}),		

	/**
	 * Set to true in order to move all tests properties from XML file to properties file before loading the Scenario		
	 */
	MOVE_PARAMS_FROM_XML_TO_PROP_ON_SCENARIO_LOAD(
			"move.params.to.param.file",
			"Should params move from XML to Properties",
			DataType.BOOLEAN,
			false,
			false),

	CONTEXT_MENU_PLUGIN_CLASSES(
			"context.menu.plugin.classes",
			"List of classes of context menu plugins",			
			DataType.SEARCH_AND_MULTY_SELECT_LIST, 
			"",
			true, 
			new String[] { "jsystem.treeui.teststable.ContextMenuPlugin" }),


	SCM_PLUGIN_CLASS(
			"scm.class",
			"Class name of the SCM plugin",			
			DataType.SEARCH_AND_LIST, 
			"",
			true, 
			new String[] { "jsystem.extensions.sourcecontrol.SourceControlI" }),


	SCM_REPOSITORY(
			"scm.repoistory",
			"Repository of SCM",			
			DataType.TEXT,
			"",
			false),

	SCM_USER("scm.user",
			"SCM user",
			DataType.TEXT,
			"",
			false),

	SCM_PASSWORD("scm.password",
			"SCM password",
			DataType.TEXT,
			"",
			false),

	GENERIC_TABS(
			"add.generic.tabs",
			"Add tabs",
			DataType.SEARCH_AND_MULTY_SELECT_LIST,
			"",
			true,
			new String[] {"jsystem.treeui.interfaces.JSystemTab"}),

	DATA_PROVIDER_CLASSES(
			"data.provider.classes",
			"Data driven data provider classes",
			DataType.SEARCH_AND_MULTY_SELECT_LIST,
			"",
			false,
			new String[] {"jsystem.framework.scenario.flow_control.datadriven.DataProvider"}
			),

	/**
	 * Publisher class to user for publishing reports
	 */
	REPORTS_PUBLISHER_CLASS(
			"publisher.class", 
			"Publisher class to user for publishing reports", 
			DataType.SEARCH_AND_LIST, 
			"",
			true, 
			new String[] { "jsystem.runner.agent.publisher.Publisher" }),

	REPORTS_PUBLISHER_HOST(
			"publisher.host",
			"Reports Server host name or ip",
			DataType.TEXT,
			"localhost",
			false),

	REPORTS_PUBLISHER_PORT(
			"publisher.port",
			"Reports server port",
			DataType.TEXT,
			"8080",
			false);


	private String string;
	private String description;
	private DataType dataType;
	private Object defaultValue;
	private boolean reloadRunnerRequire;
	private String[] reserve;
	private boolean saveDefaultValueToFile;

	FrameworkOptions(String string) {
		this.string = string;
	}

	FrameworkOptions(String string, String description, DataType dataType, Object defaultValue, boolean reloadRunnerRequire) {
		setParameters(string, description, dataType, defaultValue, reloadRunnerRequire, null, true);
	}

	FrameworkOptions(String string, String description, DataType dataType, Object defaultValue, boolean reloadRunnerRequire, String[] reserve) {
		setParameters(string, description, dataType, defaultValue, reloadRunnerRequire, reserve, true);
	}

	FrameworkOptions(String string, String description, DataType dataType, Object defaultValue, boolean reloadRunnerRequire, String[] reserve, boolean saveDefaultValueToFile) {
		setParameters(string, description, dataType, defaultValue, reloadRunnerRequire, reserve, saveDefaultValueToFile);
	}

	private void setParameters(String string, String description, DataType dataType, Object defaultValue, boolean reloadRunnerRequire, String[] reserve, boolean saveDefaultValueToFile) {
		this.string = string;
		this.description = description;
		this.dataType = dataType;
		this.defaultValue = defaultValue;
		this.reloadRunnerRequire = reloadRunnerRequire;
		this.reserve = reserve;
		this.saveDefaultValueToFile = saveDefaultValueToFile;
	}

	public static FrameworkOptions getFrameworkOptionKeyByStringName(String strName) {
		for (FrameworkOptions frameworkOptions: FrameworkOptions.values()) {
			if (strName.equals(frameworkOptions.getString())) {
				return frameworkOptions;
			}
		}
		return null;
	}

	public String toString() {
		return string;
	}

	public String getString() {
		return string;
	}

	public String getDescription() {
		return description;
	}

	public DataType getDataType() {
		return dataType;
	}

	public boolean isReloadRunnerRequire() {
		return reloadRunnerRequire;
	}

	public String[] getReserve() {
		return reserve;
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isSaveDefaultValueToFile() {
		return saveDefaultValueToFile;
	}
}