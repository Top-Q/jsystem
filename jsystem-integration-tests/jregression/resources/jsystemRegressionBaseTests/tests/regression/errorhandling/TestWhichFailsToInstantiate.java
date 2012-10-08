package regression.errorhandling;

import junit.framework.SystemTestCase;

/**
 * A test class which the runner should not be able 
 * to instantiate.
 * @author goland
 */
public class TestWhichFailsToInstantiate extends SystemTestCase {

	int i = justAMethodThatThrowsException();
	
	public void testDummyTest(){	
	}
	
	
	private static int justAMethodThatThrowsException(){
		throw new RuntimeException("Dummy error");
	}
}