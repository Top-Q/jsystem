/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.utils;

import junit.framework.SystemTestCase;
import jsystem.utils.datasource.DataSource;

public class DataSourceTests extends SystemTestCase {
	DataSource increment = new DataSource("INC(64, 64, 128)");
	DataSource decrement  = new DataSource("DEC(128, 64, 90)");
	DataSource random  = new DataSource("RAND(2,90)");
	DataSource list = new DataSource("12, 56, 8");
	DataSource fixed = new DataSource("23");
	
	
	int loopAmount = 20;
	

	public void testIncrement() throws Exception{
		for (int i = 0; i < loopAmount; i++) {
			report.report("" + increment.getInt());
			increment.getNextValue();
		}
	}
	
	public void testDecrement() throws Exception{
		for (int i = 0; i < loopAmount; i++) {
			report.report("" + decrement.getInt());
			decrement.getNextValue();
		}
	}
	
	public void testRandom() throws Exception{
		for (int i = 0; i < loopAmount; i++) {
			report.report("" + random.getInt());
			random.getNextValue();
		}
	}
	
	public void testList() throws Exception{
		for (int i = 0; i < loopAmount; i++) {
			report.report("" + list.getInt());
			list.getNextValue();
		}
	}
	
	
	public void testFixed() throws Exception{
		for (int i = 0; i < loopAmount; i++) {
			report.report("" + fixed.getInt());
			fixed.getNextValue();
		}
	}
	
	
	public void testFail() throws Exception{
		DataSource wrongExample = new DataSource("IN(1,1,1)");
		wrongExample.getNextValue();
	}


	public int getLoopAmount() {
		return loopAmount;
	}

	
	public void setLoopAmount(int loopAmount) {
		this.loopAmount = loopAmount;
	}

	public String getDecrement() {
		return decrement.getCurrentCommand();
	}

	/**
	 * @section Decrement
	 * @param decrement
	 */
	public void setDecrement(String decrement) {
		this.decrement = new DataSource(decrement);
	}

	public String getFixed() {
		return fixed.getCurrentCommand();
	}

	/**
	 * @section Fixed
	 * @param fixed
	 */
	public void setFixed(String fixed) {
		this.fixed = new DataSource(fixed);
	}

	public String getIncrement() {
		return increment.getCurrentCommand();
	}


	/**
	 * @section Increment
	 * @param increment
	 */
	public void setIncrement(String increment) {
		this.increment = new DataSource(increment);
	}


	public String getList() {
		return list.getCurrentCommand();
	}


	/**
	 * @section List
	 * @param list
	 */
	public void setList(String list) {
		this.list = new DataSource(list);
	}


	public String getRandom() {
		return random.getCurrentCommand();
	}

	
	/**
	 * @section Random
	 * @param random
	 */
	public void setRandom(String random) {
		this.random = new DataSource(random);
	}
	
	public String[] sectionOrder(){
		return new String[]{"Decrement","Random","Increment","Fixed"};
	}
}
