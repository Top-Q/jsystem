/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;

import jsystem.framework.common.CommonResources;

/**
 * This is a launcher for jsystem.
 */
public class Launcher2 {

	public static final String TEXT_MAIN_CLASS = "jsystem.textui.ConsoleTestRunner";

	/**
	 * Entry point for starting jsystem
	 * 
	 * @param args
	 *            commandline arguments
	 */
	public static void main(String[] args) {
		try {
			Launcher2 launcher = new Launcher2();
			launcher.run(args);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	/**
	 * Run the launcher to launch Ant
	 * 
	 * @param args
	 *            the command line arguments
	 * 
	 * @exception MalformedURLException
	 *                if the URLs required for the classloader cannot be
	 *                created.
	 */
	public void run(String[] args) throws Exception {
		if (isRunFromJar()) { // if running from jar change the user.dir to
			// the parent dir
			File cd = new File(System.getProperty("user.dir"));
			System.setProperty("user.dir", cd.getParent());
		}
		CommonResources.setBasicClasspath();
		System.setProperty("java.class.path", CommonResources.getClassPath());
		if(!(new File(CommonResources.getRunnerDir()).equals(new File(System.getProperty("user.dir"))))){
			File propertiesFile = new File(System.getProperty("user.dir"),"jsystem.properties");
			if(!propertiesFile.exists()){
				try {
					copyFile(new File(CommonResources.getRunnerDir(),"jsystem.properties"), propertiesFile);
				} catch (Exception ignore){
					ignore.printStackTrace();
				}
			}
		}
		URLClassLoader loader = new URLClassLoader(CommonResources.getUserJars());
		Thread.currentThread().setContextClassLoader(loader);
		try {
			String mainClassName = System.getProperty(CommonResources.JSYSTEM_MAIN);
			if (mainClassName == null || "".equals(mainClassName.trim())){
				throw new Exception("jsystem main class is not defined");
			}
			Class<?> mainClass;
			mainClass = loader.loadClass(mainClassName);

			StartRunner main = (StartRunner) mainClass.newInstance();
			main.startRunner(args);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private boolean isRunFromJar() {
		/*
		 * if jsystem-launcher.jar found in the current dir than it was run from
		 * the jar.
		 */
		File f = new File(System.getProperty("user.dir"));
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {

			if (files[i].getName().equals("jsystem-launcher.jar")) {
				return true;
			}
		}
		return false;
	}
	private static void copyFile(File source, File destination) throws IOException {
		if ((destination.getParentFile() != null) && (!destination.getParentFile().exists())) {

			destination.getParentFile().mkdirs();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try{
		
			fis = new FileInputStream(source);
			fos = new FileOutputStream(destination);

			byte[] buffer = new byte[1024 * 4];
			int n = 0;

			while ((n = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, n);
			}

		}finally{
			if(fis != null){
				fis.close();
			}
			if(fos != null){
				fos.close();
			}
		}
	}

}
