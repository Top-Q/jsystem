/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.treeui.TestFilterManager;
import junit.framework.TestCase;

import org.junit.internal.runners.JUnit4TestCaseParser;

public class TestCaseNode extends AssetNode {

	private static final long serialVersionUID = -4756435918185373700L;

	private Class<?> aClass;

	public TestCaseNode(AssetNode parent, Class<?> userObject) throws Exception {
		super(parent, userObject);
		aClass = userObject;

		children = new Vector();
		
		if (TestCase.class.isAssignableFrom(userObject)) {
			// JUnit 3 style class
			createJUnit3TestNodes(userObject);
		}
		else {
			// JUnit 4 style class
			createJUnit4TestNodes(userObject);
		}
		if("true".equals(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SORT_ASSETS_TREE))){
			Collections.sort(children, null);
		}
	}

	public String toString() {
		String name = aClass.getName();
		return name.substring(name.lastIndexOf(".") + 1);
	}

	/**
	 * Get the name of the class this node represent
	 * 
	 * @return the class name
	 */
	public String getClassName() {
		if (aClass == null) {
			return null;
		}
		return aClass.getName();
	}
	
	public void createJUnit3TestNodes(Class<?> userObject) {
		assert(userObject.isAssignableFrom(TestCase.class));
		
		for (Method m : userObject.getMethods()) {
			if (m.getName().startsWith("test")
					&& m.getParameterTypes().length == 0) {
				TestNode tn = new TestNode(this, m);
				String[] groups = tn.getTest().getGroups();
				StringBuffer groupString = new StringBuffer();
				if(groups != null){
					for(String group: groups){
						groupString.append('.');
						groupString.append(group);
					}
				}
				if (TestFilterManager.getInstance().filter(
						this.getClassName() + "." + m.getName() + groupString.toString())) {
					continue;
				}
				children.add(tn);
			}
		}
	}
	
	public void createJUnit4TestNodes(Class<?> userObject) {
		JUnit4TestCaseParser parser = new JUnit4TestCaseParser(userObject);
		List<Method> methods = parser.getTestMethods();
		for (Method m : methods) {
			TestNode tn = new TestNode(this, m);
			String[] groups = tn.getTest().getGroups();
			StringBuffer groupString = new StringBuffer();
			if(groups != null){
				for(String group: groups){
					groupString.append('.');
					groupString.append(group);
				}
			}
			if (TestFilterManager.getInstance().filter(
					this.getClassName() + "." + m.getName() + groupString.toString())) {
				continue;
			}
			children.add(new TestNode(this, m));
		}
	}
}
