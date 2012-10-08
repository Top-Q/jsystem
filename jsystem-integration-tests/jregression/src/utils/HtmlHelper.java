package utils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.utils.FileUtils;

/**
 * helper for finding html elements
 * 
 * @author Nizan Freedman
 *
 */
public class HtmlHelper {

	/**
	 * finds the matching html file for a given test name
	 * 
	 * @param linkName	the Class.method name \ meaningful name
	 * @return	the full link path
	 * @throws IOException
	 */
	public static String findTestLinkFile(String linkName,String runnerLogFolder) throws IOException{
		String currentLogFolder = runnerLogFolder + 
	    File.separator+"current" + File.separator;
		String fileToSearchIn = currentLogFolder + "report3.html"; // report3 is the html report of "all tests"
		String content = FileUtils.read(fileToSearchIn);
		Pattern p = Pattern.compile("<a TITLE(\\s*.*)*</a>");
		Matcher match = p.matcher(content);
		String refString = "href=\"";
		while (match.find()){
			String s = match.group();
			if (s.contains(linkName)){
				int index = s.indexOf(refString);
				s = s.substring(index+refString.length());
				index = s.indexOf("\"");
				s = s.substring(0,index);
				return currentLogFolder + s;
			}
		}
		return null;
	}
	
}
