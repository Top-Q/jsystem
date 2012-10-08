/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import java.io.File;
import java.util.logging.Logger;

import jsystem.runner.agent.ProjectComponent;

import org.apache.commons.io.FileUtils;
/**
 * Service class which extracts jsystem automation project zip.
 * @author goland
 */
public class ProjectUnZip {
	
	private static Logger log = Logger.getLogger(ProjectUnZip.class.getName());
	private File projectClassesPath;
	private File projectZip;
	
	/**
	 * Constructs a <code>ProjectUnZip</code>
	 * @param testsClassesPath - extract destination
	 * @param projectZip - full path to project zip file
	 */
	public ProjectUnZip(File testsClassesPath,File projectZip) throws Exception {
		this.projectClassesPath = testsClassesPath;
		this.projectZip = projectZip;
		if (!projectZip.exists()){
			throw new IllegalArgumentException("Project zip file was not found. " + projectZip.getAbsolutePath());
		}
		if (!testsClassesPath.exists()){
			throw new IllegalArgumentException("Project directory was not found. " + testsClassesPath.getAbsolutePath());
		}
	}
	
	/**
	 * Perform actual extraction.
	 * @param components - array of project components to extract.
	 */
	public void unzipProject(ProjectComponent[] components) throws Exception{		
		File zipFileDirectory = File.createTempFile("project","");
		zipFileDirectory.delete();
		try {

			if (!zipFileDirectory.mkdirs()){
				throw new Exception("Failed creating temp folder for project transfer");
			}
			jsystem.utils.FileUtils.extractZipFile(projectZip, zipFileDirectory);
			for (ProjectComponent component:components){
				if (component.equals(ProjectComponent.classes)){
					extractClasses(zipFileDirectory);
					extractTestsCode(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.libs)){
					extractLibs(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.suts)){
					extractSut(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.scenarios)){
					extractScenarios(zipFileDirectory);
				}else
				if (component.equals(ProjectComponent.resources)){
					extractResources(zipFileDirectory);
				}
			}			
		}finally {
			jsystem.utils.FileUtils.deltree(zipFileDirectory);
		}
	}
	
	private void extractClasses(File zipFileDirectory) throws Exception {
		File zipDirectoryClassesDir = new File(zipFileDirectory,projectClassesPath.getName());
		if (!zipDirectoryClassesDir.exists()){
			log.fine("classes dir was not found in extracted project file. " + zipDirectoryClassesDir.getAbsolutePath());
			return;
		}
		
		File[] subFolders = projectClassesPath.listFiles();
		for (File file:subFolders){
			if (file.getName().equals("sut")){
				continue;
			}
			if (file.getName().equals("scenarios")){
				continue;
			}
			if (file.isDirectory()){
				FileUtils.deleteDirectory(file);
			}else {
				file.delete();
			}
		}
		
		subFolders = zipDirectoryClassesDir.listFiles();
		for (File file:subFolders){
			if (file.getName().equals("sut")){
				continue;
			}
			if (file.getName().equals("scenarios")){
				continue;
			}
			if (file.isDirectory()){
				FileUtils.copyDirectory(file, new File(projectClassesPath,file.getName()));
			}else {
				FileUtils.copyFile(file, new File(projectClassesPath,file.getName()));
			}
		}

	}
	
	private void extractLibs(File zipFileDirectory) throws Exception{
		File zipDirectoryLibDir = new File(zipFileDirectory,"lib");
		if (!zipDirectoryLibDir.exists()){
			log.fine("lib dir was not found in extracted project file. " + zipDirectoryLibDir.getAbsolutePath());
			return;
		}
		File projectDirectoryLibDir = new File(projectClassesPath.getParent(),"lib");
		FileUtils.deleteDirectory(projectDirectoryLibDir);
		FileUtils.copyDirectory(zipDirectoryLibDir,projectDirectoryLibDir);
	}

	private void extractResources(File zipFileDirectory) throws Exception{
		File zipDirectoryResourcesDir = new File(zipFileDirectory,"resources");
		if (!zipDirectoryResourcesDir.exists()){
			log.fine("lib dir was not found in extracted project file. " + zipDirectoryResourcesDir.getAbsolutePath());
			return;
		}
		File projectDirectoryResourcesDir = new File(projectClassesPath.getParent(),"resources");
		FileUtils.deleteDirectory(projectDirectoryResourcesDir);
		FileUtils.copyDirectory(zipDirectoryResourcesDir,projectDirectoryResourcesDir);
	}

	private void extractTestsCode(File zipFileDirectory) throws Exception{
		File zipDirectoryTestsCodeDir = new File(zipFileDirectory,"tests");
		if (!zipDirectoryTestsCodeDir.exists()){
			log.fine("tests code dir was not found in extracted project file. " + zipDirectoryTestsCodeDir.getAbsolutePath());
			return;
		}
		File projectDirectoryTestsCodeDir = new File(projectClassesPath.getParent(),"tests");
		FileUtils.deleteDirectory(projectDirectoryTestsCodeDir);
		FileUtils.copyDirectory(zipDirectoryTestsCodeDir,projectDirectoryTestsCodeDir);
	}

	private void extractSut(File zipFileDirectory) throws Exception{
		File zipDirectoryClassesDir = new File(zipFileDirectory,projectClassesPath.getName());
		File zipDirectorySutDir = new File(zipDirectoryClassesDir,"sut");
		if (!zipDirectorySutDir.exists()){
			log.fine("sut dir was not found in extracted project file. " + zipDirectorySutDir.getAbsolutePath());
			return;
		}
		File projectDirectorySutDir = new File(projectClassesPath,"sut");
		FileUtils.deleteDirectory(projectDirectorySutDir);
		FileUtils.copyDirectory(zipDirectorySutDir,projectDirectorySutDir);
	}
	
	private void extractScenarios(File zipFileDirectory) throws Exception{
		File zipDirectoryClassesDir = new File(zipFileDirectory,projectClassesPath.getName());
		File zipDirectoryScenariosDir = new File(zipDirectoryClassesDir,"scenarios");
		if (!zipDirectoryScenariosDir.exists()){
			log.fine("sut dir was not found in extracted project file. " + zipDirectoryScenariosDir.getAbsolutePath());
			return;
		}
		File projectDirectoryScenariosDir = new File(projectClassesPath,"scenarios");
		FileUtils.deleteDirectory(projectDirectoryScenariosDir);
		FileUtils.copyDirectory(zipDirectoryScenariosDir,projectDirectoryScenariosDir);
	}

}
