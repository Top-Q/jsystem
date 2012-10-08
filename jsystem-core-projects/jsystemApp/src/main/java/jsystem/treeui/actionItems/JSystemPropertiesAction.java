/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.images.ImageCenter;
import jsystem.treeui.properties.JSystemPropertiesDialog;

public class JSystemPropertiesAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	private static JSystemPropertiesAction action;

	private JSystemPropertiesAction(){
		super();
		putValue(Action.NAME, JsystemMapping.getInstance().getJSystemPropertiesMenuItem());
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getJSystemPropertiesMenuItem());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_JSYSTEM));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_JSYSTEM));
		putValue(Action.ACTION_COMMAND_KEY, "jsystem-properties-dialog");
	}
	
	public static JSystemPropertiesAction getInstance(){
		if (action == null){
			action =  new JSystemPropertiesAction();
		}
		return action;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			JSystemPropertiesDialog.getInstance().dialogShow();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
