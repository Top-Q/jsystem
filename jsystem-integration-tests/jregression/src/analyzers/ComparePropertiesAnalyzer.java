package analyzers;

import java.util.Enumeration;
import java.util.Properties;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * This analyzer receive 2 vectors of properties, and verify they are equals.
 * Each vector contain a list of Strings in which:
 * each odd cell contain the property name,
 * each even cell contain the value of the property from the previous cell property. 
 * @author Dror Voulichman
 *
 */
public class ComparePropertiesAnalyzer extends AnalyzerParameterImpl {
	Properties expectedValues = null;
	Properties testValues = null;
	
	public ComparePropertiesAnalyzer(Properties testValues) {
		this.testValues = testValues;
	}

	/**
	 * This analyzer receive 2 vectors of properties, and verify they are equals.
	 *	Each vector contain a list of Strings in which:
	 * each odd cell contain the property name,
	 * each even cell contain the value of the property from the previous cell property. 
	 */
	public void analyze() {
		expectedValues = (Properties)this.testAgainst;
		status = true;
		title = "Verify the dialog contain the expected values";
		String expectedValue, testValue;
	    StringBuffer buffer = new StringBuffer();

		buffer.append("\n There was an attempt to change " + expectedValues.size() + " properties:");
		buffer.append("\n **************************************************************");

		Enumeration keys = expectedValues.keys();
		int counter = 0;
		while (keys.hasMoreElements()) {
			counter++;
			String key = (String) keys.nextElement();
			expectedValue = expectedValues.getProperty(key);
			testValue = testValues.getProperty(key);
			if (testValue.equals(expectedValue)) {
				buffer.append("\n" + counter + ") - " + key + " = " + testValue + " as expected");
			} else {
				status = false;
				buffer.append("\n" + counter + ") - " + key.toString() + " = " + testValue + " instead of " + expectedValue);
			}
		}
		
		message = buffer.toString();
	}
}
