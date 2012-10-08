/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.upgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * As from version 4.8 the format of scenario files was changed. The new format
 * is of standart ANT file format and we uses the ANT engien to execute the
 * scenario. The entry point will be <code>processOldFormatScenarios</code>
 * method.
 * 
 * @author guy.arieli
 * 
 */
public class ScenarioConversion {
	
	private static Logger log = Logger.getLogger(ScenarioConversion.class.getName());

	private static ScenarioConversion sc = null;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_hh_mm_ss");
	
	public static ScenarioConversion getInstance() {
		if (sc == null) {
			sc = new ScenarioConversion();
		}
		return sc;
	}

	static private DocumentBuilder db;

	private ScenarioConversion() {
		// private constractor (use the singleton)
		try {
			db = XmlUtils.getDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.log(Level.WARNING, "Fail to init the document builder", e);
		}
	}

	/**
	 * Collect all the scenario in the old format
	 * 
	 * @return a <code>Vector</code> of the old scenario
	 */
	public Vector<File> collectOldScenarios() {
		Vector<File> allScenarios = new Vector<File>();
		String testsClassesFolderName = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER);
		if (testsClassesFolderName == null) {
			return allScenarios;
		}
		FileUtils.collectAllFiles(new File(testsClassesFolderName), new FilenameFilter() {

			public boolean accept(File dir, String name) {
				if (!name.toLowerCase().endsWith(".xml")) {
					return false;
				}
				Document doc;
				try {
					doc = db.parse(new File(dir, name));
				} catch (Exception e) {
					return false;
				}
				/*
				 * If the root tag is <tests> ...
				 */
				return ((Element) doc.getDocumentElement()).getTagName().equals("tests");
			}

		}, allScenarios);
		return allScenarios;
	}

	/**
	 * Convert a single scenario file.
	 * 
	 * @param scenarioFile
	 *            the scenario file to convert.
	 * @return the converted scenario.
	 * @throws Exception
	 *             the scenario convertion failed.
	 */
	private Scenario convertScenario(File scenarioFile) throws Exception {
		log.info("Converting  " + scenarioFile);
		File classesDirectory = new File(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.TESTS_CLASS_FOLDER));
		String scenarioName = scenarioFile.getAbsolutePath().substring(classesDirectory.getAbsolutePath().length() + 1,
				scenarioFile.getAbsolutePath().length() - 4);
		FileInputStream fis = new FileInputStream(scenarioFile);
		Document doc = db.parse(fis);
		fis.close();
		/*
		 * If scenario is already converted will return it as is.
		 */
		if(((Element) doc.getDocumentElement()).getTagName().equals("project")){
			return ScenariosManager.getInstance().getScenario(scenarioName);
		}
		NodeList children = doc.getDocumentElement().getChildNodes();
		Scenario scenario = ScenariosManager.getInstance().getScenario(scenarioName);
		ScenariosManager.getInstance().setCurrentScenario(scenario);

		if (scenario.getTests().size() > 0) {
			return scenario;
		}

		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				Element el = (Element) children.item(i);
				if (el.getTagName().equals("scenario")) {
					String sName = el.getAttribute("name");
					String d = el.getAttribute("User-Doc");
					File scenarioToConvertFile = new File(classesDirectory.getAbsolutePath() + File.separator + sName
							+ ".xml");
					/*
					 * If the scenario doesn't exit will continue
					 */
					if(!scenarioToConvertFile.exists()){ 
						log.log(Level.WARNING,"Scenario file: " + sName + " couldn't be found and will be ignored");
						continue;
					}
					Scenario s = convertScenario(scenarioToConvertFile);
					if (d != null){
						s.setDocumentation(d);
					}
					scenario.addTest(s);
					
				} else {
					// at this point the test has no actual test
					// associated
					RunnerTest test = fromElement((Element) children.item(i));
					test.setParent(scenario);
					test.setDisable(Boolean.valueOf((String) test.getProperties().get("disable")).booleanValue());
					// fix the old scenario file format problem
					if (test.getClassName().indexOf(';') >= 0 || test.getMethodName().indexOf(';') >= 0) {
						continue;
					}
					scenario.addTest(test);
				}
			}
		}
		return scenario;
	}

	/**
	 * constructs a RunnerTest from and Element got from the scenario xml file
	 * initiates the test's properties from the element. uses the Test to get
	 * the method (includes searching in super classes)
	 * 
	 * @param e
	 *            the element from the xml file
	 * @param test
	 *            the new created RunnerTest Test
	 * @return a RunnerTest instance
	 * @throws Exception
	 */
	private static RunnerTest fromElement(Element e) throws Exception {
		String className = e.getAttribute("class");
		String methodName = ((Text) e.getFirstChild()).getData();
		if (methodName == null) {
			return null;
		}
		RunnerTest rTest = new RunnerTest(className, methodName);
		NamedNodeMap attributes = e.getAttributes();
		Properties p = new Properties();
		String name;
		for (int i = 0; i < attributes.getLength(); i++) {
			Node att = attributes.item(i);
			String attName = att.getNodeName();
			if (attName.equals("User-Doc")) {
				rTest.setDocumentation(att.getNodeValue());
				continue;
			}
			if (attName.equals("comment")) {
				rTest.setTestComment(att.getNodeValue());
				continue;
			}
			if (attName.equals("disable")) {
				if("true".equals(att.getNodeValue())){
					rTest.setDisable(true);
				}
				continue;
			}
			if (!attName.equals("class")) {
				name = attName;
				p.put(name, att.getNodeValue());

			}

		}
		rTest.setProperties(p);
		return rTest;
	}

	/**
	 * The entry point to the class. Execute all the conversion process
	 * 
	 */
	public void processOldFormatScenarios() {
		String convertString = JSystemProperties.getInstance().getPreference(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT);

		if (!StringUtils.isEmpty(convertString) && convertString.startsWith("true")) {
			return;
		}
		
		if (!StringUtils.isEmpty(convertString) && convertString.startsWith("never")) {
			return;
		}
		try {
			Vector<File> oldScenarios = ScenarioConversion.getInstance().collectOldScenarios();
			if (oldScenarios == null || oldScenarios.size() == 0) {
				JSystemProperties.getInstance().setPreference(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT, "true");
				return;
			}
			String classesFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
			String classesFolderPatent = new File(classesFolder).getParent();

			String backupName = getBackupFileName();
			File backupFile = new File(classesFolderPatent,backupName);

			if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null,
					"An old scenario format was found, JSystem can automatically\n"
							+ "convert them to the new format. A zip file with the old files\n"
							+ "will be created. The new scenarios are formated as\n" + "ANT file. Continue?",
					"Scenario format change", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE)) {
				return;
			}
			FileUtils.zipDirectory(
					classesFolder, 
					".xml", backupFile.getAbsolutePath());
			ErrorPanel.showErrorDialog("Old scenarios have been backed up to: " +backupFile.getName() , backupFile.getAbsolutePath(), ErrorLevel.Info);
			WaitDialog.launchWaitDialog("Convert scenarios", null);
			for (File scenarioFile : oldScenarios) {
				try {					
					ScenarioConversion.getInstance().convertScenario(scenarioFile);
				} catch (Exception ce) {
					WaitDialog.endWaitDialog();
					ErrorPanel.showErrorDialog("Scenario conversion fail", "Scenario file: " + scenarioFile.getName()
							+ "\n" + StringUtils.getStackTrace(ce), ErrorLevel.Warning);
					WaitDialog.launchWaitDialog("Convert scenarios", null);
				}
			}
			WaitDialog.endWaitDialog();
		} catch (Exception e) {
			log.log(Level.WARNING, "Old scenario convertion fail", e);
			return;
		}
		ScenariosManager.init();
	}
	
	private String getBackupFileName() throws Exception {		
		String currentDate = 
			DateUtils.getDate(System.currentTimeMillis(), dateFormat);
		return "scenariosBackup_"+currentDate+".zip";
	}
	
	public void resetScenarioConvertorFlag() {
		String convertString = JSystemProperties.getInstance().getPreference(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT);
		if (!StringUtils.isEmpty(convertString) && convertString.startsWith("never")){
			return;
		}
		JSystemProperties.getInstance().setPreference(FrameworkOptions.DISABLE_OLD_SCENARIO_CONVERT, "false");
	}
}
