/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.teststable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerFixture;
import jsystem.framework.scenario.RunnerScript;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntDataDriven;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.scenario.flow_control.AntIfCondition;
import jsystem.framework.scenario.flow_control.AntIfElse;
import jsystem.framework.scenario.flow_control.AntIfElseIf;
import jsystem.framework.scenario.flow_control.AntSwitch;
import jsystem.framework.scenario.flow_control.AntSwitchCase;
import jsystem.framework.scenario.flow_control.AntSwitchDefault;
import jsystem.framework.scripts.ScriptEngine;
import jsystem.framework.scripts.ScriptsEngineManager;
import jsystem.framework.sut.ChangeSutTest;
import jsystem.treeui.images.ImageCenter;

/**
 * NodeRenderer class This class implements the look of the node in the tree
 */
public class ScenarioRenderer implements TreeCellRenderer {

	protected boolean bSelected = false;

	protected boolean bFocus = false;

	protected int checkX;

	public ScenarioRenderer() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		ScenarioTreeNode node = (ScenarioTreeNode) value;
		JTest test = node.getTest();
		String stringValue = ((ScenarioTreeNode) value).getName();

		/**
		 * don`t add check box if the node is fixture
		 */
		if (test instanceof RunnerFixture) {
			leaf = false;
		}

		TreeJPanel panel = new TreeJPanel(leaf);
		panel.setEnabled(tree.isEnabled());

		bSelected = isSelected;
		bFocus = hasFocus;

		panel.check.setSelected(((ScenarioTreeNode) value).isSelected());
		panel.setOpaque(false);
		panel.label.setOpaque(false);
		panel.check.setBackground(Color.white);
		panel.check.setOpaque(false);
		panel.label.setFont(tree.getFont());

		/**
		 * give only Test index
		 */
		if (!node.isJTestContainer() && !(node.getTest() instanceof RunnerFixture)) {
			int index = ScenariosManager.getInstance().getCurrentScenario().getPresentationIndex(test);
			stringValue = "(" + index + ") " + stringValue;
		}

		if ((node.getTest() instanceof Scenario) && ScenariosManager.isDirty() && ((Scenario) node.getTest()).isRoot()) {
			stringValue = stringValue + "**";
		}

		if (node.getTest().isHiddenInHTML()) {
			stringValue += "(h)";
		}

		panel.label.setText(stringValue);
		if (test.isValidationErrorsFound() && !test.isRunning()) {
			panel.label.setColor(Color.red);
		} else {
			panel.label.setColor(Color.black);
		}
		panel.check.setVisible(true);
		if (leaf) {
			panel.label.setIcon(UIManager.getIcon("Tree.leafIcon"));
		} else if (expanded) {
			panel.label.setIcon(UIManager.getIcon("Tree.openIcon"));
		} else {
			panel.label.setIcon(UIManager.getIcon("Tree.closedIcon"));
		}

