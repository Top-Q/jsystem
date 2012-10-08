package manualTests.parameters;

import java.io.File;
import java.text.SimpleDateFormat;

import junit.framework.SystemTestCase;
/**
 * MetaLink dongle setup and configuration tests.
 */
public class MetaLinkBclDongleOperations extends SystemTestCase {	
	
	
	
    /**	Dongle Parameters **/
	
	private String chipVarStatistics = "HOST_TO_MAC_TX_REQUESTS;TX_LEGACY_PACKETS;TX_BEACONS";
	private String build = "02.01.00.Intg0.47";
	private String network_type="Access Point";
	private String ip_lan="10.0.1.1";
	private String ip_wlan="10.0.0.1";
	private String dongleInstance = "sta1";  //st1
	private String tx_dongle="ap";
	private String rx_dongle="sta1";
	private String path = "2.01";
	private boolean config_dongle=true;
	private String nonproc_essid="Metalink";
	private String protocoltype="802.11na 5.2GHz";
	private String protocoltype_sta="802.11n Dual 2.4GHz/5.2Ghz";
	private int    channel=36;
	private boolean ldpc=true;
	private boolean mac_cloning=false;
	private boolean hidden_ssid=false;
	private boolean ap_forwarding=true;
	private boolean long_preambleformulti=false;
	private boolean over_lapping_bss_prot=false;
	private String erp_protection_type="None";
	private String n_protection_type="None";
    private boolean stbc=false;
    private boolean short_cyclicprefix=false;
    private boolean reliable_multi=false;
    private String wlan_bridging="Route";
    private String scan_mode="Passive";
    private String scan_infra_adhoc="Infrastructure";
	private String regulatorydomain="ETSI";
	private String channelbondingmode="40";
	private String upperlowerchannelbonding="USB";
	private String security_mode="Open";
	private boolean wep_encryption=true;
	private String authentication="Open";
	private int wep_key_length=64;
	private int wep_key_index=0;
	private String wep_key_value="12345678";
	private String wpa_enterprise_radius_key="0x00";
	private String wpa_enterprise_radius_user="0x00";
	private String wpa_enterprise_radius_password="0x00";
	private String wpa_globalmode="Personal";
	private String wpa_mode="WPA";
	private String wpa_encapsulation="TKIP";
	private String wpa_personal_psk="12345678";
	private boolean agg_use_bk=true;
	private boolean agg_accept_bk=true;
	private boolean agg_use_vo=true;
	private boolean agg_accept_vo=true;
	private boolean agg_use_vi=true;
	private boolean agg_accept_vi=true;
	private boolean agg_use_be=true;
	private boolean agg_accept_be=true;
	private String min_ht_rate="7";
	private String max_ht_rate="27";
	private int cpu_load_interval=1;
	private int cpu_load_duration=5;
	private String restore_defaults_band="5G"; 
	private String restore_defaults_mode="Route";
	
	/**Iperf Parameters**/
	
	private String iperf_bandwith="20";
	private String iperf_protocol = "UDP";
	private int    iperf_interval = 1;
	private int    iperf_duration = 10;
	private String iperf_output_file = "iperf_output.txt";
	private int    iperf_packet_size = 1440;
	
    public static  boolean excel_setup=false;
	File excel_file=null;
	private String excel_infile="c:\\Test_Automation.xls";
	private String excel_outfile="TestResults";
	public static String excelGlobalOutfile;
	private int    excel_mode=1;
	static int excel_write_row=0;
	static int excel_read_row=0;
	SimpleDateFormat sdf_date = new SimpleDateFormat("_dd_MM_yy_HHmmss");
	
			
	/** Util Parameters **/ 
	
	private long restartTimeout = 60000;
	private boolean debug=false;
	private String testComment;
	private String test_name="Stability";
	private String filename = "";
	private String grepstr = "";
	private String pingDestination = "ap";
	private boolean apInTheWlan = true;
	private boolean sta1InTheWlan = true;
	private boolean sta2InTheWlan = false;
	private boolean sta3InTheWlan = false;
	private String numOfRestartsUntilConnect;
	private String logFileLocation;

	
	
	enum BridgeMode {
		 DONGLE_ROUTE_MODE,
		 RESERVED,
		 DONGLE_BRIDGE_MODE,
		 DONGLE_WDS_MODE,
	}

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
	
	/**
	 * restores defaults, by loading a default profile to the dongle.
	 * @params.include dongleInstance,restartTimeout,wlan_bridging,restore_defaults_band,restore_defaults_mode
	 */
	public void testRestoreDefaults() throws Exception {
	}
	
