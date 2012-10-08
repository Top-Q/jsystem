/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.com.aqua.excel;

import jsystem.framework.TestBeanClass;
import jsystem.framework.TestBeanMethod;
import jsystem.utils.beans.CellEditorType;

@TestBeanClass(include={"i1", "true", "type", "b1"}, model=TestBeanCellEditorModel.class)
public class Test {
	
	String s1 = "hi";
	float f1;
	long l1 = 33;
	int i1;
	boolean isTrue;
	byte b1 = 0;
	
	public byte getB1() {
		return b1;
	}
	public void setB1(byte b1) {
		this.b1 = b1;
	}
	public boolean isTrue() {
		return isTrue;
	}
	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}
	CellEditorType type = CellEditorType.BOOLEAN;
	public CellEditorType getType() {
		return type;
	}
	public void setType(CellEditorType type) {
		this.type = type;
	}
	public String getS1() {
		return s1;
	}
	public void setS1(String s1) {
		this.s1 = s1;
	}
	public float getF1() {
		return f1;
	}
	public void setF1(float f1) {
		this.f1 = f1;
	}
	public long getL1() {
		return l1;
	}
	@TestBeanMethod(options={"1","7","13"})
	public void setL1(long l1) {
		this.l1 = l1;
	}
	public int getI1() {
		return i1;
	}
	//@TestBeanMethod(options={"1","77","13"})
	public void setI1(int i1) {
		this.i1 = i1;
	}
}
