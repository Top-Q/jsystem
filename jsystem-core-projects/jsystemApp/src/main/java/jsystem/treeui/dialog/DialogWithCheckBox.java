/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.dialog;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.treeui.TestRunner;

/**
 * General dialog with checkBox for different Framework options
 * 
 * @author Nizan Freedman
 * 
 */
public class DialogWithCheckBox extends JDialog {

	private static final long serialVersionUID = -1570893402282756718L;

	/**
	 * this function will display(confirm) a dialog pop up with a checkbox -the value of the check box will be given from the
	 * FrameworkOptions value and will be set to the properties files.
	 * 
	 * after the user answer- the value of the checkbox will be set to the Jsystem properties file
	 * 
	 * Note: the value of the checkbox will be set to the properties file only if the user confirm the dialog
	 *
	 *   
	 * @param title - the title of the popup
	 * @param message - the message of the dialog
	 * @param checkBox - the label of the check box
	 * @param option - the value of the checkbox - if the propertie is ture in the Jsystem propertie file , the check box will be checked.
	 * 					else- will be set to false 
	 * 
	 * @return user answer (yes or no)
	 * 
	 * 
	 * @author liel_r 
	 */
	public static int showConfirmDialogWithCheckBox(String title, String message, String checkBox, FrameworkOptions option) {

		boolean propertyValue = false;
		/**
		 * read property from jsystem.properties
		 */
		String editProperty = JSystemProperties.getInstance().getPreference(option);

		if(editProperty!=null){
			if(("true").equals(editProperty)){
				propertyValue = true; 
			}
		}
		
		JCheckBox cb = new JCheckBox(checkBox, propertyValue);

		JLabel label = new JLabel(message);

		JLabel separator = new JLabel();

		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(3, 1));

		panel.add(label);

		panel.add(separator);

		panel.add(cb);
		
		int answer = JOptionPane.YES_OPTION;

		answer = JOptionPane.showConfirmDialog(TestRunner.treeView, panel, title, JOptionPane.YES_NO_OPTION);

		//set the value only if the user will confirm the dialog
		if (JOptionPane.YES_OPTION ==answer  ) {
			JSystemProperties.getInstance().setPreference(option, cb.isSelected() + "");
		}

		return answer;

	}

	/**
	 * Show a confirm dialog with given title, message and checkBox String.
	 * 
	 *  
	 * @param title
	 *            The dialog title
	 * @param message
	 *            The dialog message
	 * @param checkBox
	 *            The checkBox String
	 * @param option
	 *            The FrameworkOption to be read and modified if checkbox is
	 *            marked
	 * @return
	 */
	public static int showConfirmDialog(String title, String message, String checkBox, FrameworkOptions option) {
		JCheckBox cb = new JCheckBox(checkBox);

		JLabel label = new JLabel(message);

		JLabel separator = new JLabel();

		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(3, 1));

		panel.add(label);

		panel.add(separator);

		panel.add(cb);

		/**
		 * read property from jsystem.properties
		 */
		String editProperty = JSystemProperties.getInstance().getPreference(option);

		int answer = JOptionPane.YES_OPTION;

		if (editProperty == null || "false".equals(editProperty)) {
			answer = JOptionPane.showConfirmDialog(TestRunner.treeView, panel, title, JOptionPane.YES_NO_CANCEL_OPTION);
		}

		if (cb.isSelected()) {
			JSystemProperties.getInstance().setPreference(option, true + "");
		}

		return answer;
	}

}
