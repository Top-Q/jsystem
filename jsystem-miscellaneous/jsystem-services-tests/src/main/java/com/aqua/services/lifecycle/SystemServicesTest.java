/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.lifecycle;

import java.util.Date;
import java.util.List;

import org.w3c.dom.Node;

import jsystem.framework.RunProperties;
import jsystem.framework.report.Summary;
import jsystem.framework.system.SystemObject;
import junit.framework.SystemTestCase;

import com.aqua.services.demo.WindowsStation;

/**
 * Demonstrates system and SUT services.
 * Issues that are covered:
 * 
 * 1. init, close
 * 2. lifetime
 * 3. SUT file
 * 4. SUT editor
 * 5. Direct access to SUT
 * 6. run properties
 * 
 */
public class SystemServicesTest extends SystemTestCase {
	
	private WindowsStation station;
	private String pingHost	=	"127.0.0.1";
	private LifeTime lifeTime = LifeTime.PERMANENT_LIFETIME;
	
	public void setUp() throws Exception {
		station = (WindowsStation) system.getSystemObject("station1");
		station.setLifeTime(SystemObject.TEST_LIFETIME);
	}

	public void testPing() throws Exception {
		station.ping(getPingHost());
	}

	public void testDemonstrateDirectAccessToSUT() throws Exception {
		String setupName	=	sut().getSetupName();
		List<Node> allValues =		sut().getAllValues("sut/station/*");
		String value =		sut().getValue("sut/station/class/text()");
		assertEquals("mystation.xml",setupName);
		assertEquals(2,allValues.size());
		assertEquals("com.aqua.services.demo.WindowsStation",value);
	}

	public void testRunPropertiesDemonstration()  throws Exception{
		RunProperties.getInstance().setRunProperty("property", "value");
		Summary.getInstance().setProperty("time", new Date().toString());
	}

	public void testEmptyTest() {		
	}
	public LifeTime getLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(LifeTime lifeTime) {
		this.lifeTime = lifeTime;
	}

	public String getPingHost() {
		return pingHost;
	}

	public void setPingHost(String pingHost) {
		this.pingHost = pingHost;
	}

}


