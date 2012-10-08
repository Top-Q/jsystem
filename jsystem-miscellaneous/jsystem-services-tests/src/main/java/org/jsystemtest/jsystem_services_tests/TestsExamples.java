package org.jsystemtest.jsystem_services_tests;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

public class TestsExamples extends SystemTestCase4 {

	// Test parameters can also have default values
	private File file = new File(".");
	private String str = "Some string";
	private int i = 5;
	private Date date = new Date();
	private String[] strArr;
	private Account account;
	private Account[] accountArr;

	/**
	 * Test with success report
	 */
	@Test
	@TestProperties(name = "Report Success", paramsInclude = { "" })
	public void reportSuccess() {
		report.report("Success");
	}

	/**
	 * Test with failure report
	 */
	@Test
	@TestProperties(name = "Report Failure", paramsInclude = { "" })
	public void reportFailure() {
		report.report("Failure", false);
	}

	/**
	 * Test with error report
	 * 
	 * @throws Exception
	 */
	@Test
	@TestProperties(name = "Report Error", paramsInclude = { "" })
	public void reportError() throws Exception {
		report.report("Error");
		throw new Exception("Error");
	}

	/**
	 * Test with warning report
	 */
	@Test
	@TestProperties(name = "Report Warning", paramsInclude = { "" })
	public void reportWarning() {
		report.report("Warning", Reporter.WARNING);
	}

	/**
	 * Test with different parameters
	 */
	@Test
	@TestProperties(name = "Test with file '${file}' string '${str}' integer ${i} date ${date}", paramsInclude = {
			"file", "str", "i", "date", "strArr" })
	public void testWithParameters() {
		report.report("File: " + file.getAbsolutePath());
		report.report("Date: " + date.toString());
		report.report("String: " + str);
		report.report("Integer: " + i);
	}

	/**
	 * Test with levels
	 * 
	 * @throws IOException
	 */
	@Test
	@TestProperties(name = "Report With Levels", paramsInclude = { "" })
	public void reportWithLevels() throws IOException {
		report.startLevel("Starting level", 2);
		try {
			report.report("Inside level");
			report.report("Inside level");
			report.startLevel("Starting level", 2);
			try {
				report.report("Inside level");
			} finally {
				report.stopLevel();
			}
			report.report("Inside level");
		} finally {
			// We would like it in a finally block in case an exception is
			// thrown before the stop level happens.
			report.stopLevel();
		}
	}

	/**
	 * Test with parameter provider
	 */
	@Test
	@TestProperties(name = "Test with parameter provider", paramsInclude = { "account", "accountArr" })
	public void testWithParameterProvider() {

	}

	public File getFile() {
		return file;
	}

	@ParameterProperties(description = "File Parameter")
	public void setFile(File file) {
		this.file = file;
	}

	public String getStr() {
		return str;
	}

	@ParameterProperties(description = "String Parameter")
	public void setStr(String str) {
		this.str = str;
	}

	public int getI() {
		return i;
	}

	@ParameterProperties(description = "Integer Parameter")
	public void setI(int i) {
		this.i = i;
	}

	public Date getDate() {
		return date;
	}

	@ParameterProperties(description = "Java Date Parameter")
	public void setDate(Date date) {
		this.date = date;
	}

	public String[] getStrArr() {
		return strArr;
	}

	@ParameterProperties(description = "String Array Parameter")
	public void setStrArr(String[] strArr) {
		this.strArr = strArr;
	}

	public Account getAccount() {
		return account;
	}

	@ParameterProperties(description = "Provider that exposes bean")
	@UseProvider(provider = jsystem.extensions.paramproviders.GenericObjectParameterProvider.class)
	public void setAccount(Account account) {
		this.account = account;
	}

	public Account[] getAccountArr() {
		return accountArr;
	}

	@ParameterProperties(description = "Provider that exposes bean array")
	@UseProvider(provider = jsystem.extensions.paramproviders.ObjectArrayParameterProvider.class)
	public void setAccountArr(Account[] accountArr) {
		this.accountArr = accountArr;
	}
	
}
