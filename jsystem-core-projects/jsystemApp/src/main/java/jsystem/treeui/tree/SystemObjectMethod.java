/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.util.ArrayList;
import java.util.logging.Level;

import jsystem.framework.scenario.RunnerSOTest;
import jsystem.framework.scenario.RunnerTest;
import jsystem.treeui.suteditor.planner.SutTreeNode;
import jsystem.treeui.suteditor.planner.SutTreeNode.NodeType;

public class SystemObjectMethod extends TestNode {
	String methodName;
	public SystemObjectMethod(String methodName, String[] parametersName, Class<?>[] parametersType, String descriptor) {
		this.methodName = methodName;
		try {
			rt = new RunnerSOTest(methodName, parametersName, parametersType, descriptor);
			rt.initTestProperties(false);
			meaningfulName = RunnerSOTest.getParametersAsString(parametersName);
			String[] groups = rt.getGroups();
			if(groups != null){
				for(String group: groups){
					GroupsManager.getInstance().addGroup(group);
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING,"Failed to init test", e);
		}
	}
	public String getXpath(){
		ArrayList<String> xpathElements = new ArrayList<String>();
		AssetNode parent = (AssetNode)getParent();
		while(parent != null &&(parent instanceof SutTreeNode)){
			SutTreeNode parentSut = (SutTreeNode)parent;
			String xpath = parentSut.getName();
			if(xpath == null || xpath.equals("")){
				xpath = "sut";
			}
			if(parentSut.getType().equals(NodeType.ARRAY_SO)){
				xpath = xpath + "[" + (parentSut.getIndex() + 1) +"]";
			}
			xpathElements.add(xpath);
			parent = (AssetNode)parent.getParent();
		}
		StringBuffer buf = new StringBuffer();
		for(int i = xpathElements.size() -1; i >= 0; i--){
			buf.append("/");
			buf.append(xpathElements.get(i));
		}
		if(rt != null){
			((RunnerSOTest)rt).setXpath(buf.toString());
		}
		return buf.toString();
	}
	public RunnerTest getTest() {
		getXpath();
		return rt;
	}
	public String toString() {
		if (meaningfulName != null && (!meaningfulName.equals(""))) {
			return methodName + " - " + meaningfulName;
		}
		return methodName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 400217159557842839L;

}
