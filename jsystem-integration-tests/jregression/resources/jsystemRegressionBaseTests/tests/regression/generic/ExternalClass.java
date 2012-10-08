package regression.generic;

import java.io.File;

import junit.framework.SystemTestCase;

/**
 * Added this class to check runing tests that uses external classes
 * @author Yaron
 *
 */
public class ExternalClass  extends SystemTestCase {
	
	String a ="defult";
	String b ="defult";
	String c ="defult";
	String d ="defult";
	String e ="defult";
	String f ="defult";
	String g ="defult";
	String h ="defult";
	String i ="defult";
	String j ="defult";
	int Num2 = 5;
	int Num3 = 5;
	int Num4 = 5;
	int Num5 = 5;
	int Num6 = 5;
	int Num7 = 5;
	int Num8 = 5;
	char Ch='a';
	char Ch2='a';
	char Ch3='a';
	char Ch4='a';
	char Ch5='a';
	char Ch6='a';
	char Ch7='a';
	boolean Bl1 = true;
	boolean Bl2 = true;
	boolean Bl3 = true;
	boolean Bl4 = true;
	boolean Bl = true;
	boolean Bl6 = true;
	boolean Bl7 = true;
	float Faa = 56;
	float Fab = 56;
	float Fav = 56;
	float Fac = 56;
	float Fas = 56;
	float Far = 56;
	double Dub1 = 14565.676 ;
	double Dub2 = 14565.676 ;
	double Dub3 = 14565.676 ;
	double Dub4 = 14565.676 ;
	double Dub5 = 14565.676 ;
	double Dub6 = 14565.676 ;
	
	File file1 = null;
	File file2 = null;
	File file3 = null;
	File file4 = null;
	File file5 = null;
	File file6 = null;
	File file7 = null;
	File file8 = null;
	File file9 = null;
	File file10 = null;
	
	public String getA() {
		return a;
	}


	public void setA(String a) {
		this.a = a;
	}


	public String getB() {
		return b;
	}


	public void setB(String b) {
		this.b = b;
	}


	public boolean isBl() {
		return Bl;
	}


	public void setBl(boolean bl) {
		Bl = bl;
	}


	public boolean isBl1() {
		return Bl1;
	}


	public void setBl1(boolean bl1) {
		Bl1 = bl1;
	}


	public boolean isBl2() {
		return Bl2;
	}


	public void setBl2(boolean bl2) {
		Bl2 = bl2;
	}


	public boolean isBl3() {
		return Bl3;
	}


	public void setBl3(boolean bl3) {
		Bl3 = bl3;
	}


	public boolean isBl4() {
		return Bl4;
	}


	public void setBl4(boolean bl4) {
		Bl4 = bl4;
	}


	public boolean isBl6() {
		return Bl6;
	}


	public void setBl6(boolean bl6) {
		Bl6 = bl6;
	}


	public boolean isBl7() {
		return Bl7;
	}


	public void setBl7(boolean bl7) {
		Bl7 = bl7;
	}


	public String getC() {
		return c;
	}


	public void setC(String c) {
		this.c = c;
	}


	public char getCh() {
		return Ch;
	}


	public void setCh(char ch) {
		Ch = ch;
	}


	public char getCh2() {
		return Ch2;
	}


	public void setCh2(char ch2) {
		Ch2 = ch2;
	}


	public char getCh3() {
		return Ch3;
	}


	public void setCh3(char ch3) {
		Ch3 = ch3;
	}


	public char getCh4() {
		return Ch4;
	}


	public void setCh4(char ch4) {
		Ch4 = ch4;
	}


	public char getCh5() {
		return Ch5;
	}


	public void setCh5(char ch5) {
		Ch5 = ch5;
	}


	public char getCh6() {
		return Ch6;
	}


	public void setCh6(char ch6) {
		Ch6 = ch6;
	}


	public char getCh7() {
		return Ch7;
	}


	public void setCh7(char ch7) {
		Ch7 = ch7;
	}


	public String getD() {
		return d;
	}


	public void setD(String d) {
		this.d = d;
	}


	public String getE() {
		return e;
	}


	public void setE(String e) {
		this.e = e;
	}


	public String getF() {
		return f;
	}


	public void setF(String f) {
		this.f = f;
	}


	public String getG() {
		return g;
	}


	public void setG(String g) {
		this.g = g;
	}


	public String getH() {
		return h;
	}


	public void setH(String h) {
		this.h = h;
	}


	public String getI() {
		return i;
	}


	public void setI(String i) {
		this.i = i;
	}


