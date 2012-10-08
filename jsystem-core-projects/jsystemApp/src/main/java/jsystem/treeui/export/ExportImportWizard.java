/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.export;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.actionItems.RefreshAction;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;
import jsystem.utils.build.BuildException;
import jsystem.utils.build.BuildUtils;
import net.javaprog.ui.wizard.AbstractStep;
import net.javaprog.ui.wizard.DataModel;
import net.javaprog.ui.wizard.DefaultDataLookup;
import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.WizardModel;
import net.javaprog.ui.wizard.WizardModelEvent;
import net.javaprog.ui.wizard.WizardModelListener;

public class ExportImportWizard implements WizardModelListener {
	DataModel data = null;

	boolean cancel = false;

	boolean export = true;

	public ExportImportWizard(boolean export) {
		this.export = export;
	}
	
	public void launch(String testsDir, String classDir, String runnerDir) {
		data = new DataModel();
		WizardModel model = new DefaultWizardModel(new Step[] { new WelcomeStep(export),
				new ConfigurationStep(data, testsDir, classDir, runnerDir, export), new FinishStep(export) });

		String title = export? "Export Wizard" : "Import Wizard";

		Wizard wizard = new Wizard(model, title, new ImageIcon("computer.gif"));
		model.addWizardModelListener(this);

		((Frame) wizard.getOwner()).setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));

		wizard.pack();
		wizard.setLocationRelativeTo(null);
		wizard.setVisible(true);
	}

	public void stepShown(WizardModelEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void wizardCanceled(WizardModelEvent arg0) {
		cancel = true;

	}

	public void wizardFinished(WizardModelEvent arg0) {
		if (!cancel) {
			final String srcDir = (String) data.getData("srcLocation");
			final String classDir = (String) data.getData("classLocation");
			final String runnerDir = (String) data.getData("runnerLocation");
			//Creating the resources folder name.
			final String resourcesDir = new File(new File(srcDir).getParent(),CommonResources.RESOURCES_FOLDER_NAME).getAbsolutePath();
			final String jdkDir = (String) data.getData("jdkLocation");
			final String zipFile = (String) data.getData("zipFile");
			final Boolean includeSrc = (Boolean) data.getData("includeSrc");
			final Boolean includeSut = (Boolean) data.getData("includeSut");
			final Boolean includeScenarios = (Boolean) data.getData("includeScenarios");
			final Boolean includeClasses = true;//(Boolean) data.getData("includeClasses");
			final Boolean includeRunner = (Boolean) data.getData("includeRunner");
			final Boolean includeJdk = (Boolean) data.getData("includeJdk");
			final Boolean includeLog = (Boolean) data.getData("includeLog");
			final Boolean includeLib = (Boolean) data.getData("includeLib");
			final Boolean deleteLib = false;// (Boolean) data.getData("deleteLib");
			final Boolean deleteTests = (Boolean) data.getData("deleteTests");
			final Boolean deleteSuts = (Boolean) data.getData("deleteSuts");
			final Boolean deleteScenarios = (Boolean) data.getData("deleteScenarios");
			
			WaitDialog.launchWaitDialog(export? "Export process ..." : "Import process ...", null);
			(new Thread() {
				public void run() {
					try {

						if (includeLog.booleanValue()){
							createDebugInformationPropertiesFile();
						}
						if (export){
							BuildUtils.export(includeSrc.booleanValue(), includeSut.booleanValue(), includeScenarios.booleanValue(), includeClasses.booleanValue(), includeRunner
									.booleanValue(), includeJdk.booleanValue(),
									includeLog.booleanValue(), "temp", srcDir, classDir, runnerDir ,jdkDir, resourcesDir, zipFile);
						}else{
							BuildUtils.importProject(includeSrc.booleanValue(), includeSut.booleanValue(), includeScenarios.booleanValue(), includeLib, deleteTests, deleteSuts,
									deleteScenarios, deleteLib, "temp", srcDir, classDir, resourcesDir, zipFile);
						}

					} catch (BuildException be) {
						ErrorPanel.showErrorDialog(export? "Export error" : "Import error", be.getAntFailString(), ErrorLevel.Error);
					} catch (Exception e) {
						ErrorPanel.showErrorDialog(export? "Export error" : "Import error", e, ErrorLevel.Error);
					} finally {
						WaitDialog.endWaitDialog();
						if (!export && !isCancel()){
							RefreshAction.getInstance().refresh(false);
						}
					}
				}
			}).start();
		}

	}

	public void wizardModelChanged(WizardModelEvent arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * @todo This is not the best place to generate the debug information
	 * rest of the information is found in jsystem.properties file and log files. 
	 */
	private static void createDebugInformationPropertiesFile() throws Exception {
		Properties props = new Properties();
		props.setProperty("currentScenario",ScenariosManager.getInstance().getCurrentScenario().getName());
		FileOutputStream stream = new FileOutputStream("debug.properties");
		try {
			props.store(stream,"Aqua runner debug information");
		}finally {
			stream.close();
		}
	}

	public boolean isCancel() {
		return cancel;
	}
}

class WelcomeStep extends AbstractStep {
	boolean export;

	public WelcomeStep(boolean export) {
		super("Welcome", (export) ? "This is the Export Wizard" : "This is the Import Wizard");
		this.export = export;
	}

	protected JComponent createComponent() {
		JPanel stepComponent = new JPanel();
		stepComponent.add(new JLabel((export) ? "<html>This wizard will guide you through the process<p>"
				+ "of exporting your project.<p>"
				+ "You can navigate through the steps using the buttons below.</html>"
				: "<html>This wizard will guide you through the process<p>" + "of importing a project.<p>"
				+ "You can navigate through the steps using the buttons below.</html>"));
		return stepComponent;
	}

	public void prepareRendering() {
	}
}

class ConfigurationStep extends AbstractStep {
	boolean export;

	protected DataModel data;

	protected JTextField srcTextField = new JTextField();

	protected JTextField classTextField = new JTextField();

	protected JTextField runnerTextField = new JTextField();

	protected JTextField outFileTextField = new JTextField();

	protected JTextField jdkTextField = new JTextField();

	protected JFileChooser fc = new JFileChooser();

	protected JFileChooser zfc = new JFileChooser();

	public ConfigurationStep(DataModel data, String testDir, String classDir, String runnerDir, boolean export) {
		super((export) ? "Export Configuration" : "Import Configuration", "Please build the export configuration");
		this.export = export;
		this.data = data;
		srcTextField.setText(testDir);
		classTextField.setText(classDir);
		runnerTextField.setText(runnerDir);
	}

	protected JComponent createComponent() {
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		zfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		zfc.setFileFilter(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.getName().toLowerCase().endsWith(".zip");
			}

			public String getDescription() {
				return "zip";
			}

		});
		final JPanel stepComponent = new JPanel(new BorderLayout());

		JPanel inputPanel = new JPanel(new SpringLayout());
		
		if (export) {
			
			inputPanel.add(new JLabel("Test location:"));
			inputPanel.add(srcTextField);
			JButton browseButton = new JButton("Browse...");
			inputPanel.add(browseButton);

			inputPanel.add(new JLabel("Class location:"));
			inputPanel.add(classTextField);
			JButton browseButton2 = new JButton("Browse...");
			inputPanel.add(browseButton2);

			inputPanel.add(new JLabel("Runner location:"));
			inputPanel.add(runnerTextField);
			JButton browseButton3 = new JButton("Browse...");
			inputPanel.add(browseButton3);

			
			inputPanel.add(new JLabel("Jdk location:"));
			inputPanel.add(jdkTextField);

			jdkTextField.setText(System.getProperty("java.home"));

			JButton browseButton4 = new JButton("Browse...");
			inputPanel.add(browseButton4);

			browseButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					navigateToTheCurrentDirectory(fc,srcTextField.getText());
					if (fc.showOpenDialog(stepComponent) == JFileChooser.APPROVE_OPTION) {
						srcTextField.setText(fc.getSelectedFile().getPath());
					}
				}
			});
			browseButton2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					navigateToTheCurrentDirectory(fc, classTextField.getText());
					if (fc.showOpenDialog(stepComponent) == JFileChooser.APPROVE_OPTION) {
						classTextField.setText(fc.getSelectedFile().getPath());
					}
				}
			});
			browseButton3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					navigateToTheCurrentDirectory(fc, runnerTextField.getText());
					if (fc.showOpenDialog(stepComponent) == JFileChooser.APPROVE_OPTION) {
						runnerTextField.setText(fc.getSelectedFile().getPath());
					}
				}
			});

			
			browseButton4.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					navigateToTheCurrentDirectory(fc, jdkTextField.getText());
					if (fc.showOpenDialog(stepComponent) == JFileChooser.APPROVE_OPTION) {
						jdkTextField.setText(fc.getSelectedFile().getPath());
					}
				}
			});
		}




		inputPanel.add(new JLabel(export? "Output file:" : "Import file:"));
		inputPanel.add(outFileTextField);
		if (export) {
			String lastExport = JSystemProperties.getInstance().getPreference(FrameworkOptions.LAST_EXPORT_FILE);
			if (!StringUtils.isEmpty(lastExport)) {
				outFileTextField.setText(lastExport);
			}else{
				outFileTextField.setText("reg_env-1.0.zip");
			}
		}else{
			String lastImport = JSystemProperties.getInstance().getPreference(FrameworkOptions.LAST_IMPORT_FILE);
			if (!StringUtils.isEmpty(lastImport)) {
				outFileTextField.setText(lastImport);
			}else{
				outFileTextField.setText("");
			}
		}
		JButton browseButton5 = new JButton("Browse...");
		inputPanel.add(browseButton5);

		if (export){
			SpringUtilities.makeCompactGrid(inputPanel, 5, 3, // rows, cols
					6, 6, // initX, initY
					4, 4); // xPad, yPad
		}else{
			Dimension dim = new Dimension(80,30);
			inputPanel.setPreferredSize(dim);
			inputPanel.setMinimumSize(dim);
			SpringUtilities.makeCompactGrid(inputPanel, 1, 3, // rows, cols
					1, 6, // initX, initY
					1, 1); // xPad, yPad
		}


		final JCheckBox includeSrc = new JCheckBox((export) ? "Export tests" : "Import tests");
		includeSrc.setSelected(true);
