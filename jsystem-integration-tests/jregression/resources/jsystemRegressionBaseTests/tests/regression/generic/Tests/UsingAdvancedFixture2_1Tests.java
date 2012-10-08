package regression.generic.Tests;

import junit.framework.SystemTestCase;
import regression.generic.fixturetree.AdvancedFixture2_1;

public class UsingAdvancedFixture2_1Tests extends SystemTestCase {
	public UsingAdvancedFixture2_1Tests() {
		super();
		setFixture(AdvancedFixture2_1.class);
	}

	public void setUp() {
		report.step("UsingAdvancedFixture2_1Tests setUp");
	}

	public void testPass3() {

	}
}
