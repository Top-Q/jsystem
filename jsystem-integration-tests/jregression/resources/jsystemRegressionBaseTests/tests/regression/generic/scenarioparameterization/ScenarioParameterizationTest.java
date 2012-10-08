package regression.generic.scenarioparameterization;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import jsystem.framework.common.CommonResources;
import junit.framework.SystemTestCase;

public class ScenarioParameterizationTest extends SystemTestCase {
	
	private int param1  = 15;	
	private String param2 = "";
	public ScenarioParameterizationTest(){
	}
	
	public void testParameterization1() throws Exception{
		setRollingProperty("testParameterization1param1", ""+param1);
		setRollingProperty("testParameterization1param2", param2);
		setRollingProperty("testParameterization1fixture", ""+getFixtureName());
//		setRollingProperty("comment", ""+getTestInfo().getComment());
//		setRollingProperty("doc", ""+getTestInfo().getDocumentation());
	}
	
	public void testParameterization2() throws Exception{
		setRollingProperty("testParameterization2param1", ""+param1);
		setRollingProperty("testParameterization2param2", param2);
		setRollingProperty("testParameterization2fixture", ""+getFixtureName());
	}

	public void testRunPropertyIsEmpty() throws Exception{
		assertTrue(runPropertiesHasOnlySystemValuesSet(runProperties.getRunProperties()));
		File runPropertiesfile = new File(CommonResources.RUN_PROPERTIES_FILE_NAME);
		assertTrue(runPropertiesfile.delete());

	}
	
	private boolean runPropertiesHasOnlySystemValuesSet(Properties prpts){
		Enumeration<Object> values = prpts.keys();
		while(values.hasMoreElements()){
			String printOut = values.nextElement().toString();
			if(printOut.toLowerCase().startsWith("summary.") || printOut.toLowerCase().startsWith("jsystem.")){
				continue;
			}else{
				return false;
			}
		}
		return true;
	}

	public void testParameterizationFail() throws Exception{
		throw new Exception("TestFailed");
	}

	public void testParameterizationAssert() throws Exception{
		assertTrue(false);
	}

	public int getParam1() {
		return param1;
	}

	public void setParam1(int param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}
	
	private void setRollingProperty(String name,String value) throws Exception {
		int index = -1;
		boolean found = false;
		do {
			index++;
			String propRollingVal = runProperties.getRunProperty(name+"_"+index);
			found = propRollingVal==null;
		}while(!found);
		runProperties.setRunProperty(name+"_"+index, value);
		report.report("Parameter:" + name + "Roling name= " + name+"_"+index+ ". Value:" +value);
		
	}
}
