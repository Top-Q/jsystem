/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jsystem.framework.DataType;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.interfaces.JsystemPropertiesChangeListener;
import jsystem.treeui.properties.GUIFrameworkOptions.Group;
import jsystem.utils.StringUtils;
import jsystem.utils.SwingUtils;

/**
 * The main dialog that preset and allow the user to edit the JSystem properties.
 * @author Dror Voulichman
 */
public class JSystemPropertiesDialog implements ActionListener, MouseListener, KeyListener{
	static JSystemPropertiesDialog jsystemPropertiesDialog = null;
	
	private Group group;
	private DataType dataType;
	private String[] resurve, groupNames;
	private String description, stringName, longDescription, example; 
	private Object defaultValue;
	private boolean reloadRunnerRequire, exposeToDialog, saveDefaultValueToFile;
	private static List<JsystemPropertiesChangeListener> changeListeners = new ArrayList<JsystemPropertiesChangeListener>();
	JDialog myDialog = null;
	ArrayList<Vector<JSystemProperty>> groupProperties = null;
	Vector<JTable> tables = null;
	JTabbedPane mainTabbedPane = null;
	JScrollPane extentionPanel;
	JTextArea textArea;
	int currentTabIndex;
	
	final static Color MAIN_COLOR = new Color(0xf6, 0xf6, 0xf6);


	/**
	 * 
	 * @return the same instance of the class, and in that way ensure that only one object can be instantiate from this class
	 * @throws Exception
	 */
	public static JSystemPropertiesDialog getInstance() throws Exception {
		if (null == jsystemPropertiesDialog) {
			jsystemPropertiesDialog = new JSystemPropertiesDialog();
		}
		return jsystemPropertiesDialog;
	}
	
	/**
	 * add a listener to call when relevant event occured.
	 * for example, a save action.
	 * @param listener
	 */
	public static void addListener(JsystemPropertiesChangeListener listener){
		changeListeners.add(listener);
	}
	
