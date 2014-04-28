package tests.examples;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

public class TestsExamples extends SystemTestCase4 {

	public enum HarryPotterBook {
		Philosophers_Stone, Chamber_of_Secrets, Prisoner_of_Azkaban, Goblet_of_Fire, Order_of_the_Phoenix, Half_Blood_Prince, Deathly_Hallows
	}

	// Test parameters can also have default values
	private File file = new File(".");
	private String str = "Some string";
	private int i = 5;
	private Date date = new Date();
	private String[] strArr;
	private Account account;
	private Account[] accountArr;
	private HarryPotterBook book = HarryPotterBook.Chamber_of_Secrets;
	private String[] books;
	// Parameters in sections. See the @ParameterProperties of each of the
	// parameters setters
	private int intInSection9;
	private String strInSection9;
	private File fileInSection9;

	// Parameters for showing how to control parameter properties - see
	// handleUiEvent method
	private boolean setEditable;
	private boolean setVisible;
	private String canYouSeeMe;
	private String canYouEditMe;

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
			"file", "str", "i", "date", "strArr", "book", "intInSection9", "strInSection9", "fileInSection9","books" })
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

	/**
	 * Use JSystem handleUiEvent method to control the <b>visibility</b> and
	 * <b>editability</b> of parameters. To see the effect, change the values of
	 * the setEditable and setVisible parameters.
	 * 
	 */
	@Test
	@TestProperties(name = "Control different parametrs attributes", paramsInclude = { "setEditable", "setVisible",
			"canYouSeeMe", "canYouEditMe" })
	public void controlParametersAttributes() {
		report.report("Using handle UI event for controlling parameters attibutes");
	}

	@Override
	public void handleUIEvent(HashMap<String, Parameter> map, String methodName) throws Exception {
		// Making sure that the event is only for the correct building block
		if (methodName.equals("controlParametersAttributes")) {
			Parameter setEditableParam = map.get("SetEditable");
			if (setEditableParam.getValue().toString().equals("true")) {
				map.get("CanYouEditMe").setEditable(true);
			} else {
				map.get("CanYouEditMe").setEditable(false);
			}
			Parameter setVisibleParam = map.get("SetVisible");
			if (setVisibleParam.getValue().toString().equals("true")) {
				map.get("CanYouSeeMe").setVisible(true);
			} else {
				map.get("CanYouSeeMe").setVisible(false);
			}

		}
	}

	public boolean isSetEditable() {
		return setEditable;
	}

	public void setSetEditable(boolean setEditable) {
		this.setEditable = setEditable;
	}

	public boolean isSetVisible() {
		return setVisible;
	}

	public void setSetVisible(boolean setVisible) {
		this.setVisible = setVisible;
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

	public HarryPotterBook getBook() {
		return book;
	}

	@ParameterProperties(description = "Harry Potter Book")
	public void setBook(HarryPotterBook book) {
		this.book = book;
	}

	public String getCanYouSeeMe() {
		return canYouSeeMe;
	}

	public void setCanYouSeeMe(String canYouSeeMe) {
		this.canYouSeeMe = canYouSeeMe;
	}

	public String getCanYouEditMe() {
		return canYouEditMe;
	}

	public void setCanYouEditMe(String canYouEditMe) {
		this.canYouEditMe = canYouEditMe;
	}

	public int getIntInSection9() {
		return intInSection9;
	}

	@ParameterProperties(section = "Section9")
	public void setIntInSection9(int intInSection9) {
		this.intInSection9 = intInSection9;
	}

	public String getStrInSection9() {
		return strInSection9;
	}

	@ParameterProperties(section = "Section9")
	public void setStrInSection9(String strInSection9) {
		this.strInSection9 = strInSection9;
	}

	public File getFileInSection9() {
		return fileInSection9;
	}

	@ParameterProperties(section = "Section9")
	public void setFileInSection9(File fileInSection9) {
		this.fileInSection9 = fileInSection9;
	}

	/**
	 * @see getBooksOptions
	 * @return
	 */
	public String[] getBooks() {
		return books;
	}

	@ParameterProperties(description = "Multiple selection example")
	public void setBooks(String[] books) {
		this.books = books;
	}

	public String[] getBooksOptions() {
		return new String[] { HarryPotterBook.Chamber_of_Secrets.name(), HarryPotterBook.Deathly_Hallows.name(),
				HarryPotterBook.Half_Blood_Prince.name(), HarryPotterBook.Goblet_of_Fire.name(),
				HarryPotterBook.Order_of_the_Phoenix.name(), HarryPotterBook.Philosophers_Stone.name(),
				HarryPotterBook.Prisoner_of_Azkaban.name() };
	}

}
