package com.aqua.jsystemobjects.handlers;

import java.util.Random;
import java.util.logging.Logger;

import javax.swing.tree.TreePath;

import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.scenario.JTestContainer;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.TestRunner;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.jsystem.jemmyHelpers.TipNameButtonFinder;
import org.jsystem.objects.handlers.HandlerBasic;
import org.netbeans.jemmy.operators.JDialogOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import com.aqua.jsystemobjects.TestType;

public class BaseHandler extends HandlerBasic implements ExecutionListener{
	protected boolean executionEnded = false;
	TestRunner runner;
	protected Logger log = Logger.getLogger(BaseHandler.class.getName());
	protected static JsystemMapping jmap;
	String mainWindow;
	volatile boolean runEnd = false;
	volatile int runEndCount;
	volatile boolean executionEnd = false;
	protected static JTreeOperator testsTree;
	protected static JTreeOperator scenarioTree;
	boolean launched = false;
	
	public BaseHandler(){
		jmap = new JsystemMapping();
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
		if (TestType.SCENARIO.getType().equals(parentNode) || TestType.SCRIPT.getType().equals(parentNode) || TestType.RANDOM.getType().equals(parentNode)){
			parentNode = "";
		}
		jemmySupport.report("mainFrame is null? "+(mainFrame==null));
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
	
	public void exit() throws Exception{
		mainFrame.close();
		JDialogOperator dialog = jemmySupport.getDialogIfExists("JSystem",5);
		jemmySupport.pushButton(dialog, new TipNameButtonFinder("Yes"));
	}


	//----------------------------------------------------------------------------
			//
	//----------------------------------------------------------------------------
	
	@Override
	public void errorOccured(String title, String message, ErrorLevel level) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public synchronized  void executionEnded(String scenarioName) {
		executionEnded = true;
		notifyAll();
	}


	@Override
	public void remoteExit() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void remotePause() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addWarning(Test test) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void endRun() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startTest(TestInfo testInfo) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addError(Test arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void endTest(Test arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void startTest(Test arg0) {
		// TODO Auto-generated method stub
		
	}
}
