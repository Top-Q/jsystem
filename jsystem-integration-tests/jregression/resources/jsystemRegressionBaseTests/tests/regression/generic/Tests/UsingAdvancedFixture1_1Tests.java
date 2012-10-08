package regression.generic.Tests;

import regression.generic.fixturetree.AdvancedFixture1_1;
import junit.framework.SystemTestCase;

public class UsingAdvancedFixture1_1Tests extends SystemTestCase {

	public UsingAdvancedFixture1_1Tests() {
		super();
		setFixture(AdvancedFixture1_1.class);
	}

	public void setUp() {
		report.step("UsingAdvancedFixture1_1Tests setUp");
	}

	public void testPass2() {

	}
}
