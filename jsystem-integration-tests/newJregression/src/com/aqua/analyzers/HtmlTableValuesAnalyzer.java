package com.aqua.analyzers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * this class will take a key value pair made of String and String[]
 * and will analyze and determine if the given keys have the expected values related 
 * to them in the tables inside the given html file.
 * @author Dan Hirsch
 *
 */
public class HtmlTableValuesAnalyzer extends AnalyzerParameterImpl {

	String htmlAsText;
	Map<String, String[]> columnsNvalues;
	public HtmlTableValuesAnalyzer() {
		// TODO Auto-generated constructor stub
	}
	
	
	public HtmlTableValuesAnalyzer(String htmlAsTest){
		this.htmlAsText = htmlAsTest;
	}
	
	/**
	 * checks that key columns have the correct value columns in the 
	 * given html file.
	 */
	@Override
	public void analyze() {
		this.columnsNvalues = (Map<String, String[]>)testAgainst;
		Pattern pattern = Pattern.compile("<td>.*</td>");
		Matcher matcher = pattern.matcher(htmlAsText);

		String columnName;
		int i = 0;
		String value = "";
		//values in expected value list must be in order
		while(matcher.find() && i < columnsNvalues.size()){
			//check if this match key is one we are looking for in the test.
			if(columnsNvalues.containsKey(columnName = cutOffColumnEdges(matcher.group(0)))){
				//if found your key, check that the value in the report is the one you
				//expect to find.
				for(int j = 0; j < columnsNvalues.get(columnName).length; j++){
					matcher.find();
					value = cutOffColumnEdges(matcher.group(0));
					if(columnsNvalues.get(columnName)[j].equals(value)){
						continue;
					}
					else{
						status = false;
						message += "value in column "+columnName+"is not equal to expected value "+value+"\n";
						return;
					}
				}
				status = true;
				message += "value in column "+columnName+" equals to the expected value "+value+"\n";
				i++;
			}
		}
	}
	private String cutOffColumnEdges(String str){
		str = str.replaceAll("<td>", "");
		str = str.replaceAll("</td>", "");
		return str;
	}
}
