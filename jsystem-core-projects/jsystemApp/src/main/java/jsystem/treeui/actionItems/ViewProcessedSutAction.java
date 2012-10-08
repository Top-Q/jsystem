/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.SutEditorManager;
import jsystem.treeui.images.ImageCenter;

/**
 * If an SutReader was defined, this button will be visible, to enable viewing of the
 * post-processed sut 
 * 
 * @author Nizan Freedman
 *
 */
public class ViewProcessedSutAction extends IgnisAction {

	private static final long serialVersionUID = 1L;
	
	private static ViewProcessedSutAction action;

	private ViewProcessedSutAction(){
		super();
		putValue(Action.NAME, "View Processed Sut");
		putValue(Action.SHORT_DESCRIPTION, JsystemMapping.getInstance().getViewProcessedSutButton());
		putValue(Action.SMALL_ICON, ImageCenter.getInstance().getImage(ImageCenter.ICON_VIEW_PROCESSED_SUT));
		putValue(Action.LARGE_ICON_KEY, ImageCenter.getInstance().getImage(ImageCenter.ICON_VIEW_PROCESSED_SUT));
		putValue(Action.ACTION_COMMAND_KEY, "view-processed-sut");
	}
	
	public static ViewProcessedSutAction getInstance(){
		if (action == null){
			action =  new ViewProcessedSutAction();
		}
		return action;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		SutEditorManager.getInstance().launchProcessedEditor();
	}

}
