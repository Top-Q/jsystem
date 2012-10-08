/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import java.io.File;
import java.util.HashMap;

import jsystem.framework.TestProperties;
import jsystem.framework.scenario.Parameter;
import junit.framework.SystemTestCase;

public class DynamicUpdateDemonstration extends SystemTestCase {
	
	private File file;
	private File file2;

	private boolean recursive = true;
	
	/**
	 * Compares the content of files <code>file1</code>,<code>file2</code>.
	 * If file and file2 are folders compares the content of the files in the folders.
	 * Additional folder comparison parameters:
	 * recursive - whether to do a recursive compare.
	 * between the folders
	 */
	@TestProperties(name="Compares the content of folders ${file},${file2}")
	public void testCompareFiles() throws Exception{

	}
	
	/**
	 * <a href="http://www.ynet.co.il">YNET</a>
	 */
	public void testWithLinkInJavaDoc(){
		
	}
	/**
	 * 
	 * @param map
	 * @param methodName
	 * @throws Exception
	 */
	public void handleUIEvent(HashMap<String,Parameter> map,String methodName) throws Exception {
		if (!"testCompareFiles".equals(methodName)){
			return;
		}
		
		/**
		 * 
		 */
		Parameter fileParameter = map.get("File"); 
		Parameter file2Parameter = map.get("File2");
		Parameter recursiveParameter =map.get("Recursive");
		
		/**
		 * 
		 */
		if (fileParameter.getValue() == null){
			fileParameter.setValue("");
		}
		if (file2Parameter.getValue() == null){
			file2Parameter.setValue("");
		}

		File fileObject = new File(fileParameter.getValue().toString());
		File file2Object = new File(file2Parameter.getValue().toString());
		
		/**
		 * 
		 */
		if (fileObject.isDirectory()){
			fileParameter.setDescription("Source folder");
		}else {
			fileParameter.setDescription("Source file");
		}

		if (file2Object.isDirectory()){
			file2Parameter.setDescription("Source folder");
		}else {
			file2Parameter.setDescription("Source file");
		}

		/**
		 * 
		 */
		if (file2Object.exists() && file2Object.isDirectory() && fileObject.exists() && fileObject.isDirectory()){
			recursiveParameter.setEditable(true);
		}else {
			recursiveParameter.setEditable(false);
		}
	}
	
	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
	}


	public File getFile2() {
		return file2;
	}


	public void setFile2(File file2) {
		this.file2 = file2;
	}


	public boolean isRecursive() {
		return recursive;
	}


	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}


}
