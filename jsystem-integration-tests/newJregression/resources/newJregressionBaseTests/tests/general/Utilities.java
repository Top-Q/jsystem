package general;

import junit.framework.SystemTestCase4;

import org.junit.Test;

public class Utilities extends SystemTestCase4 {
	private long sleep;
	
	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

	@Test
	public void sleep() {
		sleep(getSleep());
	}
}
