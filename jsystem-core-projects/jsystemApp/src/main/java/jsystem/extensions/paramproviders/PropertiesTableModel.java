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

public class PropertiesTableModel extends AbstractTableModel implements CellEditorModel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4912338796859225011L;
	
	private static final String[] COLUMN_NAMES = { "Name", "Value" };
	private LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
	private ArrayList<BeanElement> beanElements;
	HashMap<String, BeanElement> beanMap;
	public PropertiesTableModel(LinkedHashMap<String, String> map,ArrayList<BeanElement> beanElements) {
		this.map = map;
		this.beanElements = beanElements;
		beanMap = new HashMap<String, BeanElement>();
		for(BeanElement element: beanElements){
			beanMap.put(element.getName(), element);
		}
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}
	

	@Override
	public int getRowCount() {
		return map.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0) { // key
			return map.keySet().toArray()[rowIndex];
		} else {
			return map.get(map.keySet().toArray()[rowIndex]);
		}
	}
	
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		String key = map.keySet().toArray()[rowIndex].toString();
		map.put(key, (aValue != null)? aValue.toString(): null);
	}
    public boolean isCellEditable(int rowIndex, int columnIndex) {
    	return (columnIndex == 1);
    }


	@Override
	public CellEditorType getEditorType(JTable table, int row, int column) {
		BeanElement element = beanElements.get(row);
		return BeanUtils.getBeanType(element);
	}

	@Override
	public String[] getOptions(JTable table, int row, int column) {
		return  beanElements.get(row).getOptions();
	}
	
	@Override
	public ParameterProvider getProvider(JTable table, int row, int column) {
		return beanElements.get(row).getParameterProvider();
	}
	
	@Override
	public Class<?> getCellType(JTable table, int row, int column) {
		return beanElements.get(row).getType();
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

}
