/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import java.io.File;
import java.util.HashMap;

import jsystem.framework.RunProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.scenario.Parameter;
import junit.framework.SystemTestCase;

public class AdvancedTestParametersDemonstration extends SystemTestCase {

	/**
	 * Demo type
	 */
	public enum DemoType{
		enabledisable,
		hide;
	}

	private DemoType demoType = DemoType.hide;
	/**
	 * File comparison
	 */
	public enum CompareBy{
		content,
		attributes,
		contentAndAttributes;
	}

	private CompareBy compareBy = CompareBy.content;
	
	//compare by content
	private boolean binary;
	
	//compare attributes
	private boolean compareBySize;
	private boolean compareByCreationDate;
	private boolean compareByModifiedDate;
	private boolean ignoreFileNameCase;

	private String fileType;
	
	private File file;
	private File file2;
	/**
	 * @params.include file
	 */
	public void testFileParameter() throws Exception {
		RunProperties.getInstance().setRunProperty("FileParameterTest_file", getFile().getName());
	}

	/**
	 * Compares the content of folders <code>file1</code>,<code>file2</code>.
	 * see {@link #testCompareFiles()} for comparison rules parameters.
	 * Additional folder comparison parameters:
	 * recursive - whether to check comaprison recursivelly.
	 * between the folders
	 * 
	 * If folders path contain ${envVariable}, ${envVariable} is replaced with the
	 * value of the environment variable 'envVariable' on the remote machine
	 *   
	 * @params.include file,file2,compareBy,binary,compareBySize,compareByCreationDate,
	 * compareByModifiedDate,fileType,ignoreFileNameCase,demoType 
	 */
	@TestProperties(name="Compares the content of folders ${file},${file2}")
	public void testCompareFolders() throws Exception{
		if (getFile() != null){
			RunProperties.getInstance().setRunProperty("File", getFile().getName());
		}
		if (getFile2() != null){
			RunProperties.getInstance().setRunProperty("File2", getFile2().getName());
		}
		RunProperties.getInstance().setRunProperty("compareBy", ""+getCompareBy());
		RunProperties.getInstance().setRunProperty("binary", ""+isBinary());
		RunProperties.getInstance().setRunProperty("compareBySize", ""+isCompareBySize());
		RunProperties.getInstance().setRunProperty("compareByCreationDate", ""+isCompareByCreationDate());
		RunProperties.getInstance().setRunProperty("compareByModifiedDate", ""+isCompareByModifiedDate());
		RunProperties.getInstance().setRunProperty("fileType", ""+getFileType());
		RunProperties.getInstance().setRunProperty("ignoreFileNameCase", ""+isIgnoreFileNameCase());
		RunProperties.getInstance().setRunProperty("demoType", ""+getDemoType());
	}
	
	
	public void handleUIEvent(HashMap<String,Parameter> map,String methodName) throws Exception {
		if (!"testCompareFolders".equals(methodName) ){
			return;
		}
		boolean enableDisable = map.get("DemoType").getValue().equals(DemoType.enabledisable.toString());
		setVisible(map, enableDisable);
		Parameter param = map.get("CompareBy");
		if (Enum.valueOf(CompareBy.class,param.getValue().toString()) == CompareBy.attributes){
			toggleCompareByContent(map,false);
			toggleCompareByAttribute(map,true);
			param.setDescription("Compares files by file attributes (creation date etc')");
			map.get("FileType").setOptions(new String[]{"bin","exe","doc"});
			map.get("FileType").setValue("bin");
			param.setSection("Compare Type");
			
		}else
		if (Enum.valueOf(CompareBy.class,param.getValue().toString()) == CompareBy.content){
			toggleCompareByAttribute(map,false);
			toggleCompareByContent(map,true);
			param.setDescription("Compares files by comparing their content");
			param.setSection("General");
			map.get("FileType").setOptions(new String[]{"txt"});
			map.get("FileType").setValue("txt");
			
		}else{
			toggleCompareByContent(map,true);
			toggleCompareByAttribute(map,true);
			param.setDescription("Compares files by both attributes and content");
			param.setSection("Compare Type");
			map.get("FileType").setOptions(new String[]{"txt","bin","exe","doc"});
		}
	}

	private void toggleCompareByContent(HashMap<String, Parameter> map,boolean byContent) {
		boolean enableDisable = map.get("DemoType").getValue().equals(DemoType.enabledisable.toString());
		if (enableDisable){
			map.get("Binary").setEditable(byContent);
		}else {
			map.get("Binary").setVisible(byContent);
		}
	}
	private void toggleCompareByAttribute(HashMap<String, Parameter> map,boolean byAttribute){
		boolean enableDisable = map.get("DemoType").getValue().equals(DemoType.enabledisable.toString());
		if (enableDisable){
			map.get("CompareBySize").setEditable(byAttribute);
			map.get("CompareByCreationDate").setEditable(byAttribute);
			map.get("CompareByModifiedDate").setEditable(byAttribute);
			map.get("IgnoreFileNameCase").setEditable(byAttribute);
		}else {
			map.get("CompareBySize").setVisible(byAttribute);
			map.get("CompareByCreationDate").setVisible(byAttribute);
			map.get("CompareByModifiedDate").setVisible(byAttribute);
			map.get("IgnoreFileNameCase").setVisible(byAttribute);
		}
	}
	
	private void setVisible(HashMap<String, Parameter> map,boolean show){
		map.get("CompareBySize").setVisible(show);
		map.get("CompareByCreationDate").setVisible(show);
		map.get("CompareByModifiedDate").setVisible(show);
		map.get("IgnoreFileNameCase").setVisible(show);
		map.get("Binary").setVisible(show);
	}
	
	public File getFile() {
		return file;
	}

	/**
	 * @section Files
	 */
	public void setFile(File file) {
		this.file = file;
	}

	public boolean isBinary() {
		return binary;
	}

	public void setBinary(boolean binary) {
		this.binary = binary;
	}

	public boolean isCompareByCreationDate() {
		return compareByCreationDate;
	}

	public void setCompareByCreationDate(boolean compareByCreationDate) {
		this.compareByCreationDate = compareByCreationDate;
	}

	public boolean isCompareByModifiedDate() {
		return compareByModifiedDate;
	}

	public void setCompareByModifiedDate(boolean compareByModifiedDate) {
		this.compareByModifiedDate = compareByModifiedDate;
	}

	public boolean isCompareBySize() {
		return compareBySize;
	}

	public void setCompareBySize(boolean compareBySize) {
		this.compareBySize = compareBySize;
	}

	public boolean isIgnoreFileNameCase() {
		return ignoreFileNameCase;
	}

	public void setIgnoreFileNameCase(boolean ignoreFileNameCase) {
		this.ignoreFileNameCase = ignoreFileNameCase;
	}

	public CompareBy getCompareBy() {
		return compareBy;
	}

	public void setCompareBy(CompareBy compareBy) {
		this.compareBy = compareBy;
	}

	public DemoType getDemoType() {
		return demoType;
	}

	/**
	 * @section Demo Type
	 */
	public void setDemoType(DemoType demoType) {
		this.demoType = demoType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String[] getFileTypeOptions() {
		return new String[0];
	}

	public String[] sectionOrder() {
		return new String[]{"Files","Compare Type","General","Demo Type"};
	}

	public File getFile2() {
		return file2;
	}

	/**
	 * @section Files
	 */
	public void setFile2(File file2) {
		this.file2 = file2;
	}

}
