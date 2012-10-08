/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.build;

import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.utils.AntExecutor;
import jsystem.utils.FileUtils;
import jsystem.utils.exec.Command;

public class BuildUtils {

	private static Logger log = Logger.getLogger(BuildUtils.class.getName());

	public static void compile(String srcDir, String classDir, String classpath, String include) throws Exception {
		Properties p = new Properties();
		p.setProperty("dist", classDir);
		p.setProperty("compileSrc", srcDir);
		p.setProperty("include", include);
		p.setProperty("classpath", classpath);
		p.setProperty("user.dir", System.getProperty("user.dir"));
		Command cmd = AntExecutor.executeAnt(CommonResources.getAntDirectory(), (BuildUtils.class).getClassLoader()
				.getResourceAsStream("jsystem/utils/build/buildTasks.xml"), p, "compile");
		log.fine(cmd.getStd().toString());
	}

	/**
	 * Used to build regression distribution. Following are the script
	 * parameters:<br>
	 * 1. export.src - if set will run the export of the tests sources.<br>
	 * 2. export.classes - export tests classes.<br>
	 * 3. export.so - export system objects.<br>
	 * 4. export.runner - export the runner.<br>
	 * 5. out.dir - the out dir used to build the delevery.<br>
	 * 6. src.dir - the tests sources directory.<br>
	 * 7. class.dir - the classes directory.<br>
	 * 8. so.lib - the system objects directory.<br>
	 * 9. zip.file - the target zip file.<br>
	 * 10. runner.dir - the runner dir.
	 * @param exportSut if True will export Sut folder
	 * @param exportScenarios if True will export Scenarios folder
	 * 
	 */
	public static void export(boolean exportSrc, boolean exportSut, boolean exportScenarios,boolean exportClasses,boolean exportRunner,
			boolean exportJdk, boolean exportLog, String outDir, String srcDir,String classDir,String runnerDir, String jdkDir, String resourcesDir, String zipFile)
			throws Exception {
		Properties p = new Properties();
		
		p.setProperty("export.src", "true");
		p.setProperty("src.dir", srcDir);
		
		// Exclude Sut\Scenarios from both source and classes
		StringBuffer excludeDirectory = new StringBuffer();;
		
		if (exportSrc) {
			p.setProperty("export.tests", "true");
			p.setProperty("src.dir", srcDir);
			// Checking if resources folder exists, only if true the
			// export_resources method will be executed
			if (new File(resourcesDir).exists()){
				p.setProperty("export.resources", "true");
				p.setProperty("resources.dir", resourcesDir);
			}
		}else{
			String[] dirs = FileUtils.listDirs(classDir);
			for (String dir : dirs){
				if (!dir.equals("sut") && !dir.equals("scenarios")){
					excludeDirectory.append(" ");
					excludeDirectory.append(dir);
					excludeDirectory.append("\\");
				}
			}
		}
		
		if (exportSut){
			p.setProperty("export.suts", "true");
		}
		if (exportScenarios){
			p.setProperty("export.scenarios", "true");
		}
		if (!exportSut){
			excludeDirectory.append(" sut\\");
		}
		if (!exportScenarios){
			excludeDirectory.append(" scenarios\\");
		}
		p.setProperty("exclude.directories",excludeDirectory.toString());

		if (exportClasses) {
			p.setProperty("export.classes", "true");
			p.setProperty("classes.dir", classDir);
			p.setProperty("export.lib", "true");						
			p.setProperty("lib.dir", classDir+"/../"+"lib");//lib and classes are in the same directory.
		}
		if (exportRunner) {
			p.setProperty("export.runner", "true");
			p.setProperty("runner.dir", runnerDir);
		}
		if (exportJdk) {
			p.setProperty("export.jdk", "true");
			p.setProperty("jdk.dir", jdkDir);
		}
		if (exportLog) {
			p.setProperty("runner.dir", runnerDir);
			p.setProperty("export.log", "true");
		}
		p.setProperty("basedir", System.getProperty("user.dir"));
		p.setProperty("out.dir", System.getProperty("user.dir") + File.separator + "tmp");
		if (!zipFile.endsWith(".zip")){
			zipFile += ".zip";
		}
		p.setProperty("zip.file", zipFile);
		Command cmd = AntExecutor.executeAnt(CommonResources.getAntDirectory(), BuildUtils.class.getClassLoader()
				.getResourceAsStream("jsystem/utils/build/buildTasks.xml"), p, "export");
		log.fine(cmd.getStd().toString());
	}
	
	public static void importProject(boolean importSrc, boolean importSut, boolean importScenarios,boolean importLib,boolean deleteTests,
			boolean deleteSuts, boolean deleteScenarios, boolean deleteLib, String outDir, String srcDir, String classDir, String resourcesDir, String zipFile)
			throws Exception {
		Properties p = new Properties();
		if (importSrc) {
			p.setProperty("import.tests", "true");
			if (deleteTests){
				p.setProperty("delete.tests", "true");
			}
			// Checking if resources folder exists, only if true the
			// import_resources method will be executed
			if (new File(resourcesDir).exists()){
				p.setProperty("import.resources", "true");
				p.setProperty("resources.dir", resourcesDir);
			}
		}
		
		// Exclude Sut\Scenarios from both source and classes
		if (importSut){
			p.setProperty("import.suts", "true");
			if (deleteSuts){
				p.setProperty("delete.suts", "true");
			}
		}
		if (importScenarios){
			p.setProperty("import.scenarios", "true");
			if (deleteScenarios){
				p.setProperty("delete.scenarios", "true");
			}
		}
		if (importLib){
			p.setProperty("import.lib", "true");
			if (deleteLib){
				p.setProperty("delete.lib", "true");
			}
		}

		
		p.setProperty("base.dir", new File(JSystemProperties.getCurrentTestsPath()).getParentFile().getAbsolutePath());
		p.setProperty("out.dir", classDir);
		p.setProperty("src.dir", srcDir);
		if (!zipFile.endsWith(".zip")){
			zipFile += ".zip";
		}
		p.setProperty("zip.file", zipFile);
		Command cmd = AntExecutor.executeAnt(CommonResources.getAntDirectory(), BuildUtils.class.getClassLoader()
				.getResourceAsStream("jsystem/utils/build/buildTasks.xml"), p, "import");
		log.fine(cmd.getStd().toString());
	}
}
