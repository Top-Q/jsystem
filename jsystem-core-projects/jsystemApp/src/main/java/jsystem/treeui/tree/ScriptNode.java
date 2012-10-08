/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import jsystem.framework.scenario.RunnerScript;

public class ScriptNode extends AssetNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6813496885441449222L;

	private RunnerScript rscript;
	public ScriptNode(AssetNode parent, RunnerScript rscript) {
		super(parent, rscript);
		this.rscript = rscript;
	}
	
	public RunnerScript getRunnerScript(){
		return rscript;
	}
	protected int getTestsCount() {
		return 1;
	}

	public boolean isLeaf() {
		return true;
	}

	public String toString() {
		return rscript.getTestName();
	}

}
