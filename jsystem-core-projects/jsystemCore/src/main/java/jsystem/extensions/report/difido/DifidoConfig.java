package jsystem.extensions.report.difido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Allows configuration of the Difido HTML report. This will affect the local
 * and the remote reports.
 * 
 * @author Itai Agmon
 *
 */
class DifidoConfig {

	private static final Logger log = Logger.getLogger(DifidoConfig.class.getName());

	private static final String FILE_NAME = "difido.properties";

	private static DifidoConfig instance;

	private Properties properties;

	static DifidoConfig getInstance() {
		if (null == instance) {
			instance = new DifidoConfig();
		}
		return instance;
	}

	private DifidoConfig() {
		File configFile = new File(System.getProperty("user.dir"), FILE_NAME);
		if (!configFile.exists()) {
			createDefaultConfigFile();
		}
		readConfigFile();
	}

	private void readConfigFile() {
		properties = new Properties();
		try (FileInputStream in = new FileInputStream(new File(System.getProperty("user.dir"), FILE_NAME))) {
			properties.load(in);
		} catch (IOException e) {
			log.warning("Failed to read Difido configuration file");
		}
	}

	private void createDefaultConfigFile() {
		Properties properties = new Properties();
		for (DifidoProperty prop : DifidoProperty.values()) {
			properties.setProperty(prop.propName, prop.defaultValue != null ? prop.defaultValue.toString() : "");
		}
		try (FileOutputStream out = new FileOutputStream(new File(System.getProperty("user.dir"), FILE_NAME))) {
			properties.store(out, "Difido report properties");

		} catch (IOException e) {
			log.warning("Failed to create default Difido properties file due to " + e.getMessage());
		}

	}

	/**
	 * Get the value of the specified property. If the property was not found,
	 * it will return the default value.
	 * 
	 * @param property
	 * @return property value from type string
	 */
	String getString(DifidoProperty property) {
		String value = null;
		if (properties.containsKey(property.getPropName())) {
			value = properties.getProperty(property.getPropName());
		} else {
			value = property.getDefaultValue();
		}
		if (null == value) {
			value = "";
		}
		return value.trim();
	}

	/**
	 * Get the value of the specified property. If the property was not found,
	 * it will return the default value.
	 * 
	 * @param property
	 * @return property value from type boolean
	 */
	boolean getBoolean(DifidoProperty property) {
		String value = getString(property);
		return Boolean.parseBoolean(value);
	}

	long getLong(DifidoProperty property) {
		String value = getString(property);
		long longValue = 0;
		try {
			longValue = Long.parseLong(value);
		} catch (Throwable t) {
		}
		return longValue;
	}

	enum DifidoProperty {

		// @formatter:off
		ERRORS_TO_FAILURES("errors.to.failures", "false", "Replace each error with failure"),
		MIN_INTERVAL_BETWEEN_MESSAGES("min.interval.between.messages", "100",
						"The min allowed interval between message in millis");
		// @formatter:on

		private String propName;

		private String defaultValue;

		private String description;

		DifidoProperty(String propName, String defaultValue, String description) {
			this.propName = propName;
			this.defaultValue = defaultValue;
			this.description = description;
		}

		protected String getPropName() {
			return propName;
		}

		protected String getDefaultValue() {
			return defaultValue;
		}

		protected String getDescription() {
			return description;
		}

	}

}
