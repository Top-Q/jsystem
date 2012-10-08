/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jsystem.framework.TestRunnerFrame;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.runner.SOCheckStatus;
import jsystem.treeui.actionItems.PlayAction;
import jsystem.treeui.images.ImageCenter;
import junit.framework.AssertionFailedError;
import junit.framework.Test;

public class SystemObjectCheckWindow implements ExtendTestListener{

	private static SystemObjectCheckWindow ref;

	SysObjTableDialog tableDialog;

	private SystemObjectCheckWindow() {
		// singleton
	}

	/**
	 * singletone mechanizem.
	 */
	public static SystemObjectCheckWindow getInstance() {
		if (ref == null) {
			ref = new SystemObjectCheckWindow();
		}
		return ref;
	}

	public void setSysObjStatus(String oName, SOCheckStatus status, String errString) {
//		synchronized (statusMap) {
//			statusMap.put(oName, new OStatus(status, errString));
			tableDialog.model.setValueAt(getStatusIcon(status), getSystemObjectIndex(oName), 1);
			tableDialog.model.setValueAt(getStatusName(status), getSystemObjectIndex(oName), 2);
			tableDialog.model.setValueAt(errString, getSystemObjectIndex(oName), 3);
//		}
	}

	/**
	 * Struct for System Object status entry.
	 * 
	 * @author Uri.Koaz
	 */
	class OStatus {
		public OStatus(SOCheckStatus status, String errString) {
			this.status = status;
			this.errString = errString;
		}

		public SOCheckStatus status;

		public String errString = null;
	}
	Scenario previosScenario = null;
	/**
	 * Activates check function for every System Object as a new Thread. After
	 * getting the desired information, print it into the table.
	 */
	public void getSysObjStatus() throws Exception{
		ListenerstManager.getInstance().addListener(this);
		/*
		 * When the user press the check so button a new scenario will be
		 * created A test that will check the so will be add to the
		 * scenario. The scenario then executed.
		 */
		previosScenario = ScenariosManager.getInstance().getCurrentScenario();
		Scenario scenario = ScenariosManager.getInstance().getScenario("CheckSystemObjects");
		scenario.cleanAll();
		RunnerTest rt = new RunnerTest(CheckSystemObjectTest.class.getName(), "testSystemObjects");
		scenario.addTest(rt);
		ScenariosManager.getInstance().setCurrentScenario(scenario);
		TestRunner.treeView.tableController.refresh();
		PlayAction.getInstance().run();
	}

	/**
	 * checking if the Dialog is already on, if not, show it.
	 */
	public void show() {
		if (tableDialog != null) {
			tableDialog.dispose();
		}
		tableDialog = new SysObjTableDialog();

		try {
			getSysObjStatus();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableDialog.showFrame();
	}

	/**
	 * start a new Window Thread.
	 */
	public void showWindow() {
		new Thread(){
			public void run(){
				show();
			}
		}.start();
	}

	/**
	 * The actual Dialog structure.
	 */
	class SysObjTableDialog extends JDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4724213632673477451L;

		JTable table;

		JPanel mainPanel;

		Object[][] data;

		SysObjTableModel model;

		public SysObjTableDialog() {
			super(TestRunnerFrame.guiMainFrame);
			setTitle("System Objects Check Status");
			setResizable(false);
			Vector<String> sysObjNames = SystemManagerImpl.getAllObjects(true);
			data = new Object[sysObjNames.size()][2];

			table = new JTable();
			model = new SysObjTableModel();
			table.setModel(model);

			// "block" the table from writing
			table.setEnabled(false);

			TableColumn column = table.getColumnModel().getColumn(0);
			column.setPreferredWidth(70);

			column = table.getColumnModel().getColumn(1);
			column.setPreferredWidth(10);

			column = table.getColumnModel().getColumn(2);
			column.setPreferredWidth(60);

			column = table.getColumnModel().getColumn(3);
			column.setPreferredWidth(100);

			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());

			JScrollPane tableScroll = new JScrollPane(table);
			GridBagConstraints tableConstraints = new GridBagConstraints();
			tableConstraints.gridx = 0;
			tableConstraints.gridwidth = 2;
			tableConstraints.fill = GridBagConstraints.HORIZONTAL;

			setBounds(250, 100, 0, 0);

			for (int i = 0; i < sysObjNames.size(); i++) {
				data[i][0] = sysObjNames.get(i).toString();
				data[i][1] = ImageCenter.getInstance().getImage(ImageCenter.ICON_EMPTY);
			}

			((SysObjTableModel) table.getModel()).addRows(data);
			mainPanel.add(tableScroll, tableConstraints);
			getContentPane().add(mainPanel);

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenHeight = screenSize.height;
			int screenWidth = screenSize.width;
			setLocation(screenWidth / 3, screenHeight / 4);

			setFocusable(true);
			pack();

			showFrame();
		}

		public void showFrame() {
			setVisible(true);
		}
	}

	/**
	 * Model for table.
	 * 
	 * @author Uri.Koaz
	 * 
	 */
	public class SysObjTableModel extends DefaultTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3873195528370960731L;

		public SysObjTableModel() {
			setColumnIdentifiers(new String[] { "System Object Name", "Status", "Description", "Error Cause" });
		}

		public Class<?> getColumnClass(int c) {
			switch (c) {
			case 1:
				return ImageIcon.class;
			default:
				return String.class;
			}
		}

		public void addRows(Object[][] data) {
			for (int i = 0; i < data.length; i++) {
				addRow(new Object[] { (String) data[i][0], ((ImageIcon) data[i][1]), "Please Wait...", "" });
			}
		}
	}

	private String getStatusName(SOCheckStatus s) {
		switch (s) {
		case INITTING:
			return "Try to init";
		case INIT_SUCESS:
			return "Init O.K";
		case INIT_FAIL:
			return "Init failed";
		case CHECK_NOT_IMPLEMENTED:
			return "Init O.K (check N/A)";
		case CHECK_SUCESS:
			return "Connected";
		case CHECK_FAIL:
			return "Disconnected";
		}
		return "Unknown";
	}

	private ImageIcon getStatusIcon(SOCheckStatus s) {
		ImageIcon icon = null;
		switch (s) {
		case INITTING:
		case INIT_SUCESS:
		case CHECK_NOT_IMPLEMENTED:
			icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_BLUE);
			break;
		case CHECK_SUCESS:
			icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_GREEN);
			break;
		case CHECK_FAIL:
		case INIT_FAIL:
			icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_RED);
			break;
		}
		return icon;
	}
	
	private int getSystemObjectIndex(String objectName){
		return SystemManagerImpl.getAllObjects(true).indexOf(objectName);
	}


	public void addWarning(Test test) {
		//ignored
	}

	public void endRun() {
		
		ListenerstManager.getInstance().removeListener(this);
		if(previosScenario != null){
			TestRunner.treeView.tableController.clearScenario(false);
			ScenariosManager.getInstance().setCurrentScenario(previosScenario);
			TestRunner.treeView.tableController.refresh();
		}
	}

	public void startTest(String className, String methodName, String meaningfulName , String comment,String paramString, int count) {
		//ignored
		
	}

	public void addError(Test test, Throwable t) {
		//ignored
		
	}

	public void addFailure(Test test, AssertionFailedError t) {
		//ignored
		
	}

	public void endTest(Test test) {
		//ignored
		
	}

	public void startTest(Test test) {
		//ignored
		
	}

	@Override
	public void startTest(TestInfo testInfo) {
		// TODO Auto-generated method stub
		
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
