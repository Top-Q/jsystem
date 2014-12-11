package jsystem.framework.scenario.flow_control;

import java.util.ArrayList;
import java.util.List;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;

public class FlowControlHelpers {

	// Recursively collect all of the flow controls in the tree
	public static List<AntFlowControl> getAllFlowControls(JTestContainer container) {
		ArrayList<AntFlowControl> allFlowControls = new ArrayList<AntFlowControl>();

		for (JTest test : container.getRootTests()) {
			if (test instanceof AntFlowControl) {
				allFlowControls.add((AntFlowControl) test);
			}
			if (test instanceof JTestContainer) {
				allFlowControls.addAll(getAllFlowControls((JTestContainer) test));
			}
		}
		return allFlowControls;
	}
	
}
