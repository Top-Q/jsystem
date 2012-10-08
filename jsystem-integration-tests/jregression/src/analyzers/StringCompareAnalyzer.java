package analyzers;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * analyze the expected string to verify if it equals to the string found 
 * in the test
 * if a TestOption.UnEqual is chosen with ignoreCase = true, then a different
 * String(different chars in the String) are matched.
 * if a TestOption.UnEqual is chosen with ignoreCase = true, then every kind of difference will 
 * be matched including case difference.
 * @author Chen n.
 */

public class StringCompareAnalyzer extends AnalyzerParameterImpl{

	private String testString;
	private String expectedString;
	private TestOption option;
	private boolean ignoreCase;
	
	public static enum TestOption{
		Equals,
		UnEqual,
		Contains,
		startsWith,
		endsWith;
	}
	
	/**
	 * the default behavior is to test for equals, and ignore case
	 * @param testString
	 */
	public StringCompareAnalyzer(String testString) {
		this(testString, TestOption.Equals);
	}
	
	/**
	 * takes a String for comparison as test subject and what kind of 
	 * comparison you want.
	 * use the StringCompareAnalyzer.TestOption.<type> to pass the option argument.
	 * the direction is always from testAgainst object to testString i.e:
	 * Contains means you want to test if the testAgainst String contains the testString.
	 * will ignore case by default
	 * @param testString
	 * @param option
	 */
	public StringCompareAnalyzer(String testString, TestOption option){
		this(testString, option, true);
	}
	
	/**
	 * takes a String for comparison as test subject and what kind of 
	 * comparison you want.
	 * use the StringCompareAnalyzer.TestOption.<type> to pass the option argument.
	 * the direction is always from testAgainst object to testString i.e:
	 * Contains means you want to test if the testAgainst String contains the testString.
	 * if ignoreCase is true, the match will ignore case(be case insensitive)
	 * if ignore case is false, the match will be exact.
	 * @param testString
	 * @param option
	 * @param ignoreCase
	 */
	public StringCompareAnalyzer(String testString, TestOption option, boolean ignoreCase){
		this.testString = testString;
		this.option = option;
		this.ignoreCase = ignoreCase;
	}
	
	public void analyze() {
		expectedString = testAgainst.toString().trim();
		switch(option)
		{
			case Equals:
			{
				if(ignoreCase){
					if (!testString.equalsIgnoreCase(expectedString)){
						status = false;
						title = "test String: \""+   testString+ "\" isn't as expected String \"" +expectedString +"\"";
						return;
					
					}else {
						status = true;
						title = "test String: \""+ testString+ "\" is as expected String \"" + expectedString +"\"";
						return;
					
					}
				}
				else{
					if (!testString.equals(expectedString)){
						status = false;
						title = "test String: \""+   testString+ "\" isn't as expected String \"" +expectedString +"\"";
						return;
					
					}else {
						status = true;
						title = "test String: \""+ testString+ "\" is as expected String \"" + expectedString +"\"";
						return;
					
					}
				}
			}
			case UnEqual:
			{
				if(ignoreCase){
					if(!testString.equalsIgnoreCase(expectedString)){
						status = true;
						title = "test string: \""+   testString+ "\" is not equal to expected String \"" +expectedString +"\"";
						return;
					}
					else{
						status = false;
						title = "test string: \""+   testString+ "\" is equal to expected String \"" +expectedString +"\"";
						return;
					}
				}
				else{
					if(!testString.equals(expectedString)){
						status = true;
						title = "test string: \""+   testString+ "\" is not equal to expected String \"" +expectedString +"\"";
						return;
					}
					else{
						status = false;
						title = "test string: \""+   testString+ "\" is equal to expected String \"" +expectedString +"\"";
						return;
					}
				}
			}
			case Contains:
			{
				if(ignoreCase){
					if(expectedString.toLowerCase().contains(testString.toLowerCase())){
						status = true;
						title = "the testAgainst string \"" + expectedString +"\" contains the testString "+ testString;
						return;
					}
					else{
						status = false;
						title = "the testAgainst string \"" + expectedString +"\"does not contain the testString "+ testString;
						return;
					}
				}
				else{
					if(expectedString.contains(testString)){
						status = true;
						title = "the testAgains string \"" + expectedString +"\" contains the testString "+ testString;
						return;
					}
					else{
						status = false;
						title = "the testAgains string \"" + expectedString +"\"does not contain the testString "+ testString;
						return;
					}
				}
			}
			case startsWith:
			{
				if(ignoreCase){
					if(expectedString.toLowerCase().startsWith(testString.toLowerCase())){
						status = true;
						title = "the testAgains string \"" + expectedString +"\" starts with the testString "+ testString;
						return;
					}
					else{
						status = true;
						title = "the testAgains string \"" + expectedString +"\" does not start with the testString "+ testString;
						return;
					}
				}
				else{
					if(expectedString.startsWith(testString)){
						status = true;
						title = "the testAgains string \"" + expectedString +"\" starts with the testString "+ testString;
						return;
					}
					else{
						status = true;
						title = "the testAgains string \"" + expectedString +"\" does not start with the testString "+ testString;
						return;
					}
				}
			}
			case endsWith:
			{
				if(ignoreCase){
					if(expectedString.toLowerCase().endsWith(testString.toLowerCase())){
						status = true;
						title = "the testAgains string \"" + expectedString +"\" ends with the testString "+ testString;
						return;
					}
					else{
						status = true;
						title = "the testAgains string \"" + expectedString +"\" does not end with the testString "+ testString;
						return;
					}
				}
				else{
					if(expectedString.endsWith(testString)){
						status = true;
						title = "the testAgains string \"" + expectedString +"\" ends with the testString "+ testString;
						return;
					}
					else{
						status = true;
						title = "the testAgains string \"" + expectedString +"\" does not end with the testString "+ testString;
						return;
					}
				}
			}
		}
	}
}


