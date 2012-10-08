/*
 * Created on Dec 14, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreePath;

import jsystem.extensions.report.html.HtmlCodeWriter;
import jsystem.framework.scenario.DistributedExecutionHelper;
import jsystem.framework.scenario.DistributedExecutionParameter;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.MultipleScenarioOps;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterUtils;
import jsystem.framework.scenario.PresentationDefinitions;
import jsystem.framework.scenario.RunnerFixture;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioParameter;
import jsystem.framework.scenario.UIHandler;
import jsystem.framework.scenario.ValidationError;
import jsystem.framework.scenario.ValidationError.Originator;
import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.params.ParametersPanel;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.teststable.TestsTableController;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;
import junit.framework.SystemTest;

/**
 * @author guy.arieli
 * 
 */
public class TestInformationTab implements TreeSelectionListener, FocusListener, ParameterListener, ActionListener,
		ChangeListener {
	HTMLJavaDocView testDocumentation;

	JTextArea testUserDocumentation;

	JLabel runningTime;

	JLabel fixture;

	JLabel failToFixture;

	JLabel classLabel;

	JLabel returnParams;

	JPanel pane;

	JTree tableTree;

	JTabbedPane tabbes;

	JTabbedPane docTabs;

	ParametersPanel ppanel;

	JTest currentTest = null;

	JButton applyButton;

	private String userDoc;

	TestsTableController testTableController;

	private UserDocumentation ud;

	private static Logger log = Logger.getLogger(TestInformationTab.class.getName());

	private String currentSectionName = "";

	private ScenarioTreeNode node;

	public TestInformationTab(TestsTableController testTableController) {
		this.testTableController = testTableController;
		testTableController.getTree().getSelectionModel().addTreeSelectionListener(this);
		testTableController.getTree().addFocusListener(this);
		ud = new UserDocumentation(testTableController);
		ud.setBackground(new Color(0xf6, 0xf6, 0xf6));
	}

	public JPanel getTestInformationPanel() {
		pane = new JPanel();

		pane.setLayout(new BorderLayout());
		pane.setPreferredSize(new Dimension(pane.getWidth(), 400));

		JPanel docPane = new JPanel();
		docPane.setLayout(new BorderLayout());

		testDocumentation = new HTMLJavaDocView(new Color(0xf6, 0xf6, 0xf6));
		testDocumentation.setEditable(false);

		JScrollPane sp = new JScrollPane(testDocumentation);
		docPane.setSize(new Dimension(pane.getWidth(), 100));
		docPane.add(sp, BorderLayout.CENTER);

		JPanel testInfoPane = new JPanel();
		testInfoPane.setLayout(new GridLayout(5, 1));
		testInfoPane.setBackground(new Color(0xf6, 0xf6, 0xf6));

		classLabel = new JLabel();
		testInfoPane.add(classLabel);
		fixture = new JLabel();
		testInfoPane.add(fixture);
		failToFixture = new JLabel();
		testInfoPane.add(failToFixture);

		returnParams = new JLabel();
		testInfoPane.add(returnParams);

		runningTime = new JLabel();
		testInfoPane.add(runningTime);

		ppanel = new ParametersPanel(this, testTableController);

		pane.add(testInfoPane, BorderLayout.NORTH);

		docTabs = SwingUtils.getJTabbedPaneWithBgImage(
				ImageCenter.getInstance().getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG), ImageCenter.getInstance()
						.getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG));

		docPane.setBackground(new Color(0xf6, 0xf6, 0xf6));
		docTabs.add(docPane, "Test Documentation");
		docTabs.add(ud, "User Test Documentation");

		UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
		uidefs.put("SplitPane.background", new ColorUIResource(new Color(0x99, 0xaa, 0xbb)));

		JSplitPane spp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, docTabs, ppanel);
		spp.setDividerLocation(150);

		pane.add(spp, BorderLayout.CENTER);
		return pane;
	}

	/**
	 * used when test has changed and a new information has to be displayed
	 * 
	 * @param jtest
	 *            the new test to display
	 * @param applyJTestContainer
	 * @param updateParameterChange
	 * 
	 */
	public void setCurrentTest(JTest jtest, boolean applyJTestContainer, boolean updateParameterChange) {
		// save previous test settings
		updateCurrentTestUIDefinition();
		if (updateParameterChange) {
			parameterChanged(applyJTestContainer);
		}

		if (!StringUtils.isEmpty(ppanel.getActiveSectionName())) {
			currentSectionName = ppanel.getActiveSectionName();
		}

		Parameter[] clonedParameters = null;

		// load new test and update ui with new test
		if (jtest != null) {
			jtest.loadParametersAndValues();
			clonedParameters = ParameterUtils.clone(jtest.getVisibleParamters(ppanel.recursiveRegulerParameter
					.isSelected()));
			if (jtest instanceof UIHandler) {
				((UIHandler) jtest).handleUIEvent(clonedParameters);
				if (jtest instanceof RunnerTest) {
					ValidationError[] errors = ((RunnerTest) jtest).validate(clonedParameters);
					ValidationError.clearValidatorsWithOriginator(jtest.getValidationErrors(), Originator.TEST);
					if (errors != null) {
						for (ValidationError error : errors) {
							error.setOriginator(Originator.TEST);
						}
						jtest.getValidationErrors().addAll(Arrays.asList(errors));
					}
				}

			}
		}

		synchronized (TestInformationTab.class) {
			currentTest = jtest;
		}
		ud.setTest(jtest);
		setTextDocumentation(null);
		setName("", "");
		setFailToFixture("");
		setFixtureName("");
		setRunningTime(-1);

		if (jtest == null) {
			ud.resetText("", false);
			ud.setTest(null);
			userDoc = "";
			docTabs.setSelectedIndex(0);
			ppanel.reset();
			ppanel.repaint();
			pane.repaint();
			return;
		}

		/**
		 * if a test has a user documentation then set it as default (1)
		 * otherwise set the test documentation as default (0)
		 */
		userDoc = jtest.getDocumentation();
		ud.setEnabled(true);
		if (!StringUtils.isEmpty(userDoc)) {
			docTabs.setSelectedIndex(1);
		} else {
			docTabs.setSelectedIndex(0);
		}

		ud.resetText(userDoc, true);

		ScenarioParameter[] scenarioParameters = null;
		ppanel.setIsJTestContainer((jtest instanceof JTestContainer));
		if (jtest instanceof RunnerTest) {
			RunnerTest test = (RunnerTest) jtest;
			String className = test.getClassName();
			String methodName = test.getMethodName();
			setName(className, methodName);
			updateUD();
			String doc = null;
			try {
				doc = HtmlCodeWriter.getInstance().getMethodJavaDoc(className, methodName);
			} catch (Exception e1) {
				log.log(Level.WARNING, "Fail to read javadoc", e1);
			}
			if (StringUtils.isEmpty(doc)) {
				doc = test.getDocumentation();
			}
			if (doc != null) {
				setTextDocumentation(doc);
			}
			String[] retParams = test.getReturnParameters();
			if (retParams == null) {
				setReturnParams("");
			} else {
				setReturnParams(StringUtils.objectArrayToString(",", (Object[]) retParams));
			}
			if (test.getTest() instanceof SystemTest) {
				SystemTest sTest = (SystemTest) test.getTest();
				setFixtureName(sTest.getFixture().getName());
				Class<?> ff = sTest.getTearDownFixture();
				if (ff != null) {
					setFailToFixture(ff.getName());
				} else {
					setFailToFixture("not set");
				}
			}
			setRunningTime(ProgressController.getTestTime(className, methodName));

		} else if (jtest instanceof Scenario) {
			updateUD();
			Scenario scenario = (Scenario) jtest;
			setScenarioName(scenario.getName());
			scenarioParameters = scenario.getScenarioParameters(true, ppanel.recursiveReference.isSelected());
		} else if (jtest instanceof AntFlowControl) {
			docTabs.remove(ud);
		}
		DistributedExecutionParameter[] hostsParameters = new DistributedExecutionParameter[0];
		try {
			if ((jtest instanceof RunnerTest || jtest instanceof Scenario) // hosts
																			// tab
																			// should
																			// be
																			// shown
																			// only
																			// for
																			// scenarios
																			// and
																			// tests.
					&& ((JSystemAgentClientsPool.getClients(null).length > 0) || DistributedExecutionHelper
							.isAssigned(jtest))) { // show agents tab if runner
													// is connected to agents or
													// test is associated to an
													// agent
				hostsParameters = jtest.getDistributedExecutionParameters();
				if (DistributedExecutionHelper.ancestorIsAssignedWithHosts(jtest)) {
					ParameterUtils.setEnabled(hostsParameters, false);
				}
				if (jtest.getParent() == null) {
					ParameterUtils.setVisible(hostsParameters, false);
				}
				if (jtest instanceof RunnerFixture) {
					ParameterUtils.setVisible(hostsParameters, false);
				}
			}
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Failure loading JSystem hosts", e, ErrorLevel.Error);
		}
		if (jtest instanceof RunnerTest) {
			ppanel.setSectionOrder(((RunnerTest) jtest).getSectionOrder());
		}
		PresentationDefinitions sort = jtest.getPresentationObject();
		if (sort != null) {
			ppanel.changeVals(sort);
		} else {
			sort = new PresentationDefinitions();
			jtest.setPresentationObject(sort);
		}
		ppanel.setParameters(clonedParameters, scenarioParameters, hostsParameters);
		ppanel.setActiveTab(currentSectionName);
		testTableController.getTree().requestFocusInWindow();
	}

	private void handleWithMandatoryParameters(Parameter[] clonedParameters) {
		for (Parameter currentParameter : clonedParameters) {
			if (currentParameter.isMandatory()
					&& (currentParameter.getValue() == null || currentParameter.getValue().toString().isEmpty())) {
				ValidationError error = new ValidationError();
				error.setOriginator(Originator.TEST);
				error.setTitle("Parameter " + currentParameter.getName() + " is mandatory");
				currentTest.addValidationError(error);
			}
		}
	}

	private void updateUD() {
		// TODO Fixed Ticket #158
		boolean isMissing = true;
		for (Component comp : docTabs.getComponents()) {
			if (comp instanceof UserDocumentation) {
				isMissing = false;
				break;
			}
		}
		if (isMissing) {
			docTabs.add(ud, "User Test Documentation");
		}
	}

	public void updateCurrentTestUIDefinition() {
		if (currentTest == null) {
			return;
		}
		PresentationDefinitions sort = currentTest.getPresentationObject();
		sort.saveDefinitions(ppanel.getSortSection(), ppanel.getSortHeader(), ppanel.getParametersOrder(),
				ppanel.getActiveTab(), ppanel.getHeaderRatio());
	}

	/**
	 * resets the information tab and sets currentTest to null
	 * 
	 */
	public void resetInformationTab() {
		setCurrentTest(null, false, true);
	}

	private void setTextDocumentation(String doc) {
		if (doc == null) {
			doc = "To set the documentation javadoc the test source";
		}
		testDocumentation.setContent(doc);
	}

	private void setRunningTime(long runningTime) {
		if (runningTime >= 0) {
			this.runningTime.setText(bold("Last run time: " + runningTime / 1000 + " Sec."));
		} else {
			this.runningTime.setText(bold("Last run time: unknown"));
		}
	}

	private void setFixtureName(String name) {
		fixture.setText(bold("Fixture: " + name));
	}

	private void setFailToFixture(String name) {
		failToFixture.setText(bold("Fail to fixture: " + name));
	}

	private void setReturnParams(String name) {
		returnParams.setText(bold("Return parameters: " + name));
	}

	private void setName(String className, String methodName) {
		classLabel.setText(bold("Name: " + className + "-" + methodName));
	}

	private void setScenarioName(String name) {
		classLabel.setText(bold("Name: " + name));
	}

	private static String bold(String in) {
		return "<html><B>" + in + "</B></html>";
	}

	public void setTabbes(JTabbedPane tabbes) {
		this.tabbes = tabbes;
	}

	/**
	 * 
	 */
	public void parameterChanged(boolean isApplyForScenario) {
		/**
		 * synchronized to prevent null pointer exception that can be created if
		 * currentTest changes in the middle
		 */
		synchronized (TestInformationTab.class) {
			if (currentTest == null) {
				return;
			}
			ppanel.stopEditing();
			boolean uiHandlerWasActivated = false;
			try {
				// first update the scenario reference parameters
				ScenarioParameter[] scenarioParameters = ppanel.getScenarioParameters();
				if (currentTest instanceof Scenario && ParameterUtils.isDirty(scenarioParameters)) {// &&
				// (!ppanel.recursiveReference.isSelected() ||
				// isApplyForScenario)){
					((Scenario) currentTest).setScenarioParameters(scenarioParameters, isApplyForScenario);
				}

				// then update hosts
				DistributedExecutionParameter[] hostsParameters = ppanel.getHostParameters();
				if (ParameterUtils.isDirty(hostsParameters)) {
					currentTest.setDistributedExecutionParameters(hostsParameters);
				}

				// don't update scenario test parameters if apply wasn't pressed
				if (currentTest instanceof Scenario && !isApplyForScenario) {
					return;
				}
				ppanel.applyJTestContainer.setEnabled(false);

				if (currentTest instanceof UIHandler) {
					Parameter[] parameterArray = ppanel.getTestParameters();
					uiHandlerWasActivated = ((UIHandler) currentTest).handleUIEvent(parameterArray);
					if (currentTest instanceof RunnerTest) {
						ValidationError.clearValidatorsWithOriginator(currentTest.getValidationErrors(),
								Originator.TEST);
						handleWithMandatoryParameters(parameterArray);
						ValidationError[] errors = ((RunnerTest) currentTest).validate(parameterArray);
						if (errors != null) {
							for (ValidationError error : errors) {
								error.setOriginator(Originator.TEST);
							}
							currentTest.getValidationErrors().addAll(Arrays.asList(errors));
						}
					}
				}
				if (!ParameterUtils.isDirty(ppanel.getAllParameters())) {
					return;
				}
				updateCurrentTestUIDefinition();
				MultipleScenarioOps.updateTest(currentTest, ppanel.getTestParameters(),
						ppanel.recursiveRegulerParameter.isSelected());
				// Reset the tree, since Ant Flow Control's comment might have
				// changed
				this.testTableController.fireCurrentNodeChange();

			} catch (Exception e1) {
				ErrorPanel
						.showErrorDialog("Failed updating scenario.", StringUtils.getStackTrace(e1), ErrorLevel.Error);
			}

			if (uiHandlerWasActivated) {
				ppanel.buildTabs();
			}
		}
	}

	/**
	 * implementation of ChangeListener. Invoked when tab selection (Test Tree,
	 * Reporter, Test Info ...) is changed. In case the user selects tab 2 (Test
	 * Info) the test info panel is refreshed.
	 */
	public void stateChanged(ChangeEvent e) {
		TreePath treePath = testTableController.getTree().getSelectionPath();

		if (treePath == null || treePath.getLastPathComponent() == null) {
			return;
		}

		node = (ScenarioTreeNode) treePath.getLastPathComponent();

		if (testTableController.getTabbes().getSelectedIndex() == 2) {
			setCurrentTest(node.getTest(), false, true);
		}
	}

	/**
	 * Invoked when the user changes selection in the scenario tree. In case the
	 * selected tab is 2 (Test Info) the test info panel is refreshed.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if (testTableController.isEventCheckEvent()) {
			return;
		}
		TreePath[] paths = e.getPaths();
		if (paths == null) {
			return;
		}
		node = (ScenarioTreeNode) e.getPath().getLastPathComponent();
		if (testTableController.getTabbes().getSelectedIndex() != 2) {
			return;
		}
		/**
		 * not allowed for scenarios, only for tests
		 */
		if (node.getTest() != currentTest) {
			// We can gain major performance improvement when traveling between
			// BB if we change the last parameter the false. The problem is that
			// we will lose support for the mandatory parameter feature and when
			// leaving parameter of BB when it is in focus, the value will not
			// be saved.
			//TODO: Change the last parameter to false.
			setCurrentTest(node.getTest(), false, true);
		}
	}

	public void focusGained(FocusEvent e) {
	}

	/**
	 * 
	 */
	public void focusLost(FocusEvent e) {
		TreePath treePath = testTableController.getTree().getSelectionPath();
		if (treePath == null) {
			/**
			 * not allowed for scenarios, only for tests
			 */
			setCurrentTest(null, false, true);
		} else {
			ScenarioTreeNode node = (ScenarioTreeNode) treePath.getLastPathComponent();
			if (node == null) {
				/**
				 * not allowed for scenarios, only for tests
				 */
				setCurrentTest(null, false, true);
			}
		}
	}

	public TestsTableController getTestTableController() {
		return testTableController;
	}

	/**
	 * serves the applyJTestContainer button
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source.equals(ppanel.applyJTestContainer)) {
			boolean applyRecursively = ppanel.recursiveRegulerParameter.isSelected();
			boolean applyChanges = showapplyJTestContainerDialog(applyRecursively);
			if (!applyChanges) {
				return;
			}
			if (node.getTest() instanceof JTestContainer) {
				setCurrentTest(node.getTest(), true, true);
			}
		} else if (source.equals(ppanel.recursiveReference) || source.equals(ppanel.recursiveRegulerParameter)) {
			ppanel.resetDirty = false;
			setCurrentTest(node.getTest(), false, true);
		}
	}

	private boolean showapplyJTestContainerDialog(boolean recursively) {
		StringBuffer buffer = new StringBuffer();
		if (recursively) {
			buffer.append("The following parameters will be updated in ALL sub tests:");
		} else {
			buffer.append("The following parameters will be updated in ROOT tests only:");
		}
		buffer.append(System.getProperty("line.separator"));
		buffer.append(System.getProperty("line.separator"));
		Parameter[] params = ppanel.getAllParameters();
		String lineSeperator = System.getProperty("line.separator");
		boolean scenarioDirty = false;
		for (Parameter p : params) {
			if (p.isDirty()) {
				if (p instanceof ScenarioParameter) {
					scenarioDirty = true;
				}
				buffer.append(p.getName() + " new value: " + p.getValue() + lineSeperator);
			}
		}

		if (ppanel.recursiveReference.isSelected() && scenarioDirty) {
			if (JOptionPane.showConfirmDialog(ppanel,
					"NOTICE: Reference parameters will be changed in All sub-scenarios recursively. continue?",
					JsystemMapping.getInstance().getRecursiveDialogTitle(), JOptionPane.YES_NO_OPTION) != 0) {
				return false;
			}
		}
		TextArea area = new TextArea();
		area.setEditable(false);
		area.setEnabled(true);
		area.setText(buffer.toString());

		int res = JOptionPane.showConfirmDialog(ppanel, area, "Apply Scenario Parameters", JOptionPane.YES_NO_OPTION);
		return res == 0;
	}

	/**
	 * refreshes the information tab<br>
	 * refreshes the last test, without updating the parameters
	 */
	public void refresh() {
		setCurrentTest(currentTest, false, false);
	}
}