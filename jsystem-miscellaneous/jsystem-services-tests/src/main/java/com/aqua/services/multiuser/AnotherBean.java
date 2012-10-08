/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import jsystem.framework.TestBeanClass;

@TestBeanClass(include={"enumSelect", "selectedItem"}, model=AnotherBeanCellEditorModel.class)
public class AnotherBean {
	enum MyEnum {
		val1,
		val2,
		val3
	}
	
	private MyEnum enumSelect;
	private String selectedItem;
	
	public MyEnum getEnumSelect() {
		return enumSelect;
	}
	
	public void setEnumSelect(MyEnum enumSelect) {
		this.enumSelect = enumSelect;
	}
	
	public String getSelectedItem() {
		return selectedItem;
	}
	
	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}
}
