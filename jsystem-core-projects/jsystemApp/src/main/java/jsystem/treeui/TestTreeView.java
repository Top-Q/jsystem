/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.fixture.RootFixture;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.StatusManager;
import jsystem.framework.sut.SutFactory;
import jsystem.framework.sut.SutListener;
import jsystem.guiMapping.JsystemMapping;
import jsystem.runner.ErrorLevel;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.actionItems.CheckStatusAction;
import jsystem.treeui.actionItems.ClearScenarioAction;
import jsystem.treeui.actionItems.CopyScenarioAction;
import jsystem.treeui.actionItems.EditSutAction;
import jsystem.treeui.actionItems.ExitAction;
import jsystem.treeui.actionItems.ExportProjectAction;
import jsystem.treeui.actionItems.ImportProjectAction;
import jsystem.treeui.actionItems.InitReportersAction;
import jsystem.treeui.actionItems.NewScenarioAction;
import jsystem.treeui.actionItems.OpenReportsApplicationAction;
import jsystem.treeui.actionItems.PauseAction;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.actionItems.PublishXmlResultAction;
import jsystem.treeui.actionItems.RefreshAction;
import jsystem.treeui.actionItems.SaveFailedSequenceAction;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.actionItems.StopAction;
import jsystem.treeui.actionItems.SwitchProjectAction;
import jsystem.treeui.actionItems.SystemObjectBrowserAction;
import jsystem.treeui.actionItems.ViewLogAction;
import jsystem.treeui.actionItems.ViewProcessedSutAction;
import jsystem.treeui.actionItems.ViewTestCodeAction;
import jsystem.treeui.client.JSystemAgentClientsPool;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.fixtureui.FixturePanel;
import jsystem.treeui.fixtureui.FixtureView;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.interfaces.JSystemTab;
import jsystem.treeui.reporter.ReportersPanel;
import jsystem.treeui.suteditor.planner.SutTreeDialog;
import jsystem.treeui.suteditor.planner.SystemObjectBrowserUtils;
import jsystem.treeui.teststable.ScenarioTreeNode;
import jsystem.treeui.teststable.TestsTableController;
import jsystem.treeui.tree.TestTreePanel;
import jsystem.treeui.tree.TestsTreeController;
import jsystem.treeui.tree.TestsTreeListener;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;
import jsystem.utils.UploadRunner;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;

/**
 * 
 * This is the TestTree view class implementation. This class holds the tree and
 * the report view
 */
