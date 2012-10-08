/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.diistributed;

import junit.framework.SystemTestCase;
/**
 * Test parameters simple example.
 * @author goland
 */
public class DistributedExecutionExample extends SystemTestCase {

	private enum RepeatCondition {
		noRepeat,
		onSuccess,
		onFail,
		anyWay
	}
	
	private enum Condition {
		NA,
		onSuccess,
		onFail
	}

	private boolean skipOnFail;
	
	private RepeatCondition repeatCondition = RepeatCondition.noRepeat;
	private int repeatNumber =1;
	
	private boolean successToFail;
	
	private  Condition condition = Condition.onFail;
	private  int jumpTo;
	
	/**
	 * @params.include skipOnFail,repeatCondition,repeatNumber,successToFail,condition,jumpTo  
	 */
	public void testJustATest() throws Exception {
		
	}
	
	public boolean isSkipOnFail() {
		return skipOnFail;
	}
	
	/**
	 * Skip scenario execution in case one of the tests fail
	 * @section Flow Control
	 */	
	public void setSkipOnFail(boolean skipOnFail) {
		this.skipOnFail = skipOnFail;
	}
	
	public RepeatCondition getRepeatCondition() {
		return repeatCondition;
	}
	
	/**
	 * @section Flow Control
	 */
	public void setRepeatCondition(RepeatCondition repeatCondition) {
		this.repeatCondition = repeatCondition;
	}
	
	public int getRepeatNumber() {
		return repeatNumber;
	}
	
	/**
	 * @section Flow Control
	 */
	public void setRepeatNumber(int repeatNumber) {
		this.repeatNumber = repeatNumber;
	}
	
	public boolean isSuccessToFail() {
		return successToFail;
	}
	
	/**
	 * @section Flow Control
	 */
	public void setSuccessToFail(boolean successToFail) {
		this.successToFail = successToFail;
	}
	
	public Condition getCondition() {
		return condition;
	}
	
	/**
	 * @section Flow Control
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public int getJumpTo() {
		return jumpTo;
	}
	/**
	 * @section Flow Control
	 */
	public void setJumpTo(int jumpTo) {
		this.jumpTo = jumpTo;
	}

	
}
