/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

/**
 * Enum of the agents notifications level.
 * The enum was created to enable users to control the amount of notification
 * sent from agent.
 * @author gderazon
 */
public enum NotificationLevel {	
	ALL,
	ALL_ONLY_TITLE,
	NO_REPORT,
	NO_FAIL,
	NO_TEST_INDICATION;
	
	public static NotificationLevel getCurrentNotificationLevel() {
		String level = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.AGENT_NOTIFICATION_LEVEL);
		return NotificationLevel.valueOf(level);
	};

}
