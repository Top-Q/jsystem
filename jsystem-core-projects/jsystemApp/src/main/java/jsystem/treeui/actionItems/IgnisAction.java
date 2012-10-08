/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.actionItems;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * all Action objects should extend this class<br>
 * it enables them to retrieve values of different keys
 * 
 * @author nizanf
 */
public abstract class IgnisAction extends AbstractAction {

	private static final long serialVersionUID = -5451632709696124200L;

	public abstract void actionPerformed(ActionEvent e);
	
	public String getActionCommand(){
		return ""+getValue(Action.ACTION_COMMAND_KEY);
	}
	
	public String getName(){
		return ""+getValue(Action.NAME);
	}
	
	public String getShortDescription(){
		return ""+getValue(Action.SHORT_DESCRIPTION);
	}
	
	public String getLongDescription(){
		return ""+getValue(Action.LONG_DESCRIPTION);
	}
	
	public ImageIcon getSmallImageIcon(){
		return (ImageIcon)getValue(Action.SMALL_ICON);
	}
	
	public ImageIcon getLargeImageIcon(){
		return (ImageIcon)getValue(Action.LARGE_ICON_KEY);
	}

}
