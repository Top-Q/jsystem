package il.co.topq.refactor.utils;

public class StringUtils {

	private StringUtils() {

	}

	/**
	 * Capitalize the first character in a string
	 * 
	 * @param newParameterName
	 * @return A String with its first letter capitalized
	 */
	public static String firstCharToUpper(String newParameterName) {
		if ((newParameterName == null) || (newParameterName.isEmpty()))
			return newParameterName;
		return Character.toUpperCase(newParameterName.charAt(0)) + newParameterName.substring(1);
	}

	public static boolean isEmpty(final String mode) {
		return null == mode || mode.isEmpty();
	}

	public static String frontSlashToBackSlash(String str) {
		return str.replace("/", "\\");
	}
	

}
