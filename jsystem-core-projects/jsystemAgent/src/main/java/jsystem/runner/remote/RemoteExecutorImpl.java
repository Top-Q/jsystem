/*
 * Created on 28/07/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.remote;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.ExecutionListener;
import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.runner.WaitDialog;
import jsystem.runner.agent.publisher.Publisher;
import jsystem.runner.agent.publisher.PublisherManager;
import jsystem.runner.agent.tests.PublishTest;
import jsystem.runner.remote.RemoteTestRunner.RemoteMessage;
import jsystem.utils.StringUtils;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

/**
 * Used to execute and manage a remote test environment. A remote java vm will
 * be executed, the remote vm will connect to this process via object stream
 * using socket. The remote side used to execute the tests is ant.
 * 
 * @author guy.arieli TODO: remote executor does not work with flow control !
 */
public class RemoteExecutorImpl implements RemoteExecutor {

	private static Logger log = Logger.getLogger(RemoteExecutorImpl.class.getName());

	/**
	 * use to send report message
	 */
	private JSystemListeners reporter;

	/**
	 * The object ouput steam. This stream will be used to send messages to the
	 * test vm.
	 */
	private ObjectOutputStream out;

	/**
	 * The object input stream. This stream will be used to recieve messages
	 * from the test vm.
	 */
	private ObjectInputStream in;

	/**
	 * The socket the message will be send on
	 */
	private Socket socket;

	/**
	 * The server socket
	 */
	private ServerSocket ss = null;

	/**
	 * The thread that read the test vm messages
	 */
	Thread reader;

	/**
	 * The command to be executed
	 */
	Command cmd = null;

	/**
	 * If true the scenario is running
	 */
	private volatile boolean running = false;

	/**
	 * Indicate that the run was interrupted by the user
	 */
	private volatile boolean interrupted = false;

	/**
	 * Notify on run end
	 */
	private ExecutionListener runEndListener = null;

	/**
	 * if set to true will print the send and received messages
	 */
	boolean debugMessages = false;

	boolean firstMessage = true;

	boolean testStarted = false;

	public RemoteExecutorImpl() {
		reporter = RunnerListenersManager.getInstance();
	}

	/**
	 * Set the run end listener
	 */
	public void setRunEndListener(ExecutionListener runEndListener) {
		this.runEndListener = runEndListener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.runner.remote.RemoteExecutor#connect()
	 */
	public void run(String antFile, String[] targets, Properties additional) throws Exception {
		if (!(new File(antFile).exists())) {
			throw new Exception("Scenario file " + antFile + " was not found");
		}
		int usedPort;
		/*
		 * init server socket on any available port the remote vm will use this
		 * port to connect
		 */

		ss = new ServerSocket(0);
		usedPort = ss.getLocalPort();
		if (targets == null) {
			targets = new String[0];
		}

		/*
		 * If configured to work in test.debug=true the execution of the tests
		 * will block The user will be prompt to execute the RemoteTestRunner
		 * (test vm) manually with the information as execution parameters:
		 * -port 1999 -host 127.0.0.1
		 */
		boolean testDebug = "true".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.TESTS_DEBUG));
		String waitMessage;

		if (testDebug) {
			waitMessage = "Run Eclipse remote debugger and connect it to port 8787";
		} else {
			waitMessage = "Wait for remote VM";
		}

		File antHome = CommonResources.getAntDirectory();
		WaitDialog.launchWaitDialog(waitMessage);

		File antLauncher = new File(antHome + File.separator + "lib", "ant-launcher.jar");
		String vmParams = JSystemProperties.getInstance().getPreference(FrameworkOptions.TEST_VM_PARMS);
		String[] vmParamsArr = new String[0];
		if (vmParams != null) {
			// replaces socket number templates with free socket numbers
			vmParams = new TestVMParamsUtil().relpaceSocketNumber(vmParams);
			vmParamsArr = vmParams.split(" ");
		}
		cmd = new Command();
		ArrayList<String> cmdStringArray = new ArrayList<String>();

