/*
 * Created on 15/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.teststable;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class ScenarioModel extends DefaultTreeModel {

	private static final long serialVersionUID = 3995271562461451365L;

	/**
	 * This is very dangerous way to create instance of the model. The problem
	 * is that the root is not a real root and it may cause problems with
	 * nodeChanged event.
	 */
	public ScenarioModel() {
		super(new ScenarioTreeNode(null));
	}

	/**
	 * Builds the parents of node up to and including the root node, where the
	 * original node is the last element in the returned array. The length of
	 * the returned array gives the node's depth in the tree.
	 * 
	 * @param aNode
	 *            the TreeNode to get the path for
	 * @param depth
	 *            an int giving the number of steps already taken towards the
	 *            root (on recursive calls), used to size the returned array
	 * @return an array of TreeNodes giving the path from the root to the
	 *         specified node
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
		// ITAI: This method was override because that the root defined is the
		// node is not the real root. Because of that, when the model creates
		// event (for example nodeChanged event), and it builds the path to the
		// node, the path is not correct and the UI ignores it.
		// This method is a hack that fixes it.

		TreeNode[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/*
		 * Check for null, in case someone passed in a null node, or they passed
		 * in an element that isn't rooted at root.
		 */
		if (aNode == null) {
			if (depth == 0) {
				return null;
			} else {
				retNodes = new TreeNode[depth];
			}
		} else {
			depth++;
			if (isRoot(aNode)) {
				retNodes = new TreeNode[depth];
			} else {
				retNodes = getPathToRoot(aNode.getParent(), depth);
			}
			if (isRoot(aNode)) {
				retNodes[retNodes.length - depth] = root;
			} else {
				retNodes[retNodes.length - depth] = aNode;

			}
		}
		return retNodes;
	}
	
	private boolean isRoot(TreeNode node){
		if (!(node instanceof ScenarioTreeNode)){
			throw new IllegalArgumentException("Node is not from instance "+ScenarioTreeNode.class.getSimpleName());
		}
		return (((ScenarioTreeNode)node).getTest().getParent() == null);
	}

}
