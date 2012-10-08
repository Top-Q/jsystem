/*
 * Created on 05/05/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.utils;

import java.io.File;

import jsystem.utils.AntExecutor;
import jsystem.utils.exec.Command;
import junit.framework.SystemTestCase;

public class AntTest extends SystemTestCase {
	public void testAnt() throws Exception{
		Command command = AntExecutor.executeAnt(new File("d:\\apache-ant-1.6.5"), new File("build.xml"));
		System.out.println(command.getStd().toString());
		System.err.println(command.getStderr().toString());
		
	}
}
