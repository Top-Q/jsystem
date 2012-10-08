/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package tests.jsystem.framework.report;

import java.io.IOException;


import jsystem.extensions.analyzers.text.FindText;
import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

/**
 * 
 * test LevelHtmlTestReporter 
 * to use it change line in jsystem.properties file
 * from
 * 
 * reporter.classes=jsystem.extensions.report.html.HtmlTestReporter
 * 
 * to
 * 
 * reporter.classes=jsystem.extensions.report.html.LevelHtmlTestReporter
 * 
 * 
 */
public class LevelReporter extends SystemTestCase {

	public void test() throws IOException {

   
		  //report.report("fail007",false); 
        

				 report.startLevel("Level1", Reporter.MainFrame);
				
				 report.step("inside level1"); 
				 
				 report.stopLevel();
				 
		 
		 
		 report.report("out",false); 
		 
		 report.stopLevel();
		 
		 
		 
		 report.startLevel("Level2", Reporter.MainFrame);
			
		 report.step("inside level2"); 
		 
						     report.startLevel("Level3", Reporter.CurrentPlace);
							
						     report.step("inside level3"); 
						 
						     report.stopLevel();
						     
						     
						     report.report("fail",false); 
		 report.stopLevel();
		 
		 
	 
	}
	
	
	public void testStam1() throws IOException
	{

		 report.startLevel("Level1", Reporter.MainFrame);
		
		 report.step("inside level1"); 
		 
		 report.stopLevel();
		 

		 
		 report.report("out",false); 
		 
		 
		 report.startLevel("Level2", Reporter.MainFrame);
			
		 report.report("inside level2",false); 
		 
		 report.stopLevel();
	}
	public void test1() throws IOException
	{
		
		 report.startLevel("Level1", Reporter.CurrentPlace);
			
		 report.step("inside level1"); 
		 
		 report.stopLevel();  
		 
		 
		 report.startLevel("Level2", Reporter.CurrentPlace);
			
		 report.step("inside level2"); 
		 
		 report.stopLevel();
		 
		 report.startLevel("Level3", Reporter.CurrentPlace);
			
		 report.step("inside level3"); 
		 
		 report.stopLevel();
		 
		 
		 report.startLevel("Level4", Reporter.CurrentPlace);
			
		 report.step("inside level4"); 
		 
		 report.stopLevel();
		 for (int i = 0; i < 1000; i++) {
			 report.report("report "+i,true); 
		}
		 
		 report.startLevel("Level5", Reporter.CurrentPlace);
			
		 report.step("inside level5"); 
		 report.report("fail",false); 
		 report.stopLevel();
		 
		 report.report("fail",false); 
		 
		 
		
		
		
		
		
		
	}
	public void testStam() throws IOException{
		report.startLevel("first level", Reporter.MainFrame);
		report.startLevel("second level", Reporter.CurrentPlace);
		report.startLevel("third level", Reporter.CurrentPlace);
		String path = report.getCurrentTestFolder()+"\\";
		long time = System.currentTimeMillis();
		String fileName = "TestingLongBuffer_"+time+".txt";
		FileUtils.write(path+fileName, "Nizan Testing");
		report.step(fileName);
		report.addLink("Check This file out:",fileName);
	}
	public void testOne() throws IOException{
		report.startLevel("first level", Reporter.MainFrame);
		
		report.setFailToWarning(true);
        report.report("inside level4",false);
        report.setFailToWarning(false);
		
        
        report.stopLevel();
		 
	}
	public void test11() throws Exception {

	

		   MySystemObject mso = new MySystemObject();
		report.startLevel("l1", Reporter.CurrentPlace);
		     report.startLevel("l2", Reporter.CurrentPlace);
		    
		   
		    report.stopLevel();
		report.stopLevel();
		
		report.startLevel("l3", Reporter.CurrentPlace);
		report.stopLevel();
		
		
		report.startLevel("l4", Reporter.CurrentPlace);
		
		     // report.startLevel("l5", Reporter.CurrentPlace);
		      //report.report("fail",false);
		      mso.setTestAgainstObject(new String("fff"));
		      mso.analyze(new FindText("s"));
		     // report.stopLevel();
		//report.stopLevel();
		
		

		}
   public void testObject() throws IOException
   {
	   MySystemObject mso = new MySystemObject();

		
		report.startLevel("l3", Reporter.MainFrame);
		//report.stopLevel();
		
		
		report.startLevel("l4", Reporter.MainFrame);
		
		      //report.startLevel("l5", Reporter.c);
		      //report.report("fail",false);
		      mso.setTestAgainstObject(new String("fff"));
		      mso.analyze(new FindText("s"));
		    //  report.report("i fail",false);
		    //  report.stopLevel();
		//report.stopLevel();
		
		
//		  mso.setTestAgainsObject(new String("fff"));
//	      mso.analyze(new FindText("s"),false,false);
   }
   class MySystemObject extends SystemObjectImpl{
		
   }
}
