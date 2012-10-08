package il.co.topq.refactor.exception;

/**
 * @author Itai Agmon
 */

public class ExtractScenarioTestsException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExtractScenarioTestsException() {
        super("A problem occurred while trying to extract all test for a specific scenario");
    }
}
