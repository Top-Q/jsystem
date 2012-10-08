package il.co.topq.refactor.exception;

public class ScenarioXmlParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ScenarioXmlParseException() {
		super("Failed to parse scenario XML file. Check that file is properly constructed and is not currupted");
	}
	
}
