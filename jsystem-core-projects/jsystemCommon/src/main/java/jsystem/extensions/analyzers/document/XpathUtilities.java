/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.document;

/**
 * xPath string creation utilties
 * 
 * @author ohad.crystal <br>
 *         <href a="http://www.w3schools.com/xpath/xpath_syntax.asp">Link</href>
 */
public class XpathUtilities {

	/**
	 * 
	 * Get a valid xPath string to enable retrieval of an Element by attribute
	 * name and its value. note that the full tag path must also be stated
	 * 
	 * @param tagPath
	 *            example: /calls/step
	 * @param attrName
	 *            example: ReportStepAttributes.STEP_NAME
	 * @param attrValue
	 *            example: Call failed
	 * 
	 * @return A valid xPath string
	 */
	public static String getXpathBasic(String tagPath, String attrName, String attrValue) {
		StringBuffer buf = new StringBuffer();

		buf.append(tagPath);
		buf.append("[");
		buf.append("@");
		buf.append(attrName.toString());
		buf.append("=");
		buf.append("'");
		buf.append(attrValue);
		buf.append("'");
		buf.append("]");

		// System.out.println("Xpath generated: " + (retStr = buf.toString()));
		return buf.toString();

	}

	/**
	 * 
	 * Get a valid xPath string to enable retrieval of an Element by ORing
	 * attributes name and their corresponding value. note that the full tag
	 * path must also be stated
	 * 
	 * @param tagPath
	 *            example: /calls/step
	 * @param attrNames
	 *            example: call_step
	 * @param attrValues
	 *            example: Call failed
	 * 
	 * @return A valid xPath string
	 */
	public static String getXpathOR(String tagPath, String[] attrNames, String[] attrValues) {
		StringBuffer buf = new StringBuffer();

		buf.append(tagPath);
		buf.append("[");
		for (int i = 0; i < attrValues.length; i++) {
			buf.append("@");
			buf.append(attrNames[i]);
			buf.append("=");
			buf.append("'");
			buf.append(attrValues[i]);
			buf.append("'");
			if (i != (attrValues.length - 1))
				buf.append(" or ");
		}
		buf.append("]");

		// System.out.println("Xpath generated: " + (retStr = buf.toString()));

		return buf.toString();

	}

	/**
	 * 
	 * Get a valid xPath string to enable retrieval of an Element by ANDing
	 * attributes name and their corresponding value. note that the full tag
	 * path must also be stated
	 * 
	 * @param tagPath
	 *            example: /calls/step
	 * @param attrNames
	 *            example: call_step
	 * @param attrValues
	 *            example: Call failed
	 * 
	 * @return A valid xPath string
	 * 
	 * 
	 */
	public static String getXpathAND(String tagPath, String[] attrNames, String[] attrValues) {
		StringBuffer buf = new StringBuffer();
		String retStr;

		buf.append(tagPath);
		buf.append("[");
		for (int i = 0; i < attrValues.length; i++) {
			buf.append("@");
			buf.append(attrNames[i]);
			buf.append("=");
			buf.append("'");
			buf.append(attrValues[i]);
			buf.append("'");
			if (i != (attrValues.length - 1))
				buf.append(" and ");
		}
		buf.append("]");

		System.out.println("Xpath generated: " + (retStr = buf.toString()));

		return retStr;
	}

}
