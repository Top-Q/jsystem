/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.com.aqua.excel;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JTable;

import jsystem.extensions.paramproviders.BeanCellEditorModel;
import jsystem.framework.scenario.ProviderDataModel;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.CellEditorType;

public class TestBeanCellEditorModel extends BeanCellEditorModel implements ProviderDataModel {

	public TestBeanCellEditorModel(ArrayList<BeanElement> beanElements,
			ArrayList<LinkedHashMap<String, String>> multiMap) {
		super(beanElements, multiMap);
	}
	public CellEditorType getEditorType(JTable table, int row, int column) {
		String columnName = table.getColumnName(column);
		if(columnName.equals("I1")){
			return CellEditorType.LIST;
		} else {
			return super.getEditorType(table, row, column);
		}
	}
	@Override
	public String[] getOptions(JTable table, int row, int column) {
		String columnName = table.getColumnName(column);
		if(columnName.equals("I1")){
			ArrayList<String> options = new ArrayList<String>();
			for(int i = 0; i < row; i++){
				options.add(Integer.toString(i));
			}
			return options.toArray(new String[options.size()]); 
		} else {
			return super.getOptions(table, row, column);
		}
	}

}
