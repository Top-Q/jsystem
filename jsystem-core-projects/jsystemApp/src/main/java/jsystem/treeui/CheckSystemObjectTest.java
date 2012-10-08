/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.util.ArrayList;
import java.util.Vector;

import jsystem.framework.report.ListenerstManager;
import jsystem.framework.system.SystemManagerImpl;
import jsystem.framework.system.SystemObject;
import jsystem.framework.system.SystemObjectManager;
import jsystem.runner.SOCheckStatus;
import junit.framework.SystemTestCase;

public class CheckSystemObjectTest extends SystemTestCase {
	public void testSystemObjects() throws Exception{
		if(!(report instanceof ListenerstManager)){
			/*
			 * Can't execute the test as it run in eclipse and not from the runner.
			 */
			return;
		}
		Vector<String> list = SystemManagerImpl.getAllObjects(true);
		/*
		 * Collect all the System Objects in the SUT
		 */
//		List list = sut.getAllValues("sut/*/class");
		ArrayList<ProcessSystemObject>systemObjectProcessThreads = new ArrayList<ProcessSystemObject>();
		for (int i = 0; i < list.size(); i++) {
			String so = list.elementAt(i);
			if(so == null){
				continue;
			}
			ProcessSystemObject pso = new ProcessSystemObject(so, system, (ListenerstManager)report);
			pso.start();
			systemObjectProcessThreads.add(pso);
		}
		long startJoinTime = System.currentTimeMillis();
		while (true){
			if(systemObjectProcessThreads.size() == 0){
				return;
			}
			if(System.currentTimeMillis() - startJoinTime > 20000){
				return;
			}
			for(ProcessSystemObject pso: systemObjectProcessThreads){
				if(!pso.isAlive()){
					systemObjectProcessThreads.remove(pso);
					break;
				}
			}
			Thread.sleep(200);
		}
	}
	
	
	
	
}

class ProcessSystemObject extends Thread{
	String soName;
	SystemObjectManager system;
	ListenerstManager lm;
	public ProcessSystemObject(String soName, SystemObjectManager system, ListenerstManager lm){
		this.soName = soName;
		this.system = system;
		this.lm = lm;
	}
	
	public void run(){
		SystemObject so;
		try {
			lm.checkSystemObjectStatus(soName, SOCheckStatus.INITTING, null);
			so = system.getSystemObject(soName);
			lm.checkSystemObjectStatus(soName, SOCheckStatus.INIT_SUCESS, null);
		} catch (Exception e) {
			lm.checkSystemObjectStatus(soName, SOCheckStatus.INIT_FAIL, null);
			return;
		}
		try {
			so.check();
			int checkStatus = so.getCheckStatus();
			if(checkStatus == SystemObject.CHECK_NOT_IMPL){
				lm.checkSystemObjectStatus(soName, SOCheckStatus.CHECK_NOT_IMPLEMENTED, null);
			} else if(checkStatus == SystemObject.CHECK_CONNECTED){
				lm.checkSystemObjectStatus(soName, SOCheckStatus.CHECK_SUCESS, null);
			} else {
				lm.checkSystemObjectStatus(soName, SOCheckStatus.CHECK_FAIL, null);
			}

		} catch (Exception e) {
			lm.checkSystemObjectStatus(soName, SOCheckStatus.CHECK_FAIL, e.getMessage());
		}
	}
}
