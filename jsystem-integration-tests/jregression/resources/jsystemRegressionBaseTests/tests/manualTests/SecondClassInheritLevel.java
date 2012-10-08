package manualTests;

public class SecondClassInheritLevel extends BaseClassToInherit{
	
	boolean checkContainer = true ;

	public void testSecondLevel() throws Exception{
	report.report("Do nothing");	
	}
	
	public boolean isCheckContainer() {
		return checkContainer;
	}
	/**
     * Execute syslog validation
     * 
     * @section validations
     */
	public void setCheckContainer(boolean checkContainer) {
		this.checkContainer = checkContainer;
	}
	

}
