/*
 * Created on Nov 25, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.HTMLJavaDocView;
import jsystem.treeui.ProgressController;
import jsystem.treeui.TestFilterManager;
import jsystem.treeui.TestTreeModel;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.actionItems.RefreshAction;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;
import junit.framework.SystemTest;

/**
 * Control the tests tree tab.
 * 
 * @author guy.arieli
 * 
 */
public class TestsTreeController implements TreeSelectionListener, MouseListener, ActionListener, KeyListener {

	private static Logger log = Logger.getLogger(TestsTreeController.class.getName());

	private TestTreePanel testBrowser;

	private TestsTreeListener listener;

	private AssetNode currentNode = null;

	private JMenuItem popupCheckItem;

	private JMenuItem popupUncheckItem;

	private JPopupMenu popupMenu;

	private JPopupMenu rootPopupMenu;

	private JMenuItem popupCheckItem2;

	private JMenuItem popupUncheckItem2;

	private JMenuItem popupCollapseTree;

	private JMenuItem popupExpandTree;

	private JMenuItem popupCollapseTree2;

	private JMenuItem popupExpandTree2;

	private JButton addButton;

	private JSpinner testsNumSpinner;

	private int testsCounter;

	private JSplitPane main;

	private JComboBox filter;

	private JToggleButton sutFilter;

	private JToggleButton sort;

	private HTMLJavaDocView nodeInformation;

	public TestsTreeController(TestsTreeListener listener) {
		this.listener = listener;
		testBrowser = new TestTreePanel();
		testBrowser.showLoadFailIfExists();
		testBrowser.getTree().addMouseListener(this);
		testBrowser.getTree().addTreeSelectionListener(this);

		createPopup();
		main = new JSplitPane();
		main.setOrientation(JSplitPane.VERTICAL_SPLIT);
		main.setDividerLocation(30);

		main.setOneTouchExpandable(false);
		main.setDividerSize(0);

		JSplitPane treeAndTestInfo = new JSplitPane();
		treeAndTestInfo.setOrientation(JSplitPane.VERTICAL_SPLIT);
		treeAndTestInfo.setDividerSize(10);
		treeAndTestInfo.setDividerLocation(700);
		treeAndTestInfo.setOneTouchExpandable(true);
		treeAndTestInfo.setBackground(new Color(0xf6, 0xf6, 0xf6));
		treeAndTestInfo.setTopComponent(new JScrollPane(testBrowser));
		treeAndTestInfo.setResizeWeight(0.85);

		//This will make sure that the test/scenario info pane is expandable.
		BasicSplitPaneUI ui = (BasicSplitPaneUI) treeAndTestInfo.getUI();
		BasicSplitPaneDivider divider = ui.getDivider();
		divider.setLocation(0, 700);
		JButton button = (JButton) divider.getComponent(1);
		button.doClick();

		// Add scenario/test information view
		nodeInformation = new HTMLJavaDocView(new Color(0xf6, 0xf6, 0xf6));
		nodeInformation.setToolTipText(JsystemMapping.getInstance().getBuildingBlockInformationToolTip());
		nodeInformation.setOpaque(true);
		nodeInformation.setEditable(false);
		JScrollPane nodeInformationPane = SwingUtils.getJScrollPaneWithWaterMark(
				ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_TEST_TREE_BG), nodeInformation);
		nodeInformationPane.setOpaque(true);
		treeAndTestInfo.setBottomComponent(nodeInformationPane);

		main.setBottomComponent(treeAndTestInfo);
		main.setTopComponent(createToolBar());

