/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.util.regex.Pattern;

/**
 * The prompt class is used to define expected prompts to wait for. <br>
 * <br>
 * Variables: <br>
 * ----------- <br>
 * <br>
 * prompt - String that defines the prompt content.<br>
 * default is null<br>
 * <br>
 * isRegularExpression - defines if the prompt string should be searched as
 * written or as a regular expression.<br>
 * default is false<br>
 * <br>
 * dontWaitForScrollEnd - defines whether prompt should appear after all input
 * was read (wait until all input was received)<br>
 * default is false<br>
 * <br>
 * commandEnd - does this prompt indicate the end of the command?<br>
 * default is false<br>
 * <br>
 * stringToSend - the String to send to the terminal if the prompt was found but
 * command wasn't terminated yet <br>
 * Used for prompts that require input to continue, for example:<br>
 * <b>Prompt p = new Prompt("enter user:",false);<br>
 * p.setStringToSend("admin");<br>
 * </b> default is null<br>
 * <br>
 * addEnter - True means an enter String will be added to the stringToSend<br>
 * default is true<br>
 * <br>
 * pattern - the pattern for the regular expression (optional) if this is a
 * regularExpression prompt<br>
 * default is null<br>
 * <br>
 * 
 * 
 */
public class Prompt {
	private String prompt;

	private boolean regularExpression = false;

	private boolean dontWaitForScrollEnd = false;

	private boolean commandEnd = false;

	private String stringToSend = null;

	private boolean addEnter = true;

	private boolean regexCaseInsensitive = false;

	private Pattern pattern = null;

	/**
	 * a new empty prompt, with default values
	 */
	public Prompt() {

	}

	/**
	 * a new Prompt with given prompt String
	 * 
	 * @param prompt
	 *            String that defines the prompt to search
	 * @param isRegExp
	 *            if True then the Prompt is a regular expression
	 */
	public Prompt(String prompt, boolean regularExpression) {
		this(prompt, regularExpression, false);
	}

	/**
	 * 
	 * @param prompt
	 * @param regularExpression
	 * @param commandEnd
	 */
	public Prompt(String prompt, boolean regularExpression, boolean commandEnd) {
		this(prompt, regularExpression, commandEnd, false);
	}

	/**
	 * 
	 * @param prompt
	 * @param regularExpression
	 * @param commandEnd
	 * @param regexCaseInsensitive
	 */
	public Prompt(String prompt, boolean regularExpression, boolean commandEnd, boolean regexCaseInsensitive) {
		this(prompt, regularExpression, false, commandEnd, null, true, regexCaseInsensitive);
	}

	/**
	 * 
	 * @param prompt
	 * @param regularExpression
	 * @param stringToSend
	 * @param addEnter
	 */
	public Prompt(String prompt, boolean regularExpression, String stringToSend, boolean addEnter) {
		this(prompt, regularExpression, stringToSend, addEnter, false);
	}

	/**
	 * 
	 * @param prompt
	 * @param regularExpression
	 * @param stringToSend
	 * @param addEnter
	 * @param regexCaseInsensitive
	 */
	public Prompt(String prompt, boolean regularExpression, String stringToSend, boolean addEnter, boolean regexCaseInsensitive) {
		this(prompt, regularExpression, false, false, stringToSend, addEnter, regexCaseInsensitive);
	}

	/**
	 * 
	 * @param prompt
	 * @param regularExpression
	 * @param dontWaitForScrollEnd
	 * @param commandEnd
	 * @param stringToSend
	 * @param addEnter
	 * @param regexCaseInsensitive
	 */
	public Prompt(String prompt, boolean regularExpression, boolean dontWaitForScrollEnd, boolean commandEnd, String stringToSend,
			boolean addEnter, boolean regexCaseInsensitive) {
		super();
		setPrompt(prompt);
		setRegularExpression(regularExpression);
		setDontWaitForScrollEnd(dontWaitForScrollEnd);
		setCommandEnd(commandEnd);
		setStringToSend(stringToSend);
		setAddEnter(addEnter);
		setRegexCaseInsensitive(regexCaseInsensitive);
	}

	/**
	 * get the Prompt string to search for
	 * 
	 * @return
	 */
	public String getPrompt() {
		return prompt;
	}

	/**
	 * set the Prompt string to search for<br>
	 * default is null
	 * 
	 * @param prompt
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	/**
	 * @return True if Prompt is defined to be a regular expression
	 */
	public boolean isRegularExpression() {
		return regularExpression;
	}

	/**
	 * signal if this Prompt is a regular expression<br>
	 * default is false
	 * 
	 * @param regularExpression
	 */
	public void setRegularExpression(boolean regularExpression) {
		this.regularExpression = regularExpression;
	}

	public boolean equals(Object o) {
		return (o instanceof Prompt && ((Prompt) o).getPrompt().equals(getPrompt()));
	}

	/**
	 * should prompt appear after all input was read?
	 * 
	 * @return True if it shouldn't wait, False if all input should be read
	 *         first
	 */
	public boolean dontWaitForScrollEnd() {
		return dontWaitForScrollEnd;
	}

	/**
	 * signal if the prompt should appear after all input was read<br>
	 * default is false
	 * 
	 * @param scrollEnd
	 */
	public void setDontWaitForScrollEnd(boolean scrollEnd) {
		dontWaitForScrollEnd = scrollEnd;
	}

	/**
	 * the String to send to the terminal if the prompt was found
	 * 
	 * @return
	 */
	public String getStringToSend() {
		return stringToSend;
	}

	/**
	 * the String to send to the terminal if the prompt was found
	 * 
	 * @param stringToSend
	 */
	public void setStringToSend(String stringToSend) {
		this.stringToSend = stringToSend;
	}

	/**
	 * should an enter string be added to the stringToSend?
	 * 
	 * @return
	 */
	public boolean isAddEnter() {
		return addEnter;
	}

	/**
	 * signal if a an enter string should be added to the stringToSend<br>
	 * default is true
	 * 
	 * @param addEnter
	 */
	public void setAddEnter(boolean addEnter) {
		this.addEnter = addEnter;
	}

	/**
	 * does this prompt indicate the end of the command?
	 * 
	 * @return
	 */
	public boolean isCommandEnd() {
		return commandEnd;
	}

	/**
	 * signal that this prompt indicates the end of the command<br>
	 * default is false
	 * 
	 * @param commandEnd
	 *            if True then after the prompt is found, stop reading the input
	 */
	public void setCommandEnd(boolean commandEnd) {
		this.commandEnd = commandEnd;
	}

	/**
	 * get the defined Pattern for the regular expression or the default on if
	 * not defined
	 * 
	 * @return
	 */
	public Pattern getPattern() {
		if (pattern == null) {
			pattern = Pattern.compile(prompt, Pattern.DOTALL | (isRegexCaseInsensitive() ? Pattern.CASE_INSENSITIVE : 0));
		}
		return pattern;
	}

	public boolean isRegexCaseInsensitive() {
		return regexCaseInsensitive;
	}

	public void setRegexCaseInsensitive(boolean regexCaseInsensitive) {
		this.regexCaseInsensitive = regexCaseInsensitive;
	}
}
