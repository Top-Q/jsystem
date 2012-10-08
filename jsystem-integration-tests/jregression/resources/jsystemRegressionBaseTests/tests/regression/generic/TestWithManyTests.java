package regression.generic;


import regression.generic.*;
import java.io.*;

import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

public class TestWithManyTests{

	
	public static void main(String[] args) throws IOException{
		int i = 1;
		File file = new File("/home/aqua/workspace/jsystemRegressionBaseTests/tests/manualTests/manyTests.java");
		String testThatPassPlusNum;
		String header;
		header = "package manualTests;\n"+
								"import java.io.*;\n"+"import jsystem.utils.FileUtils;\n"+"import junit.framework.SystemTestCase;\n"+
								"public class manyTests extends SystemTestCase{\n";
		FileUtils.write(file, header, false);
		while (i < 2000){
			testThatPassPlusNum = "public void testThatPass"+i+"() {}\n\n";
			FileUtils.write(file, testThatPassPlusNum, true);
			i++;
		}
		FileUtils.write(file, "}", true);
	}
}