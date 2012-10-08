/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.utilities;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.framework.common.CommonResources;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.TestRunner;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.StringUtils;

public class ApplicationUtilities {
	public static String invalidClassesDirectoryMessage = 
		"The given workspace is missing required sut and scenarion libraries. \n" +
		"These libraries are needed for runner execution. \n" +
		"Please select a different workspace or create necessary folders.";
	
	
	/**
	 * chooseClassesDirectory() method can be invoke for two different cases:
	 * 1 - During runner loading - Call this method with the classes path exists in jsystem.properties file
	 * 2 - When the user chooses to switch project - Call this method with the classes path = null.
	 * 
	 * At first stage we check if the path parameter is valid. in case this method has been called during Runner loading, and the 
	 * jsystem.properties already had a valid path, we return this path and exit the method.
	 * If path=null (the user pressed the changeProject button), or the path in the jsystem.properties is invalid,
	 * We open the file chooser dialog inside a loop until one of the following:
	 * 1 - The user choose a valid classes directory
	 * 2 - The user chooses to exit
	 * 
	 * If the user choose an invalid directory, a pop up error message inform the user that the selected directory is invalid.
	 * If the user chooses to exit the system then:
	 * 		If this method was called during Runner loading, the runner cannot be loaded and therefore we exit with error code = 1
	 * 		If this method was called from "Switch Project" - We return null (which ends closing the change directory dialog.
	 * 
	 * @param path
	 * 		path contain the value of the test.dir property from the jsystem.properties file (in case we call this method during Runner loading)
	 * 		path = null if the user pressed the "Change Project" button
	 * @param mustChooseProject:
	 * 		If we call this method during Runner loading mustChooseProject = true.
	 * 		If we call this method from the switch project button, mustChooseProject = false
	 * @return	null if path is incorrect or cancel was selected
	 */
	public static String chooseClassesDirectory(String path, boolean mustChooseProject) {
		if (verifyClassesDirectory(path)){
			return path;
		}
		
		String newPath = null;
		String testDirectory = JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER);
		
		String currentDir = (StringUtils.isEmpty(testDirectory))? System.getProperty("user.dir") : testDirectory;
		
		JFileChooser fc = new JFileChooser(currentDir);
		File f = new File(currentDir);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle(JsystemMapping.getInstance().getSwitchProjectWin());
		
		String exitString = mustChooseProject? "Exit" : "Continue with current Project";
		
		boolean validDirectory = false;
		while (! validDirectory) {
			fc.setCurrentDirectory(f);
			if (fc.showDialog(TestRunnerFrame.guiMainFrame,JsystemMapping.getInstance().getSwitchProjectSaveButton()) != JFileChooser.APPROVE_OPTION ) {
				// handle cancel on file chooser
				if (!mustChooseProject){ // runner is already active
					return null;
				}
				// runner is starting
				JOptionPane.showMessageDialog(
					fc,
					"A project must be selected. Please restart the runner and select a project. \nYou can set tests.dir property manualy In the jsystem.properties file",
					"Setting tests directory",
					JOptionPane.WARNING_MESSAGE);
				
				throw new RuntimeException("Project was not selected");
				
			} else { // a directory was chosen in the file chooser
				
				newPath = fc.getSelectedFile().getPath();
				validDirectory = verifyClassesDirectory(newPath);
				if (! validDirectory) {
					ImageIcon icon = ImageCenter.getInstance().getImage(ImageCenter.ICON_WARNING);
					String title = "Invalid Classes Directory";
					String[] options = new String[]{"Choose a different directory","Continue",exitString};
					String message = "The given workspace is missing required sut and scenarion libraries. \n" +
									 "These libraries are needed for runner execution. \n" +
									 "Please select a different workspace or create necessary folders.";			
					
					int res  = JOptionPane.showOptionDialog(fc,message, title, 	
			   			  JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE, icon, options , "Choose a different directory");
					
					if (res == 1){
						validDirectory = true;
					} else if (res == 2) {
						if (mustChooseProject){
							TestRunner.treeView.getRunner().exit();
						}else{
							return null;
						}
					}
				}
			}
		} // while
		return newPath;
	}
	
	public static boolean verifyClassesDirectory(String path) {
		if (path == null){
			return false;
		}
		boolean sutFolderExists, scenariosFolderExists;
		
		File selectedDirectory = new File(path);
		String[] list = selectedDirectory.list();
		sutFolderExists = searchFor(list, "sut");
		scenariosFolderExists = searchFor(list, "scenarios");
		
		return sutFolderExists && scenariosFolderExists;
	}
	
	private static boolean searchFor(String[] list, String item) {
		boolean result = false;
		if (list == null){
			return false;
		}
		for (int i = 0; i < list.length; i++) {
			if ( list[i].equals(item) ) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public static boolean isNumric(String strValue) {
		boolean isNumeric = true;
		try {
			Integer.parseInt(strValue);
		} catch (Exception e) {
			isNumeric = false; 
		}
		return isNumeric;
	}
	
	/**
	 * This method receive a long string that contain a list of email addresses separated by ';'
	 * and verify all mail addresses are valid
	 * @param value - the mail list entered by the user
	 * @return: true - if all mail addresses are valid. false - otherwise
	 */
	public static boolean isEmailList(String value) {
		String[] addresses = value.split(CommonResources.DELIMITER);
		
		for (int i = 0; i < addresses.length; i++) {
			if (! isEmailAddress(addresses[i])) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method verify that the given string parameter contain a valid email address
	 * @param value - the email address that was entered by the user
	 * @return true if the given string is a valid email address, false - otherwise.
	 */
	public static boolean isEmailAddress(String value) {
		Character lastChar = value.charAt(value.length()-1);
		Character firstChar = value.charAt(0);
		
		// Verify the string contain only letters, "@" and "." (No other characters
		if (value.length() < 5) {
			return false;
		}
		
		// check that no other characters then letters numbers and the following special characters: {'@', '.', '_', '-'}
		for (int index = 0; index < value.length(); index++) {
			if (validateCharacter(value.charAt(index)) == false) {
				return false;
			}
		}

		// Check that "." appear in the string
		if ( ! value.contains(".") ) {
			return false;
		}
		
		// Check that "@" appear in the string
		if ( ! value.contains("@") ) {
			return false;
		}

		// Check that "@" appear only once
		int atIndex = value.indexOf("@");
		if (atIndex != value.lastIndexOf("@")) {
			return false; // more then one appearance of "@"
		}
		
		
		// check that the "@" comes before the last "." and that there is at list one letter between them 
		if (value.lastIndexOf(".") - atIndex < 2) {
			return false;
		}
			
		// Check that the first character is a letter
		if (! Character.isLetter(firstChar)) {
			return false;
		}
		
		// Check that the last character is a letter
		if (! Character.isLetter(lastChar)) {
			return false;
		}
		
		// If we arrive here it means that the string is a legal Email address
		return true;
	}

	/**
	 * This method receive a character and verify it is a valid Email character
	 * @param currentChar
	 * @return true if the received character can be part of an Email address, false - otherwise
	 */
	private static boolean validateCharacter(Character currentChar){
		Character[] allowCharacters = new Character[]{'@', '.', '_', '-'};
		boolean validCharacter = false;
		
		if ( Character.isLetter(currentChar) || Character.isDigit(currentChar) ) {
			validCharacter = true;
		} else {
			for (int index = 0; index < allowCharacters.length; index++) {
				if (currentChar == allowCharacters[index]) {
					validCharacter = true;
				}
			}
		}
		return validCharacter;
	}	

}
