package jsystem.runner.agent.publisher;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;

public class EmptyPublisher implements Publisher {

	private Reporter report = ListenerstManager.getInstance();


	@Override
	public boolean isUp() {
		// report.report("No publisher was defined",2);
		return true;
	}

	@Override
	public String[] getAllPublishOptions() {
		return null;
	}

	@Override
	public void publish(String description,boolean uploadLogs) throws Exception {
	}

	@Override
	public void publish(String description, boolean uploadLogs, String[] publishOptions) throws Exception {
		
	}


}
