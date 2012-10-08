/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.text;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Find a regex in a string.
 * 
 * @author TacB0sS
 */
public class FindRegex extends AnalyzeTextParameter {

	protected boolean caseSensitive = true;

	private int instance = 1;

	private int group = 1;

	protected String counter = null;

	/**
	 * @param toFind
	 *            The string to find.
	 */
	public FindRegex(String toFind) {
		super("");
		setRegex(toFind);
	}

	/**
	 * @param toFind
	 *            The string to find.
	 * @param instance
	 *            The instance for which to get the group text for.
	 * @param group
	 *            The index of the group in the regex expression.
	 */
	public FindRegex(String toFind, int instance, int group) {
		super("(" + toFind + ")");
		this.instance = instance;
		this.group = group;
	}

	/**
	 * @param toFind
	 *            The string to find.
	 * @param caseSensitive
	 *            Is looking for a case sensitive regex or not.
	 * @param group
	 *            The index of the group in the regex expression.
	 */
	public FindRegex(String toFind, boolean caseSensitive, int group) {
		this(toFind, caseSensitive, 1, group);
	}

	/**
	 * @param toFind
	 *            The string to find.
	 * @param caseSensitive
	 *            Is looking for a case sensitive regex or not.
	 * @param instance
	 *            The instance for which to get the group text for.
	 * @param group
	 *            The index of the group in the regex expression.
	 * 
	 */
	public FindRegex(String toFind, boolean caseSensitive, int instance, int group) {
		this(toFind);
		this.caseSensitive = caseSensitive;
		this.instance = instance;
		this.group = group;
	}

	/**
	 * Find a text in the output string
	 * 
	 * @param toFind
	 *            The string to find.
	 * @param caseSensitive
	 *            Is looking for a case sensitive regex or not.
	 */
	public FindRegex(String toFind, boolean caseSensitive) {
		this(toFind);
		this.caseSensitive = caseSensitive;
	}

	public void analyze() {
		status = false;
		message = "Text to find: " + toFind + System.getProperty("line.separator")
				+ System.getProperty("line.separator") + "Actual text: " + testText;
		if (testText == null) {
			title = "Text to analyze is null";
			return;
		}

		counter = findRegex(instance, group, testText);

		if (counter != null) {
			title = "The text <" + counter + "> was found";
			message = message.replaceAll(toFind, "<b>" + counter + "</b>");
			status = true;
		} else {
			title = "The Regex <" + toFind + "> was not found";
		}
	}

	/**
	 * @param instance
	 *            The index of the instance for which to get the regex group.
	 * @param fullText
	 *            The full text to search for the regex in.
	 * @param groupIndices
	 *            The group indices to get from the regex instance.
	 * @return The strings of the specified group, from the specified instance
	 *         of the complete regex.
	 */
	public final String[] findRegex(int instance, String fullText, int... groupIndices) {
		int instanceIndex = 1;
		String value = null;
		Pattern p = caseSensitive ? Pattern.compile(toFind) : Pattern.compile(toFind,
				Pattern.CASE_INSENSITIVE);
		while (instanceIndex <= instance) {
			Matcher m = p.matcher(fullText);
			if (!m.find())
				break;
			if (instanceIndex == instance) {
				String[] toRet = new String[groupIndices.length];
				for (int i = 0; i < groupIndices.length; i++)
					toRet[i] = m.group(groupIndices[i]);
				return toRet;
			}
			instanceIndex++;
			value = m.group(1);
			fullText = fullText.replace(value, "");
		}
		return new String[] { null };
	}

	/**
	 * By default searches for all the Regex Instance for the first Regex Group,
	 * which is the entire Regex string supplied.
	 * 
	 * @param caseSensitive
	 *            Is looking for a case sensitive regex or not.
	 * @param fullText
	 *            The full text to search for the supplied Regex array in.
	 * @param regexToFind
	 *            An array of Regex to find any of them in the full text
	 *            supplied.
	 * @return An array of the found results.
	 */
	public static final String[] findRegex(String fullText, boolean caseSensitive,
			String... regexToFind) {
		return findRegex(fullText, caseSensitive, false, regexToFind);
	}

	/**
	 * Check if the supplied text contains any of the supplied Regex strings.
	 * 
	 * @param caseSensitive
	 *            Is looking for a case sensitive regex or not.
	 * @param fullText
	 *            The full text to search for the supplied Regex array in.
	 * @param regexToFind
	 *            An array of Regex to find any of them in the full text
	 *            supplied.
	 * @return Whether the supplied text contains any of the supplied Regex.
	 */
	public static final boolean containsRegex(String fullText, boolean caseSensitive,
			String... regexToFind) {
		return findRegex(fullText, caseSensitive, false, regexToFind).length > 0;
	}

	private static final String[] findRegex(String fullText, boolean caseSensitive,
			boolean stopOnFirst, String... regexToFind) {
		Vector<String> toRet = new Vector<String>();
		FindRegex fr = new FindRegex("", caseSensitive);
		for (String regex : regexToFind) {
			fr.setRegex(regex);
			String[] instances = fr.instances(fullText);
			for (String instance : instances)
				toRet.add(instance);
			if (stopOnFirst && toRet.size() > 0)
				break;
		}
		return toRet.toArray(new String[toRet.size()]);
	}

	private void setRegex(String regex) {
		toFind = "(" + regex + ")";
	}

	/**
	 * @param instance
	 *            The index of the instance for which to get the regex group.
	 * @param groupIndex
	 *            The group index to get from the regex instance.
	 * @param fullText
	 *            The full text to search for the regex in.
	 * @return The string of the specified group, from the specified instance of
	 *         the complete regex.
	 */
	public final String findRegex(int instance, int groupIndex, String fullText) {
		return findRegex(instance, fullText, new int[] { groupIndex })[0];
	}

	/**
	 * @param fullText
	 *            The text to check for the regex.
	 * @return The count of the instances of the full regex.
	 */
	public final int instanceCount(String fullText) {
		return instances(fullText).length;
	}

	/**
	 * @param fullText
	 *            The text to check for the regex.
	 * @return The instances of the full regex.
	 */
	public final String[] instances(String fullText) {
		int instanceIndex = 0;
		Vector<String> instances = new Vector<String>();
		String tempStr = "";
		while (true) {
			Pattern p = caseSensitive ? Pattern.compile(toFind) : Pattern.compile(toFind,
					Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(fullText);

			if (!m.find())
				break;
			instanceIndex++;
			tempStr = m.group(1);
			if (tempStr != null && tempStr.length() > 0)
				instances.add(tempStr);
			fullText = fullText.replace(tempStr, "");
		}
		return instances.toArray(new String[instances.size()]);
	}

	public String getCounter() {
		return counter;
	}
}
