/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.common.CommonResources;

public class StringUtils {
	public static final String BOLD_START = "%%%BOLD_START%%%";

	public static final String BOLD_END = "%%%BOLD_END%%%";

	public static final String NEW_LINE = "%%%NEW_LINE%%%";

	private final static String separator = "/SEP/";

	private final static int separatorLength = separator.length();

	public final static String notAllowedCharacters = "#%&";

	/**
	 * The source was taken from the internet. Replace a text in other text in a
	 * given input text.
	 * 
	 * @param lookIn
	 *            The text to look in.
	 * @param lookFor
	 *            The text to look for.
	 * @param replaceWith
	 *            The text to replace with.
	 * 
	 * @return The text after the replace operation.
	 */
	public static String replace(String lookIn, String lookFor,
			String replaceWith) {
		int count = 0;
		int i, j;
		StringBuffer sb;

		for (i = 0; (i = lookIn.indexOf(lookFor, i)) != -1; i += lookFor
				.length())
			++count;
		if (count == 0) {
			return lookIn;
		}
		sb = new StringBuffer(lookIn.length() + count
				* (replaceWith.length() - lookFor.length()));
		for (i = 0; (j = lookIn.indexOf(lookFor, i)) != -1; i = j
				+ lookFor.length())
			sb.append(lookIn.substring(i, j)).append(replaceWith);
		sb.append(lookIn.substring(i));
		return sb.toString();
	}
	public static String toHtmlString(String s) {
		String toReturn = null;
		if (s == null) {
			return null;
		}
		if (s.contains("!DOCTYPE HTML")) {
			s = replace(s, "!DOCTYPE HTML", "");
			return s;
		}
		toReturn = s;
		if(toReturn.contains("<")){
			if(toReturn.contains("</a>") || toReturn.contains("</A>")){
				return toReturn;
			}
			toReturn = replace(toReturn, "<B>", BOLD_START);
			toReturn = replace(toReturn, "<b>", BOLD_START);
			toReturn = replace(toReturn, "</B>", BOLD_END);
			toReturn = replace(toReturn, "</b>", BOLD_END);
			toReturn = replace(toReturn, "<BR>", NEW_LINE);
			toReturn = replace(toReturn, "<br>", NEW_LINE);
			toReturn = replace(toReturn, "<", "&lt;");
			toReturn = replace(toReturn, ">", "&gt;");
			toReturn = replace(toReturn, BOLD_START, "<B>");
			toReturn = replace(toReturn, BOLD_END, "</B>");
			toReturn = replace(toReturn, NEW_LINE, "<br>");
		}
		toReturn = replace(toReturn, "\r\n", "<br>");
		//toReturn = replace(toReturn, "\n\r", "<br>");
		toReturn = replace(toReturn, "\r", "<br>");
		toReturn = replace(toReturn, "\n", "<br>");
		toReturn = replace(toReturn, " ", "&nbsp;");
		toReturn = replace(toReturn, "\t",
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		return toReturn;
	}
	
	/**
	 * 
	 */
	public static String stringToHTMLEscapedString(String string) {
	    StringBuffer sb = new StringBuffer(string.length());
	    // true if last char was blank
	    boolean lastWasBlankChar = false;
	    int len = string.length();
	    char c;

	    for (int i = 0; i < len; i++)
	        {
	        c = string.charAt(i);
	        if (c == ' ') {
	            // blank gets extra work,
	            // this solves the problem you get if you replace all
	            // blanks with &nbsp;, if you do that you loss 
	            // word breaking
	            if (lastWasBlankChar) {
	                lastWasBlankChar = false;
	                sb.append("&nbsp;");
	                }
	            else {
	                lastWasBlankChar = true;
	                sb.append(' ');
	                }
	            }
	        else {
	            lastWasBlankChar = false;
	            //
	            // HTML Special Chars
	            if (c == '"')
	                sb.append("&quot;");
	            else if (c == '&')
	                sb.append("&amp;");
	            else if (c == '<')
	                sb.append("&lt;");
	            else if (c == '>')
	                sb.append("&gt;");
	            else if (c == '\n')
	                // Handle Newline
	                sb.append("&lt;br/&gt;");
	            else {
	                int ci = 0xffff & c;
	                if (ci < 160 )
	                    // nothing special only 7 Bit
	                    sb.append(c);
	                else {
	                    // Not 7 Bit use the unicode system
	                    sb.append("&#");
	                    sb.append(String.valueOf(ci));
	                    sb.append(';');
	                    }
	                }
	            }
	        }
	    return sb.toString();
	}
	public static String getPackageName(String className) {
		int lastIndex = className.lastIndexOf(".");
		if (lastIndex < 0) {
			return "null";
		}
		return className.substring(0, lastIndex);
	}

	public static String getClassName(String className) {
		int lastIndex = className.lastIndexOf(".");
		if (lastIndex < 0) {
			return className;
		}
		return className.substring(lastIndex + 1);
	}

	public static boolean isFound(Object toSearch, Object[] searchIn) {
		for (int i = 0; i < searchIn.length; i++) {
			if (searchIn[i].equals(toSearch)) {
				return true;
			}
		}
		return false;
	}

	public static String[] getAsStringArray(ArrayList<Object> list) {
		int listSize = list.size();
		String[] toReturn = new String[listSize];
		for (int i = 0; i < listSize; i++) {
			toReturn[i] = list.get(i).toString();
		}
		return toReturn;
	}

	public static String getStackTrace(Throwable t) {
		if (t != null) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			t.printStackTrace(printWriter);
			return stringWriter.toString();
		}
		return "";
	}

