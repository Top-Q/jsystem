/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.properties;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.DataType;
import jsystem.treeui.properties.GUIFrameworkOptions.Group;

/**
 *	A class that describe a single property.
 *
 * @author Dror Voulichman
 */
public class JSystemProperty {
	private final String split = "=";
	private Group group;
	private DataType dataType;
	private FrameworkOptions frameworkOptions = null;
	private String[] reserve = null;
	private String description, stringName, value, longDescription, example;
	private boolean reloadRunnerRequire, dirty, saveDefaultValueToFile;
	private Object defaultVlaue;

	
	/**
	 * constructor
	 * @param stringName - A String presentation of the property
	 * @param group - The Tab in which the property will be displayed
	 * @param description - A short description
	 * @param longDescription - A detailed description
	 * @param dataType - The type of the data (Used to load a compatible editor in the JSystemPropertiesDialog
	 * @param defaultValue 
	 * @param reloadRunnerRequire - When set to true, indicates that any change of this property require to reload the ruuner
	 * @param value - The value of the property
	 * @param reserve - Some Extra information for some of the properties:
	 * 		For properties from type LIST / MULTI_SELECT_LIST, the reserve field hold the options of the list
	 * 		For properties from type SEARCH.... - the reserve field holds the names of the classes to search for.
	 * @param saveDefaultValueToFile - if true and the property value was changed to the default value
	 *        the property will be written into the jsystem.properties file.
	 */
	public JSystemProperty(String stringName, Group group, String description, String longDescription, String example, DataType dataType, Object defaultValue, boolean reloadRunnerRequire, String value, String[] reserve, boolean saveDefaultValueToFile){
		this.stringName = stringName;
		this.group = group;
		this.description = description;
		this.longDescription = longDescription;
		this.example = example;
		this.dataType = dataType;
		this.defaultVlaue = defaultValue;
		this.reloadRunnerRequire = reloadRunnerRequire;
		this.value = value;
		this.reserve = reserve;
		this.saveDefaultValueToFile = saveDefaultValueToFile;
		dirty = false;
	}
	
	/**
	 * constructor
	 * @param stringName - A String presentation of the property
	 * @param group - The Tab in which the property will be displayed
	 * @param description - A short description
	 * @param longDescription - A detailed description
	 * @param dataType - The type of the data (Used to load a compatible editor in the JSystemPropertiesDialog
	 * @param defaultValue 
	 * @param reloadRunnerRequire - When set to true, indicates that any change of this property require to reload the ruuner
	 * @param value - The value of the property
	 * @param reserve - Some Extra information for some of the properties:
	 * 		For properties from type LIST / MULTI_SELECT_LIST, the reserve field hold the options of the list
	 * 		For properties from type SEARCH.... - the reserve field holds the names of the classes to search for.
	 */
	public JSystemProperty(String stringName, Group group, String description, String longDescription, String example, DataType dataType, Object defaultValue, boolean reloadRunnerRequire, String value, String[] reserve){
		this(stringName, group, description, longDescription, example, dataType, defaultValue, reloadRunnerRequire, value, reserve, true);
	}
	
	
	// Getters and Setters
	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public FrameworkOptions getFrameworkOptions(){
		return frameworkOptions;
	}
	
	public void setFrameworkOptions(FrameworkOptions frameworkOptions){
		this.frameworkOptions = frameworkOptions;
	}
	
	public String getStringName() {
		return stringName;
	}
	
	public void setStringName(String stringName) {
		this.stringName = stringName;
	}
	
	public String getSplit() {
		return split;
	}
	

	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	
	public DataType getDataType() {
		return dataType;
	}
	
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
	public String[] getReserve(){
		return reserve;
	}
	
	public void setReserve(String[] reserve) {
		this.reserve = reserve;
	}

	public boolean isReloadRunnerRequire() {
		return reloadRunnerRequire;
	}

	public void setReloadRunnerRequire(boolean reloadRunnerRequire) {
		this.reloadRunnerRequire = reloadRunnerRequire;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getExample() {
		return example;
	}

	public void setExample(String example) {
		this.example = example;
	}
	
	
	public String getDefaultVlaue() {
		return defaultVlaue.toString();
	}

	public void setDefaultVlaue(String defaultVlaue) {
		this.defaultVlaue = defaultVlaue;
	}

	public boolean isSaveDefaultValueToFile() {
		return saveDefaultValueToFile;
	}

	public void setSaveDefaultValueToFile(boolean saveDefaultValueToFile) {
		this.saveDefaultValueToFile = saveDefaultValueToFile;
	}
	
}
