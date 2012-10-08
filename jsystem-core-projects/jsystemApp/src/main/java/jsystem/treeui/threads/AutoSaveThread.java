/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.threads;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.treeui.actionItems.SaveScenarioAction;
import jsystem.treeui.interfaces.JsystemPropertiesChangeListener;
import jsystem.treeui.properties.JSystemPropertiesDialog;
import jsystem.utils.StringUtils;

/**
 * 
 * @author Dan Hirsch
 *	A class to manage the activity of auto save scenario in the jsystem.
 */
public class AutoSaveThread implements JsystemPropertiesChangeListener{
	private static Logger log = Logger.getLogger(AutoSaveThread.class.getName());
	private static AutoSaveThread INSTANCE = null;
	private TimerTask task;
	private Timer timer;
	//in first creation start with current jsystem.properties value.
	int autoSaveInterval;
	
	/**
	 * will read the jsystem.properties file value for interval time set by user.
	 * will crate a task to run the saveScenarioAction
	 * and schedule it to run in fixed intervals that were set.
	 * if value is set to 0 - the default value, then automatic save will be disabled.
	 * 
	 */
	public void startThread(){
		autoSaveInterval = Integer.parseInt(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.AUTO_SAVE_INTERVAL));
		autoSaveInterval = autoSaveInterval * 1000;
		if(autoSaveInterval> 0){
			task = new TimerTask(){
				public void run(){
					try{
						SaveScenarioAction.getInstance().saveCurrentScenario();
					}catch (Exception e){
						log.severe(StringUtils.getStackTrace(e));
					}
				}
			};
			timer = new Timer();
			timer.schedule(task, (long)autoSaveInterval, (long)autoSaveInterval);
		}
		else{
			if(timer != null){
				timer.cancel();
				timer.purge();
				timer = null;
			}
		}
	}

	/**
	 * will return an instance only if the interval property is set to 
	 * a positive number.
	 * number.
	 * @return
	 */
	public static AutoSaveThread getInstance()throws Exception{
		int intervalProperty = Integer.parseInt(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.AUTO_SAVE_INTERVAL));
		intervalProperty = intervalProperty * 1000;
		if(INSTANCE == null){
			INSTANCE = new AutoSaveThread();
			JSystemPropertiesDialog.addListener(INSTANCE);
		}
		return INSTANCE;
	}
	
	/**
	 * in the event of a GUI change in jsystem.properties, 
	 * if the change is related to the interval property itself, reset the interval
	 * value, and reschedule the task to run every new interval cycle.
	 */
	@Override
	public void jsystemPropertiesChanged(){
		int tempInterval = Integer.parseInt(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.AUTO_SAVE_INTERVAL));
		tempInterval = tempInterval * 1000;
		if(tempInterval == autoSaveInterval){
			return;
		}
		if(timer != null){
			timer.cancel();//cancel previous task
			timer.purge();//remove previous task
		}
		startThread();
	}
}