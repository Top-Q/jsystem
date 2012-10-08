/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.util.ArrayList;

import systemobject.terminal.Cli;
import systemobject.terminal.Prompt;
import systemobject.terminal.VT100FilterInputStream;

import com.aqua.sysobj.conn.CliApplication;
import com.aqua.sysobj.conn.CliConnectionImpl;
import com.aqua.sysobj.conn.Position;

/**
 * Factory class for creation of {@link Cli}/{@link CliApplication} instances.
 *  @author Golan Derazon
 */
public class CliFactory  {
	
	public static final String OPERATING_SYSTEM_NA = EnumOperatinSystem.DEFAULT.toString().toLowerCase();
	public static final String OPERATING_SYSTEM_WINDOWS = EnumOperatinSystem.WINDOWS.toString().toLowerCase();
	public static final String OPERATING_SYSTEM_LINUX = EnumOperatinSystem.LINUX.toString().toLowerCase();
	
	public static enum EnumOperatinSystem {    
		DEFAULT(1),
		WINDOWS(2),
		LINUX(3);
		EnumOperatinSystem(int value) {
			this.value=value;
		}
		private int value;
		public int value(){return value;}
	}
	
	public static CliConnectionImpl createCliConnection(EnumOperatinSystem os) throws Exception {

		CliConnectionImpl cliConn = null;
		
		if (os == EnumOperatinSystem.WINDOWS) {
			cliConn = new WindowsDefaultCliConnection();
		} else if (os == EnumOperatinSystem.LINUX) {
			cliConn = new LinuxDefaultCliConnection();
		} else {
			throw new Exception("Unsupported Operting System - " + os.toString());
		}
		
		return cliConn;
		
	}
	
	/**
	 * Creates {@link Cli}.
	 * The creation process also includes login.
	 * If login fails the creation operation will fail.
	 * 
	 * @param host - the host to connect to. 
	 * @param operatingSystem - currently one of {@link #OPERATING_SYSTEM_LINUX} or {@link #OPERATING_SYSTEM_WINDOWS}
	 * @param protocol - one of telnet/ssh/rs232
	 * @param user - user name to login with
	 * @param password- user password to login with
	 * @param additionalPrompts - additional applicative prompts.
	 */
	public static Cli createCli(String host,String operatingSystem,String protocol,String user,String password,Prompt[] additionalPrompts) throws Exception {
		BaseCli impl = null;

		if (operatingSystem.equals(OPERATING_SYSTEM_WINDOWS)){
			impl = new WindowsCli(user,password,additionalPrompts);
			impl.setDump(true);
			impl.setUseTelnetInputStream(true);
		}else
		if (operatingSystem.equals(OPERATING_SYSTEM_LINUX)){
			impl = new LinuxCli(user,password,additionalPrompts);
		}else
		if (operatingSystem.equals(OPERATING_SYSTEM_NA)){
				impl = new DefaultCli(user,password,additionalPrompts);
		}
		
		impl.setUser(user);
		impl.setPassword(password);
		impl.setProtocol(protocol);
		impl.setHost(host);
		impl.connect();
		return impl.getCli();
	}

	/**
	 * Creates {@link Cli}.
	 * The creation process also includes login.
	 * If login fails the creation operation will fail.
	 * 
	 * @param host - the host to connect to. 
	 * @param operatingSystem - currently one of {@link #OPERATING_SYSTEM_LINUX} or {@link #OPERATING_SYSTEM_WINDOWS}
	 * @param protocol - one of telnet/ssh/rs232
	 * @param user - user name to login with
	 * @param password- user password to login with
	 * @param additionalPrompts - additional applicative prompts.
	 */
	public static CliApplication createCliApplication(String host,String operatingSystem,String protocol,String user,String password,Prompt[] additionalPrompts) throws Exception {
		BaseCli impl = null;

		if (operatingSystem.equals(OPERATING_SYSTEM_WINDOWS)){
			impl = new WindowsCli(user,password,additionalPrompts);
			impl.setDump(true);
			impl.setUseTelnetInputStream(true);
		}else
		if (operatingSystem.equals(OPERATING_SYSTEM_LINUX)){
			impl = new LinuxCli(user,password,additionalPrompts);
		}
		
		impl.setUser(user);
		impl.setPassword(password);
		impl.setProtocol(protocol);
		impl.setHost(host);
		impl.connect();
		impl.terminal.addFilter(new VT100FilterInputStream());
		String name = host+"("+operatingSystem+")";
		CliApplication cliApp = new CliApplication(impl,name);
		cliApp.setXPath(name);
		return cliApp;
	}
		
	/**
	 */
	static abstract class BaseCli extends CliConnectionImpl {
		protected Prompt[] prompts;

		private BaseCli(String userName,String password,Prompt[] additionalPrompts){
			this.prompts = additionalPrompts;
			
			this.password = password;
		}
		
		public Cli getCli(){
			return cli;
		}
		public Position[] getPositions() {
			return null;
		}
		
		protected void addAdditionalPrompts(ArrayList<Prompt> promptsColl){
			if (prompts != null){
				for (Prompt p:prompts){
					promptsColl.add(p);
				}
			}
		}
	}
	
	/**
	 */
	static class WindowsCli extends BaseCli {
		private WindowsCli(String user,String password,Prompt[] additionalPrompts){
			super(user,password,additionalPrompts);
		}
	
		public Prompt[] getPrompts() {
			ArrayList<Prompt> prompts = new ArrayList<Prompt>();
			Prompt p = new Prompt();
			p.setPrompt("login:");
			p.setStringToSend(getUser());
			prompts.add(p);
			p = new Prompt();
			p.setPrompt("password:");
			p.setStringToSend(getPassword());
			prompts.add(p);
			p = new Prompt();
			p.setPrompt(">");
			p.setCommandEnd(true);
			prompts.add(p);
			addAdditionalPrompts(prompts);
			return prompts.toArray(new Prompt[prompts.size()]);
		}
	}
	
	/**
	 * 
	 */
	static class LinuxCli extends BaseCli {
		private LinuxCli(String user,String password,Prompt[] prompts){
			super(user,password,prompts);
		}
	
		public Prompt[] getPrompts() {
			ArrayList<Prompt> prompts = new ArrayList<Prompt>();		
			Prompt p = new Prompt();
			p.setCommandEnd(true);
			p.setPrompt("# ");
			prompts.add(p);

			p = new Prompt();
			p.setPrompt("login: ");
			p.setStringToSend(getUser());
			prompts.add(p);

			p = new Prompt();
			p.setPrompt("Password: ");
			p.setStringToSend(getPassword());
			prompts.add(p);

			addAdditionalPrompts(prompts);			
			return prompts.toArray(new Prompt[prompts.size()]);
		}
	}
	
	/**
	 * 
	 */
	static class DefaultCli extends BaseCli {
		private DefaultCli(String user,String password,Prompt[] prompts){
			super(user,password,prompts);
		}
		public Prompt[] getPrompts() {
			ArrayList<Prompt> prompts = new ArrayList<Prompt>();		
			addAdditionalPrompts(prompts);			
			return prompts.toArray(new Prompt[prompts.size()]);
		}
	}

}
