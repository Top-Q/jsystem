package analyzers;

import jsystem.framework.analyzer.AnalyzerParameterImpl;
/**
 * analyzes a boolean value condition against a boolean value set
 * @author Dan
 *
 */
public class BooleanAnalyzer extends AnalyzerParameterImpl {
	private boolean expectedCondition;
	private String titleIfPass, titleIfFails;
	private boolean userDefinedTitle = false;
	
	/**
	 * compare the test against to given boolean
	 * 
	 * @param condition	the expected condition
	 */
	public BooleanAnalyzer(boolean condition) {
		this.expectedCondition = condition;
		userDefinedTitle = false;
	}
	
	/**
	 * compare the test against to given boolean and report given pass\fail string
	 * 
	 * @param condition	the expected condition
	 * @param titleIfPass	report if Pass
	 * @param titleIfFails	report if Fail
	 */
	public BooleanAnalyzer(boolean condition, String titleIfPass, String titleIfFails) {
		this.expectedCondition = condition;
		this.titleIfPass = titleIfPass;
		this.titleIfFails = titleIfFails;
		userDefinedTitle = true;
	}
	
	@Override
	public void analyze() {
		boolean metCondition = (Boolean)testAgainst;
		status = (metCondition == expectedCondition);
		if (userDefinedTitle){
			title = status?  titleIfPass : titleIfFails;
		}else{
			title = "expected to be "+expectedCondition+" effectivlly it's "+metCondition;
		}
	}

}
