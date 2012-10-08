package jsystem.treeui.client;


import java.io.*;
import junit.framework.SystemTestCase;

/**
 * This system test case class creates the file MyFile2.txt in the local working
 * directory.
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
		report.step("Create file");
		String text = "Hello all! this is creating file by java and should be biggger than others";
		newFile = new File("MyFile2.txt");
		output = new BufferedWriter(new FileWriter(newFile));
		output.write(text);
		output.close();
	}
}
