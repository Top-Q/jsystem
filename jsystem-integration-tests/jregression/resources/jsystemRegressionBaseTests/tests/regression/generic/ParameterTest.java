package regression.generic;

import jsystem.framework.ParameterProperties;
import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class ParameterTest extends SystemTestCase {
	
	private enum MyEnum{
		v1,v2,v3;
	}
	public void testAllParameters(){
		report.step("count: " + count);
		report.step("cliCommand: " + cliCommand);
		report.step("numberOfPackets: " + numberOfPackets);
		report.step("rate: " + rate);
		report.step("tolerance: " + tolerance);
		report.step("value: " + enumValue.name());
		report.step("MyEnum: " + secondEnum.name());
	}

	@TestProperties(paramsInclude={"enumValue","cliCommand","selectedOption"})
	public void testAllParametersWithOrder(){
	}

	private Values enumValue = Values.VALUE1;
	private String selectedOption2;
	public String getSelectedOption2(){
		return selectedOption2;
	}
	public void setSelectedOption2(String option){
		selectedOption = option;
	}
	public String[] getSelectedOption2Options() {
		return new String[]{"1","2","3"};
	}

	private String selectedOption;
	public String getSelectedOption(){
		return selectedOption;
	}
	public void setSelectedOption(String option){
		selectedOption = option;
	}
	public String[] getSelectedOptionOptions() {
		return new String[]{"1","2","3"};
	}

	private int count = 0;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	String cliCommand = null;

	public String getCliCommand() {
		return cliCommand;
	}

	public void setCliCommand(String cliCommand) {
		this.cliCommand = cliCommand;
	}
	
	long numberOfPackets = 300;

	public long getNumberOfPackets() {
		return numberOfPackets;
	}

	public void setNumberOfPackets(long numberOfPackets) {
		this.numberOfPackets = numberOfPackets;
	}
	
	public long[] getNumberOfPacketsOptions(){
		return new long[]{300, 3000, 30000}; 
	}
	
	float rate = (float)0.5;

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}
	
	double tolerance = 0.1;

	public double getTolerance() {
		return tolerance;
	}

	@ParameterProperties(section="tolerance")
	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}
	public Values getEnumValue() {
		return enumValue;
	}
	public void setEnumValue(Values enumValue) {
		this.enumValue = enumValue;
	}

	private MyEnum secondEnum;
	
	public MyEnum getSecondEnum() {
		return secondEnum;
	}
	
	@ParameterProperties(section="SecondEnumSec")
	public void setSecondEnum(MyEnum enumValue) {
		this.secondEnum = enumValue;
	}

}
