/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jsystem.framework.scenario.ParameterProvider;
import jsystem.treeui.utilities.CellEditorModel;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.BeanUtils;
import jsystem.utils.beans.CellEditorType;

/**
 * This class is the base for data providing and validation.
 * It can be extended and then used to valid and provide data to the
 * <code>ObjectArrayParameterProvider</code>.<p>
 * @author guy.arieli
 *
 */
public class BeanCellEditorModel extends AbstractTableModel implements CellEditorModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected HashMap<String, BeanElement>beanMap;
	protected ArrayList<LinkedHashMap<String, String>> multiMap;
	protected String[] columnNames;
	public BeanCellEditorModel(ArrayList<BeanElement> beanElements,
			ArrayList<LinkedHashMap<String, String>> multiMap
			){
		this.multiMap = multiMap;
		beanMap = new HashMap<String, BeanElement>();
		this.columnNames = new String[beanElements.size()];
		for(int i = 0; i < beanElements.size(); i++){
			BeanElement element = beanElements.get(i);
			columnNames[i] = element.getName();
			beanMap.put(element.getName(), element);
		}
	}

	@Override
	public CellEditorType getEditorType(JTable table, int row, int column) {
		String columnName = table.getColumnName(column);
		BeanElement element = beanMap.get(columnName);
		return BeanUtils.getBeanType(element);
	}
	
	public boolean isHasOptions(JTable table, int row, int column){
		return beanMap.get(table.getColumnName(column)).isHasOptions();
	}

	@Override
	public String[] getOptions(JTable table, int row, int column) {
		return beanMap.get(table.getColumnName(column)).getOptions();
	}
	
	@Override
	public ParameterProvider getProvider(JTable table, int row, int column) {
		return beanMap.get(table.getColumnName(column)).getParameterProvider();
	}
	
	@Override
	public Class<?> getCellType(JTable table, int row, int column) {
		return beanMap.get(table.getColumnName(column)).getType();
	}

	@Override
	public boolean isValidData(JTable table, int row, int column,
			Object enteredValue) {
		return true;
	}
	@Override
	public String getLastValidationMessage() {
		return null;
	}
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}
	

	@Override
	public int getRowCount() {
		return multiMap.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return multiMap.get(rowIndex).get(columnNames[columnIndex]);
	}
	
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
    	multiMap.get(rowIndex).put(columnNames[columnIndex], (aValue == null)? null: aValue.toString());
	}
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return true;
    }


}
