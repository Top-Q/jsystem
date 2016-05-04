/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Terminal {
	
	Logger log = Logger.getLogger(Terminal.class.getName());
    protected static final int IN_BUFFER_SIZE = 65536;
    private StringBuffer result = new StringBuffer();
    protected OutputStream out = null;
    protected InputStream in = null;
    protected int bufChar = 10;
    protected long scrollEndTimeout = 200;
    ArrayList<Prompt> prompts = new ArrayList<Prompt>();

    public abstract void connect() throws IOException;
    public abstract void connect(int port) throws IOException;
    public abstract void disconnect() throws IOException;
    public abstract boolean isConnected();
    public abstract String getConnectionName();
    
    private boolean delayedTyping = false;
    private boolean asciiFilter = true;
    private PrintStream printStream = System.out;
    private long keyTypingDelay = 20;
    private boolean ignoreBackSpace = false;
    private String charSet = "ASCII";
    
    /**
     * create a filter input stream:<br>
     * 1) add the current input stream to the given stream.<br>
     * 2) set the new input stream to the given one.<br>
     * 
     * @param input	the filter stream
     */
    public void addFilter(InOutInputStream input) {
    	input.setInputStream(in);
    	in = input;
    }

    /**
     * send a given string to the terminal (no prompt waiting)
     * 
     * @param command	the command to send
     * @param delayedTyping	if True will sleep keyTypingDelay ms between each typed byte entered to the terminal
     * @throws IOException	
     * @throws InterruptedException
     */
    public synchronized void sendString(String command, boolean delayedTyping) throws IOException, InterruptedException{
    	
    	byte[] buf = command.getBytes(charSet);
        
        // Do not override if delayed typing was set to TRUE from elsewhere
        if (this.delayedTyping != true) {
        	setDelayedTyping(delayedTyping);
        }
        
        if (isDelayedTyping()) {
            for (int i = 0; i < buf.length; i++) {
                out.write(buf[i]);
                out.flush();
                Thread.sleep(keyTypingDelay);
            }
        } else {
            out.write(buf);
            out.flush();
        }
    }
    
    /**
     * get the input buffer data
     * @return	a String of all data in the input buffer
     * @throws Exception
     */
	public String readInputBuffer() throws Exception {
		int avail = in.available();
		if (avail <= 0 ) {
			return "";
		}
		byte[] bytes = new byte[avail];
		in.read(bytes);
		return new String(bytes, charSet);
	}

	/**
	 * add a remark to the result buffer
	 * 
	 * @param remark	the String to add
	 */
    public synchronized void addRemark(String remark) {
        result.append(remark);
    }

    /**
     * checks if there is more input in the buffer
     * 
     * @return	True if there isn't any new input after ${scrollEndTimeout} ms
     * @throws Exception
     */
    public synchronized boolean isScrallEnd() throws Exception{
    	if (scrollEndTimeout == 0) { // if set to 0 always return true.
    		return true;
    	}
    	int avil0 = in.available();
    	Thread.sleep(scrollEndTimeout);
    	int avil1 = in.available();
    	if (avil1 > bufChar) {
    		return false;
    	}
    	if (avil0 == avil1) { // no change after 1/2 time and avail under bufChar
    		return true;
    	}
    	Thread.sleep(scrollEndTimeout);
    	if (in.available() < bufChar) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * wait for <b>ALL</b> Strings in the given Array to be found, in the given time
     * 
     * @param prompts	the Strings to check
     * @param timeout	the time (in ms) before throwing a timeout exception
     * @throws IOException
     * @throws InterruptedException
     */
    public synchronized void waitForPrompt(String[] prompts, long timeout) throws IOException, InterruptedException{
    	
    	long startTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        while (true) {
            if (timeout > 0) {
                if (System.currentTimeMillis() - startTime > timeout) {
                    result.append(sb);
                    throw new IOException("timeout: " + timeout);
                }
            }
            int avail = in.available();
            if (avail > 0) {
                while (avail > 0) {
                    int b = in.read();
                    if (b < 0) {
                        avail = in.available();
                    	continue;
                    }
                    if (b == 8 && !isIgnoreBackSpace()) {
                        sb.append('B');
                    }
                    if (b >= 127 ||
                             b < 9 ||
                             (b >= 14 && b <= 31) ||
                             b == 11 ||
                             b == 12) { // not ascii byte will be ignored
                    	avail = in.available();
                        continue;
                    }
                    sb.append((char)b);
                    if (printStream != null) {
                    	printStream.print((char)b);                    
                    }
                    
                    String bufString = sb.toString();
                    boolean allPromptsFound = true;
                    for (int i = 0; i < prompts.length; i++) {
                        if (bufString.indexOf(prompts[i]) < 0) {
                        	allPromptsFound = false;
                        	break;
                        }
                    }
                    if (allPromptsFound) {
                        result.append(sb);
                    	return;
                    }
                    avail = in.available();
                    if (timeout > 0) {
                        if (System.currentTimeMillis() - startTime > timeout) {
                            result.append(sb);
                            throw new IOException("timeout: " + timeout);
                        }
                    }
                }
            } else {
                Thread.sleep(10);
            }
        }
    	
    }

    /**
     * wait for one of the terminal defined prompts to be found in the input buffer
     * 
     * @param timeout	the time on which timeout exception will be thrown
     * @return	the found Prompt if any was found
     * @throws IOException	if Timeout was reached
     * @throws InterruptedException
     */
    public synchronized Prompt waitForPrompt(long timeout) throws IOException, InterruptedException{
    	
        long startTime = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();
        if (prompts == null || prompts.size() == 0) {
        	return null;
        }
        while (true) {
            if (timeout > 0) {
                if (System.currentTimeMillis() - startTime > timeout) {
                    result.append(sb);
                    throw new IOException("timeout: " + timeout);
                }
            }
            int avail = in.available();
            if (avail > 0) {
                while (avail > 0) {
                    int b = in.read();
                    if (b < 0) {
                        avail = in.available();
                    	continue;
                    }
                    if (b == 8 && !isIgnoreBackSpace()) {
                        sb.append('B');
                    }
                    if (asciiFilter) {
                        if (b >= 127 || b < 9 || (b >= 14 && b <= 31) || b == 11 || b == 12) { // not ascii byte will be ignored
                        	avail = in.available(); // if the last value is a non-ascii character
                            continue;
                       }
                    }
                    sb.append((char)b);
                    if (printStream != null) {
                    	printStream.print((char)b);
                    }
                    int promptArraySize = prompts.size();
                    boolean skipNonExact = false;
                    if (in.available() > 40 + bufChar) {
                    	skipNonExact = true;
                    }
                    for (int j = 0; j < promptArraySize; j++) {
                        Prompt prompt = (Prompt)prompts.get(j);
                        if (prompt == null || prompt.getPrompt() == null) {
                            continue;
                        }
                        String bufString = sb.toString();
                        if (prompt.isRegularExpression()) {
                        	Pattern p = prompt.getPattern();
                        	Matcher m = p.matcher(bufString);
                            if (m.find()) {
                                result.append(sb);
                                return prompt;
                            }
                        } else {
                        	// accelerate cases with long output
                        	if (!prompt.dontWaitForScrollEnd() && skipNonExact) {
                        		continue;
                        	}
                            if (bufString.endsWith(prompt.getPrompt())) {
                                result.append(sb);
                                return prompt;
                            }
                        }
                    }
                    avail = in.available();
                    /**
                     * change the timeout to be activate in a state were there is endless amount of output
                     * from the cli.
                     */ 
                    if (timeout > 0) {
                        if (System.currentTimeMillis() - startTime > timeout) {
                            result.append(sb);
                            throw new IOException("timeout: " + timeout);
                        }
                    }
                }
            } else {
                Thread.sleep(10);
            }
        }
    }

    /**
     * wait for one of the terminal Prompts for 20 seconds
     * 
     * @return	the found Prompt if any was found
     * @throws IOException	if no prompt was found after 20 seconds
     * @throws InterruptedException
     */
    public synchronized Prompt waitFor() throws IOException, InterruptedException{
    	return waitForPrompt(20000);
    }

    /**
     * get all data gathered by the input buffer
     * 
     * @return	a String of all input data gathered by the terminal
     */
    public synchronized String getResult() {
        String toRetun = result.toString();
        result = new StringBuffer();
        return toRetun;
    }

    /**
     * close input and output streams
     * 
     * @throws IOException
     */
    public void closeStreams() throws IOException{
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
    }

    /**
     * add a given Prompt to the Terminal Prompts array
     * 
     * @param promptString	the prompt String to add
     * @param isRegExp	if True then Prompt will be marked as a regular expression
     */
    public void addPrompt(String promptString, boolean isRegExp) {
        Prompt prompt = new Prompt(promptString,isRegExp);
        addPrompt(prompt);
    }
    
    /**
     * add a given Prompt to the Terminal Prompts array
     * 
     * @param prompt	the prompt to add
     */
    public void addPrompt(Prompt prompt) {
        prompts.remove(prompt);
        prompts.add(prompt);
    }
    
    /**
     * locate the matching Terminal Prompt object by the given Prompt String
     * @param prompt	the Prompt String
     * @return	the Prompt object from the Terminal Prompts list or null if none was found
     */
    public Prompt getPrompt(String prompt) {
        for (int i = 0; i < prompts.size(); i++) {
            Prompt p = (Prompt)prompts.get(i);
            if (p.getPrompt().equals(prompt)) {
                return p;
            }
        }
        return null;
    }
    
    /**
     * clear all Terminal Prompts
     */
    public void removePrompts() {
    	prompts = new ArrayList<Prompt>();
    }
    
    /**
     * get a clone of the Terminal Prompts list
     * @return
     */
    @SuppressWarnings("unchecked")
	public ArrayList<Prompt> getPrompts() {
    	return (ArrayList<Prompt>)prompts.clone();
    }
    
    /**
     * set the Terminal Prompts list
     * 
     * @param prompts	the Prompts to set
     */
    public void setPrompts(ArrayList<Prompt> prompts) {
    	this.prompts = prompts;
    }
	public int getBufChar() {
		return bufChar;
	}
	public void setBufChar(int bufChar) {
		this.bufChar = bufChar;
	}
	
	/**
	 * the time (in ms) to wait for a terminal input to be received before declaring scroll end (no more input)
	 * 
	 * @return
	 */
	public long getScrollEndTimeout() {
		return scrollEndTimeout;
	}
	
	/**
	 * the time (in ms) to wait for a terminal input to be received before declaring scroll end (no more input)
	 * 
	 * @param scrollEndTimeout
	 */
	public void setScrollEndTimeout(long scrollEndTimeout) {
		this.scrollEndTimeout = scrollEndTimeout;
	}
	
	/**
	 * signals if input String should be typed char by char with 20ms delay or all at once
	 * @return
	 */
	public boolean isDelayedTyping() {
		return delayedTyping;
	}
	
	/**
	 * if set to True then input String (command) will be typed char by char with 20ms delay<br>
	 * if set to False all String will be send at once
	 * default is false
	 * 
	 * @param delayedTyping
	 */
	public void setDelayedTyping(boolean delayedTyping) {
		this.delayedTyping = delayedTyping;
	}
	
	/**
	 * signals if ascii chars should be ignored when reading from the buffer
	 * 
	 * @return
	 */
	public boolean isAsciiFilter() {
		return asciiFilter;
	}
	
	/**
	 * if set to True then non ascii chars will be ignored
	 * 
	 * @param asciiFilter
	 */
	public void setAsciiFilter(boolean asciiFilter) {
		this.asciiFilter = asciiFilter;
	}

	/**
	 * Sets the print stream to which the stream of the connection 
	 * will be dumped to.
	 * Set the print stream to System.out to dump terminal stream to the console,
	 * Set print stream to null to turn off stream dump.
	 */
	public void setPrintStream(PrintStream printStream) {
		this.printStream = printStream;
	}
	
	/**
	 * the time (in ms) to sleep between each typed byte entered to the terminal
	 * 
	 * @return
	 */
	public long getKeyTypingDelay() {
		return keyTypingDelay;
	}
	
	/**
	 * the time (in ms) to sleep between each typed byte entered to the terminal
	 * 
	 * @param keyTypingDelay
	 */
	public void setKeyTypingDelay(long keyTypingDelay) {
		this.keyTypingDelay = keyTypingDelay;
	}

	/**
	 * Whether to ignore backspace characters or not
	 * 
	 * @param ignoreBackSpace
	 */
	public boolean isIgnoreBackSpace() {
		return ignoreBackSpace;
	}
	
	/**
	 * Whether to ignore backspace characters or not
	 * 
	 * @param ignoreBackSpace
	 */
	public void setIgnoreBackSpace(boolean ignoreBackSpace) {
		this.ignoreBackSpace = ignoreBackSpace;
	}
	
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	
	public String getCharSet() {
		return charSet;
	}
	
	public InputStream getIn() {
		return in;
	}
	
}
