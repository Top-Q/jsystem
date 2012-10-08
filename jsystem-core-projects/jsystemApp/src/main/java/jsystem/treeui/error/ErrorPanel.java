/*
 * Created on Dec 10, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.treeui.error;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import jsystem.framework.TestRunnerFrame;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.WaitDialog;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;

/**
 * @author guy.arieli
 * 
 */
public class ErrorPanel {
	private static ErrorDialog ERROR_DIALOG;
	public static JPanel getErrorPanel(JTable table, String message) {
		JPanel pane = new JPanel();
		pane.setLayout(new GridBagLayout());

		ImageIcon icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_BUG);
		GridBagConstraints iconConstraints = new GridBagConstraints();
		iconConstraints.weightx = 1;
		iconConstraints.weighty = 1;
		iconConstraints.gridx = 0;
		iconConstraints.gridy = 0;
		iconConstraints.gridwidth = 1;
		iconConstraints.gridheight = 1;
		iconConstraints.anchor = GridBagConstraints.CENTER;
		iconConstraints.insets = new Insets(2, 2, 2, 8); // top padding
		pane.add(new JLabel(icon), iconConstraints);

		JTextArea textArea = new JTextArea(message);
		GridBagConstraints textAreaConstraints = new GridBagConstraints();
		textAreaConstraints.weightx = 1;
		textAreaConstraints.weighty = 1;

		textAreaConstraints.gridx = 1;
		textAreaConstraints.gridy = 0;
		textAreaConstraints.gridwidth = 1;
		textAreaConstraints.gridheight = 1;
		textAreaConstraints.anchor = GridBagConstraints.CENTER;
		pane.add(textArea, textAreaConstraints);

		JScrollPane tableScroll = new JScrollPane(table);
		GridBagConstraints tableConstraints = new GridBagConstraints();
		tableConstraints.weightx = 1;
		tableConstraints.weighty = 1;
		tableConstraints.gridx = 0;
		// textAreaConstraints.gridy = 1;
		tableConstraints.gridwidth = 2;
		// textAreaConstraints.gridheight = 1;
		tableConstraints.anchor = GridBagConstraints.CENTER;
		tableConstraints.fill = GridBagConstraints.HORIZONTAL;
		// tableConstraints.fill = GridBagConstraints.VERTICAL;

