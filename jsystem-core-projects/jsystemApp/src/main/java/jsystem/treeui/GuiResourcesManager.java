/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import jsystem.framework.common.CommonResources;
import jsystem.runner.loader.LoadersManager;

public class GuiResourcesManager {
	
	public enum GuiResource{
		RUNNER_TITLE,
		WAIT_DIALOG_CANCEL,
		NEW_SCENARIO_BUTTON_TOOLTIP;
	}
	
	private static GuiResourcesManager grm = null;
	public static GuiResourcesManager getInstance(){
		if(grm == null){
			grm = new GuiResourcesManager();
		}
		return grm;
	}
	
	public void init() throws Exception{
		Properties prop = new Properties();
		prop.load(LoadersManager.getInstance().getLoader().getResourceAsStream(CommonResources.GUI_RESOURCE_FILE));
		guiMap = new HashMap<GuiResource, String>();
		GuiResource[] resources = GuiResource.values();
		for(GuiResource resource: resources){
			guiMap.put(resource, prop.getProperty(resource.name().toLowerCase().replace('_', '.')));
		}
	}
	HashMap<GuiResource, String> guiMap = null;
	public String getGuiResource(GuiResource guiResour, Object ...objects ){
		return MessageFormat.format(guiMap.get(guiResour), objects);
	}
	public String getGuiResource(GuiResource guiResour){
		return guiMap.get(guiResour);
	}
}
