/*
 * Created on 15/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.teststable;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;

public class ScenarioTreeNode implements TreeNode {

	JTest test = null;

	int nodeLevel = 0;

	public ScenarioTreeNode(JTest test) {
		this.test = test;
	}

	public TreeNode getChildAt(int childIndex) {
		if (isJTestContainer()) {
			JTestContainer scenario = (JTestContainer) getTest();
			JTest baseTest = (JTest) scenario.getRootTests().elementAt(childIndex);
			return new ScenarioTreeNode(baseTest);
		}
		return null;
	}

	public int getChildCount() {
		if (isJTestContainer() && !ScenarioHelpers.isScenarioAsTestAndNotRoot(getTest())) {
			return ((JTestContainer) getTest()).getRootTests().size();
		}
		return 0;
	}

	/**
	 * Return the parent of the node.
	 * 
	 * @return new instance of the parent node. if the node is the root, it will
	 *         return null.
	 */
	public TreeNode getParent() {
		if (test == null) {
			return null;
		}
//		if (null == getTest().getParent()) {
//			return null;
//		}
		return new ScenarioTreeNode(getTest().getParent());
	}

	public int getNodeLevel() {

		nodeLevel = 0;

		TreeNode tempNode;
		tempNode = (ScenarioTreeNode) getParent();

		while (tempNode != null) {
			nodeLevel++;
			tempNode = tempNode.getParent();
		}

		return nodeLevel;
	}

	public int getIndex(TreeNode node) {
		if (getTest() instanceof JTestContainer) {
			return ((JTestContainer) getTest()).getRootIndex((JTest) ((ScenarioTreeNode) node).getTest());
		}
		return -1;
	}

	public boolean getAllowsChildren() {
		return (isJTestContainer());
	}

	public boolean isLeaf() {
		return (!isJTestContainer() || ScenarioHelpers.isScenarioAsTestAndNotRoot(getTest()));
	}

	public Enumeration<? extends TreeNode> children() {
		Vector<ScenarioTreeNode> elements = new Vector<ScenarioTreeNode>();
		Vector<JTest> tests = null;
		if (isJTestContainer()) {
			tests = ((JTestContainer) getTest()).getRootTests();
		}
		if (tests != null) {
			for (Object testObject : tests.toArray()) {
				JTest test = (JTest) testObject;
				elements.addElement(new ScenarioTreeNode(test));
			}
		}
		return elements.elements();
	}

	public JTest getTest() {
		if (test != null) {
			return test;
		}
		return ScenariosManager.getInstance().getCurrentScenario();
	}

	public String toString() {
		return getTest().toString();
	}

	public boolean isJTestContainer() {
		return (getTest() instanceof JTestContainer);
	}

	public boolean isSelected() {
		return !getTest().isDisable();
	}

	public void setSelected(boolean status) {
		getTest().setDisable(!status);
	}

	/**
	 * return the test parent
	 * 
	 * @return parent scenario
	 */
	public Scenario getTestsScenario() {
		return getTest().getMyScenario();
	}

	/**
	 * get the name used in the xml file - works for both Scenario and
	 * RunnerTest
	 * 
	 * @return the name of the test
	 */
	public String getName() {
		JTest test = getTest();
		return test.getTestName();
	}

	public boolean isRoot() {
		return this.getParent() == null;
	}

}
