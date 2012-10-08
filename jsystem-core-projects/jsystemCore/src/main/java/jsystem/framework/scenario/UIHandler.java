/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

/**
 * Added in order to support dynamic parameters feature.
 * An object implementing UIHandler will receive parameters change events
 * 
 * @author Nizan
 *
 */
public interface UIHandler {
	
	/**
	 * Signal a parameter change has occurred
	 * 
	 * @param params	the test Parameters array
	 * @return	True if HandleUIEvent was executed by the test
	 */
	public boolean handleUIEvent(Parameter[] params);
}
