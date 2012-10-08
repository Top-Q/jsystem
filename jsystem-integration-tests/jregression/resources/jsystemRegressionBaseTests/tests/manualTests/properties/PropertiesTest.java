package manualTests.properties;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class PropertiesTest extends SystemTestCase {
	private int keyGenerator  = 15;
	private int keyLength = 20;
	private int valLength = 20;
	
	@TestProperties(name = "simple test, add two properties")
	public void testProperties1() throws Exception{
		report.addProperty("key1", "value1");
		report.addProperty("key2", "value2");
	}
	
	@TestProperties(name = "assigning duplicate value for same key, the first should not appear")
	public void testProperties2() throws Exception{
		report.addProperty("key1", "should not appear");
		report.addProperty("key1", "should appear");
	}
	
	@TestProperties(name = "property generator, add ${keyGenerator} properties to test")
	public void testPropertiesKeyGenerator() throws Exception{
		for (int i=0 ; i<keyGenerator ; i++){
			report.addProperty("key_num"+i, "val"+i);
		}
	}
	
	@TestProperties(name = "create long named key")
	public void testPropertiesLongNameKey() throws Exception{
		report.addProperty("a long name key to test the boundries of all the involved objects related to properties", "value");
	}
	
	@TestProperties(name = "create long named value")
	public void testPropertiesLongNameValue() throws Exception{
		report.addProperty("key", "a long name value to test the boundries of all the involved objects related to properties");
	}
	
	@TestProperties(name = "create long named key & value")
	public void testPropertiesLongNameKeyAndValue() throws Exception{
		report.addProperty("a long name key to test the boundries of all the involved objects related to properties",
						   "a long name value to test the boundries of all the involved objects related to properties");
	}
	
	@TestProperties(name = "create a property with key of length ${keyLength} , val of length ${valLength}")
	public void testPropertiesLengthKeyAndValue() throws Exception{
		int index=0;
		String key = "";
		for (int i=0; i<keyLength ; i++){
			key+=index;
			index = (index+1)%10;
		}
		index=0;
		String val = "";
		for (int i=0; i<valLength ; i++){
			val+=index;
			index = (index+1)%10;
		}
		report.addProperty(key+1,val);
	}
	
	@TestProperties(name = "create properties with special charachters - All should fail")
	public void testPropertiesSpecialCharacters() throws Exception{
		report.addProperty("test#","val1");
		report.addProperty("test%","val1");
		report.addProperty("test&","val1");
		report.addProperty("test","val#");
		report.addProperty("test","val%");
		report.addProperty("test","val&");
	}
	
	public int getKeyGeneraor() {
		return keyGenerator;
	}

	public void setKeyGeneraor(int keyGeneraor) {
		this.keyGenerator = keyGeneraor;
	}

	public int getKeyLength() {
		return keyLength;
	}

	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}

	public int getValLength() {
		return valLength;
	}

	public void setValLength(int valLength) {
		this.valLength = valLength;
	}

}
