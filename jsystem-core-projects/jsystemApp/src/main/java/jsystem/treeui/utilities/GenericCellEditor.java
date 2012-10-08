/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.utilities;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.params.BeanParameterElement;
import jsystem.treeui.params.ParameterTableUserDefine;
import jsystem.treeui.params.ParametersStringArrayChooser;
import jsystem.treeui.params.ParametersTableDateTimeChooser;
import jsystem.treeui.params.ParametersTableFileChooser;
import jsystem.utils.StringUtils;
import jsystem.utils.beans.CellEditorType;

public class GenericCellEditor  extends AbstractCellEditor implements TableCellEditor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4425540069459297905L;

	private CellEditorType currentType;
	private Component currentEditorComponent = null;
	private int row,column;
	private JTable table;
	
	
	private CellEditorModel model;
	public GenericCellEditor(CellEditorModel model){
		this.model = model;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.table = table;
		this.row = row;
		this.column = column;
		String content = "";
		if (value != null) {
			content = value.toString();
		}
		
		currentType = model.getEditorType(table, row, column);
		switch (currentType){
		case UNKNOWN:
		case STRING:
		case INT:
		case BYTE:
		case FLOAT:
		case DOUBLE:
		case SHORT:
		case LONG:
			currentEditorComponent = new JTextField(content);
			break;
		case COMPONENT:
			// Not implement
			//currentEditorComponent = model.getComponent(table, row, column);
			break;
		case LIST:
			JComboBox comboBox = new JComboBox(model.getOptions(table, row, column));
			comboBox.setSelectedItem(value);
			currentEditorComponent = comboBox;
			break;
		case BOOLEAN:
			JComboBox comboBox1 = new JComboBox(new String[] {"true", "false"});
			comboBox1.setSelectedItem(value);
			currentEditorComponent = comboBox1;
			break;
		case FILE:
			currentEditorComponent = new ParametersTableFileChooser(content);
			break;
		case DATE:
			currentEditorComponent = new ParametersTableDateTimeChooser(content);
			break;
		case MULTI_SELECTION_LIST:
			currentEditorComponent = new ParametersStringArrayChooser(model.getOptions(table, row, column), content.split(CommonResources.DELIMITER));
			break;
		case USER_DEFINED:
			try {
				Class<?> type = model.getCellType(table, row, column);
				ParameterProvider provider = model.getProvider(table, row, column);
				Parameter parameter = new Parameter();
				parameter.setValue(value);
				currentEditorComponent = new ParameterTableUserDefine(provider, type, parameter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		if (currentEditorComponent instanceof BeanParameterElement){
			((BeanParameterElement)currentEditorComponent).addParameterChangedListener(this);
		}
		return currentEditorComponent;
	}
	/**
	 * This method override the method from AbstractCellEditor, and allow us 
	 * to check user input value before it is written into the table
	 * If the value is invalid, we give a compatible error message, and move the user back to 
	 * the last edited cell.
	 */
	public boolean stopCellEditing() {
		if(currentType == null){
			return false;
		}
		boolean validData = false;
		Object enteredValue = getCellEditorValue();
		if(enteredValue != null){
			try {
				switch (currentType){
				case USER_DEFINED:
				case BOOLEAN:
				case STRING:
				case LIST:
				case MULTI_SELECTION_LIST:
					// no validation needed
					validData = true;
					break;
				case COMPONENT:
					// not implemented
					validData = true; 
					break;
				case BYTE:
					Byte.parseByte(enteredValue.toString());
					validData = true;
					break;
				case DOUBLE:
					Double.parseDouble(enteredValue.toString());
					validData = true;
					break;
				case INT:
					Integer.parseInt(enteredValue.toString());
					validData = true;
					break;
				case FLOAT:
					Float.parseFloat(enteredValue.toString());
					validData = true;
					break;
				case LONG:
					Long.parseLong(enteredValue.toString());
					validData = true;
					break;
				case FILE:
					validData = true;
					break;
				case DATE:
					validData = true;
					break;
				default:
					break;
				}
			} catch (NumberFormatException ignore){
			}
			if(!validData){
				ErrorPanel.showErrorDialog("Data validation error","\"" + enteredValue.toString() +"\" is not valid data for field of type " + currentType.name().toLowerCase() , ErrorLevel.Warning);
				table.changeSelection(row, column, false, false); 
				return false;
			}
			// if the model implemented data validator it will be used at this point
			if(!model.isValidData(table, row, column, enteredValue)){
				ErrorPanel.showErrorDialog("Data validation error",model.getLastValidationMessage() , ErrorLevel.Warning);
				table.changeSelection(row, column, false, false); 
				return false;
			}
		}
		return super.stopCellEditing();
	}

	@Override
	public Object getCellEditorValue() {
		if(currentType == null){
			return null;
		}
		switch (currentType){
		case UNKNOWN:
		case STRING:
		case INT:
		case FLOAT:
		case DOUBLE:
		case BYTE:
		case LONG:
			return ((JTextField)currentEditorComponent).getText();
		case BOOLEAN:
		case LIST:
			return ((JComboBox)currentEditorComponent).getSelectedItem();
		case COMPONENT:
			// Not implemented
			//return model.getComponentValue(table, row, column, currentEditorComponent);
			return null;
		case FILE:
			return ((ParametersTableFileChooser)currentEditorComponent).getSelectedFile();
		case DATE:
			return ((ParametersTableDateTimeChooser)currentEditorComponent).getDate();
		case MULTI_SELECTION_LIST:
			return StringUtils.objectArrayToString(CommonResources.DELIMITER, (Object[]) ((ParametersStringArrayChooser)currentEditorComponent).getSelected());
		case USER_DEFINED:
			try {
				return ((ParameterTableUserDefine) currentEditorComponent).getField();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
