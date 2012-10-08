/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.publisher;

/**
 * 
 * PublisherRunInfoFrame use this interface to
 * get notification from other windows
 */
public interface RunInfoFrameListener {
  
	public void visible(boolean visible);
	public void close ();
}
