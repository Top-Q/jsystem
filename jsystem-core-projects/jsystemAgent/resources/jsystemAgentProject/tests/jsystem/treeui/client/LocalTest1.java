package jsystem.treeui.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase;

/**
 * This system test case class creates the file MyFile.txt in
 * the local working directory. 
 * @author Guy Chen
 *
 */

public class LocalTest1 extends SystemTestCase {

	public LocalTest1() throws Exception {

	}

	/**
	 * Create file MyFile.txt
	 */

	public void testCreateFile() throws Exception {
		report.step("create Summary file");
		Object value = Summary.getInstance().getProperty("Number");
		report.report("Work with the internal project");
		int counter = value == null? 1 : Integer.parseInt(value.toString())+1;
		Summary.getInstance().setProperty("Number",""+counter);
		
		File f = new File("summary.properties");
		if(!f.exists()){
			throw new Exception("summary.properties file not created");
		}else{
			report.report("summary.properties file was created successfully");
		} 
		
		File newFile;
		Writer output = null;

		report.report("Save state");
		ListenerstManager.getInstance().saveState(this);
		report.step("Sleep 10 seconds before creating file 1");
		sleep(10000);
		
		String text = "Hello all! this is creating file by java and should be biggger than others";
		newFile = new File("MyFile.txt");
		report.step("Create file 1 at : " + newFile.getAbsolutePath());
		output = new BufferedWriter(new FileWriter(newFile));
		output.write(text);
		output.close();
	}
}
