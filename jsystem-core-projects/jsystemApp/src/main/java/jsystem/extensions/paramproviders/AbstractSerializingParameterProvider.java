package jsystem.extensions.paramproviders;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
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
		buf.append(className);buf.append(';');
		// append the properties string
		buf.append(writer.getBuffer().toString());
		return convertToWindowsEol(buf.toString());
	}
	
	/**
	 * IMPORTANT: ITAI - This is done for supporting Linux and Mac environments. DO NOT REMOVE!
	 * 
	 * If EOL is in Linux style, change it to Windows style.
	 * 
	 * @param str String that contains eol 
	 * @return
	 */
	protected String convertToWindowsEol(String str){
		if (!str.contains("\r\n")){
			return str.replace("\n", "\r\n");
		}
		return str;
	
	}


}
