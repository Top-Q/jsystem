package jsystem.runner.agent.publisher;

import java.util.Map;

public class EmptyPublisher implements Publisher {

	@Override
	public boolean isUp() {
		return true;
	}

	@Override
	public String[] getAllPublishOptions() {
		return null;
	}

	@Override
	public Map<String, String> publish(String description, boolean uploadLogs) throws PublisherException {
		return null;
	}

	@Override
	public Map<String, String> publish(String description, boolean uploadLogs, String[] publishOptions)
			throws PublisherException {
		return null;
	}

}
