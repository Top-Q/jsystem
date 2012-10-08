package com.aqua.sanity.jsystem_properties_dialog;

import java.io.File;
import java.util.Properties;
import java.util.Vector;

import jsystem.extensions.analyzers.compare.CompareValues;
import jsystem.framework.DataType;
import jsystem.framework.FrameworkOptions;
import jsystem.guiMapping.JsystemMapping;
import jsystem.treeui.properties.GUIFrameworkOptions;
import jsystem.treeui.properties.JSystemPropertiesUtilities;
import analyzers.ComparePropertiesAnalyzer;

import com.aqua.general.JSysTestCaseOld;
import com.aqua.jsystemobject.CreateEnvFixtureOld;
import com.aqua.jsystemobject.JSystemClient.JsystemPropertiesDialogButton;

public class JSystemPropertiesTests extends JSysTestCaseOld {
	
	public final int propertyCounter = 1;
	public final int cellToCopyWithoutValue = propertyCounter * 2;
	public final int cellToCopyWithValue = propertyCounter * 3;

	public enum ValueFlag {
		NO_VALUE,
		SET_VALUE_1,
		SET_VALUE_2;
	}
	JsystemMapping jmap;

	
	// Constructor
	public JSystemPropertiesTests() {
		super();
		jmap = new JsystemMapping();
		setFixture(CreateEnvFixtureOld.class);
	}

	// Basic tests
	// ***************************************************************************************

	
	/**
	 * This test update a single property, and verify the change took place
	 * in the jsystem.properties file.
	 * The verification is done by reopen the dialog and read the changed property 
	 * as it was loaded from the file to the dialog.
	 * 1 - Load the dialog
	 * 2 - Read the value of the property to be change
	 * 3 - Set new value
	 * 4 - press "Save" button
	 * 5 - Reload the dialog
	 * 6 - Re-read the same property value
	 * 7 - Verify the last value is equals to the value that was set to the dialog.
	 * @throws Exception
	 */
	public void testSetValueAndVerify() throws Exception {
		FrameworkOptions frameworkOption = FrameworkOptions.HTML_SUMMARY_DISABLE;
		String propertyName = frameworkOption.toString();
		String value = "true";
		int returnCode = 0;
		
		jsystem.launch();
		
		// Get the initial value
		Properties singleProperty = jsystem.getDialogValues(frameworkOption);
		String initialValue = singleProperty.getProperty(frameworkOption.toString());
		
		// Set different new value
		if (initialValue.equals("true")) {
			returnCode = jsystem.setJsystemPropertyThroughDialog(propertyName, "false");
		} else {
			returnCode = jsystem.setJsystemPropertyThroughDialog(propertyName, "true");
		}
		
		// if the user asked to "Restore Defaults", open new client and server 
		if ( returnCode == 1 ) {
			super.setUp(); 
			jsystem.launch();
		}

		
		// Read new value
		singleProperty = jsystem.getDialogValues(frameworkOption);
		String newValue = singleProperty.getProperty(frameworkOption.toString());
		
		// Verify the change took place
		jsystem.setTestAgainstObject(newValue);
		jsystem.analyze(new CompareValues(value));
	}
	
	
	/**
	 * Restore System Default values into the dialog by pressing the "Restore Defaults" button
	 */
	public void testRestoreSystemDefault() throws Exception {
		jsystem.launch();
		jsystem.restoreSystemDefault();
	}
	
