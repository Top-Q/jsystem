/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.fixtureui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.FixtureListener;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.TestRunner;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.client.ApplicationRunnerEngineImpl;
import jsystem.treeui.client.RunnerEngineManager;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class FixtureView implements FixtureListener, ExtendTestListener {

	private JFrame fFrame;

	private FixturePanel fixturePanel = null;

	private JButton goToButton = null;

	private JButton failToButton = null;

	private JButton setCurrentButton = null;
	
	private JCheckBox disableCheckBox = null;

	private Scenario previousScenario = null;
	
	private Scenario navigationScenario = null;

	private String failCause = null;

	private boolean isScenarioFail = false;
	
	private static final String NavigateFixtureScenario = "scenarios/internal/NavigateFixture"; 

	public JPanel initPanel() {
		if (fixturePanel == null) {
			fixturePanel = new FixturePanel();
		}

		fixturePanel.showFixtureTree();
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(fixturePanel, BorderLayout.CENTER);
		mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);
//		fPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fixturePanel, createButtonPanel());
//		fPanel.setOneTouchExpandable(false);
//		fPanel.setDividerSize(5);
		return mainPanel;
	}

	public boolean isVisable() {
		if (fFrame != null) {
			return fFrame.isVisible();
		}
		return true;
	}

	protected JFrame createFrame() {
		JFrame frame = new JFrame("JSystem Fixture Manager");
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		ImageIcon icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SMALL_LOGO);
		if (icon != null) {
			frame.setIconImage(icon.getImage());
		}

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				fFrame.dispose();
			}
		});
		return frame;
	}

	protected JPanel createButtonPanel() {
		JPanel bPanel = new JPanel();
		bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));
		goToButton = new JButton("go to ...");
		goToButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkIsConnetedToRemoteAgent()){
					return;
				}
				goTo();
			}
		});
		failToButton = new JButton("fail to ...");
		failToButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkIsConnetedToRemoteAgent()){
					return;
				}
				failTo();
			}
		});
		setCurrentButton = new JButton("set current");
		setCurrentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkIsConnetedToRemoteAgent()){
					return;
				}
				setCurrent();
			}
		});
		disableCheckBox = new JCheckBox("disable");
		setEnableFixturring(!FixtureManager.getInstance().isDisableFixture());
		disableCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FixtureManager.getInstance().setDisableFixture(((JCheckBox) e.getSource()).isSelected());
				setEnableFixturring(!FixtureManager.getInstance().isDisableFixture());
			}
		});

		bPanel.add(goToButton);
		bPanel.add(failToButton);
		bPanel.add(setCurrentButton);
		bPanel.add(disableCheckBox);

		bPanel.setBackground(new Color(0xf6, 0xf6, 0xf6));

		return bPanel;
	}

	public void aboutToChangeTo(Fixture fixture) {
		// not used
	}

	public void fixtureChanged(Fixture fixture) {
		if (isVisable()) {
			fixturePanel.showFixtureTree();
		}
		fixturePanel.repaint();
	}

	private void setEnableFixturring(boolean status) {
		disableCheckBox.setSelected(!status);
		goToButton.setEnabled(status);
		failToButton.setEnabled(status);
		setCurrentButton.setEnabled(status);
	}

	public void startFixturring() {
		if (isVisable()) {
			goToButton.setEnabled(false);
			failToButton.setEnabled(false);
			setCurrentButton.setEnabled(false);
			disableCheckBox.setEnabled(false);
		}
	}

	public void endFixturring() {
		if (isVisable()) {
			goToButton.setEnabled(true);
			failToButton.setEnabled(true);
			setCurrentButton.setEnabled(true);
			disableCheckBox.setEnabled(true);
		}
	}
	
	private void createAndExecuteNavigationScenario(RunnerTest rt, Properties properties) throws Exception{
		SaveScenarioAction.getInstance().saveCurrentScenarioWithConfirmation();
		previousScenario = ScenariosManager.getInstance().getCurrentScenario();
		navigationScenario = ScenariosManager.getInstance().getScenario(NavigateFixtureScenario);
//		ScenariosManager.getInstance().setCurrentScenario(navigationScenario);
		navigationScenario.cleanAll();
		navigationScenario.addTest(rt);
		ScenariosManager.getInstance().setCurrentScenario(navigationScenario);
		String fullUUID = rt.getFullUUID();
		for (Object key : properties.keySet()){
			ScenarioHelpers.setTestProperty(fullUUID, NavigateFixtureScenario, key.toString(), properties.get(key) + "", false);
		}
		SaveScenarioAction.getInstance().saveCurrentScenario();
		TestRunner.treeView.tableController.refresh();
		RunnerEngineManager.getRunnerEngine().addListener(this);
		PlayAction.getInstance().run();
	}

	protected void goTo() {
		final Fixture fixture = fixturePanel.getSelectedFixture();
		if (fixture == null) {
			return;
		}

		try {
			/*
			 * When the user press the go to button a new scenario will be
			 * created A test that will case navigation will be add to the
			 * scenario. The scenario then executed.
			 */
			RunnerTest rt = new RunnerTest(FixtureNavigation.class.getName(), "testGoToFixture");
			rt.initTestProperties();
			Properties p = new Properties();
			p.setProperty("GoFixture", fixture.getClass().getCanonicalName());
			rt.setProperties(p);
			rt.loadParametersAndValuesAndUpdateTestClass();
			createAndExecuteNavigationScenario(rt, p);
			
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Fail to run navigation scenario", e, ErrorLevel.Error);
		}

	}

	protected void failTo() {
		final Fixture fixture = fixturePanel.getSelectedFixture();
		if (fixture == null) {
			return;
		}
		try {
			/*
			 * When the user press the failTo button a new scenario will be
			 * created A test that will case navigation will be add to the
			 * scenario. The scenario then executed.
			 */
			RunnerTest rt = new RunnerTest(FixtureNavigation.class.getName(), "testFailToFixture");
			rt.initTestProperties();
			Properties p = new Properties();
			p.setProperty("FailToFixture", fixture.getClass().getName());
			p.setProperty("GoFixture", FixtureManager.getInstance().getCurrentFixture());
			rt.setProperties(p);
			rt.loadParametersAndValues();
			rt.setTestClassParameters();
			createAndExecuteNavigationScenario(rt, p);
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Fail to run navigation scenario", e, ErrorLevel.Error);
		}

	}

	protected void setCurrent() {
		final Fixture fixture = fixturePanel.getSelectedFixture();
		if (fixture == null) {
			return;
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					FixtureManager.getInstance().setCurrentFixture(fixture.getName());
				} catch (Exception e) {
					ErrorPanel.showErrorDialog("Fail to set current fixture", e, ErrorLevel.Error);
				}
			}
		});
	}

	public void addWarning(Test test) {
		// not used
	}

	public void endRun() {
		/*
		 * If the scenario fail will not delete it if the scenario pass will
		 * delete it and move to the previous scenario.
		 */
		if (previousScenario == null) {
			return;
		}
		try {
			TestRunner.treeView.tableController.clearScenario(false);
			ScenariosManager.getInstance().setCurrentScenario(previousScenario);
			TestRunner.treeView.tableController.refresh();
			if (isScenarioFail) {
				ErrorPanel.showErrorDialog("Scenario navigation failed", failCause, ErrorLevel.Warning);
				return;
			}
		} finally {
			isScenarioFail = false;
			previousScenario = null;
			failCause = null;
		}

	}

	public void startTest(TestInfo testInfo) {
		// not used
	}

	public void addError(Test test, Throwable t) {
		isScenarioFail = true;
		if (t != null) {
			failCause = t.getMessage();
		}
	}

	public void addFailure(Test test, AssertionFailedError t) {
		isScenarioFail = true;
		if (t != null) {
			failCause = t.getMessage();
		}
	}

	public void endTest(Test test) {
		// not used
	}

	public void startTest(Test test) {
		// not used
	}

	/**
	 * Checks the current engine type if it is {@link ApplicationRunnerEngineImpl}
	 * it means that the engine is embedded in the runner and we can navigate
	 */
	private boolean checkIsConnetedToRemoteAgent(){
		if (RunnerEngineManager.getRunnerEngine() instanceof ApplicationRunnerEngineImpl){
			return false;
		}
		JOptionPane.showOptionDialog(null,
				"Manual navigation can not be activated when JRunner is connected to a remote agent","Manual Fixtures Navigation", 
   			  JOptionPane.OK_OPTION,JOptionPane.INFORMATION_MESSAGE, ImageCenter.getInstance().getImage(ImageCenter.ICON_INFO), new String[]{"Close"}, "Close");

		return true;
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

}
