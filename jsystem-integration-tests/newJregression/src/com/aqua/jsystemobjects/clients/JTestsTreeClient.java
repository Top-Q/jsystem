package com.aqua.jsystemobjects.clients;

import org.jsystem.objects.xmlrpc.XmlRpcHelper;

import com.aqua.jsystemobjects.handlers.JTestsTreeHandler;

/**
 * 
 * @author Itai.Agmon
 * 
 */
public class JTestsTreeClient extends BaseClient {

	public JTestsTreeClient(XmlRpcHelper connectionHandler) {
		super(connectionHandler);
	}

	@Override
	protected String getHandlerName() {
		return JTestsTreeHandler.class.getSimpleName();
	}

	/**
	 * Use this method only if you want to select test without adding it to
	 * scenario. If you want to add test to scenario use the JScenarioClient.
	 * 
	 * @param methodName
	 * @param className
	 * @throws Exception
	 */
	public void selectBuildingBlock(String methodName, String className) throws Exception {
		handleCommand("Select building block: " + methodName + "." + className, "selectBuildingBlock", methodName,
				className);
		Thread.sleep(500);
	}

	/**
	 * 
	 * @return The building block information from the building block view
	 * @throws Exception
	 */
	public String getCurrentBuildingBlockInformation() throws Exception {
		final String info = (String) handleCommand("Get current building block information",
				"getCurrentBuildingBlockInformation");
		setTestAgainstObject(info);
		Thread.sleep(500);
		return info;
	}

	public void search(final String textToSearch) throws Exception {
		final int leafCount = (Integer) handleCommand("Search for text", "search", textToSearch);
		setTestAgainstObject(leafCount);
		report.report("Number of leafs in tree after search is " + leafCount);
	}
}
