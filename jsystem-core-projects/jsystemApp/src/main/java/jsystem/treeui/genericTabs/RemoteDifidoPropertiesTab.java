package jsystem.treeui.genericTabs;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JPanel;

import jsystem.extensions.report.difido.RemoteDifidoProperties;
import jsystem.treeui.interfaces.JSystemTab;
import jsystem.treeui.teststable.TestsTableController;

/**
 * Generic tab for displaying the remote Difido properties.
 *
 */
public class RemoteDifidoPropertiesTab implements JSystemTab {

	private static Logger log = Logger.getLogger(RemoteDifidoPropertiesTab.class.getName());
	
	@Override
	public JPanel init() {
		PropertiesGuiPanel panel = null;
		try {
			panel = new PropertiesGuiPanel(new File(RemoteDifidoProperties.FILE_NAME));
		} catch (IOException e) {
			log.warning("Failed to open the remote Difido tab due to " + e.getMessage());
			e.printStackTrace();
		}
		return panel;
	}

	@Override
	public String getTabName() {
		return "Remote Difido Properties";
	}

	@Override
	public void setTestsTableController(TestsTableController testsTableController) {
		//Not used
	}

}