	/**
	 * Get Wlan Connection Status
	 * @params.include dongleInstance
	 */
	public void testWlanConnectionStatus() throws Exception {
	}
	
	/**
	 * Get Dongle Cpu Load
	 * @params.include dongleInstance,cpu_load_duration,cpu_load_interval
	 */
	public void testDongleCpuLoad() throws Exception {	  		

	}
	
	/**
	 * Read file
	 * @params.include dongleInstance,filename 
	 */
	public void testFileRead() throws Exception {
	}
	
	/**
	 * Grep for a string in file
	 * @params.include dongleInstance,filename,grepstr
	 */  
	public void testFileGrep() throws Exception {
	}
/*******************************************************************************************
*                             Excel Functions
*******************************************************************************************/
	/**
	* Open Excel
	* 
	*
	*  @params.include excel_infile,excel_outfile,excel_mode,test_name
	*/
	public void testSetExcelDriver() throws Exception {

	}   
	/**
	* Save Excel
	* 
	*
	*  @params.include excel_mode
	*/
	public void testSaveOutputExcel() throws Exception {

	}   
	
	public void createExcelTemplate(String name) throws Exception {
	}   

/*******************************************************************************************
*                             Integration Test  Functions
*******************************************************************************************/
	
	/**
	 * Select Network type: AP,Adhoc Station,Infrastructure Station,Test Mac
	 * 
	 *
	 *  @params.include config_dongle,tx_dongle,rx_dongle,debug,protocoltype,protocoltype_sta,ldpc,ap_forwarding,reliable_multi,regulatorydomain,channelbondingmode,channel,iperf_duration,iperf_bandwith,iperf_protocol,iperf_interval,iperf_output_file,iperf_packet_size,authentication,wep_key_length,wep_key_index,wep_key_value,security_mode,,wpa_enterprise_radius_key,wpa_enterprise_radius_user,wpa_enterprise_radius_password,wpa_globalmode,wpa_mode,wpa_encapsulation,wpa_personal_psk,getMax_ht_rateOptions,getMin_ht_rateOptions,agg_accept_be,agg_accept_bk,agg_accept_vi,agg_accept_vo,agg_use_be,agg_use_bk,agg_use_vi,agg_use_vo,min_ht_rate,max_ht_rate
	 */
	public void testIntegrationStabilty() throws Exception {		

		
	}
/*******************************************************************************************
*                             Dongle Configuration Functions
*******************************************************************************************/
		
	/**
	 * The test include all of the dongle configurations
	 * @throws Exception
	 * @params.include dongleInstance,restartTimeout,protocoltype,protocoltype_sta,ldpc,ap_forwarding,reliable_multi,regulatorydomain,channelbondingmode,channel,authentication,wep_key_length,wep_key_index,wep_key_value,security_mode,wpa_enterprise_radius_key,wpa_enterprise_radius_user,wpa_enterprise_radius_password,wpa_globalmode,wpa_mode,wpa_encapsulation,wpa_personal_psk,getMax_ht_rateOptions,getMin_ht_rateOptions,agg_accept_be,agg_accept_bk,agg_accept_vi,agg_accept_vo,agg_use_be,agg_use_bk,agg_use_vi,agg_use_vo,min_ht_rate,max_ht_rate	 	  					
	 * 
	 */	
	public void testConfigDongleMainParams()throws Exception{
	}	
	
	/**
	 * Configure Ip Lan
	 * 
	 *
	 * @params.include dongleInstance,ip_lan
	 */
	public void testConfigureIpLan() throws Exception {	
	}
	
	/**
	 * Configure Ip Wlan
	 * 
	 *
	 * @params.include dongleInstance,ip_wlan
	 */
	public void testConfigureIpWlan() throws Exception {		
	}
	
	/**
	 * Configure NonProc_ESSID
	 * 
	 *
	 * @params.include dongleInstance,nonproc_essid
	 */
	public void testConfigureNonProc_ESSID() throws Exception {		
	}
	/**
	 * Configure Wlan mode : Bridge/Route
	 * 
	 *
	 * @params.include dongleInstance,wlan_bridging
	 */
	public void testConfigureWlanBridging() throws Exception {
	}	
	
	
	/**
	 * Enable/Disable MacCloningEnabled
	 * 
	 *
	 * @params.include dongleInstance,mac_cloning
	 */
	public void testConfigureMacCloning() throws Exception {
	}
		
