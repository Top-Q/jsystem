/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.net.ServerSocket;

import jsystem.runner.remote.TestVMParamsUtil;
import junit.framework.SystemTestCase;

public class TestVMParamsUtilTest extends SystemTestCase {

	public void testRelpaceSocketNumber() throws Exception  {
		TestVMParamsUtil util = new TestVMParamsUtil();
		String result = 
			util.relpaceSocketNumber("-Xms32M -Xmx256M -DentityExpansionLimit=1280000");
		assertEquals("-Xms32M -Xmx256M -DentityExpansionLimit=1280000", result);
	}

	
	public void testRelpaceSocketNumberWithPort() throws Exception  {
		TestVMParamsUtil util = new TestVMParamsUtil();
		String result = 
			util.relpaceSocketNumber("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=y,suspend=y");
		assertEquals("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y", result);
	}

	public void testRelpaceSocketNumberWith2Ports() throws Exception  {
		TestVMParamsUtil util = new TestVMParamsUtil();
		String result = 
			util.relpaceSocketNumber("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=${9191},suspend=y");
		assertEquals("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8787,server=9191,suspend=y", result);
	}
	
	public void testRelpaceSocketNumberWithOccupiedPort() throws Exception  {
		ServerSocket ss = null;
		try {
			ss	= new ServerSocket(8787);
			TestVMParamsUtil util = new TestVMParamsUtil();
			String result = 
				util.relpaceSocketNumber("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8787},server=y,suspend=y");
			assertEquals("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8788,server=y,suspend=y", result);
		}finally {
			try {
				ss.close();
			}catch (Exception e){
				
			}
		}
	}

	public void testPortIsNotOccupied() throws Exception  {
		TestVMParamsUtil util = new TestVMParamsUtil();
		String result = 
			util.relpaceSocketNumber("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=${8788},server=y,suspend=y");
		assertEquals("-Xms32M -Xmx256M -DentityExpansionLimit=1280000 -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8788,server=y,suspend=y", result);
		ServerSocket s = null;
		try {
			s = new ServerSocket(8788);
		}finally {
			try {
				s.close();
			}catch (Exception e){
				
			}
		}
	}

}
