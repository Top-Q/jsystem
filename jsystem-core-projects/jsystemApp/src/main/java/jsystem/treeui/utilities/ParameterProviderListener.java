package jsystem.treeui.utilities;

import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.JDialog;

/**
 * Listener that allows receiving events from parameter providers dialog.
 * 
 * @author Itai Agmon
 * 
 */
public interface ParameterProviderListener {

	/**
	 * 
	 * @param dialog
	 *            The dialog that handles the providers data
	 * @param e
	 *            The event that occurred
	 */
	void actionPerformed(JDialog dialog, EventObject e);

	/**
	 * 
	 * @param dialog
	 *            The dialog that handles the providers data
	 * @param e
	 *            The event that occurred
	 */
	void actionPerformed(JDialog dialog, ActionEvent e);

}