	public String getJ() {
		return j;
	}


	public void setJ(String j) {
		this.j = j;
	}


	public int getNum2() {
		return Num2;
	}


	public void setNum2(int num2) {
		Num2 = num2;
	}


	public int getNum3() {
		return Num3;
	}


	public void setNum3(int num3) {
		Num3 = num3;
	}


	public int getNum4() {
		return Num4;
	}


	public void setNum4(int num4) {
		Num4 = num4;
	}


	public int getNum5() {
		return Num5;
	}


	public void setNum5(int num5) {
		Num5 = num5;
	}


	public int getNum6() {
		return Num6;
	}


	public void setNum6(int num6) {
		Num6 = num6;
	}


	public int getNum7() {
		return Num7;
	}


	public void setNum7(int num7) {
		Num7 = num7;
	}


	public int getNum8() {
		return Num8;
	}


	public void setNum8(int num8) {
		Num8 = num8;
	}


	public ExternalClass(){
		super();
	}

	
	public void testRunExternalClassTests() throws Exception{
		GenericBasic genBasicClass = new GenericBasic();
		FixtureTest fixTestClass = new FixtureTest();
		ParameterTest parmTestClass = new ParameterTest();
		
		
		genBasicClass.testReportFor10Sec();		
		genBasicClass.testShouldPass();
		genBasicClass.testThatRunFor10Sec();
		
		fixTestClass.testThatPass();
		
		parmTestClass.testAllParameters();
	}
	
	public void testRunExternalClassTests2() throws Exception{
		GenericBasic genBasicClass = new GenericBasic();
		FixtureTest fixTestClass = new FixtureTest();
		ParameterTest parmTestClass = new ParameterTest();
		
		
		genBasicClass.testReportFor10Sec();		
		genBasicClass.testShouldPass();
		genBasicClass.testThatRunFor10Sec();
		
		fixTestClass.testThatPass();
		
		parmTestClass.testAllParameters();

	}


	public double getDub1() {
		return Dub1;
	}


	public void setDub1(double dub1) {
		Dub1 = dub1;
	}


	public double getDub2() {
		return Dub2;
	}


	public void setDub2(double dub2) {
		Dub2 = dub2;
	}


	public double getDub3() {
		return Dub3;
	}


	public void setDub3(double dub3) {
		Dub3 = dub3;
	}


	public double getDub4() {
		return Dub4;
	}


	public void setDub4(double dub4) {
		Dub4 = dub4;
	}


	public double getDub5() {
		return Dub5;
	}


	public void setDub5(double dub5) {
		Dub5 = dub5;
	}


	public double getDub6() {
		return Dub6;
	}


	public void setDub6(double dub6) {
		Dub6 = dub6;
	}


	public float getFaa() {
		return Faa;
	}


	public void setFaa(float faa) {
		Faa = faa;
	}


	public float getFab() {
		return Fab;
	}


	public void setFab(float fab) {
		Fab = fab;
	}


	public float getFac() {
		return Fac;
	}


	public void setFac(float fac) {
		Fac = fac;
	}


	public float getFar() {
		return Far;
	}


	public void setFar(float far) {
		Far = far;
	}


	public float getFas() {
		return Fas;
	}


	public void setFas(float fas) {
		Fas = fas;
	}


	public float getFav() {
		return Fav;
	}


	public void setFav(float fav) {
		Fav = fav;
	}


	

	public File getFile1() {
		return file1;
	}


	public void setFile1(File file1) {
		this.file1 = file1;
	}


	public File getFile10() {
		return file10;
	}


	public void setFile10(File file10) {
		this.file10 = file10;
	}


	public File getFile2() {
		return file2;
	}


	public void setFile2(File file2) {
		this.file2 = file2;
	}


	public File getFile3() {
		return file3;
	}


	public void setFile3(File file3) {
		this.file3 = file3;
	}


	public File getFile4() {
		return file4;
	}


	public void setFile4(File file4) {
		this.file4 = file4;
	}


	public File getFile5() {
		return file5;
	}


	public void setFile5(File file5) {
		this.file5 = file5;
	}


	public File getFile6() {
		return file6;
	}


	public void setFile6(File file6) {
		this.file6 = file6;
	}


	public File getFile7() {
		return file7;
	}


	public void setFile7(File file7) {
		this.file7 = file7;
	}


	public File getFile8() {
		return file8;
	}


	public void setFile8(File file8) {
		this.file8 = file8;
	}


	public File getFile9() {
		return file9;
	}


	public void setFile9(File file9) {
		this.file9 = file9;
	}
	

}
