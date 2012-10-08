/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.util.regex.Pattern;

import junit.framework.SystemTestCase4;

import org.junit.Assert;
import org.junit.Test;

public class FileUtilsTests extends SystemTestCase4 {

	@Test
	public void checkFileReplace() throws Exception {
		File f = new File("report8.html");
		int index = FileUtils.getLastLineWith(f,"<span\\s+class=.*?>");
		FileUtils.replaceInFile(f, "<span\\s+class=.*?>","<span class=\"gogol555llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll\">",index);
	}
	
	@Test
	public void tryPattern() throws Exception {
		String regexp = "<span\\s+class=.*?>";
		Pattern p = Pattern.compile(regexp);
		String s = "</span><br><b><span class=\"test_level_pass\"><a href=\"file:///D:/jsystem/runner/log/current/test_1/report10.html\" target=\"testFrame\" onclick=\"for (i=0; i<document.links.length; i++){document.links[i].style.background='white'}; this.style.background='lavender';\"><font color=\"#ff0000\">Level&nbsp;two&nbsp;checking&nbsp;it</font></a><br>";
		boolean matches = Pattern.matches(regexp,s);
		matches = Pattern.compile(regexp).matcher(s).matches();
		matches = p.matcher(s).find();
		Assert.assertTrue(matches);
	}
}