	/**
	 * Enable/Disable AC_BK_UseAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_use_bk
	 */
	public void testConfigureAgg() throws Exception {	
	}	
	/**
	 * Enable/Disable AC_BK_UseAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_use_bk
	 */
	public void testConfigureAggUSE_BK() throws Exception {
	}
	
	/**
	 * Enable/Disable AC_BE_UseAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_use_be
	 */
	public void testConfigureAggUSE_BE() throws Exception {
	}
	/**
	 * Enable/Disable AC_VO_UseAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_use_vo
	 */
	public void testConfigureAggUSE_VO() throws Exception {
	}	
	/**
	 * Enable/Disable AC_VI_UseAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_use_vi
	 */
	public void testConfigureAggUSE_VI() throws Exception {
	}	
	/**
	 * Enable/Disable AC_BK_AcceptAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_accept_bk
	 */
	public void testConfigureAggAccept_BK() throws Exception {
	}	
	/**
	 * Enable/Disable AC_BE_AcceptAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_accept_be
	 */
	public void testConfigureAggAccept_BE() throws Exception {
	}
	/**
	 * Enable/Disable AC_VO_AcceptAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_accept_vo
	 */
	public void testConfigureAggAccept_VO() throws Exception {
	}
	/**
	 * Enable/Disable AC_VI_AcceptAggregation
	 * 
	 *
	 * @params.include dongleInstance,agg_accept_vi
	 */
	public void testConfigureAggAccept_VI() throws Exception {
	}		
	/**
	 * Select Network type: AP,Adhoc Station,Infrastructure Station,Test Mac
	 * 
	 *
	 * @params.include dongleInstance,network_type
	 */
	public void testConfigureNetworkType() throws Exception {
	}	
	/**
	 * Enable/Disable LDPC
	 * 
	 *
	 * @params.include dongleInstance,ldpc
	 */
	public void testConfigureAdvancedCoding() throws Exception {
		
	}
	/**
	 * Enable/Disable HiddenSSID mode
	 * 
	 *
	 * @params.include dongleInstance,hidden_ssid
	 */
	public void testConfigureHiddenSSID() throws Exception {
	}	
	/**
	 * Enable/Disable APforwarding mode
	 * 
	 *
	 * @params.include dongleInstance,ap_forwarding
	 */
	public void testConfigureApforwarding() throws Exception {
	}		
	/**
	 * Enable/Disable Long Preamble For Multicast mode
	 * 
	 *
	 * @params.include dongleInstance,long_preambleformulti
	 */
	public void testConfigureLongPreambleForMulticast() throws Exception {
	}		
	
	/**
	 * Enable/Disable Short Slot Time mode
	 * 
	 *
	 * @params.include dongleInstance,short_slot_time
	 */
	public void testConfigureLongShortSlotTime() throws Exception {
	}		
	
	/**
	 *Enable/Disable Over lapping BSS Protectection mode
	 * 
	 *
	 * @params.include dongleInstance,over_lapping_bss_prot
	 */
	public void testConfigureOverlappingBSSProtectection() throws Exception {
	}
	
	/**
	 * Select ERP Protection Type: None,RTS/CTS,CTS2Self
	 * 
	 *
	 * @params.include dongleInstance,erp_protection_type
	 */
	public void testConfigureErpProtectionType() throws Exception {

	}	
	
	/**
	 * Select 11n Protection Type: None,RTS/CTS,CTS2Self
	 * 
	 *
	 * @params.include dongleInstance,n_protection_type
	 */
	public void testConfigure11nProtectionType() throws Exception {
	}		
	
	/**
	 * Enable/Disable STBC mode
	 * 
	 *
	 * @params.include dongleInstance,stbc
	 */
	public void testConfigureSTBC() throws Exception {
	}

	/**
	 * Enable/Disable Short CyclicPrefix mode
	 * 
	 *
	 * @params.include dongleInstance,short_cyclicprefix
	 */
	public void testConfigureShortCyclicPrefix() throws Exception {
	}
	/**
	 * Enable/Disable Short Preamble mode
	 * 
	 *
	 * @params.include dongleInstance,short_preamble
	 */
	public void testConfigureShortPreamble() throws Exception {
	}
	/**
	 * Enable/Disable Reliable Multicast mode
	 * 
	 *
	 * @params.include dongleInstance,reliable_multi
	 */
	public void testConfigureReliableMulticast() throws Exception {
	}
	
	/**
	 * Select Scan mode: Passive,Active
	 * 
	 *
	 * @params.include dongleInstance,scan_mode
	 */
	public void testConfigureScanMode() throws Exception {
	}
	
