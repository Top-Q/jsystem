/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import jsystem.extensions.report.html.CssUtils.CssType;
import jsystem.framework.report.Reporter;
import jsystem.utils.StringUtils;

public abstract class Report implements Serializable  {

	private static final long serialVersionUID = 1L;

	protected static Logger log = Logger.getLogger(HtmlTestList.class.getName());
	
	protected int changedStatus = Reporter.PASS;
	
	protected String title = null;
	
	protected String cssClass = "report";
	
	protected int isSuccess = Reporter.PASS;
	
	protected ArrayList<Report> parents;
	
	protected String fileName = null;

	protected String target = null;

	protected boolean ignore = false;

	protected boolean bold = false;

	protected boolean isHtmlTitle = false;

	protected boolean isHtmlMessage = false;

	protected String alt = null;

	protected String directory;

	protected String logDirectory;

	public boolean isLevel = false;
	
	public String time = "";
	
	public boolean isHtmlMessage() {
		return isHtmlMessage;
	}

	public void setHtmlMessage(boolean isHtmlMessage) {
		this.isHtmlMessage = isHtmlMessage;
	}
	
	// protected boolean statusChange = true;
	public Report(String title, int isSuccess, boolean bold) {
		this.title = title;
		this.isSuccess = isSuccess;
		this.bold = bold;
		parents = new ArrayList<Report>();
	}

	public Report() {
		parents = new ArrayList<Report>();
	}

	/**
	 * Write all report data to file
	 * 
	 * @param generator	generator for the file name
	 * @throws IOException
	 */
	public abstract void toFile(NameGenerator generator) throws IOException;

	/**
	 * Set the title of the report
	 * 
	 * @param title
	 *            report title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get the report title
	 * 
	 * @return report title
	 */
	public String getTitle() {
		String titleToReturn = title;
		if (!isHtmlMessage()){
			titleToReturn =  StringUtils.toHtmlString(title);
		}
		if (!StringUtils.isEmpty(time)){
			String start = CssType.TIME_STAMPS.getCssStart();
			String end = CssType.getCssClosingTag();
			titleToReturn = titleToReturn.replace(time, start + time + end);
		}
		return titleToReturn;
	}

	public void setSuccess(int isSuccess) {
		// I am already failed so my son doesn't matter
		if (this.isSuccess == Reporter.FAIL) {
			return;
		}
		//I am warning and my son is pass
		if (this.isSuccess == Reporter.WARNING && isSuccess == Reporter.PASS) {
			return;
		}
		this.isSuccess = isSuccess;
		if (isSuccess != Reporter.PASS) {			
			for (Report parent : parents){
				if (parent != null){
					parent.setSuccess(isSuccess);
				}
			}
		}

	}
	
	public int isSuccess() {
		return isSuccess;
	}

	/**
	 * returns the first parent in the ArrayList or null if no parent defined
	 * 
	 * @return
	 */
	public Report getParent() {
		if (parents.size() == 0){
			return null;
		}
		return parents.get(0);
	}

	/**
	 * set the given parent to be the first parent in the parents ArrayList (the old parent is first removed)
	 * 
	 * @param parent
	 */
	public void setParent(Report parent) {
		if (parents.size() == 0){
			parents.add(parent);
		}else{
			parents.remove(0);
			parents.add(0,parent);
		}
	}
	
	/**
	 * add a parent to the parents list
	 * 
	 * @param parent
	 */
	public void addParent(Report parent){
		parents.add(parent);
	}
	
	/**
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 */
	public String getCanonicalFileName() {
		if (fileName == null) {
			return null;
		}
		if (directory == null) {
			return fileName;
		}
		File f = new File(getLogDirectory() + File.separator + directory + File.separator + fileName);
		if (f.exists()) {
			return directory + File.separator + fileName;
		} else {
			return fileName;
		}
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		String color = "";
		String colorEnd = "";
		if (getCanonicalFileName() != null) {
			String t = "";
			if (alt != null) {
				t = " TITLE=\"" + StringUtils.stringToHTMLEscapedString(alt) + "\"";
			}
			buffer.append("<a");
			buffer.append(t);
			buffer.append(" href=\"");
			buffer.append(getHref());
			buffer.append("\" TARGET=\"");
			buffer.append(getTarget());
			buffer.append("\"");
			buffer.append("\">");
			buffer.append(getTitle());
			buffer.append("</a><br>\r\n");
		} else {
			buffer.append(color + getTitle() + colorEnd + "<br>\r\n");
		}
		
		if (!isHtmlMessage){
			buffer.insert(0, "<span class= \""+getCssClassCanonicalValue()+"\">");
			buffer.append("</span>");
		}
		
		if (isBold()){
			buffer.insert(0, "<br><b>");
			buffer.append("</b>");
		}

		return buffer.toString();
	}

	protected String getCssClassCanonicalValue() {
		String postFix = "_warn";
		if (isSuccess() == Reporter.FAIL) {
			postFix = "_erro";
		} else if (isSuccess() == Reporter.PASS) {
			postFix = "_pass";
		}
		return cssClass + postFix;
	}
	
	private String getHref() {
		Report parent = getParent();
		String parentDirectory = parent.getDirectory();
		String href;
		if (!StringUtils.isEmpty(parentDirectory)){
			if (parentDirectory.equals(getDirectory())){
				href = getFileName();
			}else {
				href = getCanonicalFileName();
			}
		}else {
			href = getCanonicalFileName();
		}
		href = href.replace('\\', '/').trim();
		if (href.startsWith("/")){			
			href =href.substring(1);			
		}
		if(getParent().getParent() != null && getParent().getParent().isLevel){
			File f = new File(new File(getReportFileCanonicalDirectory()).getParent(),fileName);
			if (f.exists()) {
				href = "../" + href;
			}
		}
		return href;
	}
	/**
	 * @return Returns the ignore.
	 */
	public boolean isIgnore() {
		return ignore;
	}

	/**
	 * @param ignore
	 *            The ignore to set.
	 */
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getDirectory() {
		return directory;
	}
	
	protected String getReportFileCanonicalDirectory() {
		String currenTestFolder;
		if (directory != null) {
			currenTestFolder = getLogDirectory() + File.separator + directory;
		}else{
			currenTestFolder = getLogDirectory();
		}				
		return currenTestFolder;
	}
	
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getLogDirectory() {
		return logDirectory;
	}

	public void setLogDirectory(String logDirectory) {
		this.logDirectory = logDirectory;
	}
	
	/**
	 * update parent colors and files
	 * 
	 * @param parent	the parent top update
	 * @param generator	the generator for the HTML file
	 * @throws IOException
	 */
	public void updateParent(Report parent,NameGenerator generator) throws IOException{
		if (parent!=null){
			parent.setChangedStatus(changedStatus);
			parent.toFile(generator);
		}
	}
	
	/**
	 * update all Report parents (Colors and files)
	 * 
	 * @param generator	the generator for the HTML file
	 * @throws IOException
	 */
	public void updateParents(NameGenerator generator) throws IOException{
		for (Report parent : parents){
			updateParent(parent, generator);
		}
	}
	
	/**
	 * the status that will be passed up to parents for changing the colors of levels
	 * 
	 * @param status	PASS\FAIL\WARNING
	 */
	public void setChangedStatus(int status){
		changedStatus = status;
	}

	/**
	 * get all parents list
	 * 
	 * @return
	 */
	public ArrayList<Report> getParents() {
		return parents;
	}

	public void setTime(String time) {
		this.time = time.trim();
	}
}
