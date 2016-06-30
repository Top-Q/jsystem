/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.projectsync;

import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.projectsync.ProjectUnZip;
import jsystem.runner.projectsync.ProjectZip;
import junit.framework.SystemTestCase;
import java.io.File;

import org.junit.Ignore;

/**
 * Unit test for MD5 calculation.
 * project1 is identical to project2
 * project3 is different from them in the following files:
 * 1. classes/scenarios changed level0.xml scenario
 * 2. in classes, deleted the class BaseClassToInherit.class
 * 
 * @author goland
 *
 */
@Ignore("Agent mechanism is deprected")
public class ProjectZipTest extends SystemTestCase {
	
	private ProjectZip projectZip;
	private ProjectUnZip projectUnZip;
	private File resourcesRoot = new File("resources");
	private File project1TestClassesFolder = new File(resourcesRoot,"project1/classes");
	
	public void setUp() throws Exception {
		projectZip = new ProjectZip(project1TestClassesFolder);
	}
	public void testProjectZip() throws Exception {
		File zipFile = projectZip.zipProject(new ProjectComponent[]{ProjectComponent.classes,ProjectComponent.libs,ProjectComponent.scenarios,ProjectComponent.suts});
		assertTrue(zipFile.exists());
		File dest = new File(resourcesRoot,"project5/classes");
		dest.mkdirs();
		projectUnZip = new ProjectUnZip(dest,zipFile);
		projectUnZip.unzipProject(null);
		

	}

	public File getResourcesRoot() {
		return resourcesRoot;
	}
	public void setResourcesRoot(File resourcesRoot) {
		this.resourcesRoot = resourcesRoot;
	}

}
