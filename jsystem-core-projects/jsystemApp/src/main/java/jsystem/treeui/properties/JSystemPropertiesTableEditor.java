/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import java.awt.Component;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import jsystem.framework.DataType;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.scenario.ParameterFileUtils;
import jsystem.treeui.utilities.ApplicationUtilities;
import jsystem.utils.Encryptor;
import jsystem.utils.StringUtils;

/**
 * A class that acts as a table cell editor for JSyatemProperties tables.
 * 
 * The tables that present the JSystem properties holds different editors to
 * enable convenient value editing.
 * 
 * @author Dror Voulichman
 */
public class JSystemPropertiesTableEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;
	Vector<JSystemProperty> properties = null;
	Component currentEditorComponent = null;
	int row = 0;
	int column = 0;
	JSystemProperty currentProperty;
	JTable table;
	String errorMessage = null;
	String editorValue = null;

	private static Logger logger = Logger.getLogger(JSystemPropertiesTableEditor.class.getName());

	public JSystemPropertiesTableEditor(Vector<JSystemProperty> properties) {
		this.properties = properties;
	}

	/**
	 * getTableCellEditorComponent() method check for the data type of the
	 * current edited property and return a compatible editor, with the correct
	 * property value
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.row = row;
		this.column = column;
		this.table = table;
		String currentTableCellContent = "";
		if (value != null) {
			currentTableCellContent = value.toString();
		}

		currentProperty = properties.get(row);

		DataType propertyType = currentProperty.getDataType();
		switch (propertyType) {
		case DIRECTORY:
		case FILE:
		case SEARCH_AND_LIST:
		case SEARCH_AND_MULTY_SELECT_LIST:
			currentEditorComponent = new JSystemPropertiesContentPanel(currentProperty, currentTableCellContent);
			break;
		case LIST:
			if (currentProperty.getReserve() == null) {
				super.stopCellEditing();
			} else {
				JComboBox comboBox = new JComboBox(currentProperty.getReserve());
				comboBox.setSelectedItem(value);
				currentEditorComponent = comboBox;
			}
			break;
		case MULTY_SELECT_LIST:
			if (currentProperty.getReserve() == null) {
				super.stopCellEditing();
			} else {
				currentEditorComponent = new JSystemPropertiesContentPanel(currentProperty, currentTableCellContent);
			}
			break;
		case BOOLEAN:
			JComboBox comboBox = new JComboBox(new String[] { "true", "false" });
			comboBox.setSelectedItem(value);
			currentEditorComponent = comboBox;
			break;
		case TEXT:
			currentEditorComponent = new JTextField(currentTableCellContent);
			break;
		case PASSWORD:
			currentEditorComponent = new JPasswordField();
			break;
		case NUMERIC:
			currentEditorComponent = new JTextField(currentTableCellContent);
			errorMessage = "Only numeric value is allowed";
			break;
		case EMAIL:
			currentEditorComponent = new JTextField(currentTableCellContent);
			errorMessage = "Invalid Email Address";
			break;
		case MAIL_LIST:
			currentEditorComponent = new JTextField(currentTableCellContent);
			errorMessage = "Atlist one of the Email address is invalid";
			break;

		} // end of switch.
		return (currentEditorComponent);
	}

	/**
	 * getCellEditorValue() method return the value of the current property from
	 * the property editor.
	 */
	public String getCellEditorValue() {
		String value = "";
		if (currentEditorComponent != null) {
			value = getValueFromEditor();
		} else {
			value = currentProperty.getValue();
		}
		return (value);
	}

	private String getValueFromEditor() {
		String value = "";
		if (currentProperty == null) {
			return "";
		}
		switch (currentProperty.getDataType()) {
		case DIRECTORY:
		case FILE:
		case MULTY_SELECT_LIST:
		case SEARCH_AND_LIST:
		case SEARCH_AND_MULTY_SELECT_LIST:
			value = ((JSystemPropertiesContentPanel) currentEditorComponent).getValue();
			break;
		case LIST:
		case BOOLEAN:
			Object o = ((JComboBox) currentEditorComponent).getSelectedItem();
			value = o == null ? "" : o.toString();
			break;
		case TEXT:
		case NUMERIC:
		case EMAIL:
		case MAIL_LIST:
			value = ((JTextField) currentEditorComponent).getText();
			break;
		case PASSWORD:
			value = new String(((JPasswordField) currentEditorComponent).getPassword());
			try {
				value = Encryptor.encrypt(value);
			} catch (Exception e) {
				logger.warning(StringUtils.getStackTrace(e));
			}
			break;
		}// switch

		// makes stdout file as relative path to the project
		if (currentProperty.getStringName().equals(FrameworkOptions.STDOUT_FILE_NAME.getString())) {
			value = ParameterFileUtils.convertUserInput(value);
		}

		return value;
	}

	/**
	 * This method override the method from AbstractCellEditor, and allow us to
	 * check user input value before it is written into the table If the value
	 * is invalid, we give a compatible error message, and move the user back to
	 * the last edited cell.
	 */
	public boolean stopCellEditing() {
		boolean validData = false;
		String enteredValue = getValueFromEditor();
		if (!enteredValue.isEmpty()) {
			validData = formatValidation(enteredValue);
			if (validData) {
				validData = specificPropertyValidation(enteredValue);
			}
			if (validData == false) {
				JOptionPane.showMessageDialog(null, "tried to set value " + editorValue + "\n" + errorMessage,
						"Wrong value was set for " + currentProperty.getStringName(), JOptionPane.ERROR_MESSAGE);
				table.changeSelection(row, column, false, false);
				return false;
			}
		}
		return super.stopCellEditing();
	}

	/**
	 * This method check if the data entered by the user is compatible with the
	 * dataType
	 * 
	 * @param value
	 *            - the value entered by the user into the dialog
	 * @return true when the data is valid / false when data is not valid.
	 */
	private boolean formatValidation(String value) {
		boolean validValue = true;

		editorValue = value;
		switch (currentProperty.getDataType()) {
		case NUMERIC:
			validValue = ApplicationUtilities.isNumric(value);
			break;
		case EMAIL:
			validValue = ApplicationUtilities.isEmailAddress(value);
			break;
		case MAIL_LIST:
			validValue = ApplicationUtilities.isEmailList(value);
		}

		return validValue;
	}

	/**
	 * Except the verification that the user input data is compatible with the
	 * data type (Done in formatValidation()), This method verify the current
	 * property value answers some other specific demands
	 * 
	 * @param value
	 * @return
	 */
	private boolean specificPropertyValidation(String value) {
		boolean validValue = true;

		String propertyName = currentProperty.getStringName();
		if (propertyName.equals(FrameworkOptions.TESTS_CLASS_FOLDER.getString())) {
			validValue = ApplicationUtilities.verifyClassesDirectory(value);
			if (!validValue) {
				errorMessage = ApplicationUtilities.invalidClassesDirectoryMessage;
			}
		}

		return validValue;
	}
}
