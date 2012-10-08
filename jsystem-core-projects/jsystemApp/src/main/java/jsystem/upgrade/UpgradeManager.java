/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.upgrade;

import java.io.File;

import javax.swing.JOptionPane;

import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.UpgradeAndBackwardCompatibility;
import jsystem.treeui.images.ImageCenter;

/**
 * Focal point for handling upgrade processes.<br>
 * The upgrade manager currently handles two upgrades:
 * to Jsystem 4.8 and to Jsystem 5.1.<br>
 * The two upgrade processes are different in two ways:<br>
 * 1. The changes that were made in scenario file structure are different<br>
 * 2. The way the upgrade process is managed is different.<br>
 * <br>
 * The upgrade to Jsystem 4.8 is done by {@link ScenarioConversion};
 * the upgrade to JSystem 5.1 is embedded in {@link Scenario} and {@link JTestContainer}
 * code and assisted by {@link UpgradeAndBackwardCompatibility}.<br>
 * <br>
 * My assumption is that the upgrade process in later jsystem 
 * versions can not be anticipated so there is no attempt to create a
 * common upgrade class. 
 * 
 * @see ScenarioConversion
 * @see UpgradeAndBackwardCompatibility
 * @author goland
 */
public class UpgradeManager {

	/**
	 */
	public static void upgrade(boolean applicationStartup) throws Exception {
		//upgrade to 4.8
		if (!applicationStartup){
			ScenarioConversion.getInstance().resetScenarioConvertorFlag();
		}
		ScenarioConversion.getInstance().processOldFormatScenarios();
		
		//upgrade to 5.1
		if (UpgradeAndBackwardCompatibility.checkWhetherToBackupScenariosAndUpdateFlag()){
			File f = UpgradeAndBackwardCompatibility.backupScenarios();
			JOptionPane.showOptionDialog(null,
					"The system has identified scenarios which where created with previous versions of JSystem. \n\r"+
					"The scenarios were backed-up to "+f.getPath(),"JSystem upgrade", 
	   			  JOptionPane.OK_OPTION,JOptionPane.INFORMATION_MESSAGE, ImageCenter.getInstance().getImage(ImageCenter.ICON_INFO), new String[]{"Close"}, "Close");

		}
	}
	
	
}