	public static String getStackTrace(Thread thread) {
		StringBuffer sb = new StringBuffer();
		StackTraceElement[] trace = thread.getStackTrace();
		for (int i = 0; i < trace.length; i++)
			sb.append("\tat " + trace[i] + "\r\n");
		return sb.toString();

	}

	/**
	 * 
	 * @param s
	 *            string
	 * @param toCount
	 *            substring that we seatch on the bug string
	 * @return how many times toCount is in s
	 */
	public static int countString(String s, String toCount) {
		int count = 0;
		int possition = 0;
		if (s == null || toCount == null) {
			return 0;
		}
		while (possition + toCount.length() <= s.length()) {
			int index = s.indexOf(toCount, possition);
			if (index < 0) {
				break;
			}
			count++;
			possition = index + 1;
		}
		return count;
	}

	/**
	 * 
	 * @param s
	 *            string
	 * @param toCount
	 *            substring that we seatch on the bug string
	 * @param isRegExp
	 *            it toCount is regular expresion or simple string
	 * @return how many times toCount is in s
	 */
	public static int countString(String s, String toCount, boolean isRegExp) {
		if (!isRegExp) 
			return countString(s,toCount);
		
		boolean status = false;
		String found;
		int count = 0;
		do {
			Pattern p;
			p = Pattern.compile("(" + toCount + ")");
			Matcher m = p.matcher(s);
			status = m.find();
			if (status) {
				found = m.group(1);
				count++;
				s = s.substring(s.indexOf(found) + found.length());
			}
		} while (status);
		return count;
	}

	public static String getClassName(String file, String root)
			throws FileNotFoundException {
		String classFileEnd = ".class";
		if (!file.startsWith(root)) {
			throw new FileNotFoundException("The file: " + file
					+ " is not under the root: " + root);
		}
		if (!file.toLowerCase().endsWith(classFileEnd)) {
			throw new FileNotFoundException("File not a class file: " + file);
		}
		// String fileSep = System.getProperty("file.separator");
		String rst = file.substring(root.length(),
				file.length() - classFileEnd.length()).replaceAll("\\\\", ".")
				.replaceAll("\\/", ".");
		if (rst.startsWith(".")) {
			rst = rst.substring(1);
		}
		return rst;
	}

	private static final String[] hexLookupTable = { "00", "01", "02", "03",
			"04", "05", "06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e",
			"0f", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
			"1a", "1b", "1c", "1d", "1e", "1f", "20", "21", "22", "23", "24",
			"25", "26", "27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f",
			"30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3a",
			"3b", "3c", "3d", "3e", "3f", "40", "41", "42", "43", "44", "45",
			"46", "47", "48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50",
			"51", "52", "53", "54", "55", "56", "57", "58", "59", "5a", "5b",
			"5c", "5d", "5e", "5f", "60", "61", "62", "63", "64", "65", "66",
			"67", "68", "69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71",
			"72", "73", "74", "75", "76", "77", "78", "79", "7a", "7b", "7c",
			"7d", "7e", "7f", "80", "81", "82", "83", "84", "85", "86", "87",
			"88", "89", "8a", "8b", "8c", "8d", "8e", "8f", "90", "91", "92",
			"93", "94", "95", "96", "97", "98", "99", "9a", "9b", "9c", "9d",
			"9e", "9f", "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8",
			"a9", "aa", "ab", "ac", "ad", "ae", "af", "b0", "b1", "b2", "b3",
			"b4", "b5", "b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be",
			"bf", "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9",
			"ca", "cb", "cc", "cd", "ce", "cf", "d0", "d1", "d2", "d3", "d4",
			"d5", "d6", "d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df",
			"e0", "e1", "e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea",
			"eb", "ec", "ed", "ee", "ef", "f0", "f1", "f2", "f3", "f4", "f5",
			"f6", "f7", "f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff" };

