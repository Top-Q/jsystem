/*
 * Created on 19/07/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.io.PrintStream;

import jsystem.framework.system.SystemObject;
import systemobject.terminal.InOutInputStream;
import systemobject.terminal.Prompt;

/**
 * @author guy.arieli
 *
 */
public interface CliConnection extends SystemObject{
    
	/**
     * 
     * @param command
     * @throws Exception
     */
	void navigateToPosition(CliCommand command) throws Exception;
    
	/**
	 * 
	 * @param command
	 * @throws Exception
	 */
	void returnFromPosition(CliCommand command) throws Exception;
    
    /**
     * run a given command object
     * 
     * @param command	CliCommand object
     */
    void command(CliCommand command);
    
    /**
     * 
     * @return	the host String set for this CLI
     */
    String getHost();
    
    /**
     * set the Host for this CLI
     * @param host
     */
    void setHost(String host);
    
    /**
     * get defined Prompts array 
     * @return
     */
    Prompt[] getPrompts();
    
    /**
     * establish a connection with the CLI
     * @throws Exception
     */
    void connect() throws Exception;
    
    
    Position[] getPositions();
    
    /**
     * send an empty command and wait for all notification in the result
     * 
     * @param notifications	 the array of strings to wait for in the result
     * @param timeout	time in ms after which the command fails
     * @throws Exception
     */
    public void waitForNotifications(String[] notifications, long timeout) throws Exception;
    
    /**
     * get the content (String) of the CLI buffer
     * 
     * @return
     */
    String getCliBuffer();
    
    /**
     * empty the CLI buffer
     */
    void cleanCliBuffer();
    
    /**
	 * returns the current status of the CliConnection :<br>
	 * if the CLI Object is not null - it will return its connectivity<br> 
	 * and if it is null - it will return "false".
	 * 
	 * @return boolean : true if connected, false if not connected.
	 */
    boolean isConnected();
    
    /**
     * checks if the connection should be established when initiating the SystemObject<br>
     * can be set from the sut
     * @return
     */
    boolean isConnectOnInit();
    
    /**
     * signal disconnection of the CLI
     */
    void disconnect();
    
    /**
     * the long representation of the time the last command was executed
     * 
     * @return
     */
    long getLastCommandTime();
    
    /**
     * Get the maximum idle time for the idle monitor.
     * @return
     */
    long getMaxIdleTime();
    
    /**
     * Set the maximum idle time for the idle monitor.
     * @param maxIdleTime
     */
    void setMaxIdleTime(long maxIdleTime);
    
    
    /**
     * get the password used for connection
     * @return
     */
    String getPassword();
    
    /**
     * get the user defined for connection
     * @return
     */
    String getUser();
    
    /**
     * set the password for CLI connection
     * 
     * @param password
     */
    void setPassword(String password);
    
    /**
     * set the user for CLI connection
     * 
     * @param user
     */
    void setUser(String user);
    
    /**
     * a String representing the connection protocol (RS232/SSH/Telnet)
     * @return
     */
    String getProtocol();
    
    /**
     * set the protocol type for this connection ((RS232/SSH/Telnet)
     * @param protocol
     */
    void setProtocol(String protocol);
    /**
     * If set to True add enter after waitForPromp fail on timeout
     * And will wait again for prompt
     */
    void setGraceful(boolean graceful);
    
    /**
     * Flag that indicates if after command timeout an enterString should be sent
     * @return
     */
    boolean isGraceful();
    
	/**
	 * Sets the print stream to which the stream of the connection 
	 * will be dumped to.
	 * Set the print stream to System.out to dump terminal stream to the console,
	 * Set print stream to null to turn off stream dump.
	 */
    void setPrintStream(PrintStream printStream);
    
	/**
	 * Returns the prompt which identification triggered the termination
	 * of the CLI operation.
	 */
    Prompt getResultPrompt();
    
	/**
	 * Reads the stream in the input buffer 
	 * and returns it as a String.
	 */
    String read() throws Exception;

    /**
     * reconnect the terminal
     */
    void reconnect();
    
	/**
	 * Activates the command <code>command</code> 
	 * and analyzes command's results 
	 * Procedure flow:
	 * 1. connects to remote machine if needed<br>
	 * 2. Runs the command<br>
	 * 3. Performs report operation & throws an exception<br> 
	 *    in case of an error. (and ignore error flags were not raised)<br>
	 * 4. Performs Analysis if one or more analyzers are defined (and ignore error flags were not raised)   
	 */
	void handleCliCommand( String title,CliCommand command) throws Exception;

	/**
	 * add possible Prompts to the CLI
	 * 
	 * @param prompts
	 */
	void addPrompts(Prompt[] prompts);
	
	/**
	 * set the Prompts to check in the result<br>
	 * if one is found, command will end
	 * 
	 * @param prompts
	 */
	void setPrompts(Prompt[] prompts);

	/**
	 * the defined enter String for the CLI<br>
	 * default is "/r"
	 * 
	 * @param enterStr
	 */
	void setEnterStr(String enterStr);
	
	/**
	 * the time (in ms) to wait for a terminal input to be received before declaring scroll end (no more input)
	 * 
	 * @param timeout
	 */
	void setScrollEndTimeout(long timeout);
	
	/**
	 * create a filter input stream on the terminal result:<br>
     * 1) add the current input stream to the given stream.<br>
     * 2) set the new input stream to the given one.<br>
     * 
	 * @param input
	 */
	void addFilter(InOutInputStream input);
	
	/**
	 * send a given string to the terminal (no prompt waiting)
     * 
     * @param toSend	the command to send
     * @param delayedTyping	if True will sleep 20 ms between each typed byte entered to the terminal
	 * @throws Exception
	 */
	void sendString(String toSend, boolean delayedTyping) throws Exception;

	/**
	 * get the enter String set for this cli
	 * 
	 * @return
	 */
	String getEnterStr();
	
	/**
	 * get the number of retries to establish a connection
	 * 
	 * @return
	 */
	int getConnectRetries();

	/**
	 * set the number of retries for connection establishing
	 * 
	 * @param connectRetries
	 */
	void setConnectRetries(int connectRetries);
	
	/**
	 * checks if enter String should be sent on log-in
	 * 
	 * @return
	 */
	boolean isLeadingEnter();

	/**
	 * if True will send an enter String on log-in
	 * 
	 * @param leadingEnter
	 */
	void setLeadingEnter(boolean leadingEnter);
	
	/**
	 * the time (in ms) to sleep between each typed byte entered to the terminal
	 * 
	 * @return
	 */
	long getKeyTypingDelay();
	
	/**
	 * the time (in ms) to sleep between each typed byte entered to the terminal
	 * 
	 * @param keyTypingDelay
	 */
	void setKeyTypingDelay(long keyTypingDelay);
}