//		includeSrc.setEnabled(false);
		final JCheckBox includeSut = new JCheckBox((export) ? "Export Sut" : "Import Sut");
		includeSut.setSelected(true);
		final JCheckBox includeScenarios = new JCheckBox((export) ? "Export Scenarios" : "Import Scenarios");
		includeScenarios.setSelected(true);
		final JCheckBox includeLib = new JCheckBox((export) ? "Export lib" : "Import lib");
		includeLib.setSelected(true);
		final JCheckBox includeRunner = new JCheckBox((export) ? "Export runner" : "Import runner");
		final JCheckBox includeJdk = new JCheckBox((export) ? "Export Jdk" : "Import Jdk");
		final JCheckBox includeLog = new JCheckBox((export) ? "Export log" : "Import log");

		final JCheckBox deleteTests = new JCheckBox("Erase prior Tests");
		deleteTests.setSelected(false);
		final JCheckBox deleteSuts = new JCheckBox("Erase prior Suts");
		deleteTests.setSelected(false);
		final JCheckBox deleteScenarios = new JCheckBox("Erase prior Scenarios");
		deleteTests.setSelected(false);
//		final JCheckBox deleteLib = new JCheckBox(erase);
//		deleteTests.setSelected(false);
		
		final JCheckBox deleteAll = new JCheckBox("Erase ALL prior data");
		deleteTests.setSelected(false);
		
		/**
		 * For enabling and disabling delete buttons
		 */
		ActionListener buttonsListener = new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
					Object source = e.getSource();
					boolean deleteSource = source.equals(deleteAll);
					boolean deleteAllSelected = deleteAll.isSelected();
					markIfSelected(deleteSource, deleteAllSelected, includeSrc, deleteTests);
					markIfSelected(deleteSource, deleteAllSelected, includeSut, deleteSuts);
					markIfSelected(deleteSource, deleteAllSelected, includeScenarios, deleteScenarios);
