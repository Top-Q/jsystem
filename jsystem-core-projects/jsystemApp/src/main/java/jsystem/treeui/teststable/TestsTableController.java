/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.treeui.teststable;

import java.awt.BorderLayout;
import java.awt.Color;
// import java.awt.Component;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.util.Log;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.extensions.scenarionamehook.ScenarioNameHookManager;
import jsystem.extensions.scenarionamehook.ScenarioNameHookManager.HookData;
import jsystem.extensions.sourcecontrol.SourceControlHandler;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.report.TestStatusListener;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.MultipleScenarioOps;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerFixture;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioChangeType;
import jsystem.framework.scenario.ScenarioEditor;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenarioListener;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.SenarioEditorManager;
import jsystem.framework.scenario.TestsContainer;
import jsystem.framework.scenario.flow_control.AntDataDriven;
import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.scenario.flow_control.AntIfCondition;
import jsystem.framework.scenario.flow_control.AntIfElse;
import jsystem.framework.scenario.flow_control.AntIfElseIf;
import jsystem.framework.scenario.flow_control.AntSwitch;
import jsystem.framework.scenario.flow_control.AntSwitchCase;
import jsystem.framework.scenario.flow_control.AntSwitchDefault;
import jsystem.framework.sut.ChangeSutTest;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.tests.PublishTest;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.TestRunner;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.actionItems.AddScenarioAction;
import jsystem.treeui.actionItems.AddSutAction;
import jsystem.treeui.actionItems.ClearScenarioAction;
import jsystem.treeui.actionItems.CommitScenarioAction;
import jsystem.treeui.actionItems.CommitSutAction;
import jsystem.treeui.actionItems.ConnectToSourceControlAction;
import jsystem.treeui.actionItems.CopyAction;
import jsystem.treeui.actionItems.CopyScenarioAction;
import jsystem.treeui.actionItems.CutAction;
import jsystem.treeui.actionItems.EditScenarioAction;
import jsystem.treeui.actionItems.MoveDownAction;
import jsystem.treeui.actionItems.MoveToBottomAction;
import jsystem.treeui.actionItems.MoveToTopAction;
import jsystem.treeui.actionItems.MoveUpAction;
import jsystem.treeui.actionItems.NewDataDrivenAction;
import jsystem.treeui.actionItems.NewElseIfAction;
import jsystem.treeui.actionItems.NewForLoopAction;
import jsystem.treeui.actionItems.NewIfConditionAction;
import jsystem.treeui.actionItems.NewSwitchAction;
import jsystem.treeui.actionItems.NewSwitchCaseAction;
import jsystem.treeui.actionItems.NextScenarioAction;
import jsystem.treeui.actionItems.OpenScenarioAction;
import jsystem.treeui.actionItems.PasteAction;
import jsystem.treeui.actionItems.PasteAfterAction;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.actionItems.PreviosScenarioAction;
import jsystem.treeui.actionItems.PublishEventAction;
import jsystem.treeui.actionItems.RemoveItemAction;
import jsystem.treeui.actionItems.RevertScenarioAction;
import jsystem.treeui.actionItems.RevertSutAction;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.actionItems.ScenarioRedoAction;
import jsystem.treeui.actionItems.ScenarioSCStatusAction;
import jsystem.treeui.actionItems.ScenarioUndoAction;
import jsystem.treeui.actionItems.SutSCStatusAction;
import jsystem.treeui.actionItems.UpdateScenarioAction;
import jsystem.treeui.actionItems.UpdateSutAction;
import jsystem.treeui.actionItems.ViewTestCodeAction;
import jsystem.treeui.dialog.DialogWithCheckBox;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.tree.TestsTreeController;
import jsystem.treeui.tree.undo.UndoManager;
import jsystem.treeui.utilities.UnmodifiableFileHandler;
import jsystem.undoredo.UserActionManager;
import jsystem.utils.BrowserLauncher;
import jsystem.utils.IntegerWrapper;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class TestsTableController extends Observable implements TestStatusListener, ActionListener, ExtendTestListener,
		TreeSelectionListener, MouseListener, TreeExpansionListener, ScenarioListener {

	private static Logger log = Logger.getLogger(TestsTableController.class.getName());

	OurTree tree;

	ScenarioModel model;

	JPanel pane;
	
	JScrollPane scroll;
	
	int scrollBarValue;

	JToolBar toolBar;

	JToolBar flowControlToolBar;

	JToolBar sourceControlToolBar;

	JTabbedPane tabbes;

	JButton changeSut;

	StatusBar statusBar;

	SourceControlHandler sourceControlHandler;

	TestsTreeController testsTreeControler = null;

	// This following array holds all the expended scenarios paths and their
	// full UUID, for restoring expand state after reloading tree.
	HashMap<String, TreePath> expandedPaths = new HashMap<String, TreePath>();

	// used to determine the size of the check box in the Tests Tree
	int checkX = 39;
	final static int xRef = 20;
	// see OurTree documentation below
	private volatile boolean isEventCheckEvent = false;

	private boolean isApprooved = false;

	private int lastSubScenResult = -1;

	private static final int TEST_POP_UP = 0;

	private static final int ROOT_POP_UP = 1;

	private static final int SCEN_POP_UP = 2;

	private static final int FIXTURE_POP_UP = 3;

	// Added in order to resolve bug #266
	private static final int FLOW_POP_UP = 4;

	private boolean rightMenuAlreadyOpened = false; // @nizan for right

	// selection purpose

	private JPopupMenu popupMenu;

	private JMenuItem popupCommentItem;

	private JMenuItem popupMapItem;

	private JMenuItem popupUnmapItem;

	private JMenuItem popupMoveUpItem2;

	private JMenuItem popupMoveDownItem2;

	private JMenuItem popupDeleteItem2;

	private JMenuItem popupSetName;

	private JMenuItem popupCollapseTree;

	private JMenuItem popupExpandTree;

	private JMenuItem markScenarioAsTest;

	private JMenuItem unMarkScenarioAsTest;

	// Limor Bortman
	private JMenuItem resetToDefault;

	// APPLIED - Add the edit only locally option to the menu
	private JMenuItem editOnlyLocallyItem;

	private JMenuItem markAsKnownIssue;

	private JMenuItem hideScenarioInHTML;

	private JMenuItem unmarkAsKnownIssue;

	private JMenuItem popupNavigateToSubScenario;

	private JMenuItem markAsNegativeTest;

	private JMenuItem unMarkAsNegativeTest;

	private JMenuItem popupExpandTreeRoot;

	private JMenuItem popupCollapseTreeRoot;
	
	private List<ContextMenuPlugin> contextMenuPlugins;

	private ScenarioTreeNode currentNode;

	private JTextArea commentTxt;

	private JPanel commentPanel;

	private JButton commentButton;

	private JButton eraseButton;

	private JButton cancelButton;

	private JFrame commentFrame;

	private TreePath lastClickedPath;

	private EventListener testTreeViewSelectionListner;

	private boolean isRunning = false;

	// @ Nizan, in order to separate the check-box - it's needed to save the
	// paths
	int[] lastPaths;

	private TreeMap<Integer, JTest> testByRow;
	private int[] paths = null;
	JTest[] selectedTests;
	private LinkedHashMap<Integer, JTest> clipboardTests;

	/**
	 * The method copies the container selected rows in the Scenario editor,
	 * into the clipboardTests HashMap Rows which are not allowed to be copied
	 * or selected rows which are contained in other selected container will not
	 * be copied to clipboardTests
	 * 
	 * @throws Exception
	 */
	public void saveClipboardTests() {
		// Get selected rows numbers
		paths = tree.getSelectionModel().getSelectionRows();
		ArrayList<Integer> testsKeys = new ArrayList<Integer>();
		HashMap<Integer, JTest> tempClipboard = new HashMap<Integer, JTest>();
		int testIndex = 0;
		if (paths != null && paths.length > 0) { // no selected rows - not
													// supposed to get here
													// since the copy and cut
													// are disabled
			clipboardTests = new LinkedHashMap<Integer, JTest>();

			// collect all selected tests by user selection order.
			TreePath[] pathsTests = tree.getSelectionModel().getSelectionPaths();
			JTest[] selectedTestes = new JTest[paths.length];
			ScenarioTreeNode node;
			for (int i = 0; i < pathsTests.length; i++) {
				node = (ScenarioTreeNode) pathsTests[i].getLastPathComponent();
				selectedTestes[i] = node.getTest();
			}

			TestsContainer container = getContainer();
			container.initRun();

			Scenario currentScenario = ScenariosManager.getInstance().getCurrentScenario();

			if (container.contains(currentScenario)) {
				return;
			}
			// Go over all selected scenario tree objects
			for (int i = 0; i < selectedTestes.length; i++) {
				boolean parentSelected = false;
				JTest jtest = selectedTestes[i];
				if ((jtest instanceof AntIfElse) || (jtest instanceof AntSwitchDefault)) { // a
																							// copy
																							// of
																							// a
																							// IfElse
																							// or
																							// a
																							// SwitchDefault
																							// stand
																							// alone
																							// is
																							// illegal
					testIndex++;
					continue;
				}

				JTest parent = jtest.getParent();
				// In case the JTest parent is selected we should not add it to
				// clipboard
				do {
					if (container.contains(parent)) { // One of the parents is
														// selected
						parentSelected = true;
						testIndex++;
						break;
					}
					// we got to the root scenario
					if (parent.getParent() == null) {
						break;
					}
					// go up to the upper container
					parent = parent.getParent(); // Maybe the parents parent is
													// selected
				} while (!(parent.getTestName().equals(currentScenario.getName())));
				if (!parentSelected) {
					// Add a test to the clipbard
					tempClipboard.put(paths[testIndex], jtest);
					testsKeys.add(paths[testIndex]);
					testIndex++;
				}
			}
		}
		Object[] keys = testsKeys.toArray();
		Arrays.sort(keys);
		for (int i = 0; i < keys.length; i++) {
			clipboardTests.put(i, tempClipboard.get(keys[i]));
		}

		updateEnabledAndDisabledActions(null);
	}

	/**
	 * Add clipboardTests to the Scenario (Paste event)
	 * 
	 * @param after
	 *            - Should the copied element be copied in the paste location
	 *            (in the container) or after the paste location
	 */
	public void addClipboardTests(final boolean after) {
		if (clipboardTests != null) {
			int testsCounter = ScenariosManager.getInstance().getCurrentScenario().getTests().size();
			testsCounter += clipboardTests.size();
			int max = Integer.parseInt(JSystemProperties.getInstance().getPreference(
					FrameworkOptions.MAX_BUILDING_BLOCKS_NUMBER));
			if (testsCounter > max) {
				ListenerstManager.getInstance().showConfirmDialog("Error",
						"Exceeded max building blocks number, couldn't add the selected tests to the scenario",
						JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE);
				return;
			}
			WaitDialog.launchWaitDialog("Pasting clipboard tests", null);
			new Thread() {
				public void run() {
					try {
						addClipboardTests(clipboardTests, after);
					} finally {
						WaitDialog.endWaitDialog();
					}
				}
			}.start();
		}
	}

	/**
	 * Add Clipboard tests to the scenario
	 * 
	 * @param tests
	 *            - clipboard tests to be Paste (add)
	 * @param after
	 *            - Should the copied element be copied in the paste location
	 *            (in the container) or after the paste location
	 * @return
	 */
	public boolean addClipboardTests(LinkedHashMap<Integer, JTest> tests, boolean after) {
		TestsContainer container = getContainer();
		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();
		JTestContainer destJTestContainer = container.getContainerRoot();

		ArrayList<Integer> containersPath = new ArrayList<Integer>();
		int index = -2;
		containersPath.add(index);
		Scenario scenarioToAddTo = rootScenario;
		destJTestContainer = rootScenario;

		if (!container.isEmpty()) {
			if (container.getTests().length > 1) { // When performing PASTE only
													// 1 row should be selected
				ErrorPanel.showErrorDialog("Failed Pasting", "Please select 1 row for Pasting the clipboard tests.",
						ErrorLevel.Warning);
				return false;
			}
			destJTestContainer = container.getContainerRoot();

			JTest last = container.getLast();
			index = destJTestContainer.getRootIndex(last);
			containersPath.set(0, index); // replace the -2
			try {
				scenarioToAddTo = ScenarioHelpers.findPath(containersPath, destJTestContainer);
			} catch (Exception e) {
				ErrorPanel.showErrorDialog(
						"Failed adding a test to scenario.\n" + "The common reason for this is a problem\n "
								+ "in creating an instance of the test class.\n", e, ErrorLevel.Warning);
			}
		}
		// ----------------------------------------------------
		// Add each test into the right place
		int indexCounter = 0;

		ArrayList<JTest> actualTests = new ArrayList<JTest>();

		Iterator<Integer> iter = tests.keySet().iterator();
		JTest[] reversedClipboardTests = new JTest[tests.size()];

		// Change the tests order to fit the add order in order to save the
		// original order in copied source tests
		for (int i = tests.size() - 1; i >= 0; i--) {
			if (iter.hasNext()) {
				reversedClipboardTests[i] = (JTest) (tests.get(iter.next()));
			}
		}

		// Go over all clipboard tests
		for (int i = 0; i < tests.size(); i++) {
			JTest test = (JTest) reversedClipboardTests[i];

			// In case of trying to Paste a scenario to itself, we should paste
			// it after the scenario and not IN the scenario
			int pasteUpLevels = isScenariosIncluded(test, destJTestContainer);
			if (((test instanceof Scenario) && (destJTestContainer instanceof Scenario) && (((Scenario) test).getName()
					.equals(destJTestContainer.getName())))
					|| (pasteUpLevels > 0)
					|| (after && !(container.getTests()[0] instanceof RunnerTest) && (!((container.getTests()[0] instanceof Scenario) && (((Scenario) container
							.getTests()[0])).isScenarioAsTest())))) {
				// In case we navigated up to a higher scenario level, there is
				// no need to do it again
				if (after)
					after = false;

				if (pasteUpLevels == 0)
					pasteUpLevels++;
				JTestContainer oldJestJTestContainer = destJTestContainer;
				containersPath = new ArrayList<Integer>();
				containersPath.add(0, -2);
				// re set the destination location for adding tests
				for (int j = 0; j < pasteUpLevels; j++) {
					oldJestJTestContainer = destJTestContainer;
					destJTestContainer = destJTestContainer.getParent();
					if (destJTestContainer == null) {
						ErrorPanel.showErrorDialog("Failed Pasting", "Illegal operation - Wrong Paste location!",
								ErrorLevel.Warning);
						return false;
					}
					index = destJTestContainer.getRootIndex(oldJestJTestContainer);
					containersPath.set(0, index);
				}
				try {
					scenarioToAddTo = ScenarioHelpers.findPath(containersPath, destJTestContainer);
				} catch (Exception e) {
					ErrorPanel.showErrorDialog("Failed adding a test to scenario.\n"
							+ "The common reason for this is a problem\n "
							+ "in creating an instance of the test class.\n", e, ErrorLevel.Warning);
				}
			}

			/*
			 * Verify that the Paste suits the Scenario rolls
			 * 
			 * 1) Test can't be inside switch 2) Switch case must be inside
			 * Switch (if pasted to another case, will be added after) 3)
			 * IfElseIf must be added to If
			 */
			if (((!(test instanceof AntSwitchCase)) && (destJTestContainer instanceof AntSwitch))
					|| ((test instanceof AntSwitchCase) && (!(destJTestContainer instanceof AntSwitch) && (!(destJTestContainer instanceof AntSwitchCase))))
					|| ((test instanceof AntIfElseIf) && (!(destJTestContainer instanceof AntIfCondition)))) {
				ErrorPanel.showErrorDialog("Failed Pasting",
						"Illegal operation - Copied element cannot be pasted into selected element!",
						ErrorLevel.Warning);
				return false;
			}

			try {
				actualTests.addAll(MultipleScenarioOps.addTestWithParamsToScenario(rootScenario, scenarioToAddTo,
						containersPath, test, indexCounter));
				indexCounter++;
			} catch (Exception e) {
				ErrorPanel.showErrorDialog(
						"Failed adding a test to scenario.\n" + "The common reason for this is a problem\n "
								+ "in creating an instance of the test class.\n", e, ErrorLevel.Warning);
			}
		}
		// ----------------------------------------------------
		// Update the selection signs
		JTest[] selectedTests = new JTest[container.getNumOfTests()];
		int[] paths = tree.getSelectionModel().getSelectionRows();

		for (int i = 0; i < selectedTests.length; i++) {
			selectedTests[i] = ((ScenarioTreeNode) tree.getPathForRow(paths[i]).getLastPathComponent()).getTest();
		}
		// Expand new scenarios that added
		ArrayList<TreePath> treePaths = getAllPaths();
		ArrayList<String> allFullUuid = new ArrayList<String>();
		for (JTest test : actualTests) {
			if (test instanceof JTestContainer) {
				allFullUuid.add(test.getFullUUID());
			}
		}
		String containerFullUuid = destJTestContainer.getFullUUID();
		if (destJTestContainer instanceof AntFlowControl) {
			containerFullUuid += StringUtils.isEmpty(containerFullUuid) ? destJTestContainer.getUUID() : "."
					+ destJTestContainer.getUUID();
		}
		for (TreePath path : treePaths) {
			String fullUuid = getTreePathUUID(path);
			if (allFullUuid.contains(fullUuid)) {
				expandAll(path);
			} else if (fullUuid.equals(containerFullUuid)) { // expand the
																// Container we
																// are adding to
				tree.expandPath(path);
			}
		}

		updateSelections(selectedTests, paths, false);
		// ----------------------------------------------------

		refreshTree();
		return true;
	}

	/**
	 * Checks if a given test is contained in a given container test
	 * 
	 * @param includedContainer
	 * @param includingContainer
	 * @return The number of levels to trace up in order to not have a recursive
	 *         contained test
	 */
	private int isScenariosIncluded(JTest includedContainer, JTest includingContainer) {
		if ((!(includedContainer instanceof JTestContainer)) || (!(includingContainer instanceof JTestContainer))) {
			return 0;
		}

		String currentScenarioName = ScenariosManager.getInstance().getCurrentScenario().getName();

		JTestContainer includingPointer = (JTestContainer) includingContainer;
		int pasteUpLevels = 1;

		/**
		 * Check all tests ArrayList
		 */
		for (int i = 0; i < ((JTestContainer) includedContainer).getTests().size(); i++) {
			if (((JTestContainer) includedContainer).getTests().get(i) instanceof Scenario) { // the
																								// current
																								// node
																								// is
																								// a
																								// Scenario
				do {
					if (includingPointer instanceof Scenario) {
						if (((Scenario) ((JTestContainer) includedContainer).getTests().get(i)).getName().equals(
								((Scenario) includingPointer).getName())) {
							return pasteUpLevels;
						}
					}
					if (includingPointer.getParent() == null)
						break;
					includingPointer = includingPointer.getParent(); // Maybe
																		// the
																		// parents
																		// parent
																		// a
																		// Scenario
					pasteUpLevels++;
				} while (!(includingPointer.getTestName().equals(currentScenarioName)));
			}
		}

		includingPointer = (JTestContainer) includingContainer;
		pasteUpLevels = 1;

		/**
		 * Check root tests ArrayList
		 */
		for (int i = 0; i < ((JTestContainer) includedContainer).getRootTests().size(); i++) {
			if (((JTestContainer) includedContainer).getRootTests().get(i) instanceof Scenario) {
				do {
					if (includingPointer instanceof Scenario) {
						if (((Scenario) ((JTestContainer) includedContainer).getRootTests().get(i)).getName().equals(
								((Scenario) includingPointer).getName())) {
							return pasteUpLevels;
						}
					}
					if (includingPointer.getParent() == null)
						break;
					includingPointer = includingPointer.getParent(); // Maybe
																		// the
																		// parents
																		// parent
																		// a
																		// Scenario
					pasteUpLevels++;
				} while (!(includingPointer.getTestName().equals(currentScenarioName)));
			}
		}

		includingPointer = (JTestContainer) includingContainer;
		pasteUpLevels = 1;

		/**
		 * Check if one of the containers from the paste point till the root
		 * scenario are in the same scenario as the copied object
		 */
		if (includedContainer instanceof Scenario) {
			do {
				if (includingPointer instanceof Scenario
						&& ((Scenario) includedContainer).getName().equals(((Scenario) includingPointer).getName())) {
					return pasteUpLevels;
				}
				if (includingPointer.getParent() == null)
					break;
				includingPointer = includingPointer.getParent(); // Maybe the
																	// parents
																	// parent a
																	// Scenario
				pasteUpLevels++;
			} while (!(includingPointer.getTestName().equals(currentScenarioName)));
		}

		return 0;
	}

	private static boolean isTheMouseOnTheCheckBox(ScenarioTreeNode myNode, int x) {
		// TODO fixed ticket #186
		if (myNode == null)
			return false;
		int mycheckX = myNode.getNodeLevel() * xRef;
		return ((mycheckX <= x) && (mycheckX + 11 >= x));
	}

	/**
	 * Actions that can be performed on the scenarios tree
	 */
	public enum ActionType {
		DOWN, UP, DELETE, NEW_FOR_LOOP, NEW_SWITCH, NEW_SWITH_CASE, NEW_IF_CONDITION, NEW_ELSE_IF, NEW_DATA_DRIVEN, TO_BOTTOM, TO_TOP;
	}

	public void setTestsTreeControler(TestsTreeController controler) {
		this.testsTreeControler = controler;
	}

	public TestsTableController() {
		this(null);
	}

	public TestsTableController(EventListener listner) {
		super();
		ListenerstManager.getInstance().addListener(this);
		testTreeViewSelectionListner = listner;
		createComment();
		model = new ScenarioModel();
		tree = new OurTree();
		tree.setModel(model);
		tree.setCellRenderer(new ScenarioRenderer());
		tree.setShowsRootHandles(true);
		tree.setExpandsSelectedPaths(true);
		tree.addMouseListener(this);
		tree.addKeyListener((KeyListener) listner);
		tree.addKeyListener(new ScenarioTreeKeyHandler(this));

		setSelectionModel();

		ToolTipManager.sharedInstance().registerComponent(tree);

		tree.addTreeExpansionListener(this);

		pane = new JPanel(new BorderLayout());

		scroll = SwingUtils.getJScrollPaneWithWaterMark(
				ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_SCENARIO_TREE_BG), tree);

		pane.add(scroll, BorderLayout.CENTER);
		toolBar = createToolBar();
		flowControlToolBar = createFlowControlToolBar();
		sourceControlToolBar = createSourceControlToolBar();
		sourceControlHandler = new SourceControlHandler();

		statusBar = new StatusBar("status bar", JToolBar.HORIZONTAL, ImageCenter.getInstance().getImage(
				ImageCenter.ICON_SCEANRIO_TOOLBAR_BG));
		statusBar.setFloatable(false);

		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel scenarioOperationsPanel = new JPanel(new BorderLayout());
		if ("true".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT))) 
		{
			scenarioOperationsPanel.add(toolBar, BorderLayout.CENTER);
			scenarioOperationsPanel.add(flowControlToolBar, BorderLayout.SOUTH);
			scenarioOperationsPanel.add(sourceControlToolBar, BorderLayout.NORTH);
		}
		mainPanel.add(scenarioOperationsPanel, BorderLayout.CENTER);
		mainPanel.add(statusBar, BorderLayout.SOUTH);

		pane.add(mainPanel, BorderLayout.NORTH);

		/**
		 * sets this class as Model Listener to the model.
		 */
		statusBar.setMessage(ScenariosManager.getInstance().getCurrentScenario().getName());

		TreePath rootPath = new TreePath(((TreeNode) tree.getModel().getRoot()));
		expandedPaths.put(getTreePathUUID(rootPath), rootPath);
		expandAll();

		setEdit(false, false, false, true, true, false);

		initContextMenuPlugins();
		
		refreshTree();
	}

	private void initContextMenuPlugins() {
		contextMenuPlugins = new ArrayList<ContextMenuPlugin>();
		String pluginNamesString = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.CONTEXT_MENU_PLUGIN_CLASSES);
		if (StringUtils.isEmpty(pluginNamesString)) {
			return;
		}
		StringTokenizer st = new StringTokenizer(pluginNamesString, ";");
		while (st.hasMoreTokens()){
			String pluginName = st.nextToken();
			try {
				Class<?> reporterClass = LoadersManager.getInstance().getLoader().loadClass(pluginName);
				ContextMenuPlugin plugin = (ContextMenuPlugin)reporterClass.newInstance();
				plugin.init(this);
				ListenerstManager.getInstance().addListener(plugin);
				contextMenuPlugins.add(plugin);
			} catch (Exception e) {
				log.log(Level.WARNING, "Failed to init context menu plugin " + pluginName +" due to " + e.getMessage());
			}

		}
	}

	private void setSelectionModel() {
		DefaultTreeSelectionModel tsm = new DefaultTreeSelectionModel();
		// For Multi-Selection at Scenario Tree
		tsm.setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tsm.addTreeSelectionListener(this);
		tsm.addTreeSelectionListener((TreeSelectionListener) testTreeViewSelectionListner);
		tree.setSelectionModel(tsm);
	}

	/**
	 * a TestContainer is an object that hold all the test currently selected
	 * 
	 * @return a TestsContainer object
	 */
	@SuppressWarnings("unused")
	public TestsContainer getContainer() {
		TreePath[] paths = tree.getSelectionModel().getSelectionPaths();
		TestsContainer container = new TestsContainer();

		final Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		ScenarioTreeNode node;
		JTest test;
		if (paths != null) {
			for (int i = 0; i < paths.length; i++) {
				node = (ScenarioTreeNode) paths[i].getLastPathComponent();
				test = node.getTest();
				container.addTest(test);
			}
		}
		container.initRun();
		return container;
	}

	public void fireCurrentNodeChange() {
		// ITAI: I spat blood until I made this event work. The problem is that
		// we added constructor to the model that created the tree root. This is
		// not really the instance of the tree root, so when the model wants
		// create the tree path for event, the tree path is not correct and the
		// UI ignores it.Currenly I fixed it by overriding the getPathToRoot
		// method of the model.
		model.nodeChanged(currentNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		final Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();

		/**
		 * Calls the right method that handles the specific action performed on
		 * the scenario tree. Please use this method to handle actions from the
		 * same type
		 */
		if (source.equals(MoveUpAction.getInstance().getActionCommand())
				|| source.equals(MoveDownAction.getInstance().getActionCommand())
				|| source.equals(RemoveItemAction.getInstance().getActionCommand())
				|| source.equals(MoveToBottomAction.getInstance().getActionCommand())
				|| source.equals(MoveToTopAction.getInstance().getActionCommand())) {
			if (source.equals(MoveUpAction.getInstance().getActionCommand())) {
				handleUpDownAndDelete(ActionType.UP);
			} else {
				if (source.equals(MoveDownAction.getInstance().getActionCommand())) {
					handleUpDownAndDelete(ActionType.DOWN);
				} else {
					if (source.equals(RemoveItemAction.getInstance().getActionCommand())) {
						handleUpDownAndDelete(ActionType.DELETE);
					} else {
						if (source.equals(MoveToBottomAction.getInstance().getActionCommand())) {
							handleUpDownAndDelete(ActionType.TO_BOTTOM);
						} else {
							if (source.equals(MoveToTopAction.getInstance().getActionCommand())) {
								handleUpDownAndDelete(ActionType.TO_TOP);
							}
						}
					}
				}
			}
		} else {
			updateSelectedInScenarioTree();
		}
		/**
		 * case of creating new scenario
		 */

		if (source.equals(EditScenarioAction.getInstance().getActionCommand())) {
			editScenario();
		} else if (source.equals(OpenScenarioAction.getInstance().getActionCommand())) {
			switchScenario();
		} else if (source.equals(ViewTestCodeAction.getInstance().getActionCommand())) {
			getTestCode();
		} else if (source.equals(changeSut)) {
			changeSutFile();
		} else if (rightMenuItem(source)) {
			handleRightMenu(scenario, source);
		} else if (source.equals(commentButton) || source.equals(eraseButton) || source.equals(cancelButton)) {
			handleComment(scenario, source);
			// } else
			// if(source.equals(ScenarioRedoAction.getInstance().getActionCommand())){
			// scenarioRedo(scenario);
			// } else
			// if(source.equals(ScenarioUndoAction.getInstance().getActionCommand())){
			// scenarioUndo(scenario);
		}
		ScenariosManager.getInstance().getCurrentScenario().updateAllTests();
	}

	public void addFlowControlElement(ActionType type) throws Exception {
		// Create the new condition JTest
		Vector<JTest> oneCondition = new Vector<JTest>();
		JTestContainer condition = null;
		if (type.equals(ActionType.NEW_FOR_LOOP)) {
			condition = new AntForLoop();
		} else if (type.equals(ActionType.NEW_SWITCH)) {
			condition = new AntSwitch();
			// Add a default "test"
			// TODO: maybe we should define the given ID properly
			((AntSwitch) condition).addTest(new AntSwitchDefault(condition, null));
		} else if (type.equals(ActionType.NEW_SWITH_CASE)) {
			condition = new AntSwitchCase();
		} else if (type.equals(ActionType.NEW_IF_CONDITION)) {
			condition = new AntIfCondition();
			((AntIfCondition) condition).addTest(new AntIfElse(condition, null));
		} else if (type.equals(ActionType.NEW_ELSE_IF)) {
			condition = new AntIfElseIf();
		} else if (type.equals(ActionType.NEW_DATA_DRIVEN)) {
			condition = new AntDataDriven();
		}
		else {
			// error
		}
		oneCondition.add(condition);

		addTests(oneCondition);
		refreshTree();
	}

	public void addPublishTest() {
		// Check DB configuration
		// DBPropertiesAction dbPropertiesActionToPerform = new
		// DBPropertiesAction();
		// try {
		// DBProperties.getInstance();
		// if (!dbPropertiesActionToPerform.checkDBConnection()) {
		// throw new Exception("Failed connecting to the Database");
		// }
		// } catch (Exception ee) {
		// String errMessage =
		// "Failed connecting to the Database\n\nDo you wish to configure Database Properties?";
		// int ans = JOptionPane.showConfirmDialog(TestRunnerFrame.guiMainFrame,
		// errMessage,
		// "Database connection problem", JOptionPane.YES_NO_OPTION);
		// dbPropertiesActionToPerform.setOK(ans == JOptionPane.NO_OPTION);
		// while (!dbPropertiesActionToPerform.isOK()) {
		// DefineDbPropertiesDialog db = new
		// DefineDbPropertiesDialog("Database Properties", true, null);
		// dbPropertiesActionToPerform.setDb(db);
		// DefineDbPropertiesDialog.okButton.addActionListener(dbPropertiesActionToPerform);
		// DefineDbPropertiesDialog.cancelButton.addActionListener(dbPropertiesActionToPerform);
		// db.requestFocus();
		// db.setVisible(true);
		// db.dispose();
		// }
		// // if cancel pressed return
		// }

		// Create the new publish JTest
		Vector<JTest> oneTest = new Vector<JTest>();

		JTest publishTest = new RunnerTest(PublishTest.class.getName(), "publish");

		oneTest.add(publishTest);

		addTests(oneTest);
		refreshTree();
	}

	/**
	 * 
	 * @return is scenario is currently running.
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * 
	 */
	public void toggleFlowControlToolBarVisability() {
		flowControlToolBar.setVisible(!flowControlToolBar.isVisible());
	}

	public void toggleSourceControlToolBarVisability() {
		sourceControlToolBar.setVisible(!sourceControlToolBar.isVisible());
	}

	/**
	 * 
	 */
	public boolean isFlowControlToolbarVisible() {
		return flowControlToolBar.isVisible();
	}

	private void changeSutFile() {

		// Create the new publish JTest
		Vector<JTest> oneTest = new Vector<JTest>();

		RunnerTest changeSutTest = new RunnerTest(ChangeSutTest.class.getName(), "changeSut");

		oneTest.add(changeSutTest);

		addTests(oneTest);
		refreshTree();
	}

	/**
	 * Uses for getting all the tests selected in the scenario tree updated in a
	 * file.
	 * 
	 */
	private void updateSelectedInScenarioTree() {

		/**
		 * for getting the tests sorted by row
		 */
		testByRow = new TreeMap<Integer, JTest>();

		/**
		 * get all selected rows.
		 */
		paths = tree.getSelectionModel().getSelectionRows();

		if (paths != null && paths.length > 0) {
			selectedTests = new JTest[paths.length];

			/**
			 * collect all selected tests.
			 */
			for (int i = 0; i < selectedTests.length; i++) {
				selectedTests[i] = ((ScenarioTreeNode) tree.getPathForRow(paths[i]).getLastPathComponent()).getTest();

				/**
				 * put key:current path , value: current test. TreeMap can
				 * return the value sorted by the keys.
				 */
				testByRow.put(paths[i], selectedTests[i]);
			}
		}

	}

	private void restoreExpandedState() {
		// removing UI to speed-up expand operation
		TreeUI ui = tree.getUI();
		tree.setUI(null);

		HashMap<String, TreePath> tmpPath = new HashMap<String, TreePath>(expandedPaths);
		Collection<TreePath> e = tmpPath.values();
		for (TreePath path : e) {
			TreeNode node = (TreeNode) path.getLastPathComponent();
			if (node.isLeaf()) {
				continue;
			}
			if (tmpPath.containsKey(getTreePathUUID(path))) {
				// if (allParentAreExpanded(path, e)){
				tree.expandPath(path);
			}
		}
		// restoring the UI
		tree.setUI(ui);
	}

	private boolean isTreePathAFlowControlElement(TreePath path) {
		ScenarioTreeNode node = (ScenarioTreeNode) path.getLastPathComponent();
		JTest test = node.getTest();
		return (test instanceof AntFlowControl);

	}

	/**
	 * returns all path elements in the scenario tree
	 * 
	 * @return
	 */
	private ArrayList<TreePath> getAllPaths() {
		ArrayList<TreePath> list = new ArrayList<TreePath>();

		TreeNode root = (TreeNode) tree.getModel().getRoot();

		getAllPaths(new TreePath(root), list);
		return list;
	}

	private void getAllPaths(TreePath parent, ArrayList<TreePath> toAddTo) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);

				getAllPaths(path, toAddTo);
			}
		}

		toAddTo.add(parent);
	}

	/**
	 * replace old expanded tree paths with new tree paths
	 */
	private void updateExpandedPaths() {
		ArrayList<TreePath> paths = getAllPaths();
		for (TreePath path : paths) {
			String fullUuid = getTreePathUUID(path);
			if (expandedPaths.containsKey(fullUuid)) {
				expandedPaths.put(fullUuid, path);
			}
		}
	}

	/**
	 * restore selection from lastPaths member
	 */
	private void restoreSelections(final int[] _lastPath) {
		final TreePath[] paths = rowsToPaths(_lastPath != null ? _lastPath : lastPaths);
		tree.getSelectionModel().setSelectionPaths(paths);
	}

	/**
	 * save selected tests to lastPaths member
	 */
	private int[] saveSelection() {
		lastPaths = tree.getSelectionModel().getSelectionRows();
		return lastPaths;
	}
	
	/**
	 * restore last scroll bar position
	 */
	private void restoreScrollBarPosition() {
		scroll.getVerticalScrollBar().setValue(scrollBarValue);
	}
	
	/**
	 * save current scroll bar position
	 */
	private void saveScrollBarPosition() {
		scrollBarValue = scroll.getVerticalScrollBar().getValue();
	}

	/**
	 * Handles up, down and delete actions performed on the scenario tree.
	 * 
	 * @param type
	 */
	public void handleUpDownAndDelete(ActionType type) {
		updateSelectedInScenarioTree();
		if (paths == null || paths.length == 0 || getContainer().hasRoot()) {
			return;
		}
		switch (type) {
		case DELETE:
			try {
				/**
				 * Finds the minimum index number of selected test in the
				 * scenario test. This index will be selected after the deletion
				 * operation.
				 */
				int min = paths[0];
				for (int i = 1; i < paths.length; i++) {
					if (paths[i] < min) {
						min = paths[i];
					}
				}
				removeTests(selectedTests);
				tree.setSelectionRow(min);

				updateEnabledAndDisabledActions(null);
				clearExpandSelected(false, true);

			} catch (Exception ex) {
				log.log(Level.SEVERE, "Fail to remove test", ex);
			}
			break;
		case DOWN:
			try {
				moveDown();
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to move down test", e1);
			}
			break;
		case UP:
			try {
				moveUp();
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to move up test", e1);
			}
			break;
		case TO_BOTTOM:
			try {
				moveToBottom();
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to move test to bottom", e1);
			}
			break;
		case TO_TOP:
			try {
				moveToTop();
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to move test to top", e1);
			}
		}

		// ITAI: Settings the tests selection again.
		final JTest[] origianlTestsSelection = selectedTests;
		final int[] originalTestPathes = paths;
		ScenariosManager.getInstance().getCurrentScenario().updateAllTests();
		refreshTree();
		updateSelections(origianlTestsSelection, originalTestPathes, false);
		resetInfoTab();

	}

	public void scenarioRedo() {
		try {
			Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
			UndoManager.getInstance().redo(scenario);
			// loadScenario(scenarioName, false);
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Fail to redo scenario edit", e.getMessage(), ErrorLevel.Warning);
			log.log(Level.WARNING, "Fail to redo scenario edit", e);
		}
	}

	public void scenarioUndo() {
		try {
			Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
			UndoManager.getInstance().undo(scenario);
			// loadScenario(scenarioName, false);
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Fail to undo scenario edit", e.getMessage(), ErrorLevel.Warning);
			log.log(Level.WARNING, "Fail to undo scenario edit", e);
		}
	}

	public boolean switchScenario() {
		/**
		 * prevent renaming of files
		 */
		UIManager.put("FileChooser.readOnly", true);
		JFileChooser fc = new JFileChooser(ScenariosManager.getInstance().getCurrentScenario().getScenarioFile()
				.getParentFile());
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		fc.setFileFilter(new XmlFileFilter());
		fc.setDialogTitle(JsystemMapping.getInstance().getScenarioSelectWin());
		File testClassDir = new File(JSystemProperties.getCurrentTestsPath());

		if (fc.showOpenDialog(TestRunnerFrame.guiMainFrame) != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		String scenarioName = fc.getSelectedFile().getAbsolutePath()
				.substring(testClassDir.getAbsolutePath().length() + 1);
		if (scenarioName.endsWith(".xml")) {
			scenarioName = scenarioName.substring(0, scenarioName.length() - 4);
		}
		loadScenario(scenarioName, false);
		return true;
	}

	public void getTestCode() {

		JTest test = getContainer().getLast();

		if (test != null && !(test instanceof RunnerTest)) {
			return;
		}
		RunnerTest rt = (RunnerTest) test;
		HtmlCodeWriter codeWriter = HtmlCodeWriter.getInstance();
		String code = null;
		try {
			code = codeWriter.getCode(rt.getClassName());
			code = code.replaceAll(rt.getMethodName(), "<b>" + rt.getMethodName() + "</b>");
		} catch (FileNotFoundException e) {
			log.log(Level.WARNING, "Fail to load test code because sorce file is missing. " + e.getMessage());
		} catch (ClassNotFoundException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"Can't display test code because java2html.jar is missing.\nIf you wish to view code, please install java2html.jar. For instructions go to http://trac.jsystemtest.org/wiki/DetailedOSProjectsList",
							"View Test Code warning", JOptionPane.INFORMATION_MESSAGE);
			log.log(Level.WARNING, "Fail to load test code because java2html jar is missing. " + e.getMessage());
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to load test code. " + e.getMessage());
		}
		if (code == null) {
			return;
		}
		try {
			File f = File.createTempFile("code", ".html");
			FileWriter fw = new FileWriter(f);
			fw.write(code);
			fw.flush();
			fw.close();

			BrowserLauncher.openURL(f.getPath());
		} catch (IOException e2) {
			ErrorPanel.showErrorDialog("Fail to load test code", e2.getMessage(), ErrorLevel.Warning);
			log.log(Level.INFO, "Fail to load test code", e2);
		}
	}

	public void editScenario() {
		WaitDialog.launchWaitDialog("Wait for excel process end ... ", null, "(close excel sheet to continue)", false);
		(new Thread() {
			public void run() {
				final Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();

				try {
					ScenarioEditor dup = null;
					try {
						dup = SenarioEditorManager.getDuplicator();
					} catch (Exception e1) {
						ErrorPanel.showErrorDialog("Failed to open a Scenario Editor", e1, ErrorLevel.Error);
						return;
					}
					if (dup != null) {
						try {

							JTest test = getContainer().getNext();

							Scenario s = null;

							if (test == null) {
								s = ScenariosManager.getInstance().getCurrentScenario();
							} else {
								if (test instanceof Scenario) {
									s = (Scenario) test;
								}
							}
							/**
							 * Please note: since scenario parameters and values
							 * are not loaded at startup, it is important to
							 * trigger loading of scenario data before editing.
							 */
							s.loadParametersAndValues();
							dup.executeSenarioEditor(s);

							ScenariosManager.getInstance().setCurrentScenario(scenario);

						} catch (Exception e1) {
							ErrorPanel.showErrorDialog("Scenario editor fail", e1, ErrorLevel.Error);
							return;
						}
					}

					refresh();

				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		}).start();
	}

	public boolean copyScenario() {
		Scenario t = ScenariosManager.getInstance().getCurrentScenario();

		JTest nodeTest = getCurrentNode().getTest();
		if (nodeTest instanceof Scenario && tree.getSelectionModel().getSelectionRows() != null) {
			t = (Scenario) nodeTest;
		}

		if (!saveList(false, t)) {
			return false;
		}
		tree.getSelectionModel().clearSelection();
		testsTreeControler.refreshView();
		testsTreeControler.expandTree();
		clearExpandSelected(true, false);
		refresh();
		ListenerstManager.getInstance().scenarioChanged(t, ScenarioChangeType.SAVE);
		return true;
	}

	public boolean createNewScenario() {
		boolean isOK = saveList(true, null);
		tree.getSelectionModel().clearSelection();
		testsTreeControler.refreshView();
		testsTreeControler.expandTree();
		clearExpandSelected(true, true);
		return isOK;
	}
	

	// this function is only to decide if this element is included in the
	// rightMenuItem
	private boolean rightMenuItem(Object source) {
		return (source.equals(popupCommentItem) || source.equals(popupMapItem) || source.equals(popupUnmapItem)
				|| source.equals(MoveUpAction.getInstance().getActionCommand())
				|| source.equals(MoveDownAction.getInstance().getActionCommand())
				|| source.equals(RemoveItemAction.getInstance().getActionCommand()) || source.equals(popupMapItem)
				|| source.equals(MoveToTopAction.getInstance().getActionCommand())
				|| source.equals(MoveToBottomAction.getInstance().getActionCommand())
				|| source.equals(popupMoveDownItem2) || source.equals(popupDeleteItem2) || source.equals(popupSetName)
				|| source.equals(popupCollapseTree)
				|| source.equals(popupExpandTree)
				|| source.equals(popupCollapseTreeRoot)
				|| source.equals(popupExpandTreeRoot)
				|| source.equals(markScenarioAsTest)
				|| source.equals(unMarkScenarioAsTest)
				|| source.equals(markAsKnownIssue)
				// APPLIED - this is to recognize the item was selected from the
				// JPopupMenu
				|| source.equals(editOnlyLocallyItem) || source.equals(hideScenarioInHTML)
				|| source.equals(unmarkAsKnownIssue) || source.equals(popupNavigateToSubScenario)
				|| source.equals(markAsNegativeTest) || source.equals(unMarkAsNegativeTest)
		// Limor Bortman
		|| source.equals(resetToDefault));
	}

	/**
	 * Handles Right Menu options uses container as a setup for future
	 * multi-selection rightMenu
	 * 
	 * @param scenario
	 *            the current Scenario
	 * @param source
	 *            the item selected in the menu
	 */
	private void handleRightMenu(Scenario scenario, Object source) {
		rightMenuAlreadyOpened = false;

		/**
		 * collect all selected tests
		 */
		updateSelectedInScenarioTree();
		/**
		 * case of Map / unmap item
		 */
		if (source.equals(popupMapItem)) {
			handleMultipleNodesMap(true);
		} else if (source.equals(popupUnmapItem)) {
			handleMultipleNodesMap(false);
		}
		/**
		 * case of move up / down item
		 */
		else if (source.equals(MoveUpAction.getInstance().getActionCommand()) || source.equals(popupMoveUpItem2)
				|| source.equals(MoveDownAction.getInstance().getActionCommand()) || source.equals(popupMoveDownItem2)
				|| source.equals(MoveToBottomAction.getInstance().getActionCommand())
				|| source.equals(MoveToTopAction.getInstance().getActionCommand())) {

			if ((source.equals(MoveUpAction.getInstance().getActionCommand()) || source.equals(popupMoveUpItem2))) {
				handleUpDownAndDelete(ActionType.UP);
			} else {
				if (source.equals(MoveDownAction.getInstance().getActionCommand()) || source.equals(popupMoveDownItem2)) {
					handleUpDownAndDelete(ActionType.DOWN);
				} else {
					if (source.equals(MoveToBottomAction.getInstance().getActionCommand())) {
						handleUpDownAndDelete(ActionType.TO_BOTTOM);
					} else {
						if (source.equals(MoveToTopAction.getInstance().getActionCommand())) {
							handleUpDownAndDelete(ActionType.TO_TOP);
						}
					}
				}
			}
		}

		/**
		 * case of delete item
		 */
		else if (source.equals(RemoveItemAction.getInstance().getActionCommand()) || source.equals(popupDeleteItem2)) {
			handleUpDownAndDelete(ActionType.DELETE);
		} else if (source.equals(popupCommentItem)) {
			commentTxt.setText(currentNode.getTest().getComment());
			commentFrame.setVisible(true);
		} else if (source.equals(popupSetName)) {
			JTest jtest = currentNode.getTest();
			/*
			 * Set the meaningful name to the scenario update it and refresh the
			 * tree
			 */
			String value = JOptionPane.showInputDialog(TestRunnerFrame.guiMainFrame, "Set meaningful name",
					jtest.getMeaningfulName());

			if (value != null) {
				if (value.trim().equals("")) {
					value = null;
				}
				jtest.setMeaningfulName(value, true);
				ScenarioHelpers.setDirtyFlag();
			}
		} else if (source.equals(popupExpandTreeRoot) || source.equals(popupCollapseTreeRoot)) {
			boolean expand = source.equals(popupExpandTreeRoot);
			expandCollapseAllByRoot(expand);
		} else if (source.equals(popupCollapseTree) || source.equals(popupExpandTree)) {
			boolean expand = source.equals(popupExpandTree);
			ArrayList<TreePath> treePaths = getAllPaths();
			ArrayList<String> allFullUuid = new ArrayList<String>();
			for (JTest test : selectedTests) { // add selected SCENARIOS to
				// expanding list
				if (test instanceof Scenario) {
					allFullUuid.add(test.getFullUUID());
				}
				// Added in oeder to resolve bug #266
				if (test instanceof AntFlowControl) {
					allFullUuid.add(((AntFlowControl) test).getFlowFullUUID());
				}
			}
			for (TreePath path : treePaths) {
				String fullUuid = getTreePathUUID(path);
				if (allFullUuid.contains(fullUuid)) {
					if (expand) {
						expandAll(path);
					} else {
						collapseAll(path);
					}
				}
			}
		} else if (source.equals(markScenarioAsTest) || source.equals(unMarkScenarioAsTest)) {
			Scenario root = (Scenario) currentNode.getTest();
			boolean scenarioAsTest = source.equals(markScenarioAsTest);
			root.setScenarioAsTest(scenarioAsTest);
			ScenarioHelpers.setDirtyFlag();

			testsTreeControler.refreshView();
			testsTreeControler.expandTree();
		} else if (source.equals(editOnlyLocallyItem)) {
			Scenario root = (Scenario) currentNode.getTest();
			root.setEditLocalOnly(!root.isEditLocalOnly());
			ScenarioHelpers.setDirtyFlag();
			testsTreeControler.refreshView();
			testsTreeControler.expandTree();
		} else if (source.equals(markAsKnownIssue) || source.equals(unmarkAsKnownIssue)) {
			for (JTest test : selectedTests) {
				test.markAsKnownIssue(source.equals(markAsKnownIssue));
			}
			ScenarioHelpers.setDirtyFlag();
		} else if (source.equals(markAsNegativeTest) || source.equals(unMarkAsNegativeTest)) {
			for (JTest test : selectedTests) {
				test.markAsNegativeTest(source.equals(markAsNegativeTest));
			}
			ScenarioHelpers.setDirtyFlag();
		} else if (source.equals(hideScenarioInHTML)) {
			for (JTest test : selectedTests) {
				test.hideInHTML(!test.isHiddenInHTML());
			}

			ScenarioHelpers.setDirtyFlag();

		} else if (source.equals(popupNavigateToSubScenario)) {
			// Added in order to resolve bug #246
			try {
				SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
			} catch (Exception e1) {
				Log.error(e1.getMessage());
			}
			loadScenario(((Scenario) currentNode.getTest()).getName(), false);
			return;
			// Limor Bortman
		} else if (source.equals(resetToDefault)) {
			int answer = JOptionPane.showConfirmDialog(TestRunner.treeView,
					"Are you sure you want to reset all the parmters of the selected tests\\scenarios?", JsystemMapping
							.getInstance().getResetToDefaultWindow(), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.INFORMATION_MESSAGE);
			if (0 == answer) {
				try {
					for (JTest test : selectedTests) {
						test.resetToDefault();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		refreshTree();
	}

	/**
	 * Handles Mapping and unMapping of one or more tests in scenario test
	 */
	public void handleMultipleNodesMap() {
		handleMultipleNodesMap(!getContainer().isMapped());

	}

	public void handleMultipleNodesMap(boolean mapOrUnMap) {
		try {
			ArrayList<JTest> list = new ArrayList<JTest>();
			TreePath[] treePaths = rowsToPaths(paths);
			for (int i = 0; i < paths.length; i++) {
				ScenarioTreeNode node = (ScenarioTreeNode) treePaths[i].getLastPathComponent();
				JTest test = node.getTest();
				list.add(test);
			}
			MultipleScenarioOps.checkAllNodes(list, mapOrUnMap);
		} catch (Exception e1) {
			ErrorPanel.showErrorDialog("Failed mapping/unmapping element", StringUtils.getStackTrace(e1),
					ErrorLevel.Error);
		} finally {
			refreshTree();
		}
	}

	/**
	 * load a given scenario to scenario tree panel
	 * 
	 * @param scenarioName
	 *            the scenario to load
	 * @param wait
	 *            if True will wait for Scenario loading thread to finish
	 */
	public void loadScenario(final String scenarioName, boolean wait) {
		// make sure scenario name starts with "scenarios/"
		final String updatedScenarioName = ScenarioHelpers.addScenarioHeader(scenarioName);
		resetInfoTab();
		WaitDialog.launchWaitDialog(JsystemMapping.getInstance().getLoadScenarioDialog(), null);
		Thread t = new Thread() {
			public void run() {
				try {
					try {
						clearExpandSelected(true, true);
						selectScenario(updatedScenarioName);
						expandAll();
						updateEnabledAndDisabledActions(null);
					} catch (Exception e1) {
						log.log(Level.SEVERE, "Fail to load scenario: " + updatedScenarioName, e1);
						ErrorPanel.showErrorDialog(
								"Problem loading Scenario \""
										+ ScenarioHelpers.removeScenarioHeader(updatedScenarioName) + "\"",
								StringUtils.getStackTrace(e1), ErrorLevel.Error);
					}
				} finally {
					WaitDialog.endWaitDialog();
				}
			}
		};
		t.start();

		if (wait) {
			try {
				t.join();
			} catch (Exception e) {
				ErrorPanel.showErrorDialog("Failed waiting for Scenario to load.", e, ErrorLevel.Error);
			}
		}
	}

	/**
	 * clear the inner selected tests data and expanded paths data
	 * 
	 * @param sExpand
	 *            if True will clear expanded list and add root to it
	 * @param sSelected
	 *            if True will clear selected list
	 */
	public void clearExpandSelected(boolean sExpand, boolean sSelected) {
		if (sExpand) {
			TreePath rootPath = new TreePath(((TreeNode) tree.getModel().getRoot()));
			expandedPaths.clear();
			expandedPaths.put(getTreePathUUID(rootPath), rootPath);
		}
		if (sSelected) {
			selectedTests = null;
			lastPaths = new int[0];
		}
	}

	/**
	 * handling comment buttons - erase,ok and cancel
	 * 
	 * @param scenario
	 *            the current scenario
	 * @param source
	 *            the source button
	 * @throws Exception
	 */
	public void handleComment(Scenario scenario, Object source) {
		if (source.equals(commentButton)) {
			commentFrame.setVisible(false);
			String comment = commentTxt.getText();
			commentTxt.setText("");
			try {
				MultipleScenarioOps.editComment(currentNode.getTest(), comment);
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to update Scenario after Renaming", e1);
			}
			refreshTree();
			tree.getSelectionModel().setSelectionPath(lastClickedPath);
		} else if (source.equals(eraseButton)) {
			commentTxt.setText("");
		} else if (source.equals(cancelButton)) {
			commentFrame.setVisible(false);
		}
	}

	/**
	 * creates a custom popup RightMenu for the tests
	 * 
	 * @param type
	 *            TEST_POP_UP/ROOT_POP_UP/SCEN_POP_UP
	 * @param comment
	 *            add comment
	 * @param map
	 *            add map/false mean unmap
	 * @param moveUp
	 *            add moveUp
	 * @param moveDown
	 *            add moveDown
	 * @param collapse
	 * 
	 * @param expand
	 * 
	 * @param scenarioAsTest
	 *            if on root, then signals if root is marked as test
	 * @param markedAsKnownIssue
	 * 
	 * @param meaningfulName
	 *            add meaningful name setting
	 * @param hiddenInHTML
	 * 
	 * @param markedAsNegativeTest
	 * 
	 * @param mapAll
	 * 
	 * @param unMapAll
	 * 
	 * @return the created JPopupMenu
	 */
	private JPopupMenu createPopup(int type, boolean comment, boolean map, boolean moveUp, boolean moveDown,
			boolean collapse, boolean expand, boolean scenarioAsTest, boolean markedAsKnownIssue,
			boolean meaningfulName, boolean hiddenInHTML, boolean markedAsNegativeTest, boolean mapAll, boolean unMapAll) {

		popupMenu = new JPopupMenu();

		if (type == TEST_POP_UP) { // create test popup menu
			if (meaningfulName
					&& !"true".equals(JSystemProperties.getInstance().getPreference(
							FrameworkOptions.IGNORE_MEANINGFUL_NAME))) {
				popupSetName = new JMenuItem(JsystemMapping.getInstance().getUpdateMeaningfulNameMenuItem());
				popupSetName.addActionListener(this);
				popupMenu.add(popupSetName);
			}
			if (comment) {
				popupCommentItem = new JMenuItem("Comment Item");
				popupCommentItem.addActionListener(this);
				popupMenu.add(popupCommentItem);
			}
			if (map) {
				popupMapItem = new JMenuItem(JsystemMapping.getInstance().getTestMapMenuItem(), ImageCenter
						.getInstance().getImage(ImageCenter.ICON_CHECK));
				popupMapItem.addActionListener(this);
				popupMenu.add(popupMapItem);
			} else { // unmap item
				popupUnmapItem = new JMenuItem(JsystemMapping.getInstance().getTestUnmapMenuItem(), ImageCenter
						.getInstance().getImage(ImageCenter.ICON_UNCHECK));
				popupUnmapItem.addActionListener(this);
				popupMenu.add(popupUnmapItem);
			}

			if (moveUp || moveDown) {
				if (moveUp) {
					popupMenu.add(MoveUpAction.getInstance());
					popupMenu.add(MoveToTopAction.getInstance());
				}
				if (moveDown) {
					popupMenu.add(MoveDownAction.getInstance());
					popupMenu.add(MoveToBottomAction.getInstance());
				}
			}

			// delete is always shown
			popupMenu.add(RemoveItemAction.getInstance());

			if (scenarioAsTest
					&& !"false"
							.equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SCENARIO_AS_TEST))) {
				popupNavigateToSubScenario = new JMenuItem("Navigate to sub scenario");
				popupNavigateToSubScenario.addActionListener(this);
				popupMenu.add(popupNavigateToSubScenario);
			}

			if (!"false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SCENARIO_AS_TEST))) {
				if (scenarioAsTest) {
					unMarkScenarioAsTest = new JMenuItem(JsystemMapping.getInstance().getScenarioUnMarkAsTestMenuItem());
					unMarkScenarioAsTest.addActionListener(this);
					popupMenu.add(unMarkScenarioAsTest);
				}
			}

			if (markedAsNegativeTest) {
				unMarkAsNegativeTest = new JMenuItem(JsystemMapping.getInstance().getUnMarkAsNegativeTestMenuItem(),
						ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_CANCEL_NEGETIVE));
				unMarkAsNegativeTest.addActionListener(this);
				popupMenu.add(unMarkAsNegativeTest);
			} else {
				markAsNegativeTest = new JMenuItem(JsystemMapping.getInstance().getMarkAsNegativeTestMenuItem(),
						ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_NEGETIVE));
				markAsNegativeTest.addActionListener(this);
				popupMenu.add(markAsNegativeTest);
			}

			if (markedAsKnownIssue) {
				unmarkAsKnownIssue = new JMenuItem(
						JsystemMapping.getInstance().getScenarioUnMarkAsKnownIssueMenuItem(), ImageCenter.getInstance()
								.getImage(ImageCenter.MENU_ICON_CANCEL_KNOWN_ISSUE));
				unmarkAsKnownIssue.addActionListener(this);
				popupMenu.add(unmarkAsKnownIssue);
			} else {
				markAsKnownIssue = new JMenuItem(JsystemMapping.getInstance().getScenarioMarkAsKnownIssueMenuItem(),
						ImageCenter.getInstance().getImage(ImageCenter.MENU_ICON_KNOWN_ISSUE));
				markAsKnownIssue.addActionListener(this);
				popupMenu.add(markAsKnownIssue);
			}

			if (hiddenInHTML) {
				hideScenarioInHTML = new JMenuItem(JsystemMapping.getInstance().getShowInHTMLMenuItem());
			} else {
				hideScenarioInHTML = new JMenuItem(JsystemMapping.getInstance().getHideInHTMLMenuItem());
			}
			hideScenarioInHTML.addActionListener(this);
			popupMenu.add(hideScenarioInHTML);

			// Limor Bortman
			// return To Default for the test popup men
			addResetToDefault();

		} else if (type == ROOT_POP_UP) {

			if (comment) {
				popupCommentItem = new JMenuItem("Comment Item");
				popupCommentItem.addActionListener(this);
				popupMenu.add(popupCommentItem);
			}

			if (mapAll) {
				popupMapItem = new JMenuItem(JsystemMapping.getInstance().getTestMapAllMenuItem(), ImageCenter
						.getInstance().getImage(ImageCenter.ICON_CHECK));
				popupMapItem.addActionListener(this);
				popupMenu.add(popupMapItem);
			}

			if (unMapAll) {
				popupUnmapItem = new JMenuItem(JsystemMapping.getInstance().getTestUnmapAllMenuItem(), ImageCenter
						.getInstance().getImage(ImageCenter.ICON_UNCHECK));
				popupUnmapItem.addActionListener(this);
				popupMenu.add(popupUnmapItem);
			}

			if (!"true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.IGNORE_MEANINGFUL_NAME))) {
				popupSetName = new JMenuItem(JsystemMapping.getInstance().getUpdateMeaningfulNameMenuItem());
				popupSetName.addActionListener(this);
				popupMenu.add(popupSetName);
			}

			if (!"false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SCENARIO_AS_TEST))) {
				if (!scenarioAsTest) {
					markScenarioAsTest = new JMenuItem(JsystemMapping.getInstance().getScenarioMarkAsTestMenuItem());
					markScenarioAsTest.addActionListener(this);
					popupMenu.add(markScenarioAsTest);
				} else {
					unMarkScenarioAsTest = new JMenuItem(JsystemMapping.getInstance().getScenarioUnMarkAsTestMenuItem());
					unMarkScenarioAsTest.addActionListener(this);
					popupMenu.add(unMarkScenarioAsTest);
				}
			}

			if (collapse) {
				popupCollapseTree = new JMenuItem("Collapse tree");
				popupCollapseTree.addActionListener(this);
				popupMenu.add(popupCollapseTree);
			}

			if (expand) {
				popupExpandTreeRoot = new JMenuItem("Expand Root tree");
				popupExpandTreeRoot.addActionListener(this);
				popupMenu.add(popupExpandTreeRoot);
			} else {
				popupCollapseTreeRoot = new JMenuItem("Collapse Root tree");
				popupCollapseTreeRoot.addActionListener(this);
				popupMenu.add(popupCollapseTreeRoot);
			}

			// APPLIED - Adding the Edit Local property to the Scenario
			if (ScenariosManager.getInstance().getCurrentScenario().isEditLocalOnly()) {
				editOnlyLocallyItem = new JMenuItem(JsystemMapping.getInstance().getScenarioEditOnlyLocallyItem(),
						ImageCenter.getInstance().getImage(ImageCenter.ICON_CHECK));
				editOnlyLocallyItem.addActionListener(this);
				popupMenu.add(editOnlyLocallyItem);
			} else {
				editOnlyLocallyItem = new JMenuItem(JsystemMapping.getInstance().getScenarioEditOnlyLocallyItem());
				editOnlyLocallyItem.addActionListener(this);
				popupMenu.add(editOnlyLocallyItem);
			}

			// return To Default for the test Root menu
			addResetToDefault();

		} else if (type == SCEN_POP_UP) {

			// Scenario popup Menu

			if (mapAll) {
				popupMapItem = new JMenuItem("Map All", ImageCenter.getInstance().getImage(ImageCenter.ICON_CHECK));
				popupMapItem.addActionListener(this);
				popupMenu.add(popupMapItem);
			}

			if (unMapAll) {
				popupUnmapItem = new JMenuItem("Unmap All", ImageCenter.getInstance()
						.getImage(ImageCenter.ICON_UNCHECK));
				popupUnmapItem.addActionListener(this);
				popupMenu.add(popupUnmapItem);
			}

			if (comment) {
				popupCommentItem = new JMenuItem("Comment Item");
				popupCommentItem.addActionListener(this);
				popupMenu.add(popupCommentItem);

			}

			if (!"true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.IGNORE_MEANINGFUL_NAME))) {
				popupSetName = new JMenuItem(JsystemMapping.getInstance().getUpdateMeaningfulNameMenuItem());
				popupSetName.addActionListener(this);
				popupMenu.add(popupSetName);
			}

			if (moveUp || moveDown) {
				if (moveUp) {
					popupMenu.add(MoveUpAction.getInstance());
					popupMenu.add(MoveToTopAction.getInstance());
				}
				if (moveDown) {
					popupMenu.add(MoveDownAction.getInstance());
					popupMenu.add(MoveToBottomAction.getInstance());
				}
			}
			// delete is always shown
			popupMenu.add(RemoveItemAction.getInstance());

			if (collapse) {
				popupCollapseTree = new JMenuItem("Collapse tree");
				popupCollapseTree.addActionListener(this);
				popupMenu.add(popupCollapseTree);
			}
			if (expand) {
				popupExpandTree = new JMenuItem("Expand tree");
				popupExpandTree.addActionListener(this);
				popupMenu.add(popupExpandTree);
			}

			if (!"false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SCENARIO_AS_TEST))) {
				markScenarioAsTest = new JMenuItem(JsystemMapping.getInstance().getScenarioMarkAsTestMenuItem());
				markScenarioAsTest.addActionListener(this);
				popupMenu.add(markScenarioAsTest);
			}

			if (hiddenInHTML) {
				hideScenarioInHTML = new JMenuItem("Show in HTML");
			} else {
				hideScenarioInHTML = new JMenuItem("Hide in HTML");
			}
			hideScenarioInHTML.addActionListener(this);
			popupMenu.add(hideScenarioInHTML);

			popupNavigateToSubScenario = new JMenuItem("Navigate to sub scenario");
			popupNavigateToSubScenario.addActionListener(this);
			popupMenu.add(popupNavigateToSubScenario);

			// APPLIED - Adding an item to the popup menu created when a
			// scenario is selected
			try {
				Scenario scenario = (Scenario) currentNode.getTest();
				if (ScenariosManager.getInstance().getScenario(scenario.getName()).isEditLocalOnly()) {
					editOnlyLocallyItem = new JMenuItem(JsystemMapping.getInstance().getScenarioEditOnlyLocallyItem(),
							ImageCenter.getInstance().getImage(ImageCenter.ICON_CHECK));
					editOnlyLocallyItem.addActionListener(this);
					editOnlyLocallyItem.setEnabled(false);
					popupMenu.add(editOnlyLocallyItem);
				} else {
					editOnlyLocallyItem = new JMenuItem(JsystemMapping.getInstance().getScenarioEditOnlyLocallyItem());
					editOnlyLocallyItem.addActionListener(this);
					editOnlyLocallyItem.setEnabled(false);
					popupMenu.add(editOnlyLocallyItem);
				}
			} catch (Exception e) {
				
			}
			// Limor Bortman
			addResetToDefault();
		} else if (type == FIXTURE_POP_UP) {

			if (comment) {
				popupCommentItem = new JMenuItem("Comment Item");
				popupCommentItem.addActionListener(this);
				popupMenu.add(popupCommentItem);
			}

			popupMenu.add(RemoveItemAction.getInstance());
		}
		// Added in order to resolve bug #266
		else if (type == FLOW_POP_UP) {
			if (moveUp || moveDown) {
				if (moveUp) {
					popupMenu.add(MoveUpAction.getInstance());
				}
				if (moveDown) {
					popupMenu.add(MoveDownAction.getInstance());
				}
			}

			// delete is always shown
			popupMenu.add(RemoveItemAction.getInstance());

			if (meaningfulName
					&& !"true".equals(JSystemProperties.getInstance().getPreference(
							FrameworkOptions.IGNORE_MEANINGFUL_NAME))) {
				popupSetName = new JMenuItem(JsystemMapping.getInstance().getUpdateMeaningfulNameMenuItem());
				popupSetName.addActionListener(this);
				popupMenu.add(popupSetName);
			}

			if (mapAll) {
				popupMapItem = new JMenuItem("Map All", ImageCenter.getInstance().getImage(ImageCenter.ICON_CHECK));
				popupMapItem.addActionListener(this);
				popupMenu.add(popupMapItem);
			}

			if (unMapAll) {
				popupUnmapItem = new JMenuItem("Unmap All", ImageCenter.getInstance()
						.getImage(ImageCenter.ICON_UNCHECK));
				popupUnmapItem.addActionListener(this);
				popupMenu.add(popupUnmapItem);
			}

			if (collapse) {
				popupCollapseTree = new JMenuItem("Collapse tree");
				popupCollapseTree.addActionListener(this);
				popupMenu.add(popupCollapseTree);
			}
			if (expand) {
				popupExpandTree = new JMenuItem("Expand tree");
				popupExpandTree.addActionListener(this);
				popupMenu.add(popupExpandTree);
			}

		}
		
		// Handling the context menu plugins. 
		for (ContextMenuPlugin plugin : contextMenuPlugins){
			if (plugin.shouldDisplayed(currentNode, null, currentNode.getTest())){
				JMenuItem pluginMenu = new JMenuItem(plugin.getItemName());
				if (plugin.getIcon() != null){
					pluginMenu.setIcon(plugin.getIcon());					}
				pluginMenu.addActionListener(plugin);
				popupMenu.add(pluginMenu);
			}
		}

		return popupMenu;
	}

	// Limor Bortman
	private void addResetToDefault() {
		resetToDefault = new JMenuItem(JsystemMapping.getInstance().getScenarResetToDefault());
		resetToDefault.addActionListener(this);
		popupMenu.add(resetToDefault);
	}

	public void createComment() {
		int width = 300;
		int height = 100;
		commentFrame = new JFrame("TYPE A NEW COMMENT");
		commentFrame.setResizable(false);
		commentFrame.setLayout(new BorderLayout());
		commentFrame.setSize(new Dimension(width, height));
		commentFrame.setLocation(new Point(200, 200));

		commentPanel = new JPanel();
		commentPanel.setLayout(new BorderLayout());
		commentPanel.setSize(new Dimension(width, height));

		commentTxt = new JTextArea();
		commentTxt.setLayout(new BorderLayout());
		commentTxt.setSize(new Dimension(width, height / 2));

		JScrollPane commentScroll = new JScrollPane(commentTxt);
		commentScroll.setSize(new Dimension(width, height));

		commentButton = new JButton("Ok");
		commentButton.setMinimumSize(new Dimension(width / 2, height / 4));
		commentButton.addActionListener(this);

		eraseButton = new JButton("Clear");
		eraseButton.setMinimumSize(new Dimension(width / 2, height / 4));
		eraseButton.addActionListener(this);

		cancelButton = new JButton("Cancel");
		cancelButton.setMinimumSize(new Dimension(width / 2, height / 4));
		cancelButton.addActionListener(this);

		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, commentButton, cancelButton);
		sp.setDividerLocation(width / 3);
		sp.setDividerSize(0);

		JSplitPane sp1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, eraseButton, sp);
		sp1.setDividerLocation(width / 3);
		sp1.setDividerSize(0);

		commentPanel.add(commentScroll, BorderLayout.CENTER);
		commentPanel.add(sp1, BorderLayout.SOUTH);

		commentFrame.add(commentPanel);
	}

	/**
	 * show the RightMenu (if right clicked)
	 * 
	 * @param e
	 *            event
	 */
	private void showRightMenu(MouseEvent e) {
		// don't allow any right click when edit is disabled
		if ("false".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT)))
			return;
		int x = e.getX();
		int y = e.getY();

		TestsContainer container = getContainer();
		ScenarioTreeNode currentNode = null;

		// save the path for future use
		TreePath clickedPath = tree.getPathForLocation(x, y);
		if (clickedPath == null) {
			return;
		}
		lastClickedPath = clickedPath;

		// save the selected node
		currentNode = (ScenarioTreeNode) clickedPath.getLastPathComponent();

		// get position
		if (rightMenuAlreadyOpened || container.hasRoot() || !container.contains(currentNode.getTest())) {
			tree.setSelectionPath(clickedPath);
		}

		container = getContainer();

		// show the popup menu
		JPopupMenu menu = null;
		int type = TEST_POP_UP;
		// check if there is only one test selected
		boolean comment = (tree.getSelectionCount() < 2);
		// check if there is an unmapped item
		boolean map = !container.isMapped();

		JTest test = container.getLast();

		boolean moveUp = false;
		boolean moveDown = false;
		boolean expand = false;
		boolean collapse = false;
		boolean mapAll = false;
		boolean unMapAll = false;
		boolean singleTestScenarioSelected = false;
		boolean meaningful = false;
		boolean markedAsKnownIssue = test.isMarkedAsKnownIssue();
		boolean markedAsNegativeTest = test.isMarkedAsNegativeTest();
		boolean hiddenInHTML = test.isHiddenInHTML();
		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();
		// check if can move up
		moveUp = rootScenario.canMoveUp(container);
		// check if can move down
		moveDown = rootScenario.canMoveDown(container);

		singleTestScenarioSelected = container.hasTestScenario() && container.getNumOfTests() == 1;

		meaningful = container.getNumOfTests() == 1;

		// Changed in order to resolve bug #266
		if (container.hasScenario() || container.hasFlowControl()) {
			String fullUuid = test.getFullUUID();
			if (test instanceof AntFlowControl) {
				fullUuid = ((AntFlowControl) test).getFlowFullUUID();
			}
			if (expandedPaths.containsKey(fullUuid)) {
				collapse = true;
			}
			ArrayList<TreePath> paths = new ArrayList<TreePath>();
			getAllPaths(tree.getSelectionPath(), paths);
			for (TreePath path : paths) {
				TreeNode node = (TreeNode) path.getLastPathComponent();
				if (node.getChildCount() > 0) {
					fullUuid = getTreePathUUID(path);
					if (!expandedPaths.containsKey(fullUuid)) {
						expand = true;
					}
				} else {
					if (((ScenarioTreeNode) node).getTest().isDisable()) {
						if (!mapAll) {
							mapAll = true;
						}
					} else {
						if (!unMapAll) {
							unMapAll = true;
						}
					}
				}
			}
		}

		if (currentNode.isRoot()) { // ROOT Menu
			menu = createPopup(ROOT_POP_UP, comment, false, false, false, false, expand,
					rootScenario.isScenarioAsTest(), markedAsKnownIssue, true, hiddenInHTML, markedAsNegativeTest,
					mapAll, unMapAll);
		} else {
			if (container.hasScenario() && container.getNumOfTests() == 1) { // Single
				// Scenario
				// selected
				type = SCEN_POP_UP;
				// Added in order to resolve bug #266
			} else if (test instanceof AntFlowControl) {
				type = FLOW_POP_UP;
			} else {
				type = TEST_POP_UP;
			}

			if (test instanceof RunnerFixture) {
				menu = createPopup(FIXTURE_POP_UP, true, false, false, false, false, false, false, false, false, false,
						false, false, false);
			} else {
				menu = createPopup(type, comment, map, moveUp, moveDown, collapse, expand, singleTestScenarioSelected,
						markedAsKnownIssue, meaningful, hiddenInHTML, markedAsNegativeTest, mapAll, unMapAll);
			}
		}

		menu.show(tree, x, y);
		rightMenuAlreadyOpened = true;
	}

	private void moveToBottom() throws Exception {
		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();

		Scenario scenarioToMoveIn = rootScenario;

		JTest[] tests = getContainer().getTests();

		for (int i = tests.length - 1; i >= 0; i--) {
			JTest testToMove = tests[i];

			ArrayList<Integer> containersPath = new ArrayList<Integer>();
			JTestContainer containerToMoveIn = testToMove.getParent();

			scenarioToMoveIn = ScenarioHelpers.findPath(containersPath, containerToMoveIn);

			// ----------------------------------------------------
			int index = containerToMoveIn.getRootIndex(testToMove);

			MultipleScenarioOps.moveTestToBottom(rootScenario, scenarioToMoveIn, containersPath, index);
		}
	}

	private void moveToTop() throws Exception {

		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();

		Scenario scenarioToMoveIn = rootScenario;

		JTest[] tests = getContainer().getTests();

		for (JTest testToMove : tests) {
			ArrayList<Integer> containersPath = new ArrayList<Integer>();
			JTestContainer containerToMoveIn = testToMove.getParent();

			scenarioToMoveIn = ScenarioHelpers.findPath(containersPath, containerToMoveIn);

			// ----------------------------------------------------
			int index = containerToMoveIn.getRootIndex(testToMove);
			MultipleScenarioOps.moveTestToTop(rootScenario, scenarioToMoveIn, containersPath, index);

		}
	}

	private void moveUp() throws Exception {
		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();

		Scenario scenarioToMoveIn = rootScenario;

		// ArrayList<String> allFullUuid = new ArrayList<String>();
		// ArrayList<TreePath> treePaths = getAllPaths();

		// Changed in order to resolve bug #288
		JTest[] tests = selectedTests;

		for (JTest testToMove : tests) {
			ArrayList<Integer> containersPath = new ArrayList<Integer>();
			// TODO: maybe we get exception if we try to delete the root...
			JTestContainer containerToMoveIn = testToMove.getParent();

			scenarioToMoveIn = ScenarioHelpers.findPath(containersPath, containerToMoveIn);

			// ----------------------------------------------------
			int index = containerToMoveIn.getRootIndex(testToMove);
			MultipleScenarioOps.moveTestUp(rootScenario, scenarioToMoveIn, containersPath, index);

			// if (testToMove instanceof Scenario) {
			// // expandedPaths.remove(testToMove.getFullUUID());
			// allFullUuid.add(testToMove.getFullUUID());
			// }
		}

		// for (TreePath path : treePaths){
		// String fullUuid = getTreePathUUID(path);
		// if (allFullUuid.contains(fullUuid)){
		// expandAll(path, false);
		// }
		// }

	}

	private void moveDown() throws Exception {
		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();

		Scenario scenarioToMoveIn = rootScenario;

		// ArrayList<String> allFullUuid = new ArrayList<String>();
		// ArrayList<TreePath> treePaths = getAllPaths();

		// Changed in order to resolve bug #288
		JTest[] tests = selectedTests;

		for (int i = tests.length - 1; i >= 0; i--) {
			JTest testToMove = tests[i];
			ArrayList<Integer> containersPath = new ArrayList<Integer>();
			JTestContainer containerToMoveIn = testToMove.getParent();

			scenarioToMoveIn = ScenarioHelpers.findPath(containersPath, containerToMoveIn);

			// ----------------------------------------------------
			int index = containerToMoveIn.getRootIndex(testToMove);
			MultipleScenarioOps.moveTestDown(rootScenario, scenarioToMoveIn, containersPath, index);

			// if (testToMove instanceof Scenario) {
			// // expandedPaths.remove(testToMove.getFullUUID());
			// allFullUuid.add(testToMove.getFullUUID());
			// }
		}

		// for (TreePath path : treePaths){
		// String fullUuid = getTreePathUUID(path);
		// if (allFullUuid.contains(fullUuid)){
		// expandAll(path, false);
		// }
		// }

	}

	private void removeTests(JTest[] selectedTests) throws Exception {
		int ans = JOptionPane.YES_OPTION;
		ans = DialogWithCheckBox.showConfirmDialog("Delete confirmation",
				"Are you sure that you want to delete this item?",
				"In the future, don't show this dialog and Auto-delete", FrameworkOptions.AUTO_DELETE_NO_CONFIRMATION);
		if (ans != JOptionPane.YES_OPTION) {
			return;
		} else {
			try {
				ScenariosManager.setDirty();
				ScenariosManager.setDirtyStateEventsSilent(true);

				Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();

				Scenario scenarioToRemoveFrom = rootScenario;

				ArrayList<String> allFullUuid = new ArrayList<String>();
				ArrayList<TreePath> treePaths = getAllPaths();

				for (JTest testToRemove : selectedTests) {
					ArrayList<Integer> containersPath = new ArrayList<Integer>();
					// TODO: maybe we get exception if we try to delete the
					// root...
					JTestContainer containerToDeleteFrom = testToRemove.getParent();

					try {
						scenarioToRemoveFrom = ScenarioHelpers.findPath(containersPath, containerToDeleteFrom);
					} catch (Exception e) {
						// Probably the reason for this exception is related to
						// bug
						// #1917 - deleting
						// parent before its child. The simplest thing would be
						// skipping
						// to the next test
						// to delete
						continue;
					}

					// ----------------------------------------------------
					int index = containerToDeleteFrom.getRootIndex(testToRemove);

					MultipleScenarioOps.removeTests(rootScenario, scenarioToRemoveFrom, containersPath, index);

					// Added in order to resolve bug #288
					String fullUuid = testToRemove.getFullUUID();
					if (testToRemove instanceof AntFlowControl) {
						fullUuid = ((AntFlowControl) testToRemove).getFlowFullUUID();
					}
					// Changed in order to resolve bug #288
					if (testToRemove instanceof Scenario || testToRemove instanceof AntFlowControl) {
						expandedPaths.remove(fullUuid);
						allFullUuid.add(fullUuid);
					}
				}

				for (TreePath path : treePaths) {
					String fullUuid = getTreePathUUID(path);
					if (allFullUuid.contains(fullUuid)) {
						expandAll(path);
					}
				}

				tree.clearSelection();

				resetInfoTab();
			} finally {
				ScenariosManager.setDirtyStateEventsSilent(false);
			}
		}
	}

	/**
	 * address the TestsTreeView and reset the TestInformationTab
	 * 
	 */
	private void resetInfoTab() {
		TestRunner.treeView.resetInfoTab();
	}

	private void updateSelections(JTest[] selectedTests, int[] paths, boolean refreshTree) {
		if (selectedTests == null || paths == null) {
			return;
		}
		/**
		 * updating selections after up / down action.
		 */
		for (int i = 0; i < selectedTests.length; i++) {
			IntegerWrapper wrapper = new IntegerWrapper();
			wrapper.value = 0;

			/**
			 * get the node new location.
			 */
			findNode(((ScenarioTreeNode) tree.getModel().getRoot()), selectedTests[i], wrapper);

			/**
			 * update the new row.
			 */
			paths[i] = wrapper.value;
		}

		if (refreshTree) {
			refreshTree();
		}

		if (paths != null) {
			TreePath[] treePaths = rowsToPaths(paths);
			tree.getSelectionModel().setSelectionPaths(treePaths);
		}
	}

	/**
	 * selects test in tree
	 * 
	 * @param test
	 */
	public void selectTest(JTest test) {
		updateSelections(new JTest[] { test }, new int[1], true);
		TestRunner.treeView.testInformation.setCurrentTest(test, false, false);
	}

	/**
	 * for jsystemobject
	 */
	public void changeSut(String sutFileName) throws Exception {
		RunnerTest rt = new RunnerTest(ChangeSutTest.class.getName(), "changeSut");
		Properties p = new Properties();
		p.setProperty("Sut", sutFileName);
		rt.setProperties(p);
		ScenariosManager.getInstance().getCurrentScenario().addTest(rt);
		refreshTree();
	}

	public void filterSuccess(Scenario scenario) {
		try {
			Vector<JTest> tests = scenario.getTests();
			Vector<RunnerTest> fails = new Vector<RunnerTest>();
			boolean failFound = false;
			for (int i = 0; i < tests.size(); i++) {
				RunnerTest rtest = (RunnerTest) tests.elementAt(i);
				if (rtest.getStatus() != RunnerTest.STAT_SUCCESS) {
					failFound = true;
					fails.addElement(rtest);
				}
			}
			if (failFound) {
				Scenario s = ScenariosManager.getInstance().getScenario(scenario.getName() + "_fails");
				ScenariosManager.getInstance().setCurrentScenario(s);
				for (int i = 0; i < tests.size(); i++) {
					RunnerTest rtest = (RunnerTest) tests.elementAt(i);
					if (rtest.getStatus() != RunnerTest.STAT_SUCCESS || rtest.getTest() instanceof ChangeSutTest) {
						s.addTest(rtest);
					}
				}
			}

			tree.getSelectionModel().clearSelection();
			refreshTree();
		} catch (Exception ex) {
			log.log(Level.WARNING, "Fail to filter success", ex);
		}
		testsTreeControler.refreshView();
		testsTreeControler.expandTree();
	}

	public void selectScenario(String scenarioName) throws Exception {
		log.fine("Starting to load scenario - " + scenarioName);
		ScenariosManager.setDirtyStateEventsSilent(true);
		ScenarioHelpers.resetCache();
		Scenario s = ScenariosManager.getInstance().getScenario(scenarioName);
		ScenariosManager.getInstance().setCurrentScenario(s);
		// ScenarioUIUtils.showScenarioErrorDialog(s);
		tree.getSelectionModel().clearSelection();
		statusBar.setMessage(s.getName());
		refreshTree();
		ScenariosManager.setDirtyStateEventsSilent(false);

		log.fine("Ended to load scenario - " + scenarioName);
	}

	public void clearScenario(boolean ask) {

		JTest[] selectedTests = getContainer().getTests();
		TreePath[] selectedPaths = tree.getSelectionPaths();
		ArrayList<TreePath> paths = new ArrayList<TreePath>();
		/**
		 * case of no selection has been made.
		 */
		if (selectedTests == null || selectedTests.length == 0) {
			selectedTests = new JTest[] { ScenariosManager.getInstance().getCurrentScenario() };
		}

		for (int i = 0; i < selectedTests.length; i++) {
			try {
				if (selectedTests[i] instanceof RunnerTest) {
					continue;
				}

				if (selectedPaths != null && selectedPaths.length > i) {
					getAllPaths(selectedPaths[i], paths);
				}

				Scenario s = (Scenario) selectedTests[i];

				String warningText = "Are you sure you want to delete the scenario " + s.getName() + " ?";

				if (!ask
						|| (JOptionPane.showConfirmDialog(null, warningText, "Delete Scenario",
								JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)) {

					s.cleanAll();

					if (!s.getName().equals("scenarios/default")) {
						ScenarioHelpers.deleteScenario(s);

						s = ScenariosManager.getInstance().getScenario("scenarios/default");
						ScenariosManager.getInstance().setCurrentScenario(s);

						statusBar.setMessage("scenarios/default");
					}
				}
			} catch (Exception e1) {
				log.log(Level.SEVERE, "Fail to clear scenario", e1);
			}

			for (TreePath path : paths) {
				String fullUuid = getTreePathUUID(path);
				if (expandedPaths.containsKey(fullUuid)) {
					expandedPaths.remove(fullUuid);
				}
			}
		}

		tree.getSelectionModel().clearSelection();
		refreshTree();

		testsTreeControler.refreshView();
		testsTreeControler.expandTree();

		ScenarioNavigationManager.getInstance().init();
		updateEnabledAndDisabledActions(null);

	}

	public Component getObject() {
		return pane;
	}

	public JTree getTree() {
		return tree;
	}

	public SourceControlHandler getSourceControlHandler() {
		return sourceControlHandler;
	}

	public class MyComboBoxRenderer extends BasicComboBoxRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (-1 < index) {
					list.setToolTipText((value == null) ? "" : new String(value.toString()));
				}
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	private JToolBar createToolBar() {
		toolBar = SwingUtils.getJToolBarWithBgImage("scenario toolbar", JToolBar.HORIZONTAL, ImageCenter.getInstance()
				.getImage(ImageCenter.ICON_SCEANRIO_TOOLBAR_BG));
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(OpenScenarioAction.getInstance());
		toolBar.addSeparator(new Dimension(15, 0));

		JButton upButton = toolBar.add(MoveUpAction.getInstance());
		upButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton downButton = toolBar.add(MoveDownAction.getInstance());
		downButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton topButton = toolBar.add(MoveToTopAction.getInstance());
		topButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton bottomButton = toolBar.add(MoveToBottomAction.getInstance());
		bottomButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton navigateBackwordButton = toolBar.add(PreviosScenarioAction.getInstance());
		navigateBackwordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton navigateForwardButton = toolBar.add(NextScenarioAction.getInstance());
		navigateForwardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton deleteButton = toolBar.add(RemoveItemAction.getInstance());
		deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton undoButton = toolBar.add(ScenarioUndoAction.getInstance());
		undoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton redoButton = toolBar.add(ScenarioRedoAction.getInstance());
		redoButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		toolBar.addSeparator(new Dimension(10, 0));
		toolBar.addSeparator(new Dimension(10, 0));
		toolBar.add(EditScenarioAction.getInstance());
		toolBar.addSeparator(new Dimension(5, 0));
		toolBar.addSeparator(new Dimension(5, 0));

		// TODO The following buttons shouldn't be here but in the Test Tree

		changeSut = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_CHANGE_SUT));
		changeSut.setToolTipText(JsystemMapping.getInstance().getChangeSutEventButton());
		changeSut.addActionListener(this);
		toolBar.add(changeSut);

		toolBar.add(PublishEventAction.getInstance());
		toolBar.addSeparator(new Dimension(5, 0));
		toolBar.add(ViewTestCodeAction.getInstance());

		return toolBar;
	}

	private JToolBar createSourceControlToolBar() {
		sourceControlToolBar = SwingUtils.getJToolBarWithBgImage("source control toolbar", JToolBar.HORIZONTAL,
				ImageCenter.getInstance().getImage(ImageCenter.ICON_SCEANRIO_TOOLBAR_BG));
		sourceControlToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		sourceControlToolBar.setFloatable(false);
		sourceControlToolBar.setRollover(true);
		sourceControlToolBar.setVisible(false);
		addSourceControlButtons(sourceControlToolBar);
		return sourceControlToolBar;

	}

	private JToolBar createFlowControlToolBar() {
		flowControlToolBar = SwingUtils.getJToolBarWithBgImage("scenario toolbar", JToolBar.HORIZONTAL, ImageCenter
				.getInstance().getImage(ImageCenter.ICON_SCEANRIO_TOOLBAR_BG));
		flowControlToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		flowControlToolBar.setFloatable(false);
		flowControlToolBar.setRollover(true);
		flowControlToolBar.setVisible(true);
		addFlowControlButtons(flowControlToolBar);
		return flowControlToolBar;
	}

	private void addFlowControlButtons(JToolBar toolBar) {
		toolBar.add(NewForLoopAction.getInstance());
		toolBar.add(NewSwitchAction.getInstance());
		toolBar.add(NewSwitchCaseAction.getInstance());
		toolBar.add(NewIfConditionAction.getInstance());
		toolBar.add(NewElseIfAction.getInstance());
		toolBar.add(NewDataDrivenAction.getInstance());
	}

	private void addSourceControlButtons(JToolBar toolBar) {
		toolBar.add(ConnectToSourceControlAction.getInstance());
		toolBar.addSeparator(new Dimension(20, 0));
		toolBar.add(new JLabel("Scenario"));
		toolBar.add(ScenarioSCStatusAction.getInstance());
		toolBar.add(AddScenarioAction.getInstance());
		toolBar.add(CommitScenarioAction.getInstance());
		toolBar.add(UpdateScenarioAction.getInstance());
		toolBar.add(RevertScenarioAction.getInstance());
		toolBar.addSeparator(new Dimension(20, 0));
		toolBar.add(new JLabel("SUT"));
		toolBar.add(SutSCStatusAction.getInstance());
		toolBar.add(AddSutAction.getInstance());
		toolBar.add(CommitSutAction.getInstance());
		toolBar.add(UpdateSutAction.getInstance());
		toolBar.add(RevertSutAction.getInstance());
	}

	public void setEnableToolBar(boolean enable) {
		changeSut.setEnabled(enable);
		OpenScenarioAction.getInstance().setEnabled(enable);
	}

	/**
	 * Checks that for situations that are not allowed in the scenario tree :<br>
	 * 1. More then one Fixture is selected<br>
	 * 2. Scenario is copied to itself<br>
	 * 3. adding tests to Switch<br>
	 * 4. adding fixtures to flow control<br>
	 * 
	 * Any error checking should be added to this method.
	 * 
	 * @param tests
	 * @return True : No errors False : Errors was detected
	 * 
	 */
	private boolean checkForErrors(Vector<JTest> tests, JTestContainer selectedContainer) {

		int fixturesCount = 0;

		Scenario selectedScenario = ScenarioHelpers.getFirstScenarioAncestor(selectedContainer);
		//
		// if (selectedScenario.isScenarioAsTest() &&
		// !selectedScenario.isRoot()){
		// ErrorPanel.showErrorDialog("Scenario add error",
		// "You are trying to add Tests to a Test Scenario "+
		// selectedScenario.getName(),
		// ErrorLevel.Warning);
		// return false;
		// }

		for (int i = 0; i < tests.size(); i++) {

			JTest test = (JTest) tests.elementAt(i);

			if (test instanceof RunnerFixture) {
				if (selectedContainer instanceof AntFlowControl) {
					ErrorPanel.showErrorDialog("Adding Fixtures to Flow Control Elements is not Supported",
							"Fixtures can Only be added to Scenarios", ErrorLevel.Warning);

					return false;
				}

				fixturesCount++;
				if (fixturesCount > 1) {
					ErrorPanel
							.showErrorDialog("More Than 1 Fixture selected",
									"More than 1 Fixture Selected for add. \nPlease select only 1 fixture.",
									ErrorLevel.Warning);

					return false;
				}
			}

			if (!(test instanceof AntSwitchCase) && selectedContainer instanceof AntSwitch) {
				ErrorPanel.showErrorDialog("Trying to add illegal elements to switch!",
						"Only Case can be added to Switch", ErrorLevel.Warning);

				return false;
			}

			if (test instanceof Scenario) {

				String scenarioName = ((Scenario) test).getName();
				String selectedScenarioName = selectedScenario.getName();

				Vector<String> subScen = new Vector<String>();
				subScen.addElement(scenarioName);
				((Scenario) test).getSubScenariosNames(subScen);

				for (int j = 0; j < subScen.size(); j++) {
					if (subScen.elementAt(j).equals(selectedScenarioName)) {
						ErrorPanel.showErrorDialog("Scenario add error", "You are trying to add the scenario:\n"
								+ selectedScenarioName + " to itself.", ErrorLevel.Warning);
						return false;
					}

				}
			}
		}

		return true;
	}

	/**
	 * adds tests to scenario menu. container is used to find the lowest
	 * selected test on the scenario menu. the tests are added right after it
	 * 
	 * @param tests
	 *            tests to add from right menu tree
	 */
	public boolean addTests(Vector<JTest> tests) {
		TestsContainer container = getContainer();
		Scenario rootScenario = ScenariosManager.getInstance().getCurrentScenario();
		JTestContainer destJTestContainer = container.getContainerRoot();
		if (destJTestContainer == null) { // no test is selected
			destJTestContainer = rootScenario;
		}

		if (!checkForErrors(tests, destJTestContainer)) {
			return false;
		}

		ArrayList<Integer> containersPath = new ArrayList<Integer>();
		int index = -2;
		containersPath.add(index);
		Scenario scenarioToAddTo = rootScenario;
		destJTestContainer = rootScenario;

		if (!container.isEmpty()) {
			destJTestContainer = container.getContainerRoot();
			JTest last = container.getLast();
			index = destJTestContainer.getRootIndex(last);
			containersPath.set(0, index); // replace the -2
			try {
				scenarioToAddTo = ScenarioHelpers.findPath(containersPath, destJTestContainer);
			} catch (Exception e) {
				ErrorPanel.showErrorDialog(
						"Failed adding a test to scenario.\n" + "The common reason for this is a problem\n "
								+ "in creating an instance of the test class.\n", e, ErrorLevel.Warning);
			}
		}
		// ----------------------------------------------------
		// Add each test into the right place
		int indexCounter = 0;

		ArrayList<JTest> actualTests = new ArrayList<JTest>();

		try {
			ScenariosManager.setDirty();
			ScenariosManager.setDirtyStateEventsSilent(true);

			for (int i = 0; i < tests.size(); i++) {

				JTest test = (JTest) tests.elementAt(i);
				test.setUUID(JTestContainer.getRandomUUID());

				try {
					actualTests.addAll(MultipleScenarioOps.addTestToScenario(rootScenario, scenarioToAddTo,
							containersPath, test, indexCounter));
					indexCounter++;
				} catch (Exception e) {
					ErrorPanel.showErrorDialog("Failed adding a test to scenario.\n"
							+ "The common reason for this is a problem\n "
							+ "in creating an instance of the test class.\n", e, ErrorLevel.Warning);
				}
			}
			// ----------------------------------------------------
			// Update the selection signs
			JTest[] selectedTests = new JTest[container.getNumOfTests()];
			int[] paths = tree.getSelectionModel().getSelectionRows();

			for (int i = 0; i < selectedTests.length; i++) {
				selectedTests[i] = ((ScenarioTreeNode) tree.getPathForRow(paths[i]).getLastPathComponent()).getTest();
			}
			// Expand new scenarios that added
			ArrayList<TreePath> treePaths = getAllPaths();
			ArrayList<String> allFullUuid = new ArrayList<String>();
			for (JTest test : actualTests) {
				if (test instanceof Scenario) {
					allFullUuid.add(test.getFullUUID());
				}
			}
			String containerFullUuid = destJTestContainer.getFullUUID();
			if (destJTestContainer instanceof AntFlowControl) {
				containerFullUuid += StringUtils.isEmpty(containerFullUuid) ? destJTestContainer.getUUID() : "."
						+ destJTestContainer.getUUID();
			}
			for (TreePath path : treePaths) {
				String fullUuid = getTreePathUUID(path);
				if (allFullUuid.contains(fullUuid)) {
					expandAll(path);
				} else if (fullUuid.equals(containerFullUuid)) { // expand the
					// Container we
					// are adding to
					tree.expandPath(path);
				}
			}

			updateSelections(selectedTests, paths, false);
			// ----------------------------------------------------

		} finally {
			ScenariosManager.setDirtyStateEventsSilent(false);
		}

		refreshTree();
		return true;
	}

	class XmlFileFilter extends FileFilter {

		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			return f.getName().toLowerCase().endsWith(".xml");
		}

		public String getDescription() {
			return "xml";
		}
	}

	class MyFileSystemView extends FileSystemView {
		File root = null;

		public MyFileSystemView(File root) {
			this.root = root;
		}
		
		public MyFileSystemView(String preference) {
			this.root = new File(preference);
		}

		public File createNewFolder(File containingDir) throws IOException {
			if (containingDir == null) {
				throw new IOException("Containing directory is null:");
			}
			File newFolder = null;
			// Using NT's default folder name
			newFolder = createFileObject(containingDir, UIManager.getString("FileChooser.other.newFolder"));

			if (newFolder.exists()) {
				throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
			} else {
				newFolder.mkdirs();
			}

			return newFolder;
		}

		public Boolean isTraversable(File f) {
			return Boolean.valueOf(f.getAbsolutePath().startsWith(root.getAbsolutePath()));
		}

		public File[] getRoots() {
			return new File[] { root };
		}

	}
	
	
	/**
	 * Delete Scenario by File Chooser
	 * 1. Create file chooser on tests directory, 
	 *    will show only xml files, throws exception if the selected file isn't xml type
	 *    multi-selection isn't allowed.
	 * 2. Show "Are you sure.." dialog
	 * 3. Try to delete all 4 files: classes\...\[scenario].xml, classes\...\[scenario].properties, tests\...\[scenario].xml, tests\...\[scenario].properties
	 * 
	 * @return True  : Succeeded to delete all 4 files or user canceled the operation
	 *         False : Errors were detected: not all files were deleted, user canceled or not succeeded with the make writable operation,
	 * 
	 */
	public boolean selectScenarioAndDelete(){
		UIManager.put("FileChooser.readOnly", true);
		String testSourceFolderPath = JSystemProperties.getInstance().getPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER);
		File testSourceDir = new File(testSourceFolderPath);
		JFileChooser fc = new JFileChooser(testSourceFolderPath);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setFileSystemView(new MyFileSystemView(testSourceFolderPath));
		fc.setFileFilter(new XmlFileFilter());
		fc.setDialogTitle(JsystemMapping.getInstance().getScenarioSelectWin());

		if (fc.showDialog(TestRunnerFrame.guiMainFrame, "Delete") != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		String scenarioName = fc.getSelectedFile().getAbsolutePath()
				.substring(testSourceDir.getAbsolutePath().length() + 1);
		try {
			if (scenarioName.endsWith(".xml")) { // Cut the file extension
				scenarioName = scenarioName.substring(0, scenarioName.length() - 4);
			}
			else{
				throw new Exception("For delete scenario you need to choose XML file");
			}
			// should return all 4 files xml and properties from tests and classes directories
			File[] allScenarioFiles = ScenariosManager.getInstance().getScenario(scenarioName).getScenarioFiles();
			int toDelete = JOptionPane.showConfirmDialog(
				    null,
				    "Are you sure you want to delete "+scenarioName+"?",
				    "Delete Scenario",
				    JOptionPane.YES_NO_OPTION);
			if (toDelete == 0){ // yes option
				if (!UnmodifiableFileHandler.getInstance().makeWritable(allScenarioFiles)) {
					// Failed to set file permissions to writable or user canceled
					// operation.
					return false;
				}
				boolean deleteAll = true;
				for (int i=0; i< allScenarioFiles.length; i++ ){
					boolean isOK = allScenarioFiles[i].delete();
					if (!isOK){
						deleteAll =  false;
					}
				}
				if(!deleteAll){
					throw new Exception("Not all files were deleted!");
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public boolean saveList(boolean isNew, Scenario scen) {
		/**
		 * will hold the result from the JFileChooser
		 */
		File path = null;

		ArrayList<File> newNames = new ArrayList<File>();
		File testClassDir = new File(JSystemProperties.getCurrentTestsPath());

		while (true) {

			/**
			 * open the JFileChooser at the parent dir of the current scenario
			 */
			JFileChooser fc = new JFileChooser(ScenariosManager.getInstance().getCurrentScenario().getScenarioFile()
					.getParentFile());
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(false);
			fc.setFileFilter(new XmlFileFilter());
			fc.setFileSystemView(new MyFileSystemView(testClassDir));

			if (isNew) {
				fc.setDialogTitle(JsystemMapping.getInstance().getNewScenarioWin());
			} else {
				fc.setDialogTitle("Save scenario as");
			}

			try {
				if (fc.showDialog(TestRunnerFrame.guiMainFrame, "Save as") != JFileChooser.APPROVE_OPTION) {
					return false;
				} else {
					path = fc.getSelectedFile();
					newNames.add(path);
					if (path.getAbsolutePath().startsWith(testClassDir.getAbsolutePath())) {
						break;
					} else {
						JOptionPane.showMessageDialog(TestRunnerFrame.guiMainFrame,
								"The scenario file should be saved under the tests classes directory",
								"Save scenario location error", JOptionPane.ERROR_MESSAGE);
					}
				}

			} catch (Throwable t) {
				continue;
			}
		}

		String listName = path.getAbsolutePath().substring(testClassDir.getAbsolutePath().length() + 1);

		if (!listName.endsWith(".xml")) {
			listName = listName + ".xml";
		}
		String listNameWithoutPostfix = listName.substring(0, listName.length() - 4);
		Scenario scenario;
		ScenarioHelpers.resetCache();
		ScenariosManager.setDirtyStateEventsSilent(true);
		try {
			if (isNew) {
				try {

					if (ScenarioNameHookManager.getHookClass() != null) {
						scenario = ScenarioNameHookManager.createScenarioWithNameHook(listNameWithoutPostfix);
					} else {
						scenario = ScenariosManager.getInstance().getScenario(listNameWithoutPostfix);
					}
					scenario.update();
					ScenariosManager.getInstance().setCurrentScenario(scenario);
					tree.getSelectionModel().clearSelection();
					statusBar.setMessage(scenario.getName());
					updateEnabledAndDisabledActions(null);
					refreshTree();
				} catch (Exception e) {
					log.log(Level.WARNING, "New scenario creation failed", e);
				}
			} else {
				try {
					HookData data1 = null;
					if (ScenarioNameHookManager.getHookClass() != null) {
						data1 = ScenarioNameHookManager.getNextScenarioData();
						listName = ScenarioNameHookManager.getScenarioNameForHookData(data1, listName);
					}

					File newScenarioFile = new File(testClassDir.getAbsolutePath(), listName);
					if (newScenarioFile.exists()) {
						int res = JOptionPane.showOptionDialog(null, "The scenario file " + newScenarioFile.getName()
								+ " already exists.\n" + "Do you want to override file?", "Scenario already exists",
								JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, ImageCenter.getInstance()
										.getImage(ImageCenter.ICON_INFO), new String[] { "Yes", "No" }, "No");
						if (res == 1) {
							return false;
						}
						// In case the user wants to overwrite existing
						// scenario, we have to make sure that the scenario
						// files are modifiable.
						String newScenarioName = listName;
						if (newScenarioName.toLowerCase().endsWith(".xml")) {
							newScenarioName = newScenarioName.substring(0, newScenarioName.length() - 4);
						}
						if (newScenarioName.startsWith(".")) {
							newScenarioName = newScenarioName.substring(1);
						}
						File[] filesToCheck = new File[4];
						filesToCheck[0] = new File(ScenarioHelpers.getScenarioSrcFile(newScenarioName));
						filesToCheck[1] = new File(ScenarioHelpers.getScenarioSrcPropertiesFile(newScenarioName));
						filesToCheck[2] = new File(ScenarioHelpers.getScenarioPropertiesFile(newScenarioName));
						filesToCheck[3] = new File(ScenarioHelpers.getScenarioFile(newScenarioName));
						if (!UnmodifiableFileHandler.getInstance().makeWritable(filesToCheck)) {
							return false;
						}
					}

					if (ScenarioNameHookManager.getHookClass() != null) {
						ScenarioNameHookManager.saveScenarioAsWithNameHook(scen, listName, data1);
					} else {
						scen.save(listName);
					}
					Scenario s = ScenariosManager.getInstance().getScenario(scen.getName());
					ScenariosManager.getInstance().setCurrentScenario(scen);

					tree.getSelectionModel().clearSelection();
					statusBar.setMessage(s.getName());
					refreshTree();

				} catch (Exception e1) {
					log.log(Level.WARNING, "List save failed", e1);
				}
			}
		} finally {
			ScenariosManager.setDirtyStateEventsSilent(false);
		}
		return true;
	}

	/**
	 * searching testToFind in the tree nodes from aNode
	 * 
	 * @param aNode
	 * @param testToFind
	 * @param wrapper
	 *            {@link IntegerWrapper}
	 * @return
	 */
	private boolean findNode(ScenarioTreeNode aNode, JTest testToFind, IntegerWrapper wrapper) {

		if (aNode.getTest() == testToFind) {
			return true;
		}

		int childCount = aNode.getChildCount();
		String fullUuid = aNode.getTest().getFullUUID();

		// Added in order to resolve bug #288
		if (aNode.getTest() instanceof AntFlowControl) {
			fullUuid = ((AntFlowControl) aNode.getTest()).getFlowFullUUID();
		}

		boolean isExpand = true;
		if (childCount > 0 && !expandedPaths.containsKey(fullUuid)) {
			isExpand = false;
		}
		for (int i = 0; i < childCount; i++) {
			ScenarioTreeNode child = (ScenarioTreeNode) aNode.getChildAt(i);
			if (isExpand) {
				wrapper.value++;
			}
			if (findNode(child, testToFind, wrapper)) {
				return true;
			}
		}

		return false;
	}

	public void refresh() {
		clearExpandSelected(false, true);
		tree.getSelectionModel().clearSelection();
		resetInfoTab();
		Scenario scenario;
		try {
			String sName = ScenariosManager.getInstance().getCurrentScenario().getName();
			scenario = ScenariosManager.getInstance().getScenario(sName);
			ScenariosManager.getInstance().setCurrentScenario(scenario);
			// ScenarioUIUtils.showScenarioErrorDialog(scenario);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fail to reload scenario", e);
			ErrorPanel.showErrorDialog("Problem reloading current Scenario ",
					StringUtils.getStackTrace(ScenariosManager.getInstance().getLastException()), ErrorLevel.Error);
			return;
		}

		refreshTree();

		updateEnabledAndDisabledActions(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addError(junit.framework.Test,
	 * java.lang.Throwable)
	 */
	public void addError(Test test, Throwable t) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#addFailure(junit.framework.Test,
	 * junit.framework.AssertionFailedError)
	 */
	public void addFailure(Test test, AssertionFailedError t) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#endTest(junit.framework.Test)
	 */
	public void endTest(Test test) {
		// ignored
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestListener#startTest(junit.framework.Test)
	 */
	public void startTest(Test test) {
	}

	public void addWarning(Test test) {
	}

	public void startTest(TestInfo testInfo) {
		isRunning = true;
		setEdit(false, false, false, false, false, true);
		JTest currentTest = ScenariosManager.getInstance().getCurrentScenario().getTestByFullId(testInfo.fullUuid);
		// calculate test position on tree
		IntegerWrapper nodeRow = new IntegerWrapper();
		findNode(((ScenarioTreeNode) tree.getModel().getRoot()), currentTest, nodeRow);
		IntegerWrapper fixtureCount = new IntegerWrapper();
		ScenariosManager.getInstance().getCurrentScenario().getFixtureAmountTillTest(currentTest, fixtureCount);
		tree.scrollRowToVisible(nodeRow.value + fixtureCount.value);
		tree.repaint();
	}

	public void endRun() {
		isRunning = false;
	}

	public JTabbedPane getTabbes() {
		return tabbes;
	}

	public void setTabbes(JTabbedPane tabbes) {
		this.tabbes = tabbes;
	}

	/**
	 * any click on any node will activate this function
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if (isRunning) {
			return;
		}
		// ITAI: The value of the node might changed and we would like to update
		// the test description so we firing node changed event for the last
		// selected node.
		if (e.getOldLeadSelectionPath() != null && e.getNewLeadSelectionPath() != null
				&& (e.getOldLeadSelectionPath().getLastPathComponent() instanceof TreeNode)) {
			final TreeNode lastPathNode = (TreeNode) e.getOldLeadSelectionPath().getLastPathComponent();
			model.nodeChanged(lastPathNode);
		}
		updateSelectedInScenarioTree();
		updateEnabledAndDisabledActions(null);
	}

	/**
	 * set the up,down and delete to enable/disable if enable (first param) is
	 * false then all are disabled
	 * 
	 * @param enable
	 *            true if test is in the main root scenario
	 * @param down
	 *            if down is to be enabled
	 * @param up
	 *            if the up is to be enabled
	 */
	private void setEdit(boolean delete, boolean down, boolean up, boolean editScenario, boolean clearScenario,
			boolean code) {
		MoveUpAction.getInstance().setEnabled(up);
		MoveToTopAction.getInstance().setEnabled(up);
		MoveDownAction.getInstance().setEnabled(down);
		MoveToBottomAction.getInstance().setEnabled(down);
		RemoveItemAction.getInstance().setEnabled(delete);
		CopyAction.getInstance().setEnabled(delete);
		CutAction.getInstance().setEnabled(delete);
		PasteAction.getInstance().setEnabled(delete);
		PasteAfterAction.getInstance().setEnabled(delete);
		EditScenarioAction.getInstance().setEnabled(editScenario);
		ClearScenarioAction.getInstance().setEnabled(clearScenario);
		ViewTestCodeAction.getInstance().setEnabled(code);
	}

	/**
	 * reload the tree model, save expanded paths and selected tests
	 */
	public void refreshTree() {
		updateExpandedPaths();
		int[] paths = saveSelection();
		model.reload();
		restoreExpandedState();
		restoreSelections(paths);
		restoreScrollBarPosition();
		pane.repaint();
		checkPlayActionMode();
	}

	private void expandCollapseAllByRoot(boolean expand) {
		TreeUI ui = tree.getUI();
		tree.setUI(null);
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		TreePath parent = new TreePath(root);
		int childCount = 0;
		if ((childCount = root.getChildCount()) >= 0) {
			if (expand) {
				for (int childIndex = 0; childIndex < childCount; childIndex++) {
					TreePath path = parent.pathByAddingChild(root.getChildAt(childIndex));
					expandAllActual(path);
				}
			} else {
				for (int childIndex = 0; childIndex < childCount; childIndex++) {
					TreePath path = parent.pathByAddingChild(root.getChildAt(childIndex));
					collapseAllActual(path);
				}
			}
		}
		tree.setUI(ui);
	}

	public void expandAll() {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		// Traverse tree from root
		expandAll(new TreePath(root));
	}

	public void collapseAll() {
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		collapseAll(new TreePath(root));
	}

	private void collapseAll(TreePath parent) {
		TreeUI ui = tree.getUI();
		tree.setUI(null);
		collapseAllActual(parent);
		tree.setUI(ui);
	}

	private void collapseAllActual(TreePath parent) {
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				collapseAllActual(path);
			}
		}

		// Collapse must be done bottom-up
		tree.collapsePath(parent);

	}

	/**
	 * expands\collapses all nodes from this parent and on
	 * 
	 * @param parent
	 *            the parent to expand from
	 * @param expand
	 *            if True will expand, otherwise collapse
	 */
	private void expandAll(TreePath parent) {
		TreeUI ui = tree.getUI();
		tree.setUI(null);
		expandAllActual(parent);
		tree.setUI(ui);
	}

	private void expandAllActual(TreePath parent) {
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAllActual(path);
			}
		}

		// Expansion must be done bottom-up
		tree.expandPath(parent);
	}

	/**
	 * implement this function for TableModelListener interface.
	 */

	public void mouseClicked(MouseEvent e) {

	}

	/**
	 * The method creating OptionPane that will pop each time the user will
	 * select sub scenario item.
	 * 
	 * @return boolean: User`s choice (whether to work on the sub scenarios or
	 *         not).
	 */
	private boolean showSubScenarioOptionPane() {
		JCheckBox cb = new JCheckBox("remember my decision");

		JLabel label = new JLabel("Changing this scenario may affect other scenarios");

		JLabel separator = new JLabel("Click Yes to approve");

		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(3, 1));

		panel.add(label);

		panel.add(separator);

		panel.add(cb);

		/**
		 * read property from jsystem.properties
		 */
		String editProperty = JSystemProperties.getInstance().getPreference(FrameworkOptions.SUB_SCENARIO_EDIT);

		if (editProperty != null && editProperty.equals("true")) {
			isApprooved = true;
			lastSubScenResult = 0;
		}

		if (!isApprooved) {
			lastSubScenResult = JOptionPane.showConfirmDialog(TestRunner.treeView, panel, "Changing Sub Scenario",
					JOptionPane.YES_NO_OPTION);
		}

		/**
		 * 
		 */
		if (lastSubScenResult == 0) {
			if (cb.isSelected()) {
				isApprooved = true;

				JSystemProperties.getInstance().setPreference(FrameworkOptions.SUB_SCENARIO_EDIT,
						Boolean.toString(isApprooved));
			}

			return true;
		}

		if (lastSubScenResult == 1) {
			if (cb.isSelected()) {
				isApprooved = true;
			}

			return false;
		}

		return false;
	}

	public void mousePressed(MouseEvent e) {

		/**
		 * On double click always jump to the test info tab
		 */
		if (e.getClickCount() == 2) {
			if (tabbes != null) {
				tabbes.setSelectedIndex(2);
			}
		}

		/**
		 * if in run mode ignore mouse press
		 */
		if (isRunning) {
			return;
		}

		int x = e.getX();
		int y = e.getY();

		// save the path for future use
		TreePath clickedPath = tree.getPathForLocation(x, y);
		if (clickedPath == null) {
			return;
		}

		// save the selected node
		currentNode = (ScenarioTreeNode) clickedPath.getLastPathComponent();

		if (currentNode.getNodeLevel() > 2 && !clickedPath.equals(lastClickedPath)) {
			if (!showSubScenarioOptionPane()) {
				return;
			}
		}

		if (e.getButton() == MouseEvent.BUTTON3) { // right mouse button
			if (x >= (checkX * (currentNode.getNodeLevel() / 1.5))) {
				showRightMenu(e);
			}
		} else { // left mouse button
			rightMenuAlreadyOpened = false;
			/**
			 * handle check box pressing
			 */
			boolean checkBoxLocation = isTheMouseOnTheCheckBox(currentNode, x);
			boolean legalComponent = (!currentNode.isJTestContainer() || ScenarioHelpers
					.isScenarioAsTestAndNotRoot(currentNode.getTest()));
			if (legalComponent && checkBoxLocation) {
				restoreSelections(null);

				try {
					handleNodeMap();

				} catch (Exception e1) {
					log.log(Level.WARNING, "Fail to update Scenario after check/uncheck test", e1);
				}
			} else { // regular selection was made

				updateEnabledAndDisabledActions(currentNode);
			}
		}

		saveSelection();
		saveScrollBarPosition();
		lastClickedPath = clickedPath;
	}

	/**
	 * go over all relevant buttons and enable\disable them if needed
	 * 
	 * called after the following actions:
	 * 
	 * 1) Mouse press <br>
	 * 2) Refresh <br>
	 * 3) Delete <br>
	 * 4) Clear <br>
	 * 5) Add Test <br>
	 * 6) Load scenario<br>
	 * 7) Save Scenario<br>
	 * 8) Copy\Cut\Paste <br>
	 * 
	 * @param treeNode
	 *            if not null, then this is the current selected tree node
	 */
	public void updateEnabledAndDisabledActions(ScenarioTreeNode treeNode) {
		if ("false".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT)))
			return;
		Scenario scenario = ScenariosManager.getInstance().getCurrentScenario();
		TestsContainer container;
		if (treeNode == null) {
			container = getContainer();
		} else {
			container = new TestsContainer();
			container.addTest(treeNode.getTest());
		}
		boolean singleScenarioIsSelected = container.getNumOfTests() == 1 && container.hasScenario();
		boolean singleTestSelected = container.getNumOfTests() == 1;
		boolean noTestsSelected = container.getNumOfTests() == 0;
		boolean noRootItemSelected = !container.hasRoot() && container.getNumOfTests() > 0;
		boolean onlyRootScenarioIsSelected = container.hasRoot() && container.getNumOfTests() == 1;
		boolean singleRunnerTestIsSelected = container.getNumOfTests() == 1 && !container.hasScenario()
				&& !container.hasFlowControl();

		boolean antSwitchIsSelected = (container.getNumOfTests() > 0 && container.getTests()[0] instanceof AntSwitch);
		boolean antCaseIsSelected = (container.getNumOfTests() > 0 && container.getTests()[0] instanceof AntSwitchCase);
		boolean antDefaultIsSelected = (container.getNumOfTests() > 0 && container.getTests()[0] instanceof AntSwitchDefault);
		// In the following case we compare to AntIfCondition and not using
		// instanceof because
		// AntIfElseIf is instanceof AntIfCondition and we search only for pure
		// AntIfCondition instances.
		boolean antIfIsSelected = (container.getNumOfTests() > 0 && container.getTests()[0].getClass() == AntIfCondition.class);
		boolean antElseIsSelected = (container.getNumOfTests() > 0 && container.getTests()[0] instanceof AntIfElse);
		boolean antElseIfIsSelected = (container.getNumOfTests() > 0 && container.getTests()[0] instanceof AntIfElseIf);

		/**
		 * Enables copy scenario only if the root scenario is selected.
		 */
		CopyScenarioAction.getInstance().setEnabled(onlyRootScenarioIsSelected);

		/**
		 * Allow remove button unless root is checked and at least 1 item is
		 * selected, and it is not else\switch default
		 */
		RemoveItemAction.getInstance().setEnabled(
				noRootItemSelected && !container.hasElse() && !container.hasSwitchDefault());

		// Enables the edit scenario menu items in case that the container
		// contains tests and not the root scenario
		boolean copyTestsActions = (noRootItemSelected && (container.getNumOfTests() > 0));
		CopyAction.getInstance().setEnabled(copyTestsActions);
		CutAction.getInstance().setEnabled(copyTestsActions);
		PasteAction.getInstance().setEnabled(
				(container.getNumOfTests() == 1) && (clipboardTests != null) && clipboardTests.size() > 0); // Paste
																											// is
																											// allowed
																											// when
																											// the
																											// root
																											// test
																											// is
																											// selected
		PasteAfterAction.getInstance().setEnabled(
				noRootItemSelected && (container.getNumOfTests() == 1) && (clipboardTests != null)
						&& clipboardTests.size() > 0);

		ScenarioRedoAction.getInstance().setEnabled(UserActionManager.isRedoEnabled());

		ScenarioUndoAction.getInstance().setEnabled(UserActionManager.isUndoEnabled());
		/**
		 * Allow clear button when only scenarios are chosen
		 */
		ClearScenarioAction.getInstance().setEnabled(onlyRootScenarioIsSelected);

		/**
		 * Allow View Test Code button when only one RunnerTest is chosen, and
		 * it is not publish/sut change
		 */
		boolean viewCode = singleRunnerTestIsSelected;
		if (viewCode) {
			JTest test = container.getTests()[0];
			if (test instanceof RunnerTest) {
				if (((RunnerTest) test).getTest() instanceof PublishTest) {
					viewCode = false;
				} else if (((RunnerTest) test).getTest() instanceof ChangeSutTest) {
					viewCode = false;
				}

			}
		}
		ViewTestCodeAction.getInstance().setEnabled(viewCode);

		/**
		 * Allow Edit Scenario button when only one scenario is chosen
		 */
		EditScenarioAction.getInstance().setEnabled(singleScenarioIsSelected);

		NextScenarioAction.getInstance().setEnabled(ScenarioNavigationManager.getInstance().canNavitageForward());
		PreviosScenarioAction.getInstance().setEnabled(ScenarioNavigationManager.getInstance().canNavitageBackward());

		/**
		 * Allow moving down and up arrow's
		 */
		MoveDownAction.getInstance().setEnabled(
				scenario.canMoveDown(container) && !container.hasRoot() && !noTestsSelected);
		MoveUpAction.getInstance()
				.setEnabled(scenario.canMoveUp(container) && !container.hasRoot() && !noTestsSelected);
		MoveToBottomAction.getInstance().setEnabled(
				scenario.canMoveToBottom(container) && !container.hasRoot() && !noTestsSelected && !antCaseIsSelected
						&& !antElseIfIsSelected);
		MoveToTopAction.getInstance().setEnabled(
				scenario.canMoveToTop(container) && !container.hasRoot() && !noTestsSelected && !antDefaultIsSelected
						&& !antElseIsSelected);

		/**
		 * Enable case and else statements only under switch and if statements
		 */
		NewSwitchAction.getInstance().setEnabled(noTestsSelected || (singleTestSelected && !antSwitchIsSelected));
		NewSwitchCaseAction.getInstance().setEnabled(singleTestSelected && antSwitchIsSelected);
		NewForLoopAction.getInstance().setEnabled(noTestsSelected || (singleTestSelected && !antSwitchIsSelected));
		NewIfConditionAction.getInstance().setEnabled(noTestsSelected || (singleTestSelected && !antSwitchIsSelected));
		NewElseIfAction.getInstance().setEnabled(singleTestSelected && antIfIsSelected);

		/**
		 * Disable publish/change SUT in case "switch" is selected
		 */
		PublishEventAction.getInstance().setEnabled(noTestsSelected || (singleTestSelected && !antSwitchIsSelected));
		changeSut.setEnabled(noTestsSelected || (singleTestSelected && !antSwitchIsSelected));

	}

	/**
	 * convert a given array of indexes (rows in tree) to an array of updated
	 * TreePath according to the current tree member
	 * 
	 * @param rows
	 *            the rows of the nodes
	 * @return an array of TreePath od the selected rows
	 */
	private TreePath[] rowsToPaths(int[] rows) {
		if (rows == null) {
			return null;
		}
		TreePath[] paths = new TreePath[rows.length];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = tree.getPathForRow(rows[i]);
		}

		return paths;
	}

	/**
	 * handle check / uncheck for Test Node Note: check/uncheck saved in
	 * grandparent, unless does not exist
	 * 
	 * @throws Exception
	 */
	private void handleNodeMap() throws Exception {
		currentNode.setSelected(!currentNode.isSelected());
		ScenarioHelpers.setDirtyFlag();
		tree.repaint();
		checkPlayActionMode();
	}

	/**
	 * check if tests are selected in scenario tree and if true, set the play
	 * button enabled. else set disabled
	 */
	public void checkPlayActionMode() {
		boolean enable = ScenariosManager.getInstance().getCurrentScenario().getEnabledTests().size() != 0;
		PlayAction.getInstance().setEnabled(enable);
	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public ScenarioTreeNode getCurrentNode() {
		return currentNode;
	}

	public void setTestTreeViewSelectionListner(TreeSelectionListener testTreeViewSelectionListner) {
		this.testTreeViewSelectionListner = testTreeViewSelectionListner;
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		expandedPaths.remove(getTreePathUUID(event.getPath()));
		ArrayList<TreePath> paths = new ArrayList<TreePath>();
		getAllPaths(event.getPath(), paths);
		for (TreePath path : paths) {
			String fullUuid = getTreePathUUID(path);
			if (expandedPaths.containsKey(fullUuid)) {
				expandedPaths.remove(fullUuid);
			}
		}
	}

	/**
	 * Add to expandedPaths array the scenario the expanded
	 */
	public void treeExpanded(TreeExpansionEvent event) {
		String fullUuid = getTreePathUUID(event.getPath());
		if (!expandedPaths.containsKey(fullUuid)) {
			expandedPaths.put(fullUuid, event.getPath());
		}
	}

	private String getTreePathUUID(TreePath path) {
		ScenarioTreeNode node = (ScenarioTreeNode) path.getLastPathComponent();
		JTest test = node.getTest();
		String fullUuid = test.getFullUUID();
		if (isTreePathAFlowControlElement(path)) {
			fullUuid = ((AntFlowControl) test).getFlowFullUUID();
		}

		return fullUuid;
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
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub

	}

	/**
	 * JTree class is extended to solve the following problem: When the user
	 * checks (using the mouse) a tree node, swing identify the check as node
	 * selection. As a workaround, after check we reverse back tree selection to
	 * original selection. This results with two calls to
	 * TestInformationTab#valueChanged (if tab is currently selected by the
	 * user) which is a listener to tree path changes. as a result, parameters
	 * panel is drown twice, and in the case of loaded tests this takes time and
	 * slows the system.
	 * 
	 * To prevent the above , I have extended JTree and overridden the process
	 * mouse event method. When an event occurs, I identify the this is a check
	 * event and raise a flag. the TestInformationTab#valueChanged method checks
	 * this flag and skips updates if the flag is up.
	 * 
	 * @author gderazon
	 */
	class OurTree extends JTree {
		private static final long serialVersionUID = 1L;

		public void processMouseEvent(MouseEvent e) {
			boolean checkBoxLocation = false;
			int x = e.getX();
			int y = e.getY();
			TreePath clickedPath = tree.getPathForLocation(x, y);
			if (clickedPath != null) {
				// TODO fixed ticket #186
				ScenarioTreeNode myNode = (ScenarioTreeNode) clickedPath.getLastPathComponent();
				if (myNode != null) {
					checkBoxLocation = isTheMouseOnTheCheckBox(myNode, x);
					// checkBoxLocation = e.getX() < (checkX *
					// (currentNode.getNodeLevel() / 1.5));
					setEventCheckEvent(checkBoxLocation);
					// if no edit is allowed, return without the parent handling.
					if (checkBoxLocation && "false".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT))) 
						return;
				}
			}
			super.processMouseEvent(e);
			setEventCheckEvent(false);
		}
	}

	// see OurTree documentation
	public boolean isEventCheckEvent() {
		return isEventCheckEvent;
	}

	// see OurTree documentation
	public void setEventCheckEvent(boolean isCheck) {
		isEventCheckEvent = isCheck;
	}

	@Override
	public void scenarioChanged(Scenario current, ScenarioChangeType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scenarioDirectoryChanged(File directory) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scenarioDirtyStateChanged(Scenario s, boolean isDirty) {
		Object root = model.getRoot();
		model.nodeChanged((ScenarioTreeNode) root);
	}

	@Override
	public void testParametersChanged(String testIIUUD, Parameter[] oldValues, Parameter[] newValues) {
		// TODO Auto-generated method stub

	}

	public boolean deleteScenario() {
		boolean isOK = selectScenarioAndDelete();
		tree.getSelectionModel().clearSelection();
		testsTreeControler.refreshView();
		testsTreeControler.expandTree();
		return isOK;
	}

	public JTest[] getSelectedTests() {
		return selectedTests;
	}
	

}

class StatusBar extends JToolBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = -586528345974515673L;

	JLabel label;

	JLabel scenarioLabel;

	ImageIcon bgImage;

	/** Creates a new instance of StatusBar */
	public StatusBar(String name, int orientation, ImageIcon bgImage) {
		super(name, orientation);
		label = new JLabel();
		label.setFont(label.getFont().deriveFont(Font.BOLD));
		label.setText("Scenario: ");
		scenarioLabel = new JLabel();
		add(label);
		add(scenarioLabel);
		setMessage("");
		setOpaque(true);
		this.bgImage = bgImage;

		// Fix to allow long name scenarios without
		// locking the divider between the TestsTree and the Tabs (Bug #347)
		setMinimumSize(new Dimension(10, 10));
	}

	public void setMessage(String message) {
		scenarioLabel.setText(message);
	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		Dimension size = this.getSize();

		GradientPaint gradient1 = new GradientPaint(0, 0, new Color(190, 190, 190), size.width, size.height,
				Color.white);

		g2d.setPaint(gradient1);

		g.fillRect(0, 0, size.width, size.height);
	}
	
	
	



}
