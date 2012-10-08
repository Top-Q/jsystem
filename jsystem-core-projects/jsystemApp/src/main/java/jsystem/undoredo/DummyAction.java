/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.undoredo;

public class DummyAction extends BaseUserAction {
	
	private static int counter;
	private int index;

	public DummyAction(){
		index = counter++;
	}
	
	@Override
	public boolean redo() throws Exception {
		System.out.println("redo - " + index);
		Thread.sleep(500);
		return true;
	}

	@Override
	public boolean undo() throws Exception {
		System.out.println("undo - " + index);
		Thread.sleep(500);
		return true;
	}

}