//					markIfSelected(deleteSource, deleteAllSelected, includeLib, deleteLib);
			}
			
			private void markIfSelected(boolean deleteAllEvent,boolean mark, JCheckBox toCheck, JCheckBox toMark){
				if (deleteAllEvent){ // Delete all was checked/unchecked
					if (mark){
						toMark.setSelected(toCheck.isSelected());
					}else{
						toMark.setSelected(false);
					}
				}else{
					if (mark){
						toMark.setSelected(toCheck.isSelected());
					}else{
						toMark.setSelected(toMark.isSelected() && toCheck.isSelected());
					}
				}
				
				toMark.setEnabled(!mark && toCheck.isSelected());
			}
			
		};
		
		deleteAll.addActionListener(buttonsListener);
		includeSrc.addActionListener(buttonsListener);
		includeSut.addActionListener(buttonsListener);
		includeScenarios.addActionListener(buttonsListener);
		includeLib.addActionListener(buttonsListener);
		
		JPanel checkPanel;
		
		
		if (export){
		
			checkPanel = new JPanel(new GridLayout(4, 1));
			checkPanel.add(includeSrc);
			checkPanel.add(includeSut);
			checkPanel.add(includeScenarios);
			checkPanel.add(includeLib);
			checkPanel.add(includeRunner);
			checkPanel.add(includeJdk);
			checkPanel.add(includeLog);
			
		}else{ // Import
			
			checkPanel = new JPanel(new GridLayout(5, 1));
			
			checkPanel.add(includeLib);
			checkPanel.add(new JLabel(""));
			
			checkPanel.add(includeSrc);
			checkPanel.add(deleteTests);
			
			checkPanel.add(includeSut);
			checkPanel.add(deleteSuts);
			
			checkPanel.add(includeScenarios);
			checkPanel.add(deleteScenarios);
			
			checkPanel.add(new JPanel());
			checkPanel.add(deleteAll);
		}
		
		if (export){
			stepComponent.add(inputPanel, BorderLayout.CENTER);
			stepComponent.add(checkPanel, BorderLayout.SOUTH);
		}else{ // Import
			stepComponent.add(inputPanel, BorderLayout.NORTH);
			stepComponent.add(checkPanel, BorderLayout.SOUTH);
		}

		outFileTextField.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				if (!StringUtils.isEmpty(outFileTextField.getText())){
					setCanGoNext(true);
				}
			}

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
		browseButton5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				navigateToTheCurrentDirectory(zfc, outFileTextField.getText());
				if (zfc.showOpenDialog(stepComponent) == JFileChooser.APPROVE_OPTION) {
					outFileTextField.setText(zfc.getSelectedFile().getPath());
					if (export){
						JSystemProperties.getInstance().setPreference(FrameworkOptions.LAST_EXPORT_FILE, zfc.getSelectedFile().getPath());
					}else{ // import
						JSystemProperties.getInstance().setPreference(FrameworkOptions.LAST_IMPORT_FILE, zfc.getSelectedFile().getPath());
					}
					setCanGoNext(true);
				}
			}
		});


		registerData(data, srcTextField, "srcLocation");
		
		registerData(data, classTextField, "classLocation");
		
		registerData(data, runnerTextField, "runnerLocation");
		
		registerData(data, outFileTextField, "zipFile");
		
		registerData(data, jdkTextField, "jdkLocation");

		registerData(data, includeSrc, "includeSrc");
		
		registerData(data, includeSut, "includeSut");

		registerData(data, includeScenarios, "includeScenarios");

		registerData(data, includeLib, "includeLib");

		registerData(data, includeRunner, "includeRunner");
		
		registerData(data, includeJdk, "includeJdk");

		registerData(data, includeLog, "includeLog");
		