	/**
	 * A listener for TAB change.
	 * We use this listener for the following purposes:
	 * 1 - o validate the last edited property - If the user entered an invalid 
	 * value to the last edited property on the previous tab, and try to move to 
	 * another tab, then we force him to go back to the last edited value.
	 * 2 - When a new tab is selected, and no row is selected, do not display any 
	 * long description, until the user have pressed a specific line. 
	 */
    ChangeListener changeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent changeEvent) {
			JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
			int newTabIndex = sourceTabbedPane.getSelectedIndex();
			if ( (newTabIndex != currentTabIndex) && (stopLastCellEditing() == true) ) {
		        currentTabIndex = newTabIndex;
		        textArea.setText("");
			} 
			
		}
    };

      
    /**
     * initContents() method read all JSystem properties information from two Enums:
     * FrameworkOptions and GUIFrameworkOptions, and fill up the properties into vectors.
     */
	public void initContents() {
		String value;
		int numberOfGroups = Group.values().length;
		groupNames = new String[numberOfGroups];
		groupProperties = new ArrayList<Vector<JSystemProperty>>(numberOfGroups);
		JSystemProperties pProperties = JSystemProperties.getInstance();
		pProperties.rereadPropertiesFile();
		
		// init group names (= Tab names)
		for (Group currentGroup: Group.values()) {	
			groupNames[currentGroup.getIndex()] = new String(currentGroup.getValue());
			groupProperties.add(currentGroup.getIndex(), new Vector<JSystemProperty>());
		}
		
		// Init all properties from the file
		for (FrameworkOptions frameworkOptions: FrameworkOptions.values()) {
			stringName = frameworkOptions.getString();
			if (stringName == null) {
				stringName = "";
			}
			
			description = frameworkOptions.getDescription();
			if (description == null) {
				description = "";
			}

			dataType = frameworkOptions.getDataType();
			defaultValue = frameworkOptions.getDefaultValue();
			reloadRunnerRequire = frameworkOptions.isReloadRunnerRequire();
			resurve = frameworkOptions.getReserve();
			saveDefaultValueToFile = frameworkOptions.isSaveDefaultValueToFile();

			try {
				GUIFrameworkOptions guiFrameworkOption = GUIFrameworkOptions.valueOf(frameworkOptions.name());
				exposeToDialog = guiFrameworkOption.isExposeToDialog();
				group = guiFrameworkOption.getGroup();
				longDescription = guiFrameworkOption.getLongDescription();
				if (longDescription == null) {
					longDescription = "";
				}
				example = guiFrameworkOption.getExample();
				if (example == null) {
					example = "";
				}
			} catch (Exception e) {
				group = Group.ADVANCED;
				longDescription = "";
				example = "";
				exposeToDialog = false;
			}

			if (exposeToDialog) {
				value = pProperties.getPreference(frameworkOptions);
				if ( StringUtils.isEmpty(value) ){
					if ( StringUtils.isEmpty(defaultValue.toString()) == false ) {
						value = defaultValue.toString();
					}
				}

				JSystemProperty property = new JSystemProperty(stringName, group, description, longDescription, example, dataType, defaultValue, reloadRunnerRequire, value, resurve, saveDefaultValueToFile);
				groupProperties.get(group.getIndex()).add(property);
			}
		}

	}



	/**
	 * 
	 * @param group - The name of the TAB to be construct.
	 * @param properties - A Vector holding all the properties to be preset in the current Tab.
	 * @return - A Panel with a JTable that holds all the properties.
	 */
	private JPanel constructTablePanel(int group, Vector<JSystemProperty> properties) {
		// Construct the table
		JSystemPropertiesTableModel model = new JSystemPropertiesTableModel(properties);
		JTable table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setBackground(MAIN_COLOR);
		
		
		table.getColumn("Property Name").setHeaderRenderer(new JSystemOPropertiesTableHeaderRenderer());
		table.getColumn("Property Name").setCellRenderer(new JSystemPropertiesTableRenderer());
		table.getColumn("Description").setHeaderRenderer(new JSystemOPropertiesTableHeaderRenderer());
		table.getColumn("Description").setCellRenderer(new JSystemPropertiesTableRenderer());
		table.getColumn("Value").setHeaderRenderer(new JSystemOPropertiesTableHeaderRenderer());
		table.getColumn("Value").setCellRenderer(new JSystemPropertiesTableRenderer());

		table.setDefaultEditor(DataType.class, new JSystemPropertiesTableEditor(properties));
		table.addMouseListener(this);
		table.addKeyListener(this);
		table.setRowHeight(24);
		tables.add(table);

		// Construct the JScrollPanel with the table inside it
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setBackground(MAIN_COLOR);
		scrollPane.setViewportView(table);
		
		// Put everything together 
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * @return - JTabbedpane that holds all the properties tables.
	 * @throws Exception
	 */
	private JTabbedPane constructTabbedPanel() throws Exception {
		JTabbedPane tabbedPane = SwingUtils.getJTabbedPaneWithBgImage(
				ImageCenter.getInstance().getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG), 
				ImageCenter.getInstance().getImage(ImageCenter.ICON_TABBES_TOOLBAR_BG));
		tabbedPane.setBackground(MAIN_COLOR);
		tabbedPane.setName(JsystemMapping.getInstance().getJSystemPropertiesTabPanelName());
		tabbedPane.setToolTipText(JsystemMapping.getInstance().getJSystemPropertiesTabPanelName());
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		tabbedPane.addChangeListener(changeListener);
		
		for (int group = 0; group < Group.values().length; group++) {
			JPanel panel = constructTablePanel(group, groupProperties.get(group));
			tabbedPane.addTab(groupNames[group], panel);
		}
		return (tabbedPane);
	}

	
	/**
	 * @param dialog - A JDialog to be resize
	 * @throws Exception
	 */
	private void dialogResize(JDialog dialog) throws Exception {
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);

		dialog.setTitle(JsystemMapping.getInstance().getJSystemPropertiesMenuItem());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialog.setLocation(screenSize.width / 4, screenSize.height / 5);
		Dimension dialogSize = new Dimension((int) (screenSize.width / 1.5), (int) (screenSize.height / 1.5));
		dialog.setPreferredSize(dialogSize);
	}

	/**
	 * @return - A panel with all the desired buttons
	 * @throws Exception
	 */
	private JPanel constructButtonsPanel() throws Exception {
		String[] buttonNames = new String[] {
				JsystemMapping.getInstance().getJSystemPropertiesSaveButtonName(),
				JsystemMapping.getInstance().getJSystemPropertiesSystemDefaultButtonName(),
				JsystemMapping.getInstance().getJSystemPropertiesCancleButtonName() };
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(MAIN_COLOR);


		for (int index = 0; index < buttonNames.length; index++) {
			JButton button = new JButton(buttonNames[index]);
			button.addActionListener(this);
			buttonsPanel.add(button);
		}
		return (buttonsPanel);
	}


	/**
	 * load up the dialog GUI
	 * @throws Exception
	 */
	public void dialogShow() throws Exception {
		Image image = ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM);
		JSplitPane innerSplitPane;
		if (myDialog == null) {
			
			textArea = new JTextArea();
			textArea.setBackground(MAIN_COLOR);
			textArea.setMargin(new Insets(10, 10, 10, 10));
			textArea.setEditable(false);
			textArea.setWrapStyleWord(true);
			
			tables = new Vector<JTable>();
			myDialog = new JDialog();
			myDialog.setResizable(false);
			myDialog.setIconImage(image);
			
			initContents();
			dialogResize(myDialog);
			myDialog.setTitle(JsystemMapping.getInstance().getJSystemPropertiesMenuItem());
			mainTabbedPane = constructTabbedPanel();
			extentionPanel = SwingUtils.getJScrollPaneWithWaterMark(ImageCenter.getInstance().getAwtImage(
					ImageCenter.ICON_TEST_TREE_BG), textArea);
			
			extentionPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 3));
			innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainTabbedPane, extentionPanel);
			innerSplitPane.setDividerLocation(250);
			
			myDialog.add(innerSplitPane, BorderLayout.CENTER);
			myDialog.add(constructButtonsPanel(), BorderLayout.SOUTH);
			myDialog.pack();
		}
		myDialog.setVisible(true);
	}

	
	/**
	 * Close the dialog GUI
	 * @throws Exception
	 */
	private void dialogClose() throws Exception {
		myDialog.setVisible(false);
		myDialog.dispose();
		tables.clear();
		myDialog = null;
		tables = null;
	}


	/**
	 * Save JSystem properties from the dialog into a file, and refresh the GUI
	 * @throws Exception
	 */
	private void actionSave() throws Exception {
		Vector<String> listOfPropertiesThatRequierRestsrt = new Vector<String>();
		JSystemProperties pJSysPropetires = JSystemProperties.getInstance();
		JTable groupTable;
		JSystemProperty currentProperty;
		
		// Run over all the Tabs (Each tab contain a table with some properties
		for (int group = 0; group < Group.values().length; group++) {
			groupTable = tables.get(group);
			// For each property in the current tab, save any property that contain a value (not null, and not empty) and that was changed
			for (int propIndex = 0; propIndex < groupProperties.get(group).size(); propIndex++) {
				currentProperty = groupProperties.get(group).get(propIndex);
				String oldValue = currentProperty.getValue();
				Object objectNewValue = groupTable.getValueAt(propIndex, JSystemPropertiesTableModel.VALUE_COLUMN);
				String newValue = "";
				if (objectNewValue != null) {
					newValue = objectNewValue.toString();
				}

				if ( newValue.equalsIgnoreCase(oldValue) ) {
					continue;
				}
				currentProperty.setDirty(true);
				currentProperty.setValue(newValue);
				if ( StringUtils.isEmpty(newValue) ) {
					// The value of the current property was deleted, and need to remove this property from the file
					pJSysPropetires.removePreference(currentProperty.getStringName());
				} else {
					pJSysPropetires.setPreference(currentProperty.getStringName(), newValue);
					if (currentProperty.isReloadRunnerRequire()) {
						listOfPropertiesThatRequierRestsrt.add(currentProperty.getStringName());
					}
				}

			}
		}
		
		JSystemProperties.getInstance().flushCacheToFile();
		
		// before closing the dialog, check if Runner should be reloaded, and pop up a compatible message to the user if needed.
		if (listOfPropertiesThatRequierRestsrt.size() > 0) {
			StringBuffer message = new StringBuffer("The following properties will only be effected after runner restart:");
			for (int i = 0; i < listOfPropertiesThatRequierRestsrt.size(); i++) {
				message.append("\n " + listOfPropertiesThatRequierRestsrt.get(i));
			}
			message.append("\n Would you like to restart runner now?");
			int ans = JOptionPane.showConfirmDialog(
					myDialog, 
					message.toString(),
					JsystemMapping.getInstance().getJSystemPropertiesConfirmRestartDialogTitle(), 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);

			if (ans == JOptionPane.YES_OPTION) {
				System.exit(6); // Reload the Runner
			} 
		} else {
			pJSysPropetires.rereadPropertiesFile();
		}
		dialogClose();
	}

	/**
	 * Restore System defaults.
	 * This operation close the dialog, delete jsystem.properties file, and reload the runner.
	 * During Runner loading, if jsystem.properties file can not be found, the application create this file with default values.
	 * @throws Exception
	 */
	private void actionSystemDefault() throws Exception {
		int ans = JOptionPane.showConfirmDialog(
				myDialog, 
				"In order to restore system defaults, runner must be restarted. \n " +
				"continue with restart? \n " +
				"(No will not change current properties)", 
				JsystemMapping.getInstance().getJSystemPropertiesRestoreDefaultsDiallogTitle(), 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE);
		
		if (ans == JOptionPane.YES_OPTION) {
			// restart the runner
			JSystemProperties pPropetires = JSystemProperties.getInstance();
			
			// 1) save needed data
			Properties p = pPropetires.getPreferences();
			
			// 2) close jsystem.properties
			// 3) create a new Properties reference
			pPropetires.clearAndResetJsystemPropertiesFile();

			// 4) restore required data from properties 
			// TESTS_SOURCE_FOLDER
			FrameworkOptions[] options = new FrameworkOptions[] {
					FrameworkOptions.TESTS_CLASS_FOLDER, 
					FrameworkOptions.TESTS_SOURCE_FOLDER,
					FrameworkOptions.USED_SUT_FILE,
					FrameworkOptions.REPEAT_ENABLE};
			
			for (FrameworkOptions option :options) {
				System.out.println("Trying to save " + option.toString() + " into " + pPropetires.toString());
				String value = p.getProperty(option.toString());
				if (value!=null){
					pPropetires.setPreference(option.toString(), value);
				}
			}
			System.exit(6);

		}
	}


	
	/**
	 * Close the dialog as a respond for pressing cancle button 
	 * @throws Exception
	 */
	private void actionClose() throws Exception {
		for (int counter = 0; counter < Group.values().length; counter++) {
			groupNames[counter] = null;
			groupProperties.get(counter).clear();
		}
		groupProperties.clear();
		groupNames = null;
		groupProperties = null;
		dialogClose();
	}

	/**
	 * Button clicked event handler.
	 */
	public void actionPerformed(ActionEvent e) {
		boolean dataIsvalid = stopLastCellEditing();
		try {
			if (e.getActionCommand() == JsystemMapping.getInstance().getJSystemPropertiesSaveButtonName()) {
				if (dataIsvalid) {
					actionSave();
					fireChangeListeners();
				}
			} else if (e.getActionCommand() == JsystemMapping.getInstance().getJSystemPropertiesSystemDefaultButtonName()) {
				actionSystemDefault();
			} else if (e.getActionCommand() == JsystemMapping.getInstance().getJSystemPropertiesCancleButtonName()) {
				actionClose();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void fireChangeListeners(){
		for(JsystemPropertiesChangeListener listener : changeListeners){
			listener.jsystemPropertiesChanged();
		}
	}

	/**
	 * This method come to deal with a bug of JTable.
	 * stopCellEditing() is not called for the last edited cell, unless you press <Enter>
	 * We use stopLastCellEditing() method only in the context of user input validation.
	 * If the user entered an invalid value to one of the properties, and pressed save, 
	 * in case his last input is invalid we force him to go back to the last edited value. 
	 */
	private boolean stopLastCellEditing() {
		boolean dataIsValid = true;
		
		if (mainTabbedPane != null) {
			// at the beginning of initialization tables has only one table, and from some reasons currentTabIndex > tables.size() and cause for exception 
			if (currentTabIndex < tables.size()) { 
				JTable lastEditedTable = tables.get(currentTabIndex);
				
				int lastEditedcolumn = lastEditedTable.getSelectedColumn();	
				int lastEditedRow = lastEditedTable.getSelectedRow();
				if ( (lastEditedcolumn == 2) && (lastEditedRow > -1) && (lastEditedTable != null)) {
					dataIsValid = lastEditedTable.getCellEditor(lastEditedRow, lastEditedcolumn).stopCellEditing();
				}
				if (! dataIsValid) {
					mainTabbedPane.setSelectedIndex(currentTabIndex);
				}
			}
		}
		return dataIsValid;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JSystemPropertiesDialog jsystemPropertiesDialog = JSystemPropertiesDialog.getInstance();
			jsystemPropertiesDialog.dialogShow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}



	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}


	/**
	 * Once the user pressed another line in the table, this method presents the 
	 * compatible property long description, and default value in the extension panel 
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		updateDescription();
	}
	
	private void updateDescription(){
		JTable lastEditedTable = tables.get(currentTabIndex);
		int lastEditedRow = lastEditedTable.getSelectedRow();
		String spaceLine = new String("\n");

		if (lastEditedRow >= 0) {
			JSystemProperty property = (groupProperties.get(currentTabIndex)).get(lastEditedRow);
			String longDescription = property.getLongDescription();
			String example = property.getExample();
			String defaultValue = property.getDefaultVlaue();
			StringBuffer extendedInfo = new StringBuffer();
			if ( ! StringUtils.isEmpty(longDescription) ){
				extendedInfo.append("Description:" + spaceLine + longDescription + spaceLine);
			}
			if ( ! StringUtils.isEmpty(example) ) {
				extendedInfo.append(spaceLine + "Example:" + spaceLine + example + spaceLine);
			}
			if ( ! StringUtils.isEmpty(defaultValue) ) {
				extendedInfo.append(spaceLine + "Default Value = " + defaultValue);
			}
			textArea.setText(extendedInfo.toString());
		}
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		updateDescription();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}



	@Override
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == 38 || keyCode == 40){ // up or down arrows
			updateDescription();
		}
	}



	@Override
	public void keyTyped(KeyEvent e) {
	}

	
}
