/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

/**
 * Interface for core components which want to notify on
 * long operations progress.
 * The interface was defined in order not to need to incorporate GUI
 * components in core entities.
 * 
 * Core entities which want to publish their progress should incorporate
 * {@link ProgressNotifier} in the code, the code which invoked the operation, should
 * pass to the component the notifier (usually it will be a GUI component)
 * which will show operation progress.
 * 
 * @author goland
 */
public interface ProgressNotifier {

	/**
	 * progress is in percentage (should be between 0-100)
	 */
	public void notifyProgress(String message, int progress);
	
	/**
	 * Should be called when operation is done.
	 */
	public void done();
}
