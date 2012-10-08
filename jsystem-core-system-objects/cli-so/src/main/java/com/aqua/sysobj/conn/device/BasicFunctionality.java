/*
 * Created on Jan 26, 2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn.device;

/**
 * This interface defines an API for a basic functionality of a device
 * @author ohad.crystal
 */
public interface BasicFunctionality {
	
	/**
	 * Send any command to the cli
	 * @param position the desired position
	 * @param command full free command syntax
	 * @throws Exception
	 */	
	public void sendAnyCommand(String position, String command) throws Exception;

	/**
	 * Show the device name
	 * @throws Exception
	 */	
	public abstract void showDeviceName() throws Exception;
	
	/**
	 * Show the device version information
	 * @throws Exception
	 */

	public abstract void showVersion() throws Exception;
	
	/**
	 * Show the device running configuration
	 * @throws Exception
	 */
	public abstract void showRunningConfiguration() throws Exception;
	
	/**
	 * Perform system restart (without device reload monitoring)
	 * @throws Exception
	 */
	public abstract void rebootDevice() throws Exception;
		
	/**
	 * Perform system restart and verify device reload during the defined timeout
	 * @param timeout the number of seconds after which the device prompt must return.
	 * @throws Exception
	 */
	public abstract void rebootDevice(int timeOut) throws Exception;
	
	/**
	 * Update the software version using TFTP server
	 * path String the file path Example: 10.10.11.12/updates/ver5.2
	 * @throws Exception
	 */
	public abstract void updateVersionTFTP(String path,String versionName) throws Exception;
	
	/**
	 * Update the software version using FTP server
	 * @param user	the ftp user
	 * @param password the ftp password
	 * @param path  the file path Example: 10.10.11.12/updates/ver5.2
	 * @throws Exception
	 */
	public abstract void updateVersionFTP(String user, String password, String path,String versionName) throws Exception;
		
	/**
	 * Add snmp community
	 * @param communityName the community name
	 * @access The comunity access level
	 * @throws Exception
	 */
	public abstract void setCommunity(String communityName, String access) throws Exception;
	
	/**
	 * Remove snmp community
	 * @param communityName the community name
	 * @throws Exception
	 */
	public abstract void deleteCommunity(String communityName) throws Exception;
	
	/**
	 * 
	 * @param isEnable boolean true for enable, false for disable
	 * @param trapName the trap name
	 * @throws Exception
	 */
	public abstract void enableTrap(boolean isEnable, String trapName) throws Exception;
	
	/**
	 * 
	 * @param timeOut the time of inactivity (seconds) untill the terminal will logout
	 * @throws Exception
	 */
	public abstract void setTerminalTimeOut(int timeOut) throws Exception;
	
	/**
	 * Clear current configuration and return the device to its factory defaults (Use with care ...)
	 * @throws Exception
	 */
	public abstract void returnToFactoryDefault() throws Exception;

}