	/**
	 * This test change a set of properties values, and verify all changes were successfully 
	 * saved into jsystem.properties file
	 * 1 - Load the dialog
	 * 2 - Read set of Properties values from the dialog
	 * 3 - Set new value, to the set of Properties from previouse stage
	 * 4 - press "Save" button
	 * 5 - Reload the dialog
	 * 6 - Re-read the same properties values
	 * 7 - Verify the last values are equals to the value that were set to the dialog.
	 * @throws Exception
	 */
	public void testChangeDialogValuesAndSave() throws Exception {
		changeDialogValues(JsystemPropertiesDialogButton.SAVE);
	}

	
	/**
	 * This method change all properties, press the Restore Defaults button
	 * and verify the changes didn't take place, and that the jsystem.properties
	 * file contain the default values.
	 * 1 - Load the dialog
	 * 2 - Read set of Properties values from the dialog
	 * 3 - Set new value, to the set of Properties from previous stage
	 * 4 - press "Restore Defaults" button
	 * 5 - Reload the dialog
	 * 6 - Re-read the same properties values
	 * 7 - Verify the the dialog last values (=jsystem.properties file) contain default values
	 * @throws Exception
	 */
	public void testChangeDialogValuesAndRestoreDefault() throws Exception {
		changeDialogValues(JsystemPropertiesDialogButton.RESTORE_DEFAULTS);
	}

	/**
	 * This method change all properties, press the Cancel button,
	 * and verify the changes didn't take place.
	 * 1 - Load the dialog
	 * 2 - Read set of Properties values from the dialog
	 * 3 - Set new value, to the set of Properties from previouse stage
	 * 4 - press "Cancel" button
	 * 5 - Reload the dialog
	 * 6 - Re-read the same properties values
	 * 7 - Verify the the dialog last values are equals to the values from the first read.
	 * @throws Exception
	 */
	public void testChangeDialogValuesAndCancel() throws Exception {
		changeDialogValues(JsystemPropertiesDialogButton.CANCEL);
	}
	

	/**
	 * This method check the dialog behaviour.
	 * At first - The test asks for some of the properties values (= default values)
	 * Then, the test change those properties, press one of the following buttons.
	 * At last, the test read once again the properties values, and compare these values 
	 * to the values that were saved from the first stage.
	 * The test verify that we receive the expected values from the dialog. 
	 * ("Save", "Restore Defaults", "Cancel") according to the given button name
	 * @param buttonToPress - The name of the button to press.
	 * @throws Exception
	 */
	private void changeDialogValues(JsystemPropertiesDialogButton button) throws Exception {
		String buttonToPress = button.toString();
		report.report("Entering testChangeDialogValues()");
		
		// build a vector that contain all framework options
		GUIFrameworkOptions[] guiFrameworkOptions = new GUIFrameworkOptions[] {
			GUIFrameworkOptions.CHANGE_AGENT_DURING_RUN,
			GUIFrameworkOptions.EXIT_TIMEOUT,
			GUIFrameworkOptions.HTML_OLD_DIRECTORY,
			GUIFrameworkOptions.IGNORE_MEANINGFUL_NAME,
			GUIFrameworkOptions.LIB_DIRS,
			GUIFrameworkOptions.MAIL_SEND_TO,
			GUIFrameworkOptions.RUN_MODE
		};

		// build a vector that contain pairs of: FrameworkOption +  value, for all JSystem properties 
		Properties setOfNewPropertiesValues = new Properties();
		for (GUIFrameworkOptions option : guiFrameworkOptions){
			setOfNewPropertiesValues.put(option.getFrameworkOption().toString(), getValue(option));
		}
		
		
		// load the dialog.
		jsystem.launch();
	
		// Restore system defaults before start to run the test in order to get the Expected results".
		// Later on, the restore defaults button will be press once again according to the test flow.
		// After we press the "Restore Defaults" the dialog is close, and therefore we must re-open the runner for the test continuous. 
		if (buttonToPress.equals(jmap.getJSystemPropertiesSystemDefaultButtonName())) {
			jsystem.restoreSystemDefault();
			reloadRunner();
		}
		
		// Read the initial values from the dialog (Before changes)
		Properties oldProperties = jsystem.getDialogValues(convertGuiToFramworkOption(guiFrameworkOptions));

		
		// Change all dialog values
		int returnCode = jsystem.setJsystemPropertyThroughDialog(setOfNewPropertiesValues, button, true);
		
		// if the user asked to "Restore Defaults", open new client and server 
		if ( returnCode == 1 ) {
			reloadRunner();
		}

		// read the dialog values after reloading the dialog (After the changes)
		Properties newProperties = jsystem.getDialogValues(convertGuiToFramworkOption(guiFrameworkOptions));

		
		// Analyze the result
		if (buttonToPress.equals(jmap.getJSystemPropertiesSaveButtonName())) {
			jsystem.setTestAgainstObject(setOfNewPropertiesValues);
		} else {
			jsystem.setTestAgainstObject(oldProperties);
		}
		jsystem.analyze(new ComparePropertiesAnalyzer(newProperties));
	}
	
