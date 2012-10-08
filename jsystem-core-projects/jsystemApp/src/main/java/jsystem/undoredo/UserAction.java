/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.undoredo;

public interface UserAction {
	
	public boolean undo() throws Exception;
	
	public boolean redo()throws Exception;
}
