/*
 * Created on Jul 1, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

/**
 * @author guy.arieli
 * 
 */
public class ProgressPanel extends JPanel implements ProgressListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4564376213589613600L;

	JProgressBar testProgressBar;

	JProgressBar suiteProgressBar;

	private final String CURRENT_TEST_TEXT = "Current Test ";

	private final String TOTAL_RUN_TEXT = "Total Run ";

	public ProgressPanel() {

		// UIManager.put("ProgressBar.selectionBackground", Color.BLUE);
		// UIManager.put("ProgressBar.selectionForeground", Color.BLUE);
		// UIManager.put("ProgressBar.foreground", Color.BLACK);
		// UIManager.put("ProgressBar.shadow", Color.GREEN);
		// UIManager.put("ProgressBar.highlight", Color.BLUE);

		UIManager.put("ProgressBar.background", new Color(0xf6, 0xf6, 0xf6));
		UIManager.put("ProgressBar.foreground", new Color(0x8e, 0xa1, 0xb0));

		testProgressBar = new JProgressBar();
		testProgressBar.setMinimum(0);
		testProgressBar.setStringPainted(true);
		testProgressBar.setBorderPainted(true);
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, testProgressBar);
		testProgressBar.setString(CURRENT_TEST_TEXT + "0/0 sec.");

		UIManager.put("ProgressBar.background", new Color(0xe1, 0xe4, 0xe6));

		JProgressBar emptyPB = new JProgressBar();
		emptyPB.setBorderPainted(false);
		add(BorderLayout.CENTER, emptyPB);

		UIManager.put("ProgressBar.background", new Color(0xf6, 0xf6, 0xf6));

		suiteProgressBar = new JProgressBar();
		suiteProgressBar.setMinimum(0);
		suiteProgressBar.setSize(30, suiteProgressBar.getHeight());
		suiteProgressBar.setStringPainted(true);
		suiteProgressBar.setBorderPainted(true);
		add(BorderLayout.SOUTH, suiteProgressBar);
		suiteProgressBar.setString(TOTAL_RUN_TEXT + "0/0 sec.");

		setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 26));

		setBackground(new Color(0xe1, 0xe4, 0xe6));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.treeui.ProgressListener#setCurrentTestRunningTime(long)
	 */
	public void setCurrentTestRunningTime(long time) {
		testProgressBar.setMaximum((int) time);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.treeui.ProgressListener#setCurrentSuiteRunningTime(long)
	 */
	public void setCurrentSuiteRunningTime(long time) {
		suiteProgressBar.setMaximum((int) time);
		suiteProgressBar.setString(TOTAL_RUN_TEXT + getTimeString(-1, time));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.treeui.ProgressListener#updateTimes(long, long)
	 */
	public void updateTimes(long testTime, long suiteTime) {
		testProgressBar.setValue((int) testTime);
		testProgressBar.setString(CURRENT_TEST_TEXT + getTimeString(testTime, testProgressBar.getMaximum()));
		suiteProgressBar.setValue((int) suiteTime);
		suiteProgressBar.setString(TOTAL_RUN_TEXT + getTimeString(suiteTime, suiteProgressBar.getMaximum()));
	}

	private String getTimeString(long time, long maxTime) {
		StringBuffer sb = new StringBuffer();
		if (time >= 0) {
			sb.append(time / 1000);
			sb.append("/");
		}
		sb.append(maxTime / 1000);
		sb.append(" sec.");
		return sb.toString();
	}

}
