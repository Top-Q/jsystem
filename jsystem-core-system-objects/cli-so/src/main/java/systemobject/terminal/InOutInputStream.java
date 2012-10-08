/*
 * Created on Sep 23, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author guy.arieli
 *
 */
public abstract class InOutInputStream extends InputStream {
	protected InputStream in;
	public void setInputStream(InputStream in){
		this.in = in;
	}
	public void close() throws IOException{
		if(in != null){
			in.close();
		}
	}

}
