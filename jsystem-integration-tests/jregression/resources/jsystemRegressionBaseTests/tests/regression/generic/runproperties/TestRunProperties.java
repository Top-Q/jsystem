package regression.generic.runproperties;

import jsystem.framework.RunProperties;
import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase;

/**
 * @author goland
 */
public class TestRunProperties extends SystemTestCase {

	private String version;
	private String propName;
	private String propValue;

	/**
	 * @params.include propName,propValue
	 */
	public void testCheckRunProperty() throws Exception{
		String val = RunProperties.getInstance().getRunProperty(getPropName());
		assertEquals(val, getPropValue());
	}

	/**
	 * @params.include propName,propValue
	 */
	public void testSetPermanentSummaryProperty() throws Exception{
		Summary.getInstance().setProperty(getPropName(),getPropValue());
	}

	/**
	 * @params.include propName,propValue
	 */
	public void testSetTemporarySummaryProperty() throws Exception {
		Summary.getInstance().setTempProperty(getPropName(), getPropValue());
		
	}

	/**
	 * 
	 * @params.include version
	 */
	public void testSetVersionSummaryProperty() throws Exception {
		Summary.getInstance().setVersion(getVersion());
	}

	/**
	 * @params.inclide propName,propValue
	 */
	public void testSetRunProperty() throws Exception {
		RunProperties.getInstance().setRunProperty(getPropName(),getPropValue());
	}

	/**
	 * @params.include
	 */	
	public void testThatDoesntDoAnyThing() throws Exception{
		report.report("Doesn't do anything");
	}
	
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	public String getPropValue() {
		return propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
}
