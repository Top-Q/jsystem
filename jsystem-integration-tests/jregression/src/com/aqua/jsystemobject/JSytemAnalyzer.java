
package com.aqua.jsystemobject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

import com.aqua.jsystemobject.JSystem.JSystemEvents;

public class JSytemAnalyzer extends AnalyzerParameterImpl {
	

	private JSystemEvents event;
	private Object[] parametrs;
	private String eventTitle;
    
	public JSytemAnalyzer(JSystemEvents event, Object[] parametrs) {
		this.event = event;
		this.parametrs = parametrs;
	}
	
	public JSytemAnalyzer(JSystemEvents event, Object[] parametrs,
			String eventTitle) {
		this.event = event;
		this.parametrs = parametrs;
		this.eventTitle = eventTitle;
	}

	public void analyze() {
		switch (event) {
		case AddChangeSutTestEvent:
			anlyzeChangeSutEvent();
			break;
		case SetTestParametr:
			analyzeSetTestParametr();
			break;
		case SetFreezeOnFail:
			analyzeSetFreezeOnFail();
			break;
		case ClearCurrentScenario:
			analyzeClearCurrentScenario();
			break;
		case CheckRepeatStatus:	
			analyzeCheckRepeatStatus();
			break;
		case AddTestEvent:	
			analyzeAddTestEvent();
		     break;
		case SelectScenarioEvent:	
			analyzeSelectScenarioEvent();
		     break;
		case DeleteEvent:	
			analyzeDeleteEvent();
		     break;
		case ChangeSutEvent:	
			 analyzeChangeSutEvent();
		     break;
		default:
			
			break;
		}

	}
	
	
     
	private void analyzeChangeSutEvent() {
		
		String  dir=(String)parametrs[0];
		String sut=(String)parametrs[1];
		dir=dir+"\\jsystem0.log";
        System.out.println("dir"+dir);
		FileInputStream fin;		
        String buffer = null;
        char letter;
		try
		{
		    // Open an input stream
		    fin = new FileInputStream (dir);
		    DataInputStream stream=new DataInputStream(fin);
		    while((letter=(char) stream.read())!=-1)
		    	buffer=buffer+letter;

		    // Close our input stream
		    fin.close();		
		    
		    System.out.println(buffer);
		   //; int index=buffer.lastIndexOf("")
		    
		}
		// Catches any error conditions
		catch (IOException e)
		{
			System.err.println ("Unable to read from file");
			System.exit(-1);
		}

	}

	private void analyzeDeleteEvent() {
		 String scenario=(String) (testAgainst);
		 Pattern p = Pattern.compile("<test.*>"+(String)parametrs[0]+"<.*");
		 Matcher m = p.matcher(scenario);
		 message=scenario+"\n*****************************************\n"+"Test "+parametrs[0]+" ";
		 if( !m.find())
		 {
			 status=true;
			 title = eventTitle + "  successful  ";
			 message=message+" not found";
		 }
		 else 
		 {
			 status=false;
			 title = eventTitle + "   fail";
			 message=message+"  found";
		 }
	}

	private void analyzeSelectScenarioEvent() {
		 String scenario=(String) (testAgainst);
		 String expectedScenario=(String)parametrs[0];
		 message=" Current scenario : "+scenario;
		 if(expectedScenario.equals(scenario))
		 {
			 status=true;
			 title = eventTitle + "  successful  ";
		 }
		 else  
		 {
			 status=false;
			 title = eventTitle + "  fail  ";
		 }
	}

	private void analyzeAddTestEvent() {
		 String scenario=(String) (testAgainst);
		 Pattern p = Pattern.compile("<test.*class=\""+(String)parametrs[0]+"\".*>"+(String)parametrs[1]+"<.*");
		 Matcher m = p.matcher(scenario);
		 message=scenario+"\n*****************************************\n"+(String)parametrs[0]+"."+parametrs[1];
		 if( m.find())
		 {
			 status=true;
			 title = eventTitle + " to scenario successful  ";
		 }
		 else
		 {
			 status=false;
			 title = eventTitle + " to scenario fail ";
		 }
	}

	private void analyzeCheckRepeatStatus() {
		boolean boolActual;
		boolean expected=(Boolean)parametrs[0];
		String strActual=(String) (testAgainst);
		if(strActual.equals("1"))boolActual=true;
		else boolActual=false;
		message =" repeat status is "+boolActual;
		if(expected==boolActual)
		{
			status=true;
			title = eventTitle + " successful .repeat set to "+boolActual;
		}
		else
		{
			status=false;
			title = eventTitle + " fail .repeat set to "+boolActual;
		}
		
	}

	private void analyzeClearCurrentScenario() {
		final String defaultScenario="<tests name=\"scenarios.default\"";
		String result=(String) (testAgainst);
		
		
		
		if(result.contains(defaultScenario))
		{
			status=true;
			title = eventTitle + " successful";
		}
		else
		{
			status=false;
			title = eventTitle + " fail";
		}
		message = (String) (testAgainst);
	}

	private void analyzeSetFreezeOnFail() {
	
		String frezeeOnFailStatus = (String) (testAgainst);
		if(frezeeOnFailStatus.equals("true"))
		{
			status=true;
			title = eventTitle + " successful . freeze.fail=true";
			
		}
		else
		{
			status=false;
			title = eventTitle + " fail";
		}
		message = title;
	}

	private void analyzeSetTestParametr() {
		String section = (String) parametrs[1];
		String name = (String) parametrs[2];
		String value = (String) parametrs[3];
		String expected = "Parametr Name : " + name + "\nParametr section : "
				+ section + "\nParametr value : " + value;

		String realValue = (String) (testAgainst);
		message = realValue;
		if (realValue.equals(expected))

		{
			status = true;
			title = eventTitle + " successful . ";

		}

		else {
			status = false;
			title = eventTitle + " fail ";

		}
	}
   /**
    * check before play
    *
    */
	private void anlyzeChangeSutEvent() {
		String sut = (String) parametrs[0];
		String scenario = ((String) testAgainst);
		title = eventTitle + "  : Change Sut Test ( " + sut + " )";
		if (scenario.contains("test Sut=\"" + sut + "\"")) {
			status = true;
			title = title + "was found";
		} else {
			status = false;
			title = title + "wasn't found";
		}
		message = scenario;
	}
	 /**
	    * check after play
	    *
	    */
	
}
