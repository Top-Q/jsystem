/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.compare;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * 
 * this class compares two numeric values<br>
 * the setTestAgainst is the number to Check (double,long,int...)
 * 
 * @author Nizan Freedman
 *
 */
public class NumberCompare extends AnalyzerParameterImpl {
	
	public enum compareOption{
		EQUAL,LESS,GREATER,GREAT_OR_EQUAL,LESS_OR_EQUAL;
	}
	
	public enum CheckError{
		CHECK_BY_PRECENTAGE,CHECK_BY_DIFFERENCE;
	}
	
	private compareOption option = compareOption.EQUAL;
	private double numToCompare;
	private double error;
	private CheckError checkError;
	
	/**
	 * compare the test against with the given number, check equality by enum with given precentage error
	 * 
	 * @param option	compareOption - Equal\Greater...
	 * @param numToCompare	number to compare to
	 * @param error	error precentage
	 */
	public NumberCompare(compareOption option, double numToCompare, double error){
		this.option = option;
		this.numToCompare = numToCompare;
		this.error = error;
		checkError = CheckError.CHECK_BY_PRECENTAGE;
	}
	
	/**
	 * compare the test against with the given number, check equality by enum with given difference error
	 * 
	 * @param option	compareOption - Equal\Greater...
	 * @param numToCompare	number to compare to
	 * @param error	error difference
	 */
	public NumberCompare(compareOption option, double numToCompare, long error){
		this.option = option;
		this.numToCompare = numToCompare;
		this.error = error;
		checkError = CheckError.CHECK_BY_DIFFERENCE;
	}
	
	public void analyze() {
		double numToCheck = Double.parseDouble(testAgainst.toString());
		double min,max;
		if (checkError == CheckError.CHECK_BY_PRECENTAGE){
			min = numToCompare * (1-error);
			max = numToCompare * (1+error);
		}else{
			min = numToCompare - error;
			max = numToCompare + error;
		}
		String difference = "";
		switch(option){
			case EQUAL:
				status = (numToCheck>= min && numToCheck<=max);
				title = "Expected number between values: "+min+"-"+max;
				break;
			case LESS:
				status =  numToCheck< max;
				title = "Expected number Less than: "+max;
				difference = " Difference: "+(max-numToCheck);
				break;
			case GREATER:
				status =  numToCheck> min;
				title = "Expected number Greater than: "+min;
				difference = " Difference: "+(numToCheck-min);
				break;
			case LESS_OR_EQUAL:
				status =  numToCheck<= max;
				title = "Expected number Less or equal to: "+max;
				difference = " Difference: "+(max-numToCheck);
				break;
			case GREAT_OR_EQUAL:
				status =  numToCheck>= min;
				title = "Expected number Greater or Equal to: "+max;
				difference = " Difference: "+(numToCheck-min);
				break;
		}
		title += " , Actual Number is: "+numToCheck+" "+difference;
	}
}
