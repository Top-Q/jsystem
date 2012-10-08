/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.sql.Connection;

/**
 * classes who want to use DbGuiUtility connection checking should implement this to register to thread events
 * 
 * @author NorthTeam
 *
 */
public interface DBConnectionListener {

	/**
	 * signal if connection to DB is ok or not
	 * @param status	True means Tomcat and MySql are ok, False otherwise
	 * @param con	the Connection to the database if successful
	 */
	public void connectionIsOk(boolean status,Connection con);
	
}
