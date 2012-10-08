/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.io.File;

import jsystem.framework.JSystemProperties;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * Utility class for handling test parameters of type <code>File</code>.
 * @author goland
 */
public class ParameterFileUtils {

	/**
	 * Invoked before opening the select file dialog and sets the initial 
	 * dialog folder.
	 * If path is empty returns project dir.
	 * If <code>path</code> is relative the absolute path will be
	 * project_dir/path.
	 * If <code>path</code> is absolute it will be returned as is.
	 */
	public static File getInitialPath(String path) {
		File currentDir;
		File projectPath =	new File(JSystemProperties.getCurrentTestsPath()).getParentFile();
		File selectedFile = new File(path);
		if (StringUtils.isEmpty(path)){
			currentDir	= projectPath; 
		}else 
		if (!selectedFile.isAbsolute()){
			currentDir = new File(projectPath,path);
		}else {
			currentDir = new File(path);
		}
		return currentDir;
	}
	
	/**
	 * If the folder that the user has selected is under project_dir,
	 * returns a relative path to project_dir , otherwise returns
	 * the input as is. 
	 */
	public static String convertUserInput(String path) {
		File projectPath =	
			new File(JSystemProperties.getCurrentTestsPath()).getParentFile();
		if (path.startsWith(FileUtils.getCannonicalPath(projectPath))){
			if (path.equals(FileUtils.getCannonicalPath(projectPath))){
				path = "./";
			}else {
				path = path.substring(FileUtils.getCannonicalPath(projectPath).length()+1);
			}
		}
		path = FileUtils.replaceSeparator(path);
		return path;
	}
	
	/**
	 * Invoked before setting test File parameter.
	 * If <code>path</code> is relative the method will return the absolute path 
	 * project_dir/path.
	 * If <code>path</code> is absolute it will be returned as is.
	 */
	public static File convertBeforeTestUpdate(String path){
		File f =new File(path);
		File projectPath =	
			new File(JSystemProperties.getCurrentTestsPath()).getParentFile();
		if (!f.isAbsolute()){
			f = new File(projectPath,path);
		}
		return f;
	}

}
