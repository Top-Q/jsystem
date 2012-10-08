package sanity;

import org.junit.Test;
//import com.aqua.testConstructs.StringBean;
//import testUtiels.StringBean;

import jsystem.extensions.analyzers.compare.NumberCompare;
import jsystem.extensions.analyzers.compare.NumberCompare.compareOption;
import jsystem.extensions.paramproviders.GenericObjectParameterProvider;
import jsystem.extensions.paramproviders.ObjectArrayParameterProvider;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Reporter;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;
import java.io.File;
import java.util.Date;

public class SimpleTests extends SystemTestCase4 {

	private String str;
	private int i;
	private File file;
	private Date date;
	private StringBean stringBean;
	private StringBean[] stringBeanArray;

	@Test
	@TestProperties(paramsInclude = { "" })
	public void testThatPass() {
		analyzer.setTestAgainstObject(1);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 1, 0));
	}

	@Test
	@TestProperties(paramsInclude = { "" })
	public void testThatFail() {
		analyzer.setTestAgainstObject(2);
		analyzer.analyze(new NumberCompare(compareOption.EQUAL, 1, 0));
	}

	@Test
	@TestProperties(paramsInclude = { "" })
	public void testThatWarns() {
		report.report("warning", Reporter.WARNING);
		
	}

	@Test
	@TestProperties(paramsInclude = { "str", "i", "file", "date" })
	public void testWithParameters() {
		report.report("str", str, true);
		report.report("i", Integer.toString(i), true);
		if (null != file){
			report.report("file", file.getName(), true);
		}
		if (null != date){
			report.report("date", Long.toString(date.getTime()), true);
		}
	}
	
	@Test
	@TestProperties(paramsInclude = { "str" })
	public void test1() {
		report.report("Do Nothing", str, true);
	}
	
	@Test
	@TestProperties(paramsInclude = { "stringBean" })
	public void userProviderTest() {
		report.report("Do Nothing", true);
	}
	
	@Test
	@TestProperties(paramsInclude = { "stringBeanArray" })
	public void arrayTest() {
		report.report("Do Nothing", true);
	}
	
	/**
	 * This is the documentation of test testWithDocumentations
	 */
	@Test
	@TestProperties(paramsInclude = {""})
	public void testWithDocumentation(){
		report.report("Test with documentations");
	}



	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public StringBean getStringBean() {
		return stringBean;
	}

	@UseProvider(provider = GenericObjectParameterProvider.class)
	public void setStringBean(StringBean stringBean) {
		this.stringBean = stringBean;
	}

	public StringBean[] getStringBeanArray() {
		return stringBeanArray;
	}
	@UseProvider(provider = ObjectArrayParameterProvider.class)
	public void setStringBeanArray(StringBean[] stringBeanArray) {
		this.stringBeanArray = stringBeanArray;
	}

}
