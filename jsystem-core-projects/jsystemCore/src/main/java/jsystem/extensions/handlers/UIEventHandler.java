package jsystem.extensions.handlers;

import java.util.HashMap;

import jsystem.framework.scenario.Parameter;

/**
 * Tests implementing this class will have support for dynamic parameters panel in the JRunner
 * 
 * @author Nizan Freedman
 *
 */
public interface UIEventHandler {

	public void handleUIEvent(HashMap<String,Parameter> map,String methodName) throws Exception;
}
