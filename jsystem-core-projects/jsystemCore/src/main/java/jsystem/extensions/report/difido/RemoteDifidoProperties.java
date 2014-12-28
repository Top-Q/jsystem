package jsystem.extensions.report.difido;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

class RemoteDifidoProperties {

	private static final Logger log = Logger.getLogger(RemoteDifidoProperties.class.getName());

	private static final String FILE_NAME = "remoteDifido.properties";

	private static RemoteDifidoProperties instance;

	private Properties properties;

	enum RemoteDifidoOptions {
		HOST("host", "localhost"), PORT("port", "8080"), ENABLED("enabled", "true"), APPEND_TO_EXISTING_EXECUTION(
				"append.to.existing.execution", "false");

		private String property;

		private String defaultValue;

		private RemoteDifidoOptions(final String property, final String defaultValue) {
			this.property = property;
			this.defaultValue = defaultValue;
		}

		public String getProperty() {
			return property;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

	}

	private RemoteDifidoProperties() {
		properties = new Properties();
		final File propertiesFile = new File(FILE_NAME);
		if (!propertiesFile.exists()) {
			initDefaultProperties();
		}
		try (final FileReader reader = new FileReader(propertiesFile)) {
			properties.load(reader);
			if (properties.isEmpty()) {
				initDefaultProperties();
			}
		} catch (IOException e) {
			initDefaultProperties();
		}
	}

	private void initDefaultProperties() {
		properties.setProperty(RemoteDifidoOptions.HOST.getProperty(), RemoteDifidoOptions.HOST.getDefaultValue());
		properties.setProperty(RemoteDifidoOptions.PORT.getProperty(), RemoteDifidoOptions.PORT.getDefaultValue());
		properties
				.setProperty(RemoteDifidoOptions.ENABLED.getProperty(), RemoteDifidoOptions.ENABLED.getDefaultValue());
		properties.setProperty(RemoteDifidoOptions.APPEND_TO_EXISTING_EXECUTION.getProperty(),
				RemoteDifidoOptions.APPEND_TO_EXISTING_EXECUTION.getDefaultValue());
		final File propertiesFile = new File(FILE_NAME);
		if (propertiesFile.exists()) {
			propertiesFile.delete();
		}
		try (final FileWriter writer = new FileWriter(propertiesFile)) {
			properties.store(writer, "Default Difido properties");
		} catch (IOException e) {
			log.warning("Failed to write Difido properties to file");
		}
	}

	public String getProperty(RemoteDifidoOptions option) {
		return properties.getProperty(option.getProperty());
	}

	public static RemoteDifidoProperties getInstance() {
		if (null == instance) {
			instance = new RemoteDifidoProperties();
		}
		return instance;
	}
}
