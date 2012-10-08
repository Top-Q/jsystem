/*
 * Created on 21/07/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.analyzer.AnalyzerParameter;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;
import systemobject.terminal.Prompt;

/**
 * @author guy.arieli
 * 
 */
public class CliApplication extends Application implements TestListener {

	/**
	 * If set to true will start to buffer commands and analysis
	 */
	private boolean bufferCommands = false;
	
	/**
	 * A list of commands to buffer
	 */
	private List<Object> commands = null;
	/**
	 * The buffer executor thread
	 */
	private Executor executor = null;
	
	public CliApplication() {
		super();
	}

	/**
	 * Constrctor that is used to init and work with the CliApplication without
	 * the sut initialization.
	 * 
	 * @param cliConnection
	 * @throws Exception
	 */
	public CliApplication(CliConnection cliConnection, String name)
			throws Exception {
		conn = new ConnectivityManager();
		conn.setParent(this);

		conn.cli = cliConnection;

		setName(name);
		init();
	}

	public void init() throws Exception {
		super.init();
	}

	/**
	 * Activates the command <code>command</code> 
	 * on given <code>connection</code>
	 * 
	 * Procedure flow:
	 * 1. connects to remote machine if needed
	 * 2. Runs the command
	 * 3. Performs report operation & throws an exception 
	 *    in case of an error. (and ignore error flags were not raised)
	 * 4. Performs Analysis if one or more analyzers are defined (and ignore error flags were not raised)   
	 */
	protected void handleCliCommand(CliConnection connection, String title,
			CliCommand command) throws Exception {
		// if set to buffer will only add the command to a list
		if(isBufferCommands()){
			commands.add(new CommandObject(connection, title, command));
			return;
		}
		boolean commandIgnoreError = command.isIgnoreErrors();
		command.setIgnoreErrors(forceIgnoreAnyErrors || commandIgnoreError);
		try {
			connection.handleCliCommand(title, command);
		}finally {
			command.setIgnoreErrors(commandIgnoreError);
		}
		setTestAgainstObject(command.getResult());
	}

	/*
	 * 8/3/2007 - changed by Golan Derazon from protected to public to enable
	 * classes which don't inherit from CliApplication to use this method. This
	 * is part of an attempt to promote usage of this class in composite design
	 * pattern.
	 */
	public void handleCliCommand(String title, CliCommand command)
			throws Exception {
		handleCliCommand(conn.getCli(), title, command);
	}
	
	/**
	 * Handle console command. Use the console cli connection to execute the cli command
	 * @param title command title
	 * @param command the command object
	 * @throws Exception
	 */
	public void handleConsoleCommand(String title, CliCommand command) throws Exception{
		handleCliCommand(conn.getConsole(), title, command);
	}
	/**
	 * Waits for device to reload and regain its prompt. This utility makes sure
	 * device prompt will return during the time out. Priodic reconnect tries
	 * are done every 10 seconds. when calling the method without the first
	 * parameter, the default time will be 10 seconds.
	 * 
	 * @param timeout
	 *            int the number of seconds after which the device prompt must
	 *            return.
	 */
	public void waitForDeviceToReload(long timeout) throws Exception {
		waitForDeviceToReload(10000, timeout);
	}

	/**
	 * Waits for device to reload and regain its prompt. This utility makes sure
	 * device will not be connected before the first timeout . After that the
	 * device will be checked for prompt return during the time out. Priodic
	 * reconnect tries are done every 10 seconds
	 * 
	 * @param timeToWaitbeforeReboot
	 *            the time to wait for the machine to gracfully stop all
	 *            services
	 * @param timeout
	 *            int the number of seconds after which the device prompt must
	 *            return
	 * @throws Exception
	 */
	public void waitForDeviceToReload(long timeToWaitbeforeReboot,
			long timeout) throws Exception {
		long startTime = java.lang.System.currentTimeMillis();
		// initial time to wait before starting to handle the re-connection (let
		// the machine gracefully close all services
		report
				.report(getName()
						+ " waiting for device to gracefully close all services. timeout: "
						+ timeToWaitbeforeReboot / 1000 + " seconds");
		Thread.sleep(timeToWaitbeforeReboot);
		report.report(getName() + " waiting for device to reload. timeout: "
				+ timeout / 1000 + " seconds");
		while (true) {
			if (timeout > 0
					&& java.lang.System.currentTimeMillis() - startTime > timeout) {
				throw new Exception(getName()
						+ " waitForDeviceReload fail, timeout: " + timeout);
			}
			Thread.sleep(10000);
			/* debug: */report
					.report(" waiting for device to reload. Time elapsed "
							+ ((java.lang.System.currentTimeMillis() - startTime) / 1000)
							+ " sec");
			conn.getCli().close();// Just In Case ...
			try {
				conn.getCli().connect();// re-connect
				conn.getCli().setClose(false);
				return;
			} catch (Exception e) {
			}
		}
	}

