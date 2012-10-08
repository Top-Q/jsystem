/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.scenario;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class ParametersSortingTest extends SystemTestCase {
	String S1 = "222";
	String Wwws = "64";
	int Num = 3;
	int Num2 = 4;
	String S5 = "second s";
	String T1 = "send";
	int Wwwi = 46;
	
	public int getNum() {
		return Num;
	}

	public void setNum(int num) {
		Num = num;
	}

	public int getNum2() {
		return Num2;
	}

	public void setNum2(int num2) {
		Num2 = num2;
	}

	public String getS1() {
		return S1;
	}

	public void setS1(String s1) {
		S1 = s1;
	}

	public String getS5() {
		return S5;
	}

	public void setS5(String s5) {
		S5 = s5;
	}

	public String getT1() {
		return T1;
	}

	public void setT1(String t1) {
		T1 = t1;
	}

	public int getWwwi() {
		return Wwwi;
	}

	public void setWwwi(int wwwi) {
		Wwwi = wwwi;
	}

	public String getWwws() {
		return Wwws;
	}

	public void setWwws(String wwws) {
		Wwws = wwws;
	}

	/**
	 * Test the parameters feature
	 */
	@TestProperties(name = "test the parameters sorting")
	public void testEmptyInclude(){
	}
}
