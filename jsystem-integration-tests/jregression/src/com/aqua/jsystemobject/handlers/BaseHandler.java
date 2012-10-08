package com.aqua.jsystemobject.handlers;

import java.util.Random;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.tree.TreePath;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.utils.StringUtils;

import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import com.aqua.jsystemobject.JSystem;
import com.aqua.jsystemobject.JemmySupport;

public class BaseHandler{
	TestRunner runner;

	protected static JsystemMapping jmap;

	String mainWindow;

	volatile boolean runEnd = false;

	volatile int runEndCount;

	volatile boolean executionEnd = false;

	protected static JFrameOperator mainFrame;

	protected static JTreeOperator testsTree;

	protected static JTreeOperator scenarioTree;

	protected static JemmySupport jemmyOperation;

	protected Logger log = Logger.getLogger(BaseHandler.class.getName());

	boolean launched = false;
	
	/*
	 * Vectorize() and unvectorize(0 - simple utils for XMLRPC simpler coding.
	 * TODO: maybe put it somewhere else, or replace with existing utils (didn't find any)
	 */
	public Vector vectorize(Object...objects) {
		Vector vector = new Vector<String>();
		for (Object item : objects) {
			vector.add(item);
		}
		return vector;
	}
	
	public Object[] unvectorize(Vector vector) {
		return vector.toArray(new Object[vector.size()]);
	}
	
	/**
	 * gets a test name and a test class name and returns the path 
	 * to the requested test.
	 * @param node
	 * @param parentNode
	 * @return
	 * @throws Exception
	 */
	protected TreePath getTreePath(String node, String parentNode) throws Exception{
		//if parent node or method name are empty string, throw exception
		if ( StringUtils.isEmpty(node) || StringUtils.isEmpty(parentNode)) {
			throw new Exception("empty test Parent node or method is not aloud: Give both parent and method name!!!");
		}
		if (JSystem.SCENARIO.equals(parentNode) || JSystem.SCRIPT.equals(parentNode) || JSystem.RANDOM.equals(parentNode)){
			parentNode = "";
		}
		jemmyOperation.report("mainFrame is null? "+(mainFrame==null));
		JTabbedPaneOperator testTreeTab = new JTabbedPaneOperator(mainFrame, 0);
		testTreeTab.selectPage(jmap.getTestTreeTab());//give control on relevant tab
		int c = testsTree.getRowCount();
		System.out.println("Row count: " + c);
		TreePath foundPath = null;
		//method is not null and is equal to Random
		if (node != null && node.equals("Random")) { // Then we want to select a test Randomly
			
			Random generator = new Random();
			TreePath path = testsTree.getPathForRow(generator.nextInt(c));//get path to a random row in row count
			Object[] pathElements = path.getPath();
			String CurrentPath = pathElements[pathElements.length - 1].toString();

			while (CurrentPath.indexOf("test") == -1) { // If we didn't find a test
				path = testsTree.getPathForRow(generator.nextInt(c));
				pathElements = path.getPath();
				CurrentPath = pathElements[pathElements.length - 1].toString();
				System.out.println("CurrentPath " + CurrentPath);

			}
			System.out.println("selected random Node");
			foundPath = path;
		}
		//if a parentNode and a node had been passed, run the node under that 
		//specific Parent.
		else if(!node.equals("") && !parentNode.trim().equals("")){
			try{
				for (int i = 0; i < c; i++) {
					TreePath path = testsTree.getPathForRow(i);
					System.out.println("Path: " + path);
					Object[] pathElements = path.getPath();
					
					//if path to test has less then two elements it surely
					//doesn't have test and testClass in it to run.
					if (pathElements == null || pathElements.length <2) {
						continue;
					}
					
					Object node1 = pathElements[pathElements.length - 1];
					Object node2 = pathElements[pathElements.length - 2];
					if (node1 == null || node2 == null){
						continue;
					}
					
					//check that node and parent node exist.
					if (node1.toString().startsWith(node) && parentNode.equals(node2.toString()) && node1 != null && node2 != null) {
						foundPath = path;
						break;
					}
				}
			}catch (Exception e) {
				throw new Exception("My Exception \n\n"+StringUtils.getStackTrace(e));
			}			System.out.println("searched for test With parent ->"+parentNode+" , is empty= "+parentNode.trim().isEmpty());
		}else if (!node.equals("") && parentNode.trim().equals("")) {
			System.out.println("search for test Without parent");
			for (int i = 0; i < c; i++) {
				TreePath path = testsTree.getPathForRow(i);
				System.out.println("Path: " + path);
				Object[] pathElements = path.getPath();
				if (pathElements == null || pathElements.length == 0) {
					continue;
				}

				if (pathElements[pathElements.length - 1].toString().startsWith(node)) {
					foundPath = path;
					break;
				}
			}
			System.out.println("searched for test Without parent");
		}

		if (foundPath == null) {
			throw new Exception("Path not found node: " + node + ", parrent: " + parentNode);
		}
		return foundPath;
	}
}
