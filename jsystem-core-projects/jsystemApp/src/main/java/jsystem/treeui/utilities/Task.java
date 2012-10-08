/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.utilities;

import java.awt.Toolkit;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

import jsystem.utils.ProgressNotifier;

public abstract class Task extends SwingWorker<Void, Object>  implements ProgressNotifier{
	
	private ProgressNotifier notifier;

	protected Task(ProgressNotifier notifier){
		this.notifier = notifier;
	}
	
	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() {
		Toolkit.getDefaultToolkit().beep();
		notifier.notifyProgress("Operation is done" ,100);
		notifier.done();
	}

	@Override
	public void notifyProgress(String message, int progress) {
		publish(message,progress);
		try {Thread.sleep(100);}catch (Exception e){};
	}


	@Override
	protected void process(List<Object> chunks) {
		if (chunks.size() == 0){
			return;
		}
		Iterator<Object> iter = chunks.iterator();
		String message = iter.next().toString();
		int progress = (Integer)iter.next();
		notifier.notifyProgress(message,progress);
	}

}