	public static String bytesToString(byte[] bytes) {
		int readBytes = bytes.length;
		StringBuffer hexData = new StringBuffer();
		for (int i = 0; i < readBytes; i++) {
			hexData.append(hexLookupTable[0xff & bytes[i]]);
		}
		return hexData.toString();
	}

	public static byte[] stringToBytes(String s) {
		byte[] toReturn = new byte[s.length() / 2];
		for (int i = 0; i < toReturn.length; i++) {
			toReturn[i] = (byte) Integer.parseInt(
					s.substring(i * 2, i * 2 + 2), 16);
		}
		return toReturn;
	}

	/**
	 * converting Properties to String
	 * 
	 * @param properties
	 *            the properties to convert
	 * @return a string of the properties with a separator
	 */
	public static String propertiesToString(Properties properties) {
		String s = "";
		if (properties == null)
			return "";
		Enumeration<?> e = properties
				.propertyNames();
		boolean first = true;
		String key;
		while (e.hasMoreElements()) {
			key = (String)e.nextElement();
			if (first) {
				s += key + "=" + properties.getProperty(key);
				first = false;
			} else
				s += separator + key + "=" + properties.getProperty(key);
		}
		return s;
	}

	/**
	 * convert a converted String back to properties (only if it was initially
	 * converted by this class)
	 * 
	 * @param propString
	 *            the converted String
	 * @return the original properties
	 */
	public static Properties stringToProperties(String propString) {
		if (propString == null)
			return null;
		Properties p = new Properties();
		String key, value;
		int equallInd, separatorInd;
		while (!propString.equals("")) {
			separatorInd = propString.indexOf(separator);
			equallInd = propString.indexOf("=");
			key = propString.substring(0, equallInd);
			if (separatorInd > -1) {
				value = propString.substring(equallInd + 1, separatorInd);
				propString = propString.substring(separatorInd
						+ separatorLength);
			} else {
				value = propString
						.substring(equallInd + 1, propString.length());
				propString = "";
			}
			p.setProperty(key, value);
		}
		return p;
	}

	/**
	 * More the first char of the string to be upper case
	 * 
	 * @param firstToUpperString
	 *            the string to change
	 * @return the string with the change
	 */
	public static String firstCharToUpper(String firstToUpperString) {
		if (firstToUpperString == null || firstToUpperString.length() == 0) {
			return firstToUpperString;
		} else if (firstToUpperString.length() == 1) {
			return firstToUpperString.toUpperCase();
		}
		return firstToUpperString.substring(0, 1).toUpperCase()
				+ firstToUpperString.substring(1);
	}

	/**
	 * More the first char of the string to be lower case
	 * 
	 * @param firstToLowerString
	 *            the string to change
	 * @return the string with the change
	 */
	public static String firstCharToLower(String firstToLowerString) {
		if (firstToLowerString == null || firstToLowerString.length() == 0) {
			return firstToLowerString;
		} else if (firstToLowerString.length() == 1) {
			return firstToLowerString.toLowerCase();
		}
		return firstToLowerString.substring(0, 1).toLowerCase()
				+ firstToLowerString.substring(1);
	}

	/**
	 * searches the given String for specisl characters (defined in the final
	 * String "notAllowedCharacters"
	 * 
	 * @param s
	 *            the String to search
	 * @return true if one of the special characters was found;
	 */
	public static boolean hasNotAllowedSpecialCharacters(String s) {
		if (s == null || s.length() == 0)
			return false;
		for (int i = 0; i < notAllowedCharacters.length(); i++) {
			if (s.indexOf(notAllowedCharacters.charAt(i)) > -1)
				return true;
		}
		return false;
	}

	/**
	 * Splits <code>text</code> to a string array according to provided
	 * delimiter. 
	 * Delimiter has to be {@link StringTokenizer} delimiter.
	 * If either <code>text</code> or <code>delim</code> is <code>null</code>,  
	 * the method returns string array with length 0.
	 */
	public static String[] split(String text,String delim) {
		if (text == null || delim == null){
			return new String[0];
		}
		StringTokenizer tokenizer = new StringTokenizer(text,delim);
		String[] res = new String[tokenizer.countTokens()];
		int i=0;
		while (tokenizer.hasMoreElements()){
			res[i]=tokenizer.nextToken();
			i++;
		}
		return res;
	}

