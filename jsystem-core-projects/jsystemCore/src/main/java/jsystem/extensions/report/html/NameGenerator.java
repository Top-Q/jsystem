/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.common.CommonResources;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

public class NameGenerator implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4918047399994880310L;
	
	private static Logger log = Logger.getLogger(NameGenerator.class.getName());
	
	int i = 1;

	private String lastName;
	
	/**
	 * get the name of the next report HTML file (by increasing the serial number)
	 * @return
	 */
	public synchronized String getName() {
		StringBuffer buf = new StringBuffer(16);
		buf.append("report");
		buf.append(i);
		buf.append(".html");
		i++;
		lastName =  buf.toString();
		
		updateLastReport(lastName);
		
		return lastName;
	}
	
	/**
	 * Update a file which holds current test directory name
	 * 
	 * @param directory	the current test directory name
	 */
	private void updateLastReport(String lastReportName){
		
		if (!StringUtils.isEmpty(lastReportName)){
			try {
				FileUtils.addPropertyToFile(CommonResources.TEST_INNER_TEMP_FILENAME, CommonResources.LAST_REPORT_NAME, lastReportName);
			}catch (Exception e) {
				log.log(Level.WARNING,"Failed updating last report",e);
			}
		}		
	}

	public void setIndex(int index) {
		i = index;
	}

	public int getIndex() {
		return i;
	}



	public String getLastName() {
		return lastName;
	}
}
