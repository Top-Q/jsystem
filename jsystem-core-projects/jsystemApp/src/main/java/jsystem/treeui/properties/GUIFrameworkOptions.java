/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import jsystem.framework.FrameworkOptions;

/**
 * This Enum should be parallel to the FrameworkOptions Enum in the core.
 * For each entry in the FrameworkOptions, this Enum should hold an equivalent entry that holds extra information
 * to be used by the GUI of JSystemPropertiesDialog.
 * Group - Describe the TAB that this property belong to
 * longDescription - A detailed explanation of the current property. 
 * 
 * @author Dror Voulichman
 *
 */
public enum GUIFrameworkOptions {

	SAVE_REPORTERS_ON_RUN_END (
			FrameworkOptions.SAVE_REPORTERS_ON_RUN_END,
			true,Group.REPORTER,
			"Save reporters to file system only on run end",
			"When set to true, reporters are saved to file system when one of the following happens:\n" +
					"1.End of run (normal end or termination by pressing on 'stop'.\n" +
					"2.Publish event \n" +
					"3.User presses on 'pause'. \n"
			),
	SAVE_REPORTERS_INTERVAL (
			FrameworkOptions.SAVE_REPORTERS_INTERVAL,
			true,Group.REPORTER,
			"The interval of report writting",
			"The amount of seconds between report flush. The default is 10 seconds. To direct writting use -1\n"
			),
	/**
	 */
	HTML_CSS_PATH(
			FrameworkOptions.HTML_CSS_PATH,
			true,Group.REPORTER,
			"list of css file paths",
			"list of css file paths which will be used in the html reporter. " +
					"css files will be added to html header in the given order path. " +
					"css paths should be seperated with a semicolumn"
			),
	/**
	 * tag for the status of the logger (can be set to disable) can be
	 * disable/enable
	 */
	LOGGER_STATUS(
			FrameworkOptions.LOGGER_STATUS,
			true,
			Group.REPORTER, 
			" Enable/Disable the logger. \n" + 
					"In order to disable the logger set the property value to false \n" +
					"In order to enable the logger set the property value to true \n"
			),

	/**
	 * tag for the tests classes folder
	 */
	TESTS_CLASS_FOLDER(
			FrameworkOptions.TESTS_CLASS_FOLDER,
			false,
			Group.TEST_EXECUTION, 
			"The location of tests classes folder \n" +
					"when executing tests from the Eclipse IDE, this location should point to the binaries files \n" +
					"The location of tests classes library is needed for the JRunner execution. \n" +
					"The selected directory must include the 'SUT' and 'SCENARIOS' libraries. \n" 
			),

	/**
	 * tag for the tests source folder
	 */
	TESTS_SOURCE_FOLDER(
			FrameworkOptions.TESTS_SOURCE_FOLDER,
			false,
			Group.TEST_EXECUTION, 
			"The complete path to the tests directory. \n" +
					"Used to extract the tests code as part of the reports. \n" +
					"if this field is empty, JRunner assumes that the \n" + 
					"test source folder is './tests'"
			),


	/**
	 * tag for the log folder
	 */
	LOG_FOLDER(
			FrameworkOptions.LOG_FOLDER,
			true,
			Group.REPORTER,
			"The target directory of the HTML reporter. \n" +
					"All HTML reports are written into this dir. \n" + 
					"The default JRunner setting is the 'log' directory \n" +
					"under the JRunner installation folder"
			),

	/**
	 * tag for the log folder
	 */
	HTML_LOG_PARAMS_IN_LEVEL(
			FrameworkOptions.HTML_LOG_PARAMS_IN_LEVEL,
			true,
			Group.REPORTER,
			"Signals the JSystem reports to log parameters in a seperate level"
			),

	/**
	 * Tag for the command to execute excel
	 */
	EXCEL_COMMAND(
			FrameworkOptions.EXCEL_COMMAND,
			true,
			Group.RUNNER,
			"All Scenario information is saved as an XML file. \n" + 
					"The scenario information includes the scenario tests list, fixtures, \n" + 
					"sub scenarios, scenario parameters and scenario documentation. \n" +
					"The EXCEL_COMMAND property holds the name of the execution file that is used \n" + 
					"to load the Excel scenario file for editing"
			),

