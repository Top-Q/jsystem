/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import java.util.List;

import org.w3c.dom.Node;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

import com.aqua.services.demo.WindowsStation;
import com.aqua.services.lifecycle.LifeTime;

/**
 * Demonstrates system and SUT services.
 * Issues that are covered:
 * 
 * 1. init, close
 * 2. lifetime
 * 3. SUT file
 * 4. SUT editor
 * 5. Direct access to SUT
 * 
 */
public class SystemServicesTestWithAnnotations extends SystemTestCase {
	private WindowsStation station;

	
	private String pingHost	=	"127.0.0.1";
	private LifeTime lifeTime = LifeTime.PERMANENT_LIFETIME;
	

	public void setUp() throws Exception {
		station = (WindowsStation) system.getSystemObject("station");
		station.setLifeTime(lifeTime.ordinal());
	}

	/**
	 * Test ping operation on remote machine
	 * @params.include pingHost,lifeTime
	 */
	@TestProperties(name="Test ping operation on machine ${pingHost}, with SystemObject lifetime=${lifeTime}")
	public void testPing() throws Exception {
		station.ping(getPingHost());
	}

	/**
	 */
	@TestProperties(name="Demonstrates direct access to the SUT file",paramsInclude={})
	public void testDemonstrateDirectAccessToSUT() throws Exception {
		String setupName	=	sut().getSetupName();
		List<Node> allValues =		sut().getAllValues("sut/station/*");
		String value =		sut().getValue("sut/station/class/text()");
		assertEquals("mystation.xml",setupName);
		assertEquals(2,allValues.size());
		assertEquals("com.aqua.services.demo.WindowsStation",value);
	}

	/**
	 */
	@TestProperties(name="Empty test to trigger fixtures navigation",paramsInclude={})
	public void testEmptyTest() {
		
	}
	
	public LifeTime getLifeTime() {
		return lifeTime;
	}
	/**
	 * The life span of the system object
	 */
	@ParameterProperties(section="Lifetime")
	public void setLifeTime(LifeTime lifeTime) {
		this.lifeTime = lifeTime;
	}

	public String getPingHost() {
		return pingHost;
	}

	public String[] getPingHostOptions() {
		return new String[]{"127.0.0.1","localhost","192.16.45.1"};
	}
	
	/**
	 * The host which will be pinged
	 */
	@ParameterProperties(section="Target Host")
	public void setPingHost(String pingHost) {
		this.pingHost = pingHost;
	}
	public String[] sectionOrder() {
		return new String[]{"Target host","Lifetime"};
	}
}
