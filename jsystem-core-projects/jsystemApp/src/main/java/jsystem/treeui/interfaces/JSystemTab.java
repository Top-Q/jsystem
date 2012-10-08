package jsystem.treeui.interfaces;

import javax.swing.JPanel;

/**
 * Add a generic tab to the JSystem tabs (under TestsTableController)
 * The tab must implement the init() and getTabName() functions
 * 
 * @author Topq
 *
 */
public interface JSystemTab {

	/**
	 * Init the JPanel component
	 * @return the JPanel component to show in the tab
	 */
	public JPanel init();
	
	/**
	 * The name of the tab 
	 * @return the tab title
	 */
	public String getTabName();
	
}
