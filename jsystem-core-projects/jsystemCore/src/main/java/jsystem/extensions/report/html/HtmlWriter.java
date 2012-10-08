/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.summary.ContainerSummaryReport;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scenario.flow_control.AntFlowControl;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

public class HtmlWriter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4635612042883816752L;

	public static String PACKAGE_FRAME = "packageFrame";

	public static String TESTLIST_FRAME = "testList";

	public static String TEST_FRAME = "testFrame";

	private String resultDirectory = null;

	private String currentTestDir = null;

	private HtmlTestList packagesList = null;

	private HtmlTestList allTestsList = null;
	
	private HtmlTreeTestList scenarioHierarchy = null;

	private HtmlTestList allFailsList = null;

	private HtmlTestList lastPackageTestsList = null;

	protected HtmlTestList lastTestReportList = null;

	private String lastPackageName = null;

	private HashMap<String, HtmlTestList> testListsHash = new HashMap<String, HtmlTestList>();

	public HtmlTestList mainFrame = null;

	protected Report lastDisabledReport = null;

	private NameGenerator generator;
	
	private static Logger log = Logger.getLogger(HtmlWriter.class.getName());

	private int testCounter = 0;
	
	private Stack<HtmlTreeTestList> containersStack;
	
	private HtmlTreeTestList currentContainer;
	
	HashMap<String, Integer> rootScenario;
	
	private TestInfo lastTestInfo = null;
	
	
	
	public HtmlWriter(String resultDirectory) throws IOException {
		this.resultDirectory = resultDirectory;
		File resultDir = new File(resultDirectory);
		if (!resultDir.exists()) {
			resultDir.mkdirs();
		}
		generator = new NameGenerator();
		
		packagesList = new HtmlTestList(resultDirectory, null,"package_list");
		packagesList.setFileName(generator.getName());
		packagesList.setTarget(TESTLIST_FRAME);
		packagesList.setFastList(false);
		
		createIndexFile();

		scenarioHierarchy = new HtmlTreeTestList(resultDirectory, null);
		scenarioHierarchy.setFileName(generator.getName());
		scenarioHierarchy.setTarget(TESTLIST_FRAME);
		scenarioHierarchy.setTitle("Scenario Hierarchy:");
		scenarioHierarchy.setFastList(false);
		scenarioHierarchy.setTreeRoot(true);
		scenarioHierarchy.doToFile(generator);
		
		allTestsList = new HtmlTestList(resultDirectory, null,"all_tests");
		allTestsList.setFileName(generator.getName());
		allTestsList.setTarget(TESTLIST_FRAME);
		allTestsList.setTitle("All Tests");
		allTestsList.setFastList(false);
		allTestsList.doToFile(generator);

		allFailsList = new HtmlTestList(resultDirectory, null,"all_fails");
		allFailsList.setFileName(generator.getName());
		allFailsList.setTarget(TESTLIST_FRAME);
		allFailsList.setTitle("All Fails");
		allFailsList.setFastList(false);
		allFailsList.doToFile(generator);

		TestReport tmpReport = new TestReport("ScenarioHierarchy:", null, Reporter.PASS, false, false, resultDirectory);
		scenarioHierarchy.addReport(tmpReport);
		
		
		tmpReport = new TestReport("All Tests:", null,
				Reporter.PASS, false, false, resultDirectory);
		allTestsList.addReport(tmpReport);

		tmpReport = new TestReport("All Fails:", null, Reporter.PASS, false,
				false, resultDirectory);
		allFailsList.addReport(tmpReport);

		tmpReport.toFile(generator);
		
		currentContainer = scenarioHierarchy;
		
		try {
			addResourceFiles();
		} catch (Exception e) {
			log.warning("Failed adding needed html files for sceario Hierarchy");
		}
		
		rootScenario = new HashMap<String, Integer>();
		containersStack = new Stack<HtmlTreeTestList>();


		legacyPackageList();
		//note method doesn't do anything if FrameworkOptions.HTML_PACKAGE_LIST is not set.
		updatePackageListFromPropsFile();
		updateTestDirectoryFile("");
		scenarioHierarchy.setContainerSummaryReport(new ContainerSummaryReport(new File(resultDirectory,generator.getName()),"root"));
	}
		
	/**
	 * Creates the legacy package list, which includes the following links:
	 * 1. summary report
	 * 2. scenario hierarchy
	 * 3. allTests
	 * 4. allFails
	 * 5. tests by package
	 */
	private void legacyPackageList() {
		packagesList.addReport(new Link("summary.html", "Summary report",
				TEST_FRAME));
		packagesList.addReport(new LineBreak());
		TestReport tmpReport = new TestReport("Tests packages:", null,
				Reporter.PASS, false, false, resultDirectory);
		packagesList.addReport(tmpReport);
		packagesList.addReport(scenarioHierarchy);
		packagesList.addReport(allTestsList);
		packagesList.addReport(allFailsList);
		
		try {
			packagesList.doToFile(generator);
		}catch (Exception e) {
			log.fine("Failed updating package list file." + e.getMessage());
		}		
	}
	
	/**
	 * Updates package list area with links taken from 
	 * a property file.
	 * Name of property file is taken from jsystem.properties (FrameworkOptions.HTML_PACKAGE_LIST)
	 * For each property in the properties file, a link is added, the key is link text, and property value
	 * is the url referred by the link.
	 */
	private void updatePackageListFromPropsFile() {
		try {
			String propsFileName = JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_PACKAGE_LIST);
			if (StringUtils.isEmpty(propsFileName)){
				return;
			}
			packagesList.removeAllReports();
			Properties p = FileUtils.loadPropertiesFromFile(propsFileName);
			Enumeration<Object> e = p.keys();
			while (e.hasMoreElements()){
				String k = e.nextElement().toString();
				String v = p.getProperty(k);
				packagesList.addReport(new Link(v, k,TEST_FRAME));
			}
			packagesList.toFile(generator);
		}catch (Exception e1) {
			log.fine("Failed updating package list file." + e1.getMessage());
		}		
		
	}
	/**
	 * add javascript, css and images
	 * @throws Exception 
	 */
	private void addResourceFiles(){
		
		String sourceFolder =  "jsystem/extensions/report/html/resources/";
		String[] fileNames = {"bullet.gif","minus.gif","plus.gif","mktree.js","mktree.css","scenario.gif"};
		String destinationFolder = resultDirectory+"/";
		for (String fileName : fileNames){
			copyResource(sourceFolder+fileName, new File(destinationFolder,fileName));
		}
		
		//copy additional css
		String cssPathList[] = StringUtils.split(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.HTML_CSS_PATH),";");
		for (String cssPath:cssPathList){
			copyResource(cssPath,new File(destinationFolder,FileUtils.getFileNameWithoutFullPath(cssPath)));
		}
	}

	private void copyResource(String sourcePath,File destination) {
		ClassLoader loader = LoadersManager.getInstance().getLoader();
		InputStream is = loader.getResourceAsStream(sourcePath);
		try {
			FileUtils.saveInputStreamToFile(is,destination);
		} catch (Exception e) {
			log.warning("Failed copying File "+sourcePath+" needed to represent Html Hierarchy");
		}finally{
			try {
				is.close();
			} catch (IOException e) {
				log.warning("failed closing input stream");
			}
		}
	}

	public synchronized String newTestStart(TestInfo testinfo) throws IOException {
		testCounter++;

		String testName;
		String tName = StringUtils.getClassName(testinfo.className);
		String actualName = testinfo.methodName == null ? tName : tName + "." + testinfo.methodName; 
		String packageName = StringUtils.getPackageName(testinfo.className);
		/*
		 * If meaningful name exist will use it
		 */
		if(testinfo.meaningfulName != null){
			testName = testinfo.meaningfulName;
		} else {
			testName = actualName;
		}
		/*
		 * If comment exits will add it to the current name
		 */
		if (testinfo.comment != null){
			testName = testName + " - " + testinfo.comment;
		}

		if (testinfo.className == null) {
			return null;
		}
		
		String usedName = null;
		if (testinfo.count == 1) {
			usedName = testCounter + " " + testName;
			actualName = testCounter + " "+actualName;
		} else {
			usedName = testCounter + " " + testName + "(" + testinfo.count + ")";
			actualName = testCounter + " " + actualName + "(" + testinfo.count + ")";
		}
		
		currentTestDir = "test_" + testCounter;

		updateTestDirectoryFile(currentTestDir);
		// The test is from a new package the list should be created/get.
		if (!packageName.equals(lastPackageName)) {
			lastPackageName = packageName;
			lastPackageTestsList = (HtmlTestList) testListsHash
					.get(lastPackageName);
			if (lastPackageTestsList == null) { // create a new package list				
				lastPackageTestsList = new HtmlTestList(resultDirectory, null,"package_list");
				lastPackageTestsList.setFileName(generator.getName());
				lastPackageTestsList.setTarget(TESTLIST_FRAME);
				lastPackageTestsList.setTitle(packageName);
				lastPackageTestsList.setFastList(false);
				lastPackageTestsList.doToFile(generator);

				TestReport tmpReprot = new TestReport("Tests in package "
						+ lastPackageName + ":", null, Reporter.PASS, false,
						false, resultDirectory);
				lastPackageTestsList.addReport(tmpReprot);
				testListsHash.put(lastPackageName, lastPackageTestsList);
				packagesList.addReport(lastPackageTestsList);
				tmpReprot.toFile(generator);
			} else {
				lastPackageTestsList = (HtmlTestList) testListsHash
						.get(lastPackageName);

			}
		}
		lastTestReportList = new HtmlTestList(resultDirectory, null,"test_list");
		lastTestReportList.setFastList(true);
		String testFileName = generator.getName();
		lastTestReportList.setFileName(testFileName);
		lastTestReportList.setTarget(TEST_FRAME);
		
		lastTestReportList.setTitle(usedName);
		lastTestReportList.setFileName(testFileName);
		if (testinfo.parameters != null) {
			lastTestReportList.setAlt(testinfo.parameters);
		}
		currentContainer.addReport(lastTestReportList);
		
		allTestsList.addReport(lastTestReportList);
		lastPackageTestsList.addReport(lastTestReportList);
		lastTestInfo = testinfo;
		return testFileName;
	}
	
    /**
	 * Update a file which holds current test directory name
	 * 
	 * @param directory	the current test directory name
	 */
	private void updateTestDirectoryFile(String directory){
		Properties p = new Properties();
		if (!StringUtils.isEmpty(directory)){
			try {
				FileUtils.addPropertyToFile(CommonResources.TEST_INNER_TEMP_FILENAME, CommonResources.TEST_DIR_KEY, directory);
			}catch (Exception e) {
				log.log(Level.WARNING,"Failed updating tmp properties",e);
			}
		}
	}
	
	/**
	 * signal that a container started, create a new html element and add to stack
	 * 
	 * @param container	the container object
	 */
	public void startContainer(JTestContainer container){
		boolean isRoot = false;
		String title = "";
		if (container.isRoot()){
			String name = ScenarioHelpers.removeScenarioHeader(container.getTestName());
			title = name;
			Integer index = (Integer) rootScenario.get(name);
			int rootIndex = 1;
			if (index != null) {
				rootIndex = index.intValue() + 1;
				title += "(" + rootIndex + ")";
			}
			rootScenario.put(name, rootIndex);
			isRoot = true;
			
		}else if (container instanceof AntForLoop) {
			title = ((AntForLoop)container).getTestName(0);
		}else if (container instanceof AntFlowControl){
			title = container.getTestName();
		}else{ // Scenario
			title = ScenarioHelpers.removeScenarioHeader(container.getTestName());
		}
		addHierarchyList(title,isRoot);
	}
	
	/**
	 * signal that a container ended, switch to the higher container
	 * 
	 * @param container	the container object
	 */
	public void endContainer(JTestContainer container){
		HtmlTreeTestList tmp = currentContainer;
		currentContainer = containersStack.pop();
		if (container.isHiddenInHTML() && container.isSuccess()){
			currentContainer.removeReport(tmp);
			updateHierarchy();
		}
	}

	private void updateHierarchy() {
		try {
			scenarioHierarchy.setStatusChange(true);
			scenarioHierarchy.toFile(generator);
		}catch (Exception e){
			throw new RuntimeException("Failed saving report file");
		}
	}
	
	/**
	 * signal a loop start with a given count
	 * 
	 * @param loop	loop object
	 * @param count	loop number
	 */
	public void startLoop(AntForLoop loop, int count){
		addHierarchyList(loop.getTestName(count),false);
	}
	
	/**
	 *  signal a loop ended with a given count
	 * 
	 * @param loop	loop object
	 * @param count	loop number
	 */
	public void endLoop(AntForLoop loop, int count){
		currentContainer = containersStack.pop();
	}
	
	/**
	 * add an html list object and update hierarchy
	 *  
	 * @param title	the title of the html object
	 */
	private void addHierarchyList(String title,boolean isRoot){
		HtmlTreeTestList tmpContainer = new HtmlTreeTestList(resultDirectory,null);
		tmpContainer.setRootScenario(isRoot);
		tmpContainer.setTarget(TEST_FRAME);
		tmpContainer.setTitle(title);
		tmpContainer.setContainerSummaryReport(new ContainerSummaryReport(new File(resultDirectory,generator.getName()),title));
		containersStack.push(currentContainer);
		currentContainer.addReport(tmpContainer);
		currentContainer = tmpContainer;
		
	}
	
	
	/**
	 * 
	 * @return current main frame file name
	 */
	public String getCurrentMainFrameFileName() {
		return lastTestReportList.getFileName();
	}

	/**
	 * Create new HtmlTestList with specific file name
	 * 
	 * @param fileName
	 * @return a list of test report
	 */
	public HtmlTestList createNewLevelReportList(String fileName) {
		String title = lastTestReportList.getTitle();
		String dir = lastTestReportList.getLogDirectory();
		lastTestReportList = new HtmlTestList(dir, currentTestDir,"level_list");
		lastTestReportList.setFastList(true);
		lastTestReportList.setFileName(fileName);
		lastTestReportList.setTarget(TEST_FRAME);
		lastTestReportList.setTitle(title);
		return lastTestReportList;
	}

	public void backToMainFrame() {
		if (mainFrame != null){
			lastTestReportList = mainFrame;
		}

	}

	public HtmlTestList getLastTestReportList() {
		return lastTestReportList;
	}

	public synchronized void endTest(long runningTime, boolean failed)
			throws IOException {
		
		if (lastTestInfo != null && lastTestInfo.isHiddenInHTML && !failed && lastTestReportList != null){
			currentContainer.removeReport(lastTestReportList);
			
			allTestsList.removeReport(lastTestReportList);
			allTestsList.setStatusChange(true);
			allTestsList.toFile(generator);
			
			lastPackageTestsList.removeReport(lastTestReportList);	
			lastPackageTestsList.setStatusChange(true);
			lastPackageTestsList.toFile(generator);
			
			deleteListFiles(lastTestReportList);
			lastTestReportList = null;
		}		
		
		if (failed && lastTestReportList != null) {
			allFailsList.addReport(lastTestReportList);
			allFailsList.toFile(generator);
		}

		updateHierarchy();
		updatePackageListFromPropsFile();
	}

	public synchronized void addReport(TestReport testReport) throws IOException {
		if (lastTestReportList == null) {
			return;
		}
		testReport.setDirectory(currentTestDir);
		testReport.setLogDirectory(resultDirectory);
		testReport.setTarget(TEST_FRAME);
		lastTestReportList.addReport(testReport);
		testReport.toFile(generator);
	}

	/**
	 * Create basic index.html file
	 * 
	 * @throws IOException
	 */
	private void createIndexFile() throws IOException {

		StringBuilder sb = new StringBuilder("");
		sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n").
		append("<HTML>\n").append("<HEAD>\n").append("<TITLE>\n").
		append("JSystem results\n").append("</TITLE>\n").append("</HEAD>\n").
		append("<FRAMESET cols=\"30%,70%\">\n").
		append("<FRAMESET rows=\"30%,70%\">\n").
		append("    <FRAME src=\"report1.html\" name=\"").append(PACKAGE_FRAME).
		append("\">\n").append("    <FRAME src=\"report2.html\" name=\"").
		append(TESTLIST_FRAME).append("\">\n").append("</FRAMESET>\n").
		append("<FRAME src=\"summary.html\" name=\"").append(TEST_FRAME).append("\">\n").
		append("</FRAMESET>\n").append("</HTML>\n");
		FileWriter file = new FileWriter(resultDirectory + File.separatorChar
				+ "index.html");
		file.write(sb.toString());
		file.close();

	}

	/**
	 * Return the last test report status.
	 * 
	 * @return Test status, true for success and false for fail.
	 */
	public int getLastTestReportStatus() {
		return lastTestReportList.isSuccess();
	}
	
	/**
	 * signal a run ended
	 */
	public void runEnded(){
		containersStack.clear();
		currentContainer = scenarioHierarchy;
	}
	
	public void resetCurrentContainer(){
		currentContainer = scenarioHierarchy;
	}
	
	public void flush() throws Exception {
		scenarioHierarchy.setStatusChange(true);
		scenarioHierarchy.doToFile(generator);
		packagesList.doToFile(generator);
		allTestsList.doToFile(generator);
		allFailsList.doToFile(generator);
		
		Collection<HtmlTestList> lists = testListsHash.values();
		Iterator<HtmlTestList> iter = lists.iterator();
		while(iter.hasNext()){
			iter.next().doToFile(generator);
		}
	}
	
	private void deleteListFiles(HtmlTestList list) {
		File folder = new File(list.getLogDirectory(),currentTestDir);
		File testHtmlFile = new File(list.getLogDirectory(),list.fileName);
		if (!testHtmlFile.delete()) {
			log.info("Failed deleteing hidden test file " + list.fileName);
		}
		FileUtils.deltree(folder);
	}
	
	public void setContainerProperties(int ancestorLevel, String key,String value) {
		HtmlTreeTestList list = currentContainer;
		if (ancestorLevel > 0){
			int pos = 0;
			if (ancestorLevel < containersStack.size()) {
				pos = containersStack.size() - ancestorLevel;
			}
			list = containersStack.get(pos);
		}
		list.getContainerSummaryReport().setProperty(key, value);
	}
}