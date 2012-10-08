/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;

public class PublishEventAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static PublishEventAction action;
	
	private PublishEventAction(){
		super();
		putValue(Action.NAME, "Publish Event");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getPublishEventButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_PUBLISH));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_PUBLISH));
		putValue(Action.ACTION_COMMAND_KEY, "publish-event");
	}
	
	public static PublishEventAction getInstance(){
		if (action == null){
			action =  new PublishEventAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TestRunner.treeView.getTableController().addPublishTest();
	}

}
