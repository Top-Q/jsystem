package regression.report;

import jsystem.framework.report.Summary;
import junit.framework.SystemTestCase;

public class AddPublishSummaryValues extends SystemTestCase {

	public void testAddSummaryValues() throws Exception{
	
		Summary.getInstance().setProperty("Version","SummaryVersion");
		Summary.getInstance().setProperty("Build","SummaryBuild");	
		Summary.getInstance().setProperty("description","SummaryDescription");
	}
	
}
