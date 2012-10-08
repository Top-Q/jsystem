package com.aqua.sanity;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.GeneralEnums.RunMode;
import jsystem.framework.TestProperties;
import jsystem.framework.scenario.ParametersManager;
import jsystem.framework.scenario.ParametersManager.Reference;
import jsystem.framework.scenario.ScenarioParameter;
import jsystem.framework.sut.SutImpl;
import jsystem.utils.StringUtils;
import utils.ScenarioUtils;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.ActivateRunnerFixture;

/**
 * check the reference paramatization feature
 * 
 * this class works with the RegressionBaseTest "ParametersByReference" test
 * and with sut "testReferenceSut.xml"
 * 
 * @author nizanf
 *
 */
public class ReferenceParameters extends JSysTestCaseOld {

	private int intValue = 111;
	private double doublValue = 1.345;
	private String stringValue = "String from Reference";
	private long longValue = 444444;
	private float floatValue = 2e-40f;
	private File file = new File("testReferenceFile.stam");
	private Date date = new Date();
	
	/**
	 * Order of Parameters is:
	 * Int
	 * Double
	 * String
	 * Long
	 * Float
	 * <Date>
	 * File
	 */
	
	/**
	 * keys for Summary properties
	 */
	private String[] keys = new String[] {"intToCheck","doubleToCheck","stringToCheck","longToCheck","floatToCheck"/*,"dateToCheck"*/,"fileToCheck"};
	/**
	 * members with values to check
	 */
	private Object [] expected = new Object[]{intValue,doublValue,stringValue,longValue,floatValue/*,date*/,file};
	/**
	 * name of parameters to get value from (to reference to)
	 */
	private String [] parametersValues = new String[]{"intChange","doubleChange","stringChange","longChange","floatChange"/*,"dateChange"*/,"fileChange"};
	/**
	 * name of parameters waiting for values from reference
	 */
	private String [] referenceParameters = new String[]{"IntValue","DoubleParameter","String","LongParameter","FloatParameter"/*,"Date"*/,"File"};
	
	public ReferenceParameters(){
		super();
		setFixture(ActivateRunnerFixture.class);
	}
	
	public void setUp() throws Exception{
		super.setUp();
		jsystem.setJSystemProperty(FrameworkOptions.RUN_MODE, RunMode.DROP_EVERY_RUN.toString());
		jsystem.launch();
		jsystem.initReporters();
		
	}
	
	private void regularInit() throws Exception{
		ScenarioUtils.createAndCleanScenario(jsystem, "ReferenceParameters");
		jsystem.addTest("testAllValues","ParametersByReference",1, true);
	}
	
	/**
	 * 1) run a test that adds RunProperties (to the run properties file)
	 * 2) run a test that uses reference to the RunProperties and writes the values to the Summary file.
	 * 3) check the Summary file for the correct values
	 * 
	 * @throws Exception
	 */
	@TestProperties(name="check if value is read from RunProperties by reference")
	public void testRunPropertiesReference() throws Exception{
		regularInit();
		jsystem.addTest(0,"testAddRunProperty","ParametersByReference", true);
		setRegularParametersValues(1,true);
		
		setAllParametersValues(Reference.RUN,2);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		checkAllParametersValues();
	}

	@TestProperties(name="check if value is read from Summary by reference")
	public void testSummaryReference() throws Exception{
		regularInit();
		jsystem.addTest(0,"testAddSummary","ParametersByReference", true);
		setRegularParametersValues(1,true);
		
		setAllParametersValues(Reference.SUMMARY,2);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		checkAllParametersValues();
	}

	@TestProperties(name="check if value is read from the sut by reference")
	public void testSutReference() throws Exception{
		regularInit();
		String sut = "testReferenceSut.xml";
		jsystem.changeSut(sut); // add sut to base
		
		setAllParametersValues(Reference.SUT,1);
		
		jsystem.play();
		jsystem.waitForRunEnd();
		
		final String fullPath = envController.getRegressionBaseTestsFolder() +File.separator+"classes/sut/"+sut;
		SutImpl sutFile = new SutImpl();
		sutFile.setSutXml(new File(fullPath));
		Object[] values = new Object[parametersValues.length];
		for (int i=0 ; i<values.length ; i++){
			values[i] = sutFile.getValue("/sut/"+parametersValues[i]+"/text()").replace("\\\\","\\");
		}
		checkAllParametersValues(values.length,values);
	}

	@TestProperties(name="check if value is created randomaly for int and double - range")
	public void testRandomRangeReference() throws Exception{
		regularInit();
		setAllParametersValues(Reference.RANDOM,1,"4:10","0:1.0");
		
		jsystem.play();
		jsystem.waitForRunEnd();
		checkExecutedAndPassed(1, 1);
	}
	
	@TestProperties(name="check if value is created randomaly for int,double and string - group")
	public void testRandomGroupReference() throws Exception{
		regularInit();
		setAllParametersValues(Reference.RANDOM,1,"(1;2;4;8)","(1.5;2.5;3.5)","(\"first\";\"second\";\"third\")");
		
		jsystem.play();
		jsystem.waitForRunEnd();
		checkExecutedAndPassed(1, 1);
	}

