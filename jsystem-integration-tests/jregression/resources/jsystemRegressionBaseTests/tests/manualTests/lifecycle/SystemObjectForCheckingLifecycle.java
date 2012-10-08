package manualTests.lifecycle;

import java.io.File;
import java.util.HashMap;

import jsystem.framework.system.SystemObjectImpl;

public class SystemObjectForCheckingLifecycle extends SystemObjectImpl {

	public static final File LIFECYCLE_FILE = new File("lifecycle.log");
	
	private SystemObjectState CURRENT_STATE = SystemObjectState.CLASS_LOADED;  
	
	private boolean waitOnClose = false;
	
	public SystemObjectForCheckingLifecycle() throws Exception{
		CURRENT_STATE = SystemObjectState.CONSTRACTOR_ACTIVATED;
	}
	
	public void init() throws Exception {
		super.init();
		CURRENT_STATE = SystemObjectState.INIT_ACTIVATED;
		writeStateToPropFile();
	}
	
	public void close(){
		super.close();
		if (waitOnClose){
			try {Thread.sleep(100000000);}catch(Exception e){}
		}
		CURRENT_STATE = SystemObjectState.CLOSE_ACTIVATED;
		writeStateToPropFile();
	}

	private void writeStateToPropFile(){
		System.out.println(getTagName() + ": " + CURRENT_STATE.toString());
		System.out.flush();
		//logger.log(Level.FINE,getTagName() + ": " + CURRENT_STATE.toString());
	}
	public String toString(){
		return getName() + ":"+ CURRENT_STATE.toString();
	}
	
	public enum SystemObjectState {
		
		CLASS_LOADED,
		CONSTRACTOR_ACTIVATED,
		INIT_ACTIVATED,
		CLOSE_ACTIVATED;
		
		private static HashMap<SystemObjectState,String> map = new HashMap<SystemObjectState, String>();
		static {
			map.put(CLASS_LOADED, "CLASS_LOADED");
			map.put(CONSTRACTOR_ACTIVATED, "CONSTRACTOR_ACTIVATED");
			map.put(INIT_ACTIVATED, "INIT_ACTIVATED");
			map.put(CLOSE_ACTIVATED, "CLOSE_ACTIVATED");
		}
		public String toString(){
			return map.get(this);
		}
	}

	public boolean isWaitOnClose() {
		return waitOnClose;
	}

	public void setWaitOnClose(boolean waitOnClose) {
		this.waitOnClose = waitOnClose;
	}
}
