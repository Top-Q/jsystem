/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scripts.jython;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scripts.ScriptExecutor;

import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyJavaClass;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyTuple;
import org.python.util.PythonInterpreter;

public class JythonScriptExecutor extends ScriptExecutor {

	Thread thread;
	Throwable exception;

	private static Logger log = Logger.getLogger(JythonScriptExecutor.class
			.getName());

	public class InterpreterRunnable implements Runnable {
		@Override
		public void run() {
			PythonInterpreter interp = new JythonTestInterpreter();

			PyObject result = null;
			try {
				// get the test
				interp.exec(String.format("test = getTestByTag(r'%s', r'%s')",
						getJythonFile().getAbsolutePath(), tag));

				// set up parameters
				for (Parameter param : parameters.values()) {
					if (!param.getName().contains(".")) {
						String cmd = String.format("test.%s = '%s'", param
								.getName(), param.getValue().toString()
								.replace("\\", "\\\\"));
						interp.exec(cmd);
					}
				}

				// run it
				interp.exec("result = runTest(test)");

				// get the result
				result = interp.get("result");
			} catch (Throwable e) {
				exception = e;
			}

			int wasSuccesful = ((PyInteger)result.__getattr__("wasSuccessful").__call__()).getValue();
			if (exception == null && wasSuccesful != 1) {
				try {
					interp.exec("errors = result.getErrors()");
					String errors = interp.get("errors").toString();
					exception = new JythonFailedException("Jython test failed!", errors);
				} catch (Throwable e) {
					exception = e; 
				}
			}
		}
	}

	@Override
	public void processParameters() {
		super.processParameters();

		PythonInterpreter interp = new JythonTestInterpreter();
		interp.exec(String.format("test = getTestByTag(r'%s', r'%s')",
				getJythonFile().getAbsolutePath(), tag));

		// set up fixtures (actual test parameters from the UI are set in
		// InterpreterRunnable.run)
		interp.exec("fixture = test.__fixture__");
		Class<?> fixture = (Class<?>) ((PyJavaClass) interp.get("fixture"))
				.__tojava__(Class.class);
		setFixture(fixture);
		interp.exec("tearDownFixture = test.__tearDownFixture__");
		Class<?> tearDownFixture = (Class<?>) ((PyJavaClass) interp
				.get("tearDownFixture")).__tojava__(Class.class);
		setTearDownFixture(tearDownFixture);
	}

	@Override
	protected void startExecute() throws Throwable {
		report.report(String.format("Execute Jython: %s (%s)", filePath, tag));
		/*
		 * Check that the file exists
		 */
		File jythonFile = new File(JSystemProperties.getCurrentTestsPath()
				+ File.separatorChar + filePath);
		if (!jythonFile.exists()) {
			throw new FileNotFoundException("File not found: "
					+ jythonFile.getAbsolutePath());
		}

		thread = new Thread(new InterpreterRunnable());
		thread.start();
	}

	@Override
	protected void waitForExecuteEnd() throws Throwable {
		if (thread != null) {
			thread.join();
			if (exception != null) {
				throw exception;
			}
		}
	}

	@Override
	public void initParamsFromFile() {
		try {
			PythonInterpreter interp = new JythonTestInterpreter();
			interp.exec(String.format("test = getTestByTag(r'%s', r'%s')",
					getJythonFile().getAbsolutePath(), tag));
			interp.exec("params = test.__parameters__.items()");
			PyList params = (PyList) interp.get("params");

			// add all the parameters so that they will be displayed in the UI
			for (int i = 0; i < params.__len__(); i++) {
				PyTuple pyparam = (PyTuple) params.__getitem__(i);
				String name = pyparam.__getitem__(0).toString();
				PyObject info = pyparam.__getitem__(1);

				Parameter param = new Parameter();
				param.setName(name);
				param.setType(ParameterType.STRING);
				parameters.put(name, param);
			}

		} catch (PyException e) {
			String message = e.toString();
			log.warning(String.format(
					"Error while loading parameters from '%s':\n%s",
					getJythonFile().getAbsolutePath(), message));
		}
	}

	@Override
	public String getTestName() {
		try {
			PythonInterpreter interp = new JythonTestInterpreter();
			interp.exec(String.format("test = getTestByTag(r'%s', r'%s')",
					getJythonFile().getAbsolutePath(), tag));
			interp.exec("testDescr = test.shortDescription()");
			PyObject descr = interp.get("testDescr");
			if (descr.__nonzero__()) {
				return descr.toString();
			} else {
				return getTagName();
			}
		} catch (Throwable e) {
			String message = e.toString();
			System.err.println(message);
			return getTagName();
		}
	}

	private File getJythonFile() {
		return new File(JSystemProperties.getCurrentTestsPath()
				+ File.separatorChar + filePath);
	}
}
