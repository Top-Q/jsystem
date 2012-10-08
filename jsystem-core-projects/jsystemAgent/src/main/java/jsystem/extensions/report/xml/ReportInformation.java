/*
 * Created on Nov 30, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.extensions.report.xml;


/**
 * Define an interface into test group results
 * 
 * @author guy.arieli
 * 
 */
public interface ReportInformation {
	 int getNumberOfTests();

	 int getNumberOfTestsPass();

	 int getNumberOfTestsFail();

	 int getNumberOfTestsWarning();

	 String getVersion();

	 String getBuild();

	 String getUserName();

	 String getScenarioName();

	 String getSutName();
	 
	 String getStation();

	 long getStartTime();

	//  long getEndTime();
	 String getTestClassName(int testIndex);

	 String getTestName(int testIndex);

	 int getTestStatus(int testIndex);

	 String getTestSteps(int testIndex);

	 String getTestDocumentation(int testIndex);

	 String getTestFailCause(int testIndex);

	 long getTestStartTime(int testIndex);

	 long getTestEndTime(int testIndex);
	 
	 void refresh();


}
