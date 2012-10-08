package regression.generic.ParametersHandling;

import jsystem.framework.RunProperties;
import junit.framework.SystemTestCase;

public class ParamaetersHandlingSection extends SystemTestCase{
	
	int integer1 = 1;
	int integer2 = 1;
	int integer3 = 1;
	int integer4 = 1;
	int integer5 = 1;
	int integer6 = 1;
	int integer7 = 1;
	int integer8 = 1;
	String str1 = "aa";
	String str2 = "aa";
	String str3 = "aa";
	String str4 = "aa";
	String str5 = "aa";
	String str6 = "aa";
	String str7 = "aa";
	String str8 = "aa";
	boolean bool1=true;
	boolean bool2=true;
	boolean bool3=true;
	boolean bool4=true;
	boolean bool5=true;
	boolean bool6=true;
	boolean bool7=true;
	boolean bool8=true;
	float flo1 = 1;
	float flo2 = 1;
	float flo3 = 1;
	float flo4 = 1;
	double dob1 = 1.0;
	double dob2 = 1.0;
	double dob3 = 1.0;
	double dob4 = 1.0;

	/**
	 * Test for testing params include with newline
	 *@params.include flo2 dob3 bool4 
	 *str8 integer7
	 */	
	public void testWithInclueParametersNewLine() throws Exception {
		report.report("This test has all class parameters");
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLine_flo2", ""+getFlo2());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLine_dob3", ""+getDob3());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLine_bool4", ""+isBool4());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLine_str8", ""+getStr8());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLine_integer7", ""+getInteger7());
	}

	/**
	 * Test for testing params include with newline
	 *@params.include flo2 dob3 bool4 
	 *str8 integer7
	 *@author goland
	 */	
	public void testWithInclueParametersNewLineAndDoclet() throws Exception {
		report.report("This test has all class parameters");
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLineAndDoclet_flo2", ""+getFlo2());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLineAndDoclet_dob3", ""+getDob3());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLineAndDoclet_bool4", ""+isBool4());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLineAndDoclet_str8", ""+getStr8());
		RunProperties.getInstance().setRunProperty("testWithInclueParametersNewLineAndDoclet_integer7", ""+getInteger7());

	}

	/**
	 * 
	 *@throws Exception
	 *@params.include flo2 dob3 bool4 str8 integer7
	 */	
	public void testWithInclueParameters() throws Exception {
		report.report("This test has all class parameters");
	}
	/**
	 * 
	 *@throws Exception
	 *@params.exclude flo2 dob3 bool4 str8 integer7
	 */	
	public void testWithExcludeParameters() throws Exception {
		report.report("This test has all class parameters");
	}

	//Settets & Getters

	public boolean isBool1() {
		return bool1;
	}

	public void setBool1(boolean bool1) {
		this.bool1 = bool1;
	}

	public boolean isBool2() {
		return bool2;
	}

	public void setBool2(boolean bool2) {
		this.bool2 = bool2;
	}

	public boolean isBool3() {
		return bool3;
	}

	public void setBool3(boolean bool3) {
		this.bool3 = bool3;
	}

	public boolean isBool4() {
		return bool4;
	}

	public void setBool4(boolean bool4) {
		this.bool4 = bool4;
	}

	public boolean isBool5() {
		return bool5;
	}

	public void setBool5(boolean bool5) {
		this.bool5 = bool5;
	}

	public boolean isBool6() {
		return bool6;
	}
	public void setBool6(boolean bool6) {
		this.bool6 = bool6;
	}

	public boolean isBool7() {
		return bool7;
	}

	public void setBool7(boolean bool7) {
		this.bool7 = bool7;
	}

	public boolean isBool8() {
		return bool8;
	}

	public void setBool8(boolean bool8) {
		this.bool8 = bool8;
	}

	public double getDob1() {
		return dob1;
	}

	public void setDob1(double dob1) {
		this.dob1 = dob1;
	}

	public double getDob2() {
		return dob2;
	}

	public void setDob2(double dob2) {
		this.dob2 = dob2;
	}

	public double getDob3() {
		return dob3;
	}

	public void setDob3(double dob3) {
		this.dob3 = dob3;
	}

	public double getDob4() {
		return dob4;
	}

	public void setDob4(double dob4) {
		this.dob4 = dob4;
	}

	public float getFlo1() {
		return flo1;
	}

	public void setFlo1(float flo1) {
		this.flo1 = flo1;
	}

	public float getFlo2() {
		return flo2;
	}

	public void setFlo2(float flo2) {
		this.flo2 = flo2;
	}

	public float getFlo3() {
		return flo3;
	}

	public void setFlo3(float flo3) {
		this.flo3 = flo3;
	}

	public float getFlo4() {
		return flo4;
	}
	public void setFlo4(float flo4) {
		this.flo4 = flo4;
	}

	

	public int getInteger1() {
		return integer1;
	}

	public void setInteger1(int integer1) {
		this.integer1 = integer1;
	}

	public int getInteger2() {
		return integer2;
	}

	public void setInteger2(int integer2) {
		this.integer2 = integer2;
	}

	public int getInteger3() {
		return integer3;
	}

	public void setInteger3(int integer3) {
		this.integer3 = integer3;
	}

	public int getInteger4() {
		return integer4;
	}

	public void setInteger4(int integer4) {
		this.integer4 = integer4;
	}

	public int getInteger5() {
		return integer5;
	}

	public void setInteger5(int integer5) {
		this.integer5 = integer5;
	}

	public int getInteger6() {
		return integer6;
	}

	public void setInteger6(int integer6) {
		this.integer6 = integer6;
	}

	public int getInteger7() {
		return integer7;
	}

	public void setInteger7(int integer7) {
		this.integer7 = integer7;
	}

	public int getInteger8() {
		return integer8;
	}

	public void setInteger8(int integer8) {
		this.integer8 = integer8;
	}

	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	public String getStr3() {
		return str3;
	}
	public void setStr3(String str3) {
		this.str3 = str3;
	}
	public String getStr4() {
		return str4;
	}
	public void setStr4(String str4) {
		this.str4 = str4;
	}
	public String getStr5() {
		return str5;
	}

	public void setStr5(String str5) {
		this.str5 = str5;
	}

	public String getStr6() {
		return str6;
	}
	public void setStr6(String str6) {
		this.str6 = str6;
	}

	public String getStr7() {
		return str7;
	}
	public void setStr7(String str7) {
		this.str7 = str7;
	}

	public String getStr8() {
		return str8;
	}

	public void setStr8(String str8) {
		this.str8 = str8;
	}



	
	


}
