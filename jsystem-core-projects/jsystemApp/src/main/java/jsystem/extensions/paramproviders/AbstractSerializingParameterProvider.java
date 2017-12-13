package jsystem.extensions.paramproviders;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.scenario.ParameterProvider;

public abstract class AbstractSerializingParameterProvider implements ParameterProvider {
	private static Logger log = Logger.getLogger(GenericObjectParameterProvider.class.getName());

	protected String propetiesToString(String className, Properties properties) {
		StringWriter writer = new StringWriter();
		try {
			properties.store(writer, null);
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to store the property object to the StringWriter", e);
		}
		StringBuilder buf = new StringBuilder();
		// append the class name then ';'
		buf.append(className);
		buf.append(';');
		// append the properties string
		buf.append(writer.getBuffer().toString());
		return convertToWindowsEol(buf.toString());
	}

	/**
	 * Will replace any single backslash that is before a special character with
	 * double backslashes. Can be used before parsing properties string with the
	 * Java properties object.
	 * 
	 * @param content
	 * @return the content after multiplying the backslashes.
	 * @author Itai Agmon
	 */
	protected static String multiplySingleBackslashes(String content) {
		// The regular expression will search for backslashes and will perform
		// negative look behind for another backslash and negative look forward
		// for backslashes and other special characters <br>
		// Special characters include: ! : = # <space> 
		return content.replaceAll("(?<!\\\\)\\\\(?![:!=#\\s\\\\])", "\\\\\\\\");
	}

	protected LinkedHashMap<String, String> propertiesToMap(Properties properties) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		Set<Object> keys = properties.keySet();
		for (Object key : keys) {
			if (!properties.getProperty(key.toString()).isEmpty())
				map.put(key.toString(), properties.getProperty(key.toString()));
		}
		return map;
	}

	/**
	 * IMPORTANT: ITAI - This is done for supporting Linux and Mac environments.
	 * DO NOT REMOVE!
	 * 
	 * If EOL is in Linux style, change it to Windows style.
	 * 
	 * @param str
	 *            String that contains eol
	 * @return
	 */
	protected String convertToWindowsEol(String str) {
		if (!str.contains("\r\n")) {
			return str.replace("\n", "\r\n");
		}
		return str;

	}

}