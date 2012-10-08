/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import jsystem.treeui.tree.TestNode;

public interface NodeSelectionChange {
	public abstract void changeTestNodeSelection(TestNode testNode, boolean isSelected);
}