/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.RunnerStatePersistencyManager;
import jsystem.framework.analyzer.AnalyzerException;
import jsystem.framework.fixture.Fixture;
import jsystem.framework.fixture.FixtureListener;
import jsystem.framework.fixture.FixtureManager;
import jsystem.framework.report.DefaultReporterImpl;
import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunningProperties;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.sut.SutListener;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.runner.SOCheckStatus;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.NamedTest;
import junit.framework.Test;
import junit.framework.TestResult;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;

/**
 * A RemoteTestRunner run a scenario and report result via socket using
 * ObjectInputStream. for more information regarding the protocol see the
 * RemoteMessage enum. To execute the RemoteTestRunner the main method is called
 * with the host and port of the runner vm.
 */
public class RemoteTestRunner extends DefaultReporterImpl
		implements ExtendTestListener, FixtureListener, SutListener, BuildListener {

	static Logger log = Logger.getLogger(RemoteTestRunner.class.getName());

	boolean silent = false;

	public enum RemoteMessage {

		/**
		 * Send flush events to all the reports
		 */
		M_FLUSH_REPORTER,
		/**
		 * Send init events to all the reports
		 */
		M_INIT_REPORTER,
		/**
		 * Set the reporter to be silent (<--)
		 */
		M_SET_SILENT,

		/**
		 * Set the reporter to be silent (<--)
		 */
		M_SET_TIME_STAMP,

		/**
		 * Reporting step (<--)
		 */
		M_STEP,

		/**
		 * Regular report (<--)
		 */
		M_REPORT,

		/**
		 * Set the test report data (<--)
		 */
		M_SET_DATA,

		/**
		 * Launch start report call (<--)
		 */
		M_START_REPORT,

		/**
		 * Launch end report call (<--)
		 */
		M_END_REPORT,

		/**
		 * Save file report call (<--)
		 */
		M_SAVE_FILE,

		/**
		 * Set fail reports to pass (<--)
		 */
		M_SET_FAIL_TO_PASS,

		/**
		 * Set fail report to warning (<--)
		 */
		M_SET_FAIL_TO_WARNING,

		/**
		 * Send on test start and received from the runner for sych (<-->)
		 */
		M_TEST_START,

		/**
		 * Send to runner to notify on test end (<--)
		 */
		M_TEST_END,

		/**
		 * Send to the runner on failure (<--)
		 */
		M_ADD_FAILURE,

		/**
		 * Send to the runner on error (<--)
		 */
		M_ADD_ERROR,

		/**
		 * Send to the runner on warning (<--)
		 */
		M_ADD_WARNING,

		/**
		 * Notify the runner on start fixture (<--)
		 */
		M_FIXTURE_START,

		/**
		 * Notify the runner on end fixture (<--)
		 */
		M_FIXTURE_END,

		/**
		 * Notify the runner on about to change fixture (<--)
		 */
		M_FIXTURE_ABOUT,

		/**
		 * Notify the runner on fixture changed (<--)
		 */
		M_FIXTURE_CHANGED,

		/**
		 * Received when the remote test should exit (<-->)
		 */
		M_EXIT,

		/**
		 * Signal the runner when operation failed (<--)
		 */
		M_OPERATION_FAIL,

		/**
		 * Signal the runner that the SUT changed (<--)
		 */
		M_SUT_CHANGED,

		/**
		 * Interrupt the current execution (-->)
		 */
		M_INTERRUPT,

		/**
		 * Pause the current execution (-->)
		 */
		M_PAUSE,

		/**
		 * Stop the current execution (-->)
		 */
		M_GRACEFUL_STOP,

		/**
		 * Resume the current execution (-->)
		 */
		M_RESUME,

		/**
		 * Approved the pause operation
		 */
		M_PAUSED,

		/**
		 * Change the report level
		 */
		M_LEVEL,

		/**
		 * end current report level
		 */
		M_STOP_LEVEL,

		/**
		 * end current report level
		 */
		M_CLOSE_ALL_LEVELS,

		/**
		 * /** Build start, send the ant notification to the remote side (<--)
		 */
		M_BUILD_START,

		/**
		 * Build finish
		 */
		M_BUILD_FINISH,

		/**
		 * Target start, send the ant notification to the remote side (<--)
		 */
		M_TARGET_START,

		/**
		 * Target finish
		 */
		M_TARGET_FINISH,

		/**
		 * Task start, send the ant notification to the remote side (<--)
		 */
		M_TASK_START,

		/**
		 * Task finished
		 */
		M_TASK_FINISH,

		/**
		 * Message logged,
		 */
		M_ANT_MESSAGE_LOGED,

		/**
		 * Synchronization request
		 */
		M_SYNCH,

		/**
		 * Synchronization response
		 */
		M_SYNCHED,

		/**
		 * Property message consisted of key and value
		 */
		M_PROPERTY,
		/**
		 * Indicate a show confirm dialog request or dialog confirmed
		 */
		M_SHOW_CONFIRM_DIALOG,

		/**
		 * Check system object message
		 */
		M_CHECK_SYSTEM_OBJECT,

		/**
		 * save runner engine state
		 * 
		 * @see RunnerStatePersistencyManager
		 */
		M_SAVE_STATE,

		/**
		 * Property message consisted of key and value
		 */
		M_CONTAINER_PROPERTY,

		M_CONTAINER_START,

		M_CONTAINER_END,

	}

	/**
	 * The current test result
	 */
	private TestResult fTestResult;

	/**
	 * The client socket.
	 */
	private Socket fClientSocket;

	/**
	 * Print writer for sending messages
	 */
	private ObjectOutputStream fWriter;

	/**
	 * Reader for incoming messages
	 */
	private ObjectInputStream fReader;

	/**
	 * Host to connect to, default is the localhost
	 */
	private String fHost = ""; //$NON-NLS-1$

	/**
	 * Port to connect to.
	 */
	private int fPort = -1;

	/**
	 * Port to connect for reporting.
	 */
	// private int rPort= -1;
	/**
	 * Is the debug mode enabled?
	 */
	private boolean fDebugMode = false;

	/**
	 * Keep the test run server alive after a test run has finished. This allows
	 * to rerun tests.
	 */
	private boolean fKeepAlive = false;

	/**
	 * Thread reading from the socket
	 */
	private ReaderThread fReaderThread;

	/**
	 * The scenario beeing executed
	 */
	Scenario scenario = null;

	/**
	 * The tests indexes to be execute
	 */
	int[] testRunIndexes;

	/**
	 * block all messages
	 */
	private boolean blockAllMessages = false;

	private boolean interrupted = false;

	/**
	 * Set the true if the request for a message was confirmed
	 */
	private volatile boolean messageConfirmed = false;

	/*
	 * Hold the dialog return value
	 */
	private int messageConfermedReturn = -1;

	/**
	 * if set to true will print all the messages send and received
	 */
	private boolean debugMessages = false;

	/**
	 * Set to true when the connected to the remote runner
	 */
	private boolean connected = false;

	/**
	 * Set to true when the remote VM is synchronized
	 */
	private boolean synchronize = false;

	/**
	 * Singletone object
	 */
	private static RemoteTestRunner rtrunner = null;

	/**
	 * contains the message that RemoteExecutor reply. we use it only with
	 * M_PUBLISH message it contains the data on the scenario that run like -
	 * run index,number of tests, user and etc.
	 */
	private Message sendEmailRunDetails = null;

	public static RemoteTestRunner getInstance() {
		return rtrunner;
	}

	/**
	 * Private constructor to init the object use static method getInstance
	 * 
	 * @throws Exception
	 * 
	 */
	public RemoteTestRunner() throws Exception {
		/*
		 * Extract the runner host and port from the system environment
		 */
		String rp = System.getenv(RunningProperties.RUNNER_PORT);
		String rh = System.getenv(RunningProperties.RUNNER_HOST);
		if (rp == null || rh == null) { // if the environment is not set
			JSystemProperties.getInstance(true).setJsystemRunner(false);
			// (execute directly from ant)
			launchReporter();
		} else {
			JSystemProperties.getInstance(false).setJsystemRunner(false);
			init(new String[] { "-port", rp, "-host", rh });
			connect();
		}

		rtrunner = this;
		Runtime.getRuntime().addShutdownHook(new ShutdownThread(fWriter));
	}

	public void launchReporter() throws Exception {
		// throw new Exception("Launch reporter mode not supported yet");
	}

	/**
	 * Reader thread that processes messages from the client.
	 */
	private class ReaderThread extends Thread {
		public ReaderThread() {
			super("ReaderThread"); //$NON-NLS-1$
		}

		public void run() {
			try {
				while (true) {
					Message m = (Message) fReader.readObject();
					if (debugMessages) {
						System.err.println("Rcv: R-->T " + m.getType().name());
						System.err.flush();
					}
					if (m == null) {
						processExit();
						return;
					}
					switch (m.getType()) {
					case M_EXIT:
						log.fine("got exit message.");
						processExit();
						log.fine("exit process ended.");
						break;
					case M_INTERRUPT:
						log.fine("got interrupt message.");
						interrupted = true;
						processExit();
						log.fine("exit process after interrupt ended.");
						break;
					case M_PAUSE:
						log.fine("got pause message.");
						((ListenerstManager) ListenerstManager.getInstance()).pause();
						break;
					case M_GRACEFUL_STOP:
						log.fine("got graceful stop message.");
						((ListenerstManager) ListenerstManager.getInstance()).gracefulStop();
						break;
					case M_RESUME:
						log.fine("got resume message.");
						((ListenerstManager) ListenerstManager.getInstance()).resume();
						break;
					case M_SYNCHED:
						synchronize = true;
						log.fine("Got M_SYNCHED message");
						synchronized (fWriter) {
							fWriter.notifyAll();
						}
						log.fine("M_SYNCHED message after notify all");
						break;
					case M_SYNCH:
						Message mm = new Message();
						mm.setType(RemoteMessage.M_SYNCHED);
						sendMessage(mm);
						log.fine("Send M_SYNCHED message");
						break;

					case M_SHOW_CONFIRM_DIALOG:
						/*
						 * The test get the return value of the message and
						 * notify to the wait method
						 */
						messageConfermedReturn = Integer.parseInt(m.getField(0));
						synchronized (fWriter) {
							messageConfirmed = true;
							fWriter.notifyAll();
						}

						break;
					case M_CONTAINER_START:
						ListenerstManager.getInstance().startContainer(desrialize(m.getField(0), JTestContainer.class));
						break;
					case M_CONTAINER_END:
						ListenerstManager.getInstance().startContainer(desrialize(m.getField(0), JTestContainer.class));
						break;
					default:
						System.err.println("Unkown message type: " + m.getType());
					}
				}
			} catch (Exception e) {
				interrupted = true;
				processExit();
				RemoteTestRunner.this.stop();
			}
		}

	}

	@SuppressWarnings("unchecked")
	private static <T> T desrialize(String serializedObj, Class<T> type) {
		final byte[] data = Base64.getDecoder().decode(serializedObj);
		T e = null;
		try (ByteArrayInputStream fileIn = new ByteArrayInputStream(data);
				ObjectInputStream in = new ObjectInputStream(fileIn);) {
			e = (T) in.readObject();
		} catch (Exception i) {
			log.log(Level.WARNING, "Failed to deserialize string", e);
			return null;
		}
		return e;
	}

	private static String serialize(Serializable object) {
		try (ByteArrayOutputStream strout = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(strout)) {
			out.writeObject(object);
			return Base64.getEncoder().encodeToString(strout.toByteArray());
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to serialize object", e);
		}
		return null;

	}

	public static void main(String[] args) throws Exception {
		JTestContainer container = new Scenario(null, "scenarios/default");
		String s = serialize(container);
		JTestContainer c1 = desrialize(s, JTestContainer.class);
		System.out.println(c1);
	}

	/**
	 * Parse command line arguments. Hook for subclasses to process additional
	 * arguments.
	 */
	protected void init(String[] args) {
		defaultInit(args);
	}

	/**
	 * The class loader to be used for loading tests. Subclasses may override to
	 * use another class loader.
	 */
	protected ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	/**
	 * Process the default arguments.
	 */
	protected final void defaultInit(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().equals("-port")) { //$NON-NLS-1$
				fPort = Integer.parseInt(args[i + 1]);
				i++;
			} else if (args[i].toLowerCase().equals("-host")) { //$NON-NLS-1$
				fHost = args[i + 1];
				i++;
			} else if (args[i].toLowerCase().equals("-keepalive")) { //$NON-NLS-1$
				fKeepAlive = true;
			} else if (args[i].toLowerCase().equals("-debugging") || args[i].toLowerCase().equals("-debug")) { //$NON-NLS-1$ //$NON-NLS-2$
				fDebugMode = true;
			}
		}
		if (fPort == -1)
			throw new IllegalArgumentException("The -port option wasn't found"); //$NON-NLS-1$
		if (fDebugMode)
			System.out.println("keepalive " + fKeepAlive); //$NON-NLS-1$
	}

	protected void runFailed(String message, Throwable exception) {
		System.err.println(message);
		if (exception != null)
			exception.printStackTrace();
	}

	/*
	 * @see TestListener#addError(Test, Throwable)
	 */
	public final void addError(Test test, Throwable throwable) {
		Message m = new Message();
		m.setType(RemoteMessage.M_ADD_ERROR);
		m.addField(getTestFullId(test));
		m.addField(throwable.getMessage());
		m.addField(StringUtils.getStackTrace(throwable));
		sendMessage(m);
	}

	/*
	 * @see TestListener#addFailure(Test, AssertionFailedError)
	 */
	public final void addFailure(Test test, AssertionFailedError assertionFailedError) {
		Message m = new Message();
		m.setType(RemoteMessage.M_ADD_FAILURE);
		m.addField(getTestFullId(test));
		m.addField(assertionFailedError.getMessage());
		m.addField(StringUtils.getStackTrace(assertionFailedError));
		m.addField(Boolean.toString(assertionFailedError instanceof AnalyzerException));
		sendMessage(m);
	}

	/*
	 * @see TestListener#endTest(Test)
	 */
	public void endTest(Test test) {
		Message m = new Message();
		m.setType(RemoteMessage.M_TEST_END);
		m.addField(getTestFullId(test));
		sendMessage(m);
	}

	public void initReporters() {
		Message m = new Message();
		m.setType(RemoteMessage.M_INIT_REPORTER);
		sendMessage(m);
	}

	public void flushReporters() {
		Message m = new Message();
		m.setType(RemoteMessage.M_FLUSH_REPORTER);
		sendMessage(m);
	}

	public void endScenario() {
		Message m = new Message();
		m.setType(RemoteMessage.M_TEST_END);
		m.addField("-1");
		sendMessage(m);
	}

	/*
	 * @see TestListener#startTest(Test)
	 */
	public void startTest(Test test) {
		this.currentTest = test;
		/*
		 * Send notification that the test was started
		 */
		Message m = new Message();
		m.setType(RemoteMessage.M_TEST_START);
		m.addField(getTestFullId(test));
		sendMessage(m);
		/*
		 * Wait for the runner synch message
		 */
		synchronized (fWriter) {
			synchronize();
		}
	}

	/**
	 * Stop the current test run.
	 */
	protected void stop() {
		if (fTestResult != null) {
			fTestResult.stop();
		}
	}

	/**
	 * Connect to the remote test listener.
	 */
	private boolean connect() {
		if (fDebugMode)
			System.out.println("RemoteTestRunner: trying to connect " + fHost + ":" + fPort); //$NON-NLS-1$ //$NON-NLS-2$
		Exception exception = null;
		for (int i = 1; i < 20; i++) {
			try {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// ignore
				}
				fClientSocket = new Socket(fHost, fPort);
				fWriter = new ObjectOutputStream(fClientSocket.getOutputStream()); // $NON-NLS-1$
				fReader = new ObjectInputStream(fClientSocket.getInputStream());
				fReaderThread = new ReaderThread();
				fReaderThread.start();
				connected = true;
				return true;
			} catch (IOException e) {
				exception = e;
			}
		}
		runFailed("Fail to connect to host: " + fHost + ", on port: " + fPort, exception); //$NON-NLS-1$
		return false;
	}

	/**
	 * Close all the system object and wait on the close process
	 * 
	 */
	private void closeAllSystemObjects() {
		Thread t = new Thread() {
			public void run() {
				SystemManagerImpl.getInstance().closeAllObjects();
			}
		};
		t.start();
		try {
			t.join();
		} catch (Exception ignore) {
			// ignore
		}
	}

	private void synchronize() {
		synchronized (fWriter) {
			synchronize = false;
			Message m = new Message();
			m.setType(RemoteMessage.M_SYNCH);
			sendMessage(m);
			log.fine("Sent M_SYNCH message");
			try {
				while (!synchronize) {
					try {
						fWriter.wait(1000);
					} catch (InterruptedException e) {
						return;
					}
				}
			} finally {
				synchronize = false;
			}
		}
	}

	public void processExit() {
		closeAllSystemObjects();
		blockAllMessages = true;
		synchronized (fWriter) {
			waitForExit = false;
			fWriter.notifyAll();
			if (interrupted) {
				System.exit(0);
			}
		}
	}

	/**
	 * Shuts down the connection to the remote test listener.
	 */
	public static Object getField(Object object, String fieldName) {
		Class<?> currentClass = object.getClass();
		try {
			Field field = currentClass.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(object);
		} catch (Exception e) {
			// fall through
		}
		return null;
	}

	public void setSilent(boolean status) {
		Message m = new Message();
		m.setType(RemoteMessage.M_SET_SILENT);
		m.addField(Boolean.toString(status));
		sendMessage(m);
		silent = status;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setTimeStamp(boolean enable) {
		Message m = new Message();
		m.setType(RemoteMessage.M_SET_TIME_STAMP);
		m.addField(Boolean.toString(enable));
		sendMessage(m);
	}

	public void saveFile(String fileName, byte[] content) {
		Message m = new Message();
		m.setType(RemoteMessage.M_SAVE_FILE);
		m.addField(fileName);
		m.addField(StringUtils.bytesToString(content));
		sendMessage(m);
	}

	public void setData(String data) {
		Message m = new Message();
		m.setType(RemoteMessage.M_SET_DATA);
		m.addField(data);
		sendMessage(m);
	}

	public void startReport(String methodName, String parameters, String classDoc, String testDoc) {
		Message m = new Message();
		m.setType(RemoteMessage.M_START_REPORT);
		m.addField(methodName);
		m.addField(parameters);
		m.addField(classDoc);
		m.addField(testDoc);
		sendMessage(m);
	}

	public void endReport(String steps, String failCause) {
		Message m = new Message();
		m.setType(RemoteMessage.M_END_REPORT);
		m.addField(steps);
		m.addField(failCause);
		sendMessage(m);
	}

	public void report(String title, String message, int status, boolean bold, boolean html, boolean step, boolean link,
			long time) {
		Message m = new Message();
		m.setType(RemoteMessage.M_REPORT);
		m.addField(title);
		m.addField(String.valueOf(message));
		m.addField(Integer.toString(status));
		m.addField(Boolean.toString(bold));
		m.addField(Boolean.toString(html));
		m.addField(Boolean.toString(step));
		m.addField(Boolean.toString(link));
		m.addField(Long.toString(time));
		sendMessage(m);
	}

	public void startLevel(String level, int place) throws IOException {
		Message m = new Message();
		m.setType(RemoteMessage.M_LEVEL);
		m.addField(level);
		m.addField(Integer.toString(place));
		m.addField(Integer.toString(0));
		m.addField(Boolean.toString(true));
		m.addField(Boolean.toString(false));
		m.addField(Boolean.toString(false));
		m.addField(Boolean.toString(false));
		sendMessage(m);
	}

	public void startLevel(String level, EnumReportLevel place) throws IOException {
		startLevel(level, place.value());

	}

	public void stopLevel() throws IOException {
		Message m = new Message();
		m.setType(RemoteMessage.M_STOP_LEVEL);
		m.addField(Integer.toString(0));
		m.addField(Integer.toString(0));
		m.addField(Integer.toString(0));
		m.addField(Boolean.toString(true));
		m.addField(Boolean.toString(false));
		m.addField(Boolean.toString(false));
		m.addField(Boolean.toString(false));
		sendMessage(m);

	}

	public void setFailToPass(boolean failToPass) {
		super.setFailToPass(failToPass);
		Message m = new Message();
		m.setType(RemoteMessage.M_SET_FAIL_TO_PASS);
		m.addField(Boolean.toString(failToPass));
		sendMessage(m);
	}

	public void setFailToWarning(boolean failToWarning) {
		super.setFailToWarning(failToWarning);
		Message m = new Message();
		m.setType(RemoteMessage.M_SET_FAIL_TO_WARNING);
		m.addField(Boolean.toString(failToWarning));
		sendMessage(m);
	}

	public void addWarning(Test test) {
		Message m = new Message();
		m.setType(RemoteMessage.M_ADD_WARNING);
		m.addField(getTestFullId(test));
		sendMessage(m);
	}

	public void startTest(TestInfo testInfo) {
		// not called
	}

	public void endRun() {
		// Message m = new Message();
		// m.setType(RemoteMessage.M_RUN_END);
		// sendMessage(m);
	}

	public void aboutToChangeTo(Fixture fixture) {
		Message m = new Message();
		m.setType(RemoteMessage.M_FIXTURE_ABOUT);
		m.addField(fixture.getClass().getName());
		sendMessage(m);
	}

	public void fixtureChanged(Fixture fixture) {
		Message m = new Message();
		m.setType(RemoteMessage.M_FIXTURE_CHANGED);
		m.addField(fixture.getClass().getName());
		sendMessage(m);
	}

	public void startFixturring() {
		Message m = new Message();
		m.setType(RemoteMessage.M_FIXTURE_START);
		sendMessage(m);
	}

	public void endFixturring() {
		Message m = new Message();
		m.setType(RemoteMessage.M_FIXTURE_END);
		sendMessage(m);
	}

	public void operationFail(String title, String message) {
		Message m = new Message();
		m.setType(RemoteMessage.M_OPERATION_FAIL);
		m.addField(title);
		m.addField(message);
		sendMessage(m);
	}

	public void sutChanged(String sutName) {
		Message m = new Message();
		m.setType(RemoteMessage.M_SUT_CHANGED);
		m.addField(sutName);
		sendMessage(m);
	}

	public void paused() {
		Message m3 = new Message();
		m3.setType(RemoteMessage.M_PAUSED);
		sendMessage(m3);
		log.fine("Paused event send");
	}

	/**
	 * Send message to the runner
	 * 
	 * @param message
	 *            the message to send
	 */
	private void sendMessage(Message message) {
		if (blockAllMessages) {
			return;
		}
		if (fWriter == null) {
			return;
		}
		if (debugMessages) {
			System.err.println("Snd: T-->R " + message.getType().name());
			System.err.flush();
		}
		synchronized (fWriter) {
			try {
				fWriter.writeObject(message);
				fWriter.flush();
				/*
				 * Fix the object output stream memory issue
				 */
				fWriter.reset();
			} catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Fail to send message", e);
			}
		}
	}

	boolean waitForExit = false;

	private void waitForExit() {
		if (!connected) {
			return;
		}
		waitForExit = true;
		synchronized (fWriter) {
			while (waitForExit) {
				try {
					fWriter.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
		}
	}

	public void buildFinished(BuildEvent event) {
		Message m = new Message();
		m.setType(RemoteMessage.M_BUILD_FINISH);
		sendMessage(m);
		log.fine("sent buildFinished message.");
		new ExitWatcher().start();
		if (interrupted) {
			blockAllMessages = true;
		} else {
			log.fine("started wait for exit.");
			waitForExit();
			log.fine("ended wait for exit.");
		}
	}

	public void buildStarted(BuildEvent event) {
		String currentFixture = System.getProperty(RunningProperties.CURRENT_FIXTURE_BASE);
		log.info("Current fixture: " + currentFixture);
		if (currentFixture != null) {
			try {
				FixtureManager.getInstance().setCurrentFixture(currentFixture);
			} catch (Exception e) {
				log.log(Level.WARNING, "Fail to set current fixture", e);
			}
		}
		FixtureManager.getInstance().addListener(ListenerstManager.getInstance());
		Message m = new Message();
		m.setType(RemoteMessage.M_BUILD_START);
		sendMessage(m);
	}

	public void messageLogged(BuildEvent event) {
		// ignore
	}

	public void targetFinished(BuildEvent event) {
		Message m = new Message();
		m.setType(RemoteMessage.M_TARGET_FINISH);
		m.addField(event.getTarget().getName());
		m.addField(event.getTarget().getProject().getName());
		sendMessage(m);
	}

	public void addProperty(String key, String value) {
		Message m = new Message();
		m.setType(RemoteMessage.M_PROPERTY);
		m.addField(key);
		m.addField(value);

		sendMessage(m);
	}

	public void targetStarted(BuildEvent event) {
		Message m = new Message();
		m.setType(RemoteMessage.M_TARGET_START);
		m.addField(event.getTarget().getName());
		m.addField(event.getTarget().getProject().getName());
		sendMessage(m);
	}

	public void taskFinished(BuildEvent event) {
		Message m = new Message();
		m.setType(RemoteMessage.M_TASK_FINISH);
		m.addField(event.getTask().getTaskName());
		sendMessage(m);
	}

	public void taskStarted(BuildEvent event) {
		Message m = new Message();
		m.setType(RemoteMessage.M_TASK_START);
		m.addField(event.getTask().getTaskName());
		sendMessage(m);
	}

	public String getTestFullId(Test test) {
		if (test == null) {
			test = currentTest;
		}
		String uuid = "";
		if (test instanceof NamedTest) {
			uuid = ((NamedTest) test).getFullUUID();
		}
		return uuid;
	}

	public int showConfirmDialog(String title, String message, int optionType, int messageType) {
		/*
		 * Send the conferm dialog information and wait for a response
		 */
		messageConfirmed = false;
		Message m = new Message();
		m.setType(RemoteMessage.M_SHOW_CONFIRM_DIALOG);
		m.addField(title);
		m.addField(message);
		m.addField(Integer.toString(optionType));
		m.addField(Integer.toString(messageType));
		sendMessage(m);
		while (!messageConfirmed) {
			synchronized (fWriter) {
				try {
					fWriter.wait();
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		return messageConfermedReturn;
	}

	public void checkSystemObjectStatus(String soName, SOCheckStatus status, String errorMessage) {
		Message m = new Message();
		m.setType(RemoteMessage.M_CHECK_SYSTEM_OBJECT);
		m.addField(soName);
		m.addField(status.name());
		m.addField(errorMessage);
		sendMessage(m);
	}

	public void exit() {
		Message m = new Message();
		m.setType(RemoteMessage.M_EXIT);
		sendMessage(m);
	}

	public void saveState(Test t) {
		Message m = new Message();
		m.setType(RemoteMessage.M_SAVE_STATE);
		m.addField(getTestFullId(t));
		sendMessage(m);
	}

	/**
	 * create and send publish message
	 * 
	 * @param p
	 *            properties that contains the description, setup name, version
	 *            and build (field name and values)
	 * @param test
	 *            the test that calls this method (for the Full Unique ID)
	 * @throws Exception
	 */
	public Message sendPublishMessage(Message m, Test test) throws Exception {
		messageConfirmed = false;
		sendMessage(m);

		while (!messageConfirmed) {
			synchronized (fWriter) {
				try {
					fWriter.wait();
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		return sendEmailRunDetails;

	}

	public Message getSendEmailRunDetails() {
		return sendEmailRunDetails;
	}

	public void setSendEmailRunDetails(Message sendEmailRunDetails) {
		this.sendEmailRunDetails = sendEmailRunDetails;
	}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
	}

	@Override
	public void closeAllLevels() throws IOException {
		Message m = new Message();
		m.setType(RemoteMessage.M_CLOSE_ALL_LEVELS);
		sendMessage(m);
	}

	@Override
	public void setContainerProperties(int ancestorLevel, String key, String value) {
		Message m = new Message();
		m.setType(RemoteMessage.M_CONTAINER_PROPERTY);
		m.addField("" + ancestorLevel);
		m.addField(key);
		m.addField(value);
		sendMessage(m);

	}

}

/**
 * Will be added to shutdownhook and will be execute on vm exit
 * 
 * @author guy.arieli
 * 
 */
class ShutdownThread extends Thread {
	ObjectOutputStream out;

	// ObjectInputStream in;
	public ShutdownThread(ObjectOutputStream out) {
		this.out = out;
		// this.in = in;
	}

	public void run() {
		try {
			if (out != null) {
				Message m = new Message();
				m.setType(RemoteTestRunner.RemoteMessage.M_EXIT);
				out.writeObject(m);
				out.flush();
				RemoteTestRunner.log.fine("Shutdown thread - sent exit.");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// ignore
				}
			}
			out.close();
		} catch (Throwable t) {
			// ignore
		}
	}
}

/**
 * Verify that the VM will exit in 8 sec.
 * 
 * @author guy.arieli
 * 
 */
class ExitWatcher extends Thread {
	public void run() {
		RemoteTestRunner.log.fine("Exit watcher started");
		long exitTimeout = JSystemProperties.getInstance().getLongPreference(FrameworkOptions.EXIT_TIMEOUT, 15000);

		if (exitTimeout == 0) {
			RemoteTestRunner.log.fine("Exit watcher exittimeout=0 before system.exit");
			System.exit(0);
		}

		if (exitTimeout < 0) {
			RemoteTestRunner.log.fine("Exit watcher exittimeout<0 before return");
			return;
		}

		try {
			Thread.sleep(exitTimeout);
		} catch (InterruptedException e) {
			return;
		}
		RemoteTestRunner.log.fine("Exit watcher exittimeout>0 after sleep before system.exit");
		System.exit(0);
	}
}
