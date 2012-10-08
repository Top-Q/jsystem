/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import jsystem.treeui.images.ImageCenter;

/**
 * a sorting JPopupMenu for the sections sorting has 2 members -
 * AlphaBetical/Type order and also a User-Defined if present
 * 
 * @author Nizan Freedman
 * 
 */
public class SortOptionsPanel extends JPopupMenu implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8636392839843300700L;

	private int size;

	private SortMenuItem ab;

	private SortMenuItem order;

	private ParametersPanel ppanel;

	private SortMenuItem current;
	
	public SortOptionsPanel(ParametersPanel ppanel) {
		super();
		this.setLayout(new BorderLayout());
		this.ppanel = ppanel;
		ab = new SortMenuItem("AlphaBetical", this);
		order = new SortMenuItem("Pre-Defined order", this);
		current = ab;
		createPopUp();
	}

	/**
	 * create the togglable popup menu for the "Sort parameters" button
	 * 
	 */
	public void createPopUp() {
		this.removeAll();
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		current.setMarked(false);
		if (ppanel.sortSection == ParametersPanel.SORT_BY_SECTION_STRING){
			current = order;
		} else {
			current = ab;
		}
		current.setMarked(true);
		panel.add(ab);
		if (ppanel.getSectionOrder().equals("")) // no user string order
			size = 25 * 2;
		else {
			size = 25 * 3;
			panel.add(order);
		}
		add(panel);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(ppanel.sortButton)) {
			Component c = (Component) (source);// e.getSource();
			this.show(c, 0, c.getHeight() - size);
		} else if (source.equals(ab) || source.equals(order)) {
			this.setVisible(false);
			current.setMarked(false);
			if (source.equals(ab)) {
				current = ab;
				ppanel.sectionChanged(ParametersPanel.SORT_BY_SECTION_AB);
			} else if (source.equals(order)) {
				current = order;
				ppanel.sectionChanged(ParametersPanel.SORT_BY_SECTION_STRING);
			}
			current.setMarked(true);
		}
	}

}

/**
 * a sortItem JButton for the SortOptionsPanel
 * @author Nizan Freedman
 */
class SortMenuItem extends JButton {
	private static final long serialVersionUID = 3129297102506958073L;
	Icon icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_SMALL_OK);
	public SortMenuItem(String s, ActionListener listener) {
		super(s, null);
		this.addActionListener(listener);
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	/**
	 * sets the current button as marked or not (adds an icon)
	 * 
	 * @param addIcon
	 */
	public void setMarked(boolean addIcon) {
		if (addIcon) {
			this.setIcon(icon);
		}else{
			this.setIcon(null);
		}
	}
}
