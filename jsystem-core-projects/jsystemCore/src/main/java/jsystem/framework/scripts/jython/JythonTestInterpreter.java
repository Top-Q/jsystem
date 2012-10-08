/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts.jython;

import java.io.InputStream;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;

import org.python.core.PyException;
import org.python.core.PyModule;
import org.python.core.PyObject;
import org.python.core.imp;
import org.python.util.PythonInterpreter;

public class JythonTestInterpreter extends PythonInterpreter {

	public JythonTestInterpreter() {
		super();
		
		// do common interpreter initialization
		
		// set up the __main__ module
		PyModule mod = imp.addModule("__main__");
        setLocals(mod.__dict__);

        // load the jyrunner module that contains helper methods for reflecting and 
        // running jython tests
		exec("import sys");
		exec(String.format("sys.path.append(r'%s\\lib\\jython')", System.getenv("RUNNER_ROOT")));
		exec(String.format("sys.path.append(r'%s')", 
				JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_CLASS_FOLDER)));
		execfile(String.format("%s\\lib\\jython\\jyrunner.py", System.getenv("RUNNER_ROOT")));
	}

	@Override
	public PyObject eval(String s) {
		try {
			return super.eval(s);
		} catch (PyException e) {
			throw new PyExceptionWrapper(e);
		}
	}

	@Override
	public void exec(PyObject s) {
		try {
			super.exec(s);
		} catch (PyException e) {
			throw new PyExceptionWrapper(e);
		}
	}

	@Override
	public void exec(String s) {
		try {
			super.exec(s);
		} catch (PyException e) {
			throw new PyExceptionWrapper(e);
		}
	}

	@Override
	public void execfile(InputStream s, String name) {
		try {
			super.execfile(s, name);
		} catch (PyException e) {
			throw new PyExceptionWrapper(e);
		}
	}

	@Override
	public void execfile(InputStream s) {
		try {
			super.execfile(s);
		} catch (PyException e) {
			throw new PyExceptionWrapper(e);
		}
	}

	@Override
	public void execfile(String s) {
		try {
			super.execfile(s);
		} catch (PyException e) {
			throw new PyExceptionWrapper(e);
		}
	}
	
}
