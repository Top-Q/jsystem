/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

public class ThreadedBufferedInputStream extends InputStream {

	ReaderThread reader;
	public ThreadedBufferedInputStream(InputStream in){
		reader = new ReaderThread(in);
		reader.setName(Thread.currentThread().getName());
		reader.start();
	}
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		return reader.read();
	}
	public int available() throws IOException{
		return reader.getSize();
	}

}
class ReaderThread extends Thread{
	IntBuffer buffer;
	int bufferSize;
	InputStream in;
	IOException e;
	public ReaderThread(InputStream in){
		System.out.println("ReaderThread init");
		buffer = IntBuffer.allocate(1000);
		this.in = in;
		bufferSize = 0;
	}
	public void run(){
		while (true){
			int avail;
			try {
				avail = in.available();
				if(avail > 0){
					System.out.println("Avail: " + avail);
					synchronized(buffer){
						for(int i = 0; i < avail; i++){
							System.out.println("read from buffer index: " + i);
							int toPut = in.read();
							buffer.put(toPut);
							System.out.print((char)toPut);
							bufferSize++;
							buffer.notify();
						}
					}
					System.out.println("Buffer was read");
				}
			} catch (IOException e) {
				this.e = e;
				return;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
			}
		}
	}
	public int read() throws IOException{
		if(e != null){
			throw e;
		}
		synchronized(buffer){
			if(bufferSize == 0){
				try {
					wait();
				} catch (InterruptedException e) {
					throw new IOException("Interrupted");
				}
			}
			System.out.println("Buffer size: " + bufferSize);
			bufferSize--;
			return buffer.get();
		}
	}
	public int getSize() throws IOException{
		synchronized(buffer){
			return bufferSize + in.available();
		}
	}
}
