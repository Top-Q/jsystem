package com.aqua.sanity;

import java.io.File;
import java.io.FileNotFoundException;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.fixture.FixtureManager;
import jsystem.utils.FileUtils;

import org.junit.Test;

import com.aqua.analyzers.BooleanAnalyzer;
import com.aqua.base.JSysTestCase4UseExistingServer;
import com.aqua.fixtures.CreateEnvFixture;
import com.aqua.fixtures.NewActivateRunnerFixture;
import com.aqua.jsystemobjects.JSystemEnvController;

/**
 * 
 * @author Dan Hirsch
 *
 */
public class CloseJSystemOperationTests extends JSysTestCase4UseExistingServer {

	public CloseJSystemOperationTests(){
		super();
	}
	
	/**
	 * 1. open runner and initReportes, check no logs available
	 * 2. add tests and run them, check that logs were created.
	 * 3. close runner gracefully and check that logs were backed up in old dir
	 * 4. open runner and initReporters, check no logs available
	 * 5. add tests and run them, check that logs were created.
	 * 6. close runner forcibly and check that logs were not backed up
	 * 7. open runner and check that logs were backed up in old directory.
	 * @throws Throwable 
	 */
	@Test
	public void checkLogsBackupOnRunnerReopenIfClosedFocibly() throws Throwable{
		boolean logsExist = false;
		boolean backupExist = false;
		File tmpDir = null;
		reporterClient.initReporters();
		applicationClient.setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE, "false");
		
		report.report("remote dir is: "+getRemoteLogsDir("current"));
		logsExist = runCreatedRemoteLogs(getRemoteLogsDir("current"));
		
		report.step("clear remote logs directory");
		//remove all logs from current directory
		tmpDir = getRemoteLogsDir("current");
		FileUtils.deltree(tmpDir);
		new File(tmpDir.getAbsolutePath()).mkdirs();
		
		report.step("clear remote logs backup directory");
		//remove all logs backups from old directory
		tmpDir = getRemoteLogsDir("old");
		FileUtils.deltree(tmpDir);
		new File(tmpDir.getAbsolutePath()).mkdirs();
		
		report.step("test that logs doesn't exist before play");
		analyzer.setTestAgainstObject(logsExist);
		analyzer.analyze(new BooleanAnalyzer(false));
		
		scenarioClient.createScenario("tempScenario");
		scenarioClient.addTest("testThatPass", "SimpleTests",5);
		applicationClient.play();//blocking
		
		report.step("test that after play logs exist");
		logsExist = runCreatedRemoteLogs(getRemoteLogsDir("current"));
		analyzer.setTestAgainstObject(logsExist);
		analyzer.analyze(new BooleanAnalyzer(true));
		
		report.step("close remote runner");
		applicationClient.closeApp();
		
		report.step("test that logs backup exist after runner standard close");
		backupExist = backupLogsCreated(getRemoteLogsDir("old"));
		analyzer.setTestAgainstObject(backupExist);
		analyzer.analyze(new BooleanAnalyzer(true));
		
		report.step("clear all log backup");
		//remove all logs backups from old directory
		tmpDir = getRemoteLogsDir("old");
		FileUtils.deltree(tmpDir);
		new File(tmpDir.getAbsolutePath()).mkdirs();
		
		report.step("navigate out and back in to the fixture to create remote env again in an orderly fassion.");
		FixtureManager.getInstance().goTo(CreateEnvFixture.class.getName());
		FixtureManager.getInstance().goTo(NewActivateRunnerFixture.class.getName());
		super.setUp(); 
		
		sleep(500);
		report.report("create a new run of tests");
		applicationClient.setJSystemProperty(FrameworkOptions.HTML_ZIP_DISABLE, "false");
		scenarioClient.createScenario("tempScenario");
		scenarioClient.addTest("testThatPass", "SimpleTests",5);
		applicationClient.play();//blocking
		
		//kill remote runner brutally.
		report.step("kill remote process forcibly");
		applicationClient.killRunnerProcess();
		
		backupExist = backupLogsCreated(getRemoteLogsDir("old"));
		report.step("test that backup logs doesn't exist");
		analyzer.setTestAgainstObject(backupExist);
		analyzer.analyze(new BooleanAnalyzer(false));

		report.step("navigate out and back in to the fixture to create remote env again in an orderly fassion.");
		FixtureManager.getInstance().goTo(CreateEnvFixture.class.getName());
		FixtureManager.getInstance().goTo(NewActivateRunnerFixture.class.getName());

		
		sleep(500);
		backupExist = backupLogsCreated(getRemoteLogsDir("old"));
		analyzer.setTestAgainstObject(backupExist);
		analyzer.analyze(new BooleanAnalyzer(true));
	}
	
	private boolean backupLogsCreated(File remoteBackupLogsdir){
		File[] files = remoteBackupLogsdir.listFiles();
		for(File file:files){
			if(file.getAbsolutePath().endsWith(".zip")){
				return true;
			}
		}
		return false;
	}
	
	private boolean runCreatedRemoteLogs(File remoteLogsRootDir){
		File[] files = remoteLogsRootDir.listFiles();
		for(File file:files){
			if(file.getAbsolutePath().contains("test_") && file.isDirectory()){
				return true;
			}
		}
		return false;
	}
	
	private File getRemoteLogsDir(String oldORcurrent) throws Exception{
		JSystemEnvController cont = (JSystemEnvController)system.getSystemObject("envController");
		File tmp = new File(cont.getRunnerOutDir()+File.separatorChar+"runnerout"+File.separatorChar+"runner"+File.separatorChar+"log"+File.separatorChar+oldORcurrent);
		if(tmp.exists()){
			return tmp;
		}
		else{
			throw new FileNotFoundException("no file with the name "+tmp.getAbsolutePath()+" exists");
		}
	}
}
