package regression.generic.ParametersHandling;

import jsystem.framework.ParameterProperties;
import junit.framework.SystemTestCase;
import jsystem.framework.ParameterProperties;

public class ParamaetersIncludeExclude extends SystemTestCase{
	
	int Int1 = 1;
	int Int2 = 1;
	int Int3 = 1;
	int Int4 = 1;
	int Int5 = 1;
	int Int6 = 1;
	int Int7 = 1;
	int Int8 = 1;
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

	
	public void testWithAllParametersSections() throws Exception {
		report.report("This test has all class parameters");
	}
	//Settets & Getters


	public boolean isBool1() {
		return bool1;
	}

	@ParameterProperties(section = "bool")
	public void setBool1(boolean bool1) {
		this.bool1 = bool1;
	}


	public boolean isBool2() {
		return bool2;
	}


	@ParameterProperties(section = "bool")
	public void setBool2(boolean bool2) {
		this.bool2 = bool2;
	}


	public boolean isBool3() {
		return bool3;
	}


	@ParameterProperties(section = "bool")
	public void setBool3(boolean bool3) {
		this.bool3 = bool3;
	}


	public boolean isBool4() {
		return bool4;
	}

	@ParameterProperties(section = "bool")
	public void setBool4(boolean bool4) {
		this.bool4 = bool4;
	}

	public boolean isBool5() {
		return bool5;
	}

	@ParameterProperties(section = "bool")
	public void setBool5(boolean bool5) {
		this.bool5 = bool5;
	}


	public boolean isBool6() {
		return bool6;
	}


	@ParameterProperties(section = "bool")
	public void setBool6(boolean bool6) {
		this.bool6 = bool6;
	}


	public boolean isBool7() {
		return bool7;
	}


	@ParameterProperties(section = "bool")
	public void setBool7(boolean bool7) {
		this.bool7 = bool7;
	}


	public boolean isBool8() {
		return bool8;
	}


	@ParameterProperties(section = "bool")
	public void setBool8(boolean bool8) {
		this.bool8 = bool8;
	}


	public double getDob1() {
		return dob1;
	}

	@ParameterProperties(section = "double")
	public void setDob1(double dob1) {
		this.dob1 = dob1;
	}


	public double getDob2() {
		return dob2;
	}


	@ParameterProperties(section = "double")
	public void setDob2(double dob2) {
		this.dob2 = dob2;
	}


	public double getDob3() {
		return dob3;
	}


	@ParameterProperties(section = "double")
	public void setDob3(double dob3) {
		this.dob3 = dob3;
	}


	public double getDob4() {
		return dob4;
	}


	@ParameterProperties(section = "double")
	public void setDob4(double dob4) {
		this.dob4 = dob4;
	}


	public float getFlo1() {
		return flo1;
	}


	@ParameterProperties(section = "float")
	public void setFlo1(float flo1) {
		this.flo1 = flo1;
	}


	public float getFlo2() {
		return flo2;
	}


	@ParameterProperties(section = "float")
	public void setFlo2(float flo2) {
		this.flo2 = flo2;
	}


	public float getFlo3() {
		return flo3;
	}

	@ParameterProperties(section = "float")
	public void setFlo3(float flo3) {
		this.flo3 = flo3;
	}


	public float getFlo4() {
		return flo4;
	}

	@ParameterProperties(section = "float")
	public void setFlo4(float flo4) {
		this.flo4 = flo4;
	}


	public int getInt1() {
		return Int1;
	}

	@ParameterProperties(section = "int")
	public void setInt1(int int1) {
		Int1 = int1;
	}


	public int getInt2() {
		return Int2;
	}

	@ParameterProperties(section = "int")
	public void setInt2(int int2) {
		Int2 = int2;
	}


	public int getInt3() {
		return Int3;
	}

	@ParameterProperties(section = "int")
	public void setInt3(int int3) {
		Int3 = int3;
	}


	public int getInt4() {
		return Int4;
	}

	@ParameterProperties(section = "int")
	public void setInt4(int int4) {
		Int4 = int4;
	}


	public int getInt5() {
		return Int5;
	}

	@ParameterProperties(section = "int")
	public void setInt5(int int5) {
		Int5 = int5;
	}


	public int getInt6() {
		return Int6;
	}

	@ParameterProperties(section = "int")
	public void setInt6(int int6) {
		Int6 = int6;
	}


	public int getInt7() {
		return Int7;
	}

	@ParameterProperties(section = "int")
	public void setInt7(int int7) {
		Int7 = int7;
	}


	public int getInt8() {
		return Int8;
	}

	@ParameterProperties(section = "int")
	public void setInt8(int int8) {
		Int8 = int8;
	}


	public String getStr1() {
		return str1;
	}

	@ParameterProperties(section = "string")
	public void setStr1(String str1) {
		this.str1 = str1;
	}


	public String getStr2() {
		return str2;
	}

	@ParameterProperties(section = "string")
	public void setStr2(String str2) {
		this.str2 = str2;
	}


	public String getStr3() {
		return str3;
	}

	@ParameterProperties(section = "string")
	public void setStr3(String str3) {
		this.str3 = str3;
	}


	public String getStr4() {
		return str4;
	}

	@ParameterProperties(section = "string")
	public void setStr4(String str4) {
		this.str4 = str4;
	}


	public String getStr5() {
		return str5;
	}

	@ParameterProperties(section = "string")
	public void setStr5(String str5) {
		this.str5 = str5;
	}


	public String getStr6() {
		return str6;
	}

	@ParameterProperties(section = "string")
	public void setStr6(String str6) {
		this.str6 = str6;
	}


	public String getStr7() {
		return str7;
	}

	@ParameterProperties(section = "string")
	public void setStr7(String str7) {
		this.str7 = str7;
	}


	public String getStr8() {
		return str8;
	}

	@ParameterProperties(section = "string")
	public void setStr8(String str8) {
		this.str8 = str8;
	}



	
	


}