	/**
	 * Allow editing in runner
	 */
	RUNNER_ALOW_EDIT(
			FrameworkOptions.RUNNER_ALOW_EDIT,
			true,
			Group.RUNNER,
			"Allow editing in runner"
			),

	/**
	 * Disable the stop button
	 */
	RUNNER_DISABE_STOP(
			FrameworkOptions.RUNNER_DISABE_STOP,
			true,
			Group.RUNNER,
			"Disable the stop button"
			),

	/**
	 * Directory to zip old html reports (the default is old folder)
	 */
	HTML_OLD_DIRECTORY(
			FrameworkOptions.HTML_OLD_DIRECTORY,
			true,
			Group.REPORTER, 
			"The directory used to zip old html reports (the default is 'old' folder) \n" +
					"If the HTML_ZIP_DISABLE property = false, the JRunner will send the old html report to this directory."
			),

	/**
	 * Disable the zip of html report on system exit
	 */
	HTML_ZIP_DISABLE(
			FrameworkOptions.HTML_ZIP_DISABLE,
			true,
			Group.REPORTER, 
			"When set to 'True' all old HTML reports will not be backed up on 'init reporters' action nor on system exit"
			),


	/**
	 * If set to true will save the zip file only in the tree
	 */
	HTML_ZIP_TREE_ONLY(
			FrameworkOptions.HTML_ZIP_TREE_ONLY,
			true,
			Group.REPORTER, 
			"The old HTML reports are saved in the filesystem in a regular directories form and in scenarios hierarchical directories\n"+
					"Setting the HTML_ZIP_TREE_ONLY to 'True' will save old reports only using the latter form"
			),



	/**
	 * Disable the summary report
	 */
	HTML_SUMMARY_DISABLE(
			FrameworkOptions.HTML_SUMMARY_DISABLE,
			true,
			Group.REPORTER, 
			"Disable the summary report. \n" +
					"When set to 'True', the system disallowes aggregate events in JRunner heap memory. \n" +
					"This feature should be used in long runs, to prevent memory problems \n"
			),

	/**
	 * A list of shutdown thread (will be execute on system exit)
	 */
	SHUTDOWN_THREADS(
			FrameworkOptions.SHUTDOWN_THREADS,
			true,
			Group.ADVANCED, 
			"A �;� delimited list of Threads that is executed on the JRunner closure. \n" + 
					"Used by Junit runners to close the system objects."
			),


	/**
	 * List of the reporters classes
	 */
	REPORTERS_CLASSES(
			FrameworkOptions.REPORTERS_CLASSES,
			true,
			Group.REPORTER, 
			"List of the reporters classes \n" +
					"The reporter is a customizable service that allows the programmer to customize \n" +
					"a single reporter plug-in.\n" +
					"This enables the reporter with the abbility to send report messages to different \n" +
					"terminal reporters. Each reporter can then receive the reporter information and \n" +
					"process it according to the locally configured requirements.\n" +
					"Use this property to add custom reporters. \n" +
					"Once you select to update this field, the JRunner runs on the virtual machine \n" +
					"and searches for classes that implement the Reporter interface \n" +
					"The search might take few seconds, and returns a list of all the reporter implemented classes, \n" +
					"Select one or more options from the list \n"
			),

	REPORTER_DELETE_CURRENT(
			FrameworkOptions.REPORTER_DELETE_CURRENT, 
			true,
			Group.REPORTER,
			"If in each time the reports are initialized all the current reports are being deleted."
			),


	/**
	 * XML editor
	 */
	XML_EDITOR(
			FrameworkOptions.XML_EDITOR,
			true,
			Group.RUNNER, 
			"The name (full path) of the application used to edit SUT files"
			),


