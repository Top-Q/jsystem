/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.lang.reflect.Method;
import java.util.logging.Level;

import jsystem.framework.scenario.RunnerTest;
import jsystem.utils.StringUtils;

public class TestNode extends AssetNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String meaningfulName = null;

	protected RunnerTest rt;
	public TestNode(AssetNode parent, Method userObject) {
		super(parent, userObject);
		String className = ((Class<?>)((TestCaseNode) parent).getUserObject()).getName();
		String methodName = userObject.getName();
		rt = new RunnerTest(className, methodName);
		try {
			rt.initTestProperties(false);
			meaningfulName = rt.getMeaningfulName();
			String[] groups = rt.getGroups();
			if(groups != null){
				for(String group: groups){
					GroupsManager.getInstance().addGroup(group);
				}
			}
		} catch (Exception e) {
			log.log(Level.FINE,"Failed getting meaningfull name");
		}
	}
	protected TestNode(){
	}

	protected int getTestsCount() {
		return 1;
	}

	public boolean isLeaf() {
		return true;
	}

	public String toString() {
		if (!StringUtils.isEmpty(meaningfulName)) {
			return ((Method) userObject).getName() + " - " + meaningfulName;
		}
		return ((Method) userObject).getName();
	}

	public RunnerTest getTest() {
		return rt;
	}

}
