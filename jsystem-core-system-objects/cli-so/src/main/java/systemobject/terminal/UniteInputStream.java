/*
 * Created on Oct 30, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author embext
 */
public class UniteInputStream extends InputStream {

	InputStream in;
	InputStream err;
	
	public UniteInputStream(InputStream in, InputStream err){
		this.in = in;
		this.err = err;
	}
	
	/**
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		if(err.available() > 0){
			return err.read();
		} else {
			return in.read();
		}
	}
	
    public int available() throws IOException {
		return in.available() + err.available();
	}
    
    public void close() throws IOException{
    	err.close();
    	in.close();
    }

}
