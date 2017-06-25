package jsystem.treeui.teststable;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.TestsContainer;

/**
 * Allow user to add items and behaviors to the tests table controller context
 * menu. <br>
 * Since the plugins are also registered as listeners in the
 * testListenerstManager, they can implement any other JSystem listener
 * interface and receive events accordingly (e.g. ExtendTestListener,
 * ScenarioListener).
 * 
 * @author Itai Agmon
 *
 */
public interface ContextMenuPlugin extends ActionListener {

	/**
	 * Is the plugin should be appear in the context menu when right clicking on
	 * the specified tree node.
	 * 
	 * @param currentNode
	 *            The node the user clicked on
	 * @param container
	 * @param test
	 *            The test that the node represents
	 * @return true if the plugin should be displayed
	 */
	boolean shouldDisplayed(ScenarioTreeNode currentNode, TestsContainer container, JTest test);

	/**
	 * Initialize the plugin
	 * 
	 * @param testsTableController
	 *            The object that represents the left hand tests table. The most
	 *            common use of this object to use to it to retrieve the current
	 *            selected tests by calling
	 *            <code>testsTableController.selectedTests</code> <br>
	 */
	void init(TestsTableController testsTableController);

	/**
	 * Get the item text that should appear in the context menu
	 * 
	 * @return
	 */
	String getItemName();

	/**
	 * The icon to display in the context menu. <br>
	 * This is called before every rendering of the context menu so it can be
	 * used to change the icon according to different scenarios.
	 * 
	 * @return The icon to dsiplay
	 */
	ImageIcon getIcon();

}
