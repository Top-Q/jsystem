package regression.analyzersTests;

import jsystem.framework.analyzer.AnalyzerParameterImpl;


/**
 * This is a simple example of analyzer
 * This analyzer search for equal strings we will use it in the 
 * analysis test. 
 * @author Guy levi.
 *
 */
public class SimpleAnalyzer extends AnalyzerParameterImpl {

	protected String toFind;

	public SimpleAnalyzer(String toFind) {
		this.toFind = toFind;
	}

	public void analyze() {
		String testText = testAgainst.toString();
		if (testText == null) {
			title = "Text to analyze is null";
			status = false;
		}
		message = "Expected text=" + toFind + ". Actual text=" + testText;
		status = (testText.equals(toFind));

		if (status) {
			title = "The text to find is substring of the text that was chcked ";
		} else {
			title = "The texts are not equals(or substrings).";
		}
	}

}
