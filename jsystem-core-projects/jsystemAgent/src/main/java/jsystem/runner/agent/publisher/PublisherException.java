package jsystem.runner.agent.publisher;

/**
 * Exception to throw in case something went wrong while publishing.
 * 
 * @author itai_a
 * 
 */
public class PublisherException extends Exception {

	private static final long serialVersionUID = 1L;

	public PublisherException(String message) {
		super(message);
	}

	public PublisherException(String message, Throwable t) {
		super(message, t);
	}

}
