/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import jsystem.framework.scenario.RunnerFixture;

/**
 * represent Fixture Node on Test Tree and Scenario Tree
 * @author uri.koaz
 *
 */
public class FixtureNode extends AssetNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FixtureNode(AssetNode parent, String className) throws Exception {
		super(parent, className);
	}

	@Override
	protected int getTestsCount() {
		return 1;
	}

	public boolean isLeaf() {
		return true;
	}

	public RunnerFixture getFixture() {
		return new RunnerFixture((String)userObject);
	}

	@Override
	public String toString() {
		return (String) userObject;
	}
}
