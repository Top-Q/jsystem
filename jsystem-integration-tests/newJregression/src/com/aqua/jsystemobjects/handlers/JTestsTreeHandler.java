package com.aqua.jsystemobjects.handlers;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.netbeans.jemmy.operators.JComboBoxOperator;
import org.netbeans.jemmy.operators.JEditorPaneOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTreeOperator;

import com.aqua.utils.NameChooser;
import com.aqua.utils.ToolTipChooser;

/**
 * 
 * @author Itai.Agmon
 * 
 */
public class JTestsTreeHandler extends BaseHandler {

	/**
	 * Use this method only if you want to select test without adding it to
	 * scenario. If you want to add test to scenario use the JScenarioClient.
	 * 
	 * @param methodName
	 * @param className
	 * @throws Exception
	 */
	public int selectBuildingBlock(String methodName, String className) throws Exception {
		return checkTestInTestsTree(methodName, className, true);
	}

	/**
	 * takes test class name and test name to check, and a boolean value of true
	 * or false and returns an int 0 if succeeds or -1 if such path is not found
	 * in test tree only checks the requested test's checkboxes
	 * 
	 * @param node
	 * @param parentNode
	 * @param check
	 * @return
	 * @throws Exception
	 */
	public int checkTestInTestsTree(String node, String parentNode, boolean check) throws Exception {
		TreePath foundPath = getTreePath(node, parentNode);
		if (foundPath == null) {
			return -1;
		}
		JPopupMenuOperator pp = new JPopupMenuOperator(testsTree.callPopupOnPath(foundPath));
		if (check) {
			pp.pushMenu(jmap.getTestSelection());
		} else {
			pp.pushMenu(jmap.getTestUnSelection());
		}
		return 0;
	}

	/**
	 * Adds value to the tests tree filter combobox and selects the value.
	 * 
	 * @param textToSearch
	 * @return Tree leaf count
	 * @throws Exception
	 */
	public int search(final String textToSearch) throws Exception {
		JComboBoxOperator filter = new JComboBoxOperator(mainFrame, new ToolTipChooser(jmap.getFilterToolTip()));
		filter.addItem(textToSearch);
		filter.selectItem(textToSearch);

		// Gives time for the tree to refresh
		Thread.sleep(1000);
		return getTreeLeafCount();
	}

	/**
	 * 
	 * @return Tree leaf count
	 */
	public int getTreeLeafCount() {
		JTreeOperator tree = new JTreeOperator(mainFrame, new NameChooser(jmap.getTestsTreeName()));
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
		return root.getLeafCount();
	}

	/**
	 * 
	 * @return The building block information from the building block view
	 * @throws Exception
	 */
	public String getCurrentBuildingBlockInformation() throws Exception {
		System.out.println("*******  In getCurrentBuildingBlockInformation");
		JEditorPaneOperator nodeInformation = new JEditorPaneOperator(mainFrame, new ToolTipChooser(
				jmap.getBuildingBlockInformationToolTip()));
		return nodeInformation.getText();
	}
	
}
