package il.co.topq.refactor.refactorUtil;

import il.co.topq.refactor.infra.LoggerHandler;
import il.co.topq.refactor.utils.FileUtils;

import java.io.File;
import java.io.IOException;

import org.junit.Before;

public class AbstractTestCase {
	protected JSystemUtilImpl util;
	protected File testbed = new File("testbed");

	@Before
	public void before() throws Exception {
		LoggerHandler.initLogger();
		if (testbed.exists()) {
			FileUtils.deltree(testbed);
			if (testbed.exists()) {
				throw new IOException("Failed to delete testbed");
			}

		}
		if (!testbed.mkdir()) {
			throw new IOException("Failed to create testbed directory");
		}
		FileUtils.copyDirectory(new File("resources"), testbed);
		util = new JSystemUtilImpl(testbed);
	}

}
