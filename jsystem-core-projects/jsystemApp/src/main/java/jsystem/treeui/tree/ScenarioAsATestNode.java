/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

/**
 * defines a Scenario node that is marked as test
 * 
 * @author Nizan Freedman
 *
 */
public class ScenarioAsATestNode extends ScenarioNode {

	public ScenarioAsATestNode(AssetNode parent, String scenarioName,
			String meaningfulName) {
		super(parent, scenarioName, meaningfulName);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
