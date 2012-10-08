/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Generic systemobject.Cli
 * 
 * @author Guy Arieli
 */
public class Cli {
	//private static Logger log = Logger.getLogger(Cli.class.getName());
	private static final String ENTER = "\r";

	private String enterStr = ENTER;

	protected Terminal terminal = null;

	private StringBuffer result = new StringBuffer();

	private Prompt resultPrompt;

	/**
	 * if set to true will send enterString when prompt wait timeout is received
	 */
	private boolean graceful = false;

	private long startTime = 0;

	/**
	 * in case that graceful is true, how many times press enter
	 */
	private int waitWithGraceCounter = 0;
	
	private boolean dontWaitForPrompts = false;

	/**
	 * Create a systemobject.terminal.Cli object
	 * 
	 * @param terminal The terminal to use, can be Telnet, Rs232, Cmd or SSH
	 *
	 * @exception IOException
	 */
	public Cli(Terminal terminal) throws IOException {
		this.terminal = terminal;
		terminal.connect();
	}

	/**
	 * add a Prompt to the terminal Prompts list
	 * 
	 * @param prompt	the Prompt to add
	 */
	public void addPrompt(Prompt prompt) {
		terminal.addPrompt(prompt);
	}

	/**
	 * locate the matching Terminal Prompt object by the given Prompt String
	 * 
     * @param prompt	the Prompt String
     * @return	the Prompt object from the Terminal Prompts list or null if none was found
	 */
	public Prompt getPrompt(String prompt) {
		return terminal.getPrompt(prompt);
	}

	/**
	 * Sets the print stream to which the stream of the connection 
	 * will be dumped to.
	 * Set the print stream to System.out to dump terminal stream to the console,
	 * Set print stream to null to turn off stream dump.
	 */
	public void setPrintStream(PrintStream printStream) {
		terminal.setPrintStream(printStream);
	}

	/**
	 * send an enter string and wait for a matching Prompt in 60 seconds
	 * 
	 * @exception IOException
	 */
	public void login() throws Exception {
		login(60000);
	}

	/**
	 * send an enter string and wait for a matching Prompt in given time
	 * 
	 * @param timeout	the time after which a timeout exception will be thrown
	 * @throws Exception
	 */
	public void login(long timeout) throws Exception {
		login(timeout, false);
	}

	/**
	 * send an enter string and wait for a matching Prompt in given time 
	 * 
	 * @param timeout	the time after which a timeout exception will be thrown
	 * @param delayedTyping	if True then terminal buffer reading will be delayed (20 ms sleep between each byte)
	 * @throws Exception
	 */
	public void login(long timeout, boolean delayedTyping) throws Exception {
		Thread.sleep(1000);
		command(null, timeout, true, delayedTyping);
	}

	/**
	 * Sends the command without waiting to any prompt.
	 * If delayedTyping is true, sends each byte desperately + small wait after each byte.
	 */
	public void sendString(String command, boolean delayedTyping) throws Exception {
		terminal.sendString(command, delayedTyping);
	}

	/**
	 * Send a command and wait for prompt, with a timeout of 20 seconds
	 * 
	 * @param command Command text.
	 * 
	 * @exception IOException
	 */
	public void command(String command) throws Exception {
		command(command, 20000, true, false);
	}

	/**
	 * Get the systemobject.terminal.Cli output text.
	 * 
	 * @return systemobject.terminal.Cli capture text.
	 */
	public String getResult() {
		String toReturn = result.toString();
		result = new StringBuffer();
		return toReturn;
	}

	/**
	 * Returns the prompt which identification triggered the termination
	 * of the cli operation.
	 */
	public Prompt getResultPrompt() {
		return resultPrompt;
	}

	/**
	 * Send a command and wait for prompt.
	 * 
	 * @param command  Command text.
	 * @param timeout  Command timeout in miliseconds
	 * @param addEnter If true enterString will be add to the command.
	 * 
	 * @exception IOException
	 */
	public void command(String command, long timeout, boolean addEnter) throws Exception {
		command(command, timeout, addEnter, false);
	}

