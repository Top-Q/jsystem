package com.aqua.sanity;

import utils.ScenarioUtils;
import jsystem.framework.TestProperties;
import jsystem.framework.fixture.RootFixture;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;

public class ParametersFunctionality extends JSysTestCaseOld {
	public ParametersFunctionality(){
		super();
		setFixture(CreateEnvFixtureOld.class);
		setTearDownFixture(RootFixture.class);
	}
	
	/**
	 * Check that simple parameter set is working
	 * change the Count ot 6 and check it is print to the 
	 * reporter.
	 */
	@TestProperties(name = "5.2.4.23 Run parameters type and verify that they were used during run")
	public void testBasic() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "General", "Count", "6",false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "count: 6");
		
	}
	
	/**
	 * Check that string array parameter set is working
	 * first - check that the default String array parameter in the GUI is : {"15","10","1979"}
	 * second - change the parameters to: {"roey","oren","aqua"} , 
	 * and check that the test got it in the reporter.
	 */
	@TestProperties(name = "Run a test with a spesific String array Param values")
	public void testStringArray() throws Exception{
		jsystem.launch();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testStringArrayParameters", "StringArrayParameters", true);
		
		//check that the default String array parameter in the GUI is : {"15","10","1979"}
		String value = jsystem.getParameterValue("Array", 1, "General");
		if(value.equals("15;10;1979")){
			report.report("The default value is shown properly in the UI",true);
		}else{
			report.report("The default value is not shown properly in the UI",false);
		}
		
		//change the parameters to: {"roey","oren","aqua"} and check that the test got it in the reporter.
		jsystem.setTestParameter(1, "General", "Array", "roey;oren;aqua",false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "first vlaue in the array is : roey");
		jsystem.checkXmlTestAttribute(1, "steps", "second vlaue in the array is : oren");
		jsystem.checkXmlTestAttribute(1, "steps", "third vlaue in the array is : aqua");
	}
	
	
	/**
	 * Check that simple parameter set is working on string param,
	 * change the cliCommand ot 'ddd' and check it is print to the 
	 * reporter.
	 */
	@TestProperties(name = "5.2.4.24 Run a test with a spesific String Param value")
	public void testStringParam() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "General", "CliCommand", "ddd",false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "cliCommand: ddd");
		
	}

	/**
	 * Check that simple parameter set is working on long param,
	 * change the numberOfPackets ot '66' and check it is print to the 
	 * reporter.
	 */
	@TestProperties(name = "5.2.4.25 Run a test with a spesific Long param value")
	public void testLongParam() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "General", "NumberOfPackets", "30000" , true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "numberOfPackets: 30000");
		
	}

	/**
	 * Check that simple parameter set is working on float param,
	 * change the rate ot '0.9' and check it is print to the 
	 * reporter.
	 */
	@TestProperties(name = "5.2.4.26 Run a test with a spesific Float param value")
	public void testFloatParam() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "General", "Rate", "0.9",false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "rate: 0.9");
		
	}
	/**
	 * Check that simple parameter set is working on double parameter. <br>
	 * change the value of the double parameter <i>tolerance</i> from 0.1 to
	 * 0.4, and assert that it is printed to the report.<br>
	 */
	@TestProperties(name = "5.2.4.27 Run a test with a spesific Double param value")
	public void testDoubleParamAndSection() throws Exception{
		jsystem.launch();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "tolerance", "Tolerance", "0.4",false);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "tolerance: 0.4");
		
	}
	
	/**
	 * Check that simple parameter set is working on float param,
	 * change the rate ot '0.9' then change it to 'ddd' (it should be cenceled)
	 * then check '0.9' is print to the reporter.
	 */
	@TestProperties(name = "5.2.4.28 Assign wrong value to a float param")
	public void testWrongParamTypeForFloat() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "General", "Rate", "0.9",false);
		Thread.sleep(1000);
		jsystem.setTestParameter(1, "General", "Rate", "ddd",false);
		jsystem.waitForMessage("Warning");
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "rate: 0.9");
		
	}
	
	/**
	 * Check the support of parameter that is enum type.
	 * Check that the set of value is valid after refresh.
	 */
	public void testEnumParam() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "General", "EnumValue", "VALUE3",true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "value: VALUE3");
		
		jsystem.refresh();
		jsystem.initReporters();
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "value: VALUE3");
		
		
	}
	
	/**
	 * Check the support of parameter that is enum type.
	 * were the enum is defined in test class
	 */
	public void testEnumParamInClass() throws Exception{
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testAllParameters", "ParameterTest", true);
		jsystem.setTestParameter(1, "SecondEnumSec", "SecondEnum", "v2",true);
		jsystem.play();
		jsystem.waitForRunEnd();
		jsystem.checkXmlTestAttribute(1, "steps", "MyEnum: v2");		
	}

	/**
	 * Open a test and verify that parameters from the code are displayed in
	 * the tests parameters tab 
	 * 
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.4.13 Check that parameters from the code are found in the runner")
	public void testParamsFromTheCodeExist() throws Exception {
		String[]params= new String[]{"Int1","Int2","Int3","str1","str2","str3","bool1","bool2","bool3"};
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithAllParameters", "ParamaetersHandlingBase", true);
		
		for(int i=0; i< params.length ; i++){
			if (jsystem.verifyParameterseExist(params[i],1 , "General") == "false"){
				assertTrue("Parameter "+params[i]+" was not found on the testWithAllParameters test",true);	
			}
		}
	}

	/**
	 * This method open a test that all his parameters are
	 * sorted in section
	 * The method verify that all parameters are located in there right
	 * sections
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.4.19 Check that parameters from the code sorted to sections")
	public void testCheckParametersSectionFunctionality() throws Exception {
		
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithAllParametersSections", "ParamaetersIncludeExclude", true);
		
		//Checking that all parameters are in sections
		for(int i=1; i<= 4 ; i++){
			jsystem.refresh();
			if (jsystem.verifyParameterseExist("Int"+i,1 , "int") == "false"){
				assertTrue("Parameter Int"+i+" was not found on the testWithAllParameters test",true);	
			}
			if (jsystem.verifyParameterseExist("Bool"+i,1 , "bool") == "false"){
				assertTrue("Parameter Bool"+i+" was not found on the testWithAllParameters test",true);	
			}
			if (jsystem.verifyParameterseExist("Dob"+i,1 , "double") == "false"){
				assertTrue("Parameter Dob"+i+" was not found on the testWithAllParameters test",true);	
			}
			if (jsystem.verifyParameterseExist("Flo"+i,1 , "float") == "false"){
				assertTrue("Parameter Flo"+i+" was not found on the testWithAllParameters test",true);	
			}
			if (jsystem.verifyParameterseExist("Str"+i,1 , "string") == "false"){
				assertTrue("Parameter Str"+i+" was not found on the testWithAllParameters test",true);	
			}

		}

	}
	/**
	 * This test was designed to check the @params.include and @params.exclude options
	 * @return
	 * @throws Exception
	 */
	@TestProperties(name = "5.2.4.17-18 Parameters include exclude functionality")
	public void testParametersIncludeExcludeFunctionality() throws Exception {
		String[]paramsSelected= new String[]{"flo2","dob3","bool4","str8","integer7"};
		String[]paramsNotSelected= new String[]{"flo3","dob2","bool5","str6","integer1"};

		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithInclueParameters", "ParamaetersHandlingSection", true);
		jsystem.addTest("testWithExcludeParameters", "ParamaetersHandlingSection", true);
		
		for(int i=0; i< paramsSelected.length ; i++){
			//Checking that parameters that were suppose to be included are there
			if (jsystem.verifyParameterseExist(paramsSelected[i],1 , "General") == "false"){
				assertTrue("Parameter "+paramsSelected[i]+" was include and not found",true);	
			}

			//Checking that parameters that were not suppose to be included are not there 
			if (jsystem.verifyParameterseExist(paramsNotSelected[i],1 , "General") == "true"){
				assertTrue("Parameter "+paramsNotSelected[i]+" was not included and still was found",true);	
			}

			//Checking that parameters that were suppose to be exceluded are not there
			if (jsystem.verifyParameterseExist(paramsSelected[i],2 , "General") == "true"){
				assertTrue("Parameter "+paramsSelected[i]+" was not exclused and still found",true);	
			}

			//Checking that parameters that were not exceluded are there
			if (jsystem.verifyParameterseExist(paramsNotSelected[i],2 , "General") == "false"){
				assertTrue("Parameter "+paramsNotSelected[i]+" was not excluded and still did not exist",true);	
			}

		}
		
	}
	
	/**
	 */
	@TestProperties(name = "5.2.4.17-18 Parameters include exclude with new line and doclet")
	public void testParametersIncludeExcludeWithNewLineAndDoclet() throws Exception {
		String[]paramsSelected= new String[]{"Flo2","Dob3","Bool4","Str8","Integer7"};
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithInclueParametersNewLine", "ParamaetersHandlingSection", true);
		jsystem.addTest("testWithInclueParametersNewLineAndDoclet", "ParamaetersHandlingSection", true);
		for(int i=0; i< paramsSelected.length ; i++){			
			jsystem.verifyParameterIndexAndEditability(paramsSelected[i],1 , "General",i, true);
		}
		for(int i=0; i< paramsSelected.length ; i++){			
			jsystem.verifyParameterIndexAndEditability(paramsSelected[i],2 , "General",i, true);
		}
	}
	
	/**
	 * 
	 */
	public void testParametersInScenario() throws Exception {
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithInclueParametersNewLine", "ParamaetersHandlingSection", true);
		jsystem.addTest("testWithInclueParametersNewLineAndDoclet", "ParamaetersHandlingSection", true);
		report.step("update parameters in tests");
		jsystem.setTestParameter(1, "General", "Flo2", "10",false);
		jsystem.setTestParameter(2, "General", "Flo2", "20",false);
		report.step("Select scenario and update a parameter");
		jsystem.setTestParameter(0, "General", "Integer7", "130",false,true);
		report.step("Make sure only selected parameters where updated");
		assertEquals("10.0",jsystem.getParameterValue("Flo2",1,"General"));
		assertEquals("130",jsystem.getParameterValue("Integer7",1,"General"));
		assertEquals("20.0",jsystem.getParameterValue("Flo2",2,"General"));
		assertEquals("130",jsystem.getParameterValue("Integer7",2,"General"));
		jsystem.play();
		jsystem.waitForRunEnd();
		assertEquals("10.0",getRunProperties().getProperty("testWithInclueParametersNewLine_flo2"));
		assertEquals("1.0",getRunProperties().getProperty("testWithInclueParametersNewLine_dob3"));
		assertEquals("130",getRunProperties().getProperty("testWithInclueParametersNewLine_integer7"));
		assertEquals("20.0",getRunProperties().getProperty("testWithInclueParametersNewLineAndDoclet_flo2"));
		assertEquals("1.0",getRunProperties().getProperty("testWithInclueParametersNewLineAndDoclet_dob3"));
		assertEquals("130",getRunProperties().getProperty("testWithInclueParametersNewLineAndDoclet_integer7"));		
	}

	/**
	 * 
	 */
	public void testParametersInScenarioWithCancel() throws Exception {
		jsystem.launch();
//		jsystem.cleanCurrentScenario();
		String scenarioName = jsystem.getCurrentScenario();
		ScenarioUtils.createAndCleanScenario(jsystem, scenarioName);
		jsystem.addTest("testWithInclueParametersNewLine", "ParamaetersHandlingSection", true);
		jsystem.addTest("testWithInclueParametersNewLineAndDoclet", "ParamaetersHandlingSection", true);
		report.step("update parameters in tests");
		jsystem.setTestParameter(1, "General", "Flo2", "10",false);
		jsystem.setTestParameter(2, "General", "Flo2", "20",false);
		report.step("Select scenario and update a parameter");
		jsystem.setTestParameter(0, "General", "Integer7", "130",false,true,false);
		report.step("Make sure only selected parameters where updated");
		assertEquals("10.0",jsystem.getParameterValue("Flo2",1,"General"));
		assertEquals("1",jsystem.getParameterValue("Integer7",1,"General"));
		assertEquals("20.0",jsystem.getParameterValue("Flo2",2,"General"));
		assertEquals("1",jsystem.getParameterValue("Integer7",2,"General"));
		jsystem.play();
		jsystem.waitForRunEnd();
		assertEquals("10.0",getRunProperties().getProperty("testWithInclueParametersNewLine_flo2"));
		assertEquals("1.0",getRunProperties().getProperty("testWithInclueParametersNewLine_dob3"));
		assertEquals("1",getRunProperties().getProperty("testWithInclueParametersNewLine_integer7"));
		assertEquals("20.0",getRunProperties().getProperty("testWithInclueParametersNewLineAndDoclet_flo2"));
		assertEquals("1.0",getRunProperties().getProperty("testWithInclueParametersNewLineAndDoclet_dob3"));
		assertEquals("1",getRunProperties().getProperty("testWithInclueParametersNewLineAndDoclet_integer7"));
	}

}