		if (node.isJTestContainer()) { // IS FLOW \ SCENARIO
			if (node.getTest().getClass() == AntForLoop.class) { // FOR LOOP
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FOR_LOOP));
				panel.setToolTipText(((AntForLoop) test).defaultComment(true));
			} else if (node.getTest().getClass() == AntDataDriven.class) { //  DATA DRIVEN
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_DATA_DRIVEN));
			} else if (node.getTest().getClass() == AntSwitch.class) { // SWITCH
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SWITCH));
			} else if (node.getTest().getClass() == AntSwitchDefault.class) { // SWITCH
																				// DEFAULT
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SWITCH));
			} else if (node.getTest().getClass() == AntSwitchCase.class) { // SWITCH
																			// CASE
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SWITCH));
			} else if (node.getTest().getClass() == AntIfCondition.class) { // IF
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));
			} else if (node.getTest().getClass() == AntIfElse.class) { // IF
																		// ELSE
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));
			} else if (node.getTest().getClass() == AntIfElseIf.class) { // IF
																			// ELSE
																			// IF
				panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_IF_CONDITION));

			} else { // SCENARIO
				panel.label.setIcon(ImageCenter.getInstance().getImage(TestType.getMatchingIcon(test)));
			}
		} else {

			if (test instanceof RunnerFixture) { // FIXTURE
				if (test.isRunning()) {
					panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE_RUNNING));
				} else if (test.isError()) {
					panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE_FAILD));
				} else if (test.isFail()) {
					panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE_FAILD));
				} else if (test.isSuccess()) {
					panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE_PASSED));
				} else {
					panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_FIXTURE));
				}

				RunnerFixture runnerFixture = (RunnerFixture) test;
				String params = runnerFixture.getPropertiesAsString();
				if (params != null && !params.equals("")) {
					panel.setToolTipText(params);
				}

			} else if (test instanceof RunnerScript) { // SCRIPT
				RunnerScript script = (RunnerScript) test;
				ScriptEngine engine = ScriptsEngineManager.getInstance().findExecutor(
						script.getExecutor().getClass().getName());
				ImageIcon icon = null;
				if (test.isRunning()) {
					icon = engine.getRunningImageIcon();
					if (icon == null) {
						icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SCRIPT_RUN);
					}
				} else if (test.isError()) {
					icon = engine.getErrorImageIcon();
					if (icon == null) {
						icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SCRIPT_ERR);
					}
				} else if (test.isFail()) {
					icon = engine.getFailImageIcon();
					if (icon == null) {
						icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SCRIPT_FAIL);
					}
				} else if (test.isSuccess()) {
					icon = engine.getOKImageIcon();
					if (icon == null) {
						icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SCRIPT_OK);
					}
				} else {
					icon = engine.getBasicImageIcon();
					if (icon == null) {
						icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SCRIPT);
					}
				}
				panel.label.setIcon(icon);

				String params = script.getPropertiesAsString();
				if (params != null && !params.equals("")) {
					panel.setToolTipText(params);
				}
			} else { // RUNNER TEST
				if (((RunnerTest) test).getClassName().equals(ChangeSutTest.class.getName())

				&& ((RunnerTest) test).getMethodName().equals("changeSut")) { // CHANGE
																				// SUT
																				// EVENT
					if (test.isRunning()) {
						panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT_RUNNING));
					} else if (test.isError()) {
						panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT_ERROR));
					} else if (test.isFail()) {
						panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_TEST_FAILER));
					} else if (test.isSuccess()) {
						panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT_PASSED));
					} else {
						panel.label.setIcon(ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT));
					}

				} else { // REGULAR TEST
					panel.label.setIcon(ImageCenter.getInstance().getImage(TestType.getMatchingIcon(test)));
				}
				RunnerTest runnerTest = (RunnerTest) test;
				String params = runnerTest.getPropertiesAsString();
				if (runnerTest.isValidationErrorsFound()) {
					panel.setToolTipText(runnerTest.getValidationErrorsAsString());
				} else if (params != null && !params.isEmpty()) {
					panel.setToolTipText(params.replace("\n", ","));
				}

			}

		}

		return panel;
	}

	/* This class will hold the text and tree table */
	public class TreeLabel extends JLabel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6064176461779247836L;

		boolean hasFocus = false;

		Color color = null;

		TreeLabel() {
		}

		public void setColor(Color color) {
			this.color = color;
			setForeground(color);
		}

		@Override
		public void paint(Graphics g) {

			if (bSelected) {
				g.setColor(Color.lightGray);
				Dimension d = getPreferredSize();
				int imageOffset = 0;
				Icon currentI = getIcon();

				if (currentI != null) {
					imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
				}

				g.fillRect(imageOffset, 0, d.width - imageOffset, d.height);

			}
			super.paint(g);
		}
	}

	public class TreeJPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1361397284816688457L;

		public JCheckBox check;;

		public TreeLabel label;

		boolean isLeaf;

		public TreeJPanel(boolean isLeaf) {
			this.isLeaf = isLeaf;
			check = new JCheckBox();
			label = new TreeLabel();
			if (isLeaf) {
				add(check);
			}
			add(label);
		}

		public Dimension getPreferredSize() {
			if (!isLeaf) {
				return label.getPreferredSize();
			} else {
				Dimension d_check = check.getPreferredSize();
				Dimension d_label = label.getPreferredSize();
				return new Dimension(d_check.width + d_label.width, (d_check.height < d_label.height ? d_label.height
						: d_check.height));
			}
		}

		public void doLayout() {
			if (!isLeaf) {
				Dimension d_label = label.getPreferredSize();

				int y_label = 0;

				label.setLocation(0, y_label);
				label.setBounds(0, y_label, d_label.width, d_label.height);
			} else {
				Dimension d_check = check.getPreferredSize();
				Dimension d_label = label.getPreferredSize();

				int y_check = 0;
				int y_label = 0;

				check.setBounds(0, y_check, d_check.width, d_check.height);
				label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
			}
		}
	}
}