	/**
	 * Freeze on fail
	 */
	FREEZE_ON_FAIL(
			FrameworkOptions.FREEZE_ON_FAIL,
			false,
			Group.TEST_EXECUTION, 
			"When running a scenario in the JRunner, some of the tests can fail. \n" +
					"In case of a test failure during a scenario run,\n" + 
					"If set to 'True', the scenario execution will freeze. \n" + 
					"If set to 'false', the scenario execution will continue to the next test."
			),

	/**
	 * Html browser name
	 */
	HTML_BROWSER(
			FrameworkOptions.HTML_BROWSER,
			true,
			Group.REPORTER, 
			"Html browser name \n" + 
					"The browser used by the JRunner to open the html reports"
			),


	/**
	 * List of packages to exclude on class loading
	 */
	LOAD_EXCLUDE(
			FrameworkOptions.LOAD_EXCLUDE,
			true,
			Group.ADVANCED, 
			"List of packages to exclude on class loading"
			),


	/**
	 * List of packages to include on class loading
	 */
	LOAD_INCLUDE(
			FrameworkOptions.LOAD_INCLUDE,
			true,
			Group.ADVANCED, 
			"List of packages to include on class loading"
			),

	/**
	 * parameters for the test vm
	 */
	TEST_VM_PARMS(
			FrameworkOptions.TEST_VM_PARMS,
			true,
			Group.TEST_EXECUTION, 
			"In many cases the user requires control over the java parameters that are passed to the test JVM. \n" +
					"The most common use for this is to change the default memory allocation or to enable remote debugging of the test JVM. \n" +
					"In order to set the test JVM parameters add or alter the \"test.vm.params\" properties in the \"jsystem.properties\" file. \n" +
					"The two following example are provided: \n" + 
					"-	In order to alter memory allocation: test.vm.params=-Xms32M -Xmx1024M. \n" + 
					"-	To perform a debugging test execution: test.vm.params=\n" + 
					"                              -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=y,suspend=y \n" +
					"* The ${portNumber} will find the next available port if the one specified is already in use.\n" +
					"  look at the console for the found port"
			),
	ADD_DEFAULTS_CURRENT_TO_PARAM(
			FrameworkOptions.ADD_DEFAULTS_CURRENT_TO_PARAM,
			true,
			Group.ADVANCED,
			"When set to false the default value and current value of parameter will not be displayed as part of the parameter options.\n"  +
					"Every test parameter has default and current values. This property is relevant only in a state were the parameters has\n " +
					"Options. In this case by default the default and current parameter value is added to the option (if not already included.\n" +
			"By changing this property to false no values will be added to the parameter options"),

	/**
	 * Lib dirs
	 */
	LIB_DIRS(
			FrameworkOptions.LIB_DIRS,
			true,
			Group.TEST_EXECUTION, 
			"The complete path to the jars directory. \n" + 
					"Used by the JRunner in order to locate directories containing jar files needed to run the environment and tests"
			),


	/**
	 * disable loader
	 */
	LOADER_DISABLE(
			FrameworkOptions.LOADER_DISABLE,
			true,
			Group.ADVANCED, 
			"If the JProfiler application is used to debug the JRunner execution, the JRunner default Classloader should be disabled."
			),


	/**
	 * run mode
	 */
	RUN_MODE(
			FrameworkOptions.RUN_MODE,
			true,
			Group.TEST_EXECUTION, 
			"In order to conserve computer resources while executing scenarios \n" +
					"the JRunner JVM and the test execution JVM have been separated. \n" + 
					"The JRunner has three execution modes: \n" + 
					"-	Drop every run(1) - The default run mode where one JVM is created for the whole scenario execution. \n" +
					"-	Drop every test(2) - A new JVM is created for each test. \n" + 
					"-	Drop every scenario(4) - A new JVM is created for each sub scenario. \n" + 
					"Drop every test Mode was developed to perform very long test and scenario runs that can exhaust the test JVM \n" + 
					"and cause an execution failure, thus a new JVM is spawned for each test. \n" +
					"As a result, when implementing �Drop every test� special considerations must be made for extended \n" + 
					"running time as well as the fact that data cannot be shared using JVM heap space functionality. \n" + 
					"In order to relieve these two limitations �Drop every scenario� was developed, in this run mode a JVM \n" +
					"is allocated for each sub-scenario, so that tests in the same sub scenario can share the JVM \n" +
					"heap space and execution, greatly increasing test execution speed."
			),

