package regression.errorhandling;

import junit.framework.SystemTestCase;

/**
 * @author goland
 */
public class TestWithParameterWithNoGetter extends SystemTestCase {

	private String param;
	
	public void testDummyTest(){	
	}
	
	public String[] getParamOptions(){
		return new String[]{"1","2"};
	}
	
	public void setParam(String a){
		param = a;
	}

	public String getParam(){
		return param;
	}

}