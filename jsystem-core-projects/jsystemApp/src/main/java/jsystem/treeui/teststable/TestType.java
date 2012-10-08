/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.teststable;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.treeui.images.ImageCenter;

/**
 * For mapping a test with its images
 * 
 * @author Nizan Freedman
 *
 */
public enum TestType{
	SCENARIO_REGULAR(ImageCenter.ICON_SCENARIO_RUN,ImageCenter.ICON_SCENARIO_ERROR,ImageCenter.ICON_SCENARIO_FAIL,ImageCenter.ICON_SCENARIO_WARNING,ImageCenter.ICON_SCENARIO_OK,ImageCenter.ICON_SCENARIO),
	TEST_REGULAR(ImageCenter.ICON_TEST_RUN,ImageCenter.ICON_TEST_ERR,ImageCenter.ICON_TEST_FAILER,ImageCenter.ICON_TEST_WARNING,ImageCenter.ICON_TEST_OK,ImageCenter.ICON_TEST),
	SCENARIO_AS_TEST(ImageCenter.ICON_SCENARIO_AS_TEST_RUN,ImageCenter.ICON_SCENARIO_AS_TEST_ERROR,ImageCenter.ICON_SCENARIO_AS_TEST_FAIL,ImageCenter.ICON_SCENARIO_AS_TEST_WARNING,ImageCenter.ICON_SCENARIO_AS_TEST_PASS,ImageCenter.ICON_SCENARIO_AS_TEST),
	SCENARIO_KNOWN_ISSUE(ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_RUN,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_ISSUE,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_ISSUE,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_WARNING,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_ISSUE_PASS,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_ISSUE),
	TEST_KNOWN_ISSUE(ImageCenter.TEST_KNOWN_ISSUE_RUN,ImageCenter.TEST_KNOWN_ISSUE,ImageCenter.TEST_KNOWN_ISSUE,ImageCenter.TEST_KNOWN_ISSUE_WARNING,ImageCenter.TEST_KNOWN_ISSUE_PASS,ImageCenter.TEST_KNOWN_ISSUE),
	SCENARIO_NEGATIVE_TEST(ImageCenter.ICON_SCENARIO_AS_TEST_NEGETIVE_RUN,ImageCenter.ICON_SCENARIO_AS_TEST_NEGETIVE_ERROR,ImageCenter.ICON_SCENARIO_AS_TEST_NEGETIVE_FAIL,ImageCenter.ICON_SCENARIO_AS_TEST_NEGETIVE_WARNING,ImageCenter.ICON_SCENARIO_AS_TEST_NEGETIVE_PASS,ImageCenter.ICON_SCENARIO_AS_TEST_NEGETIVE),
	TEST_NEGATIVE(ImageCenter.ICON_TEST_NEGETIVE_RUN,ImageCenter.ICON_TEST_NEGETIVE_ERROR,ImageCenter.ICON_TEST_NEGETIVE_FAIL,ImageCenter.ICON_TEST_NEGETIVE_WARNING,ImageCenter.ICON_TEST_NEGETIVE_PASS,ImageCenter.ICON_TEST_NEGETIVE),
	SCENARIO_KNOWN_AND_NEGATIVE(ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_RUN,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_WARNING,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE_PASS,ImageCenter.ICON_SCENARIO_AS_TEST_KNOWN_AND_NEGETIVE),
	TEST_KNOWN_AND_NEGATIVE(ImageCenter.ICON_TEST_KNOWN_AND_NEGETIVE_RUN,ImageCenter.ICON_TEST_KNOWN_AND_NEGETIVE,ImageCenter.ICON_TEST_KNOWN_AND_NEGETIVE,ImageCenter.ICON_TEST_KNOWN_AND_NEGETIVE_WARNING,ImageCenter.ICON_TEST_KNOWN_AND_NEGETIVE_PASS,ImageCenter.ICON_TEST_KNOWN_AND_NEGETIVE);

	private String running, error, fail, warning, success, notRunning;


	private TestType(String running, String error,String fail, String warning, String success, String notRunning){
		this.running = running;
		this.error = error;
		this.fail = fail;
		this.warning = warning;
		this.success = success;
		this.notRunning = notRunning;
	}

	/**
	 * Get the matching image String by the given test status
	 * 
	 * @param test	the test to get the image for
	 * @return	the String of the image file
	 */
	public String getImageString(JTest test){
		if (test.isRunning()){
			return running;
		}
		if (test.isError()){
			return error;
		}
		if (test.isFail()){
			return fail;
		}
		if (test.isWarning()){
			return warning;
		}
		if (test.isSuccess()){
			return success;
		}
		return notRunning;
	}

	/**
	 * Get the matching icon String for a given test
	 * @param test
	 * @return
	 */
	public static String getMatchingIcon(JTest test){
		TestType type = null;
		if (test instanceof Scenario) {
			if (((Scenario)test).isScenarioAsTest()){
				if (test.isMarkedAsKnownIssue()){
					if (test.isMarkedAsNegativeTest()){
						type = TestType.SCENARIO_KNOWN_AND_NEGATIVE;
					}else{
						type = TestType.SCENARIO_KNOWN_ISSUE;
					}
				}else if (test.isMarkedAsNegativeTest()){
					type = TestType.SCENARIO_NEGATIVE_TEST;
				}else{
					type = TestType.SCENARIO_AS_TEST;
				}
			}else{
				type = TestType.SCENARIO_REGULAR;
			}
			
		}else if (test instanceof RunnerTest){
			if (test.isMarkedAsKnownIssue()){
				if (test.isMarkedAsNegativeTest()){
					type = TestType.TEST_KNOWN_AND_NEGATIVE;
				}else{
					type = TestType.TEST_KNOWN_ISSUE;
				}
			}else if (test.isMarkedAsNegativeTest()){
				type = TestType.TEST_NEGATIVE;
			}else{
				type = TestType.TEST_REGULAR;
			}
		}

		if (type != null){
			return type.getImageString(test);
		}
		return "";
	}

}