	public void analyze(AnalyzerParameter parameter, boolean silent, boolean throwException)  throws AnalyzerException {
		// if buffer is set to true will add the analyzer to the buffer
		if(bufferCommands){
			commands.add(new AnalyzeObject(parameter, silent, throwException));
		} else {
			super.analyze(parameter, silent, throwException);
		}
	}
	/**
	 * Execute the command / analysis buffer in a seperated thred.
	 * will return imidatly.
	 *
	 */
	public void executeBuffer(){
		executor = new Executor(commands, this);
		setBufferCommands(false);
		executor.start();
	}
	/**
	 * Wait for the execution buffer to end.
	 * If the execution fail an exception will be thrown
	 * @param timeout the maximum time to wait.
	 * @throws Exception
	 */
	public void waitForBufferExecution(long timeout) throws Exception{
		if(executor == null){
			return;
		}
		try {
			executor.join(timeout);
			if(executor.isAlive()){
				executor.interrupt();
				throw new Exception("Wait for execution timeout");
			}
			if(executor.isFailed()){
				if(executor.getThrown() != null){
					throw executor.getThrown();
				} else if(executor.getAnalyzeException() != null){
					throw executor.getAnalyzeException();
				}
			}
		} finally {
			executor = null;
		}
		
	}
	
	/**
	 * @see #handleCliCommand
	 */
	public void cliCommand(String command) throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[] { command });
		handleCliCommand(command, cmd);
	}

	
	/**
	 * @see #handleCliCommand
	 */	
	public void cliCommand(String command, long timeout) throws Exception {
		CliCommand cmd = new CliCommand();
		cmd.setCommands(new String[] { command });
		cmd.setTimeout(timeout);
		handleCliCommand(command, cmd);

	}	
	public void setPrintStream(PrintStream printStream){
		conn.cli.setPrintStream(printStream);
	}
	
	public Prompt getResultPrompt(){
		if (conn.cli == null){
			return null;
		}
		return conn.cli.getResultPrompt();
	}

	public void addError(Test arg0, Throwable arg1) {
		
	}

	public void addFailure(Test arg0, AssertionFailedError arg1) {
		
	}

	public void endTest(Test arg0) {
		
	}

	public void startTest(Test arg0) {
		setBufferCommands(false);
	}

	public boolean isBufferCommands() {
		return bufferCommands;
	}

	public void setBufferCommands(boolean bufferCommands) {
		this.bufferCommands = bufferCommands;
		if(bufferCommands){
			commands = new ArrayList<Object>();
		} else {
			commands = null;
		}
	}
}

class CommandObject {
	private CliConnection connection;
	private String title;
	private CliCommand command;
	public CommandObject(CliConnection connection, String title, CliCommand command){
		this.connection = connection;
		this.title = title;
		this.command = command;
	}
	public CliCommand getCommand() {
		return command;
	}
	public void setCommand(CliCommand command) {
		this.command = command;
	}
	public CliConnection getConnection() {
		return connection;
	}
	public void setConnection(CliConnection connection) {
		this.connection = connection;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}

class AnalyzeObject {
	private AnalyzerParameter parameter;
	private boolean silent;
	private boolean throwException;
	public AnalyzeObject(AnalyzerParameter parameter, boolean silent, boolean throwException){
		this.parameter = parameter;
		this.silent = silent;
		this.throwException = throwException;
	}
	public AnalyzerParameter getParameter() {
		return parameter;
	}
	public void setParameter(AnalyzerParameter parameter) {
		this.parameter = parameter;
	}
	public boolean isSilent() {
		return silent;
	}
	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	public boolean isThrowException() {
		return throwException;
	}
	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}
}
/**
 * Execute commands and analysis processes
 * @author arieli
 *
 */
class Executor extends Thread {
	List<Object> commands;
	CliApplication application;
	boolean failed = false;
	Exception thrown;
	AnalyzerException analyzeException;
	public Executor(List<Object> commands,CliApplication application){
		this.commands = commands;
		this.application = application;
	}
	public void run(){
		for(Object command: commands){
			if(command instanceof CommandObject){
				CommandObject cmd = (CommandObject)command;
				try {
					application.handleCliCommand(cmd.getConnection(), cmd.getTitle(), cmd.getCommand());
				} catch (Exception e) {
					failed = true;
					thrown = e;
					return;
				}
			} else if(command instanceof AnalyzeObject){
				AnalyzeObject analyze = (AnalyzeObject)command;
				try {
					application.analyze(analyze.getParameter(), analyze.isSilent(), analyze.isThrowException());
				} catch (AnalyzerException e){
					failed = true;
					analyzeException = e;
					return;
				}
			}
		}
	}
	public AnalyzerException getAnalyzeException() {
		return analyzeException;
	}
	public void setAnalyzeException(AnalyzerException analyzeException) {
		this.analyzeException = analyzeException;
	}
	public boolean isFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	public Exception getThrown() {
		return thrown;
	}
	public void setThrown(Exception thrown) {
		this.thrown = thrown;
	}
}