	/**
	 * disable loader
	 */
	RUN_BACKGROUND(
			FrameworkOptions.RUN_BACKGROUND,
			true,
			Group.TEST_EXECUTION,
			"Run in background mode (no po-pup messages, etc.)"),	

	/**
	 * SUT editor
	 */
	SUT_EDITOR(
			FrameworkOptions.SUT_EDITOR,
			true,
			Group.RUNNER, 
			"SUT editor class name"
			),


	/**
	 * The name of the file to add the std.out to
	 */
	STDOUT_FILE_NAME(
			FrameworkOptions.STDOUT_FILE_NAME,
			true,
			Group.RUNNER,
			"If a fileName is specified, all standard output data printed to the the JRunner terminal will also be printed to the given file."
			),



	STDOUT_FILE_APPEND(
			FrameworkOptions.STDOUT_FILE_APPEND,
			true,
			Group.RUNNER,
			"If true, append to output to stdout.file.name. else create new stdout.file.name"
			),



	/**
	 * Disable the print to the console
	 */
	CONSOLE_DISABLE(
			FrameworkOptions.CONSOLE_DISABLE,
			true,
			Group.RUNNER, 
			"When set to 'True', no standard output data will be printed to the console"
			),


	/**
	 * Repeat
	 */
	REPEAT_ENABLE(
			FrameworkOptions.REPEAT_ENABLE,
			false,
			Group.RUNNER,
			"If set to 'True' the JRunner will start with the repeat check box selected. \n"
			),

	PLANNER_JARS_INCLUDE(
			FrameworkOptions.PLANNER_JARS_INCLUDE,
			true,
			Group.RUNNER,
			"Set the planner jars to include in the analysis and search.\n" +
					"This is a ';' delimited list of jars names that will be used to exelerate the UI,\n" +
					"It will limit the source file analysis. Please be aware that you should enter the jar name\n" +
					"without the '.jar' extention"
			),

	TESTS_JAR_NAME_PREFIX(
			FrameworkOptions.TESTS_JAR_NAME_PREFIX,
			true,
			Group.ADVANCED,
			"The name prefix of the jars that are located in the tests lib folder and will be \n" +
					"scanned for building blocks. \n" +
					"This is useful in cases in which we would like to create a system object with the \n" +
					"basic system object building blocks included in the jar \n" +
					"or when we want to create project with multiple building blocks modules \n"
			),


	/**
	 * Disable the search for scenario in old format
	 */
	DISABLE_OLD_SCENARIO_CONVERT(
			FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT,
			true,
			Group.ADVANCED, 
			"When set to 'True', the JRunner will not search for scenarios in the old format"
			),


	/**
	 * Set the max timeout to wait on system exit were -1 is infinite, 0 is
	 * immediately
	 */
	EXIT_TIMEOUT(
			FrameworkOptions.EXIT_TIMEOUT,
			true,
			Group.ADVANCED, 
			"Set the max timeout to wait on system exit (in Seconds). \n" + 
					"-1 - infinite. \n" + 
					"0 - Exit immediately. \n" + 
					"N - Number of seconds to wait"
			),


	/**
	 * List of used scripts engine
	 */
	SCRIPT_ENGINES(
			FrameworkOptions.SCRIPT_ENGINES,
			false,
			Group.RUNNER, 
			"List of used script engines\n" +
					"Currently supported - Ant and Jython."
			),

	/**
	 * If set to true, meaningful name will be ignored in scenario tree
	 */
	IGNORE_MEANINGFUL_NAME(
			FrameworkOptions.IGNORE_MEANINGFUL_NAME,
			true,
			Group.ADVANCED, 

			"Tests annotations provide the JRunner additional information about how to present test metadata. \n" +
					"Normally when adding a test to the scenario, the scenario includes the class name and the test name. \n" +
					"Use the 'TestProperties' annotation to give the test a meanningfull name. \n" + 
					"If IGNORE_MEANINGFUL_NAME is set to 'False', the meaningful name will be displayed in the scenario tree, \n" +
					"If IGNORE_MEANINGFUL_NAME is set to 'True', no meaningful name will appear in the scenario tree.",
					"Example for 'TestProperties' annotation usage:\n"+  
							"@TestProperties(name = \"Generate a folder with random content\") \n" +
							"public void testCreateFilesTree() throws Exception { \n" +
							"          \\\\ method body \n" +
							"}"
			),