	private FrameworkOptions[] convertGuiToFramworkOption(GUIFrameworkOptions[] options){
		FrameworkOptions[] toReturn = new FrameworkOptions[options.length];
		for (int i=0 ; i<options.length ; i++){
			toReturn[i] = options[i].getFrameworkOption();
		}
		
		return toReturn;
	}
	
	private String getValue(GUIFrameworkOptions option){
		FrameworkOptions fOption = option.getFrameworkOption();
		if (fOption.getReserve() != null){
			return fOption.getReserve()[0];
		}
		switch (fOption.getDataType()){
		case BOOLEAN:
			return "true";
		case DIRECTORY:
			return "C:\\";
		case EMAIL:
			return "dror@gmail.com";
		case FILE:
			return "C:\\a.txt";
		case LIST:
			return "LIST";
		case TEXT:
			return "TEXT";
		case NUMERIC:
			return "0";
		case MAIL_LIST:
			return "dror@gmail.com;dror2@gmail.com";
		case MULTY_SELECT_LIST:
			return "Multi LIST";
		case PASSWORD:
			return "123456";
		case SEARCH_AND_LIST:
		case SEARCH_AND_MULTY_SELECT_LIST:
		}
		
		return null;
	}

	private void reloadRunner() throws Exception{
		super.setUp(); 
		jsystem.launch();
		jsystem.setMaskExit(false);
	}
	
	/**
	 * @param valueFlag - a flag that indicates if we would like the vector to hol
	 * @return
	 * @throws Exception
	 */
	public Properties getProperties(ValueFlag valueFlag) throws Exception {
		Properties properties = new Properties();
		GUIFrameworkOptions guiFrameworkOption;
		String key, value = null;
		
		for (FrameworkOptions frameworkOption: FrameworkOptions.values()) {
			guiFrameworkOption = GUIFrameworkOptions.valueOf(frameworkOption.name());
			if ( guiFrameworkOption.isExposeToDialog() ) {
				key = frameworkOption.toString();
 				
				switch (valueFlag) {
					case SET_VALUE_1:
						value = getCompatibleValue(frameworkOption, true);
						break;
					case SET_VALUE_2:
						value = getCompatibleValue(frameworkOption, false);
						break;
				}
				properties.put(key, value);
			}
		}
		return properties;
	}
	
	public FrameworkOptions[] getFrameworkOptions() throws Exception {
		Vector<FrameworkOptions> options = new Vector<FrameworkOptions>();
		FrameworkOptions[] frameworkOptions;
		GUIFrameworkOptions guiFrameworkOption;
		
		// Add all frameworkOptions that should be expose in the dialog into a vector
		for (FrameworkOptions frameworkOption: FrameworkOptions.values()) {
			guiFrameworkOption = GUIFrameworkOptions.valueOf(frameworkOption.name());
			if ( guiFrameworkOption.isExposeToDialog() ) {
				options.add(frameworkOption);
			}
		}
		
		// Fill up the frameworkOptions[] array
		int counter = options.size();
		frameworkOptions = new FrameworkOptions[counter];
		for (int i = 0; i < counter; i++) {
			frameworkOptions[i] = options.get(i);
		}
		return frameworkOptions;
	}

	
	

