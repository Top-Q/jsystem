/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.reporter;

import java.io.File;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.Reporter;

/**
 * Utility for reporter.
 * 
 * @author optier
 *
 */
public class HtmlReporterUtils {
	
	protected static Logger log = Logger.getLogger(HtmlReporterUtils.class.getName());

	/**
	 * Get the current test file name for current test running.
	 * (The file name is main test HTML page, not the file names that 
	 * created by leveling methods like: report.startLevel) 
	 * 
	 * @return current test file name for current test running
	 */
	public static String getCurrentTestFileName() {
		
		//Get base log folder (log/current)
		String baseLogFolder = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER)
			+ File.separator + "current";
		
		String maxReportFileName = getHighestHtmlFile(baseLogFolder);
		
		return baseLogFolder+File.separator+maxReportFileName;
	}
	
	/**
	 * Get the latest report file in the current test folder
	 * 
	 * @param report	the Reporter to get the current test folder from
	 * @return	the last report file name
	 */
	public synchronized static String getLastReportFile(Reporter report){
		String dir = System.getProperty("user.dir") + File.separator + report.getCurrentTestFolder();
		return getHighestHtmlFile(dir);
	}
	
	private static String getHighestHtmlFile(String directory){
		File currDir = new File(directory); 
		
		//get all HTML files in directory with pattern: report\d+.html 
		File[] filesInCurrDirList = currDir.listFiles(new HTMLReportFilter());
		
		int maxReportFileNum = -1;
		String maxReportFileName = "Not Defined";
		//iterate all files in directory
		for (File file:filesInCurrDirList) {
			//regex to extract html file number
			Pattern p = Pattern.compile("report(\\d+).html");
			Matcher m = p.matcher(file.getName());
			//look for file pattern with: report\d+.html 
			if (!m.find()) {
				log.severe("Expect file name format to be: 'report(\\d+).html' but file name is "+file.getName());				
			} else {
				//get html file number
				int lastReportNum = Integer.parseInt(m.group(1));
				//keep the biggest html file number in directory
				if (maxReportFileNum < lastReportNum) {
					maxReportFileNum = lastReportNum;
					maxReportFileName = file.getName();
				}			
			}
		}
		//return html file name
		return maxReportFileName;
	}
	
}
