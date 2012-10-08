/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.JTable;

import jsystem.extensions.paramproviders.BeanCellEditorModel;
import jsystem.framework.scenario.ProviderDataModel;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.CellEditorType;

public class AnotherBeanCellEditorModel extends BeanCellEditorModel implements ProviderDataModel {
	
	private static final long serialVersionUID = 1L;

	public AnotherBeanCellEditorModel(ArrayList<BeanElement> beanElements,
			ArrayList<LinkedHashMap<String, String>> multiMap) {
		super(beanElements, multiMap);
	}
	
	public CellEditorType getEditorType(JTable table, int row, int column) {
		
		String columnName = table.getColumnName(column);
		if(columnName.equals("SelectedItem")){
			return CellEditorType.LIST;
		} else {
			return super.getEditorType(table, row, column);
		}
	}
	
	@Override
	public String[] getOptions(JTable table, int row, int column) {
		
		String columnName = table.getColumnName(column);
		if(columnName.equals("SelectedItem")){
			String val = ""+table.getValueAt(row, 0);
			if (val.equals("val1")) {
				return new String[]{"xxx","yyy","zzz","ggg"};
			} else {
				return new String[]{"1111","2222","3333","4444"};
			}
			
		} else {
			return super.getOptions(table, row, column);
		}
	}

}
