/*
 * Created on Jul 2, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.util.regex.Pattern;

import javax.swing.JTree;

import jsystem.treeui.tree.TestsTreeController;

/**
 * Used to filter tests in the tests tree. Use setFilter to set a regular
 * excpression of the test name you look for all the other will be filtered.
 * 
 * @author guy.arieli
 * 
 */
public class TestFilterManager {
	private static TestFilterManager manager;

	public static TestFilterManager getInstance() {
		if (manager == null) {
			manager = new TestFilterManager();
		}
		return manager;
	}

	/**
	 * ITAI: The filter and filterRegexp are arrays because we want to support
	 * AND operator. Since AND is not possible is regular expressions we create
	 * set of regular expressions.
	 */
	private Pattern filterRegexp[];
	private String[] filter;
	private DelaiedUpdater updater = null;

	public void init(TestsTreeController ttc) {
		updater = new DelaiedUpdater(ttc);
		updater.start();
	}

	/**
	 * Checks if the given class name matches the filter criteria.
	 * 
	 * @param className
	 *            The current class name (and test name) to check if matches the
	 *            filter criteria.
	 * @return True if the classname is NOT match the filter criteria.
	 */
	public boolean filter(final String className) {
		if (filterRegexp == null) {
			return false;
		}
		boolean match = true;
		for (Pattern regex : filterRegexp) {
			match &= regex.matcher(className).find();
		}
		return !match;
	}

	/**
	 * Sets array of filters. The relationship between the specified filters is
	 * AND.
	 * 
	 * @param filter
	 */
	public void setFilter(final String[] filter) {
		if (filter == null || filter.equals("")) {
			this.filter = null;
			filterRegexp = null;
		} else {
			this.filter = filter;
			filterRegexp = new Pattern[filter.length];
			for (int i = 0; i < filter.length; i++) {
				filterRegexp[i] = Pattern.compile(filter[i], Pattern.CASE_INSENSITIVE);
			}
		}
		updater.filterChanged(filter);
	}

	public String[] getFilter() {
		return filter;
	}

}

/**
 * The updater wait for half of second silent in the text field and only then it
 * will be updated.
 * 
 * @author guy.arieli
 * 
 */
class DelaiedUpdater extends Thread {
	TestsTreeController ttc;
	long lastFilterChangeTime = 0;
	boolean updated = true;
	boolean isFilterChange = false;
	String[] filter = null;

	public DelaiedUpdater(TestsTreeController ttc) {
		setName("Filter updater thread");
		this.ttc = ttc;
	}

	public void run() {
		while (true) {
			try {
				waitForFilterChange();
				ttc.refreshView();
				if (isFilterChange) {
					continue;
				}
				JTree tree = ttc.getTree();
				int row = 0;

				while (row < tree.getRowCount()) {
					if (isFilterChange) {
						break;
					}
					tree.expandRow(row);
					row++;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void waitForFilterChange() throws Exception {
		while (!isFilterChange) {
			Thread.sleep(30);
		}
		isFilterChange = false;
		while (System.currentTimeMillis() - lastFilterChangeTime < 500) {
			Thread.sleep(50);
		}
	}

	public void filterChanged(final String[] filter) {
		this.filter = filter;
		lastFilterChangeTime = System.currentTimeMillis();
		isFilterChange = true;
	}

}