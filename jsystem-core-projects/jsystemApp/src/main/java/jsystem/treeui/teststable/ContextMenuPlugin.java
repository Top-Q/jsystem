package jsystem.treeui.teststable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.TestsContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.treeui.TestRunner;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class ContextMenuPlugin implements ActionListener, ExtendTestListener {

	private final TestsTableController testsTableController;

	private int counter;

	public ContextMenuPlugin(TestsTableController testsTableController) {
		super();
		this.testsTableController = testsTableController;
	}

	public boolean shouldDisplayed(ScenarioTreeNode currentNode, TestsContainer container, JTest test) {
		return true;
	}

	public String getItemName() {
		return "My plugin";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("******* ACTION PERFORMED ********" + e.getSource());
		System.out.println("******  Selected tests: " + Arrays.toString(testsTableController.selectedTests));
	}

	@Override
	public void startTest(TestInfo testInfo) {
		System.out.println("*****  Starting test " + testInfo.fullUuid + " *******");
		if (counter++ == 3) {
			TestRunner.treeView.pause();
//			TestRunner.treeView.setPaused(true);
//			TestRunner.treeView.getRunner().handleEvent(TestRunner.PAUSE_EVENT, null);
		}

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
	}

	@Override
	public void addWarning(Test test) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endRun() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub

	}

}
