package jsystem.extensions.report.difido;

import il.co.topq.difido.PersistenceUtils;
import il.co.topq.difido.model.execution.Execution;
import il.co.topq.difido.model.test.TestDetails;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Summary;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.BrowserLauncher;
import jsystem.utils.DateUtils;

import org.apache.commons.io.FileUtils;

/**
 * 
 * @author Itai Agmon
 * 
 */
public class HtmlReporter extends AbstractHtmlReporter {

	private static final Logger log = Logger.getLogger(HtmlReporter.class.getName());

	private boolean isZipLogDisable;

	private String reportDir;

	private File logDirectory;

	private File logCurrent;

	private File logOld;

	@Override
	public void initReporterManager() throws IOException {
		BrowserLauncher.openURL(getIndexFile().getAbsolutePath());
	}

	private File getIndexFile() {
		return new File(getLogDirectory(), "current" + File.separator + "index.html");
	}

	@Override
	public void init() {
		isZipLogDisable = ("true".equals(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.HTML_ZIP_DISABLE)));
		setDeleteCurrent(!"false".equals(JSystemProperties.getInstance().getPreference(
				FrameworkOptions.REPORTER_DELETE_CURRENT)));
		updateLogDir();
		try {
			initReporter(!isZipLogDisable);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Fail to init HtmlTestReporter", e);
		}

	}

	protected void updateLogDir() {
		reportDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER);
		if (reportDir == null || reportDir.equals("./log")) {
			reportDir = "log";
			JSystemProperties.getInstance().setPreference(FrameworkOptions.LOG_FOLDER, reportDir);
		}
	}

	/**
	 * init current logs
	 * 
	 * @param directory
	 *            the "current" directory that contains the log
	 * @param zipFirst
	 *            if True will zip before deletion
	 * @param deleteCurrent
	 *            if True will delete current logs
	 * @throws Exception
	 */
	public void initReporter(boolean zipFirst) throws Exception {
		super.initModel();
		setLogDirectory(new File(reportDir));
		if (!getLogDirectory().exists()) {
			getLogDirectory().mkdirs();
		}
		logCurrent = new File(getLogDirectory(), "current");
		if (!logCurrent.exists()) {
			logCurrent.mkdirs();
		}
		String oDir = JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_OLD_DIRECTORY);
		if (oDir != null && !oDir.equals("")) {
			logOld = new File(oDir);
		} else {
			logOld = new File(getLogDirectory(), "old");
		}
		if (!logOld.exists()) {
			logOld.mkdirs();
		}
		//
		ZipDeleteLogDirectory dl = new ZipDeleteLogDirectory(logCurrent, logOld, isDeleteCurrent(), zipFirst);
		dl.start();
		try {
			dl.join();
		} catch (InterruptedException e) {
			return;
		}
		final File currentLogFolder = new File(reportDir, "current");
		if (isDeleteCurrent()) {
			PersistenceUtils.copyResources(currentLogFolder);
		} else {
			final File testDetailsHtmlFile = new File(currentLogFolder, PersistenceUtils.TEST_DETAILS_HTML_FILE);
			final File indexFile = new File(currentLogFolder, "index.html");
			if (!testDetailsHtmlFile.exists() && !indexFile.exists()) {
				PersistenceUtils.copyResources(currentLogFolder);
			}
		}
	}

	@Override
	protected void writeTestDetails(TestDetails testDetails) {
		PersistenceUtils.writeTest(testDetails, new File(reportDir + File.separator + "current"), new File(
				ListenerstManager.getInstance().getCurrentTestFolder()));

	}

	@Override
	protected void writeExecution(Execution execution) {
		PersistenceUtils.writeExecution(execution, new File(reportDir + File.separator + "current"));

	}

	@Override
	protected Execution readExecution() {
		return PersistenceUtils.readExecution(new File(reportDir + File.separator + "current"));
	}

	public void setLogDirectory(File logDirectory) {
		this.logDirectory = logDirectory;
	}

	public File getLogDirectory() {
		return logDirectory;
	}

	@Override
	public String getName() {
		return "DifidoHtmlReporter";
	}

	static class ZipDeleteLogDirectory extends Thread {
		private static Logger log = Logger.getLogger(ZipDeleteLogDirectory.class.getName());

		File toDelete = null;

		File oldDir = null;

		boolean deleteCurrent = false;

		boolean zipFirst = true;

		public static final File ZIP_FILE = new File(".zipped");

		public ZipDeleteLogDirectory(File toDelete, File oldDir, boolean deleteCurrent, boolean zipFirst) {
			super("ZipDeleteLogDirectory");
			this.toDelete = toDelete;
			this.oldDir = oldDir;
			this.deleteCurrent = deleteCurrent;
			this.zipFirst = zipFirst;
		}

		public void run() {
			boolean disableZipLog = "true".equals(JSystemProperties.getInstance().getPreference(
					FrameworkOptions.HTML_ZIP_DISABLE));

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
			 * If the date was not set in the beginning of test execution set it
			 * to the current time.
			 */
			String date = Summary.getInstance().getProperties().getProperty("Date");
			if (date == null) {
				date = DateUtils.getDate();
				if (date == null) {
					date = Long.toString(System.currentTimeMillis());
				}
			}
			String fileName = "log_" + date.replace(':', '_').replace(' ', '_').replace('+', '_');
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
					jsystem.utils.FileUtils.zipDirectory(toDelete.getPath(), "", zipFile.getPath(), JSystemProperties
							.getInstance().isJsystemRunner());
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to zip old log - Current logs are not deleted!!!", e);
				return;
			}
			File sutFile = SutFactory.getInstance().getSutFile(false);

			if (sutFile != null) { // no sut - probably someone tampered with
									// jsystem.properties file
				String setup = null;
				setup = sutFile.getName();

				if (setup != null && setup.toLowerCase().endsWith(".xml")) {
					setup = setup.substring(0, setup.length() - 4);
				}
				String oldPath = JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_OLD_PATH);
				File dest;
				if (oldPath == null) {
					dest = new File(oldDir.getPath() + File.separator + "setup-" + setup + File.separator + "version-"
							+ Summary.getInstance().getProperties().getProperty("Version"));
				} else {
					dest = findTreePath(oldDir, oldPath);
				}
				dest.mkdirs();
				try {
					if (zipFile.exists()) {
						FileUtils.copyFile(zipFile, new File(dest, fileName + ".zip"));
					}
				} catch (IOException e1) {
					log.log(Level.WARNING, "Fail to copy old log to Hierarchical folders of Sut and Version", e1);
					return;
				}
				/**
				 * if html.tree is set to true the log zip will be only in the
				 * tree.
				 */
				String htmlTree = JSystemProperties.getInstance().getPreference(FrameworkOptions.HTML_ZIP_TREE_ONLY);
				if (htmlTree != null && htmlTree.toLowerCase().equals("true")) {
					zipFile.delete();
				}
			} else {
				log.info("Skipped Html zip tree - No Sut!");
			}

			if (deleteCurrent) {
				deleteLogDirectory();
			} else {
				try {
					jsystem.utils.FileUtils.write(toDelete.getPath() + File.separator + ".zipped", "");
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
					String version = Summary.getInstance().getProperties().getProperty("Version");
					toReturn = new File(toReturn, "version-" + version);
				} else if (paths[i].toLowerCase().equals("scenario")) {
					String scenario = ScenariosManager.getInstance().getCurrentScenario().getName();
					toReturn = new File(toReturn, "scenario-" + scenario);
				} else {
					String value = Summary.getInstance().getProperties().getProperty(paths[i]);
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
			jsystem.utils.FileUtils.deltree(toDelete);
			if (toDelete.exists()) {
				log.info("Failed to delete current log directory: " + toDelete.getAbsolutePath());
			} else {
				toDelete.mkdirs();
			}
		}

	}

	@Override
	protected void filesWereAddedToReport(File[] files) {
		// Unused
	}

}
