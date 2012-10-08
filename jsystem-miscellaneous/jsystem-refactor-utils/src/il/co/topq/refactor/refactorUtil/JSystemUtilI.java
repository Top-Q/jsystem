package il.co.topq.refactor.refactorUtil;

/**
 * @author Itai Agmon
 */

public abstract interface JSystemUtilI {

	/**
	 * renames scenario file names and body to the specified name.
	 * 
	 * @param scenarioSourceNamePath
	 *            The old scenario name. for example: scenarios/simpleScenario
	 * @param scenarioTargetNamePath
	 *            The new name the scenario will be renamed to. for example:
	 *            scenarios/newScenario
	 * 
	 * @return true if one or more files were changed
	 * @throws Exception
	 */
	boolean renameScenario(final String scenarioSourceNamePath, final String scenarioTargetNamePath) throws Exception;

	/**
	 * Renames all the instances of the specified test in the project.
	 * 
	 * @param testSourceNamePath
	 *            The old test full name. For example:
	 *            com.aqua.services.multiuser
	 *            .TestParamsIncludeExample.testCompareFolder
	 * @param testTargetNamePath
	 *            The new test name to rename to.For example:
	 *            com.aqua.services.multiuser
	 *            .TestParamsIncludeExample.testCompareFile
	 * @return true if one or more files were changed
	 * @throws Exception
	 */
	boolean renameTest(final String testSourceNamePath, final String testTargetNamePath) throws Exception;

	/**
	 * Renames parameter in specific test.
	 * 
	 * @param testSourceNamePath
	 *            The test with the parameter to rename. For example:
	 *            com.aqua.services
	 *            .multiuser.TestParamsIncludeExample.testCompareFolder
	 * @param currentParameterName
	 *            The current parameter name. For example: folder
	 * @param newParameterName
	 *            The new parameter name to rename to. For example: file
	 * @return true if one or more files were changed
	 * @throws Exception
	 */
	boolean renameTestsParameters(final String testSourceNamePath, final String currentParameterName,
			final String newParameterName) throws Exception;

	/**
	 * Renames parameter of bean.
	 * 
	 * @param beanSourceNamePath
	 *            The full name of the bean. For example:
	 *            com.aqua.services.multiuser.SimpleBean
	 * 
	 * @param currentParameterName
	 *            The current name of the bean parameter. For example: name
	 * @param newParameterName
	 *            The new name for the parameter. For example: personName
	 * @return true if one or more files were changed
	 * @throws Exception
	 */
	boolean renameTestsBeanParametersNames(final String beanSourceNamePath, final String currentParameterName,
			final String newParameterName) throws Exception;

	/**
	 * Rename scenario name in multiple scenarios suite XML file.
	 * 
	 * @param multipleSuiteAbsolutePathFileName
	 *            The absolute location of the XML file
	 * @param scenarioSourceNamePath
	 *            The old name of the scenario. For example:
	 *            scenarios/flowControl
	 * @param scenarioTargetNamePath
	 *            The new name for the scenario. For example:
	 *            scenarios/flowControl2
	 * @return true if one or more files were changed
	 * @throws Exception
	 */
	boolean renameMultipleScenariosSuiteExecutionScenarioName(final String multipleSuiteAbsolutePathFileName,
			final String scenarioSourceNamePath, final String scenarioTargetNamePath) throws Exception;

	/**
	 * 
	 * @return The number of files the tool changed during refactoring. Notice
	 *         that ususlly the tool also changes the files in the classes
	 *         folder, so every file will be counted twice.
	 */
	int getNumberOfAffectedFiles();

}