	/**
	 * Creates a {@link Set} of strings from <code>stringArray</code>.
	 * If <code>stringArray</code> is <code>null</code>
	 * the method returns an empty {@link Set}. 
	 */
	public static Set<String> stringArrayToSet(String[] stringArray){
		HashSet<String>  set = new HashSet<String>();
		if(stringArray == null){
			return set;
		}
		for (String s:stringArray){
			set.add(s);
		}
		return set;
	}


	/**
	 * Merges an array of string arrays to a concatenated 
	 * string array.
	 * Concatenation is done in the following order 
	 * arrayOfArrays[0][0],arrayOfArrays[0][1]  ...  arrayOfArrays[1][0] ... arrayOfArrays[x][z]
	 * if arrayOfArrays is <code>null</code> the method return string array with length 0.
	 * any <code>null</code> value in <code>arrayOfArrays</code> is skipped. 
	 */
	public static String[] mergeStringArrays(String[][] arrayOfArrays){
		if (arrayOfArrays == null){
			return new String[0];
		}
		ArrayList<String> res = new ArrayList<String>();
		for (int i = 0;i<arrayOfArrays.length;i++){
			if (arrayOfArrays[i] == null){
				continue;
			}
			for (int j = 0;j<arrayOfArrays[i].length;j++){
				if (arrayOfArrays[i][j] != null){
					res.add(arrayOfArrays[i][j]);
				}
			}
		}
		return res.toArray(new String[res.size()]);
	}
		
	/**
	 * Returns true if <code>stringToCheck</code> is <code>null</code>
	 * or if <code>stringToCheck</code> is composed of only white spaces.
	 */
	public static boolean isEmpty(String stringToCheck){
		return stringToCheck == null || "".equals(stringToCheck.trim());
	}
	
	
	public synchronized static String getTime() {
		Calendar cal = new GregorianCalendar();
		int hour24 = cal.get(Calendar.HOUR_OF_DAY); // 0..23
		int min = cal.get(Calendar.MINUTE); // 0..59
		int sec = cal.get(Calendar.SECOND); // 0..59
		return hour24 + ":" + min + ":" + sec;
	}

	
	/**
	 * Returns a string which is a concatenation of the {@link #toString()} of the
	 * objects passed to the method, separated  by {@link #separator}   
	 */
	public static String  objectArrayToString(String separator,Object... objects){
		StringBuffer buffer = new StringBuffer();
		for (Object obj:objects){
			buffer.append(obj.toString()).append(separator);
		}
		String res = buffer.toString();
		if (res.lastIndexOf(separator) == -1){
			return res;
		}
		return res.substring(0,res.lastIndexOf(separator));
	}
	
	
	public synchronized static void showMsgWithTime(String msg) {
		System.out.println(getTime() + " " + msg);
	}
	
	/**
	 * Converts array of int's to one String. "," is the default delimiter
	 * Example :
	 * Int array {1,2,3} will be converted to "1,2,3"
	 * 
	 * @param intArr	the int values array
	 * @return result
	 */
	public static String intArrToString(int[] intArr) {
		return intArrToString(intArr, ",");
	}
	
	
	/**
	 * Converts array of int's to one String with the given delimiter
	 * 
	 * @param intArr	the int values array
	 * @param delimiter	the delimiter to place between values
	 * @return result
	 */
	public static String intArrToString(int[] intArr,String delimiter) {
		String result = new String("");
		for (int i = 0; i < intArr.length; i++) {
			if (i==0){
				result += intArr[i];
			}
			else{
				 result+=delimiter+intArr[i];
			}
		}
		return result;
	}
	
	/**
	 * convert a String array of Integers to an Integer array
	 * 
	 * @param strArr	the string array
	 * @param start		first index to convert from
	 * @param end		last index to convert
	 * @return
	 */
	public static Integer[] stringArrToInteger(String[] strArr,int start, int end) {
		end = (end<=strArr.length)? end : strArr.length;
		start = (start<=strArr.length)? start : end+1;
		Integer[] toReturn = new Integer[end-start];
		for (int i=0 ; i<toReturn.length ; i++){
			toReturn[i] = Integer.parseInt(strArr[i+start]);
		}
		
		return toReturn;
	}
	
	
	public static String formatTimeToString(long seconds){
		int hours = (int) (seconds / 3600);
		int remainder = (int) (seconds % 3600);
		int minutes = remainder / 60;
		seconds = remainder % 60;

		return ( (hours < 10 ? "0" : "") + hours
		+ ":" + (minutes < 10 ? "0" : "") + minutes
		+ ":" + (seconds< 10 ? "0" : "") + seconds );
	}
	
	public static String advancedToString(Object object){
		if (object instanceof Object[]){
			return objectArrayToString(CommonResources.DELIMITER, (Object[])object);
		}
		
		return object.toString();
	}
	
}
