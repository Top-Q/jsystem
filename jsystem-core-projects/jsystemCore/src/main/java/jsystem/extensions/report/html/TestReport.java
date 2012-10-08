/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import jsystem.utils.StringUtils;

/**
 *	Html base class for test reports and links
 */
public class TestReport extends Report {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3105616111390882858L;

	private Object message = null;

	private boolean isStatement = false;

	public TestReport(){
		super();
	}
	
	public TestReport(String title, String message, int isSuccess, boolean bold, boolean ignore) {
		super(title, isSuccess, bold);
		this.message = message;
		this.ignore = ignore;
		this.cssClass = "test_report";
	}

	public TestReport(String title, String message, int isSuccess, boolean bold, boolean ignore, String directory) {
		this(title,message,isSuccess,bold,ignore);
		this.directory = directory;		
	}
	
	public TestReport(String title, String message, int isSuccess, boolean bold, boolean ignore, String directory,String cssClass) {
		this(title,message,isSuccess,bold,ignore,directory);
		this.cssClass = cssClass;
	}
	
	public void toFile(NameGenerator generator) throws IOException {

		if (getMessage() != null) {
			StringBuffer toFile = new StringBuffer();
			if (String.valueOf(getMessage()).indexOf("!DOCTYPE HTML") >= 0 || isHtmlMessage()) {
				toFile.append(String.valueOf(getMessage()));
			} else { // Add css for pass\fail
				toFile.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
				toFile.append("<html><head>");
				toFile.append(CssUtils.cssPropertyToHtmlHeader(getDirectory()!=null));
				toFile.append("</head><body class=\"");
				toFile.append(getCssClassCanonicalValue());
				toFile.append("\"><FONT face=\"Courier New\" size=2>");
				toFile.append(StringUtils.toHtmlString(String.valueOf(getMessage())));
				toFile.append("</FONT></body></html>");
			}
			if (fileName == null) {
				fileName = generator.getName();
			}

			String baseDir;
			if (directory == null) {
				baseDir = logDirectory;
			} else {
				baseDir = logDirectory + File.separator + directory;
			}
			File listFile = new File(baseDir, fileName);
			listFile.getParentFile().mkdirs();

			if (!listFile.getParentFile().exists()) {
				log.log(Level.INFO, "Fail to create log directory: " + listFile.getParent());
			}

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(listFile),"UTF-8"));
			bw.write(toFile.toString().toCharArray());
			bw.flush();
			bw.close();
			bw = null;
		}

		setChangedStatus(isSuccess);
		
		updateParents(generator);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getMessage() {
		return message;
	}

	public boolean isStatement() {
		return isStatement;
	}

	public void setStatement(boolean statement) {
		isStatement = statement;
	}
}