		pane.add(tableScroll, tableConstraints);
		return pane;
		// JOptionPane.showMessageDialog()
	}

	public static JDialog getErrorDialog(Vector<String[]> errors, String message) {
		JDialog d = new JDialog(TestRunnerFrame.guiMainFrame);
		d.getRootPane().setWindowDecorationStyle(JRootPane.ERROR_DIALOG);
		ErrorTableModel errTM = new ErrorTableModel(errors);
		int[] sizes = errTM.getMaximumSize();
		JTable t = new JTable(new ErrorTableModel(errors));
		if (sizes != null) {
			for (int i = 0; i < sizes.length; i++) {
				t.getColumnModel().getColumn(i).setPreferredWidth(sizes[i]);
			}
		}

		// t.setPreferredSize(errTM.getWindowSize());
		JPanel panel = getErrorPanel(t, message);
		d.getContentPane().add(panel);
		// sets the size of the dialog box by the length of the strings in the
		// table
		d.setPreferredSize(new Dimension((int) (errTM.getWindowSize().width) + 200, 700));
		// sets the location of the window in the middle of the screen
		d.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - (errTM.getWindowSize().width + 200)) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - 700) / 2);
		return d;
	}

	/**
	 * Show an error dialog log it into the logger as level finest
	 * 
	 * 
	 * @param level
	 *            the title of the error dialog
	 * @param title
	 * @param message
	 */
	public static void showErrorDialog(String title, String message, ErrorLevel errorLevel) {
		WaitDialog.endWaitDialog();
		Logger.getLogger(ErrorPanel.class.getName()).log(Level.FINEST, message);
		ERROR_DIALOG = new ErrorDialog(title, message, errorLevel, false);
		ERROR_DIALOG.init();
	}
	/**
	 * Show an error dialog with cancel/OK options
	 * @param title dialog title
	 * @param message dialog message
	 * @param errorLevel error level
	 * @return
	 */
	public static boolean showErrorDialogOkCancel(String title, String message, ErrorLevel errorLevel) {
		WaitDialog.endWaitDialog();
		Logger.getLogger(ErrorPanel.class.getName()).log(Level.FINEST, message);
		ERROR_DIALOG = new ErrorDialog(title, message, errorLevel, true);
		ERROR_DIALOG.init();
		return ERROR_DIALOG.isCancel();
	}
	
	
	/**
	 * 
	 */
	public static void disposeErrorDialog() {
		if (ERROR_DIALOG != null){
			ERROR_DIALOG.dispose();
		}
	}
	
	public static void showErrorDialog(String title, Throwable t, ErrorLevel errorLevel) {
		showErrorDialog(title, StringUtils.getStackTrace(t), errorLevel);
	}

	public static void main(String[] args) {
		Vector<String[]> vec = new Vector<String[]>();
		vec.add(new String[] { "name", "number", "id" });
		vec.add(new String[] { "blbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbkiiiiiiiiiiiiiii", "23", "250" });
		vec.add(new String[] { "blbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbkiiiiiiiiiiiiiii", "23", "250" });
		vec.add(new String[] { "blbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbkiiiiiiiiiiiiiii", "23", "250" });
		vec.add(new String[] { "blbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbkiiiiiiiiiiiiiii", "23", "250" });
		vec.add(new String[] { "blbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbkiiiiiiiiiiiiiii", "23", "250" });
		vec.add(new String[] { "blbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbkiiiiiiiiiiiiiii", "23", "250" });
		String textMessage = "Fail to load part of the tests. Following are posible actions/causes:\n"
				+ " 1. Rebuild your tests project.\n" + " 2. Check that you are using updated jars.\n"
				+ " 3. Check that you are not missing any external jars\n"
				+ " 4. If the problematic tests are old tests remove them from the scenario.";
		JDialog d = getErrorDialog(vec, textMessage);
		d.pack();
		d.setVisible(true);

		// showErrorDialog("hello", "blabla");

	}

}

class ErrorTableModel extends DefaultTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8218378625962547278L;

	Vector<String[]> tableVector = null;

	public ErrorTableModel(Vector<String[]> table) {
		tableVector = table;
		if (table.size() == 0) {
			return;
		}
		setColumnIdentifiers((Object[]) table.elementAt(0));
		for (int i = 1; i < table.size(); i++) {
			addRow((Object[]) table.elementAt(i));
		}
	}

	/**
	 * gets the length of the longest string in the table times 6 in order to
	 * know the needed width for each cell
	 * 
	 * @return
	 */
	public int[] getMaximumSize() {
		int numberOfFields = tableVector.elementAt(0).length;
		int[] fieldLength = new int[numberOfFields];
		for (int i = 0; i < tableVector.size(); i++) {
			String[] s = tableVector.elementAt(i);
			for (int j = 0; j < numberOfFields; j++) {
				int strLength = s[j].length();
				if (fieldLength[j] < strLength * 6) {
					fieldLength[j] = strLength * 6;
					// System.out.println("field length"+fieldLength[j]);
				}

			}
		}
		return fieldLength;

	}

	/**
	 * get a dimension object from the table in order to know the table needed
	 * size
	 * 
	 * @return
	 */
	public Dimension getWindowSize() {
		int[] max = getMaximumSize();
		int tmp = 0;
		for (int i = 0; i < max.length; i++) {
			tmp += max[i];
		}
		Dimension d = new Dimension(tmp, tableVector.size() * 30);
		return d;

	}

}
