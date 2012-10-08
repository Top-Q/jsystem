/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.ClassPathTestCollector;

/**
 * ScenarioCollector extends junit ClassPathTestCollector which it puprpose is
 * to collect Tests. Overiding the isTest moethod and using the isScenario
 * method , the ScenarioCollector using the ClassPathTestCollector is now
 * collects only scenario xml files.
 * 
 * @author Uri.Koaz
 */
public class ScenarioCollector extends ClassPathTestCollector {
	
	public ScenarioCollector() throws Exception {
	}

	public Enumeration<String> collectTests() {
		Hashtable<String, String> result = collectFilesInPath(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER));
		return result.elements();
	}
	
	public boolean isTestClass(String classFileName) {		
		if (Scenario.isScenario(classFileName)) {
			return true;
		} else {
			return false;
		}
	}
	
	public Vector<String> collectTestsVector() {
		Enumeration<String> en = collectTests();
		Vector<String> v = new Vector<String>(10);
		
		while (en.hasMoreElements()) {
			v.addElement(en.nextElement());
		}

		return v;
	}

	protected String classNameFromFile(String classFileName) {
		return classFileName.substring(1, classFileName.length() - 4);
	}
}
