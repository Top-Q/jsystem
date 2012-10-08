/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.agent.notifications;

import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.report.JSystemListeners;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.RunnerListenersManager;
import jsystem.runner.agent.mediators.InteractiveReporterMediator;
import jsystem.runner.agent.server.RunnerAgent;

/**
 * Handles the {@link Reporter#showConfirmDialog(String, String, int, int)} event.<br>
 * In it's nature the {@link Reporter#showConfirmDialog(String, String, int, int)} event is <br>
 * a synchronous event. The event is sent from test JVM, test execution halts until user responds<br>
 * to message, and user choice is returned to the test.<br>
 * 
 * Because JMX notifications are asynchronous events, several entities are involved in<br>
 * emulating a synchronous event on top of asynchronous infrastructure:<br>
 * 1. The {@link InteractiveReporterMediator} listens for {@link Reporter#showConfirmDialog(String, String, int, int)} event.<br>
 * once the event is fired by the test, a {@link ShowMessageDialogNotification} with confirmation details is <br>
 * created and fired by {@link InteractiveReporterMediator#showConfirmDialog(String, String, int, int)}.<br>
 * After message is fired {@link InteractiveReporterMediator#showConfirmDialog(String, String, int, int)} waits for message to<br> 
 * reach to the client and for user respond to the confirm dialog by calling the method {@link RunnerAgent#waitForConfirmDialogResults(long)}<br>
 * 2. When notification arrives to the client, the method {@link ShowMessageDialogNotification#invokeDispatcher(JSystemListeners)} is<br>
 * invoked. The method eventually calls {@link RunnerListenersManager#showConfirmDialog(String, String, int, int)} 
 * which shows the confirm dialog.
 * 3. Once user confirms the message, the method {@link RunnerAgent#returnMessageConfirmationResult(int, long)} is invoked<br>
 * Calling this method releases the method {@link RunnerAgent#waitForConfirmDialogResults(long)}.
 *   
 * @author goland
 */
public class ShowMessageDialogNotification extends RunnerNotification {
	private static long messageIndex;
	private static final long serialVersionUID = 468127957018314228L;
	private static Logger log = Logger.getLogger(ShowMessageDialogNotification.class.getName());
	private String title;
	private String message;
	private int optionType;
	private int messageType;
	
	public ShowMessageDialogNotification(Object source,String title, String message, int optionType, int messageType) {
		super(ShowMessageDialogNotification.class.getName(), source);
		this.title = title;
		this.message = message;
		this.optionType = optionType;
		this.messageType = messageType;
		setSequenceNumber(++messageIndex);
	}
	public void invokeDispatcher(JSystemListeners mediator){
		int result =  mediator.showConfirmDialog(title, message, optionType, messageType);
		try {
			getAgentMBean().returnMessageConfirmationResult(result,getSequenceNumber());
		}catch (Exception e) {
			log.log(Level.WARNING, "Failed returning to agent confirm dialog results",e);
		}
	}
}
