/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * Helper class which simplifies the work with the reporter.
 * @author goland
 */
public class ReporterHelper {
	
	private ReporterHelper(){
		// Static class
	}
	
	/**
	 * Creates a URL link from given file name
	 */
	public static String createLinkUrl(Reporter reporter, String fileName) {
		String linkUrl = null;
		if (isImage(fileName)) {
			linkUrl = fileName;
		} else {
			File currentTestFolder = new File(System.getProperty("user.dir"), reporter.getCurrentTestFolder());
			File logFolder = currentTestFolder.getParentFile();
			File linkFile = new File(currentTestFolder, fileName);

			/*
			 * Check if the file is in the current test folder, if in the
			 * current test folder change the link and add the folder to it.
			 */
			if (linkFile.exists()) {
				linkUrl = linkFile.getAbsolutePath().substring(logFolder.getAbsolutePath().length() + 1).replace('\\',
						'/');
			} else {
				linkUrl = fileName;
			}
		}
		return linkUrl;
	}

	/**
	 * Given a file <code>f</code> and link title, the
	 * method copies the file into the current test folder and
	 * adds a link to the file.
	 */
	public static String copyFileToReporterAndAddLink(Reporter reporter,File f,String title) throws Exception {
		String dest = copyFileToReporter(reporter, f);
		if (StringUtils.isEmpty(title)){
			title = f.getName();
		}
		reporter.addLink(title, f.getName());
		return dest;
	}

	/**
	 * Given a file <code>f</code>, property name and link title, the
	 * method copies the file into the current test folder and
	 * adds a property which value is a link the the given file.
	 */
	public static String copyFileToReporterAndAddLinkProperty(Reporter reporter,File f,String propertyName,String title) throws Exception {
		String dest = copyFileToReporter(reporter, f);
		if (StringUtils.isEmpty(title)){
			title = f.getName();
		}
		addPropertyLink(reporter, f.getName(), propertyName, title);
		return dest;
	}
	
	/**
	 * Given file name, property name and link title
	 * the method adds a property which value is a link the the given file.
	 * The method assumes file was copied into test's report folder
	 */
	public static void addLinkProperty(Reporter reporter,String url,String propertyName,String title) throws Exception {
		if (StringUtils.isEmpty(title)){
			title = new File(url).getName();
		}
		addPropertyLink(reporter, url, propertyName, title);
		
	}

	public static boolean isImage(String fileName) {
		String[] imagesTypes = { ".jpg", ".png", ".gif" };
		for (int i = 0; i < imagesTypes.length; i++) {
			if (String.valueOf(fileName).toLowerCase().endsWith(imagesTypes[i])) {
				return true;
			}
		}
		return false;
	}

	private static void addPropertyLink(Reporter reporter, String filePathInReporter, String propertyName, String title) {
		String fileUrl = createLinkUrl(reporter,filePathInReporter);
		String link = "<A href=\""+fileUrl +"\">"+title+"</A>";
		reporter.addProperty(propertyName, link);
	}
	
	private static String copyFileToReporter(Reporter reporter, File f) throws IOException {
		if (!f.exists()){
			throw new FileNotFoundException("File not found: " + f.getAbsolutePath());
		}
		File destination = new File(reporter.getCurrentTestFolder(),f.getName());
		FileUtils.copyFile(f, destination);
		return destination.getAbsolutePath();
	}
	
	/**
	 * add a link to an external file or to a web location
	 * 
	 * @param reporter	the Reporter to report with
	 * @param title	the link title
	 * @param url	the url to point to
	 */
	public static void addLinkToExternalLocation(Reporter reporter,String title, String url){
		if (url.startsWith("www.")){
			url = "http://"+url;
		}
		String link = "<a href=\"" + url  + "\">"+title + "</a>";
		reporter.report(link);
	}
	
	/**
	 * Reports all the report elements that were reported by the specified
	 * originator. This is usually useful when used with the <br>
	 * report.startBufferingReports(); <br>
	 * report.stopBufferingReports(); <br>
	 * calls <br>
	 * <br>
	 * Before calling this method, don't forget to call the stopBufferingReport;
	 * If not, you will not see the reports.
	 * <br>
	 * This method will also delete the report elements that were reported.
	 * 
	 * @param report
	 * @param originators
	 *            If null all the report elements will be reported
	 * 
	 * @author itaiag
	 */
	public static void reportByOriginator(Reporter report, final String[] originators) {
		List<ReportElement> reportElements = report.getReportsBuffer();
		if (null == reportElements) {
			return;
		}
		// Important: Do not change this to iterator. From some reason
		// iterator is not synchronized so you will get
		// ConcurrentModificationException
		for (int i = 0; i < reportElements.size(); i++) {
			final ReportElement element = reportElements.get(i);
			if (null == originators) {
				report.report(element);
			} else {
				for (String originator : originators) {
					if (element.getOriginator().equals(originator)) {
						report.report(element);
					}

				}
			}
		}
		deleteReportsByOriginator(report, originators);
	}

	/**
	 * Deletes all the report elements that were created by the specified
	 * <code>originators</code>
	 * 
	 * @param report
	 * @param originators
	 * 
	 * @author itaiag
	 */
	private static void deleteReportsByOriginator(Reporter report, final String[] originators) {
		if (originators == null || originators.length == 0) {
			throw new IllegalArgumentException("Originators can't be empty");
		}
		List<ReportElement> reportElements = report.getReportsBuffer();
		if (null == reportElements) {
			return;
		}
		for (int i = 0; i < reportElements.size(); i++) {
			final ReportElement element = reportElements.get(i);
			for (String originator : originators) {
				if (element.getOriginator().equals(originator)) {
					reportElements.remove(element);
					i--;
				}
			}

		}

	}
}
