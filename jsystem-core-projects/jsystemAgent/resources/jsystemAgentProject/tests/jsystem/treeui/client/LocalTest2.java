package jsystem.treeui.client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import junit.framework.SystemTestCase;

/**
 * This system test case class creates the file MyFile2.txt in the local working
 * directory.
 * 
 * @author Guy Chen
 * 
 */

public class LocalTest2 extends SystemTestCase {

	public LocalTest2() throws Exception {

	}

	/**
	 * Create file MyFile2.txt
	 */
	public void testCreateFile2() throws Exception {
		File newFile;
		Writer output = null;
		sleep(10000);
		String text = "Hello all! this is creating file by java and should be biggger than others";
		newFile = new File("MyFile2.txt");
		report.step("Create file 2 at : " + newFile.getAbsolutePath());
		output = new BufferedWriter(new FileWriter(newFile));
		output.write(text);
		output.close();
	}
}
