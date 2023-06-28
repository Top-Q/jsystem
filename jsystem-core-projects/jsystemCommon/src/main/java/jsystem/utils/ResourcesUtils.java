package jsystem.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

public class ResourcesUtils {

	private static Reporter report = ListenerstManager.getInstance();

	private ResourcesUtils() {
		throw new AssertionError();
	}

	public static String retrieveFileFromResourcesFolder(String filePath) throws Exception {
		String fileAsString = findResourcesFolder() + "\\" + filePath;
		return fileAsString;
	}

	public static String retrieveFileFromClassesFolder(String filePath) throws Exception {
		String fileAsString = findClassesFolder() + "\\" + filePath;
		return fileAsString;
	}

	private static String findClassesFolder() throws Exception {
		Properties jsystemPropertiesFile = loadJsystemPropertiesFile();
		String testsSrc = jsystemPropertiesFile.getProperty("tests.dir");
		// String testResourcesFolder = testsSrc.replace("java", "resources");
		return testsSrc;
	}

	private static String findResourcesFolder() throws Exception {
		Properties jsystemPropertiesFile = loadJsystemPropertiesFile();
		String testsSrc = jsystemPropertiesFile.getProperty("tests.src");
		String testResourcesFolder = testsSrc.replace("java", "resources");
		return testResourcesFolder;
	}

	private static Properties loadJsystemPropertiesFile() throws Exception {
		return loadPropertiesFile("jsystem.properties");
	}

	public static Properties loadRemoteDifidoPropertiesFile() throws Exception {
		return loadPropertiesFile("remoteDifido.properties");
	}

	private static Properties loadPropertiesFile(String propertiesFile) throws Exception {
		String str = report.getCurrentTestFolder();
		if (str.contains("tests")) {// for runner
			str = str + "\\..\\..\\..\\..\\";
		} else {// for eclipse
			str = str + "\\..\\..\\..\\";
		}

		Properties p = new Properties();
		try (InputStream inputStream = new FileInputStream(str + propertiesFile)) {
			p.load(inputStream);
		}
		return p;
	}

	public static Properties loadPropertiesFromFileInResources(String propertiesFile) throws Exception {
		Properties p = new Properties();
		try (InputStream inputStream = new FileInputStream(retrieveFileFromResourcesFolder(propertiesFile))) {
			p.load(inputStream);
		}
		return p;
	}

}