	/**
	 * If set to true, meaningful name will be ignored in scenario tree
	 */
	SCENARIO_AS_TEST(
			FrameworkOptions.SCENARIO_AS_TEST,
			true,
			Group.ADVANCED, 			
			"When set to true, root scenario can be marked as a test. A scenario which is marked as a test, will be visualized as a test"+
					"when added as a sub scenario and in the HTML report. \n"+
					"In JSystem reports server the scenario will be count as a test for the statiistics"			
			),

	/**
	 * Indicates the command line executor that should be used when running JSystem from command line.
	 */
	CMD_LINE_EXECUTER(
			FrameworkOptions.CMD_LINE_EXECUTER,
			true,
			Group.ADVANCED, 			
			"Indicates what command line executor engine should be chosen."			
			),

	MAX_BUILDING_BLOCKS_NUMBER(
			FrameworkOptions.MAX_BUILDING_BLOCKS_NUMBER,
			true,
			Group.ADVANCED, 			
			"Set the maximum number of building blocks in a scenario"
			),

	/**
	 * mail host server, jsystem using it to send email
	 */
	MAIL_HOST(
			FrameworkOptions.MAIL_HOST,
			true,
			Group.MAIL,
			"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the \n" + 
					"mail host server.",
					"For Gmail use : smtp.gmail.com"
			),

	/**
	 * the account name of the user that sends the mail
	 */
	MAIL_FROM_ACCOUNT_NAME(
			FrameworkOptions.MAIL_FROM_ACCOUNT_NAME,
			true,
			Group.MAIL,
			"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the \n" +  
					"account name of the user that sends the mail \n"
			),

	/**
	 * the user name of the sending mail
	 */
	MAIL_FROM_USER_NAME(
			FrameworkOptions.MAIL_FROM_USER_NAME,
			true,
			Group.MAIL,
			"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the \n" +  
					"user name of the user that send the mail"
			),

	/**
	 * the password of the sending account
	 */
	MAIL_FROM_PASSWORD(
			FrameworkOptions.MAIL_FROM_PASSWORD,
			true,
			Group.MAIL,
			"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the \n" + 
					"password of the sending account."
			),

	/**
	 * the SMTP port to send mail from
	 */
	MAIL_SMTP_PORT(
			FrameworkOptions.MAIL_SMTP_PORT,
			true,
			Group.MAIL,
			"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the \n" + 
					"SMTP port to send mail from.",
					"For Gmail use : 465"
			),

	/**
	 * is the mail secured (uses ssl?)
	 */
	MAIL_SSL(
			FrameworkOptions.MAIL_SSL,
			true,
			Group.MAIL,
			"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property indicated wether the \n" + 
					"Mail account is using a secured connection. (uses ssl?) \n" + 
					"If set to 'True' - Use secured mail. \n" +
					"If set to 'False' - Do not use secured mail",
					"For Gmail use : True"
			),

	/**
	 * send mail list (separated by ";")
	 */
	MAIL_SEND_TO(
			FrameworkOptions.MAIL_SEND_TO,
			true,
			Group.MAIL,
			"A list of recepients email addresses. \n" +
					"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the list of the recepients\n" + 
					"If the list holds more then one email address, the email addresses \n" + 
					"must be seperated it with ';' . \n", 
					"johnsmith@company.com;christchristiansan@differentcompany.com"
			),

	/**
	 * mail subject
	 */
	MAIL_SUBJECT(
			FrameworkOptions.MAIL_SUBJECT,
			true,
			Group.MAIL,
			"The mail subject. \n" +
					"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the Subject of the sent email\n" 
			),

