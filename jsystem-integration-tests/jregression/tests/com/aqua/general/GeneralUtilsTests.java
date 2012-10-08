package com.aqua.general;

import jsystem.framework.report.Summary;
import jsystem.utils.ClassPathFile;
import junit.framework.SystemTestCase;

/**
 * @author goland
 */
public class GeneralUtilsTests extends SystemTestCase {

	private String jarFullPath;
	
	public GeneralUtilsTests() {
		super();
	}
	/**
	 */
	public void testAddJarVersionToSummaryReport() throws Exception{
		Summary.getInstance().setTempProperty(getJarFullPath(), new ClassPathFile().getJarVersionData(getJarFullPath()));
	}
	
	public String getJarFullPath() {
		return jarFullPath;
	}
	public void setJarFullPath(String jarFullPath) {
		this.jarFullPath = jarFullPath;
	}

}
