/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

// import jsystem.utils.ClassPathFile;
import java.io.File;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * A factory that manage the SUTs
 * 
 * @author Guy Arieli
 */
public class SutFactory {

	public static final String CREATE_A_NEW_SUT_FILE = "Create a new SUT file...";

	private static Logger log = Logger.getLogger(SutFactory.class.getName());

	private static SutFactory factory = null;

	private volatile static boolean suppressGUI = false;

	Sut usedSut = null;

	private SutFactory() {
	}

	/**
	 * Get an instance of the factory (not the SUT).
	 * Kobi Gana : adding synchronized block for Thread safe matter
	 * @return An instance of the factory.
	 */
	public static SutFactory getInstance() {
		synchronized (SutFactory.class) {
			if (factory == null) {
				factory = new SutFactory();
			}
		}
		return factory;
	}

	/**
	 * Resets static factory instance.
	 */
	public static void resetSutFactory(String sutFile){
		factory = null;
		if (sutFile == null){
			sutFile = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
		}
		//if no sut is set in jsystem.properties, don't set the sutInstance to null, simply run without sut
		if (sutFile != null){
			JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE,sutFile);
			getInstance().getSutInstance();
		}
	}

	/**
	 * Resets static factory instance.
	 */
	public static void resetSutFactory(){
		resetSutFactory(null);
	}

	/**
	 * Get an instance of the used SUT.
	 * 
	 * @return The SUT instance.
	 */
	public Sut getSutInstance() {
		if (usedSut == null) {
			try {
				usedSut = getNewSutInstance();
				init();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Unable to load sut class: " + JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_CLASS_NAME), e);
			}
		}
		return usedSut;
	}

	public Sut getNewSutInstance() throws Exception{
		String defaultSutClassName = null;
		defaultSutClassName = JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_CLASS_NAME);
		if (defaultSutClassName == null) {
			defaultSutClassName = "jsystem.framework.sut.SutImpl";
			JSystemProperties.getInstance().setPreference(FrameworkOptions.SUT_CLASS_NAME, defaultSutClassName);
		}
		Class<?> c = Class.forName(defaultSutClassName);
		return (Sut) c.getConstructor(new Class[0]).newInstance(new Object[0]);
	}

	/**
	 * Init the SUT.
	 */
	private void init() {
		File sutFile = getSutFile();
		if (sutFile == null) {
			log.fine("Fail to load SUT file");
			return;
		}
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, sutFile.getName());

		try {
			File file = new File(getSutDirectory(), sutFile.getName());
			usedSut.setSutXml(file);
			log.log(Level.FINE, "Use sut file: " + sutFile.getName());
		} catch (Exception e) {
			String message = "Unable to init sut with file: " + sutFile.getName() + " " + e.getMessage(); 
			log.log(Level.WARNING, message);
			log.log(Level.FINE, message,e);
		}
	}

	public Vector<String> getOptionalSuts() {
		File path = getSutDirectory();
		File[] list = path.listFiles();
		if (list != null && list.length > 0){
			Arrays.sort(list);
		}
		Vector<String> sutsVector = new Vector<String>();
		try {
			if (list == null) {
				return sutsVector;
			}
			for (int i = 0; i < list.length; i++) {
				if (list[i].getName().toLowerCase().endsWith(".xml")) {
					sutsVector.removeElement(list[i].getName());
					sutsVector.addElement(list[i].getName());
				}
			}
			return sutsVector;
		}finally {
			//add create new sut file menu item in the end of the list.
			if (!"false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_PLANNER))) {
				sutsVector.addElement(CREATE_A_NEW_SUT_FILE);
			}
		}
	}

	public File getSutFile() {
		return getSutFile(true);
	}

	/**
	 * get the current sut file<br>
	 * 
	 * @param needToOpenSUTCombo	if True and sut is not found, opens a comboBox of suts
	 * NOTE: if the runner was closed, do not use true!
	 * @return
	 */
	public File getSutFile(boolean needToOpenSUTCombo) {
		String sutFileName = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
		File sutDirectory = getSutDirectory();
		File sutFile;
		if (StringUtils.isEmpty(sutFileName)){ // patch in case the entry was deleted only in the current jsystem.properties file
			JSystemProperties.getInstance().restoreFromBackup();
			sutFileName = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
		}
		if (!StringUtils.isEmpty(sutFileName)) {
			File _sutFileName = new File(sutFileName);
			if (_sutFileName.exists()) {
				return _sutFileName;
			}
			sutFile = new File(sutDirectory, sutFileName);
			if (sutFile.exists() && sutFile.isFile()) {
				return sutFile;
			}
			log.warning("Can't find sut file: " + sutFile.getPath());
		}

		Vector<String> v = getOptionalSuts();
		if (v.size() == 0 || (v.size() == 1 && CREATE_A_NEW_SUT_FILE.equals(v.get(0)))) {
			try {
				return createDefaultSUT();
			}catch (Exception e){
				throw new RuntimeException(e);
			}
		}
		sutFileName=null;
		if (needToOpenSUTCombo && !getSuppressGUI()) {
			sutFileName = (String) JOptionPane.showInputDialog(TestRunnerFrame.guiMainFrame, "Select SUT File from list",
					"Select SUT", JOptionPane.INFORMATION_MESSAGE, null, v.toArray(), v.elementAt(0));
		}

		if (sutFileName == null) {
			try {
				return createDefaultSUT();
			}catch (Exception e){
				throw new RuntimeException(e);
			}
		}

		return new File(sutDirectory, sutFileName);
	}

	public void setSut(String sutName) {
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, sutName);
		init();
	}

	public int getCurrentSutIndex() {
		Vector<String> v = getOptionalSuts();
		String currentSut = JSystemProperties.getInstance().getPreference(FrameworkOptions.USED_SUT_FILE);
		for (int i = 0; i < v.size(); i++) {
			if (v.elementAt(i).toString().equals(currentSut)) {
				return i;
			}
		}
		return -1;
	}

	public File getSutDirectory() {
		String sutDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_DIR);
		if (sutDir != null && !(sutDir = sutDir.trim()).isEmpty()) {
			File sd = new File(sutDir);
			sd = new File(System.getProperty("user.dir") + File.separator + sutDir);
			if (sd.exists() && sd.isDirectory()) {
				return sd;
			} else {
				log.log(Level.WARNING, "SUT directory: " + sutDir + " couldn't be found");
			}
		}
		final File testsPath = new File(JSystemProperties.getCurrentTestsPath());
		if (testsPath.getName().equals("test-classes")){
			//ITAI: We are running from Eclipse project with Maven structure.
			return new File(testsPath.getParentFile()+File.separator+"classes","sut");
		}
		return new File(testsPath, "sut");
	}

	public static void suppressGUI() {
		suppressGUI = true;
	}

	private static boolean getSuppressGUI() {
		return suppressGUI; 
	}
	/**
	 */
	private static File createDefaultSUT() throws Exception {
		File sutDirectory = new File(JSystemProperties.getCurrentTestsPath(), "sut");		
		JSystemProperties.getInstance().setPreference(FrameworkOptions.USED_SUT_FILE, "default.xml");
		File defaultSut =new File(sutDirectory,"default.xml");
		if (!defaultSut.exists()){
			FileUtils.write(defaultSut,"<sut/>",false);
		}
		return defaultSut;
	}	
}