	/**
	 * This method receive a FrameworkOption object, and return a compatible string value 
	 * @param frameworkOptions - the farmeworkOption object
	 * @param returnFirstSet - A flag indicate which value to return (there are 2 options)
	 * @return a string value compatible with the FrameworkOption dataType
	 * @throws Exception
	 */
	public String getCompatibleValue(FrameworkOptions frameworkOptions, boolean returnFirstSet) throws Exception {
		DataType dataType = frameworkOptions.getDataType();
		String value = null;
		String classesDirectoryName1 = "C:\\Root1\\Classes";
		String classesDirectoryName2 = "C:\\Root2\\Classes";
		
		createFolder(classesDirectoryName1);
		createFolder(classesDirectoryName1 + "\\sut");
		createFolder(classesDirectoryName1 + "\\scenarios");
		createFolder(classesDirectoryName2);
		createFolder(classesDirectoryName2 + "\\sut");
		createFolder(classesDirectoryName2 + "\\scenarios");
		String fileName1 = classesDirectoryName1 + "\\file111.txt";
		String fileName2 = classesDirectoryName2 + "\\file222.txt";
		String[] itemList = null;
		String[] classesToSearch = null;
		
		switch (dataType) {
		case BOOLEAN:
			if (returnFirstSet) {
				value = "true";
			} else {
				value = "false";
			}
			break;
		case DIRECTORY:
			if (returnFirstSet) {
				value = classesDirectoryName1;
			} else {
				value = classesDirectoryName2;
			}
			break;
		case EMAIL:
			if (returnFirstSet) {
				value = "address111@email.com";
			} else {
				value = "address222@email.com";
			}
			break;
		case FILE:
			if (returnFirstSet) {
				value = fileName1;
			} else {
				value = fileName2;
			}
			break;
		case LIST:
			itemList = frameworkOptions.getReserve();
			value = getListSelection(itemList, returnFirstSet);
			break;
		case MAIL_LIST:
			if (returnFirstSet) {
				value = "address111@email.com;address111@email111.com";
			} else {
				value = "address222@email.com;address222@email222.com";
			}
			break;
		case MULTY_SELECT_LIST:
			itemList = frameworkOptions.getReserve();
			value = getListSelection(itemList, returnFirstSet);
			break;
		case NUMERIC:
			if (returnFirstSet) {
				value = "111";
			} else {
				value = "222";
			}
			break;
		case SEARCH_AND_LIST:
			classesToSearch = frameworkOptions.getReserve();
			itemList = JSystemPropertiesUtilities.getSearchResults(classesToSearch);
			value = getListSelection(itemList, returnFirstSet);
			break;
		case SEARCH_AND_MULTY_SELECT_LIST:
			classesToSearch = frameworkOptions.getReserve();
			itemList = JSystemPropertiesUtilities.getSearchResults(classesToSearch);
			value = getListSelection(itemList, returnFirstSet);
			break;
		case TEXT:
			if (returnFirstSet) {
				value = "Text_111";
			} else {
				value = "Text_222";
			}
			break;
		default:
			if (returnFirstSet) {
				value = "Value Set 1, for unknowen type";
			} else {
				value = "Value Set 2, for unknowen type";
			}
		}
		
		report.report(value);
		return value;
	}
	
	
	/**
	 * This method receive an array with list items, and return one of the items.
	 * @param itemList - An array of options to be present in the list
	 * @param returnFirstSet - A flag indicates if to return the first item or the last one
	 * @return one of the list items
	 */
	private String getListSelection(String[] itemList, boolean returnFirstSet) {
		int counter = itemList.length;
		String value = itemList[0];
		if ( (counter > 1) && (returnFirstSet == false) ){
			value = itemList[counter - 1];
		}
		return value;
	}
	

	/**
	 * Create a directory on the file system
	 * @param path - The path of the directory to be created
	 */
	public void createFolder(String path) {
		File directory = null;
		if (path == null) {
			report.report("createFolder() path=null");
		}

		directory = new File(path);

		if (! directory.exists()) {
			directory.mkdirs();
		} 
	}
	
	public void tearDown() throws Exception{
		jsystem.exit();
	}
	
}
