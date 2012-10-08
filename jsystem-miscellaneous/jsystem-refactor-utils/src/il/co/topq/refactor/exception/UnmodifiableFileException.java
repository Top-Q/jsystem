package il.co.topq.refactor.exception;

import java.io.File;

/**
 * @author Itai Agmon
 */

public class UnmodifiableFileException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnmodifiableFileException(File file) {
        super("Cannot modified the file " + file +". Verify that it is not currently used by other program such as JSystem.");
    }

}
