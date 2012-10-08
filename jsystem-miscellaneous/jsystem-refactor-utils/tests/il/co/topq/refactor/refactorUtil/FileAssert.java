package il.co.topq.refactor.refactorUtil;

import java.io.File;

import junit.framework.Assert;

public class FileAssert {

	public static void assertFileExists(final String fileName) {
		File file = new File(fileName);
		Assert.assertTrue("File " + fileName + " is not exists", file.exists());

	}

	public static void assertFileNotExists(final String fileName) {
		File file = new File(fileName);
		Assert.assertTrue("File " + fileName + " is exists", !file.exists());

	}

}
