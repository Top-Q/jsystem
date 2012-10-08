package manualTests.parameters;


import junit.framework.SystemTestCase;
/**
 * MetaLink dongle setup and configuration tests.
 */
public class MetaLinkBclDongleOperationsCleaned extends SystemTestCase {	
	
	
	
    private String dongleInstance = "sta1";  //st1
	private int    channel=36;
	
/*******************************************************************************************
*                             Util Functions
*******************************************************************************************/	
	/**
	 * Util function that return metalink dongle's target.
	 */	

	/**
	 * @params.include dongleInstance
	 * @throws Exception
	 */
	public void testRestartPortForwarder() throws Exception{

	}
	

	
/*******************************************************************************************
*                             Dongle Util Functions
*******************************************************************************************/	

		
	/**
	 * Reconfigure dongle's parameters.
	 * 
	 *
	 * @params.include dongleInstance,restartTimeout,wlan_bridging
	 */	
	public void testRebootDongle() throws Exception {
	

	}
	/**
	 * Reconfigure dongle's parameters.
	 * 
	 *
	 * @params.include dongleInstance,restartTimeout,wlan_bridging
	 */	

	public void testReocnfigureDongle() throws Exception {

	}
	/**
	 * Confiugre the Dongle's phy parameters.
	 * 
	 *
	 * @params.include dongleInstance
	 */	
	public void testDeleteDongleConfigFile() throws Exception {

	}
	
	
	/**
	 * Gets ChipVar statistics from dongle.
	 * 
	 * @param chipVarStatistics -
	 *            free text with ChipVar statistics names separated with
	 *            semicolumn
	 * @params.include dongleInstance,chipVarStatistics
	 */
	public void testGetChipVarStatistics() throws Exception {

	}
	
	/**
	 * Deploys software release.
	 * 
	 * @parm build - build number i.e "01.04.00.DON2.00".
	 * @param dongle
	 *            instance (AP/Station)
	 * 
	 * The test looks for build folder under releasePath as designated in the
	 * SUT file. The test uses the autoloader script to deploy the the software.
	 * 
	 * @params.include dongleInstance,build,path
	 */
	public void testDeploySoftwareWithPath() throws Exception {

	}
	
	
	
/**                     Phy Parameters                **/
	
	
	/**
	 * Select protocol type: 802.11 n,a,b,g
	 * 
	 *
	 * @params.include dongleInstance,protocoltype
	 */
	public void testConfigureProtocolType() throws Exception {
	
	}	
	/**
	 * Confiugre the Dongle's phy parameters.
	 * 
	 *
	 * @params.include dongleInstance,regulatorydomain
	 */
	public void testConfigureRegularotyDomain() throws Exception {
	}	
	

/*******************************************************************************************
*                             Setters and Getters
*******************************************************************************************/	

	
	
	

	public String getDongleInstance() {
		return dongleInstance;
	}


	public void setDongleInstance(String dongleInstance) {
		this.dongleInstance = dongleInstance;
	}
	public String []  getDongleInstanceOptions() {
		return new String[]{"ap","sta1","sta2","sta3","sta4"};
	}

	public int getChannel() {
		return channel;
	}
	/**
	 * 
	 * @section dongles configuration
	 */

	public void setChannel(int channel) {
		this.channel = channel;
	}



	
}
