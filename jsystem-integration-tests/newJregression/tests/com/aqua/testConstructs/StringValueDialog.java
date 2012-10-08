package com.aqua.testConstructs;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jsystem.treeui.images.ImageCenter;

public class StringValueDialog extends JDialog {

	private String value;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void initDialog(Object[] objects){
		setTitle("String Dialog");
		((Frame)this.getOwner()).setIconImage(ImageCenter.getInstance().getAwtImage(ImageCenter.ICON_JSYSTEM));
		setModalityType(DEFAULT_MODALITY_TYPE);
		JTextField text = new JTextField("");
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(text);
		setContentPane(mainPanel);
		setSize(100, 50);
		setVisible(true);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Bla Bla");
		StringValueDialog myDialog = new StringValueDialog();
		myDialog.initDialog(null);
		frame.setContentPane(myDialog);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
