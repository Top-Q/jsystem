/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.util.Stack;
import java.util.logging.Logger;

import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.framework.scenario.flow_control.AntIfElse;
import jsystem.framework.scenario.flow_control.AntIfElseIf;
import jsystem.framework.scenario.flow_control.AntSwitchCase;
import jsystem.framework.scenario.flow_control.AntSwitchDefault;

/**
 * used to analyze a test start and create events on containers starting and ending 
 * 
 * @author Nizan Freedman
 *
 */
public class EventParser {

	private Logger log = Logger.getLogger(EventParser.class.getName());
	private Stack<LoopHandler> loopStack;
	private Stack<JTestContainer> containersStack;
	private boolean loopWasClosed;
	
	RunnerListenersManager manager;
	
	
	public EventParser(RunnerListenersManager manager){
		this.manager = manager;
		init();
	}
	
	public void init(){
		loopStack = new Stack<LoopHandler>();
		containersStack = new Stack<JTestContainer>();
	}
	
	/**
	 * notify EventParser on test start<br>
	 * the parser analyzes the containers start and end and sends the appropriate events back to RunnerListenersManager
	 * 
	 * @param test	the RunnerTest that started
	 */
	public void startTest(RunnerTest test){
		JTestContainer container = test.getParent();
		try{
			notifyOnAllContainers(container,test,true);
		}catch (Exception e) {
			log.warning("Failed creating correct hierarchical reports");
		}
	}
	
	/**
	 * create notify events on opening and closing of containers
	 * 
	 * @param container
	 */
	private void notifyOnAllContainers(JTestContainer container, JTest test, boolean checkForLoopStart){
		if (container == null){
			return;
		}
		boolean checkParentForLoop = true;
		
		/**
		 * if a new loop is starting, check if increase loop number.
		 * if loop counter is too big (loop number is greater then the loop values,
		 * signal closing of all container and start a new loop of parent loop.
		 * if the loop counter is OK, start a new loop.
		 */
		if (checkForLoopStart && checkIfLoopStart(test)){
			checkParentForLoop = false;
			LoopHandler handler = loopStack.peek();
			AntForLoop loop = handler.getLoop();
			int counter = handler.getCurrentLoopNumber(); 
			int loopNum = loop.getNumOfLoops();
			if (loopNum > -1 && loopNum <= counter){ // for finished
				loopStack.pop();
				loop = loopStack.peek().getLoop();
				loopStack.push(handler);
				notifyOnContainersClosing(loop);
				notifyOnAllContainers(test.getParent(), test,true);
				return;
			}
			handler.startNewLoop();
			restartLoop(loop, counter);
		}
		
		/**
		 * close all finished containers
		 */
		if (containersStack.contains(container)){
			notifyOnContainersClosing(container);
			if (loopWasClosed){ // check if current loop starts a new loop
				notifyOnAllContainers(container, test, true);
			}
		}
		/**
		 * if container is new first add all new parents and then itself
		 */
		else {
			notifyOnAllContainers(container.getParent(),container,checkParentForLoop);
			
			startContainer(container);
			
			if (container instanceof AntForLoop){
				LoopHandler handler = new LoopHandler((AntForLoop)container,test);
				loopStack.push(handler);
				manager.startLoop((AntForLoop)container,1);
			}
		}
	}
	
	/**
	 * send notifications that a loop has restarted (ended and started again)<br>
	 * 1) send end loop number<br>
	 * 2) close all relevant containers<br>
	 * 3) send start loop number 
	 * 
	 * @param loop	the AntForLoop object
	 * @param counter	loop number
	 */
	private void restartLoop(AntForLoop loop, int counter){
		manager.endLoop(loop,counter);
		notifyOnContainersClosing(loop);
		manager.startLoop(loop,counter+1);
	}
	
	/**
	 * check if the test is the first in a new loop
	 * @param test	the test to check
	 * @return
	 */
	private boolean checkIfLoopStart(JTest test){
		if (loopStack.size() == 0){
			return false;
		}
		LoopHandler handler = loopStack.peek();
		if (test instanceof AntSwitchCase || test instanceof AntSwitchDefault || test instanceof AntIfElse ||test instanceof AntIfElseIf){
			return handler.isLoopStart(test.getParent());
		}
		return handler.isLoopStart(test);
	}
	
	private void startContainer(JTestContainer container){
		manager.startContainer(container);
		containersStack.push(container);
	}
	
	/**
	 * close ended containers
	 * 
	 * @param container
	 */
	private void notifyOnContainersClosing(JTestContainer container){
		loopWasClosed = false;
		while (!containersStack.peek().equals(container)){
			JTestContainer closedContainer = containersStack.pop();
			if (closedContainer instanceof AntForLoop){
				LoopHandler handler = loopStack.pop();
				manager.endLoop((AntForLoop)closedContainer,handler.getCurrentLoopNumber());
				loopWasClosed = true;
			}
			manager.endContainer(closedContainer);
		}
	}
	
	/**
	 * close all open containers
	 */
	public void closeAllContainers(){
		while (!containersStack.isEmpty()){
			manager.endContainer(containersStack.pop());
		}
		init();
	}
}

/**
 * used for counting loop numbers
 * 
 * @author Nizan Freedman
 *
 */
class LoopHandler{
	
	int counter = 1;
	JTest firstLoopTest;
	AntForLoop loop;
	
	public LoopHandler(AntForLoop loop, JTest firstLoopTest){
		this.loop = loop;
		this.firstLoopTest = firstLoopTest;
	}
	
	public int startNewLoop(){
		return ++counter;
	}
	
	public boolean isLoopStart(JTest test){
		return firstLoopTest.equals(test);
	}
	
	public int getCurrentLoopNumber(){
		return counter;
	}

	public AntForLoop getLoop() {
		return loop;
	}
}