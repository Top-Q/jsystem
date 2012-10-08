/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import jsystem.framework.scenario.ParameterFileUtils;
import jsystem.guiMapping.JsystemMapping;

/**
 * A panel that acts as a tableCellEditor.
 * This panel present the property value in a JTextField, and a button that open another dialog that 
 * enable edit the property value.
 * When the user press the button in order to edit the property value, a compatible editor is open 
 * according to the property DataType.
 * If the DataType is Directory or File - a fileChooser dialog is open.
 * If the DataType is a multiSelectList, Search_And_List, Search_And_Multi_list - A dialog with a list 
 * is loaded to enable the user to choose from a list
 * 
 * @author Dror Voulichman	
 *
 */
public class JSystemPropertiesContentPanel extends JComponent implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JButton openCompatibleEditorButton;
	private JTextField textField;
	private JSystemProperty currentProperty;
	private String value;

	/**
	 * 
	 * @param currentProperty - The current property to be edited
	 * @param currentTableValue - The value of the property in the table.
	 * 		In case the user have already updated this property, and haven't save changes, we would
	 * 		like to present the last user chise, and not to drow back the the value that was read 
	 * 		from the jsystem.properties file.
	 */
	public JSystemPropertiesContentPanel(JSystemProperty currentProperty, String currentTableValue){
		super();
		setLayout(new BorderLayout());
		setName(JsystemMapping.getInstance().getJSystemPropertiesContentPanelName());
		
		// Add the content TextField to the panel
		this.currentProperty = currentProperty;
		value = currentTableValue;
		textField = new JTextField(value);
		add(textField, BorderLayout.CENTER);
		
		// Add the Update button to the panel
		openCompatibleEditorButton = new JButton(JsystemMapping.getInstance().getJSystemPropertiesUpdateContentButtonName());
		openCompatibleEditorButton.setActionCommand(JsystemMapping.getInstance().getUpdateJSystemPropertyValueButtonName());
		openCompatibleEditorButton.setName(JsystemMapping.getInstance().getUpdateJSystemPropertyValueButtonName());
		openCompatibleEditorButton.addActionListener(this);
		add(openCompatibleEditorButton,BorderLayout.EAST);	
	}
	
	/**
	 * Listen to the "Updatre button", and load a compatible editor once the user have pressed the button.
	 */
	public void actionPerformed(ActionEvent e) {
		String userAction = e.getActionCommand();
		if (userAction == JsystemMapping.getInstance().getUpdateJSystemPropertyValueButtonName()) {
			loadCompatibleEditor();
		} 
	}
	
	/**
	 * Check the property type and load a compatible editor.
	 */
	private void loadCompatibleEditor() {
		String userSelection = value;
		switch (currentProperty.getDataType()) {
			case DIRECTORY:
				userSelection = loadFileChooser(JFileChooser.DIRECTORIES_ONLY);
				break;
			case FILE:
				userSelection = loadFileChooser(JFileChooser.FILES_ONLY);
				break;
			case SEARCH_AND_LIST:
				userSelection = loadList(ListSelectionModel.SINGLE_SELECTION, true);
				break;
			case SEARCH_AND_MULTY_SELECT_LIST:
				userSelection = loadList(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, true);
				break;
			case MULTY_SELECT_LIST:
				userSelection = loadList(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION, false);
				break;
		}
		setValue(userSelection);
	}
	
	
	/**
	 * 
	 * @param mode - can hold one of the following options:
	 * 		JFileChooser.DIRECTORIES_ONLY
	 * 		JFileChooser.FILES_ONLY
	 * 		JFileChooser.FILES_AND_DIRECTORIES
	 * @return - A String holding the user selection
	 */
	private String loadFileChooser(int mode) {
		String userSelection = value;
		File currentDir = ParameterFileUtils.getInitialPath(value);
		JFileChooser fc = new JFileChooser(currentDir);
		fc.setFileSelectionMode(mode);
		fc.setName(JsystemMapping.getInstance().getJSystemPropertyFileChooserName());
		fc.setDialogTitle(JsystemMapping.getInstance().getJSystemPropertyFileChooserName());
		fc.setMultiSelectionEnabled(false);
		fc.setApproveButtonText(JsystemMapping.getInstance().getJSystemPropertiesSelectFileButtonName());
		
		if (fc.showDialog(this, JsystemMapping.getInstance().getJSystemPropertiesSelectFileButtonName()) == JFileChooser.APPROVE_OPTION) {
			textField.requestFocusInWindow();
			userSelection = fc.getSelectedFile().getPath();
		}
		
		return userSelection;
	}
	
	/**
	 * 
	 * @param mode - Can hold one of the following options:
	 * 		ListSelectionModel.SINGLE_SELECTION
	 * 		ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
	 * @return - A String contain the user selection from the list.
	 */
	private String loadList(int mode, boolean needToSearch) {
		String selection = value;
		String[] listItem = null;
		
		if (needToSearch) {
			listItem = JSystemPropertiesUtilities.getSearchResults(currentProperty.getReserve());
		} else {
			listItem = currentProperty.getReserve();
		}
		Arrays.sort(listItem);
		selection = JSystemPropertiesListDialog.showDialog(this, null, currentProperty.getDescription(), currentProperty.getStringName(), listItem, mode, value);
		
		return selection;
	}

	
	// Setters and Getters
	public String getValue() {
		return textField.getText();
	}

	public void setValue(String newValue) {
		value = newValue;
		textField.setText(newValue);
	}
    
	public void repaint() {
		textField.repaint();
    }

}
