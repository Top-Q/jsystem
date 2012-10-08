package regression.generic.testlistener;

import junit.framework.SystemTestCase;

/**
 * 
 * @author goland
 *
 */
public class TestListenerTestCase extends SystemTestCase{

	private MyTestListener myTestListener;
	public TestListenerTestCase(){
        super();
    }
    
	public void setUp() throws Exception {
		myTestListener = (MyTestListener)system.getSystemObject("testListener");
	}
	
	public void test1() throws Exception{
        
    }
	
	public void test2() throws Exception{
        throw new Exception();
    }

	public void test3() throws Exception{
        assertTrue(false);
    }

	public void testListenerWasInvoked() throws Exception{
		assertTrue(myTestListener.endWasCalled);
		assertTrue(myTestListener.startWasCalled);
		assertTrue(myTestListener.errorWasCalled);
		assertTrue(myTestListener.failWasCalled);
	}	
}

