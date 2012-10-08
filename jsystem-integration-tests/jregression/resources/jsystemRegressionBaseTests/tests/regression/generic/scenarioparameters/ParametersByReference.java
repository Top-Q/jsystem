package regression.generic.scenarioparameters;

import java.io.File;
import java.util.Date;

import jsystem.framework.RunProperties;
import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase;

/**
 * 
 * @author nizanf
 *
 */
public class ParametersByReference extends SystemTestCase {
	
	private static String INTEGER_REFERENCE = "Integer reference value = ";
	private static String DOUBLE_REFERENCE = "Double reference value = ";
	private static String STRING_REFERENCE = "String reference value = ";
	private static String FLOAT_REFERENCE = "Float reference value = ";
	private static String FILE_REFERENCE = "File reference value = ";
	private static String DATE_REFERENCE = "Date reference value = ";
	private static String LONG_REFERENCE = "Long reference value = ";
	
	private int intValue = 1;
	private String string = "aaa";
	private double doubleParameter = 0.1;
	private float floatParameter = 2e-4f;
	private long longParameter = 40000;
	private Date date; 
	private File file;
	
	private int intChange = 0;
	private String stringChange = "Testing string";
	private double doubleChange = 0.1;
	private float floatChange = 24e-4f;
	private long longChange = 44444;
	private Date dateChange;
	private File fileChange;
	
	private static int index = 0;
	
	
	/**
	 * @params.include intChange,stringChange,doubleChange,longChange,floatChange,dateChange,fileChange
	 *
	 */
	public void testAddRunProperty(){
		try {
			addRunProperty("intChange", intChange);
			addRunProperty("stringChange", stringChange);
			addRunProperty("doubleChange", doubleChange);
			addRunProperty("floatChange", floatChange);
			addRunProperty("longChange", longChange);
			addRunProperty("dateChange", dateChange);
			addRunProperty("fileChange", fileChange);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @params.include intChange,stringChange,doubleChange,longChange,floatChange,dateChange,fileChange
	 *
	 */
	public void testAddSummary(){
		try {
			Summary.getInstance().setProperty("intChange", ""+intChange);
			Summary.getInstance().setProperty("stringChange", stringChange);
			Summary.getInstance().setProperty("doubleChange", ""+doubleChange);
			Summary.getInstance().setProperty("floatChange", ""+floatChange);
			Summary.getInstance().setProperty("longChange", ""+longChange);
			Summary.getInstance().setProperty("dateChange", ""+dateChange);
			Summary.getInstance().setProperty("fileChange", ""+fileChange);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws Exception 
	 * @params.exclude intChange,stringChange,doubleChange,longChange,floatChange,dateChange,fileChange
	 *
	 */
	public void testAllValues() throws Exception{
		addPropertyToSummaryAndReport("intToCheck", intValue, INTEGER_REFERENCE);
		addPropertyToSummaryAndReport("doubleToCheck", doubleParameter, DOUBLE_REFERENCE);
		addPropertyToSummaryAndReport("stringToCheck", string, STRING_REFERENCE);
		addPropertyToSummaryAndReport("longToCheck", longParameter, LONG_REFERENCE);
		addPropertyToSummaryAndReport("floatToCheck", floatParameter, FLOAT_REFERENCE);
		addPropertyToSummaryAndReport("dateToCheck", date, DATE_REFERENCE);
		addPropertyToSummaryAndReport("fileToCheck", file, FILE_REFERENCE);
	}
	
	/**
	 * @throws Exception 
	 * @params.include string
	 *
	 */
	public void testScenarioParameters() throws Exception{
		index++;
		addPropertyToSummaryAndReport("stringToCheck"+index, string, STRING_REFERENCE);
	}
	
	/**
	 * add a run property and report it
	 * @param key	key to add
	 * @param value	value for the key
	 * @throws Exception
	 */
	private void addRunProperty(String key, Object value) throws Exception{
		RunProperties.getInstance().setRunProperty(key, ""+value);
		report.report("added run propertry: "+key+"="+value);
	}
	
	/**
	 * add property to Summary (for regression testing)
	 * 
	 * @param key
	 * @param value
	 * @param reference
	 * @throws Exception
	 */
	private void addPropertyToSummaryAndReport(String key,Object value, String reference) throws Exception{
		value = (value == null)? "" : value.toString();
		Summary.getInstance().setProperty(key, value.toString());
		report.report(reference+value);
	}
	
	/**
	 * @params.exclude intChange,stringChange,doubleChange
	 *
	 */
	public void testIntValue(){
		report.report("Integer = "+intValue);
	}
	
	/**
	 * @params.exclude intChange,stringChange,doubleChange
	 *
	 */
	public void testDoubleValue(){
		report.report("Double = "+doubleParameter);
	}
	
	/**
	 * @params.exclude intChange,stringChange,doubleChange
	 *
	 */
	public void testStringValue(){
		report.report("String = "+string);
	}
	
	/**
	 *
	 */
	public void testFileValue(){
		report.report("File = "+file.getAbsolutePath());
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public double getDoubleParameter() {
		return doubleParameter;
	}

	public void setDoubleParameter(double doubleParameter) {
		this.doubleParameter = doubleParameter;
	}

	public double getDoubleChange() {
		return doubleChange;
	}

	public void setDoubleChange(double doubleChange) {
		this.doubleChange = doubleChange;
	}

	public int getIntChange() {
		return intChange;
	}

	public void setIntChange(int intChange) {
		this.intChange = intChange;
	}

	public String getStringChange() {
		return stringChange;
	}

	public void setStringChange(String stringChange) {
		this.stringChange = stringChange;
	}

	public float getFloatParameter() {
		return floatParameter;
	}

	public void setFloatParameter(float floatParameter) {
		this.floatParameter = floatParameter;
	}

	public long getLongParameter() {
		return longParameter;
	}

	public void setLongParameter(long longParameter) {
		this.longParameter = longParameter;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public float getFloatChange() {
		return floatChange;
	}

	public void setFloatChange(float floatChange) {
		this.floatChange = floatChange;
	}

	public long getLongChange() {
		return longChange;
	}

	public void setLongChange(long longChange) {
		this.longChange = longChange;
	}

	public Date getDateChange() {
		return dateChange;
	}

	public void setDateChange(Date dateChange) {
		this.dateChange = dateChange;
	}

	public File getFileChange() {
		return fileChange;
	}

	public void setFileChange(File fileChange) {
		this.fileChange = fileChange;
	}

}
