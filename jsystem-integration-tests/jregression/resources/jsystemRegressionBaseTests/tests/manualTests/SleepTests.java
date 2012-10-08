package manualTests;

import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class SleepTests extends SystemTestCase {

	int secSleep = 10;

	int timesSleep = 3;

	public int getTimesSleep() {
		return timesSleep;
	}

	public void setTimesSleep(int timesSleep) {
		this.timesSleep = timesSleep;
	}

	/**
	 * Execute Num 1. Select how much time to sleep
	 * 
	 * @params.include secSleep
	 */
	@TestProperties(name = "Sleep for: ${SecSleep} seconds from now!!!")
	public void testSleepNseconds() {
		// String sSecSleep = secSleep;
		report.addProperty("Rate", "10");
		sleep(secSleep * 1000);
	}

	
	/**
	 * This tests sends 1000 messages to the log
	 * 
	 */
	@TestProperties(name = "Send 1000 messages to the log")
	public void testSendMasiveMessagesToLog() {
		// String sSecSleep = secSleep;
		for (int i=0 ; i < 1000 ; i++){
			report.report("This message to the log- "+i);	
		}
		
		sleep(secSleep * 1000);
	}

	
	/**
	 * Execute Num 1. Select numbers os Sleep 2. Select how much time to sleep
	 * 
	 * @params.include timesSleep, secSleep
	 */
	@TestProperties(name = "${TimesSleep} Sleeps. ${SecSleep} sec each time")
	public void testSleepNtimes() {
		report.report("Sleep " + timesSleep + " times. " + secSleep + " sec each time.");
		for (int i = 1; i <= timesSleep; i++) {
			report.report("Sleep " + i + "...");
			sleep(secSleep * 1000);
		}
	}

	public int getSecSleep() {
		return secSleep;
	}

	public void setSecSleep(int secSleep) {
		this.secSleep = secSleep;
	}

}
