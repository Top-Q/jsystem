/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import java.io.File;
import java.util.logging.Logger;

import jsystem.runner.agent.ProjectComponent;

import org.apache.commons.io.FileUtils;
/**
 * Service class which creates jsystem automation project zip.
 * @author goland
 */
public class ProjectZip {
	
	private static Logger log = Logger.getLogger(ProjectZip.class.getName());
	
	private File projectClassesPath;
	
	/**
	 * Given automation project full path, returns project name as can be used by the agent.<br>
	 * Example: if project path is <i>c:\mydev\automation\myproject\classes</i> the method returns
	 *           myproject/classes 
	 */
	public static String getProjectNameFromClassesPath(File projectClassesFile) throws Exception{
		File baseDirectory = projectClassesFile.getParentFile().getParentFile();
		String projectRelativePath = jsystem.utils.FileUtils.getRelativePath(projectClassesFile, baseDirectory);
		projectRelativePath = jsystem.utils.FileUtils.replaceSeparator(projectRelativePath);
		return projectRelativePath;
	}
	
	/**
	 * Constructs a <code>ProjectZip</code> class.
	 * @param testsClassesPath - path to classes folder of the automation project to zip.
	 */
	public ProjectZip(File testsClassesPath) throws Exception{
		this.projectClassesPath = testsClassesPath;
	}
	
	/**
	 * Returns the path to project's zip file.
	 * @param components - project components to zip.
	 */
	public File zipProject(ProjectComponent[] components) throws Exception{
		
		File zipFileDirectory = File.createTempFile("project","");
		zipFileDirectory.delete();
		try {
			if (!zipFileDirectory.mkdirs()){
				throw new Exception("Failed creating temp folder for project transfer");
			}
			for (ProjectComponent component:components){
				if (component.equals(ProjectComponent.classes)){
					copyClasses(zipFileDirectory);
					copyTestsCode(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.libs)){
					copyLibs(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.suts)){
					copySut(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.scenarios)){
					copyScenarios(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.resources)){
					copyResources(zipFileDirectory);
				}
			}
			File zipFile = File.createTempFile("project",".zip");
			jsystem.utils.FileUtils.zipDirectory(zipFileDirectory.getAbsolutePath(),null,zipFile.getAbsolutePath());
			return zipFile;
		}finally {
			jsystem.utils.FileUtils.deltree(zipFileDirectory);
		}
	}
	
	private void copyClasses(File root) throws Exception{
		File classesFolder = getAndCreateClassesFolder(root);
		File[] subFolders = projectClassesPath.listFiles();
		for (File file:subFolders){
			if (file.getName().equals("sut")){
				continue;
			}
			if (file.getName().equals("scenarios")){
				continue;
			}
			if (file.isDirectory()){
				FileUtils.copyDirectory(file,new File(classesFolder,file.getName()));
			}else {
				FileUtils.copyFile(file,new File(classesFolder,file.getName()));
			}
		}
	}

	private void copyTestsCode(File root) throws Exception{
		File testsCodeFolderSrc = new File(projectClassesPath.getParent(),"tests");
		if (!testsCodeFolderSrc.exists()){
			log.fine("tests code folder was not found in project");
			return;
		}
		File testsCodeFolderDest = new File(root,"tests");
		if (!testsCodeFolderDest.exists() && !testsCodeFolderDest.mkdir()){
			throw new Exception("Failed creating tests code folder");
		}
		FileUtils.copyDirectory(testsCodeFolderSrc,testsCodeFolderDest);
	}

	private void copyLibs(File root) throws Exception{
		File libFolderSrc = new File(projectClassesPath.getParent(),"lib");
		if (!libFolderSrc.exists()){
			log.fine("lib folder was not found in project");
			return;
		}
		File libFolderDest = new File(root,"lib");
		if (!libFolderDest.exists() && !libFolderDest.mkdir()){
			throw new Exception("Failed creating lib folder");
		}
		FileUtils.copyDirectory(libFolderSrc,libFolderDest);
	}

	private void copyResources(File root) throws Exception{
		File resourcesFolder = new File(projectClassesPath.getParent(),"resources");
		if (!resourcesFolder.exists()){
			log.fine("resources folder was not found in project");
			return;
		}
		File resourcesFolderDest = new File(root,"resources");
		if (!resourcesFolderDest.exists() && !resourcesFolderDest.mkdir()){
			throw new Exception("Failed creating resources folder");
		}
		FileUtils.copyDirectory(resourcesFolder,resourcesFolderDest);
	}

	private void copySut(File root) throws Exception{
		File sutFolderSrc = new File(projectClassesPath,"sut");
		if (!sutFolderSrc.exists()){
			log.warning("sut folder was not found in project");
			return;
		}
		File classesFolder = getAndCreateClassesFolder(root);
		File sutFolderDest = new File(classesFolder,"sut");
		if (!sutFolderDest.exists() && !sutFolderDest.mkdir()){
			throw new Exception("Failed creating sut folder");
		}
		FileUtils.copyDirectory(sutFolderSrc,sutFolderDest);		
	}
	
	private void copyScenarios(File root) throws Exception{
		File scenariosFolderSrc = new File(projectClassesPath,"scenarios");
		if (!scenariosFolderSrc.exists()){
			log.warning("scenarios folder was not found in project");
			return;
		}
		File classesFolder = getAndCreateClassesFolder(root);
		File scenariosFolderDest = new File(classesFolder,"scenarios");
		if (!scenariosFolderDest.exists() && !scenariosFolderDest.mkdir()){
			throw new Exception("Failed creating scenarios folder");
		}
		FileUtils.copyDirectory(scenariosFolderSrc,scenariosFolderDest);
	}


	private File getAndCreateClassesFolder(File root) throws Exception {
		File classesFolder = new File(root,projectClassesPath.getName());
		if (classesFolder.exists()){
			return classesFolder;
		}
		if (!classesFolder.mkdir()){
			throw new Exception("Failed creating classes folder");
		}	
		return classesFolder;
	}
}
