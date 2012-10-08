/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.io.File;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

public class ScenarioNode extends AssetNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3159112256549659822L;

	String meaningfulName = null;
	public ScenarioNode(AssetNode parent, String scenarioName, String meaningfulName) {
		super(parent, scenarioName);
		this.meaningfulName = meaningfulName;
	}

	protected int getTestsCount() {
		return 1;
	}

	public boolean isLeaf() {
		return true;
	}

	public String getScenarioName() {
		return (String) getUserObject();
	}
	
	public String toString(){
		/*
		 * Set the string to use when presented in the scenario tree
		 */
		if(meaningfulName != null && !"true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.IGNORE_MEANINGFUL_NAME))){
			return meaningfulName;
		}
		int lastIndex = getScenarioName().lastIndexOf(File.separatorChar);
		if(lastIndex >= 0){
			return getScenarioName().substring(lastIndex + 1);
		}
		return getScenarioName();
	}
}
