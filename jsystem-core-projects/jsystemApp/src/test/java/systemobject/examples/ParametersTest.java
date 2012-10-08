/*
 * Created on Dec 17, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.examples;

import jsystem.framework.TestProperties;

/**
 * @author guy.arieli
 */
public class ParametersTest extends ParamsLevel2 {

	int packetSize = 64;
	String setupName = null;
	boolean status = true;
	int array;
	
	/**
	 * Test the parameters feature
	 *
	 */
	@TestProperties(name = "Generate traffic were packet size is ${PacketSize} using setup ${SetupName}")
	public void testParameters(){
		report.report("packetSize: " + packetSize);
		report.report("setupName: " + setupName);
	}
	
	public void testInternal(){
		for (int i = 0; i < 10; i++){
			report.startReport("testMyTest", "i=" + i , null, "this is the test document");
			
			report.report("do something");
			
			report.endReport();
		}
	}
	
	public String getSetupName() {
		return setupName;
	}
	/**
	 * @section Device
	 * set setup name
	 */
	public void setSetupName(String setupName) {
		this.setupName = setupName;
	}
	public int getPacketSize() {
		return packetSize;
	}
	/**
	 * @section Generator
	 * set the packet size from 64 - 1522
	 */
	public void setPacketSize(int packetSize) {
		this.packetSize = packetSize;
	}

	public int[] getArrayOptions() {
		return new int[]{10,20,30};
	}

	
	public void setArray(int array) {
		this.array = array;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
}
