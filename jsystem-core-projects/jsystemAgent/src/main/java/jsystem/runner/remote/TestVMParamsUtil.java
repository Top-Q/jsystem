/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.remote;

import java.net.BindException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.utils.StringUtils;

/**
 * Utility class which replaces port number template with real port number.
 * The class was implemented to solve the following problem:
 * When adding debug parameters (or jmx parameters) to the test vm parameters:
 * For example: -classic -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y
 * and running in run.mode=2, the first test runs okay, the second test fails 
 * to bind to the port 8787 since it is occupied by the previous jvm.
 * (it takes the jvm ~10 seconds to close)
 * To solve this problem, the user can enter the following:
 * ... ,address=${8787},server=y,suspend=y 
 * The replaceSocketNumber will replace the pattern ${XXX} with the first free
 * port starting at XXX.
 * In our example, if port 8787 is free the method will return
 * ...,address=8787,server=y,suspend=y
 * if port 8787 is not free the method will return
 * ...,address=8788,server=y,suspend=y etc'  
 * 
 * If for some reason, fetching available port fails the method returns 
 * start port number.
 * 
 * @author goland
 */
public class TestVMParamsUtil {
	
	private static Logger log = Logger.getLogger(TestVMParamsUtil.class.getName());
	
	private static Pattern p = Pattern.compile("\\$\\{(\\d*)\\}");
	
	/**
	 * Replaces port pattern with actual free port number.
	 * See class documentation. 
	 */
	public String relpaceSocketNumber(String vmParams) throws Exception {
		Matcher m = p.matcher(vmParams);
		while (m.find()){
			int startPort = Integer.parseInt(m.group(1));
			int finalPort = findFreeServerSocket(startPort);
			vmParams = StringUtils.replace(vmParams, m.group(0), ""+finalPort);
		}
		return vmParams;
	}

	/**
	 * Finds a free port starting at <code>startNumber</code>
	 */
	private int findFreeServerSocket(int startNumber) {
		boolean found = false;
		int portNumber = startNumber;
		ServerSocket socket = null;
		;
		while (!found) {
			try {
				socket = new ServerSocket(portNumber);
				found = true;
			} catch (BindException e) {
				log.log(Level.FINEST, "port " + portNumber + " not available");
				portNumber += 1;
			} catch (Exception e) {
				log.log(Level.WARNING, "failed checking port " + portNumber, e);
				return startNumber;
			} finally {
				try {
					socket.close();
				} catch (Throwable t) {
				};				
			}
		}
		return portNumber;
	}	
}
