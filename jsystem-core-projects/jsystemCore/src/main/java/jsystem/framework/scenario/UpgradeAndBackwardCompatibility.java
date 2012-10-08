/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProjectProperties;
import jsystem.framework.JSystemProperties;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Element;

/**
 * Utility class for upgrade to Jsystem 5.1.<br>
 * <br>
 * <u>JSystem 5.1 changes</u><br>
 * 1.In order to support flow control we use antcallback task instead of antcall task <br>
 * 2.In order to support distributed execution, we use jsystem-ant task instead ant task <br>
 * <br>
 * <u>Upgrade process</u><br>
 * Unlike 4.8 upgrade process, in 5.1 the upgrade process is incremental.<br>
 * The need to backup scenarios is checked when the runner starts or when switching a project.
 * the check involves the following:<br>
 * 1. Look for the {@link JSystemProjectProperties#JSYSTEM_PROJECT_PROPERTIES_FILE_NAME} file.<br>
 * 2. If it exists, look for the property {@link #SCENARIO_VERSION_PROPERTY_NAME}<br>
 * 3. If file is found and property value is {@link Version#JSystem5_1} this means that scenarios
 *    were backed up. The check ends here. Otherwise:<br>
 * 4. Project is scanned for scenarios that were created by older versions of jsystem.
 *    If such scenarios are found scenarios are backed-up, the user gets a message that
 *    scenarios were backed up and the version project property is updated.<br>
 *    Note that at this stage scenarios are not converted.<br>
 * 5. During runner work, when scenario is loaded the system identifies whether its a 5.1
 *    scenario or an older scenario and according to scenario version the scenario is loaded
 *    correctly.<br>
 * 6. If during scenario load the system identified that the scenario is an old version scenario, 
 *    it is immediately saved in the new format.
 *          
 * @author goland
 */
public class UpgradeAndBackwardCompatibility {

	private static Pattern SCENARIO_VERSION_PATTERN = 
		Pattern.compile("<property name=\"scenario.version\" value=\"(.*?)\"/>",Pattern.CASE_INSENSITIVE);

	public enum Version{
		JSystem5_0,
		JSystem5_1,
		JSystemLatest
	}
	
	public enum AntElement {
		antCall,
		ant,
	}
	public static final String SCENARIO_VERSION_PROPERTY_NAME = "scenario.version";
	
	private static Logger log = Logger.getLogger(UpgradeAndBackwardCompatibility.class.getName());
	private static Map<Version,Map<AntElement,String>> map;
	
	static {
		map = new HashMap<Version, Map<AntElement,String>>();
		Map<AntElement,String> map50 = new HashMap<AntElement, String>();
		map50.put(AntElement.antCall, "antcall");
		map50.put(AntElement.ant, "ant");
		map.put(Version.JSystem5_0, map50);
		Map<AntElement,String> mapLatest = new HashMap<AntElement, String>();
		mapLatest.put(AntElement.antCall, "antcallback");
		mapLatest.put(AntElement.ant, "jsystem-ant");
		map.put(Version.JSystemLatest, mapLatest);
	}

	/**
	 * Get ant task for jsystem version.
=	 */
	public static String getEntityForVersion(AntElement element, Version version){
		Map<AntElement,String> elements = map.get(version);
		if (elements!=null){
			String entity = elements.get(element);
			return entity;
		}
		log.warning("Problem checking entity \""+element+"\" for version "+version);
		return null;
	}
	
	/**
	 * Extracts scenario version from XML.
	 */
	public static Version getScenarioVersion(Element root ){
		Element property = XmlUtils.getElementWithName("property", SCENARIO_VERSION_PROPERTY_NAME, root);
		if (property == null){
			return Version.JSystem5_0;
		}
		String value = property.getAttribute("value");
		if (currentVersion().equals(value)){
			return Version.JSystemLatest;
		}
		return Version.valueOf(value);
	}
	
	/**
	 * Returns the current jsystem version.
	 */
	public static String currentVersion(){
		return Version.JSystem5_1.toString();
	}
	
	/**
	 * Checks whether to backup scenarios.
	 * @see {@link UpgradeAndBackwardCompatibility}
	 */
	public static boolean checkWhetherToBackupScenariosAndUpdateFlag() throws Exception {
		if ("never-5.1".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT))){
			return false;
		}
		if (getLastUpgradeToVersion().equals(Version.JSystem5_1)){
			return false;
		}
		setLastUpgradeToVersion(Version.JSystem5_1);
		if (!hasOlderScenarios(Version.JSystem5_1)){
			return false;
		}
		return true;
	}
	
	/**
	 * Backs up scenarios and returns backup file path.
	 */
	public static File backupScenarios() throws Exception {
		String classesFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		String classesFolderParent = new File(classesFolder).getParent();
		String backupName = getBackupFileName();
		File backupFile = new File(classesFolderParent,backupName);
		FileUtils.zipDirectory(
				classesFolder, 
				".xml", backupFile.getAbsolutePath());
		return backupFile;
	}


	/**
	 */
	private static Version getLastUpgradeToVersion() throws Exception{
		String projectVer = 
			JSystemProjectProperties.getInstance().getProperty(SCENARIO_VERSION_PROPERTY_NAME);
		try {
			return Version.valueOf(projectVer);
		}catch (Exception e){
			return Version.JSystem5_0;
		}
	}
	
	/**
	 */
	private static void setLastUpgradeToVersion(Version v) throws Exception{
		JSystemProjectProperties.getInstance().setProperty(SCENARIO_VERSION_PROPERTY_NAME,v.toString());
	}

	/**
	 */
	private static boolean hasOlderScenarios(final Version version) throws Exception {
		Vector<File> allScenarios = new Vector<File>();
		final String testsClassesFolderName = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER);
		if (testsClassesFolderName == null) {
			return false;
		}
		FileUtils.collectAllFiles(new File(testsClassesFolderName), new FilenameFilter() {
			public boolean accept(File dir, String name) {
				try {
					if (!name.endsWith(".xml")){
						return false;
					}
					String path = "/"+FileUtils.getRelativePath(dir,new File(testsClassesFolderName))+"/"+name;
					if (!Scenario.isScenario(path)){
						return false;
					}
					return !isScenarioInVersion(path,version);
				}catch (Exception e){
					return false;
				}
			}
		}, allScenarios);
		
		return allScenarios.size()> 0;
	}

	/**
	 */
	private static boolean isScenarioInVersion(String scenario,Version v) {
		try {
			CharSequence charSequence = 
				FileUtils.sequentialSequenceFromFile(JSystemProperties.getCurrentTestsPath() + scenario,400);
			Matcher m = SCENARIO_VERSION_PATTERN.matcher(charSequence);
			if (!m.find()){
				return false;
			}
			String version = m.group(1);
			return version.equals(v.toString());
		}catch (Exception e) {
			log.log(Level.WARNING,"Failed getting " + scenario + " version. "+e.getMessage());
			return false;
		}
	}
	
	/**
	 */
	private static String getBackupFileName() throws Exception {		
		String currentDate = 
			DateUtils.getDate(System.currentTimeMillis(), new SimpleDateFormat("MM_dd_hh_mm_ss"));
		return "scenariosBackup_"+currentDate+".zip";
	}
}
