package il.co.topq.refactor;

import il.co.topq.refactor.commands.Commander;
import il.co.topq.refactor.commands.RenameParameterOptions;
import il.co.topq.refactor.infra.LoggerHandler;

import il.co.topq.refactor.refactorUtil.JSystemUtilI;
import il.co.topq.refactor.refactorUtil.JSystemUtilImpl;
import il.co.topq.refactor.utils.StringUtils;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * @author Itai Agmon
 */

public class Main {
	private static Logger log = Logger.getLogger("Main");

	private static final String MODE_SCENARIO = "scenario";
	private static final String MODE_TEST = "test";
	private static final String MODE_PARAMETER = "parameter";
	private static final String MODE_BEAN = "bean";
	private static final String MODE_MULTI = "multi";

	public static void main(String... args) {
		LoggerHandler.initLogger();
		Commander commander = null;
		try {
			commander = new Commander(args);

		} catch (ParseException e) {
			log.log(Level.SEVERE, "Failed to parse command line parameters. " + e.getMessage());
			System.exit(1);
		}

		if (commander.hasOption(RenameParameterOptions.OPT_HELP)) {
			printHelpAndExit(commander);
		}

		if (!commander.hasOption(RenameParameterOptions.OPT_MODE)) {
			printHelpAndExit(commander);
		}

		final String mode = commander.getOptionValue(RenameParameterOptions.OPT_MODE);
		if (StringUtils.isEmpty(mode)) {
			printHelpAndExit(commander);
		}

		if (!commander.hasOption(RenameParameterOptions.OPT_PROJECT_DIR)) {
			printHelpAndExit(commander);
		}

		final String projectDir = commander.getOptionValue(RenameParameterOptions.OPT_PROJECT_DIR);
		if (StringUtils.isEmpty(projectDir)) {
			printHelpAndExit(commander);
		}

		JSystemUtilI util = new JSystemUtilImpl(new File(projectDir));
		boolean changed = false;
		long startTime = System.currentTimeMillis();
		try {

			if (mode.equals(MODE_SCENARIO)) {
				final String scenarioSourceNamePath = commander.getOptionValue(RenameParameterOptions.OPT_OLD_SCENARIO);
				assertOption(RenameParameterOptions.OPT_OLD_SCENARIO, scenarioSourceNamePath);

				final String scenarioTargetNamePath = commander.getOptionValue(RenameParameterOptions.OPT_NEW_SCENARIO);
				assertOption(RenameParameterOptions.OPT_NEW_SCENARIO, scenarioTargetNamePath);

				changed = util.renameScenario(scenarioSourceNamePath, scenarioTargetNamePath);

			} else if (mode.equals(MODE_TEST)) {
				final String testSourceNamePath = commander.getOptionValue(RenameParameterOptions.OPT_OLD_TEST);
				assertOption(RenameParameterOptions.OPT_OLD_TEST, testSourceNamePath);

				final String testTargetNamePath = commander.getOptionValue(RenameParameterOptions.OPT_NEW_TEST);
				assertOption(RenameParameterOptions.OPT_NEW_TEST, testTargetNamePath);

				changed = util.renameTest(testSourceNamePath, testTargetNamePath);

			} else if (mode.equals(MODE_PARAMETER)) {
				final String testSourceNamePath = commander.getOptionValue(RenameParameterOptions.OPT_TEST_FULL_NAME);
				assertOption(RenameParameterOptions.OPT_TEST_FULL_NAME, testSourceNamePath);

				final String currentParameterName = commander.getOptionValue(RenameParameterOptions.OPT_OLD_PARAM);
				assertOption(RenameParameterOptions.OPT_OLD_PARAM, currentParameterName);

				final String newParameterName = commander.getOptionValue(RenameParameterOptions.OPT_NEW_PARAM);
				assertOption(RenameParameterOptions.OPT_NEW_PARAM, newParameterName);

				changed = util.renameTestsParameters(testSourceNamePath, currentParameterName, newParameterName);

			} else if (mode.equals(MODE_BEAN)) {
				final String beanSourceNamePath = commander.getOptionValue(RenameParameterOptions.OPT_BEAN_FULL_NAME);
				assertOption(RenameParameterOptions.OPT_BEAN_FULL_NAME, beanSourceNamePath);

				final String currentParameterName = commander.getOptionValue(RenameParameterOptions.OPT_OLD_PARAM);
				assertOption(RenameParameterOptions.OPT_OLD_PARAM, currentParameterName);

				final String newParameterName = commander.getOptionValue(RenameParameterOptions.OPT_NEW_PARAM);
				assertOption(RenameParameterOptions.OPT_NEW_PARAM, newParameterName);

				changed = util.renameTestsBeanParametersNames(beanSourceNamePath, currentParameterName,
						newParameterName);

			} else if (mode.equals(MODE_MULTI)) {
				final String multipleSuiteAbsolutePathFileName = commander
						.getOptionValue(RenameParameterOptions.OPT_PROJECT_DIR);
				assertOption(RenameParameterOptions.OPT_PROJECT_DIR, multipleSuiteAbsolutePathFileName);

				final String scenarioSourceNamePath = commander.getOptionValue(RenameParameterOptions.OPT_OLD_SCENARIO);
				assertOption(RenameParameterOptions.OPT_OLD_SCENARIO, scenarioSourceNamePath);

				final String scenarioTargetNamePath = commander.getOptionValue(RenameParameterOptions.OPT_NEW_SCENARIO);
				assertOption(RenameParameterOptions.OPT_NEW_SCENARIO, scenarioTargetNamePath);

				changed = util.renameMultipleScenariosSuiteExecutionScenarioName(multipleSuiteAbsolutePathFileName,
						scenarioSourceNamePath, scenarioTargetNamePath);

			} else {
				log.warning("Unknown mode: " + mode);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception was caught while refactoring", e);
			System.exit(1);
		}
		log.info("Finished refactoring in " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
		if (changed) {
			log.info(util.getNumberOfAffectedFiles() + " files were affected during refactoring");
		} else {
			log.info("No changes were done in project files");
		}

	}

	private static void assertOption(final Option option, final String optionValue) {
		if (null != optionValue) {
			return;
		}
		log.severe("Missing parameter: " + option.getLongOpt() + " (" + option.getDescription() + ")");
		System.exit(1);
	}

	private static void printHelpAndExit(final Commander commander) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("JSystem Refactor Utils", commander.getOptions(), true);
		StringBuilder examples = new StringBuilder();
		examples.append("\n\nExamples:\n");
		examples.append("Renaming scenario:\n");
		examples.append("-mode scenario -p d:\\workspaceJSystem\\jsystemServices -os scenarios/simpleScenario -ns scenarios/myNewScenario\n");
		examples.append("\nRenaming test:\n");
		examples.append("-mode test -p d:\\workspaceJSystem\\jsystemServices -ot com.aqua.services.multiuser.TestParamsIncludeExample.testCompareFolder -nt com.aqua.services.multiuser.TestParamsIncludeExample.testCompareFiles\n");
		examples.append("\nRenaming test parameter:\n");
		examples.append("-mode parameter -p d:\\workspaceJSystem\\jsystemServices -t com.aqua.services.multiuser.TestParamsIncludeExample.testCompareFolder -op folder -np file\n");
		examples.append("\nRenaming bean parameter:\n");
		examples.append("-mode bean -p d:\\workspaceJSystem\\jsystemServices -b com.aqua.services.multiuser.SimpleBean -op name -np personName\n");
		examples.append("\nRenaming multi scenarios execution file:\n");
		examples.append("-mode multi -p d:\\workspaceJSystem\\jsystemServices\\multi.xml -os scenarios/flowControl -ns scenarios/newScenario\n");
		System.out.println(examples.toString());
		System.exit(0);
	}
}
