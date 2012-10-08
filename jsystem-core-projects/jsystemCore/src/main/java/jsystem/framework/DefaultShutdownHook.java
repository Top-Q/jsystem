/*
 * Created on 17/05/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import jsystem.framework.system.SystemManagerImpl;

/**
 * @author guy.arieli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DefaultShutdownHook extends Thread {
	public DefaultShutdownHook() {
		super("DefaultShutdownHook");
	}

	public void run() {
		SystemManagerImpl.getInstance().closeAllObjects();
	}

}
