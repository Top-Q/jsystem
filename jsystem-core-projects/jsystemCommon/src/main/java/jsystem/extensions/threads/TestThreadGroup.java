/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.ReportElement;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.EnumReportLevel;
import junit.framework.AssertionFailedError;
import junit.framework.SystemTestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;

/**
 * This class manages a group of tests that will be executed in parallel.
 */
public class TestThreadGroup {
	private String groupName;
	private HashMap<String, ThreadedTest> threadedTests = new HashMap<String, ThreadedTest>();
	TestThreadGroup(String groupName) {
		this.groupName = groupName;
	}
	
	/**
	 * Add a new test to the group. 
	 * @param test - test to add.
	 */
	public void addTest(SystemTestCase test){
		FixtureManager fm = FixtureManager.getInstance();
		// set current fixture
		test.setFixture(fm.getFixture(fm.getCurrentFixture()).getClass());
		ThreadedTest threadedTest = new ThreadedTest(test);
		if (!test.getTestDocumentation().isEmpty()){
			threadedTests.put(test.getTestDocumentation(), threadedTest);
		} else {
			threadedTests.put(test.getName(), threadedTest);
		}
	}
	
	/**
	 * Start the execution of all the tests that were added.
	 */
	public void start(){
		ListenerstManager.getInstance().startBufferingReports(true);
		for (String threadName : threadedTests.keySet()) {
			threadedTests.get(threadName).setName(threadName);
			
			if (TestThreadFactory.getInstance().isRunInParallel()){				
				threadedTests.get(threadName).start();
			} 
			else {
				threadedTests.get(threadName).run();
			}
		}

	}
	
	/**
	 * Join all the executions.
	 * @param timeout - the time to wait in milliseconds.
	 * @throws Exception
	 */
	public void join(long timeout) throws Exception{
		ListenerstManager.getInstance().report("Joining " + threadedTests.size()+ " running tests");
		for (String threadName : threadedTests.keySet()) {
			threadedTests.get(threadName).join(timeout);
		}
		
		ListenerstManager.getInstance().report("Finished joining " + threadedTests.size() + " running tests");
		
		for (String threadName : threadedTests.keySet()) {
			if(threadedTests.get(threadName).isAlive()){
				ListenerstManager.getInstance().report("Thread name '" + threadName + "' was still alive after timeout - interrupting thread", false);
				threadedTests.get(threadName).interrupt();
			}
		}
		ListenerstManager.getInstance().stopBufferingReports();
		List<ReportElement> list = ListenerstManager.getInstance().getReportsBuffer();
		if (list == null){
			return;
		}
		ListenerstManager.getInstance().clearReportsBuffer();
		HashMap<String, List<ReportElement>> reportsGroup = new HashMap<String, List<ReportElement>>();
		//sort reports according to the threaded tests
		for(ReportElement el: list){
			if(!reportsGroup.containsKey(el.getOriginator())){
				ArrayList<ReportElement> group = new ArrayList<ReportElement>();
				reportsGroup.put(el.getOriginator(), group);
			}
			reportsGroup.get(el.getOriginator()).add(el);
		}
		
		if (TestThreadFactory.getInstance().isRunInParallel()){
			for (String threadName : threadedTests.keySet()) {
				ListenerstManager.getInstance().startLevel(threadName, EnumReportLevel.CurrentPlace);
				//print buffered reports under correct level
				for(ReportElement el: reportsGroup.get(threadName)){		
					//set threaded test fail status
					if (el.getStatus() == Reporter.FAIL){
						threadedTests.get(threadName).setPass(false);
						// if report fail was found
						if (threadedTests.get(threadName).getTest().getTestResult().wasSuccessful()){
							threadedTests.get(threadName).setFailReports(true);
						}
						// if exception fail was found
						else {							
							continue;
						}
					}
					ListenerstManager.getInstance().report(el);
				}

				if (!threadedTests.get(threadName).isPass()){
					if (threadedTests.get(threadName).isFailReports()){
						threadedTests.get(threadName).setPass(false);
						//print exception fail report, if exist
						threadedTests.get(threadName).getTest().setPass(false);
						threadedTests.get(threadName).getTest().getTestResult().addFailure(threadedTests.get(threadName).getTest(),
																							   new AssertionFailedError("Fail report was submitted"));			
						threadedTests.get(threadName).setThrown(threadedTests.get(threadName).getTest().getTestResult().failures().nextElement().thrownException());			
						ListenerstManager.getInstance().addFailure(threadedTests.get(threadName).getTest(), (AssertionFailedError)threadedTests.get(threadName).getThrown());
					}
					else if (threadedTests.get(threadName).getTest().getTestResult().errorCount() > 0) {
						//print exception message, if thrown
						threadedTests.get(threadName).getTest().setPass(false);
						threadedTests.get(threadName).setThrown(threadedTests.get(threadName).getTest().getTestResult().errors().nextElement().thrownException());			
						ListenerstManager.getInstance().addError(threadedTests.get(threadName).getTest(), threadedTests.get(threadName).getThrown());
					}
				}
			
				ListenerstManager.getInstance().stopLevel();
			}
		}
		else {
			for(ReportElement el: reportsGroup.get("main")){
				ListenerstManager.getInstance().report(el);
			}
		}
		
		TestThreadFactory.getInstance().removeGroup(groupName);
	}

	/**
	 * @return group name.
	 */
	public String getGroupName() {
		return groupName;
	}
}

/**
 * This class execute group tests in parallel. 
 *
 */
class ThreadedTest extends Thread{
    private SystemTestCase test;
    public SystemTestCase getTest() {
        return test;
    }
   
    public void setTest(SystemTestCase test) {
        this.test = test;
    }
   
    private boolean isPass = false;

    private boolean failReports = false;

	private Throwable thrown;
   
    public ThreadedTest(SystemTestCase test){
        this.test = test;
    }
   
    /**
     * Run the test.
     */
    public void run(){
    	test.run(new TestResult() {
    		public void addListener(TestListener listener) {

    		}
    	});
		isPass = test.getTestResult().wasSuccessful();
		//print exception reports, if exist
		if (!isPass){
			if (test.getTestResult().errorCount() > 0) {
				thrown = test.getTestResult().errors().nextElement().thrownException();			
			} 						
			ListenerstManager.getInstance().addError(test, thrown);		
		}	
	}
       
    public void setPass(boolean isPass) {
		this.isPass = isPass;
	}
    
    /**
     * @return true if test was failed.
     */
    public boolean isPass() {
        return isPass;
    }

    /**
     * @return true if test was failed with reports.
     */
	public boolean isFailReports() {
		return failReports;
	}

	public void setFailReports(boolean failReports) {
		this.failReports = failReports;
	}
    
	public void setThrown(Throwable thrown) {
		this.thrown = thrown;
	}
	
    /**
     * @return exception that thrown by the test.
     */
    public Throwable getThrown() {
        return thrown;
    } 
}