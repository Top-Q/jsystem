/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;

public class FilterInputStream extends InOutInputStream implements Runnable {
	String toFilter;
	IOException ioExp = null;
	StringBuffer buf = new StringBuffer();
	String filterFirstChar;
	boolean inconclusive = false;
	Thread thread;
	public FilterInputStream( String toFilter){
		this.toFilter = toFilter;
		filterFirstChar = toFilter.substring(0,1);
	}
	public void startThread(){
		thread = new Thread(this);
		thread.setName(Thread.currentThread().getName());
		thread.start();
	}
	public int read() throws IOException {
    	if(ioExp != null){
    		throw ioExp;
    	}
    	synchronized(this){
        	if(buf.length() == 0){
        		try {
					wait();
				} catch (InterruptedException e) {
					throw new IOException("Interrupted");
				}
        	}
    		
    	}
    	char c = buf.charAt(0);
    	buf.deleteCharAt(0);
    	inconclusive = false;
		return c;
	}
	public void close() throws IOException{
		thread.interrupt();
		super.close();
	}
    public int available() throws IOException {
    	if(ioExp != null){
    		throw ioExp;
    	}
    	// There could be a thoretical problem if the filter string is not recieved within the 50 ms.
    	if(buf.length() > 0 && inconclusive == true){
    		try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new IOException("interrupted");
			}
    	}
    	return buf.length();
    }

	public void run() {
		
		while(true){
			try {
				int c = in.read();
				if (c < 0){
					in.close();
					ioExp = new IOException("Read -1 char");
					return;
				}
				buf.append((char)c);

				if(buf.indexOf(filterFirstChar) >= 0){
					inconclusive = true;
				} else {
					inconclusive = false;
				}
			} catch (IOException e) {
				ioExp = e;
				return;
			}
			int filterStartIndex = buf.indexOf(toFilter);
			if(filterStartIndex >= 0){
				buf.delete(filterStartIndex,filterStartIndex + toFilter.length());
				System.err.println("\n**** FILTER STRING WAS FOUND ****");
				inconclusive = false;
			} else {
				synchronized (this) {
					notify();
				}
			}
			if(Thread.currentThread().isInterrupted()){
				return;
			}
		}
	}

}
