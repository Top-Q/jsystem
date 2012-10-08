/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.junit.Test;


public class ScenarioHelpersTest {

	@Test
	public void checkPropertiesBehavior() throws Exception {
		Properties props = new Properties();
		props.put("key", "");
		props.store(new FileOutputStream("file.properties"),"");
		
		props = new Properties();
		props.load(new FileInputStream("file.properties"));
		System.out.println(props.get("key"));
	}
}
