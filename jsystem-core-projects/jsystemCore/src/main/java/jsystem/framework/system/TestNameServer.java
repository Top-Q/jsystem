/*
 * Created on Feb 12, 2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import junit.framework.NamedTest;
import junit.framework.Test;

/**
 * The namming server is resopnable of creating a TName object with all the
 * meta-data for each JTest object that run in a scenario.
 * 
 * @author guy.arieli
 * 
 */
public class TestNameServer {

	private static TestNameServer nameServer = null;

	public static TestNameServer getInstance() {
		if (nameServer == null) {
			nameServer = new TestNameServer();
		}
		return nameServer;
	}

	private TestNameServer() {
	}

	/**
	 * Reset the tests name
	 * 
	 */
	public void reset() {
	}

	/**
	 * recieves a test and returns a TName object will all the meta data
	 * depending on instance - Scenario/RunnerTest
	 * 
	 * @param test
	 *            the test to get the info on
	 * @return TName with all the meta-data
	 */
	public TName getTestName(Test test) {
		TName tName = new TName();
		if (test instanceof Scenario) {
			Scenario s = (Scenario) test;
			tName.setClassName(s.getClassName());
			tName.setMethodName(s.getName());
			tName.setUserDocumentation(s.getDocumentation());
			tName.setComment(s.getComment());
		} else {
			tName.setClassName(test.getClass().getName());

			if (test instanceof NamedTest) {
				NamedTest namedTest = (NamedTest) test;
				tName.setClassName(namedTest.getClassName());
				tName.setMethodName(namedTest.getMethodName());
				if (!JSystemProperties.getInstance().isExecutedFromIDE()) {
					fetchAdditionalTestParameters(namedTest, tName);
				}

			}
		}
		return tName;
	}

	/**
	 * If we are not running from IDE (we are in runner mode or Ant mode), there
	 * are few more test properties we can collect.
	 * 
	 * @param test
	 * @param tName
	 */
	private void fetchAdditionalTestParameters(final NamedTest test, TName tName) {
		// This will work if we execute from the runner.
		final JTest jtest = ScenariosManager.getInstance().getCurrentScenario().getTestByFullId(test.getFullUUID());
		if (jtest instanceof RunnerTest) {
			RunnerTest rt = (RunnerTest) jtest;
			tName.setParamsString(rt.getPropertiesAsString());
			tName.setUserDocumentation(rt.getDocumentation());
			tName.setComment(rt.getComment());
		}
	}

}
