/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.loader;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

public class LoadersManager {
	private static LoadersManager manager;

	private boolean dynamicLoading = true;

	public static LoadersManager getInstance() {
		if (manager == null) {
			manager = new LoadersManager();
		}
		return manager;
	}

	ClassLoader loader = null;

	private LoadersManager() {
	}

	public ClassLoader getLoader() {
		String ld = JSystemProperties.getInstance().getPreference(FrameworkOptions.LOADER_DISABLE);
		if (ld != null && ld.equalsIgnoreCase("true")) {
			dynamicLoading = false;
		}
		if (loader == null) {
			if (JSystemProperties.getInstance().isJsystemRunner() && dynamicLoading) {
				loader = new ExtendsTestCaseClassLoader(ClassPathBuilder.getClassPath(), getClass().getClassLoader());
			} else {
				loader = this.getClass().getClassLoader();
			}
		}
		return loader;
	}

	public void dropAll() {
		loader = null;
	}


	public boolean isDynamicLoading() {
		return dynamicLoading;
	}

	public void setDynamicLoading(boolean dynamicLoading) {
		this.dynamicLoading = dynamicLoading;
	}

}