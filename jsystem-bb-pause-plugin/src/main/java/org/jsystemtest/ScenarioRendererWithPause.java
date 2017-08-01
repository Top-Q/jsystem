package org.jsystemtest;

import java.awt.Component;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioAsTest;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.treeui.teststable.ScenarioRenderer;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.teststable.TestType;
import jsystem.treeui.tree.ScenarioAsATestNode;

public class ScenarioRendererWithPause extends ScenarioRenderer {

	private final List<JTest> selectedTests;
	private IconsMapContainer iconsMapContainer;

	public ScenarioRendererWithPause(List<JTest> selectedTests) {

		this.selectedTests = selectedTests;
		iconsMapContainer = new IconsMapContainer();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		final Component component = super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row,
				hasFocus);

		ScenarioTreeNode node = (ScenarioTreeNode) value;
		ScenarioRenderer.TreeJPanel panel = ((ScenarioRenderer.TreeJPanel) component);

		// ScenarioHelpers.isScenarioAsTestAndNotRoot(test)
		// if (!(node.getTest() instanceof RunnerTest) || !(node.getTest()instanceof ScenarioAsTest)){
		
		//if (node.getTest() instanceof Scenario) {
		//if (node.getTest() instanceof ScenarioAsTest) {
		
		if (node.getTest() instanceof Scenario) {
			//if (((Scenario)node.getTest()).isScenarioAsTest()){
			
			if (((Scenario)node.getTest()).isScenarioAsTest()){
			//if (node.getTest() instanceof ScenarioAsTest) {
				
				//Scenario test = ((Scenario)node.getTest());
				//RunnerTest test = ((ScenarioAsTest) node.getTest()).getCurrentRunnerTest();
				//RunnerTest test = ScenarioHelpers.isScenarioAsTestAndNotRoot(node.getTest()) ? ((ScenarioAsTest) node.getTest()).getCurrentRunnerTest() : (RunnerTest) node.getTest();
				//RunnerTest test = ((ScenarioAsTest) node.getTest()).getCurrentRunnerTest();
				
				ScenarioAsTest scenarioAsTest = new ScenarioAsTest();
				scenarioAsTest.setCurrentRunnerTest((RunnerTest)node.getTest());
				RunnerTest test = scenarioAsTest.getCurrentRunnerTest();
				
				if (selectedTests.contains(test)) {
					iconStatusSetter(test, panel, iconsMapContainer.getPausedNormalMap());
					if (!test.isMarkedAsKnownIssue() && !test.isMarkedAsNegativeTest()) {
						iconStatusSetter(test, panel, iconsMapContainer.getPausedNormalMap());
					} else if (!test.isMarkedAsKnownIssue() && test.isMarkedAsNegativeTest()) {
						iconStatusSetter(test, panel, iconsMapContainer.getPausedMarkedNegMap());
					} else if (test.isMarkedAsKnownIssue() && !test.isMarkedAsNegativeTest()) {
						iconStatusSetter(test, panel, iconsMapContainer.getPausedMarkedIsseuMap());
					} else if (test.isMarkedAsKnownIssue() && test.isMarkedAsNegativeTest()) {
						iconStatusSetter(test, panel, iconsMapContainer.getPausedMarkedNegIssueMap());
					}
				}
			}
		}
		if (node.getTest() instanceof RunnerTest) {
			
			RunnerTest test = (RunnerTest) node.getTest();//ScenarioHelpers.isScenarioAsTestAndNotRoot(node.getTest()) ? ((ScenarioAsTest) node.getTest()).getCurrentRunnerTest() : (RunnerTest) node.getTest();
			if (selectedTests.contains(test)) {

				if (!test.isMarkedAsKnownIssue() && !test.isMarkedAsNegativeTest()) {
					iconStatusSetter(test, panel, iconsMapContainer.getPausedNormalMap());
				} else if (!test.isMarkedAsKnownIssue() && test.isMarkedAsNegativeTest()) {
					iconStatusSetter(test, panel, iconsMapContainer.getPausedMarkedNegMap());
				} else if (test.isMarkedAsKnownIssue() && !test.isMarkedAsNegativeTest()) {
					iconStatusSetter(test, panel, iconsMapContainer.getPausedMarkedIsseuMap());
				} else if (test.isMarkedAsKnownIssue() && test.isMarkedAsNegativeTest()) {
					iconStatusSetter(test, panel, iconsMapContainer.getPausedMarkedNegIssueMap());
				}
			}
		}

