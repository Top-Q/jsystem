package org.jsystemtest;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;

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
import jsystem.treeui.teststable.ContextMenuPlugin;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.teststable.TestsTableController;
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

	private final List<JTest> selectedTests = new TestList();

	private ImageIcon pauseIcon;

	private ImageIcon playIcon;

	/**
	 * The current icon that should be displayed. It can be pause or play
	 * according to the selected test status.
	 */
	private ImageIcon currentIcon;

	/**
	 * Is the current action is done on tests that are already selected.
	 */
	private boolean onSelectedTests;

	@Override
	public void init(TestsTableController testsTableController) {
		this.testsTableController = testsTableController;
		try {
			pauseIcon = new ImageIcon(
			
					IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("pause.gif")));
			playIcon = new ImageIcon(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("play.gif")));
			
			testsTableController.getTree().setCellRenderer(new ScenarioRendererWithPause(selectedTests));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldDisplayed(ScenarioTreeNode currentNode, TestsContainer container, JTest test) {
		if (selectedTests.contains(test)) {
			onSelectedTests = true;
			currentIcon = playIcon;
		} else {
			onSelectedTests = false;
			currentIcon = pauseIcon;
		}
		return true;
	}

	@Override
	public String getItemName() {
		if (onSelectedTests) {
			return "Continue in This Item";
		}
		return "Pause Before This Item";
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (null == testsTableController.getSelectedTests() || testsTableController.getSelectedTests().length == 0) {
			return;
		}
		for (JTest test : testsTableController.getSelectedTests()) {
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
		case RESET_DIRTY:
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

	@Override
	public ImageIcon getIcon() {
		return currentIcon;
	}

	// **************************************************
}