public class TestTreeView extends JFrame implements ActionListener, TestsTreeListener, TreeSelectionListener,
		SutListener, KeyListener {
	private static final long serialVersionUID = -1201843524227517726L;

	private static Logger log = Logger.getLogger(TestTreeView.class.getName());

	public static final String SUT_COMBO_NAME = "SUT_COMBO_NAME";

	public static final String REPEAT_LEFT_NAME = "REPEAT_LEFT_NAME";

	/**
	 * View status - used to configure the status of the view
	 */
	public static final int VIEW_IDLE = 0;

	public static final int VIEW_RUNNING = 1;

	public static final int VIEW_PAUSED = 2;

	public static final int VIEW_WAIT_FOR_PAUSE = 3;

	private final int MIN_VALUE = 0;

	private final int MAX_VALUE = 100;

	public TestsTableController tableController;

	public TestInformationTab testInformation;

	private JProgressBar progressBar;

	private JSplitPane splitPane;

	private int lastDeviderLocation;

	private JComboBox sutCombo;

	private MenuBuilder menuBuilder;

	private TestRunner runner;

	private JTabbedPane tabbes;

	FixtureView fixtureView = new FixtureView();

	ProgressPanel progressPanel;

	ProgressController progressController;

	private JCheckBox repeatCheckBox;

	private JCheckBox debugCheckBox;

	private JCheckBox freezeCheckBox;

	private JTextField repeatAmount;

	private JTextField repeatAmountLeft;

	private JLabel repAmountLeft;

	JToolBar toolBar;

	JPanel agentPanel;

	JButton sutPlanner;

	public static TestsTreeController treeController;

	boolean firsIdle = true;

	boolean isPaused = false;

	private JPanel fixture;

	private boolean isStopped = false;

	/**
	 * Creates a view given a contoller
	 * 
	 * @param runner
	 *            TestRunner
	 */
	public TestTreeView(final TestRunner runner) throws Exception {
		super();
		ScenarioUIUtils.checkCurrentScenario();
		setTitle(JsystemAppTitle.getInstance().generateTitle());

		setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));

		menuBuilder = MenuBuilder.getInstance(this);

		this.runner = runner;
		progressPanel = new ProgressPanel();

		tableController = new TestsTableController(this);

		createToolBar();
		progressController = new ProgressController(progressPanel);

		progressController.start();

		testInformation = new TestInformationTab(tableController);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ExitAction.getInstance().exit();
			}
		});

		setJMenuBar(menuBuilder.getMenuBar());

		createProgressBar();
		treeController = new TestsTreeController(this);
		tableController.setTestsTreeControler(treeController);
		treeController.getTree().addKeyListener(this);

		tabbes = SwingUtils.getJTabbedPaneWithBgImage(
				ImageCenter.getInstance().getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG), ImageCenter.getInstance()
						.getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG));

		tableController.setTabbes(tabbes);
		testInformation.setTabbes(tabbes);

		tabbes.setFont(new Font("sansserif", Font.BOLD, 11));
		tabbes.addTab(JsystemMapping.getInstance().getTestTreeTab(), treeController.getTreePanel());
		RunnerEngine[] engines = JSystemAgentClientsPool.getClients(null);
		RunnerEngine[] enginesToLog = new RunnerEngine[engines.length + 1];
		int index = 0;
		enginesToLog[index++] = RunnerEngineManager.getRunnerEngine();
		for (RunnerEngine e : engines) {
			enginesToLog[index++] = e;
		}
		tabbes.addTab("Reporter", ReportersPanel.initPanel(enginesToLog));
		tabbes.addTab("Test Info", testInformation.getTestInformationPanel());

		/**
		 * Fixture Panel handle
		 */
		fixture = fixtureView.initPanel();
		tabbes.addTab(JsystemMapping.getInstance().getFixtureTAB(), fixture);

		// Generic tabs - dynamic class loading of the chosen tabs in JSystem
		// properties Generic_tabs
		// Uses the init & getTabName functions
		String extraTabs = JSystemProperties.getInstance().getPreference(FrameworkOptions.GENERIC_TABS);
		if (extraTabs != null) {
			StringTokenizer st = new StringTokenizer(extraTabs, ";");
			while (st.hasMoreTokens()) {
				String className = st.nextToken();
				try {
					Class<?> tabClass = LoadersManager.getInstance().getLoader().loadClass(className);
					// Get the constructor
					Constructor<?>[] constractors = tabClass.getConstructors();
					JSystemTab tabInstance = (JSystemTab) constractors[0].newInstance(new Object[] {});
					tabInstance.setTestsTableController(tableController);
					tabbes.addTab(tabInstance.getTabName(), tabInstance.init());
				} catch (Exception e) {
					log.log(Level.WARNING, "fail to load tab: " + className, e);
				}
			}
		}

		tabbes.addChangeListener(testInformation);
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());

		jp.add(tableController.getObject(), BorderLayout.CENTER);
		jp.add(progressPanel, BorderLayout.SOUTH);

		UIDefaults uidefs = UIManager.getLookAndFeelDefaults();
		uidefs.put("SplitPane.background", new ColorUIResource(new Color(0x99, 0xaa, 0xbb)));

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jp, tabbes);
		splitPane.setOneTouchExpandable(false);

		getContentPane().add(splitPane, BorderLayout.CENTER);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, (int) (dim.getWidth()), (int) (dim.getHeight() * 0.9));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		lastDeviderLocation = (int) (dim.getWidth() * 0.35);
		splitPane.setDividerLocation(lastDeviderLocation);

		treeController.expandTree();
		setVisible(true);
		// int hight = (int) (tabbes.getHeight() * 0.87);
		// fixture.setDividerLocation(hight);

		registerOnAgentEvents();
		SystemObjectBrowserUtils.startCollectSOs();

		setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
	}

	public void registerOnAgentEvents() {
		RunnerEngineManager.getRunnerEngine().addListener(this);
		RunnerEngineManager.getRunnerEngine().addListener(progressController);
		RunnerEngineManager.getRunnerEngine().addListener(fixtureView);
		RunnerEngineManager.getRunnerEngine().addListener(StatusManager.getExtendedStatusListener());
		RunnerEngineManager.getRunnerEngine().addListener(StatusManager.getRegularStatusListener());
		RunnerEngineManager.getRunnerEngine().addListener(tableController);
		RunnerEngineManager.getRunnerEngine().addListener(new InteractiveReporterDefaultImpl());
	}

	public void init() {
		// ScenarioUIUtils.showScenarioErrorDialog(ScenariosManager.getInstance().getCurrentScenario());
	}

	public class MyComboBoxRenderer extends BasicComboBoxRenderer {

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
				setBackground(Color.white);
				setForeground(Color.black);
			}
			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());
			return this;
		}
	}

	/**
	 * Creates the tool bar
	 */
	private void createToolBar() {

		toolBar = SwingUtils.getJToolBarWithBgImage("main toolbar", JToolBar.HORIZONTAL, ImageCenter.getInstance()
				.getImage(ImageCenter.ICON_TOP_TOOLBAR_BG));

		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		toolBar.add(NewScenarioAction.getInstance());

		JButton saveScenario = toolBar.add(SaveScenarioAction.getInstance());
		saveScenario.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton saveScenarioAs = toolBar.add(CopyScenarioAction.getInstance());
		saveScenarioAs.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton deleteSenario = toolBar.add(ClearScenarioAction.getInstance());
		deleteSenario.setAlignmentX(Component.CENTER_ALIGNMENT);

		JToolBar runToolBar = SwingUtils.getJToolBarWithBgImage("My Toolbar", JToolBar.HORIZONTAL, ImageCenter
				.getInstance().getImage(ImageCenter.ICON_RUN_TOOLBAR_BG));

		runToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 1));
		runToolBar.setFloatable(false);
		runToolBar.setRollover(true);

		runToolBar.add(PlayAction.getInstance());

		runToolBar.add(PauseAction.getInstance());

		// add the stop button
		runToolBar.add(StopAction.getInstance());
		// runToolBar.add(ToggleDebugOptionAction.getInstance());

		debugCheckBox = new JCheckBox("Debug", false);
		debugCheckBox.setToolTipText("Debug Mode");
		debugCheckBox.setOpaque(false);

		runToolBar.add(debugCheckBox);
		String vmParams = JSystemProperties.getInstance().getPreference(FrameworkOptions.TEST_VM_PARMS);
		if (null != vmParams && vmParams.length() > 3) {
			debugCheckBox.setSelected(true);
		}

		debugCheckBox.addActionListener(this);

		toolBar.add(SaveFailedSequenceAction.getInstance());
		toolBar.add(RefreshAction.getInstance());
		toolBar.addSeparator(new Dimension(15, 0));
		toolBar.add(runToolBar);
		toolBar.addSeparator(new Dimension(15, 0));

		freezeCheckBox = new JCheckBox("Freeze on fail", false);
		freezeCheckBox.setToolTipText("Freeze on Fail");
		freezeCheckBox.setOpaque(false);

		freezeCheckBox.addActionListener(this);
		String ff = JSystemProperties.getInstance().getPreference(FrameworkOptions.FREEZE_ON_FAIL);

		if (ff != null && ff.equals("true")) {
			freezeCheckBox.setSelected(true);
		}

		sutCombo = new JComboBox(SutFactory.getInstance().getOptionalSuts());
		sutCombo.setOpaque(false);
		sutCombo.setSelectedIndex(SutFactory.getInstance().getCurrentSutIndex());
		sutCombo.addActionListener(this);
		sutCombo.setPreferredSize(new Dimension(200, 20));
		sutCombo.setRenderer(new MyComboBoxRenderer());
		sutCombo.setName(SUT_COMBO_NAME);
		refreshSUTTooltip();

		toolBar.add(ViewLogAction.getInstance());
		toolBar.addSeparator(new Dimension(10, 0));
		toolBar.add(sutCombo);
		toolBar.add(EditSutAction.getInstance());
		if (JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_READER_CLASS) != null) {
			toolBar.add(ViewProcessedSutAction.getInstance());
		}

		if (!"false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_PLANNER))) {
			sutPlanner = new JButton(ImageCenter.getInstance().getImage(ImageCenter.ICON_SUT_EDIT));
			sutPlanner.setToolTipText("SUT Planner");
			sutPlanner.addActionListener(this);

			toolBar.add(sutPlanner);
		}

		toolBar.addSeparator(new Dimension(10, 0));
		toolBar.add(ExportProjectAction.getInstance());
		toolBar.add(ImportProjectAction.getInstance());
		toolBar.addSeparator(new Dimension(5, 0));
		toolBar.add(SystemObjectBrowserAction.getInstance());
		toolBar.addSeparator(new Dimension(5, 0));
		toolBar.add(SwitchProjectAction.getInstance());
		toolBar.add(OpenReportsApplicationAction.getInstance());

		refreshOpenReportsButton();

		repeatCheckBox = new JCheckBox("Repeat", false);
		repeatCheckBox.setToolTipText("Repeat Scenario");
		repeatCheckBox.setOpaque(false);

		runToolBar.add(repeatCheckBox);
		String rp = JSystemProperties.getInstance().getPreference(FrameworkOptions.REPEAT_ENABLE);
		if (rp != null && rp.equals("true")) {
			repeatCheckBox.setSelected(true);
		}

		repeatCheckBox.addActionListener(this);

		repeatAmount = new JTextField();
		repeatAmount.setColumns(3);
		repeatAmount.setEnabled(false);
		repeatAmount.setToolTipText("Number of Scenario Repeats");

		repeatAmountLeft = new JTextField("0");
		repeatAmountLeft.setColumns(3);
		repeatAmountLeft.setEnabled(false);
		repeatAmountLeft.setToolTipText("Number of Repeats Left");
		repeatAmountLeft.setName(REPEAT_LEFT_NAME);

		repAmountLeft = new JLabel("left");

		runToolBar.add(repeatAmount);
		runToolBar.add(new JLabel("      "));

		runToolBar.add(repAmountLeft);
		runToolBar.add(repeatAmountLeft);
		runToolBar.add(new JLabel("      "));
		runToolBar.add(freezeCheckBox);

		JPanel toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.PAGE_AXIS));
		toolbarPanel.add(toolBar);
		agentPanel = new JPanel();
		agentPanel.setLayout(new BoxLayout(agentPanel, BoxLayout.X_AXIS));
		agentPanel.add(RemoteAgentUIComponents.getToolBar(runner));
		agentPanel.add(new JPanel());
		agentPanel.add(new JPanel());
		toolbarPanel.add(agentPanel);
		agentPanel.setVisible(false);
		getContentPane().add(toolbarPanel, BorderLayout.NORTH);
	}

	public void refreshInternals() {
		((FixturePanel) fixture.getComponent(0)).showFixtureTree();
		sutCombo.setModel(new DefaultComboBoxModel(SutFactory.getInstance().getOptionalSuts()));
		sutCombo.setSelectedIndex(SutFactory.getInstance().getCurrentSutIndex());
		TestTreePanel.currentSelectedTests = 0;
	}

	/**
	 * 
	 */
	public void toggleMainToolBarVisability() {
		toolBar.setVisible(!toolBar.isVisible());
	}

	/**
	 * 
	 */
	public boolean isMainToolbarVisible() {
		return toolBar.isVisible();
	}

	/**
	 * 
	 */
	public void toggleAgentToolBarVisability() {
		agentPanel.setVisible(!agentPanel.isVisible());
	}

	/**
	 * 
	 */
	public boolean isAgentToolbarVisible() {
		return agentPanel.isVisible();
	}

	/**
	 * Create the progress bar
	 */
	private void createProgressBar() {
		progressBar = new JProgressBar(MIN_VALUE, MAX_VALUE);
		progressBar.setBackground(new Color(0xe1, 0xe4, 0xe6));
		getContentPane().add(progressBar, BorderLayout.SOUTH);
		setVisible(false);
	}

	/**
	 * Handles the actions performed on the view
	 * 
	 * @param e
	 */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == null) {
			command = "";
		} else if (e.getSource().equals(sutCombo)) {
			String sutName = (String) sutCombo.getSelectedItem();
			if (sutName != null) {
				if (sutName.equals(SutFactory.CREATE_A_NEW_SUT_FILE)) {
					sutName = createSUTFile();
				}
				changeSut(sutName);
			}
		} else if (e.getSource().equals(freezeCheckBox)) {
			JSystemProperties.getInstance().setPreference(FrameworkOptions.FREEZE_ON_FAIL,
					Boolean.toString(freezeCheckBox.isSelected()));
		} else if (e.getSource().equals(menuBuilder.menuItemAboutVersion)) {
			About.getInstance(this).reload();
		} else if (e.getSource().equals(repeatCheckBox)) {
			repeateScenario();
		} else if (e.getSource().equals(debugCheckBox)) {
			if (debugCheckBox.isSelected()) {
				String debug = "-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=y,suspend=y";

				String vmParams = JSystemProperties.getInstance().getPreference(FrameworkOptions.TEST_VM_PARMS);
				if (vmParams == null || vmParams.length() < 3) {
					vmParams = new String(debug);
				}

				JSystemProperties.getInstance().setPreference(FrameworkOptions.TEST_VM_PARMS, debug);
			} else {
				JSystemProperties.getInstance().removePreference(FrameworkOptions.TEST_VM_PARMS);
			}
		} else if (e.getSource().equals(sutPlanner)) {
			SutTreeDialog panel = new SutTreeDialog("SUT planner");
			SutEditorManager.getInstance().launchEditor(panel);
		}
	}

	private void repeateScenario() {
		if (repeatCheckBox.isSelected()) {
			repeatAmount.setEnabled(true);
			runner.handleEvent(TestRunner.REPEAT_EVENT, Boolean.TRUE);
		} else {
			repeatAmount.setEnabled(false);
			runner.handleEvent(TestRunner.REPEAT_EVENT, Boolean.FALSE);
		}

	}

	public String createSUTFile() {
		File file = null;
		while (true) {
			// create the sut dir if not exists
			File sutDir = SutFactory.getInstance().getSutDirectory();
			if (sutDir != null) {
				sutDir.mkdirs();
			}
			JFileChooser fc = new JFileChooser(sutDir);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileFilter() {

				@Override
				public boolean accept(File f) {
					return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
				}

				@Override
				public String getDescription() {
					return "xml";
				}

			});
			fc.setMultiSelectionEnabled(false);
			fc.setApproveButtonText("Select");
			fc.setDialogTitle("Select/Create SUT to edit");
			fc.showDialog(TestRunnerFrame.guiMainFrame, "Select");
			file = fc.getSelectedFile();
			if (file == null) {
				return null;
			}
			if (!file.getParentFile().equals(SutFactory.getInstance().getSutDirectory())) {
				ErrorPanel.showErrorDialog("SUT selection error",
						"The selected SUT file should be located in the SUT directory:\n"
								+ SutFactory.getInstance().getSutDirectory().getAbsolutePath(), ErrorLevel.Warning);
				continue;
			}
			break;
		}
		Document doc = null;
		if (!file.getName().toLowerCase().endsWith(".xml")) {
			file = new File(file.getAbsolutePath() + ".xml");
		}
		if (file.exists()) {
			try {
				doc = FileUtils.readDocumentFromFile(file);
			} catch (Exception e1) {
				ErrorPanel.showErrorDialog("SUT process error, failed to open file: " + file.getName(), e1,
						ErrorLevel.Warning);
				return file.getName();
			}
		} else {
			try {
				doc = XmlUtils.getDocumentBuilder().newDocument();
				doc.appendChild(doc.createElement("sut"));
				FileUtils.saveDocumentToFile(doc, file);
			} catch (Exception e1) {
				ErrorPanel.showErrorDialog("SUT process error", e1, ErrorLevel.Warning);
				return file.getName();
			}
		}
		SutFactory.resetSutFactory();
		/*
		 * Init the sut combo with new suts
		 */
		sutCombo.setModel(new DefaultComboBoxModel(SutFactory.getInstance().getOptionalSuts()));
		SutFactory.resetSutFactory(file.getName());
		sutCombo.setSelectedIndex(SutFactory.getInstance().getCurrentSutIndex());

		return file.getName();
	}

	private void refreshSUTTooltip() {
		String tooltip = SutFactory.CREATE_A_NEW_SUT_FILE;
		Object selectedItem = sutCombo.getSelectedItem();
		if (selectedItem != null) {
			String selectedItemString = sutCombo.getSelectedItem().toString();
			if (0 != selectedItemString.length())
				tooltip = selectedItemString;
		} else {
			String sutName = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
			if (sutName == null) {

			}
			tooltip = sutName;
		}
		sutCombo.setToolTipText(tooltip);

	}

	public boolean validateUrl(String url) {
		return UploadRunner.validateUrl(url);
	}

	public boolean validateUrl() {
		String urlFromFile = null;
		try {
			urlFromFile = UploadRunner.getReportsApplicationUrl();
			return validateUrl(urlFromFile);
		} catch (Exception e) {
			log.log(Level.FINE, "Failed validating url " + urlFromFile, e);
			return false;
		}
	}

	/**
	 * Called from DbPropertiesListener, and enable the button only if both: 1 -
	 * validateUrl() - ended successfully 2 - checkDBConnection() - ended
	 * successfully
	 */
	public void refreshOpenReportsButton(boolean value) {
		OpenReportsApplicationAction.getInstance().setEnabled(value);
	}

	/**
	 * This method is called from old code, therefore should not change This
	 * method enable the button if validateUrl() - ended successfully. This
	 * method doesnt check if the connection parameters to the database are
	 * correct
	 */
	public void refreshOpenReportsButton() {
		refreshOpenReportsButton(validateUrl());
	}

	public void pause() {
		isPaused = true;
		runner.handleEvent(TestRunner.PAUSE_EVENT, null);
	}

	private void changeSut(String sutName) {
		// TODO: Added by Itai. I need it for the source control plug-in but I
		// am
		// not sure it will not cause side effects.
		ListenerstManager.getInstance().sutChanged(sutName);
		try {
			RunnerEngineManager.getRunnerEngine().changeSut(sutName);
		} catch (Exception e1) {
			log.log(Level.SEVERE, "Fail to load sut", e1);
		}
		refreshSUTTooltip();
	}

	public void refresh() {
		try {
			FixtureManager.getInstance().setCurrentFixture(RootFixture.class.getName());
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Failed to set fixture to RootFixture", e, ErrorLevel.Error);
		}

		LoadersManager.getInstance().dropAll();
		runner.handleEvent(TestRunner.REFRESH_EVENT, null);
		treeController.refreshView();
		treeController.expandTree();
	}

	/**
	 * Updates the progress bar given the new value In order to disable the
	 * prgress bar, provide 0 value
	 * 
	 * @param value
	 *            int
	 */
	public void updateProgress(int value) {

		if (value == 0) {
			progressBar.setVisible(false);
			return;
		}

		progressBar.setVisible(true);
		progressBar.setStringPainted(true);
		progressBar.setMaximum(MAX_VALUE);
		progressBar.setValue(value);
	}

	/**
	 * Sets the view configuration
	 * 
	 * @param viewStatus
	 *            int
	 */
	public void configureView(int viewStatus) {

		switch (viewStatus) {
		case VIEW_IDLE: // Regular view
			StopAction.getInstance().setEnabled(false);
			tableController.checkPlayActionMode();
			PauseAction.getInstance().setEnabled(false);
			RefreshAction.getInstance().setEnabled(true);
			progressController.setRunning(false);
			CheckStatusAction.getInstance().setEnabled(true);
			ReportersPanel.setInitReportsEnable(true);
			tableController.setEnableToolBar(true);
			configureSutStatus(true);
			menuBuilder.setView(VIEW_IDLE);
			tableController.updateEnabledAndDisabledActions(null);
			SwitchProjectAction.getInstance().setEnabled(true);
			ImportProjectAction.getInstance().setEnabled(true);
			PublishXmlResultAction.getInstance().setEnabled(false);
			ViewTestCodeAction.getInstance().setEnabled(true);
			repeatAmount.setEnabled(true);
			repeatCheckBox.setEnabled(true);
			debugCheckBox.setEnabled(true);
			freezeCheckBox.setEnabled(true);
			RemoteAgentUIComponents.checkJSystemPropsAndEnableAgentList(true);
			fixtureView.endFixturring();
			repaint();
			break;
		case VIEW_RUNNING: // During execution
			firsIdle = false;
			StopAction.getInstance().setEnabled(true);
			PlayAction.getInstance().setEnabled(false);
			PauseAction.getInstance().setEnabled(true);
			RefreshAction.getInstance().setEnabled(false);
			CheckStatusAction.getInstance().setEnabled(false);
			ReportersPanel.setInitReportsEnable(false);
			configureSutStatus(false);
			progressController.setRunning(true);
			menuBuilder.setView(VIEW_RUNNING);
			tableController.setEnableToolBar(false);
			SwitchProjectAction.getInstance().setEnabled(false);
			ImportProjectAction.getInstance().setEnabled(false);
			ViewTestCodeAction.getInstance().setEnabled(false);
			PublishXmlResultAction.getInstance().setEnabled(false);
			repeatAmount.setEnabled(false);
			debugCheckBox.setEnabled(false);
			repeatCheckBox.setEnabled(repeatAmount.getText().equals("0"));
			freezeCheckBox.setEnabled(false);
			RemoteAgentUIComponents.checkJSystemPropsAndEnableAgentList(false);
			fixtureView.startFixturring();
			repaint();
			break;
		case VIEW_PAUSED:
			PlayAction.getInstance().setEnabled(true);
			PauseAction.getInstance().setEnabled(false);
			configureSutStatus(true);
			tableController.setEnableToolBar(false);
			RemoteAgentUIComponents.checkJSystemPropsAndEnableAgentList(false);
			fixtureView.startFixturring();
			repaint();
			break;
		case VIEW_WAIT_FOR_PAUSE:
			InitReportersAction.getInstance().setEnabled(false);
			PlayAction.getInstance().setEnabled(false);
			PauseAction.getInstance().setEnabled(false);
			tableController.setEnableToolBar(false);
			configureSutStatus(false);
			RemoteAgentUIComponents.checkJSystemPropsAndEnableAgentList(false);
			fixtureView.startFixturring();
			repaint();
			break;
		default:
			break;
		}
	}

	private void configureSutStatus(boolean enable) {
		if ("false".equalsIgnoreCase(JSystemProperties.getInstance().getPreference(FrameworkOptions.RUNNER_ALOW_EDIT)))
			enable = false;
		sutCombo.setEnabled(enable);
		if (null != sutPlanner) {
			sutPlanner.setEnabled(enable);
		}
	}

	/**
	 * get the number of repeats marked for this scenario
	 * 
	 * @return
	 */
	public int getNumberOfCycles() {
		int repeatAmount = 0;
		String s = getRepeatAmount().getText();
		if (StringUtils.isEmpty(s)) {
			getRepeatAmount().setText("0");
			getRepeatAmountLeft().setText("0");
			return repeatAmount;
		}

		try {
			repeatAmount = Integer.parseInt(s);
		} catch (NumberFormatException ex) {
			getRepeatAmount().setText("0");
		}
		if (repeatAmount < 1) {
			repeatAmount = 0;
			getRepeatAmount().setText("0");
		}
		return repeatAmount;

	}

	public int getNumberOfLeftCycles() {
		int leftAmount = 0;
		String s = getRepeatAmountLeft().getText();
		if (StringUtils.isEmpty(s)) {
			return leftAmount;
		}
		try {
			leftAmount = Integer.parseInt(s);
		} catch (NumberFormatException ex) {
		}
		if (leftAmount < 1) {
			leftAmount = 0;
		}
		return leftAmount;
	}

	public void setNumberOfLeftCycles(int number) {
		getRepeatAmountLeft().setText(Integer.toString(number));
	}

	public void setNumberOfCycles(int number) {
		getRepeatAmount().setText(Integer.toString(number));
	}

	/**
	 * is the repeat checkBox selected?
	 * 
	 * @return
	 */
	public boolean isRepeat() {
		return repeatCheckBox.isSelected();
	}

	public void setRepeat(boolean reapit) {
		repeatCheckBox.setSelected(reapit);
	}

	/**
	 * for jsystemobject without JDialog
	 */
	public void setFreezeOnFail(boolean freeze) {
		freezeCheckBox.setSelected(freeze);
		JSystemProperties.getInstance().setPreference(FrameworkOptions.FREEZE_ON_FAIL,
				Boolean.toString(freezeCheckBox.isSelected()));
	}

	public boolean addTests(Vector<JTest> tests) {
		return tableController.addTests(tests);
	}

	public void setRepAmount(int amount) {
		repeatCheckBox.setSelected(true);
		repeatAmount.setText(String.valueOf(amount));
	}

	private JTextField getRepeatAmount() {
		return repeatAmount;
	}

	private JTextField getRepeatAmountLeft() {
		return repeatAmountLeft;
	}

	public void sutChanged(String sutName) {
		sutCombo.setSelectedItem(sutName);
		refreshSUTTooltip();
	}

	public void valueChanged(TreeSelectionEvent e) {

		JTest[] tests = null;

		int[] paths = tableController.getTree().getSelectionModel().getSelectionRows();

		if (paths != null) {
			tests = new JTest[paths.length];

			if (tests.length > 1) {
				CopyScenarioAction.getInstance().setEnabled(false);
			}

			for (int i = 0; i < paths.length; i++) {
				tests[i] = ((ScenarioTreeNode) tableController.getTree().getPathForRow(paths[i]).getLastPathComponent())
						.getTest();
			}
		}

		for (int i = 0; tests != null && i < tests.length; i++) {
			if (tests[i] instanceof RunnerTest) {
				CopyScenarioAction.getInstance().setEnabled(false);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == (KeyEvent.VK_F5)) {
			RefreshAction.getInstance().actionPerformed(null);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void resetInfoTab() {
		testInformation.resetInformationTab();
	}

	public JComboBox getSutCombo() {
		return sutCombo;
	}

	public TestRunner getRunner() {
		return runner;
	}

	public JPanel getFixture() {
		return fixture;
	}

	public TestsTableController getTableController() {
		return tableController;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	public boolean isStopped() {
		return isStopped;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	public JTabbedPane getTabbes() {
		return tabbes;
	}

	public MenuBuilder getMenuBuilder() {
		return menuBuilder;
	}

	// public PublisherTreePanel getPublishPanel() {
	// return publishPanel;
	// }

}
