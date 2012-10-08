package jsystem.treeui.client;



import java.io.*;
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
		File newFile;
		Writer output = null;

		sleep(10000);
		report.step("Create file");
		String text = "Hello all! this is creating file by java and should be biggger than others";
		newFile = new File("MyFile.txt");
		output = new BufferedWriter(new FileWriter(newFile));
		output.write(text);
		output.close();
	}
}
