package jsystem.treeui.teststable;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.TestsContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.treeui.TestRunner;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

/**
 * Allows user to define building blocks in the tests table as building blocks
 * that should pause the scenario execution.
 * 
 * @author Itai Agmon
 *
 */
public class PauseBBContextMenuPlugin implements ContextMenuPlugin, ExtendTestListener, ScenarioListener {

	private TestsTableController testsTableController;

	private final List<JTest> selectedTests = new ArrayList<JTest>();

	/**
	 * Is the current action is done on tests that are already selected.
	 */
	private boolean onSelectedTests;

	@Override
	public void init(TestsTableController testsTableController) {
		this.testsTableController = testsTableController;
	}

	@Override
	public boolean shouldDisplayed(ScenarioTreeNode currentNode, TestsContainer container, JTest test) {
		if (selectedTests.contains(test)) {
			onSelectedTests = true;
		} else {
			onSelectedTests = false;
		}
		return true;
	}

	@Override
	public String getItemName() {
		if (onSelectedTests) {
			return "Continue this test";
		}
		return "Pause In this test";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (null == testsTableController.selectedTests || testsTableController.selectedTests.length == 0) {
			return;
		}
		for (JTest test : testsTableController.selectedTests) {
			if (selectedTests.contains(test)) {
				selectedTests.remove(test);
			} else {
				selectedTests.add(test);
			}
		}
	}

	@Override
	public void startTest(TestInfo testInfo) {
		if (null == selectedTests || selectedTests.size() == 0) {
			return;
		}
		for (JTest test : selectedTests) {
			if (test.getFullUUID().equals(testInfo.fullUuid)) {
				TestRunner.treeView.pause();
			}
		}
	}

	@Override
	public void scenarioChanged(Scenario current, ScenarioChangeType changeType) {
		switch (changeType) {
		case CURRENT:
		case NEW:
			selectedTests.clear();
		default:
		}
	}

	// ************* Unused methods *************

	@Override
	public void addError(Test arg0, Throwable arg1) {
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
	}

	@Override
	public void endTest(Test arg0) {
	}

	@Override
	public void startTest(Test arg0) {
	}

	@Override
	public void addWarning(Test test) {
	}

	@Override
	public void endRun() {
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
	}

	@Override
	public void startContainer(JTestContainer container) {
	}

	@Override
	public void endContainer(JTestContainer container) {
	}

	@Override
	public void scenarioDirectoryChanged(File directory) {
	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues, Parameter[] newValues) {
	}

	// **************************************************
}