	/**
	 * mail header
	 */
	MAIL_HEADER(
			FrameworkOptions.MAIL_HEADER,
			true,
			Group.MAIL,
			"The mail header. \n" +
					"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the header of the mail\n" + 
					"which appears before the content. \n"
			),

	/**
	 * File attachment list (separated by ";")
	 */
	MAIL_ATTACHMENTS(
			FrameworkOptions.MAIL_ATTACHMENTS,
			true,
			Group.MAIL,
			"A list of file attachments. \n" +
					"When using a Publish-Event test, there is an option to also send an Email notification.\n" +
					"In the case that EMail sending is enabled, the current property holds the list of the files\n" +
					"to be attached to the mail.\n" + 
					"If the list holds more then one file, the files \n" + 
					"must be seperated it with ';' . \n", 
					"C:/workspace/file1.txt;jsystem/file2.doc"
			),

	/**
	 * Allow user to change agent during run.
	 */
	CHANGE_AGENT_DURING_RUN(
			FrameworkOptions.CHANGE_AGENT_DURING_RUN,
			true,
			Group.DISTRIBUTED_EXECUTION,
			"Allows the changing of agents during scenario Execution."
			),

	/**
	 * Allow user to change agent during run.
	 */
	AGENT_NOTIFICATION_LEVEL(
			FrameworkOptions.AGENT_NOTIFICATION_LEVEL,
			true,
			Group.DISTRIBUTED_EXECUTION,
			"Agents notifications level"
			),

	/**
	 * 
	 */
	IGNORE_DISTRIBUTED_EXECUTION(
			FrameworkOptions.IGNORE_DISTRIBUTED_EXECUTION,
			true,
			Group.DISTRIBUTED_EXECUTION,
			"When set to 'True', test assignments to different agents is ignored"),

	/**
	 * 
	 */
	DISTRIBUTED_EXECUTION_PLUGIN(
			FrameworkOptions.DISTRIBUTED_EXECUTION_PLUGIN,
			true,
			Group.DISTRIBUTED_EXECUTION,
			"Plugin class for distributed execution."),

	AGENT_AUTO_SYNC(
			FrameworkOptions.AGENT_AUTO_SYNC,
			true,
			Group.DISTRIBUTED_EXECUTION,
			"Synchronize agent automatically"),

	/**
	 * 
	 */
	CUSTOMER_PRODUCT(
			FrameworkOptions.CUSTOMER_PRODUCT,
			true,
			Group.RUNNER, 

			"Tag for all the customer products versions. \n" +
					"this property allows the user to add customer specific data to the JRunner About window. \n" +
					"Using this property, the user allows the customer to present the current driver version being worked on  \n" +
					"or any other Customer information. \n" +
					"In order to add Customer info to the About window, customer.product must be set. \n" +
					"The value is a semicolon separated string with different values. \n" +
					"Every semicolon separated value is displayed in the About window in a new line.",

					"customer.product = \"apcon-1.5;ixia 5.5;version=6\" \n"
			),

	SCEANRIO_AS_TEST_TERMINATE_ON_FAIL(
			FrameworkOptions.SCEANRIO_AS_TEST_TERMINATE_ON_FAIL,
			true,
			Group.RUNNER,
			"If enabled, and a Scenario is marked as a test,\n" +
			"then the scenario will stop execution if any of it's tests fail."),
	AUTO_SAVE_NO_CONFIRMATION(
			FrameworkOptions.AUTO_SAVE_NO_CONFIRMATION,
			true,
			Group.RUNNER,
			"If set to True then no confirmation will be shown and changes in scenario will be saved\n" +
					"on Scenario change, Project changing , runner closing, etc..."
			),

	//========================================================================

	AUTO_SAVE_INTERVAL(
			FrameworkOptions.AUTO_SAVE_INTERVAL,
			true,
			Group.RUNNER,
			"if a positive number is set, it will be the number of seconds\n"+
					"that will pass between automatic savings.\n"+
					"ONLY changes through the GUI will update the auto save operation, if a manual change\n"+
					"in the jsystem.properties file is made, runner will need to be reloaded to take effect."
			),
	//========================================================================
	AUTO_DELETE_NO_CONFIRMATION(
			FrameworkOptions.AUTO_DELETE_NO_CONFIRMATION,
			true,
			Group.RUNNER,
			"If set to True then no confirmation will be shown and selected items will be removed"),


