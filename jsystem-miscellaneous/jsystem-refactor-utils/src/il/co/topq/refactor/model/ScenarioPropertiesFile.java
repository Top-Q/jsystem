package il.co.topq.refactor.model;

import il.co.topq.refactor.exception.ScenarioXmlParseException;
import il.co.topq.refactor.exception.UnmodifiableFileException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.extensions.sourcecontrol.SourceControlI;

/**
 * Represents a JSystem Project Properties file
 * 
 * @author Itai Agmon 
 */
public class ScenarioPropertiesFile extends JSystemFile {

	private Properties properties;

	private FileInputStream fis;

	private final String scenariosFolder;

	public ScenarioPropertiesFile(File propertiesFile) {
		super(propertiesFile);
		scenariosFolder = file.getAbsolutePath().split("scenarios")[0].replaceAll("/", File.separator);
	}

	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	/**
	 * 
	 * Replace a parameter name of a test with a new one
	 * 
	 * NOTE: Usually retrieve the uuid of a specific test from the xml pair file
	 * and then it will be possible to rename a specific parameter
	 * 
	 * @param testUUID
	 *            - the test uuid which parameter need to be changed
	 * @param oldName
	 *            - the old parameter name
	 * @param newName
	 *            - the new name for the parameter
	 * @return - true if the properties file was change, false otherwise
	 * @throws IOException
	 *             - In case while loading the file an exception has occurred
	 * @throws il.co.topq.refactor.exception.UnmodifiableFileException
	 *             - If the file is in a read only mode this exception is thrown
	 */
	public boolean replacePropertyName(UUID testUUID, String oldName, String newName) throws IOException,
			UnmodifiableFileException {
		init();
		Properties newProperties = new Properties();
		Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Object, Object> entry = it.next();

            //We ask here if the property line ends with the parameter name so we wont accept a subset of this parameter name
			if (((String) entry.getKey()).endsWith(testUUID + "." + oldName)) {
				log.fine("Found property " + ((String) entry.getKey()) + " in file " + file.getName());
				it.remove();
				newProperties.put(((String) entry.getKey()).replace(oldName, newName), entry.getValue());
				// if (!oldName.equals("timeoutInSec")) {
				// }
			}
		}
		if (newProperties.size() > 0) {
			properties.putAll(newProperties);
			return true;
		}
		return false;
	}

	private void init() throws IOException {
		if (properties == null) {
			properties = new Properties();
			fis = new FileInputStream(file);
			properties.load(fis);
		}
	}

	public void close() throws IOException {
		if (properties != null) {
			if (fis != null) {
				fis.close();
			}
			properties = null;
		}
	}

	public Set<? extends String> getTestParameters(UUID testUUID) throws IOException {
		init();
		Set<String> testParameters = new HashSet<String>();
		Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
		Pattern pattern = Pattern.compile(testUUID + "\\." + "([\\w]+)$");
		while (it.hasNext()) {
			Map.Entry<Object, Object> entry = it.next();
			Matcher matcher = pattern.matcher(((String) entry.getKey()));
			if (matcher.find()) {
				testParameters.add(matcher.group(1));
			}

		}
		return testParameters;
	}

	public void rename(String newName) throws IOException, UnmodifiableFileException {
		close();
		File newPropFile = new File(scenariosFolder + newName + ".properties");
		if (newPropFile.exists()) {
			log.severe("Scenario properites file with the same name is already exists: " + newPropFile.getAbsolutePath());
			throw new IOException("Scenario properties file with the same name is already exists: "
					+ newPropFile.getAbsolutePath());
		}
		if (!file.renameTo(newPropFile)) {
			throw new UnmodifiableFileException(file);
		}
		file = newPropFile;
	}

	public void rename(String newName, SourceControlI sourceControlHandler) throws ScenarioXmlParseException,
			IOException, UnmodifiableFileException {
		if (null == sourceControlHandler) {
			rename(newName);
			return;
		}
		try {
			//We have to close because we don't want to lock the file. 
			close();
			File newPropertiesFile = new File(scenariosFolder + newName + ".properties");
			if (newPropertiesFile.exists()) {
				log.severe("Scenario properties file with the same name is already exists: "
						+ newPropertiesFile.getAbsolutePath());
				throw new IOException("Scenario XML file with the same name is already exists: "
						+ newPropertiesFile.getAbsolutePath());
			}

			sourceControlHandler.moveFile(file, newPropertiesFile);
			file = newPropertiesFile;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Excpetion was caught during renaming of file", e);
			throw new ScenarioXmlParseException();
		}

	}

	public void save() throws IOException {
		log.finer("Saving file " + this);
		init();
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			properties.store(fos, "");

		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to save properties file " + file.getAbsolutePath(), e);
			throw new IOException("Failed to save properties file " + file.getAbsolutePath());
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
		close();

	}

	/**
	 * 
	 * @return The content of the properties file. Not included the scenario
	 *         attributes.
	 * @throws IOException
	 */
	public Map<String, String> getContent() throws IOException {
		init();
		Map<String, String> content = new TreeMap<String, String>();
		for (Object key : properties.keySet()) {
			if (((String) key).startsWith("jsystem.")) {
				continue;
			}
			content.put((String) key, (String) properties.getProperty((String) key));
		}
		return content;
	}

	public void put(String prop, String newBean) throws IOException {
		init();
		log.fine("Putting in file key:" + prop + " with value: " + newBean);
		properties.put(prop, newBean);

	}

}
