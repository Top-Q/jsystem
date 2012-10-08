/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts.jython;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import jsystem.framework.scripts.ScriptEngine;
import jsystem.framework.scripts.ScriptExecutor;

import org.python.core.PyException;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

public class JythonScriptEngine implements ScriptEngine {

	private static Logger log = Logger.getLogger(JythonScriptEngine.class.getName());
	
	@Override
	public boolean accept(File file) {
		return file.getName().toLowerCase().endsWith(".py");
	}

	@Override
	public boolean accept(String id) {
		return JythonScriptExecutor.class.getName().equals(id);
	}

	@Override
	public String getId() {
		return JythonScriptExecutor.class.getName();
	}

	@Override
	public ScriptExecutor[] getExecutor(File file) {
		ArrayList<ScriptExecutor> executors = new ArrayList<ScriptExecutor>();
		
		
		try {
			// create Jython interpreter
			PythonInterpreter interp = new JythonTestInterpreter();
	
			// get list of tests in module
			interp.exec(String.format("tags = getTestTagsFromModule(r'%s')", file.getAbsolutePath()));
			PyList tags = ((PyList)interp.get("tags"));
			
			// create an executor for each tag
			for (int i = 0; i < tags.__len__(); i++) {
				PyString tag = (PyString)tags.__getitem__(i);
				JythonScriptExecutor executor = new JythonScriptExecutor();
				executor.configTagName(tag.toString());
				executors.add(executor);
			}
		} catch (PyException e) {
			String message = e.toString();
			log.warning(String.format("Error while loading '%s':\n%s", file.getAbsolutePath(), message));
		}
		
		return executors.toArray(new ScriptExecutor[0]);
	}

	@Override
	public ScriptExecutor getExecutor(String tag) {
		JythonScriptExecutor executor = new JythonScriptExecutor();
		executor.configTagName(tag);
		return executor;
	}

	// icons
	
	@Override
	public ImageIcon getBasicImageIcon() {
		return null;
	}

	@Override
	public ImageIcon getErrorImageIcon() {
		return null;
	}

	@Override
	public ImageIcon getFailImageIcon() {
		return null;
	}

	@Override
	public ImageIcon getOKImageIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ImageIcon getRunningImageIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}