	/**
	 * SUT reader for handling sut
	 */
	SUT_READER_CLASS(
			FrameworkOptions.SUT_READER_CLASS,
			true,
			Group.RUNNER, 
			"List of used sut readers.\n" +
					"By default, no sut reader is used, so regular xml is read.\n" +
					"In order to add a new Sut reader, please implement " +
					"jsystem.framework.sut.SutReader interface.\n" +
					"and then select you class here."
			),


	PARAMETERS_ORDER_DEFAULT(
			FrameworkOptions.PARAMETERS_ORDER_DEFAULT,
			true,
			Group.RUNNER,
			"Default Parmeters order in the parameters panel.\n" +
					"defaultOrder - sort by the order the parameters appear in the code.\n" +
					"ascending - ordered alphabeticaly\\numericaly ascending.\n" +
			"descending - ordered alphabeticaly\\numericaly descending.\n"),

	/**
	 * Set to true in order to move all tests properties from XML file to properties file before loading the Scenario		
	 */
	MOVE_PARAMS_FROM_XML_TO_PROP_ON_SCENARIO_LOAD(
			FrameworkOptions.MOVE_PARAMS_FROM_XML_TO_PROP_ON_SCENARIO_LOAD,
			true,
			Group.ADVANCED,
			"Set to true in order to move all tests properties from XML file to properties file before loading the Scenario"),

	SCENARIO_NAME_HOOK(
			FrameworkOptions.SCENARIO_NAME_HOOK,
			true,
			Group.ADVANCED,
			"A Scenario name hook is an implementation of the\n" +
					"jsystem.extensions.ScenarioNameHook.\n" +
					"Creating a hook allows the user to manipulate the Scenario name on creation and" +
					" add a unique ID to the scenario name.\n" +
					"In addition, it allowes attaching a project name to the scenario.\n" +
			"For more on this issue, please refer to the JSystem 5.6 release notes."),

	CACHE_SCENARIO_PROPERTIES (
			FrameworkOptions.CACHE_SCENARIO_PROPERTIES,
			true,
			Group.ADVANCED,
			"Enables the scenarios properties caching mechanis. " +
					"NOTICE: This is an experimental feature, use it on your own risk.  "
			),



	SCM_PLUGIN_CLASS(
			FrameworkOptions.SCM_PLUGIN_CLASS,
			true,
			Group.SOURCE_CONTROL,
			"Name of the SCM plugin class\n" +
			"For example: org.jsystemtest.plugin.svn.SvnHandler\n"),

	SCM_REPOSITORY(
			FrameworkOptions.SCM_REPOSITORY,
			true,
			Group.SOURCE_CONTROL,
			"The repository path to the root project\n"),

	SCM_USER(
			FrameworkOptions.SCM_USER,
			true,
			Group.SOURCE_CONTROL,
			"SCM user\n"),

	SCM_PASSWORD(
			FrameworkOptions.SCM_PASSWORD,
			true,
			Group.SOURCE_CONTROL,
			"SCM password\n"), 


	GENERIC_TABS(
			FrameworkOptions.GENERIC_TABS,
			true,
			Group.ADVANCED,
			"List of classes which implement jsystem.treeui.interfaces.JSystemTab (under JSystemApp)\n" +
					"Use this property to add custom tabs. \n" +
					"Once you update this field, The JSystem will restart and add the new tabs." +

			"The search might take few seconds, and returns a list of all the implementing classes, \n" +
			"Select one or more options from the list \n"
			),

