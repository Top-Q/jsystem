/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.report.html.summary.HtmlSummaryReporter;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunnerStatePersistencyManager;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ExtendTestReporter;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Summary;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.BrowserLauncher;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.NamedTest;
import junit.framework.Test;

/**
 * A TestReporter implementation that write the report to html.
 * 
 * @author Guy Arieli
 */
public class HtmlTestReporter implements ExtendTestReporter,
		ExtendTestListener, Externalizable {

	protected static Logger log = Logger.getLogger(HtmlTestReporter.class
			.getName());

	protected HtmlWriter writer = null;

	private File logDirectory = null;

	private File logCurrent = null;

	private File logOld = null;

	private File logIndexFile = null;

	private HtmlSummaryReporter summary;

	private String lastTestFileName;

	protected int lastTestStatus = Reporter.PASS;

	private String lastTestClassName = null;
	
	private TestInfo lastTestInfo;
	
	private long startTestTime = 0;

	private String reportDir;

	boolean isTemp = false;
	
	private String timeStampToReplace = "";

	/**
	 * If set to true (by the html.zip.disable=true in the jsystem.properties
	 * file) The log will not be ziped/backuped on runner exit.
	 */
	private boolean isZipLogDisable = false;

	public void setLastTestStatus(int lastTestStatus) {
		this.lastTestStatus = lastTestStatus;
	}

	public int getLastTestStatus() {
		return lastTestStatus;
	}

	public HtmlTestReporter() throws Exception {
		if (!JSystemProperties.getInstance().isReporterVm()) {
			throw new Exception("You are trying to activate the HTML reporter from the test, while test is communicating with the runner. " +
					"A common reason for this problem is activating the reporter from the RunnerListenersManager. Use the ListenersManager instead");
		}
		boolean loadReportersState = 
			RunnerStatePersistencyManager.getInstance().getLoadReporters();
		
		if (loadReportersState){
			return;
		}
		updateLogDir();
		/*
		 * Init the isZipLogDisable option. By default it's not set so it's not
		 * disabled.
		 */
		isZipLogDisable = ("true".equals(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.HTML_ZIP_DISABLE)));
		setLogDirectory(new File(reportDir + File.separator + "current"));

		// check if old reports where not backed up, if so , back up before
		// deletion
		logCurrent = new File(getLogDirectory(), "test_1");
		File zipped = new File(getLogDirectory(), ".zipped");
		if (logCurrent.exists() && !zipped.exists()) {
			init(!isZipLogDisable, true);
			log.info("Logs were found without a backup, creating a backup of logs and deleting old logs...");
		} else {
			init(false, true);
		}
		
		/*
		 * If the ZIP log option is not disabled
		 */
		if (!isTemp) { // zip on runner close
			ZipDeleteLogDirectory dl = new ZipDeleteLogDirectory(logCurrent, logOld, false,true);
			addToShutdownHook(dl);
	    }
	}
	
	/**
	 * re-read html log directory from the jsystem.properties file
	 */
	protected void updateLogDir(){
		reportDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER);
		if (reportDir == null || reportDir.equals("./log")) {
			reportDir = "log";
			JSystemProperties.getInstance().setPreference(FrameworkOptions.LOG_FOLDER,reportDir);
		}
	}

	public HtmlTestReporter(String directory, boolean isTemp) throws Exception {
		reportDir = directory;
		this.isTemp = isTemp;
		init();
		summary = new HtmlSummaryReporter(logCurrent);
	}

	public void init() {
		isZipLogDisable = ("true".equals(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.HTML_ZIP_DISABLE)));
		try {
			init(!isZipLogDisable, true);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fail to init HtmlTestReporter", e);
		}
	}

	/**
	 * init current logs
	 * 
	 * @param directory	the "current" directory that contains the log
	 * @param zipFirst	if True will zip before deletion
	 * @param deleteCurrent	if True will delete current logs
	 * @throws Exception
	 */
	public void init(boolean zipFirst, boolean deleteCurrent)	throws Exception {
		updateLogDir();
		setLogDirectory(new File(reportDir));
		if (!getLogDirectory().exists()) {
			getLogDirectory().mkdirs();
		}
		logCurrent = new File(getLogDirectory(), "current");
		if (!logCurrent.exists()) {
			logCurrent.mkdirs();
		}
		String oDir = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.HTML_OLD_DIRECTORY);
		if (oDir != null && !oDir.equals("")) {
			logOld = new File(oDir);
		} else {
			logOld = new File(getLogDirectory(), "old");
		}
		if (!logOld.exists()) {
			logOld.mkdirs();
		}
		//
		ZipDeleteLogDirectory dl = new ZipDeleteLogDirectory(logCurrent,
				logOld, deleteCurrent,zipFirst);
		dl.start();
		try {
			dl.join();
		} catch (InterruptedException e) {
			return;
		}
		summary = new HtmlSummaryReporter(logCurrent);
		writer = new HtmlWriter(logCurrent.getPath());
	}

	private static boolean addZip = false;

	private void addToShutdownHook(ZipDeleteLogDirectory dl) {
		if (!addZip) {
			Runtime.getRuntime().addShutdownHook(dl);
			addZip = true;
		}
	}
	
	public void initReporterManager() throws IOException {

		BrowserLauncher.openURL(getIndexFile().getAbsolutePath());

	}

	public boolean asUI() {
		return true;
	}

	public void report(String title, String message, boolean isPass,boolean bold, boolean ignore) {
		try {
			int status;
			if (isPass) {
				status = Reporter.PASS;
			} else {
				status = Reporter.FAIL;
				lastTestStatus = status;
			}
			writer.addReport(new TestReport(title, String.valueOf(message),status, bold, ignore, null));
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to add report", e);
		}
	}

	public void report(String title, String message, boolean isPass,
			boolean bold) {
		report(title, message, isPass, bold, false);
	}

	public String getName() {
		return "html report";
	}

	public void addError(Test test, Throwable t) {
		lastTestStatus = Reporter.FAIL;
	}

	public void addFailure(Test test, AssertionFailedError t) {
		lastTestStatus = Reporter.FAIL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.system.ExtendTestListener#addWarning(junit.framework.Test)
	 */
	public void addWarning(Test test) {
		lastTestStatus = Reporter.WARNING;
	}

	public void endTest(String packageName, String testName, String methodName,long time) {
		if (methodName != null && !(lastTestStatus != Reporter.FAIL && lastTestInfo.isHiddenInHTML) ) {
			summary.endTest(testName + "." + methodName, packageName,lastTestFileName, lastTestStatus, time);
		}
		try {

			if (lastTestStatus != Reporter.FAIL) {
				writer.endTest(time, false);
			} else {
				writer.endTest(time, true);
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "End test notification fail", e);
		}

	}

	public void endTest(Test test) {
		String testName;
		String testClass = lastTestClassName;
		String packageName = StringUtils.getPackageName(testClass);
		testName = StringUtils.getClassName(testClass);
		String methodName = null;
		if (test instanceof NamedTest) {
			methodName = ((NamedTest) test).getMethodName();
		}
		endTest(packageName, testName, methodName, System.currentTimeMillis()- startTestTime);
	}

	public void startTest(TestInfo testInfo) {
		startTestTime = System.currentTimeMillis();
		lastTestStatus = Reporter.PASS;
		lastTestClassName = testInfo.className;
		lastTestInfo = testInfo;
		try {
			lastTestFileName = writer.newTestStart(testInfo);
		} catch (Exception e) {
			log.log(Level.WARNING, "Start test notification failed", e);
		}
	}

	public void startTest(Test test) {
		// not implemented
	}

	public File getLogDirectory() {
		return logDirectory;
	}

	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}

	public File getCurrentDirectory() {
		return logCurrent;
	}

	public File getIndexFile() {
		return logIndexFile;
	}

	public String getReportDir() {
		return reportDir;
	}

	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#saveFile(java.lang.String,
	 *      java.io.InputStream)
	 */
	public void saveFile(String fileName, byte[] content) {
		try {

			File file = new File(ListenerstManager.getInstance()
					.getCurrentTestFolder(), fileName);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			try {
				out.write(content);
			}finally{
				out.close();
			}
		} catch (IOException e) {
			log.log(Level.WARNING, "Fail to save file", e);
		}
	}

	public static boolean isImage(String fileName) {
		String[] imagesTypes = { ".jpg", ".png", ".gif" };
		for (int i = 0; i < imagesTypes.length; i++) {
			if (String.valueOf(fileName).toLowerCase().endsWith(imagesTypes[i])) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#report(java.lang.String,
	 *      java.lang.String, int, boolean)
	 */
	public void report(String title, String message, int status, boolean bold,
			boolean html, boolean link) {
		
		TestReport tr = null;

		if (link) {
			if (isImage(message)) {
				String mm = "<img src=\"" + message.replace('\\', '/') + "\">";
				tr = new TestReport(title, mm, status, false, false,null);
				tr.setHtmlMessage(true);
			} else {
				tr = new TestReport(title, null, status, false, false,null);
				tr.setFileName(String.valueOf(message));
			}
		} else {
			if (status == Reporter.FAIL) {
				lastTestStatus = Reporter.FAIL;
			} else if (lastTestStatus == Reporter.PASS
					&& status == Reporter.WARNING) {
				lastTestStatus = Reporter.WARNING;
			}
			tr = new TestReport(title, message, status, bold, false, null);
			tr.setTime(timeStampToReplace);
			timeStampToReplace = "";
			if (html) {
				tr.setHtmlMessage(true);
			}
		}
		try {
			writer.addReport(tr);
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to add report", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#startSection()
	 */
	public void startSection() {
		try {
			writer.addReport(new SectionReport(true));
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to add report", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.framework.report.ExtendTestReporter#endSection()
	 */
	public void endSection() {
		try {
			writer.addReport(new SectionReport(false));
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to add report", e);
		}
	}

	public void setData(String data) {
		// not implemented
	}

	public void endRun() {
		writer.runEnded();
	}

	public void addProperty(String key, String value) {
		String title = "Added Property: " + key + "=" + value;
		report(title, null, 0, false, false, false);

	}

	public void report(String title, String message, int status, boolean bold) {
		report(title, message, status == Reporter.PASS, bold);

	}
	/**
	 * 
	 * The HtmlTestReporter was refactored to serialize itself and de-serialize itself, 
	 * this was done to allow agent restart support. After restart, we don't want the report to be zipped
	 * and backed up, we want the user to see a continuous report, to achieve that, before agent restart, we 
	 * save html report state to a binary file, and after restart we load state from file, and thus we can 
	 * continue runing almost as if there was no restart.
	 * There is one thing however, in order to fully support saving reporter state we need to serialize
	 * the EventParser, and this is something I can't do in the so late in 5.5.
	 * The fact the the event parser is not serialized and de-serialized causes a bug in html report hierarchy.
	 * 
	 * The fast and most correct solution at this stage is that after restart the run after restart will be seen 
	 * as a a new hierarchy. To achieve that I'm pointing the currentContainer to tree root.
	 */

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		writer = (HtmlWriter) in.readObject();
		writer.resetCurrentContainer();
		logDirectory = (File) in.readObject();
		logCurrent = (File) in.readObject();
		logOld = (File) in.readObject();
		logIndexFile = (File) in.readObject();
		summary = (HtmlSummaryReporter) in.readObject();
		lastTestFileName = (String) in.readObject();
		lastTestStatus = (Integer) in.readObject();
		lastTestClassName = (String) in.readObject();
		startTestTime = (Long) in.readObject();
		reportDir = (String) in.readObject();
		isTemp = (Boolean) in.readObject();
		isZipLogDisable = (Boolean) in.readObject();

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(writer);
		out.writeObject(logDirectory);
		out.writeObject(logCurrent);
		out.writeObject(logOld);
		out.writeObject(logIndexFile);
		out.writeObject(summary);
		out.writeObject(lastTestFileName);
		out.writeObject(lastTestStatus);
		out.writeObject(lastTestClassName);
		out.writeObject(startTestTime);
		out.writeObject(reportDir);
		out.writeObject(isTemp);
		out.writeObject(isZipLogDisable);
	}

	@Override
	public void endContainer(JTestContainer container) {
		writer.endContainer(container);
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		writer.endLoop(loop, count);
	}

	@Override
	public void startContainer(JTestContainer container) {
		writer.startContainer(container);
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		writer.startLoop(loop, count);
	}
	
	public void flush() throws Exception {
		writer.flush();
		summary.saveFile();
	}

	@Override
	public void setContainerProperties(int ancestorLevel, String key,
			String value) {		
		writer.setContainerProperties(ancestorLevel, key, value);
	}

	public void setTimeStampToReplace(String timeStampToReplace) {
		this.timeStampToReplace = timeStampToReplace;
	}
}

class ZipDeleteLogDirectory extends Thread {
	private static Logger log = Logger.getLogger(ZipDeleteLogDirectory.class
			.getName());

	File toDelete = null;

	File oldDir = null;


	boolean deleteCurrent = false;

	boolean zipFirst = true;
	
	public static final File ZIP_FILE = new File(".zipped");

	public ZipDeleteLogDirectory(File toDelete, File oldDir, boolean deleteCurrent,boolean zipFirst) {
		super("ZipDeleteLogDirectory");
		this.toDelete = toDelete;
		this.oldDir = oldDir;
		this.deleteCurrent = deleteCurrent;
		this.zipFirst = zipFirst;
	}

	public void run() {
		boolean disableZipLog = "true".equals(JSystemProperties.getInstance()
				.getPreference(FrameworkOptions.HTML_ZIP_DISABLE));
			
		if (disableZipLog || !zipFirst) {
			if (deleteCurrent) {
				deleteLogDirectory();
			}
			return;
		}

		if (JSystemProperties.getInstance().isJsystemRunner()) {
			System.out.println("Log backup process ... (don't close)");
		}

		/*
		 * If the date was not set in the beginning of test execution set it to
		 * the current time.
		 */
		String date = Summary.getInstance().getProperties().getProperty("Date");
		if (date == null) {
			date = DateUtils.getDate();
			if (date == null) {
				date = Long.toString(System.currentTimeMillis());
			}
		}
		String fileName = "log_"
				+ date.replace(':', '_').replace(' ', '_').replace('+', '_');
		File zipFile = new File(oldDir, fileName + ".zip");
		int index = 1;
		String oFileName = fileName;
		while (zipFile.exists()) {
			fileName = oFileName + "_" + index;
			zipFile = new File(oldDir, fileName + ".zip");
			index++;
		}
		try {
			String[] toDeleteList = toDelete.list();
			if (toDeleteList != null && toDeleteList.length > 0) {
				FileUtils.zipDirectory(toDelete.getPath(), "", zipFile
						.getPath(), JSystemProperties.getInstance()
						.isJsystemRunner());
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to zip old log - Current logs are not deleted!!!", e);
			return;
		}
		File sutFile = SutFactory.getInstance().getSutFile(false);

		if (sutFile != null) { // no sut - probably someone tampered with jsystem.properties file
			String setup = null;
			setup = sutFile.getName();
	
			if (setup != null && setup.toLowerCase().endsWith(".xml")) {
				setup = setup.substring(0, setup.length() - 4);
			}
			String oldPath = JSystemProperties.getInstance().getPreference(
					FrameworkOptions.HTML_OLD_PATH);
			File dest;
			if (oldPath == null) {
				dest = new File(oldDir.getPath()
						+ File.separator
						+ "setup-"
						+ setup
						+ File.separator
						+ "version-"
						+ Summary.getInstance().getProperties().getProperty(
								"Version"));
			} else {
				dest = findTreePath(oldDir, oldPath);
			}
			dest.mkdirs();
			try {
				if (zipFile.exists()){
					FileUtils.copyFile(zipFile, new File(dest, fileName + ".zip"));
				}
			} catch (IOException e1) {
				log.log(Level.WARNING, "Fail to copy old log to Hierarchical folders of Sut and Version", e1);
				return;
			}
			/**
			 * if html.tree is set to true the log zip will be only in the tree.
			 */
			String htmlTree = JSystemProperties.getInstance().getPreference(
					FrameworkOptions.HTML_ZIP_TREE_ONLY);
			if (htmlTree != null && htmlTree.toLowerCase().equals("true")) {
				zipFile.delete();
			}
		}else{
			log.info("Skipped Html zip tree - No Sut!");
		}

		if (deleteCurrent) {
			deleteLogDirectory();
		} else {
			try {
				FileUtils.write(
						toDelete.getPath() + File.separator + ".zipped", "");
			} catch (IOException e) {
				log.warning("Creating .zip file was failed");
			}
		}
	}

	private File findTreePath(File root, String pathString) {
		String[] paths = pathString.split(";");
		File toReturn = root;
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].toLowerCase().equals("setup")) {
				String setup = SutFactory.getInstance().getSutFile().getName();
				if (setup != null && setup.toLowerCase().endsWith(".xml")) {
					setup = setup.substring(0, setup.length() - 4);
				}
				toReturn = new File(toReturn, "setup-" + setup);
			} else if (paths[i].toLowerCase().equals("version")) {
				String version = Summary.getInstance().getProperties()
						.getProperty("Version");
				toReturn = new File(toReturn, "version-" + version);
			} else if (paths[i].toLowerCase().equals("scenario")) {
				String scenario = ScenariosManager.getInstance()
						.getCurrentScenario().getName();
				toReturn = new File(toReturn, "scenario-" + scenario);
			} else {
				String value = Summary.getInstance().getProperties()
						.getProperty(paths[i]);
				if (value == null) {
					value = paths[i];
				}
				toReturn = new File(toReturn, value);
			}
		}
		return toReturn;
	}

	public void deleteLogDirectory() {
		if (!toDelete.exists()) {
			return;
		}
		FileUtils.deltree(toDelete);
		if (toDelete.exists()){
			log.info("Failed to delete current log directory: "+toDelete.getAbsolutePath());
		} else {
			toDelete.mkdirs();
		}
	}
	
	
}