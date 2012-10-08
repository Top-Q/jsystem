package il.co.topq.refactor.model;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This object is created by giving it the JSystem project directory
 * <p/>
 * After created it is possible to retrieve all the type of JSystem files.
 * 
 * @author Itai Agmon
 */
class JSystemFilesCollector {

	private File projectDir;

	private String propertiesSuffix = ".properties";

	private String xmlSuffix = ".xml";

	private List<ScenarioXMLFile> scenarioXMLFile;

	private List<ScenarioPropertiesFile> scenarioPropertiesFiles;

	public JSystemFilesCollector(File projectDir) {
		this.projectDir = projectDir;
	}

	private List<ScenarioPairFiles> scenarioPairFiles;

	/**
	 * !!!IMPORTANT NOTE !!! Assuming that the xml and properties files comes in
	 * couple. If only one of them exists it will not be in the returned list.
	 * 
	 * @return Get a list of all project scenarios pair files. A pair file is an
	 *         object representing the properties and the xml files
	 */
	public List<ScenarioPairFiles> getProjectScenariosFiles() {
		if (scenarioPairFiles != null)
			return scenarioPairFiles;

		List<File> projectXMLFiles = getXMLFiles();
		List<File> projectPropertiesFile = getPropertiesFiles();
		scenarioPairFiles = createScenarioPairFiles(projectXMLFiles, projectPropertiesFile);
		return scenarioPairFiles;
	}

	/**
	 * @param projectXMLFiles
	 *            - A list of all project xml files
	 * @param projectPropertiesFile
	 *            - A list of all properties xml files
	 * @return A list of pair files xml and properties
	 */
	private List<ScenarioPairFiles> createScenarioPairFiles(List<File> projectXMLFiles, List<File> projectPropertiesFile) {
		List<ScenarioPairFiles> list = new ArrayList<ScenarioPairFiles>();
		for (File propertiesFile : projectPropertiesFile) {
			File parent = propertiesFile.getParentFile();
			String simpleName = propertiesFile.getName().replace(propertiesSuffix, "");
			for (File xmlFile : projectXMLFiles) {
				if ((xmlFile.getParentFile().equals(parent)) && (xmlFile.getName().equals(simpleName + xmlSuffix))) {
					list.add(new ScenarioPairFiles(propertiesFile, xmlFile));
				}
			}
		}
		return list;
	}

	public List<ScenarioPropertiesFile> getScenariosPropertiesFiles() {
		if (scenarioPropertiesFiles == null) {
			scenarioPropertiesFiles = new ArrayList<ScenarioPropertiesFile>();
			for (File file : getPropertiesFiles()) {
				scenarioPropertiesFiles.add(new ScenarioPropertiesFile(file));
			}
		}
		return scenarioPropertiesFiles;
	}

	public List<ScenarioXMLFile> getScenariosXMLFiles() {
		if (scenarioXMLFile == null) {
			scenarioXMLFile = new ArrayList<ScenarioXMLFile>();
			for (File file : getXMLFiles()) {
				scenarioXMLFile.add(new ScenarioXMLFile(file));
			}
		}
		return scenarioXMLFile;
	}

	/**
	 * @return Get all the xml files in a standard JSystem project
	 */
	private List<File> getXMLFiles() {

		// Set the criteria that defines a file a properties file
		FileFilter xmlFileFilter = new FileFilter() {
			public boolean accept(File file) {
				// return (file.isFile() && (file.getName().endsWith(xmlSuffix))
				// && (isAncestorScenariosDirectory(file)) &&
				// (!isAncestorClassesDirectoty(file)));
				return (file.isFile() && (file.getName().endsWith(xmlSuffix)) && (isAncestorScenariosDirectory(file)));
			}
		};
		return listFilesRecursively(projectDir, xmlFileFilter, null);
	}

	/**
	 * @return Get a list of all project properties file
	 */
	private List<File> getPropertiesFiles() {

		// Set the criteria that define a file an xml file
		FileFilter propertiesFileFilter = new FileFilter() {
			public boolean accept(File file) {
				return (file.isFile() && (file.getName().endsWith(propertiesSuffix)) && (isAncestorScenariosDirectory(file)));
			}
		};
		return listFilesRecursively(projectDir, propertiesFileFilter, null);
	}

	/**
	 * This method verify if one of the ancestor of a file is a directory called
	 * "scenarios"
	 * 
	 * @param file
	 *            -
	 * @return true if one of the ancestor of a file is a directory called
	 *         "scenarios", false otherwise;
	 */
	private boolean isAncestorScenariosDirectory(File file) {
		while (file != null) {
			if (file.getName().equals("scenarios"))
				return true;
			file = file.getParentFile();
		}
		return false;
	}

	private boolean isAncestorClassesDirectoty(File file) {
		while (file != null) {
			if (file.getName().equals("classes"))
				return true;
			file = file.getParentFile();
		}
		return false;

	}

	/**
	 * @param dir
	 *            - The directory from where to start searching
	 * @param filter
	 *            - The criteria on which files are requested
	 * @param fileList
	 *            - null when called the first time
	 * @return - A list of all files in the dir and in its sub directories
	 */
	private List<File> listFilesRecursively(File dir, FileFilter filter, List<File> fileList) {
		if (!dir.isDirectory())
			return null;

		// The first time create the list that will be returned
		if (fileList == null) {
			fileList = new ArrayList<File>();
		}

		// Collect all the requested files and add them to the list
		File[] requestedFiles = dir.listFiles(filter);
		Collections.addAll(fileList, requestedFiles);

		// Retrieve all sub directories
		File[] subDirs = dir.listFiles(new FileFilter() {
			public boolean accept(File dir) {
				return dir.isDirectory();
			}
		});

		// For each sub directory call the method again
		for (File subDir : subDirs) {
			listFilesRecursively(subDir, filter, fileList);
		}

		return fileList;
	}

}
