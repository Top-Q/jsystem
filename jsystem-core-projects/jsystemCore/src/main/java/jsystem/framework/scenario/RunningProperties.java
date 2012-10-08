/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

/**
 * Repository of keys used to ant running execution and other places
 * @author guy.arieli
 *
 */
public class RunningProperties {
	public final static String PARAM_PREFIX = "jsystem.params.";

	/**
	 * Exposed parameter
	 */
	public final static String OUT_PARAM_PREFIX = "jsystem.params.out.";

	public final static String DOCUMENTATION_TAG = "jsystem.documentation";

	public final static String COMMENT_TAG = "jsystem.comment";
	
	public static String MEANINGFUL_NAME_TAG = "meaningfulName";
	
	public final static String SCENARIO_AS_TEST_TAG = "jsystem.scenario.as.test";
	
	//APPLIED - the property key that will be seen in the properties file
	public final static String EDIT_ONLY_LOCALLY = "jsystem.scenario.edit.only.locally";

	public final static String SCENARIO_EXTERNAL_ID = "jsystem.scenario.external.id";

	public final static String SCENARIO_PROJECT_NAME = "jsystem.scenario.project.name";
	
	/**
	 * Signal a scenario as test started to solve bugs #248 and #342
	 */
	public final static String SCENARIO_AS_TEST_START = "jsystem.scenario.as.test.start";
	
	/**
	 * A counter for identifying if we are currently in a Scenario as test
	 */
	public final static String SCENARIO_AS_TEST_FAILURE = "jsystem.scenario.as.test.failure";
	
	public final static String MARKED_AS_KNOWN_ISSUE = "jsystem.known.issue";
	
	public final static String MARKED_AS_NEGATIVE_TEST = "jsystem.negative.test";
	
	public final static String HIDDEN_IN_HTML = "jsystem.hidden.in.html";
	
	/*
	 * The tag will be used in scenario xml as a comment to the doc root
	 */
	public static String MEANINGFUL_TAG = "meaningfulName: ";
	
	/**
	 * the Unique id for a test\scenario 
	 */
	public final static String UUID_TAG = "jsystem.uuid";
	
	/**
	 * the full Unique id path for a parent scenario (includes all Unique id's of parent scenarios)
	 */
	public final static String UUID_PARENT_TAG = "jsystem.parent.uuid";
	
	public final static String PARENT_NAME = "jsystem.parent.name";
	
	public final static String METHOD_NAME = "jsystem.method.name";
	
	public final static String UI_SETTINGS_TAG = "jsystem.uisettings";
	
	public final static String IS_DISABLED = "jsystem.isdisabled";

	public final static String CURRENT_FIXTURE_BASE = "jsystem.current.fixture.base";

	public final static String CURRENT_FIXTURE = "jsystem.current.fixture";

	public final static String FIXTURE_DISABLE_TAG = "jsystem.fixture.disable";

	public static final String FIXTURE_PROPERTY_NAME = "FixtureName";
	
	public final static String CURRENT_SUT = "jsystem.current.sut";

	public static final String ANT_EXECUTOR = "jsystem.ant.executor";

	public static final String RUNNER_PORT = "jsystem.runner.port";

	public static final String RUNNER_HOST = "jsystem.runner.host";

	// public static final String SCENARIOS_BASE_DIR = "jsystem.scenario.dir";
	public static final String USER_DIR = "basedir";

	public static final String RUNNER_EXIST = "jsystem.runner.exist";

	public static final String SCENARIO_BASE = "scenarios.base";
	
	public static final String SCRIPT_TAG = "jsystem.tag";
	
	public static final String SYSTEM_OBJECT_OPERATION = "jsystem.operation";
	
	public static final String SCRIPT_PATH = "jsystem.file.path";
	
	public static final String TEST_PARAMETERS_FILE_NAME_PARAMETER = "test.parameters.file.name";
	
	public static final String TEST_PARAMETERS_FILE_NAME_PREFIX = ".testPropertiesFile_";
	
	public static final String TEST_PARAMETERS_EMPTY_FILE = TEST_PARAMETERS_FILE_NAME_PREFIX +"Empty";
	
	public static final String CURRENT_SCENARIO_NAME = "jsystem.current.scenario.name";
	
	public static final String JSYSTEM_AGENT = "jsystem.agent";
	
	/**
	 * A flag for aborting Scenario execution
	 */
	public final static String ABORT_CURRENT_SCENARIO_EXECUTION = "jsystem.scenario.abort";

	public static final String ANT_TARGET = "target";
}
