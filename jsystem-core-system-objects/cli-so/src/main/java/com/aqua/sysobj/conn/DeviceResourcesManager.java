/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import jsystem.runner.loader.LoadersManager;

/**
 * Use to manage device resources in properties format.
 * <p>
 * 		 On init:
 *       rmanager = DriverResourcesManager.getInstance("device");
 * 
 *       rmanager.init("com/systemobjects/bs/device/cli.properties" );
 *       
 *       Then when a resource is needed:
 *       
 *       rmanager.getResource(resourceName, params);
 * 
 * 
 * @author guyarieli
 *
 */
public class DeviceResourcesManager {
	private static HashMap<String, DeviceResourcesManager>  grms = new HashMap<String, DeviceResourcesManager>();
	
	public static DeviceResourcesManager getInstance(String key){
		if(grms.get(key) == null){
			grms.put(key, new DeviceResourcesManager());
		}
		return grms.get(key);
	}

	Properties dirverMap = null;
	private DeviceResourcesManager(){
		
	}
	
	public void init(String resourcePath) throws Exception{
		dirverMap = new Properties();
		dirverMap.load(LoadersManager.getInstance().getLoader().getResourceAsStream(resourcePath));
	}
	
	public String getResource(String guiResour, Object ...objects ){
		return MessageFormat.format(dirverMap.getProperty(guiResour), objects);
	}
	public String getResource(String guiResour){
		return dirverMap.getProperty(guiResour);
	}
}
