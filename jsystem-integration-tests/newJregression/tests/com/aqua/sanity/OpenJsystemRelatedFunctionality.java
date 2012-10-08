package com.aqua.sanity;

import org.junit.Test;

import com.aqua.base.JSysTestCase4UseExistingServer;

public class OpenJsystemRelatedFunctionality extends
		JSysTestCase4UseExistingServer {
	public OpenJsystemRelatedFunctionality(){
		super();
	}
	
	public void setUp() throws Exception{
		super.setUp();
	}
	
	@Test
	public void checkRunnerOpensSuccessfullyWhenNoSutIsInSutFolderOfProject() throws Exception{
		report.step("copy the files under sut of remote project to another location temporarily");
//		String remoteTestDir = remoteInformationClient.getJsystemPropertyValueForKey("tests.dir");
//		File sutFolder = new File(remoteTestDir+File.separatorChar+"sut");
//		if(!sutFolder.exists()){
//			report.report("folder "+sutFolder.getAbsolutePath()+" does not exist!!!");
//			throw new Exception("folder "+sutFolder.getAbsolutePath()+" does not exist");
//		}
		//the tempFolder will be located in the tests.dir above the classes dir.
//		File tempFolder = new File(new File(remoteTestDir).getParent()+File.separatorChar+"tempDir");
//		if(!tempFolder.exists()){
//			tempFolder.mkdir();
//		}
//		report.step("close the remote application");
//		applicationClient.closeApp();
//		report.step("copy the content of the sut folder to a temp folder under the testProject folder");
//		FileUtils.copyDirectory(sutFolder, tempFolder);
//		FileUtils.deltree(sutFolder);
//		sutFolder.mkdir();
//		report.step("restart the application and check that it's started fine");
//		envController.startXmlRpcServer();
//		applicationClient.launch();
//		scenarioClient.addTest("testThatPass", "SimpleTests");
//		applicationClient.play(true);
//		
//		reporterClient.checkNumberOfTestsPass(2);
		
//		report.step("copy back the sut folder content and delete the temp directory");
//		FileUtils.copyDirectory(tempFolder, sutFolder);
//		FileUtils.deltree(tempFolder);
	}
}
