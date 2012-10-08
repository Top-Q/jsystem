/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerSOTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.ScenarioUIUtils;
import jsystem.treeui.TestTreeModel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.SwingUtils;

/**
 * Created by IntelliJ IDEA. User: michaelo Date: Dec 10, 2004 Time: 2:46:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestTreePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9026682139177791388L;

	private JTree tree;

	private TestTreeModel treeModel;

	private JScrollPane scrollTree;

	public static long currentSelectedTests = 0;

	JPanel iPanel;

	public TestTreePanel() {
		super(new BorderLayout());
		setPreferredSize(new Dimension(300, 100));

		treeModel = new TestTreeModel();

		// tree configuration
		tree = new JTree();
		tree.setModel(treeModel);
		tree.setRootVisible(false);
		//The tree name is used for automation purpose, do not remove it.
		tree.setName(JsystemMapping.getInstance().getTestsTreeName());
		tree.setShowsRootHandles(true);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.setCellRenderer(new NodeRenderer());
		

		scrollTree = SwingUtils.getJScrollPaneWithWaterMark(ImageCenter.getInstance().getAwtImage(
				ImageCenter.ICON_TEST_TREE_BG), tree);

		scrollTree.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scrollTree);
	}

	public void showLoadFailIfExists() {
		if (AssetNode.isErrorsFound()) {
			ScenarioUIUtils.showErrorDialog(AssetNode.getLoadsErrors());
		}
	}

	public JTree getTree() {
		return tree;
	}

	public void refreshTree() {
		treeModel = new TestTreeModel();
		tree.setModel(treeModel);
	}

	public TestTreeModel getTreeModel() {
		return treeModel;
	}

	public void collectSelectedTests(Vector<JTest> v, AssetNode node) {
		if (node == null) {
			AssetNode n = (AssetNode) treeModel.getRoot();
			collectSelectedTests(v, n);
		} else if (node instanceof SystemObjectMethod){
			if (node.isSelected()) {
				RunnerSOTest sonode = (RunnerSOTest)((SystemObjectMethod)node).getTest();
				v.addElement(sonode.cloneTest());
			}
		} else if (node instanceof TestNode) {
			TestNode tn = (TestNode) node;
			if (tn.isSelected()) {

				/**
				 * create new instance of the test to add
				 */
				RunnerTest newTestInstance = new RunnerTest(tn.getTest().getClassName(), tn.getTest().getMethodName());
				v.addElement(newTestInstance);
			}
		} else if (node instanceof ScenarioNode) {
			ScenarioNode tn = (ScenarioNode) node;
			if (tn.isSelected()) {
				Scenario current = ScenariosManager.getInstance().getCurrentScenario();
				try {
					Scenario s = new Scenario(current.getScenariosDir(), tn.getScenarioName(), null);
					v.addElement(s);
				} catch (Exception e) {
					ErrorPanel.showErrorDialog("Failed to create a scenario", e, ErrorLevel.Error);
				}
			}
		} else if (node instanceof FixtureNode) {
			FixtureNode fn = (FixtureNode) node;
			if (fn.isSelected()) {
				v.addElement(fn.getFixture());
			}
		} else if(node instanceof ScriptNode){
			ScriptNode tn = (ScriptNode) node;
			if (tn.isSelected()) {

				/**
				 * create new instance of the test to add
				 */
				RunnerTest newTestInstance = tn.getRunnerScript();
				newTestInstance.setTest(tn.getRunnerScript().getExecutor());
				v.addElement(newTestInstance);
			}
			
		} else {
			for (int i = 0; i < node.getChildCount(); i++) {
				collectSelectedTests(v, (AssetNode) node.getChildAt(i));
			}
		}
	}

	public void unselectAll() {
		AssetNode n = (AssetNode) treeModel.getRoot();
		n.setSelected(false);
		currentSelectedTests = 0;
	}

	public static long getCurrentSelectedTests() {
		return currentSelectedTests;
	}
}
