/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.IOException;
import java.util.Stack;

import jsystem.framework.report.Reporter;
import jsystem.framework.report.TestInfo;
import jsystem.framework.report.Reporter.EnumReportLevel;
import junit.framework.Test;

/**
 * Class that allow to create report with level hierarchy In order to use-
 * change line in jsystem.properties
 * :reporter.classes=jsystem.extensions.report.html.HtmlTestReporter;jsystem.framework
 * to tohtl
 * reporter.classes=jsystem.extensions.report.html.LevelHtmlTestReporter;jsystem.framework
 * 
 * Sample: report.startLevel("Level",Reporter.MainFrame); or
 * report.startLevel("Level",Reporter.CurrentPlace);
 * 
 * @author arseniy
 */

public class LevelHtmlTestReporter extends HtmlTestReporter implements ExtendLevelTestReporter {
 
	private HtmlTestList	mainParent;
	/**
	 * stack of previous levels
	 */
	private boolean reportWithoutLevel=false;
	private Stack<ReporterLevelData> levelDataStack;
	
	public LevelHtmlTestReporter() throws Exception {
		super();

	}
	
	/**
	 * start new test init mainFrameFileName,stack of
	 * levels and stack of levels files names
	 */

	public void startTest(Test test) {
		mainParent = null;
		levelDataStack=null;
		reportWithoutLevel=false;
		super.startTest(test);
	}

	/**
	 * start new test init mainFrameFileName,stack of
	 * levels and stack of levels files names
	 */

	public void startTest(TestInfo testInfo) {
		mainParent = null;
		levelDataStack=null;
		reportWithoutLevel=false;
		super.startTest(testInfo);
	}

	public void report(String title, String message, int status, boolean bold, boolean html, boolean link) {
		super.report(title, message, status, bold, html, link);	
		if(reportWithoutLevel && mainParent!=null && mainParent.getParent()!=null){
			if((mainParent.isSuccess==Reporter.PASS &&status!=Reporter.PASS)||
				(mainParent.isSuccess==Reporter.WARNING &&status==Reporter.FAIL)){
				mainParent.isSuccess=status;
			}
		}
	}
	/* (non-Javadoc)
	 * @see jsystem.extensions.report.html.LevelHtmlReporter#startLevel(java.lang.String, jsystem.framework.report.Reporter.EnumReportLevel)
	 */
	@Override
	public void startLevel(String level, EnumReportLevel place) throws IOException {
		startLevel(level, place.value());
	}

	/* (non-Javadoc)
	 * @see jsystem.extensions.report.html.LevelHtmlReporter#startLevel(java.lang.String, int)
	 */
	@Override
	public void startLevel(String levelName, int place) throws IOException {
		if(reportWithoutLevel==true || levelDataStack == null){
			place=Reporter.MainFrame;
		}	
		reportWithoutLevel=false;
	
		/**
		 * if we now were main frame back to main frame
		 */
		if (place == Reporter.MainFrame ) {
			switchToMainFrame();
			levelDataStack = null;
		}
		/**
		 * init new level
		 */
		HtmlTestList level = new HtmlTestList(levelName, levelName + "\n\r", 0, true, false,"",HtmlTestList.TEST_LEVEL_CSS_CLASS);
		//        
		
		/**
		 * add it to HtmlWriter
		 */
		writer.addReport(level);
		
		/**
		 * remember place to return
		 */
		String fileName = level.getFileName();
	
		if (mainParent == null) {
			mainParent = writer.lastTestReportList;
	
		}
	
		if (levelDataStack == null ) {
			levelDataStack = new Stack<ReporterLevelData>();
		}
		
		ReporterLevelData previousLevel = new ReporterLevelData();
		previousLevel.testList = writer.lastTestReportList;
		previousLevel.levelName= levelName;
		previousLevel.fileName = writer.getCurrentMainFrameFileName();
		
		levelDataStack.push(previousLevel);
		
		writer.lastTestReportList.isLevel = true;
		HtmlTestList tmp = writer.lastTestReportList;
		HtmlTestList htl= writer.createNewLevelReportList(fileName);
		htl.setParent(tmp);
	
	}
	/* (non-Javadoc)
	 * @see jsystem.extensions.report.html.LevelHtmlReporter#stopLevel()
	 */
	@Override
	public void stopLevel() {
		if (levelDataStack == null){
			return;
		}		
		if(levelDataStack.empty())	{
			switchToMainFrame();
			reportWithoutLevel=true;
			return;
		}	
		ReporterLevelData previousLevel = levelDataStack.pop();
		writer.lastTestReportList = previousLevel.testList;
		writer.lastTestReportList.isLevel = false;
		writer.lastTestReportList.resetCurrentLevelLinkStatus();
	}

	/* (non-Javadoc)
	 * @see jsystem.extensions.report.html.LevelHtmlReporter#closeAllLevels()
	 */
	@Override
	public void closeAllLevels() {
		while (levelDataStack != null && !levelDataStack.isEmpty()) {
			stopLevel();
		}
	}
	
	/* (non-Javadoc)
	 * @see jsystem.extensions.report.html.LevelHtmlReporter#closeLevelsUpTo(java.lang.String, boolean)
	 */
	@Override
	public void closeLevelsUpTo(String levelName, boolean includeLevel){
		if (levelDataStack == null || levelDataStack.empty()){
			switchToMainFrame();
			return;
		}
		ReporterLevelData data = levelDataStack.peek();
		while(!levelDataStack.empty()&& !data.levelName.equals(levelName)){
			stopLevel();
		}
		if (includeLevel && data.levelName.equals(levelName)){
			stopLevel();
		}
	}
	
	/**
	 * back to main frame
	 * @return new HtmlTestList
	 */
	private void switchToMainFrame() {
		if (mainParent != null) {
			writer.lastTestReportList = mainParent;
			writer.lastTestReportList.isLevel = false;
		}
	}
}
class ReporterLevelData {
	String fileName;
	String levelName;
	HtmlTestList testList;
}