	/**
	 * Send a command and wait for prompt.
	 * 
	 * @param command  Command text.
	 * @param timeout  Command timeout in miliseconds
	 * @param addEnter If True, defined enter string will be added to the command.
	 * 
	 * @exception IOException
	 */
	public void command(String command, long timeout, boolean addEnter, boolean delayedTyping) throws Exception {
		command(command, timeout, addEnter, delayedTyping, (String) null);
	}

	/**
	 * Send a command and wait for prompt.
	 * 
	 * @param command	Command text.
	 * @param timeout	Command timeout in miliseconds
	 * @param addEnter	If True, defined enter string will be added to the command.
	 * @param delayedTyping	if True then terminal buffer reading will be delayed (20 ms sleep between each byte)
	 * @param promptString	a Prompt String to wait for (if null then default terminal prompts will be used)
	 * @throws Exception
	 */
	public void command(String command, long timeout, boolean addEnter, boolean delayedTyping, String promptString) throws Exception {
		if (promptString != null) {
			command(command, timeout, addEnter, delayedTyping, new String[] { promptString });
		} else {
			command(command, timeout, addEnter, delayedTyping, (String[]) null);
		}

	}

	/**
	 * Send a command and wait for all given Prompts Strings.
	 * 
	 * @param command	Command text.
	 * @param timeout	Command timeout in miliseconds
	 * @param addEnter	If True, defined enter string will be added to the command.
	 * @param delayTyping	if True then terminal buffer writing will be delayed (20 ms sleep between each byte)
	 * @param promptStrings	an array of all Prompt Strings that should be found (if null then default terminal prompts will be used)
	 * @throws Exception
	 */
	public void command(String command, long timeout, boolean addEnter, boolean delayTyping, String[] promptStrings) throws Exception {
		command(command, timeout, addEnter, delayTyping, promptStrings, null);
	}

	/**
	 * send a given command with given parameters and wait for given prompts
	 * 
	 * @param command	the command to send
	 * @param timeout	the time to wait for a Prompt before throwing a timeout exception
	 * @param addEnter	if True will add the predefined enter string to the command before sending
	 * @param delayedTyping	if True will sleep 20 ms between each typed byte sent to the terminal
	 * @param promptStrings	if not null then wait for <b>ALL</b> Strings in the given Array to be found in the terminal
	 * @param prompts	if promptStrings is null, wait for ONE of the  prompts, if any exists
	 * @throws Exception
	 */
	public void command(String command, long timeout, boolean addEnter, boolean delayedTyping, String[] promptStrings, Prompt[] prompts)
			throws Exception {
		
		resultPrompt = null;
		ArrayList<Prompt> defaultPromts = null;
		if (prompts != null) {
			defaultPromts = terminal.getPrompts();
			terminal.removePrompts();
			for (int i = 0; i < prompts.length; i++) {
				terminal.addPrompt(prompts[i]);
			}
		}
		try {
			if (command != null) {
				if (addEnter) {
					command = command + getEnterStr(); 
				}
				result.append(terminal.getResult());
				terminal.sendString(command, delayedTyping);
			}
			if (dontWaitForPrompts){
				finishCommandExecution();
				return;
			}
			
			startTime = System.currentTimeMillis();
			if (promptStrings != null) {
				terminal.waitForPrompt(promptStrings, timeout);
				finishCommandExecution();
				return;
			}
			Prompt prompt = waitWithGrace(timeout);
			waitWithGraceCounter = 0;
			while (true) {
				if (prompt == null){
					finishCommandExecution();
					return;
				}
				if (timeout > 0) {
					if (System.currentTimeMillis() - startTime > (timeout)) {
						throw new IOException("timeout: " + timeout);
					}
				}
				/*
				 * If the scrollEnd property of the found prompt is set to true
				 * the check for terminal scroll end is skipped.
				 */
				if (prompt.dontWaitForScrollEnd()) {
					if (prompt.isCommandEnd()) {
						break;
					}
				} else {
					if (terminal.isScrallEnd()) {
						if (prompt.isCommandEnd()) {
							break;
						}
					} else {
						waitWithGraceCounter++;
						prompt = waitWithGrace(timeout);
						continue;
					}
				}
				String stringToSend = prompt.getStringToSend();
				if (stringToSend != null) {
					if (prompt.isAddEnter()) {
						stringToSend = stringToSend + getEnterStr(); //ENTER;
					}
					terminal.sendString(stringToSend, delayedTyping);
				}
				prompt = waitWithGrace(timeout);
			}

			resultPrompt = prompt;
		} finally {
			finishCommandExecution();
			if (defaultPromts != null) {
				terminal.setPrompts(defaultPromts);
			}
		}
	}
	