//		registerData(data, deleteLib, "deleteLib");
		registerData(data, deleteTests, "deleteTests");
		registerData(data, deleteSuts, "deleteSuts");
		registerData(data, deleteScenarios, "deleteScenarios");
		
		return stepComponent;
	}
	
	private void registerData(DataModel data, JComponent component, String name){
		Method method = null;
		String methodName = "";
		if (component instanceof JCheckBox){
			methodName = "isSelected";
		}else if (component instanceof JTextField){
			methodName = "getText";
		}
		try {
			method = component.getClass().getMethod(methodName, (Class[]) null);
		} catch (NoSuchMethodException nsme) {
		}
		data.registerDataLookup(name, new DefaultDataLookup(component, method, null));
	}

	/**
	 * <code>navigateToTheCurrentDirectory(JFileChooser jfc,String currentDirectory)</code><br>
	 * Placing the user directory as requested.<br>
	 * If the requested Directory/File isn't exist the parent directory will be choosing, if exist one.<br>
	 * If There is no parent directory the %HOME% directory will be select.<br>
	 * 
	 * @param jfc - JFileChooser
	 * @param currentDirectory - String
	 * @author Gana K
	 */
	protected void navigateToTheCurrentDirectory(JFileChooser jfc,String currentDirectory){
		File file = new File(currentDirectory);
		try{
			if(file.exists()){
				jfc.setCurrentDirectory(file);
			}else{
				if(file.getParentFile().exists()){
					jfc.setCurrentDirectory(file.getParentFile());
				}
			}
		}catch(Exception ex){
			jfc.setCurrentDirectory(new File(System.getProperty("user.home")));
		}

	}

	public void prepareRendering() {
		if (!export) {
			setCanGoNext(false);
		}
	}
}

