package jsystem.extensions.sourcecontrol;

public class SourceControlException extends Exception {

	private static final long serialVersionUID = 1L;

	public SourceControlException(String message) {
		super(message);
	}

	public SourceControlException(String message, Throwable cause) {
		super(message, cause);
	}

}
