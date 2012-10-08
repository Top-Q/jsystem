/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jsystem.framework.TestRunnerFrame;
import jsystem.framework.scenario.ParameterFileUtils;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;

/**
 * Parameters table component for FileChooser.
 * @author goland
 */
public class ParametersTableFileChooser extends BeanParameterElement{

	private static final long serialVersionUID = 1L;
	
	public static String SELECT_FILE_BUTTON_NAME = "FILE_CHOOSE_SELECT_FILE";
	
	private JTextField field;
	private JButton openFileChooser;
	private JButton launchButton;
	public ParametersTableFileChooser(String file){
		super();
		setLayout(new BorderLayout());
		field = new JTextField();
		Dimension dim = field.getSize();
		dim.width = 90;
		field.setSize(dim);
		field.setText(file == null ? "" : file);
		setSelectedFile(file);
		add(field,BorderLayout.CENTER);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		openFileChooser  = new JButton("...");
		openFileChooser.setToolTipText("Parameter File Browser");
		openFileChooser.setName(SELECT_FILE_BUTTON_NAME);
		openFileChooser.addActionListener(this);
		launchButton  = new JButton("o");
		launchButton.setToolTipText("Launch file");
		launchButton.setName(SELECT_FILE_BUTTON_NAME);
		launchButton.addActionListener(this);
		panel.add(openFileChooser, BorderLayout.WEST);
		panel.add(launchButton, BorderLayout.EAST);
		add(panel,BorderLayout.EAST);	
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(openFileChooser)){
			File currentDir = ParameterFileUtils.getInitialPath(getSelectedFile());
			JFileChooser fc = new JFileChooser(currentDir);
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setMultiSelectionEnabled(false);
			fc.setApproveButtonText("Select");
			fc.setDialogTitle("Select File");
			if (fc.showDialog(TestRunnerFrame.guiMainFrame, "Select") != JFileChooser.APPROVE_OPTION) {
				return;
			}
			field.requestFocusInWindow();
			String path = fc.getSelectedFile().getPath();
			path = ParameterFileUtils.convertUserInput(path);
			setSelectedFile(path);
		} else if(e.getSource().equals(launchButton)){
			File filePath = ParameterFileUtils.getInitialPath(getSelectedFile());
			if(filePath == null){
				ErrorPanel.showErrorDialog("File path is not set","Please select a file to be launched", ErrorLevel.Warning);
				return;
			}
			if(!filePath.exists()){
				ErrorPanel.showErrorDialog("File does not exit (" + filePath.getAbsolutePath() +")","Please select a file to be launched", ErrorLevel.Warning);
				return;
			}
			try {
				if(System.getProperty("os.name").toLowerCase().contains("windows")){
					Runtime.getRuntime().exec(new String[]{"cmd","/C", filePath.getAbsolutePath()}, null, new File(System.getProperty("user.dir")));
				} else {
					Runtime.getRuntime().exec(new String[]{filePath.getAbsolutePath()}, null, new File(System.getProperty("user.dir")));
				}
			} catch (IOException e1) {
				ErrorPanel.showErrorDialog("Error in file launch process",e1, ErrorLevel.Warning);
				return;
			}
			parameterChanged();
		}
	}

	public synchronized void addFocusListener(FocusListener l) {
		field.addFocusListener(l);
	}
	
	public String getSelectedFile() {
		return field.getText();
	}

	public void setSelectedFile(String selectedFile) {
		this.field.setText(selectedFile);
	}
    
	public void repaint() {
		field.repaint();
    }
}
