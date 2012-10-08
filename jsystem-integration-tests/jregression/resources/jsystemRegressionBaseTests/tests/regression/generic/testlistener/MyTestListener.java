package regression.generic.testlistener;

import jsystem.framework.system.SystemObjectImpl;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

public class MyTestListener extends SystemObjectImpl implements TestListener{

	public boolean errorWasCalled;
	public boolean failWasCalled;
	public boolean startWasCalled;
	public boolean endWasCalled;
	
	public void addError(Test arg0, Throwable arg1) {
		errorWasCalled = true;
	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		failWasCalled = true;
	}

	public void endTest(Test arg0) {
		endWasCalled = true;
	}

	public void startTest(Test arg0) {
		startWasCalled = true;
	}
	
}
