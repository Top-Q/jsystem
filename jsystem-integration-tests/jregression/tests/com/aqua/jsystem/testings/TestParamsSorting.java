package com.aqua.jsystem.testings;

import org.junit.Test;

import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCase4;

public class TestParamsSorting extends JSysTestCase4 {
	private enum values{
		V1, 
		V2, 
		V3;
	}
	
	public TestParamsSorting(){
		super();
		setFixture(RootFixture.class);
	}
	
	private int number;
	private double id;
	private double age;
	private int accountNum;
	private String name = "dan";
	private values vals;
	
	
	public values getVals() {
		return vals;
	}

	public void setVals(values vals) {
		this.vals = vals;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAccountNum() {
		return accountNum;
	}

	public void setAccountNum(int accountNum) {
		this.accountNum = accountNum;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getId() {
		return id;
	}

	public void setId(double id) {
		this.id = id;
	}

	public double getAge() {
		return age;
	}

	public void setAge(double age) {
		this.age = age;
	}
	
	/**
	 * @params.include name, vals, accountNum,id, number
	 */
	@Test
	public void testTabSortingByName(){
		report.report("reporting");
	}
}
