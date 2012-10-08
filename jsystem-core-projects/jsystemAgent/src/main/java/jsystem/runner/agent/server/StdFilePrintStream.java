/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.server;

import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * A PrintStream that send it's output to 2 PrintStream and merge them See
 * @see RunnerEngineImpl#redirectOutputStream
 * @author guy.arieli
 */
public class StdFilePrintStream extends PrintStream {
	PrintStream out2;
	public StdFilePrintStream(PrintStream out, PrintStream out2) throws FileNotFoundException {
		super(out, true);
		this.out2 = out2;
	}
	public void write(int b) {
		super.write(b);
		if (out2 != null) {
			out2.write(b);
		}
	}
	public void write(byte buf[], int off, int len) {
		super.write(buf, off, len);
		if (out2 != null) {
			out2.write(buf, off, len);
		}
	}
}