	private void finishCommandExecution(){
		dontWaitForPrompts = false;
		result.append(terminal.getResult());
	}

	/**
	 * Close the systemobject.terminal.Cli connection.
	 * 
	 * @exception IOException
	 */
	public void close() throws IOException {
		if (terminal != null && terminal.isConnected()) {
			terminal.disconnect();
		}
	}

	/**
	 * Reads the stream in the input buffer 
	 * and returns it as a String.
	 */
	public String read() throws Exception {
		return terminal.readInputBuffer();
	}

	/**
	 * connect the related terminal if it is not connected already
	 * @throws IOException
	 */
	public void connect() throws IOException {
		if (terminal != null && !terminal.isConnected()) {
			terminal.connect();
		}
	}

	/**
	 * disconnects the related terminal and then reconnects
	 * @throws IOException
	 */
	public void reconnect() throws IOException {
		
		close();
		connect();
	}

	/**
	 * checks if the current status of the terminal is "connected" or not
	 * @return true if the terminal is not null and connected
	 */
	public boolean isConnected() {
		if (terminal == null) {
			return false;
		}
		return terminal.isConnected();
	}

	/**
	 * Check if working in graceful mode.
	 * If true will send ENTER if prompt wait fail
	 * @param timeout the timeout to wait
	 * @return the prompt
	 * @throws Exception
	 */
	private Prompt waitWithGrace(long timeout) throws Exception {
		
		try {
			Prompt p = terminal.waitForPrompt(timeout);
			result.append(terminal.getResult());
			return p;
		} catch (Exception e) {
			if ((!graceful) || (waitWithGraceCounter > 2)) {
				throw e;
			}

			return sendEnter(Math.min(15 * 1000, timeout));
		}

	}

	/**
	 * sendEnter to terminal
	 * 
	 * @param timeout
	 * 				the prompt timeout
	 * @return	the prompt found
	 * @throws Exception
	 */
	private Prompt sendEnter(long timeout) throws Exception {
		startTime = System.currentTimeMillis();
		terminal.sendString(getEnterStr(), false);
		result.append(terminal.getResult());

		Prompt p = terminal.waitForPrompt(timeout);
		result.append(terminal.getResult());
		return p;
	}
	
	/**
	 * get the Terminal defined enter string
	 * 
	 * @return
	 */
	public String getEnterStr() {
		return enterStr;
	}

	/**
	 * set the terminal enter string (used when sending command or for graceful wait)
	 * 
	 * @param enterStr	the enter String (default is "\r")
	 */
	public void setEnterStr(String enterStr) {
		this.enterStr = enterStr;
	}

	/**
	 * checks if graceful wait is enabled
	 * 
	 * @return	the Graceful flag
	 */
	public boolean isGraceful() {
		return graceful;
	}

	/**
	 * if set to True then enter String will be sent after Timeout was reached.<br>
	 * the number of times the enter string will be sent can be configured using the <I>setWaitWithGraceCounter(int)</I>
	 * 
	 * @param graceful
	 */
	public void setGraceful(boolean graceful) {
		this.graceful = graceful;
	}

	/**
	 * in case that graceful is true, how many times press enter
	 * @return how many times press enter
	 */
	public int getWaitWithGraceCounter() {
		return waitWithGraceCounter;
	}

	/**
	 * 
	 * @param waitWithGraceCounter in case that graceful is true, how many times press enter
	 */
	public void setWaitWithGraceCounter(int waitWithGraceCounter) {
		this.waitWithGraceCounter = waitWithGraceCounter;
	}

	/**
	 * @return	true if the CLI was signaled to skip prompt checking
	 */
	public boolean isDontWaitForPrompts() {
		return dontWaitForPrompts;
	}

	/**
	 * Default is false, returned to false after each command execution.
	 * @param dontWaitForPrompts	if set to true will skip prompt waiting
	 */
	public void setDontWaitForPrompts(boolean dontWaitForPrompts) {
		this.dontWaitForPrompts = dontWaitForPrompts;
	}

}
