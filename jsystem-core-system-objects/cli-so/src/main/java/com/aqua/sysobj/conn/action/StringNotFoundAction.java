/*
 * Created on Sep 24, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.sysobj.conn.action;

import jsystem.extensions.analyzers.text.TextNotFound;

/**
 * @author guy.arieli
 *
 */
public class StringNotFoundAction extends Action {
	String notFound;
	public StringNotFoundAction(String strNotFound){
		this.notFound = strNotFound;
	}

	/* (non-Javadoc)
	 * @see com.aqua.sysobj.conn.Action#act(java.lang.String)
	 */
	public void act() {
		analyze(new TextNotFound(notFound), true);
	}

}
