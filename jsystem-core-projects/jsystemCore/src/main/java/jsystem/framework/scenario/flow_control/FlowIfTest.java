/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario.flow_control;

import java.io.File;
import java.util.HashMap;

import jsystem.framework.ParameterProperties;
import jsystem.framework.scenario.Parameter;
import junit.framework.SystemTestCase;

/**
 * Flow control IF test to enable parameters handling
 * 
 * @author Nizan
 *
 */
public class FlowIfTest extends SystemTestCase{

	
	public static final String SEPARATOR = " <SEP> "; 
	/**
	 * Comparing options for conditions use
	 * 
	 * @author Nizan
	 *
	 */
	enum CompareOptions{
		MATHEMATICAL("math"),STRING("str");//,CUSTOM("custom");

		private String shortName;

		private CompareOptions(String shortName){
			this.shortName = shortName;
		}

		public String getShortName() {
			return shortName;
		}
	}

	/**
	 * Mathematical operators for comparison options
	 * 
	 * @author Nizan
	 *
	 */
	enum MathematicalOpertaor{
		EQUALS("="),GREATER(">"),LESS("<"),GREATER_OR_EQUAL(">="),LESS_OR_EQUAL("<="),NOT_EQUALS("!=");

		private String sign;

		private MathematicalOpertaor(String sign){
			this.sign = sign;
		}

		/**
		 * @return the matching String sign for given enum
		 */
		public String getSign() {
			return sign;
		}

		/**
		 * Match an enumerator by the sign String
		 * 
		 * @param withSign
		 * @return
		 */
		public static MathematicalOpertaor fromSign(String withSign){
			for (MathematicalOpertaor operator : MathematicalOpertaor.values()){
				if (withSign.equals(operator.getSign())){
					return operator;
				}
			}
			return null;
		}
	}

	/**
	 * Optional String comparators
	 * 
	 * @author Nizan
	 *
	 */
	enum StringOpertaor{
		EQUALS,CONTAINS,STARTS_WITH,ENDS_WITH,NOT_EQUALS
	}
	
	/**
	 * A sub-group of the allowed script languages for ant scriptCondition
	 * 
	 * @author Nizan
	 *
	 */
	enum ScriptLanguage{

		JAVASCRIPT("javascript"),
		GROOVY("groovy"),
		PYTHON("python"),
		PERL("perl"),
		TCL("tcl");

		private String value;

		private ScriptLanguage(String value){
			this.value = value;
		}

		public String getValue(){
			return value;
		}

		public static ScriptLanguage getScriptLanguageByValue(String value){
			for (ScriptLanguage language : ScriptLanguage.values()){
				if (language.getValue().equals(value)){
					return language;
				}
			}
			return null;
		}
	}

	CompareOptions compareOption = CompareOptions.STRING;
	String firstValue = "",secondValue="";
	MathematicalOpertaor mathematicalOperator = MathematicalOpertaor.EQUALS;
	StringOpertaor stringOperator = StringOpertaor.EQUALS;
	String parameters = "";
	File scriptFile = null;
	ScriptLanguage scriptLanguage = ScriptLanguage.JAVASCRIPT;
	boolean isCaseSensitive = true;
	
	public void handleUIEvent(HashMap<String,Parameter> map,String methodName) throws Exception {

		compareOption = CompareOptions.valueOf(map.get("CompareOption").getValue().toString());
		boolean showMath = false, showCustom=false, showString=false;

		switch (compareOption) {
		case MATHEMATICAL:
			showMath = true;
			break;
		case STRING:
			showString = true;
			break;
//		case CUSTOM:
//			showCustom = true;
//			break;
		}

		toggleCompareMathematical(map,showMath);
		toggleCustom(map,showCustom);
		toggleCompareString(map,showString);
	}

	private void toggleCompareMathematical(HashMap<String,Parameter> map,boolean show) {
		map.get("MathematicalOperator").setVisible(show);
	}

	private void toggleCompareString(HashMap<String,Parameter> map,boolean show) {
		map.get("StringOperator").setVisible(show);
		map.get("CaseSensitive").setVisible(show);
	}

	private void toggleCustom(HashMap<String,Parameter> map,boolean show) {
		map.get("Parameters").setVisible(show);
		map.get("ScriptFile").setVisible(show);
		map.get("ScriptLanguage").setVisible(show);

		map.get("FirstValue").setVisible(!show);
		map.get("SecondValue").setVisible(!show);
	}

	/**
	 * Will be presented in the comment field
	 * 
	 * @return
	 */
	public String getConditionString(){
		switch (getCompareOption()){
		case MATHEMATICAL:
			return getFirstValue() + " " + getMathematicalOperator().getSign() + " " + getSecondValue();
		case STRING:
			return "\"" + getFirstValue() + "\"" + " " + getStringOperator() + " " +  "\"" + getSecondValue() + "\"";
//		case CUSTOM:
//			String fileName = getScriptFile() == null? "" : getScriptFile().getName();
//			return "\"" + getParameters() + "\"" + " Using script: \"" + fileName + "\"";
		}
		return "Not defined";
	}

