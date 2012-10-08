/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.utilities;

import javax.swing.JTable;

import jsystem.framework.scenario.ParameterProvider;
import jsystem.utils.beans.CellEditorType;

public interface CellEditorModel{

	/**
	 * Get the type of the field in order to select the currect cell editor
	 * @param table table object
	 * @param row row index
	 * @param column column index
	 * @return editor type.
	 */
	CellEditorType getEditorType(JTable table, int row, int column);
	/**
	 * Get the option (if <code>getEditorType</code> returned LIST), for he cell 
	 * @param table table object
	 * @param row row index
	 * @param column column index
	 * @return the editor options
	 */
	String[] getOptions(JTable table, int row, int column);
	
	/**
	 * Get provider if exist one
	 * @param table
	 * @param row
	 * @param column
	 * @return The Provider 
	 */
	ParameterProvider getProvider(JTable table, int row, int column);
	
	/**
	 * Get the Cell Type
	 * @param table
	 * @param row
	 * @param column
	 * @return
	 */
	Class<?> getCellType(JTable table, int row, int column);

	/**
	 * check if the data that was given by the user is valid. If not
	 * the <code>getLastValidationMessage</code> will be shown to the user.
	 * @param table table object
	 * @param row row index
	 * @param column column index
	 * @param enteredValue value entered by the user
	 * @return true of valid
	 */
	boolean isValidData(JTable table, int row, int column, Object enteredValue);
	/**
	 * 
	 * @return the last validation error message
	 */
	String getLastValidationMessage();

}