	DATA_PROVIDER_CLASSES(
			FrameworkOptions.DATA_PROVIDER_CLASSES,
			true,
			Group.ADVANCED,
			"Allow users to implement different data providers for the data driven building block.\n"+ 
					"For example, database data provider, Excel data provider, etc..\n" +
					"To create a new data provider one need to create a new Maven project and add JSystemAnt project "+
					" as a dependency \n" +
					"After implementing the concrete data provider class, the jar of the project needs to be added to the lib folder of JSystem.\n"+
					"If there are any additional dependencies that are needed they should be part of the jar or to be added to the JSystem thirdparty/commonLib folder\n"+ 
					"After launching JSystem, the data provider should be selected in the property  data.driven.provider. \n"
			),

	CONTEXT_MENU_PLUGIN_CLASSES (
			FrameworkOptions.CONTEXT_MENU_PLUGIN_CLASSES,
			true,
			Group.ADVANCED,
			"Allow users to implement plugins that will be added to the context menu of the scenario table\n" + 
					"For more information pleases refer to the JSystem documentation"
			),

	/**
	 * SUT reader for handling sut
	 */
	REPORTS_PUBLISHER_CLASS(
			FrameworkOptions.REPORTS_PUBLISHER_CLASS,
			true,
			Group.REPORTS_PUBLISHER, 
			"List of reports publishers.\n" +
					"In order to add a new publisher, please implement \n" +
					"jsystem.runner.agent.publisher.Publish interface \n" +
					"and then select you class here."
			),


	REPORTS_PUBLISHER_HOST(
			FrameworkOptions.REPORTS_PUBLISHER_HOST,
			true,
			Group.REPORTS_PUBLISHER,
			"Reports publisher host or ip\n"),

	REPORTS_PUBLISHER_PORT(
			FrameworkOptions.REPORTS_PUBLISHER_PORT,
			true,
			Group.REPORTS_PUBLISHER,
			"Reports publisher port\n");


	private boolean exposeToDialog;
	private Group group;
	private String longDescription;
	private FrameworkOptions frameworkOption;
	private String example;

	GUIFrameworkOptions( FrameworkOptions frameworkOption,boolean exposeToDialog, Group group, String longDescription) {
		this(frameworkOption,exposeToDialog, group, longDescription, "");
	}

	GUIFrameworkOptions( FrameworkOptions frameworkOption,boolean exposeToDialog, Group group, String longDescription, String example) {
		this.frameworkOption = frameworkOption;
		this.exposeToDialog = exposeToDialog;
		this.group = group;
		this.longDescription = longDescription;	
		this.example = example;
	}

	public Group getGroup() {
		return group;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public boolean isExposeToDialog() {
		return exposeToDialog;
	}

	public FrameworkOptions getFrameworkOption() {
		return frameworkOption;
	}

	public String getExample() {
		return example;
	}

	/**
	 * find the matching GuiFrameworkOption for the given FrameworkOption
	 * @param frameworkOption	the FrameworkOption to match
	 * @return	null if not found, the matching GuiFrameworkoption object otherwise 
	 */
	public static GUIFrameworkOptions findGuiFrameworkOption(FrameworkOptions frameworkOption){
		for (GUIFrameworkOptions option : GUIFrameworkOptions.values()){
			if (option.getFrameworkOption().equals(frameworkOption)){
				return option;
			}
		}
		return null;
	}

	/**
	 * 
	 * The jsystemPropertiesDialog has a tabbedPane that present the system properties divided to groups.
	 * Each group is presented in a different TAB.
	 * This Enum describe the TABS
	 * index - the index of the TAB within the tabbedPane
	 * value - The title to be written in the TAB.
	 * 
	 * @author Dror Voulichman	
	 */
	public enum Group {
		TEST_EXECUTION(0, "Test Execution"),
		RUNNER(1, "Runner"),
		REPORTER(2, "Reporter"),
		MAIL(3, "Mail"),
		DISTRIBUTED_EXECUTION(4, "Distributed Execution"),
		ADVANCED(5, "Advanced"),
		SOURCE_CONTROL(6, "Source Control"),
		REPORTS_PUBLISHER(7,"Reports Publisher");

		private int index;
		private String value;

		Group (int index, String value) {
			this.index = index;
			this.value = value;
		}

		public int	getIndex() {
			return(index);
		}

		public String getValue() {
			return(value);
		}
	}

}