		cmdStringArray.add(System.getProperty("java.home") + File.separatorChar + "bin" + File.separatorChar + "java");

		// set up debug parameters for remote debugging via eclipse
		if (testDebug) {
			// see http://www.eclipsezone.com/eclipse/forums/t53459.html
			cmdStringArray.add("-Xdebug");
			cmdStringArray.add("-Xnoagent");
			cmdStringArray.add("-Djava.compiler=NONE");
			cmdStringArray.add("-Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=y");
		}

		cmdStringArray.add("-D" + RunningProperties.CURRENT_FIXTURE_BASE + "="
				+ FixtureManager.getInstance().getCurrentFixture());
		cmdStringArray.add("-D" + RunningProperties.CURRENT_SCENARIO_NAME + "="
				+ ScenariosManager.getInstance().getCurrentScenario().getName());
		cmdStringArray.add("-D" + RunningProperties.FIXTURE_DISABLE_TAG + "="
				+ FixtureManager.getInstance().isDisableFixture());
		cmdStringArray.add("-D" + RunningProperties.RUNNER_EXIST + "=true");
		cmdStringArray.add("-D" + RunningProperties.JSYSTEM_AGENT + "="
				+ System.getProperty(RunningProperties.JSYSTEM_AGENT));
		for (int i = 0; i < vmParamsArr.length; i++) {
			cmdStringArray.add(vmParamsArr[i]);
		}
		cmdStringArray.add("-classpath");
		cmdStringArray.add(antLauncher.getAbsolutePath());
		if (additional != null) {
			Enumeration<Object> enum1 = additional.keys();
			while (enum1.hasMoreElements()) {
				String key = (String) enum1.nextElement();
				String value = additional.getProperty(key);
				cmdStringArray.add("-D" + key + "=" + value);
			}
		}
		cmdStringArray.add("-Dant.home=" + antHome.getAbsolutePath());
		cmdStringArray.add("org.apache.tools.ant.launch.Launcher");
		cmdStringArray.add("-listener");
		cmdStringArray.add(RemoteTestRunner.class.getName());
		cmdStringArray.add("-cp");
		cmdStringArray.add(System.getProperty("java.class.path"));
		cmdStringArray.add("-buildfile");
		cmdStringArray.add(antFile);
		cmdStringArray.add("-D" + RunningProperties.USER_DIR + "=" + System.getProperty("user.dir"));
		cmdStringArray.add("-D" + RunningProperties.SCENARIO_BASE + "=" + JSystemProperties.getCurrentTestsPath());
		for (int i = 0; i < targets.length; i++) {
			cmdStringArray.add(targets[i]);
		}

		cmd.setCmd((String[]) cmdStringArray.toArray(new String[0]));
		cmd.setEnvParams(new String[] { RunningProperties.RUNNER_PORT + "=" + usedPort,
				RunningProperties.RUNNER_HOST + "=" + "127.0.0.1", RunningProperties.ANT_EXECUTOR + "=" + "true" });

		String cmdString = cmd.getCommandAsString();
		log.fine(cmdString);

		cmd.setDir(new File(System.getProperty("user.dir")));
		Execute.execute(cmd, false, true, false);

