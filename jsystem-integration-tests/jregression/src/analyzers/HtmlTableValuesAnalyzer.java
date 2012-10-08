package analyzers;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

public class HtmlTableValuesAnalyzer extends AnalyzerParameterImpl {

	String htmlAsText;
	Map<String, String> columnsNvalues;
	public HtmlTableValuesAnalyzer() {
		// TODO Auto-generated constructor stub
	}
	
	
	public HtmlTableValuesAnalyzer(String htmlAsTest){
		this.htmlAsText = htmlAsTest;
	}
	
	@Override
	public void analyze() {
		this.columnsNvalues = (Map<String, String>)testAgainst;
		Pattern pattern = Pattern.compile("<td>.*</td>");
		Matcher matcher = pattern.matcher(htmlAsText);

		String columnName;
		int i = 0;

		//values in expected value list must be in order
		while(matcher.find() && i < columnsNvalues.size()){
			if(columnsNvalues.containsKey(columnName = cutOffColumnEdges(matcher.group(0)))){
				matcher.find();
				String value = cutOffColumnEdges(matcher.group(0));
				if(columnsNvalues.get(columnName).equals(value)){
					status = true;
					message += "value in column "+columnName+" equals to the expected "+value+"\n";
					i++;
					continue;
				}
				else{
					status = false;
					message += "value int column "+columnName+"is not equal to expected "+value+"\n";
					return;
				}
			}
			break;
		}
	}
	private String cutOffColumnEdges(String str){
		str = str.replaceAll("<td>", "");
		str = str.replaceAll("</td>", "");
		return str;
	}
}