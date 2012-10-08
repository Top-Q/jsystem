package com.aqua.stability;

import jsystem.framework.fixture.RootFixture;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase4;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;

/**
 * 
 * @author Dan Hirsch
 *
 */
public class CliStabilityTest extends SystemTestCase4 {
	private CliConnectionImpl connection;
	private String osType;
	public CliCommand command;
	public CliStabilityTest() {
		super();
		setFixture(RootFixture.class);
	}
	
	@Before
	public void setUp() throws Exception{
		Sut sut = SutFactory.getInstance().getSutInstance();
		osType = sut.getValue("/sut/osType/text()");
	}
	
	/**
	 * repeating creation of cli connection to a remote machine
	 * given in the sut, running a remote operation, and closing the connection.
	 * 
	 * @throws Exception
	 */
	@Test
	public void cliStabilityTest() throws Exception{
		/*
		 * check the type of OS specified in the sut and accordingly, instantiate the 
		 * connection object and run the relevant command
		 */
		if("linux".equalsIgnoreCase(osType)){
			connection = (CliConnectionImpl)system.getSystemObject("linuxconnection");
			command = new CliCommand("ls -l /");
		}
		else if("windows".equalsIgnoreCase(osType)){
			connection = (CliConnectionImpl)system.getSystemObject("windowsconnection");
			command = new CliCommand("dir c:\\");
		}
		connection.connect();
		connection.handleCliCommand("list files and subdirs under directory", command);
		/*
		 * get the command result to check cli connection created successfully.
		 */
		String result = command.getResult();
		report.step("testing if the command returned the string representing the list of dirs");
		if(!StringUtils.isEmpty(result)){
			Assert.assertTrue(true);
			connection.close();
		}		
		else{
			Assert.assertTrue(false);
			connection.close();
		}
	}
	
	@After
	public void tearDown(){
		report.step("closing the cli connection");
		if(!connection.isClosed()){
			connection.close();
		}
	}
	public CliConnectionImpl getConnection() {
		return connection;
	}

	public void setConnection(CliConnectionImpl connection) {
		this.connection = connection;
	}

	public String getOsType() {
		return osType;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}
}