		socket = ss.accept();
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
		running = true;
		closed = false;
		reader = new ReaderThread();
		reader.start();

	}

	private class ReaderThread extends Thread {

		public ReaderThread() {
			super("RemoteExecuterImpl-ReaderThread");
		}

		public void run() {
			try {
				while (true) {
					Message m = (Message) in.readObject();
					if (firstMessage) {
						WaitDialog.endWaitDialog();
						firstMessage = false;
					}
					if (debugMessages) {
						System.err.println("Rcv: R<--T " + m.getType().name());
					}
					if (m == null) {
						if (running) {
							RemoteExecutorImpl.this.close();
							runEndListener.endRun();
							notifyOnError("Remote Execution Error", "Remote side was closed unexpectedly",
									ErrorLevel.Error);
						}
						return;
					}
					switch (m.getType()) {
					case M_FLUSH_REPORTER:
						ListenerstManager.getInstance().flushReporters();
						break;
					case M_INIT_REPORTER:
						ListenerstManager.getInstance().initReporters();
						break;
					case M_REPORT:
						if (!running) {
							return;
						}
						try {
							reporter.report(m.getField(0), m.getField(1), Integer.parseInt(m.getField(2)), Boolean
									.valueOf(m.getField(3)).booleanValue(), Boolean.valueOf(m.getField(4))
									.booleanValue(), Boolean.valueOf(m.getField(5)).booleanValue(),
									Boolean.valueOf(m.getField(6)).booleanValue(), Long.parseLong(m.getField(7)));
						} catch (Throwable t) {
							throw new Exception("Fail to report:\n" + StringUtils.getStackTrace(t) + "\n"
									+ m.toString());
						}
						break;

					case M_LEVEL:
						if (!running) {
							return;
						}
						try {
							reporter.startLevel(m.getField(0), Integer.parseInt(m.getField(1)));
						} catch (Throwable t) {
							throw new Exception("Fail to start a new Level:\n" + StringUtils.getStackTrace(t) + "\n"
									+ m.toString());
						}
						break;
					case M_STOP_LEVEL:
						if (!running) {
							return;
						}
						try {
							reporter.stopLevel();
						} catch (Throwable t) {
							throw new Exception("Failed to stop level", t);
						}
						break;
					case M_CLOSE_ALL_LEVELS:
						if (!running) {
							return;
						}
						try {
							reporter.closeAllLevels();
						} catch (Throwable t) {
							throw new Exception("Fail to close all Levels:\n" + StringUtils.getStackTrace(t) + "\n"
									+ m.toString());
						}
						break;
					case M_SAVE_FILE:
						reporter.saveFile(m.getField(0), StringUtils.stringToBytes(m.getField(1)));
						break;
					case M_START_REPORT:
						reporter.startReport(m.getField(0), m.getField(1), m.getField(2), m.getField(3));
						break;
					case M_END_REPORT:
						reporter.endReport(m.getField(0), m.getField(1));
						break;
					case M_SET_DATA:
						reporter.setData(m.getField(0));
						break;
					case M_SET_FAIL_TO_PASS:
						reporter.setFailToPass(Boolean.valueOf(m.getField(0)).booleanValue());
						break;
					case M_SET_FAIL_TO_WARNING:
						reporter.setFailToWarning(Boolean.valueOf(m.getField(0)).booleanValue());
						break;
					case M_SET_SILENT:
						reporter.setSilent(Boolean.valueOf(m.getField(0)).booleanValue());
						break;
					case M_SET_TIME_STAMP:
						reporter.setTimeStamp(Boolean.valueOf(m.getField(0)).booleanValue());
						break;
					case M_STEP:
						reporter.step(m.getField(0));
						break;
					case M_TEST_START:
						if (!running) {
							return;
						}
						String fullUuid = m.getField(0);
						RunnerTest rt = ScenariosManager.getInstance().getCurrentScenario()
								.getRunnerTestByFullId(fullUuid);
						reporter.startTest(rt.getTest());
						testStarted = true;
						break;
					case M_TEST_END:
						if (!running) {
							return;
						}
						fullUuid = m.getField(0);
						reporter.endTest(ScenariosManager.getInstance().getCurrentScenario()
								.getRunnerTestByFullId(fullUuid).getTest());
						break;
					case M_ADD_ERROR:
						reporter.addError(
								ScenariosManager.getInstance().getCurrentScenario()
										.getRunnerTestByFullId(m.getField(0)).getTest(), m.getField(1), m.getField(2));
						break;
					case M_ADD_FAILURE:
						reporter.addFailure(
								ScenariosManager.getInstance().getCurrentScenario()
										.getRunnerTestByFullId(m.getField(0)).getTest(), m.getField(1), m.getField(2),
								Boolean.valueOf(m.getField(3)).booleanValue());
						break;
					case M_FIXTURE_START:
						reporter.startFixturring();
						break;
					case M_FIXTURE_END:
						reporter.endFixturring();
						break;
					case M_FIXTURE_ABOUT:
						reporter.aboutToChangeTo(FixtureManager.getInstance().getFixture(m.getField(0)));
						break;
					case M_FIXTURE_CHANGED:
						FixtureManager.getInstance().setCurrentFixture(m.getField(0));
						reporter.fixtureChanged(FixtureManager.getInstance().getFixture(m.getField(0)));
						break;
					case M_OPERATION_FAIL:
						notifyOnError(m.getField(0), m.getField(1), ErrorLevel.Error);
						break;
					case M_SUT_CHANGED:
						log.fine("got change sut message");
						reporter.sutChanged(m.getField(0));
						break;
					case M_PAUSED:
						log.fine("got pause message");
						runEndListener.remotePause();
						break;
					case M_EXIT:
						log.fine("got exit message");
						runEndListener.remoteExit();
						close();
						break;
					case M_SAVE_STATE:
						log.fine("got save state message");
						fullUuid = m.getField(0);
						reporter.saveState(ScenariosManager.getInstance().getCurrentScenario()
								.getRunnerTestByFullId(fullUuid).getTest());
						break;
					case M_CONTAINER_PROPERTY:
						log.fine("got set container props");
						String level = m.getField(0);
						int anLevel = 0;
						try {
							anLevel = Integer.parseInt(level);
						} catch (Exception e) {

						}
						String key = m.getField(1);
						String val = m.getField(2);
						reporter.setContainerProperties(anLevel, key, val);
						break;
					case M_ANT_MESSAGE_LOGED:
						// TODO: log the message to log file
						// log.log(Level.WARNING, m.getField(0));
						break;
					case M_BUILD_START:
						break;
					case M_PROPERTY:
						// adding a property
						if (!running) {
							return;
						}
						try {
							reporter.addProperty(m.getField(0), m.getField(1));
						} catch (Throwable t) {
							throw new Exception("Fail to report:\n" + StringUtils.getStackTrace(t) + "\n"
									+ m.toString());
						}
						break;
					case M_BUILD_FINISH:
						log.fine("got build finished message");
						interrupted = true;
						runEndListener.endRun();
						break;
					case M_SYNCH:
						log.fine("Got M_SYNCH message");
						Message mm = new Message();
						mm.setType(RemoteMessage.M_SYNCHED);
						sendMessage(mm);
						log.fine("Sent M_SYNCHED id message");
						break;
					case M_TARGET_FINISH:
						break;
					case M_TARGET_START:
						break;
					case M_TASK_FINISH:
						break;
					case M_TASK_START:
						break;
					case M_ADD_WARNING:
						reporter.addWarning(ScenariosManager.getInstance().getCurrentScenario()
								.getRunnerTestByFullId(m.getField(0)).getTest());
						break;
					case M_SHOW_CONFIRM_DIALOG:
						/*
						 * launch a show confirm dialog in the runner every
						 * thing will block to the approvel
						 */
						int mr = reporter.showConfirmDialog(m.getField(0), m.getField(1),
								Integer.parseInt(m.getField(2)), Integer.parseInt(m.getField(3)));
						/*
						 * Send the approve message to the test with return
						 * value
						 */
						Message ms = new Message();
						ms.setType(RemoteMessage.M_SHOW_CONFIRM_DIALOG);
						ms.addField(Integer.toString(mr));
						sendMessage(ms);
						break;
					case M_CHECK_SYSTEM_OBJECT:
						// SystemObjectCheckWindow.getInstance().setSysObjStatus(m.getField(0),
						// SOCheckStatus.valueOf(m.getField(1)), m.getField(2));
						break;
			
					default:
						System.out.println("Unkown message type local: " + m.getType());
					}
				}
			} catch (Throwable e) {
				if (!running || interrupted) {
					return;
				}
				notifyOnError("Remote test execution fail", StringUtils.getStackTrace(e), ErrorLevel.Error);
				runEndListener.executionEnded("");
			}

		}

	}

	/**
	 * 
	 */
	private void notifyOnError(String title, String fullMessage, ErrorLevel errorLevel) {
		log.severe(title + ": " + fullMessage);
		runEndListener.errorOccured(title, fullMessage, errorLevel);
	}

	/**
	 * create properties from message this properties contains the
	 * Build,Description and Version properties for publish
	 */
	private HashMap<String, Object> createPropertiesForPublish(Message m) {
		HashMap<String, Object> p = new HashMap<String, Object>();
		// skipping the first field since it's the test Full ID
		for (int i = 1; i < m.getFields().size(); i++) {
			String[] fieldWithValue = (m.getField(i)).split(PublishTest.delimiter);
			String field = fieldWithValue[0];
			String value = "";

			if (fieldWithValue.length == 2)
				value = fieldWithValue[1];
			System.out.println("Publish field , value: " + field + "," + value);
			p.put(field, value);
		}
		return p;
	}

	private volatile boolean closed = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsystem.runner.remote.RemoteExecutor#stop()
	 */
	public synchronized void close() {
		if (closed) {
			return;
		}
		closed = true;
		running = false;
		runEndListener = null;

		if (cmd != null) {
			Process p = cmd.getProcess();
			if (p != null) {
				long startTime = System.currentTimeMillis();
				while (true) {
					if (System.currentTimeMillis() - startTime > 5000) {
						break;
					}
					try {
						p.exitValue();
						break;
					} catch (Throwable t) {
						// ignore
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				p.destroy();
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// ignore
			}
			out = null;
		}

		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				// ignore
			}
			ss = null;
		}
		if (reader != null) {
			reader.interrupt();
			reader = null;
		}
		synchronized (this) {
			notifyAll();
		}
		if (cmd != null) {
			Process p = cmd.getProcess();
			if (p != null) {
				p.destroy();
			}
		}
	}

	public void exit() {
		Message m = new Message();
		m.setType(RemoteMessage.M_EXIT);
		sendMessage(m);
		log.fine("sent exit message");
	}

	public void interruptTest() {
		interrupted = true;
		Message m = new Message();
		m.setType(RemoteMessage.M_INTERRUPT);
		sendMessage(m);
		log.fine("sent interrupt message");
		if (cmd != null) {
			cmd.getProcess().destroy();
		}
	}

	public void pause() {
		Message m = new Message();
		m.setType(RemoteMessage.M_PAUSE);
		sendMessage(m);
		log.fine("sent pause message");
	}

	public void gracefulStop() {
		Message m = new Message();
		m.setType(RemoteMessage.M_GRACEFUL_STOP);
		sendMessage(m);
		log.fine("sent graceful stop message");
	}

	public void resume() {
		Message m = new Message();
		m.setType(RemoteMessage.M_RESUME);
		sendMessage(m);
		log.fine("sent resume message");
	}

	private synchronized void sendMessage(Message m) {
		if (debugMessages) {
			System.err.println("Snd: R-->T " + m.getType().name());
		}
		if (out == null) {
			return;
		}
		try {
			out.writeObject(m);
			out.flush();
			/*
			 * Fix the object output stream memory issue
			 */
			out.reset();
		} catch (Exception e) {
			if (!interrupted) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Fail to send message", e);
			}
		}
	}

	public boolean isTestStarted() {
		return testStarted;
	}
}