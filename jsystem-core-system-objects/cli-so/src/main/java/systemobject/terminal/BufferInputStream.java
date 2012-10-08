/*
 * Created on Sep 23, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;

public class BufferInputStream extends InOutInputStream implements Runnable {
	
	private StringBuffer logbuf = new StringBuffer();

	private StringBuffer buf = new StringBuffer();

	private int bufferMaxSize = 10000;

	private IOException ioExp = null;

	private Thread thread;

	public BufferInputStream() {
		logbuf = new StringBuffer();
	}

	public void startThread() {
		thread = new Thread(this);
		thread.setName(Thread.currentThread().getName());
		thread.start();
	}

	public int read() throws IOException {
		if (ioExp != null) {
			throw ioExp;
		}
		synchronized (this) {
			if (buf.length() == 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new IOException("Interrupted");
				}
			}

		}

		char c = buf.charAt(0);
		buf.deleteCharAt(0);
		return c;
	}

	public int available() throws IOException {
		if (ioExp != null) {
			throw ioExp;
		}
		return buf.length();
	}

	protected void addToBuffer(char c) {
		if (logbuf.length() == bufferMaxSize) {
			logbuf.deleteCharAt(0);
		}
		logbuf.append(c);
	}

	public String getBuffer() {
		return logbuf.toString();
	}

	/**
	 * Clean the buffer. The last line is not remove.
	 * 
	 */
	public void clean() {
		int lastEnter = logbuf.lastIndexOf("\n");
		int lastTemp = logbuf.lastIndexOf("\r");
		if (lastTemp > lastEnter) {
			lastEnter = lastTemp;
		}
		if (lastEnter > 0) {
			logbuf.delete(0, lastEnter);
		} else {
			logbuf = new StringBuffer();
		}
	}

	public int getBufferMaxSize() {
		return bufferMaxSize;
	}

	public void setBufferMaxSize(int bufferMaxSize) {
		this.bufferMaxSize = bufferMaxSize;
	}

	public void close() throws IOException {
		thread.interrupt();
		super.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {

			try {
				int avail = in.available();
				if (avail == 0) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						throw new IOException("Interrupted");
					}
					continue;
				}
				int c = in.read();
				if (c == -1) {
					in.close();
					return;
				}
				buf.append((char) c);
				addToBuffer((char) c);
				synchronized (this) {
					notify();
				}

			} catch (IOException e) {
				ioExp = e;
				return;
			}
		}
	}

	protected StringBuffer getLogbuf() {
		return logbuf;
	}

	protected void setLogbuf(StringBuffer logbuf) {
		this.logbuf = logbuf;
	}

	protected StringBuffer getBuf() {
		return buf;
	}

	protected void setBuf(StringBuffer buf) {
		this.buf = buf;
	}

	protected IOException getIoExp() {
		return ioExp;
	}

	protected void setIoExp(IOException ioExp) {
		this.ioExp = ioExp;
	}

	protected Thread getThread() {
		return thread;
	}

	protected void setThread(Thread thread) {
		this.thread = thread;
	}
}