	@TestProperties(name="check if value is read from the scenario parameters by reference")
	public void testScenarioReference() throws Exception{
		regularInit();
		setAllParametersValues(Reference.SCENARIO,1);

		for (int i=0 ; i<parametersValues.length ; i++){
			if (expected[i] == null){
				continue;
			}
			String param = StringUtils.firstCharToLower(parametersValues[i]);
			jsystem.setTestParameter(0, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, param, expected[i].toString(), false,true,true);
		}
		
		jsystem.play();
		jsystem.waitForRunEnd();
		checkAllParametersValues();
	}
	
	@TestProperties(name="check different scenario parameter levels")
	public void testScenarioReferenceLong() throws Exception{
		report.step("Setup 3 level scenario with scenario references and values");
		
		String folder = "Reference";
		String level = "Ref - level";
		// create level 0, add test and set a scenario parameter and value for it
		ScenarioUtils.createAndCleanScenario(jsystem, folder+File.separator+level+2);
		jsystem.addTest("testScenarioParameters","ParametersByReference",1, true);
		setAllParametersValues(Reference.SCENARIO,1,null,null,"StringRef"); // String parameter set
		
		
		jsystem.setTestParameter(0, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "1/1", false,false,false);
		
		ScenarioUtils.createAndCleanScenario(jsystem, folder+File.separator+level+1);
		jsystem.addTest(level+2 ,folder,2, true);
		jsystem.setTestParameter(1, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "1/2", false,false,false);
		jsystem.setTestParameter(3, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "2/2", false,false,false);
		
		ScenarioUtils.createAndCleanScenario(jsystem, folder+File.separator+level+0);
		jsystem.addTest(level+1 ,folder,2, true);
		jsystem.setTestParameter(2, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "1/4", false,false,false);
		jsystem.setTestParameter(4, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "2/4", false,false,false);
		jsystem.setTestParameter(7, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "3/4", false,false,false);
		jsystem.setTestParameter(9, ScenarioParameter.SCENARIO_PARAMETERS_SECTION, "StringRef", "4/4", false,false,false);
		
		report.step("Check that scenario reference parameters are different per root");
		
		jsystem.play();
		jsystem.waitForRunEnd();
		Properties prop = jsystem.getSummaryProperties();
		for (int i=0 ; i<4 ; i++){
			checkSingleParameter("stringToCheck"+(i+1), (i+1)+"/4", prop);
		}
		
		jsystem.selectSenario(folder+File.separator+level+1);
		jsystem.play();
		jsystem.waitForRunEnd();
		prop = jsystem.getSummaryProperties();
		for (int i=0 ; i<2 ; i++){
			checkSingleParameter("stringToCheck"+(i+1), (i+1)+"/2", prop);
		}
		
		jsystem.selectSenario(folder+File.separator+level+2);
		jsystem.play();
		jsystem.waitForRunEnd();
		prop = jsystem.getSummaryProperties();
		checkSingleParameter("stringToCheck1", "1/1", prop);
		
	}
	
	private void setAllParametersValues(Reference reference, int testIndex, Object... values) throws Exception{
		String ref = ParametersManager.REFERENCE_STRING+reference+":";
		for (int i=0 ; i<values.length ; i++){
			if (values[i] == null){
				continue;
			}
			jsystem.setTestParameter(testIndex, "General", referenceParameters[i], ref+values[i].toString()+"}", false);
		}
	}
	
	private void setAllParametersValues(Reference reference, int testIndex) throws Exception{
		setAllParametersValues(reference, testIndex,(Object[])parametersValues);
	}
	
	private void setRegularParametersValues(int testIndex,boolean toUpperCase) throws Exception{
		for (int i=0 ; i<parametersValues.length ; i++){
			if (parametersValues[i] == null){
				continue;
			}
			String value = toUpperCase? StringUtils.firstCharToUpper(parametersValues[i]) : parametersValues[i];
			jsystem.setTestParameter(testIndex, "General", value, expected[i].toString(), false);
		}
	}
	
	private void checkAllParametersValues() throws Exception{
		checkAllParametersValues(expected.length,expected);
	}
	
	private void checkAllParametersValues(int num,Object... expected) throws Exception{
		Properties serverProperties = jsystem.getSummaryProperties();
		for (int i=0 ; i<keys.length && i<num ; i++){
			checkSingleParameter(keys[i], expected[i], serverProperties);
		}
		
	}
	
	private void checkSingleParameter(String parameterName, Object expectedValue,Properties serverProperties) throws Exception{
		Object o = serverProperties.get(parameterName);
		if (parameterName.equals("fileToCheck")){
			int index = o.toString().lastIndexOf(File.separator);
			o = o.toString().substring(index+1);
		}
		analyzer.setTestAgainstObject(o);
		analyzer.analyze(new CompareValues(expectedValue.toString()),false,false);
	}
	
	private void checkExecutedAndPassed(int executed, int passed) throws Exception{
		jsystem.checkNumberOfTestExecuted(executed);
		jsystem.checkNumberOfTestsPass(passed);
	}

}