	/**
	 * Select Scan Infrastructure Adhoc mode: Adhoc,Infrastructure,Both
	 * 
	 *
	 * @params.include dongleInstance,scan_infra_adhoc
	 */
	public void testConfigureScanInfrastructureAdhoc() throws Exception {
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
	/**
	 * Configure Channel Bonding mode.
	 * 
	 *
	 * @params.include dongleInstance,channelbondingmode
	 */
	public void testConfigureChannelBonding() throws Exception {
	}
	/**
	 * Configure Dongle's channel.
	 * 
	 *
	 * @params.include dongleInstance,channel
	 */
	public void testConfigureChannel() throws Exception {
	}
	
	/**
	 * Configure Upper/Lower side band.
	 * 
	 *
	 * @params.include dongleInstance,upperlowerchannelbonding
	 */
	public void testConfigureBand() throws Exception {

	}	
	/**
	 * Configure Upper/Lower side band.
	 * 
	 *
	 * @params.include dongleInstance,min_ht_rate,max_ht_rate
	 */
	public void testConfigureMinMaxRates() throws Exception {
	}	
	
	
	/**
	 * Confiugre the Dongle's phy parameters.
	 * 
	 *
	 * @params.include dongleInstance,protocoltype,regulatorydomain,channelbondingmode,channel
	 */
	public void testConfigureDonglePhy() throws Exception {
	}
	
/********************  Security Parameters  ********************/
	
	/**
	 * Confiugre the Dongle's security mode operation.
	 * 
	 *
	 * @params.include dongleInstance,security_mode
	 */
	public void testConfigureDongleSecuritymode() throws Exception {
	}
	
	/**
	 * Confiugre the Dongle's security parameters.
	 * 
	 *
	 * @params.include dongleInstance,authentication,wep_key_length,wep_key_index,wep_key_value
	 */
	public void testConfigureDongleSecurityWep() throws Exception {
	}
	/**
	 * Confiugre the Dongle's security parameters.
	 * 
	 *
	 * @params.include dongleInstance,wpa_globalmode,wpa_mode,wpa_personal_psk,wpa_encapsulation,wpa_enterprise_radius_key,wpa_enterprise_radius_user,wpa_enterprise_radius_password
	 */
	public void testConfigureDongleSecurityWPAandWPA2() throws Exception {
	}
	
	/**
	 * @params.include dongleInstance,logFileLocation
	 * @throws Exception
	 */	
	public void testGetLogSysInfoFile() throws Exception{
	}
	/**
	 * @params.include dongleInstance,logFileLocation
	 * @throws Exception
	 */	
	public void testGetLogDriverCountersFile() throws Exception{		
	}
	/**
	 * @params.include dongleInstance,logFileLocation
	 * @throws Exception
	 */	
	public void testGetLogMessageFile() throws Exception{
	}
	
	/**
	 * @params.include dongleInstance,testComment
	 * @throws Exception
	 */	
	public void testGetDongleCpuLoadFile() throws Exception{
	}
	
	/**
	 * @params.include dongleInstance
	 * @throws Exception
	 */	
	public void testGetDongleLogFiles() throws Exception{
	}
	
	/**
	 * @params.include dongleInstance,logFileLocation
	 * @throws Exception
	 */	
	public void testLoadWlanConfFile() throws Exception{		

	}
	
	
	/***  Pinging Functions  ***/
	 
	/**
	 * @throws Exception 
	 * @include dongleInstance 
	 */	
	public void testPingFromManagementStationToDongle() throws Exception{		

	}
	
	
	
	/**
	 * @throws Exception 
	 * @throws Exception 
	 * @include dongleInstance
	 */	
	public void testPingFromManagementStationToDongleUntilReplay() throws Exception{
	}
	
	
	
	/**
	 * @throws Exception 
	 * @params.include pingDestination,dongleInstance
	 *
	 */
	public void testPingFromManagementStaToManagementSta() throws Exception{
	}

	public void testPingFromManagementStaToManagementStaUntilReplay() throws Exception{
	}
	
	
	
/****
 * 
 * @params.include apInTheWlan,sta1InTheWlan,sta2InTheWlan,sta3InTheWlan,numOfRestarts,wlan_bridging
 */	
	
	
	public void testPingWLANConnectivity() throws Exception{
		
	}
	
	/**
	 * @params.include sta1,sta2,sta3 
	 */
	 public void testRebootDongles() throws Exception{
	}
	 
	public void testMultiReboot() throws Exception{			
	}
	/**
	 * @params.include dongleInstance,wlan_bridging
	 * @throws Exception
	 */
	public void testChangePortforwardingMode() throws Exception{
	}
	

/*******************************************************************************************
*                             Setters and Getters
*******************************************************************************************/	

	
	
	
	public String getProtocoltype() {
		return protocoltype;
	}
	
	/**
	 * 
	 * @section util configuration
	 */
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @section util configuration
	 */

	public void setGrepstr(String grepstr) {
		this.grepstr = grepstr;
	}
	
	public String getGrepstr() {
		return grepstr;
	}
	
	
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setProtocoltype(String protocoltype) {
		this.protocoltype = protocoltype;
	}
	public String [] getProtocoltypeOptions() {
		return new String[]{"802.11a 5.2GHz","802.11bg 2.4GHz","802.11g 2.4GHz","802.11na 5.2GHz","802.11ng 2.4GHz","802.11nbg 2.4GHz"};
	
	}

	public String getRegulatorydomain() {
		return regulatorydomain;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setRegulatorydomain(String regulatorydomain) {
		this.regulatorydomain = regulatorydomain;
	}
	public String [] getRegulatorydomainOptions() {
		return new String[]{"FCC","DOC","ETSI","Spain","France","MKK"};
	}

	public String getChannelbondingmode() {
		return channelbondingmode;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setChannelbondingmode(String channelbondingmode) {
		this.channelbondingmode = channelbondingmode;
	}
	public String [] getChannelbondingmodeOptions() {
		return new String[]{"40","20"};
	}

	public String getUpperlowerchannelbonding() {
		return upperlowerchannelbonding;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setUpperlowerchannelbonding(String upperlowerchannelbonding) {
		this.upperlowerchannelbonding = upperlowerchannelbonding;
	}
	public String [] getUpperlowerchannelbondingOptions() {
		return new String[]{"LSB","USB"};
	}
	
	public String getSecurity_mode() {
		return security_mode;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setSecurity_mode(String security_mode) {
		this.security_mode = security_mode;
	}
	
	public String [] getSecurity_modeOptions() {
		return new String[]{"Open","WEP","WPA/WPA2 Personal","WPA/WPA2 Enterprise"};
	}
	
	public boolean isWep_encryption() {
		return wep_encryption;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWep_encryption(boolean wep_encryption) {
		this.wep_encryption = wep_encryption;
	}
	
	public String getAuthentication() {
		return authentication;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}
	
	public String [] getAuthenticationOptions() {
		return new String[]{"Open","Pre-Shared Key"};
	}
	
	public int getWep_key_length() {
		return wep_key_length;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWep_key_length(int wep_key_length) {
		this.wep_key_length = wep_key_length;
	}
	
	 
	public int [] getWep_key_lengthOptions() {
		return new int[]{64,128};
	}

	public String getWpa_mode() {
		return wpa_mode;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_mode(String wpa_mode) {
		this.wpa_mode = wpa_mode;
	}
	
	public String [] getWpa_modeOptions() {
		return new String[]{"WPA","WPA2","WPA+WPA2"};
	}

	public String getWpa_globalmode() {
		return wpa_globalmode;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_globalmode(String wpa_globalmode) {
		this.wpa_globalmode = wpa_globalmode;
	}
	public String [] getWpa_globalmodeOptions() {
		return new String[]{"Personal","Enterprise"};
	}

	public String getWpa_encapsulation() {
		return wpa_encapsulation;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_encapsulation(String wpa_encapsulation) {
		this.wpa_encapsulation = wpa_encapsulation;
	}
	public String [] getWpa_encapsulationOptions() {
		return new String[]{"TKIP","CCMP","TKIP + CCMP"};
	}


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


	public String getNetwork_type() {
		return network_type;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setNetwork_type(String network_type) {
		this.network_type = network_type;
	}
	public String []  getNetwork_typeOptions() {
		return new String[]{"Infrastructure Station","Adhoc Station","Access Point","Test MAC"};
	}


	public boolean isLdpc() {
		return ldpc;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setLdpc(boolean ldpc) {
		this.ldpc = ldpc;
	}


	public boolean isHidden_ssid() {
		return hidden_ssid;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setHidden_ssid(boolean hidden_ssid) {
		this.hidden_ssid = hidden_ssid;
	}


	public boolean isAp_forwarding() {
		return ap_forwarding;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAp_forwarding(boolean ap_forwarding) {
		this.ap_forwarding = ap_forwarding;
	}


	public boolean isLong_preambleformulti() {
		return long_preambleformulti;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setLong_preambleformulti(boolean long_preambleformulti) {
		this.long_preambleformulti = long_preambleformulti;
	}


	public boolean isOver_lapping_bss_prot() {
		return over_lapping_bss_prot;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setOver_lapping_bss_prot(boolean over_lapping_bss_prot) {
		this.over_lapping_bss_prot = over_lapping_bss_prot;
	}


	public String getErp_protection_type() {
		return erp_protection_type;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setErp_protection_type(String erp_protection_type) {
		this.erp_protection_type = erp_protection_type;
	}
	
	public String []  getErp_protection_typeOptions() {
		return new String[]{"None","RTS/CTS","CTS2Self"};
	}


	public String getN_protection_type() {
		return n_protection_type;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setN_protection_type(String n_protection_type) {
		this.n_protection_type = n_protection_type;
	}

	public String []  getN_protection_typeOptions() {
		return new String[]{"None","RTS/CTS","CTS2Self"};
	}


	public boolean isStbc() {
		return stbc;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setStbc(boolean stbc) {
		this.stbc = stbc;
	}


	public boolean isShort_cyclicprefix() {
		return short_cyclicprefix;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setShort_cyclicprefix(boolean short_cyclicprefix) {
		this.short_cyclicprefix = short_cyclicprefix;
	}


	public boolean isReliable_multi() {
		return reliable_multi;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setReliable_multi(boolean reliable_multi) {
		this.reliable_multi = reliable_multi;
	}


	public String getScan_mode() {
		return scan_mode;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setScan_mode(String scan_mode) {
		this.scan_mode = scan_mode;
	}
	public String []  getScan_modeOptions() {
		return new String[]{"Passive","Active"};
	}


	public String getScan_infra_adhoc() {
		return scan_infra_adhoc;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setScan_infra_adhoc(String scan_infra_adhoc) {
		this.scan_infra_adhoc = scan_infra_adhoc;
	}
	public String []  getScan_infra_adhocOptions() {
		return new String[]{"Infrastructure","Adhoc","Both"};
	}


	public long getRestartTimeout() {
		return restartTimeout;
	}


	public void setRestartTimeout(long restartTimeout) {
		this.restartTimeout = restartTimeout;
	}


	public String getRx_dongle() {
		return rx_dongle;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setRx_dongle(String rx_dongle) {
		this.rx_dongle = rx_dongle;
	}
	
	public String []  getRx_dongleOptions() {
		return new String[]{"ap","sta1","sta2","sta3","sta4","other vendor"};
	}

	public String getTx_dongle() {
		return tx_dongle;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setTx_dongle(String tx_dongle) {
		this.tx_dongle = tx_dongle;
	}
	public String []  getTx_dongleOptions() {
		return new String[]{"ap","sta1","sta2","sta3","sta4","other vendor"};
	}
	
	public String getIperf_bandwith() {
		return iperf_bandwith;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setIperf_bandwith(String iperf_bandwith) {
		this.iperf_bandwith = iperf_bandwith;
	}


	public int getIperf_interval() {
		return iperf_interval;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setIperf_interval(int iperf_interval) {
		this.iperf_interval = iperf_interval;
	}


	public String getIperf_output_file() {
		return iperf_output_file;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setIperf_output_file(String iperf_output_file) {
		this.iperf_output_file = iperf_output_file;
	}


	public int getIperf_packet_size() {
		return iperf_packet_size;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setIperf_packet_size(int iperf_packet_size) {
		this.iperf_packet_size = iperf_packet_size;
	}


	public String getIperf_protocol() {
		return iperf_protocol;
	}

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setIperf_protocol(String iperf_protocol) {
		this.iperf_protocol = iperf_protocol;
	}
	public String []  getIperf_protocolOptions() {
		return new String[]{"UDP","TCP"};
	}


	public String getExcel_infile() {
		return excel_infile;
	}


	public void setExcel_infile(String excel_infile) {
		this.excel_infile = excel_infile;
	}


	public int getExcel_mode() {
		return excel_mode;
	}
	public String []  getExcel_modeOptions() {
		return new String[]{"Input","Output","Input+Output"};
	}

	public void setExcel_mode(int excel_mode) {
		this.excel_mode = excel_mode;
	}

	
	
	public String getExcel_outfile() {
		return excel_outfile;
	}


	public void setExcel_outfile(String excel_outfile) {
		this.excel_outfile = excel_outfile;
	}


	public boolean isConfig_dongle() {
		return config_dongle;
	}


	public void setConfig_dongle(boolean config_dongle) {
		this.config_dongle = config_dongle;
	}


	public String getTest_name() {
		return test_name;
	}


	public void setTest_name(String test_name) {
		this.test_name = test_name;
	}
	
	public String []  getTest_nameOptions() {
		return new String[]{"Stability","Output","Input+Output"};
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public String [] getPathOptions(){
		return new String [] {"2.01","2.02","2.03","2.02.03","Devlop_Edi_2.03"};		
	}


	public String getBuild() {
		return build;
	}


	public void setBuild(String build) {
		this.build = build;
	}

	
	public int getIperf_duration() {
		return iperf_duration;
	}
	

	/**
	 *  
	 * @section Iperf 
	 *  
	 */
	public void setIperf_duration(int iperf_duration) {
		this.iperf_duration = iperf_duration;
	}


	public boolean isAgg_accept_be() {
		return agg_accept_be;
	}
	/**
	 * 
	 * @section dongles configuration
	 */

	public void setAgg_accept_be(boolean agg_accept_be) {
		this.agg_accept_be = agg_accept_be;
	}


	public boolean isAgg_accept_bk() {
		return agg_accept_bk;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_accept_bk(boolean agg_accept_bk) {
		this.agg_accept_bk = agg_accept_bk;
	}


	public boolean isAgg_accept_vi() {
		return agg_accept_vi;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_accept_vi(boolean agg_accept_vi) {
		this.agg_accept_vi = agg_accept_vi;
	}


	public boolean isAgg_accept_vo() {
		return agg_accept_vo;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_accept_vo(boolean agg_accept_vo) {
		this.agg_accept_vo = agg_accept_vo;
	}


	public boolean isAgg_use_be() {
		return agg_use_be;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_use_be(boolean agg_use_be) {
		this.agg_use_be = agg_use_be;
	}


	public boolean isAgg_use_bk() {
		return agg_use_bk;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_use_bk(boolean agg_use_bk) {
		this.agg_use_bk = agg_use_bk;
	}


	public boolean isAgg_use_vi() {
		return agg_use_vi;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_use_vi(boolean agg_use_vi) {
		this.agg_use_vi = agg_use_vi;
	}


	public boolean isAgg_use_vo() {
		return agg_use_vo;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setAgg_use_vo(boolean agg_use_vo) {
		this.agg_use_vo = agg_use_vo;
	}


	public String getMax_ht_rate() {
		return max_ht_rate;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setMax_ht_rate(String max_ht_rate) {
		this.max_ht_rate = max_ht_rate;
	}
	public String [] getMax_ht_rateOptions(){
		return new String [] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};		
	}

	public String getMin_ht_rate() {
		return min_ht_rate;
	}

	/**
	 * 
	 * @section dongles configuration
	 */
	public void setMin_ht_rate(String min_ht_rate) {
		this.min_ht_rate = min_ht_rate;
	}
	public String [] getMin_ht_rateOptions(){
		return new String [] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};		
	}


	public boolean isDebug() {
		return debug;
	}


	public void setDebug(boolean debug) {
		this.debug = debug;
	}


	public String getChipVarStatistics() {
		return chipVarStatistics;
	}


	public void setChipVarStatistics(String chipVarStatistics) {
		this.chipVarStatistics = chipVarStatistics;
	}

	public boolean isMac_cloning() {
		return mac_cloning;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setMac_cloning(boolean mac_cloning) {
		this.mac_cloning = mac_cloning;
	}

	public String getWlan_bridging() {
		return wlan_bridging;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setWlan_bridging(String wlan_bridging) {
		this.wlan_bridging = wlan_bridging;
	}
	public String [] getWlan_bridgingOptions(){
		return new String [] {"Route","Bridge","WDS"};
	}

	public String getNonproc_essid() {
		return nonproc_essid;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setNonproc_essid(String nonproc_essid) {
		this.nonproc_essid = nonproc_essid;
	}

	public String getIp_lan() {
		return ip_lan;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setIp_lan(String ip_lan) {
		this.ip_lan = ip_lan;
	}

	public String getIp_wlan() {
		return ip_wlan;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setIp_wlan(String ip_wlan) {
		this.ip_wlan = ip_wlan;
	}

	public String getPingDestination() {
		return pingDestination;
	}
	
	public String [] getPingDestinationOptions(){
		return  new String[]{"st0","st1","st2"};		
	}

	public void setPingDestination(String pingDestination) {
		this.pingDestination = pingDestination;
	}


	public boolean isApInTheWlan() {
		return apInTheWlan;
	}
	
	public void setApInTheWlan(boolean apInTheWlan) {
		this.apInTheWlan = apInTheWlan;
	}

	public boolean isSta1InTheWlan() {
		return sta1InTheWlan;
	}
	
	public void setSta1InTheWlan(boolean sta1InTheWlan) {
		this.sta1InTheWlan = sta1InTheWlan;
	}

	public boolean isSta2InTheWlan() {
		return sta2InTheWlan;
	}
	
	public void setSta2InTheWlan(boolean sta2InTheWlan) {
		this.sta2InTheWlan = sta2InTheWlan;
	}

	public boolean isSta3InTheWlan() {
		return sta3InTheWlan;
	}

	public void setSta3InTheWlan(boolean sta3InTheWlan) {
		this.sta3InTheWlan = sta3InTheWlan;
	}

	public String getNumOfRestartsUntilConnect() {
		return numOfRestartsUntilConnect;
	}

	public void setNumOfRestartsUntilConnect(String numOfRestartsUntilConnect) {
		this.numOfRestartsUntilConnect = numOfRestartsUntilConnect;
	}		
	public String getProtocoltype_sta() {
		return protocoltype_sta;
	}
	/**
	 * 
	 * @section dongles configuration
	 */
	public void setProtocoltype_sta(String protocoltype_sta) {
		this.protocoltype_sta = protocoltype_sta;
	}	
	public String [] getProtocoltype_staOptions() {
		return new String[]{"802.11a 5.2GHz","802.11bg 2.4GHz","802.11g 2.4GHz","802.11na 5.2GHz","802.11ng 2.4GHz","802.11nbg 2.4GHz","802.11abg Dual 2.4GHz/5.2Ghz","802.11n Dual 2.4GHz/5.2Ghz"};
	
	}

	public String getWpa_personal_psk() {
		return wpa_personal_psk;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_personal_psk(String wpa_personal_psk) {
		this.wpa_personal_psk = wpa_personal_psk;
	}

	public int getWep_key_index() {
		return wep_key_index;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWep_key_index(int wep_key_index) {
		this.wep_key_index = wep_key_index;
	}
	
	public int [] getWep_key_indexOptions() {
		return new int[]{0,1,2,3};
	
	}
	
	public String getWep_key_value() {
		return wep_key_value;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWep_key_value(String wep_key_value) {
		this.wep_key_value = wep_key_value;
	}

	public String getWpa_enterprise_radius_key() {
		return wpa_enterprise_radius_key;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_enterprise_radius_key(String wpa_enterprise_radius_key) {
		this.wpa_enterprise_radius_key = wpa_enterprise_radius_key;
	}

	public String getWpa_enterprise_radius_password() {
		return wpa_enterprise_radius_password;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_enterprise_radius_password(
			String wpa_enterprise_radius_password) {
		this.wpa_enterprise_radius_password = wpa_enterprise_radius_password;
	}

	public String getWpa_enterprise_radius_user() {
		return wpa_enterprise_radius_user;
	}
	/**
	 *  
	 * @section Security 
	 *  
	 */
	public void setWpa_enterprise_radius_user(String wpa_enterprise_radius_user) {
		this.wpa_enterprise_radius_user = wpa_enterprise_radius_user;
	}

	public String getLogFileLocation() {
		return logFileLocation;
	}

	public void setLogFileLocation(String logFileLocation) {
		this.logFileLocation = logFileLocation;
	}
	
	public String getTestComment() {
		return testComment;
	}

	public void setTestComment(String testComment) {
		this.testComment = testComment;
	}

	public int getCpu_load_duration() {
		return cpu_load_duration;
	}

	public void setCpu_load_duration(int cpu_load_duration) {
		this.cpu_load_duration = cpu_load_duration;
	}

	public int getCpu_load_interval() {
		return cpu_load_interval;
	}

	public void setCpu_load_interval(int cpu_load_interval) {
		this.cpu_load_interval = cpu_load_interval;
	}

	public String getRestore_defaults_band() {
		return restore_defaults_band;
	}
	/**
	 * the new bandwidth for the dongle
	 * @section dongles configuration
	 */
	public void setRestore_defaults_band(String restore_defaults_band) {
		this.restore_defaults_band = restore_defaults_band;
	}
	
	public String [] getRestore_defaults_bandOptions(){
		return new String [] {"5G","2.4G"};
	}

	public String getRestore_defaults_mode() {
		return restore_defaults_mode;
	}
	/**
	 * the new operation mode for the dongle
	 * @section dongles configuration
	 */
	public void setRestore_defaults_mode(String restore_defaults_mode) {
		this.restore_defaults_mode = restore_defaults_mode;
	}

	public String [] getRestore_defaults_modeOptions() {
		return new String [] {"Route", "Bridge", "WDS"};
	}


	
}
