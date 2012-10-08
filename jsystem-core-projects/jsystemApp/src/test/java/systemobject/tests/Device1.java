/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.tests;

import java.io.File;

import jsystem.framework.system.SystemObject;
import jsystem.framework.system.SystemObjectImpl;

public class Device1 extends SystemObjectImpl{
    
	public Telnet telnet;
    public Port[] port;
    private String ip;
     
    public void init() throws Exception {
        //telnet = (Telnet)deviceManager.getSystemObject(getXPath(),"telnet");
        super.init();
        System.out.println("Device1 was init");
    }

    public void close() {
        super.close();
        //telnet.close();
        System.out.println("Device1 was closed");
    }
    
    public void command(String cmd){
    	
    }
    public void commandFile(File file){
    	
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * casuing exception in order to see the error
     * at the table.
     */
    public void check() throws Exception{
    	setCheckStatus(SystemObject.CHECK_DISCONNECTED);
		Object a = null;
		a.wait();
		setCheckStatus(SystemObject.CHECK_CONNECTED);
    }
}
