/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.fixture;

/**
 * The root fixture.<br>
 * It's used by the framework to identify the root.<br>
 * If a fixture doesn't set a parent then the Root fixture is automaticlly set
 * as the parent.
 * 
 * @author Guy Arieli
 */
public final class RootFixture extends Fixture {

	private static RootFixture root;

	public static RootFixture getInstance() {
		if (root == null) {
			root = new RootFixture();
		}
		return root;
	}

	private RootFixture() {
		super();
		setParentFixture(null);

	}

	public void setUp() throws Exception {
	}

	public void tearDown() throws Exception {
	}

	public String toString() {
		return "root";
	}
}
