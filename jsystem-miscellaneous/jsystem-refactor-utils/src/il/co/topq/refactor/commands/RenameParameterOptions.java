package il.co.topq.refactor.commands;

import org.apache.commons.cli.Option;

/**
 *
 * @author Itai Agmon
 */

public class RenameParameterOptions {

	public static final Option OPT_HELP = new Option("h", "help", false, "print help and usage");

	public static final Option OPT_MODE = new Option("m", "mode", true, "The refactor mode required");

	public static final Option OPT_PROJECT_DIR = new Option("p", "projectDir", true,
			"List of directories and files that need to be processed.");

	public static final Option OPT_TEST_FULL_NAME = new Option("t", "test", true,
			"The full qualified name for a test including <package>.<className>.<methodName>");

	public static final Option OPT_OLD_PARAM = new Option("op", "oldParam", true,
			"The current parameter name that should be renamed");

	public static final Option OPT_NEW_PARAM = new Option("np", "newParam", true, "The new parameter name");

	public static final Option OPT_OLD_TEST = new Option("ot", "oldTest", true, "The old test name");

	public static final Option OPT_NEW_TEST = new Option("nt", "newTest", true, "The new test name");

	public static final Option OPT_OLD_SCENARIO = new Option("os", "oldScenario", true, "The old scenario name");

	public static final Option OPT_NEW_SCENARIO = new Option("ns", "newScenario", true, "The new scenario name");

	public static final Option OPT_BEAN_FULL_NAME = new Option("b", "bean", true,
			"The full name for a bean including <package>.<className>");

	static {
		OPT_HELP.setRequired(false);

		OPT_MODE.setRequired(false);
		OPT_MODE.setArgName("scenario,test,paramter,bean,multi");

		OPT_PROJECT_DIR.setRequired(false);
		OPT_PROJECT_DIR
				.setArgName("JSystem Project Directory or the full path of multiple scenario suite execution XML file");
		String projectOptionDescription = new StringBuilder().append(
				"The root directory for JSystem project(Not the 'classes' directory ).\n").toString();
		OPT_PROJECT_DIR.setDescription(projectOptionDescription);

		OPT_TEST_FULL_NAME.setRequired(false);
		OPT_TEST_FULL_NAME.setArgName("Full qualified name of a test");

		OPT_OLD_PARAM.setRequired(false);
		OPT_OLD_PARAM.setArgName("The current name of the parameter as it appears in the source code");

		OPT_NEW_PARAM.setRequired(false);
		OPT_NEW_PARAM.setArgName("New name that will be assigned to the paramter");

		OPT_BEAN_FULL_NAME.setRequired(false);
		OPT_BEAN_FULL_NAME.setArgName("Full name of a bean class");

		OPT_OLD_TEST.setRequired(false);
		OPT_OLD_TEST.setArgName("The current full name of the test");

		OPT_NEW_TEST.setRequired(false);
		OPT_NEW_TEST.setArgName("The target name of the test.");

		OPT_OLD_SCENARIO.setRequired(false);
		OPT_OLD_SCENARIO.setArgName("The current scenario name");

		OPT_NEW_SCENARIO.setRequired(false);
		OPT_NEW_SCENARIO.setArgName("The new scenario name to assign");

	}
}
