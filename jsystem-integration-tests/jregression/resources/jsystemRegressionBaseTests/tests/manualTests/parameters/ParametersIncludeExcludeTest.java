package manualTests.parameters;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class ParametersIncludeExcludeTest extends SystemTestCase {
	int intParam = 64;
	String stringParam = "test string";
	boolean boolParam = true;
	
	/**
	 * Test the parameters feature
	 * @params.include
	 */
	@TestProperties(name = "check that NO parameters are visible")
	public void testEmptyInclude(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
	}
	
	/**
	 * Test the parameters feature
	 * @params.exclude
	 */
	@TestProperties(name = "check that ALL parameters are visible")
	public void testEmptyExclude(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
	}
	
	/**
	 * Test the parameters feature
	 * @params.include intParam,blabla
	 */
	@TestProperties(name = "check that one parameter is visible")
	public void testOneParamAndGibrish(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
	}
	
	/**
	 * Test the parameters feature
	 * @params.include intParam,stringParam,boolParam
	 */
	@TestProperties(name = "check that ALL parameters are visible")
	public void testAllParamsInclude(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
		
		report.addProperty("intParam","55");
		report.addProperty("stringParam","myString");
		report.addProperty("boolParam","true");
	}
	
	
	
	/**
	 * Test the parameters feature
	 */
	@TestProperties(name = "Annotation - check that NO parameters are visible" ,paramsInclude={""})
	public void testEmptyIncludeAnnotation(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
	}
	
	/**
	 * Test the parameters feature
	 */
	@TestProperties(name = "Annotation - check that ALL parameters are visible", paramsExclude={""})
	public void testEmptyExcludeAnnotation(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
	}
	
	/**
	 * Test the parameters feature
	 */
	@TestProperties(name = "Annotation - check that one parameter is visible", paramsInclude={"intParam","blabla"})
	public void testOneParamAndGibrishAnnotation(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
	}
	
	/**
	 * Test the parameters feature
	 */
	@TestProperties(name = "Annotation - check that ALL parameters are visible", paramsInclude={"intParam","stringParam","boolParam"})
	public void testAllParamsIncludeAnnotation(){
		report.report("intParam = "+intParam);
		report.report("stringParam = "+stringParam);
		report.report("boolParam = "+boolParam);
		
		report.addProperty("intParam","55");
		report.addProperty("stringParam","myString");
		report.addProperty("boolParam","true");
	}
	
	

	public boolean isBoolParam() {
		return boolParam;
	}
	
	/**
	 * @section MySection
	 * @param boolParam
	 */
	public void setBoolParam(boolean boolParam) {
		this.boolParam = boolParam;
	}

	public int getIntParam() {
		return intParam;
	}

	/**
	 * @section MySection
	 * @param intParam
	 */
	public void setIntParam(int intParam) {
		this.intParam = intParam;
	}

	public String getStringParam() {
		return stringParam;
	}
	
	/**
	 * @section MySection
	 * @param stringParam
	 */
	public void setStringParam(String stringParam) {
		this.stringParam = stringParam;
	}
	
}
