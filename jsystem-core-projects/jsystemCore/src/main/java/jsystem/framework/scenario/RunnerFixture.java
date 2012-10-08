/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.HashMap;
import java.util.Properties;

import jsystem.extensions.report.html.HtmlCodeWriter;
import junit.framework.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * represents a fixture entity on the runner scenario tree. RunnerFixture
 * extends from RunnerTree because it`s a test.
 * 
 * @author uri.koaz
 */
public class RunnerFixture extends RunnerTest {

	/**
	 * the method name is setUp
	 * 
	 * @param className
	 */
	public RunnerFixture(String className) {
		super(className, "setUp");
	}
	
	public RunnerFixture cloneTest() throws Exception {
		RunnerFixture test = new RunnerFixture(className);
		return test;
	}
	
	/**
	 * constructs a RunnerFixture from and Element got from the scenario xml
	 * file initiates the test's properties from the element. uses the Test to
	 * get the method (includes searching in super classes)
	 * 
	 * @param jsystemElement
	 *            the element from the xml file
	 * @param test
	 *            the new created RunnerTest Test
	 * @return a RunnerTest instance
	 * @throws Exception
	 */
	public static RunnerFixture fromElement(Element jsystemElement, Test test, Scenario parent) throws Exception {

		String className = null;
		String methodName = null;

		String tname = jsystemElement.getAttribute("name");
		String[] splitResult = tname.split("_");
		tname = tname.split("_")[1];

		for (int i = 2; i < splitResult.length; i++) {
			tname += "_" + splitResult[i];
		}

		if (tname != null) {
			className = tname;
		}
		if (className == null) {
			return null;
		}
		RunnerFixture rTest = new RunnerFixture(className);
		rTest.setParent(parent);
		rTest.parameters = new HashMap<String, Parameter>();
		rTest.updateInternalJsystemFlags();
		rTest.setDisable("true".equals(jsystemElement.getAttribute("disabled")));
		rTest.setTest(test);
		NodeList list = jsystemElement.getElementsByTagName("sysproperty");

		Properties p = new Properties();
		//testJavadoc = rTest.getJavadoc(className, methodName);
		String include = HtmlCodeWriter.getInstance().getMethodAnnotation(className, methodName, INCLUDE_PARAMS_STRING);
			//getAnnotation(includeParamsString);
		String exclude = HtmlCodeWriter.getInstance().getMethodAnnotation(className, methodName, EXCLUDE_PARAMS_STRING);
		int annotation = compareIncludeAndExclude(className, methodName);
		String name;
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if (!(n instanceof Element)) {
				continue;
			}
			Element prop = (Element) n;
			String key = prop.getAttribute("key");
			String value = prop.getAttribute("value");

			// TODO: to see how to work with value that expected to be null
			if (key.equals(RunningProperties.DOCUMENTATION_TAG)) {
				rTest.setDocumentation(value);
				continue;
			}
			if (key.equals(RunningProperties.COMMENT_TAG)) {
				rTest.setTestComment(value);
				continue;
			}

			if (key.equals("")) { // continue if the key is
				// not set
				continue;
			}
			if (!key.startsWith(RunningProperties.PARAM_PREFIX)) {
				continue;
			}

			key = key.substring(RunningProperties.PARAM_PREFIX.length());

			name = lowerFirstLetter(key);
			if (annotation == NO_INCLUDE_OR_EXCLUDE || (annotation == INCLUDE && include.indexOf(name) != -1)
					|| (annotation == EXCLUDE && (exclude.indexOf(name) == -1))) {
				p.put(key, value);
			}
		}
		rTest.setProperties(p);
		return rTest;
	}

	/**
	 * create Xml Element representing this current RunnerFixture
	 */
	public void addTestsXmlToRoot(Document doc, Integer[] indexes) {

		/**
		 * Create the target element, set the test ID as the target name.
		 */
		Element target = doc.createElement(RunningProperties.ANT_TARGET);
		target.setAttribute("name", testId);

		Element jsystem = doc.createElement("jsystem");

		if (getDocumentation() != null) {
			Element p = doc.createElement("sysproperty");
			p.setAttribute("key", RunningProperties.DOCUMENTATION_TAG);
			p.setAttribute("value", getDocumentation());
			jsystem.appendChild(p);
		}
		if (getComment() != null) {
			Element p = doc.createElement("sysproperty");
			p.setAttribute("key", RunningProperties.COMMENT_TAG);
			p.setAttribute("value", getComment());
			jsystem.appendChild(p);
		}
		
		target.appendChild(jsystem);
		
		// Add test to root as target
		Element root = doc.getDocumentElement();
		root.appendChild(target);
	}
}