		// switch (((ScenarioAsTest) test).getCurrentRunnerTest())
		// RunnerTest test =
		// ScenarioHelpers.isScenarioAsTestAndNotRoot(node.getTest()) ?
		// ScenarioHelpers.getRunnerTest(node.getTest()) : (RunnerTest)
		// node.getTest();
		// RunnerTest test = ScenarioHelpers.getRunnerTest(node.getTest());
		// RunnerTest test = (RunnerTest) node.getTest();
		// boolean isScenarioAsTestNode =
		// ScenarioHelpers.isScenarioAsTestAndNotRoot(node.getTest());
		// ScenarioHelpers.getRunnerTest(node.getTest());
		// if(isScenarioAsTestNode){
		// if(selectedTests.contains((Scenario)node.getTest())){
		// iconStatusSetter((Scenario)node.getTest(), panel,
		// iconsMapContainer.getPausedNormalMap());
		// }
		// return component;
		// }

		return component;
	}

	private void iconStatusSetter(Scenario test, TreeJPanel panel, HashMap<String, ImageIcon> pausedIconMap) {

		ScenarioAsTest scenarioAsTest = new ScenarioAsTest();
		scenarioAsTest.setCurrentRunnerTest(test);
		RunnerTest test2 = scenarioAsTest.getCurrentRunnerTest();
		
		if (test2.isRunning()) {

			switch (test2.getStatus()) {

			case RunnerTest.STAT_FAIL: {
				panel.label.setIcon(pausedIconMap.get("pausedFailRun"));
			}
				break;
			case RunnerTest.STAT_SUCCESS: {
				panel.label.setIcon(pausedIconMap.get("pausedOkRun"));
			}
				break;
			case RunnerTest.STAT_ERROR: {
				panel.label.setIcon(pausedIconMap.get("pausedErrRun"));
			}
				break;
			case RunnerTest.STAT_WARNING: {
				panel.label.setIcon(pausedIconMap.get("pausedWarnRun"));
			}
				break;
			default:
				panel.label.setIcon(pausedIconMap.get("pausedPlainRun"));
			}
		} else if (!test2.isRunning()) {
			switch (test2.getStatus()) {

			case RunnerTest.STAT_FAIL: {
				panel.label.setIcon(pausedIconMap.get("pausedFail"));
			}
				break;
			case RunnerTest.STAT_SUCCESS: {
				panel.label.setIcon(pausedIconMap.get("pausedOk"));
			}
				break;
			case RunnerTest.STAT_ERROR: {
				panel.label.setIcon(pausedIconMap.get("pausedErr"));
			}
				break;
			case RunnerTest.STAT_WARNING: {
				panel.label.setIcon(pausedIconMap.get("pausedWarn"));
			}
				break;
			default:
				panel.label.setIcon(pausedIconMap.get("pausedPlain"));
			}
		}
	}

	//

	private void iconStatusSetter(RunnerTest test, TreeJPanel panel, HashMap<String, ImageIcon> pausedIconMap) {
		if (test.isRunning()) {

			switch (test.getStatus()) {

			case RunnerTest.STAT_FAIL: {
				panel.label.setIcon(pausedIconMap.get("pausedFailRun"));
			}
				break;
			case RunnerTest.STAT_SUCCESS: {
				panel.label.setIcon(pausedIconMap.get("pausedOkRun"));
			}
				break;
			case RunnerTest.STAT_ERROR: {
				panel.label.setIcon(pausedIconMap.get("pausedErrRun"));
			}
				break;
			case RunnerTest.STAT_WARNING: {
				panel.label.setIcon(pausedIconMap.get("pausedWarnRun"));
			}
				break;
			default:
				panel.label.setIcon(pausedIconMap.get("pausedPlainRun"));
			}
		} else if (!test.isRunning()) {
			switch (test.getStatus()) {

			case RunnerTest.STAT_FAIL: {
				panel.label.setIcon(pausedIconMap.get("pausedFail"));
			}
				break;
			case RunnerTest.STAT_SUCCESS: {
				panel.label.setIcon(pausedIconMap.get("pausedOk"));
			}
				break;
			case RunnerTest.STAT_ERROR: {
				panel.label.setIcon(pausedIconMap.get("pausedErr"));
			}
				break;
			case RunnerTest.STAT_WARNING: {
				panel.label.setIcon(pausedIconMap.get("pausedWarn"));
			}
				break;
			default:
				panel.label.setIcon(pausedIconMap.get("pausedPlain"));
			}
		}
	}
}

