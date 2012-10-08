/*
 * Created on Jul 1, 2005
 * 
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

/**
 * @author guy.arieli
 * 
 */
public interface ProgressListener {
	public void setCurrentTestRunningTime(long time);

	public void setCurrentSuiteRunningTime(long time);

	public void updateTimes(long testTime, long suiteTime);
}
