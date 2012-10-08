/*
 * Created on Nov 25, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.util.Vector;

import jsystem.framework.scenario.JTest;

/**
 * @author guy.arieli
 * 
 */
public interface TestsTreeListener {
	public boolean addTests(Vector<JTest> tests);
}
