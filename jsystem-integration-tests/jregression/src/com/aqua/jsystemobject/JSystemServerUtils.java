package com.aqua.jsystemobject;

import javax.swing.tree.TreePath;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

public class JSystemServerUtils {
	private final static JSystemServerUtils INSTANCE = new JSystemServerUtils();
	private JSystemServerUtils(){}
	private JFrameOperator mainFrame;
	
	public static JSystemServerUtils getInstance(){
		return INSTANCE;
	}
	
	public int selectTestsRows(int[] rows) {
		JTreeOperator scenarioTree = new JTreeOperator(mainFrame, 0);
		TreePath[] pathes = new TreePath[rows.length];
		for (int i = 0; i < pathes.length; i++) {
			pathes[i] = scenarioTree.getPathForRow(rows[i]);
		}

		JSystemProperties.getInstance().setPreference(FrameworkOptions.SUB_SCENARIO_EDIT, "true");

		scenarioTree.selectPaths(pathes);
		return 0;
	}
}
