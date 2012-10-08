package stability;

import org.junit.Test;

import jsystem.framework.report.Reporter;
import junit.framework.Assert;
import junit.framework.SystemTestCase4;

public class ReporterStabilityTest extends SystemTestCase4 {
	

	private int num = 3456;
	private String str = "test";
	private long longNum = 45678;
	private double doubleNum = 765.398;
	private char ch = 'V';
	private String[] stringArr = {"jsystem","ignis","success"};
	private Enumerate enumerate;
	
	public ReporterStabilityTest() throws Exception{
		super();
	}
	
	@Test
	public void heavyRportingTest() throws Exception{
		for(int i = 0; i < 200; i++){
			report.report("testing heavy reporting");
		}
	}
	
	@Test
	public void heavyReportingWarningTest() throws Exception{
		for(int i = 0; i < 200; i++){
			report.report("testing heavy reporting in warning",Reporter.WARNING);
		}
	}
	
	@Test
	public void heavyReportingErroredTest() throws Exception{
		for(int i = 0; i < 200; i++){
			Assert.assertEquals(0, 1);
		}
	}
	
	@Test
	public void heavyReportingExceptionTest() throws Exception{
		throw new Exception("heavyReportingExceptionTest");
	}
	
	@Test
	public void reportParameters() throws Exception{
		for(int i = 0; i < 200 ; i++){
			report.report("reporting parameters values: \n");
			report.report("num = "+num);
			report.report("str = "+str);
			report.report("longNum = "+longNum);
			report.report("doubleNum = "+doubleNum);
			report.report("ch = "+ch);
			report.report("num = "+num);
//			report.report("stringArr = "+stringArr);
			report.report("enumerate.INT = "+enumerate.INT);
			report.report("enumerate.DOUBLE= "+enumerate.DOUBLE);
			report.report("enumerate.LONG = "+enumerate.LONG);
			report.report("enumerate.STRING = "+enumerate.STRING);
			report.report("enumerate.CHAR = "+enumerate.CHAR);
			
		}
	}
	
	//-----------------------------------------------------------------
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public long getLongNum() {
		return longNum;
	}

	public void setLongNum(long longNum) {
		this.longNum = longNum;
	}

	public double getDoubleNum() {
		return doubleNum;
	}

	public void setDoubleNum(double doubleNum) {
		this.doubleNum = doubleNum;
	}

	public char getCh() {
		return ch;
	}

	public void setCh(char ch) {
		this.ch = ch;
	}

	public String[] getStringArr() {
		return stringArr;
	}

	public void setStringArr(String[] stringArr) {
		this.stringArr = stringArr;
	}

	public enum Enumerate{
		INT("integer"),
		CHAR("char"),
		STRING("string"),
		LONG("long"),
		DOUBLE("double");
		
		private String value;
		private Enumerate(String str){
			this.value = str;
		}
		
		public String toString(){
			return this.value;
		}
	}

}
