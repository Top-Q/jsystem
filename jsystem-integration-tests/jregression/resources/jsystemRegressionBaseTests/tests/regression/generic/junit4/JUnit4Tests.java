package regression.generic.junit4;

import static org.junit.Assert.assertEquals;
import junit.framework.SystemTestCase4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import regression.generic.AdvanceFixture;

/**
 * These are the tests used to test the JUnit 4 support in JSystem.
 * @author Gooli
 *
 */
public class JUnit4Tests extends SystemTestCase4 {
	
	int value;
	
	public JUnit4Tests() {
		setFixture(AdvanceFixture.class);
	}

	@BeforeClass
	public static void beforeClass() {
		report.report("Before class");
	}
	
	@AfterClass
	public static void afterClass() {
		report.report("After class");
	}
	
	/**
	 * This is the first compare test.
	 */
	@Test
	public void compare1() {
		assertEquals(value, 1);
	}
	
	/**
	 * This is the second compare test.
	 */
	@Test
	public void compare2() {
		assertEquals(value, 2);
	}
	
	@Test
	public void failWithException() throws Exception{
		report.report("Failing with Exception");
		throw new Exception("New Exception! YOOHOO");
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
