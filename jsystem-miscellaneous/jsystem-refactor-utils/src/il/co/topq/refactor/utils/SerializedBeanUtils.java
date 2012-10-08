package il.co.topq.refactor.utils;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SerializedBeanUtils {

	/**
	 * This regex will find strings that starts in the start of line or, in case
	 * of arrays, starts with number and one dot. <br>
	 * It is also define in groups the parts of the string we want to keep.<br>
	 * For example:
	 * 
	 * paramName=value<br>
	 * 0.paramName=Value<br>
	 */
	private static final String REGEX_PATTERN = "(\\n\\d?\\.?)%s(=.*\\s?\\n)";
	private static Logger log = Logger.getLogger("SerializedBeanUtils");

	private SerializedBeanUtils() {
		// Utils class
	}

	/**
	 * Checks if the bean serialized value exists in the given value.
	 * 
	 * 
	 * @param parameterValue
	 *            The value of the parameter that we want to check if holds the
	 *            bean serialized value.
	 * @param beanSourceNamePath
	 *            The full name of the bean we are searching
	 * @return true if the value holds the bean serialized value.
	 * @throws IOException
	 */
	public static boolean isBeanExists(final String parameterValue, final String beanSourceNamePath) throws IOException {
		if (parameterValue == null || parameterValue.isEmpty()) {
			return false;
		}
		Pattern pattern = Pattern.compile(beanSourceNamePath
				+ ";#\\S+\\s\\S+\\s\\d{2}\\s\\S{2}:\\d{2}:\\d{2}\\s\\S{3}\\s\\d{4}");
		Matcher matcher = pattern.matcher(parameterValue);
		return matcher.find();
	}

	/**
	 * Rename parameter name in bean
	 * 
	 * @param currentBean
	 *            The serialized value of the bean
	 * @param currentParameterName
	 *            The current name of the parameter
	 * @param newParameterName
	 *            The new name we want for the parameter.
	 * @return The serialized value of the bean after the renaming was done.
	 */
	public static String renameBeanParameter(final String currentBean, final String currentParameterName,
			final String newParameterName) {
		log.fine("Replacing parameter " + currentParameterName + " with " + newParameterName);

		String regex = String.format(REGEX_PATTERN, StringUtils.firstCharToUpper(currentParameterName.trim()));
		String replaceStr = String.format("$1%s$2", StringUtils.firstCharToUpper(newParameterName.trim()));

		String newBean = currentBean;
		String tempBean = "";

		//This loop is required for handling bean array.
		while (!newBean.equals(tempBean)) {
			tempBean = newBean;
			newBean = newBean.replaceAll(regex, replaceStr);
		}
		return newBean;
	}

}