class FinishStep extends AbstractStep {
	boolean export;

	public FinishStep(boolean export) {
		super("Finish", (export) ? "The export process will now be started" : "The import process will now be started");
		this.export = export;
	}

	protected JComponent createComponent() {
		JPanel stepComponent = new JPanel();
		stepComponent.add(new JLabel((export) ? "<html>The export wizard will now copy the necessary files<p>"
				+ "and build the export file. Please click \"Finish\".</html>"
				: "<html>The import wizard will now copy the necessary files<p>"
					+ "and update the import file. Please click \"Finish\".</html>"));
		return stepComponent;
	}

	public void prepareRendering() {
		setCanFinish(true);
	}
}

class SpringUtilities {
	/**
	 * A debugging utility that prints to stdout the component's minimum,
	 * preferred, and maximum sizes.
	 */
	public static void printSizes(Component c) {
		System.out.println("minimumSize = " + c.getMinimumSize());
		System.out.println("preferredSize = " + c.getPreferredSize());
		System.out.println("maximumSize = " + c.getMaximumSize());
	}

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code> components of
	 * <code>parent</code> in a grid. Each component is as big as the maximum
	 * preferred width and height of the components. The parent is made just big
	 * enough to fit them all.
	 * 
	 * @param rows
	 *            number of rows
	 * @param cols
	 *            number of columns
	 * @param initialX
	 *            x location to start the grid at
	 * @param initialY
	 *            y location to start the grid at
	 * @param xPad
	 *            x padding between cells
	 * @param yPad
	 *            y padding between cells
	 */
	public static void makeGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad, int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout) parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeGrid must use SpringLayout.");
			return;
		}

		Spring xPadSpring = Spring.constant(xPad);
		Spring yPadSpring = Spring.constant(yPad);
		Spring initialXSpring = Spring.constant(initialX);
		Spring initialYSpring = Spring.constant(initialY);
		int max = rows * cols;

		// Calculate Springs that are the max of the width/height so that all
		// cells have the same size.
		Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
		Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).getWidth();
		for (int i = 1; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

			maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
			maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
		}

		// Apply the new width/height Spring. This forces all the
		// components to have the same size.
		for (int i = 0; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));

			cons.setWidth(maxWidthSpring);
			cons.setHeight(maxHeightSpring);
		}

		// Then adjust the x/y constraints of all the cells so that they
		// are aligned in a grid.
		SpringLayout.Constraints lastCons = null;
		SpringLayout.Constraints lastRowCons = null;
		for (int i = 0; i < max; i++) {
			SpringLayout.Constraints cons = layout.getConstraints(parent.getComponent(i));
			if (i % cols == 0) { // start of new row
				lastRowCons = lastCons;
				cons.setX(initialXSpring);
			} else { // x position depends on previous component
				cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST), xPadSpring));
			}

			if (i / cols == 0) { // first row
				cons.setY(initialYSpring);
			} else { // y position depends on previous row
				cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH), yPadSpring));
			}
			lastCons = cons;
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, Spring.sum(Spring.constant(yPad), lastCons
				.getConstraint(SpringLayout.SOUTH)));
		pCons.setConstraint(SpringLayout.EAST, Spring.sum(Spring.constant(xPad), lastCons
				.getConstraint(SpringLayout.EAST)));
	}

	/* Used by makeCompactGrid. */
	private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code> components of
	 * <code>parent</code> in a grid. Each component in a column is as wide as
	 * the maximum preferred width of the components in that column; height is
	 * similarly determined for each row. The parent is made just big enough to
	 * fit them all.
	 * 
	 * @param rows
	 *            number of rows
	 * @param cols
	 *            number of columns
	 * @param initialX
	 *            x location to start the grid at
	 * @param initialY
	 *            y location to start the grid at
	 * @param xPad
	 *            x padding between cells
	 * @param yPad
	 *            y padding between cells
	 */
	public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad,
			int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout) parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++) {
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
			}
			for (int r = 0; r < rows; r++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++) {
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			for (int c = 0; c < cols; c++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}
}