	/**
	 * support for old format if "equals" tag
	 * 
	 * @param arg1	first argument String
	 * @param arg2	second argument String
	 * @return	the new parameters String format
	 */
	public String getParametersStringForOldVersions(String arg1, String arg2){
		setFirstValue(arg1);
		setSecondValue(arg2);
		setCompareOption(CompareOptions.STRING);
		setStringOperator(StringOpertaor.EQUALS);
		return getParametersString();
	}

	/**
	 * Will be saved in Scenario file and passed on to the script
	 * 
	 * @return
	 */
	public String getParametersString(){
		String toReturn = "";
		switch (getCompareOption()){
		case MATHEMATICAL:
			toReturn = getFirstValue() + SEPARATOR + getMathematicalOperator().getSign() + SEPARATOR + getSecondValue();
			break;
		case STRING:
			toReturn = getFirstValue() + SEPARATOR + getStringOperator() + SEPARATOR + getSecondValue() +SEPARATOR + isCaseSensitive();
			break;
//		case CUSTOM:
//			return getParameters();
		}
		return  getCompareOption().getShortName() + SEPARATOR + toReturn;
	}

	/**
	 * Initialize test parameters from Scenario file params string
	 * 
	 * @param paramsString
	 * @param srcFile
	 */
	public void parseParamsString(String paramsString, String srcFile, String languageString){
		// Math
		if (paramsString.startsWith(CompareOptions.MATHEMATICAL.getShortName() + " ") ||
			paramsString.startsWith(CompareOptions.STRING.getShortName() + " ")){

			String[] vars;
			if (paramsString.contains(SEPARATOR)){
				vars = paramsString.split(SEPARATOR);
			}else{
				vars = paramsString.split(" ");
			}
			firstValue = vars.length > 1? vars[1] : "";
			secondValue = vars.length > 3? vars[3] : "";

			if (paramsString.startsWith(CompareOptions.MATHEMATICAL.getShortName() + " ")){
				compareOption = CompareOptions.MATHEMATICAL;
				mathematicalOperator = vars.length > 2? MathematicalOpertaor.fromSign(vars[2]) : null;
				if (mathematicalOperator == null){
					mathematicalOperator = MathematicalOpertaor.EQUALS;
				}
			}else{
				compareOption = CompareOptions.STRING;
				stringOperator = vars.length > 2? StringOpertaor.valueOf(vars[2]) : null;
				if (stringOperator == null){
					stringOperator = StringOpertaor.EQUALS;
				}
				setCaseSensitive(Boolean.getBoolean(vars[3]));
			}

			return;
		}

		// Custom
//		compareOption = CompareOptions.CUSTOM;
		parameters = paramsString;
		scriptFile = new File(srcFile);
		setScriptLanguage(ScriptLanguage.getScriptLanguageByValue(languageString));
	}

	public CompareOptions getCompareOption() {
		return compareOption;
	}

	@ParameterProperties(description="In what way should the values be compared?")
	public void setCompareOption(CompareOptions compareOption) {
		this.compareOption = compareOption;
	}

	public String getFirstValue() {
		return firstValue;
	}

	@ParameterProperties(description="First parameter value")
	public void setFirstValue(String firstValue) {
		this.firstValue = firstValue;
	}
	public String getSecondValue() {
		return secondValue;
	}

	@ParameterProperties(description="Second parameter value")
	public void setSecondValue(String secondValue) {
		this.secondValue = secondValue;
	}

	public MathematicalOpertaor getMathematicalOperator() {
		return mathematicalOperator;
	}

	@ParameterProperties(description="Which mathematical comparison to use?")
	public void setMathematicalOperator(MathematicalOpertaor operator) {
		this.mathematicalOperator = operator;
	}

	public String getParameters() {
		return parameters;
	}

	@ParameterProperties(description="String that will be passed to the script")
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public File getScriptFile() {
		return scriptFile;
	}

	@ParameterProperties(description="Condition script to execute")
	public void setScriptFile(File scriptFile) {
		if (scriptFile.isDirectory()){
			this.scriptFile = null;
		}else{
			this.scriptFile = scriptFile;
		}

	}

	public StringOpertaor getStringOperator() {
		return stringOperator;
	}

	@ParameterProperties(description="Which string comparison to use?")
	public void setStringOperator(StringOpertaor stringOperator) {
		this.stringOperator = stringOperator;
	}

	public ScriptLanguage getScriptLanguage() {
		return scriptLanguage;
	}

	@ParameterProperties(description="The language of the chosen script")
	public void setScriptLanguage(ScriptLanguage scriptLanguage) {
		if (scriptLanguage == null){
			scriptLanguage = ScriptLanguage.JAVASCRIPT;
		}
		this.scriptLanguage = scriptLanguage;
	}
	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}

	@ParameterProperties(description="Should comparison be case sensitive?")
	public void setCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive;
	}
	
}
