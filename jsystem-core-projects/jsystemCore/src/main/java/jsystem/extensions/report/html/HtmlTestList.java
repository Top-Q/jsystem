/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.Reporter;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * @author guy.arieli
 */
public class HtmlTestList extends TestReport {
	public static final String TEST_LEVEL_CSS_CLASS = "test_level";
	private static final long serialVersionUID = -2772861420986399577L;
	protected ArrayList<Report> reports = new ArrayList<Report>();
	private boolean fastList = true;
	private Report lastAddReport = null;
	private boolean statusChange = true;
	private int currentLevelLinkStatus = Reporter.PASS; 
	
	//private static final String COLOR_STRING = "COLOR=\"#";

	public HtmlTestList(String title, String message, int isSuccess, boolean bold, boolean ignore, String directory,String cssClass) {
		super(title,message,isSuccess,bold,ignore,directory,cssClass);
	}

	public HtmlTestList(String logDirectory, String directory,String cssClass) {
		super();
		this.directory = directory;
		this.logDirectory = logDirectory;
		this.cssClass = cssClass;
	}

	public void toFile(NameGenerator generator) throws IOException {
		if (fastList == true || "false".equals(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SAVE_REPORTERS_ON_RUN_END))){
			doToFile(generator);
		}
	}
	
	public void doToFile(NameGenerator generator) throws IOException {
		if (!statusChange) {
			return;
		}
		statusChange = false;
		if (isLevel){
			updateLastReportStyle1();
			updateParents(generator);
			return;
		}
		if (fileName == null) {
			fileName = generator.getName();
		}

		File listFile = new File(getReportFileCanonicalDirectory(), fileName);
		boolean writeHead = false;
		if (!listFile.exists() || listFile.length() == 0){
			writeHead = true;
		}
		StringBuffer buffer = new StringBuffer();		
		if (writeHead || !isFastList()) {
			buffer.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
			buffer.append("<HEAD>\n");
			buffer.append(CssUtils.cssPropertyToHtmlHeader(directory != null));								
			buffer.append("</HEAD>\n");
			if (getMessage()!=null){
				buffer.append(StringUtils.toHtmlString(String.valueOf(getMessage())));
			}
		}		
		if (fastList && lastAddReport != null) {
			buffer.append(lastAddReport.toString());
		}else {
			for (int i = 0; i < reports.size(); i++) {
				Report r = (Report) reports.get(i);
				buffer.append(r.toString());
			}
		}
		writeBufferToFile(buffer);
		updateParents(generator);
	}
	
	/**
	 * 
	 */
	protected void writeBufferToFile(StringBuffer buffer) throws IOException {
		File listFile = new File(getReportFileCanonicalDirectory(), fileName);
		listFile.getParentFile().mkdirs();
		if (!listFile.getParentFile().exists()) {
			log.log(Level.INFO, "Fail to create log directory: " + listFile.getParent());
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(listFile,fastList),"UTF-8"));
		bw.write("\n");
		bw.write(buffer.toString().toCharArray());
		bw.flush();
		bw.close();
		bw = null;
		buffer = null;
	}
	
	public boolean isFastList() {
		return fastList;
	}

	public void setFastList(boolean fastList) {
		this.fastList = fastList;
	}

	public void setSuccess(int success) {
		if (success != Reporter.PASS) {
			statusChange = true;
		}
		super.setSuccess(success);
	}

	public void addReport(Report report) {
		setStatusChange(true);
		report.addParent(this);
		if (fastList) {
			lastAddReport = report;
		} else {
			reports.add(report);
		}
		if (!report.isIgnore() && report.isSuccess() != Reporter.PASS) {
			setSuccess(report.isSuccess());
		}
	}
	
	public void removeReport(Report report) {
		report.addParent(null);
		reports.remove(report);
	}
	

	public void removeAllReports() {
		reports =   new ArrayList<Report>();
	}

	public boolean isStatusChange() {
		return statusChange;
	}

	public void setStatusChange(boolean statusChange) {
		this.statusChange = statusChange;
	}
	
	
	/**
	 * update last report color according to the new status
	 * 
	 * @throws IOException
	 */
	private void updateLastReportStyle1() throws IOException{
			if (changedStatus == Reporter.PASS){
				return;
			}
			if (getCurrentLevelLinkStatus() == Reporter.FAIL){
				return;
			}
			String css = getLevelSccForStatus(changedStatus);
			File reportFile = new File(getReportFileCanonicalDirectory(), fileName);
			int index = FileUtils.getLastLineWith(reportFile,"<span\\s+class=.*?>");
			FileUtils.replaceInFile(reportFile, "<span\\s+class=.*?>","<span class=\""+css+"\">",index);
			setCurrentLevelLinkStatus(changedStatus);
	}	
	
	private String getLevelSccForStatus(int status) {
		String postFix = "_warn";
		if (status == Reporter.FAIL) {
			postFix = "_erro";
		} else if (status == Reporter.PASS) {
			postFix = "_pass";
		}
		return TEST_LEVEL_CSS_CLASS + postFix;
	}
	
	void resetCurrentLevelLinkStatus(){
		currentLevelLinkStatus = Reporter.PASS;
	}
	
	private int getCurrentLevelLinkStatus() {
		return currentLevelLinkStatus;
	}

	private void setCurrentLevelLinkStatus(int currentLevelLinkStatus) {
		this.currentLevelLinkStatus = currentLevelLinkStatus;
	}
	
}