		addButton.setEnabled(false);
		testsNumSpinner.setEnabled(false);
		TestFilterManager.getInstance().init(this);

	}

	public JSplitPane getTreePanel() {
		return main;
	}

	/**
	 * Reloads the view by updating the tree and clears the report view
	 */
	public void refreshView() {
		// reload tree
		testBrowser.refreshTree();
		testBrowser.getTree().repaint();
		testBrowser.getTree().removeTreeSelectionListener(this);
		testBrowser.getTree().clearSelection();
		testBrowser.getTree().addTreeSelectionListener(this);
	}

	public void repaintTree() {
		testBrowser.getTree().repaint();
	}

	public TestTreeModel getTreeModel() {
		return testBrowser.getTreeModel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 * Impelemnts the mause pressed action
	 * 
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	/**
	 * Handles the changes on the tree
	 * 
	 * @param e
	 *            TreeSelectionEvent
	 */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// save the selected node
		currentNode = (AssetNode) testBrowser.getTree().getLastSelectedPathComponent();
		if (nodeInformation.getParent().getHeight() <= 0) {
			nodeInformation.setContent("");
			return;
		}
		nodeInformation.setContent(getCurrentNodeInformation());

	}

	/**
	 * Collects the current node information according to the node type and
	 * returns the information as HTML formatted string
	 * 
	 * @return HTML string that represents the current node information
	 */
	private String getCurrentNodeInformation() {
		if (null == currentNode){
			return "";
		}
		final StringBuilder info = new StringBuilder();
		info.append("<b>").append(currentNode.toString()).append("</b><br>");
		if (currentNode instanceof TestNode) {
			RunnerTest test = ((TestNode) currentNode).getTest();
			final String className = test.getClassName();
			final String methodName = test.getMethodName();
			info.append("<b>Class:</b> ").append(className).append("<br>");
			info.append("<b>Method:</b> ").append(methodName).append("<br>");
			final long lastRunTime = ProgressController.getTestTime(className, methodName);
			if (lastRunTime > 0) {
				info.append("<b>Last run time:</b> ").append(lastRunTime / 1000).append(" seconds<br>");

			}
			String[] retParams = test.getReturnParameters();
			if (retParams != null && retParams.length > 0) {
				info.append("<b>Return params: </b>")
						.append(StringUtils.objectArrayToString(",", (Object[]) retParams)).append("<br>");
			}
			if (test.getTest() instanceof SystemTest) {
				SystemTest sTest = (SystemTest) test.getTest();
				info.append("<b>Fixture: </b>").append(sTest.getFixture().getName()).append("<br>");
				Class<?> ff = sTest.getTearDownFixture();
				if (ff != null) {
					info.append("<b>Fail to fixture: </b>").append(ff.getName()).append("<br>");
				}
			}

			try {
				String doc = HtmlCodeWriter.getInstance().getMethodJavaDoc(className, methodName);
				if (!StringUtils.isEmpty(doc)) {
					info.append("<b>Documentation:</b><br>").append(doc.replaceAll("\n", "<br>"));
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to get test documentation", e);
			}
		} else if (currentNode instanceof ScenarioNode) {
			String doc = ScenarioHelpers.getTestProperty(null, ((ScenarioNode) currentNode).getScenarioName(),
					RunningProperties.DOCUMENTATION_TAG);
			if (!StringUtils.isEmpty(doc)) {
				info.append("<b>User Documentation:</b><br>").append(doc);
			}
		}
		return info.toString();
	}

	/**
	 * Shows the tree popup menu
	 * 
	 * @param e
	 *            MouseEvent
	 */
	private void showPopup(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// save the path for future use
		TreePath clickedPath = testBrowser.getTree().getPathForLocation(x, y);
		if (clickedPath == null)
			return;

		// save the selected node
		currentNode = (AssetNode) clickedPath.getLastPathComponent();

		// if this is the right button
		if (e.getButton() == MouseEvent.BUTTON3) {

			// get position

			testBrowser.getTree().setSelectionPath(clickedPath);
			testBrowser.getTree().scrollPathToVisible(clickedPath);

			// show the popup menu
			if (currentNode instanceof TestNode || currentNode instanceof ScenarioNode
					|| currentNode instanceof FixtureNode || currentNode instanceof TestNode) {
				popupCollapseTree.setVisible(false);
				popupExpandTree.setVisible(false);
			} else {
				popupCollapseTree.setVisible(true);
				popupExpandTree.setVisible(true);
			}
			if (currentNode.isClassPath()) {
				rootPopupMenu.show(testBrowser.getTree(), x, y);
			} else {
				popupMenu.show(testBrowser.getTree(), x, y);
			}
		} else if (e.getButton() == MouseEvent.BUTTON1) {

			if (currentNode instanceof FixtureNode) {
				AssetNode n = currentNode;
				n.setSelected(!n.isSelected);
				testBrowser.getTree().repaint();
			}
			if (currentNode instanceof ScriptNode) {
				AssetNode n = currentNode;
				n.setSelected(!n.isSelected);
				testBrowser.getTree().repaint();
			}

			if (currentNode instanceof TestNode) {
				TestNode n = (TestNode) currentNode;
				n.setSelected(!n.isSelected());

				testBrowser.getTree().repaint();
			} else if (currentNode instanceof ScenarioNode) {
				ScenarioNode n = (ScenarioNode) currentNode;
				n.setSelected(!n.isSelected());

				testBrowser.getTree().repaint();
			}

			boolean enable = (TestTreePanel.currentSelectedTests > 0) && "true".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT));

			addButton.setEnabled(enable);
			testsNumSpinner.setEnabled(enable);
		}
	}

	/**
	 * Creates the popup menu
	 */
	private void createPopup() {

		popupMenu = new JPopupMenu();
		rootPopupMenu = new JPopupMenu();

		popupCheckItem = new JMenuItem("Check Selection");
		popupCheckItem.addActionListener(this);
		popupUncheckItem = new JMenuItem("Uncheck Selection");
		popupUncheckItem.addActionListener(this);

		popupCollapseTree = new JMenuItem("Collapse tree");
		popupCollapseTree.addActionListener(this);
		popupExpandTree = new JMenuItem("Expand tree");
		popupExpandTree.addActionListener(this);

		popupMenu.add(popupCollapseTree);
		popupMenu.add(popupExpandTree);
		popupMenu.add(popupCheckItem);
		popupMenu.add(popupUncheckItem);

		popupCheckItem2 = new JMenuItem("Check Selection");
		popupCheckItem2.addActionListener(this);
		popupUncheckItem2 = new JMenuItem("Uncheck Selection");
		popupUncheckItem2.addActionListener(this);

		popupCollapseTree2 = new JMenuItem("Collapse tree");
		popupCollapseTree2.addActionListener(this);
		popupExpandTree2 = new JMenuItem("Expand tree");
		popupExpandTree2.addActionListener(this);
		rootPopupMenu.add(popupCollapseTree2);
		rootPopupMenu.add(popupExpandTree2);
		rootPopupMenu.add(popupCheckItem2);
		rootPopupMenu.add(popupUncheckItem2);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

	/**
	 * Expands the tree
	 */
	public void expandTree() {
		JTree tree = testBrowser.getTree();

		if (tree == null) {
			return;
		}

		int row = 0;

		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	public void expandTree(int row) {
		JTree tree = testBrowser.getTree();

		if (tree == null) {
			return;
		}
		tree.expandRow(row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(popupCheckItem) || e.getSource().equals(popupCheckItem2)) {

			currentNode.setSelected(true);
			testBrowser.getTree().repaint();

		} else if (e.getSource().equals(popupUncheckItem) || e.getSource().equals(popupUncheckItem2)) {
			currentNode.setSelected(false);
			testBrowser.getTree().repaint();

		} else if (e.getSource().equals(popupCollapseTree) || e.getSource().equals(popupCollapseTree2)) {
			expandAll(testBrowser.getTree(), testBrowser.getTree().getSelectionPath(), false);

		} else if (e.getSource().equals(popupExpandTree) || e.getSource().equals(popupExpandTree2)) {
			expandAll(testBrowser.getTree(), testBrowser.getTree().getSelectionPath(), true);
			testBrowser.getTree().repaint();
		} else if (e.getSource().equals(addButton)) {
			WaitDialog.launchWaitDialog(JsystemMapping.getInstance().getAddTestsDialog(), null);
			(new Thread() {
				@Override
				public void run() {
					try {
						Vector<JTest> v = new Vector<JTest>();
						Vector<JTest> all = new Vector<JTest>();
						boolean success = true;
						testBrowser.collectSelectedTests(v, null);
						Collections.reverse(v); // add Tests in reverse order to
												// get tests in the same order
												// they appear in the tree
						for (int i = 0; i < Integer.valueOf(testsNumSpinner.getValue().toString()) && success; i++) {
							all.addAll(v);
						}
						testsCounter = ScenariosManager.getInstance().getCurrentScenario().getTests().size();
						testsCounter += all.size();
						int max = Integer.parseInt(JSystemProperties.getInstance().getPreference(
								FrameworkOptions.MAX_BUILDING_BLOCKS_NUMBER));
						if (testsCounter <= max)
							success = listener.addTests(all);
						else {
							success = false;
							WaitDialog.endWaitDialog();
							ListenerstManager
									.getInstance()
									.showConfirmDialog(
											"Error",
											"Exceeded max building blocks number, couldn't add the selected tests to the scenario",
											JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);

						}

						if (success) {
							testBrowser.unselectAll();
							testsNumSpinner.setValue(Integer.valueOf(1));
							addButton.setEnabled(false);
							testsNumSpinner.setEnabled(false);
							TestTreePanel.currentSelectedTests = 0;
						}
						testBrowser.getTree().repaint();
					} finally {
						WaitDialog.endWaitDialog();
					}
				}
			}).start();

		} else if (e.getSource().equals(filter)) {
			keyReleased(null);
		} else if (e.getSource().equals(sutFilter)) {
			JSystemProperties.getInstance().setPreference(FrameworkOptions.FILTER_SUT_IN_ASSETS_TREE,
					Boolean.toString(sutFilter.isSelected()));
			RefreshAction.getInstance().actionPerformed(null);
		} else if (e.getSource().equals(sort)) {
			JSystemProperties.getInstance().setPreference(FrameworkOptions.SORT_ASSETS_TREE,
					Boolean.toString(sort.isSelected()));
			RefreshAction.getInstance().actionPerformed(null);
		}
		boolean enable = (TestTreePanel.getCurrentSelectedTests() > 0) && "true".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT));
		addButton.setEnabled(enable);
		testsNumSpinner.setEnabled(enable);
	}

	private JToolBar createToolBar() {
		JToolBar toolBar = SwingUtils.getJToolBarWithBgImage("tests tree", JToolBar.HORIZONTAL, ImageCenter
				.getInstance().getImage(ImageCenter.ICON_SCEANRIO_TOOLBAR_BG));

		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		toolBar.setRollover(true);

		addButton = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_ADD));
		addButton.setToolTipText(JsystemMapping.getInstance().getAddTestsButton());
		addButton.setBackground(new Color(0, 0, 0, 0));
		addButton.addActionListener(this);
		toolBar.add(addButton);

		String value = JSystemProperties.getInstance().getPreference(FrameworkOptions.MAX_BUILDING_BLOCKS_NUMBER);
		if (value == null) {
			value = ((Integer) FrameworkOptions.MAX_BUILDING_BLOCKS_NUMBER.getDefaultValue()).toString();
			JSystemProperties.getInstance().setPreference(FrameworkOptions.MAX_BUILDING_BLOCKS_NUMBER, value);
		}
		int max = Integer.parseInt(value);

		testsNumSpinner = new JSpinner(new SpinnerNumberModel(1, 1, max, 1));
		testsNumSpinner.setName("testsNumSpinner");
		testsNumSpinner.setToolTipText("Number of tests to be added to scenario tree");
		toolBar.add(testsNumSpinner);

		toolBar.addSeparator(new Dimension(40, 0));
		filter = new JComboBox(GroupsManager.getInstance().getGroups());
		filter.setEditable(true);
		filter.setToolTipText(JsystemMapping.getInstance().getFilterToolTip());
		filter.getEditor().getEditorComponent().addKeyListener(this);
		((JTextField) filter.getEditor().getEditorComponent()).setColumns(10);
		filter.addActionListener(this);
		// filter.setMinimumSize(new Dimension(40, filter.getHeight()));
		toolBar.add(filter);

		toolBar.addSeparator(new Dimension(40, 0));
		sutFilter = new JToggleButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_FILTER_TESTS_TREE));
		sutFilter.setEnabled(true);
		sutFilter.setToolTipText("Filter SUT tree");
		sutFilter.addActionListener(this);
		if ("true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.FILTER_SUT_IN_ASSETS_TREE))) {
			sutFilter.setSelected(true);
		} else {
			sutFilter.setSelected(false);
		}
		toolBar.add(sutFilter);

		sort = new JToggleButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_SORT_TESTS_TREE));
		sort.setEnabled(true);
		sort.setToolTipText("Sort Assets Tree Alphabetically");
		sort.addActionListener(this);
		if ("true".equals(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SORT_ASSETS_TREE))) {
			sort.setSelected(true);
		} else {
			sort.setSelected(false);
		}
		toolBar.add(sort);

		toolBar.setPreferredSize(new Dimension(160, 30));
		toolBar.setFloatable(false);

		return toolBar;
	}

	public JTree getTree() {
		return testBrowser.getTree();
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// The code was moved from keyTyped() method into keyReleased() method
		// since
		// keyTyped() method was not updated with the last pressed character.
		
		//Support for OR operator.
		String filterText=  ((JTextField) filter.getEditor().getEditorComponent()).getText().replaceAll(" OR ", "|");
		
		//Support for AND operator.
		String filterTextArr[] = filterText.split(" AND ");
		if (filterTextArr != null && filterTextArr.length !=0 && filterTextArr[0].isEmpty()) {
			filterTextArr = null;
		}
		String[] originalFilterArr = TestFilterManager.getInstance().getFilter();
		if (originalFilterArr == null && filterTextArr == null) {
			return;
		}
		if (originalFilterArr != null && Arrays.equals(filterTextArr, originalFilterArr)) {
			return;
		}
		TestFilterManager.getInstance().setFilter(filterTextArr);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
