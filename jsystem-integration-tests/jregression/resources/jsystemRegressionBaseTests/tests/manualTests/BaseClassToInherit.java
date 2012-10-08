package manualTests;

import junit.framework.SystemTestCase;

public class BaseClassToInherit extends SystemTestCase {

	boolean checkSyslog = true;

	
	public void  testBoolean() throws Exception{
		
		if (checkSyslog){
			report.report("good");			
		}
		else{
			report.report("bad");
			
		}
	}
	
	public boolean isCheckSyslog() {
		return checkSyslog;
	}
	/**
     * Execute syslog validation
     * 
     * @section validations
     */
	public void setCheckSyslog(boolean checkSyslog) {
		this.checkSyslog = checkSyslog;
	}
	


}
