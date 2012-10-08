/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.teststable;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import jsystem.treeui.actionItems.RemoveItemAction;
/**
 * Key Listener used for keyboard operations performed on the scenario tree.
 * @author Itai Agmon
 *
 */
public class ScenarioTreeKeyHandler implements KeyListener {

	
	/**
	 * Enum that stores the name of the keys and the matching numbers
	 * @author Itai Agmon
	 *
	 */
	public enum Keys {
		DELETE(127), SPACE(32), CTRL(17), C(67), /*U(85),*/ V(86), X(88);

		private final int keyCode;

		Keys(int keyCode) {
			this.keyCode = keyCode;

		}

		public final int keyCode() {
			return keyCode;
		}

		public static Keys getKey(int code) {
			for (Keys key : Keys.values()) {
				if (key.keyCode() == code) {
					return key;
				}
			}
			return null;
		}
	}

	private TestsTableController ttc;

	public ScenarioTreeKeyHandler(TestsTableController ttc) {
		this.ttc = ttc;
	}

	private boolean ctrlPressed = false; 
	private boolean copyEvent = false;
	private boolean pasteEvent = false;
	private boolean moveEvent = false;
	
	public void keyPressed(KeyEvent e) {
		Keys key = Keys.getKey(e.getKeyCode());
		if (key == null) {
			return;
		}
		switch (key) {
		/**
		 * "Delete" key pressed on selected tests in the scenario tree.
		 */
		case DELETE:
			RemoveItemAction.getInstance().actionPerformed(null);	
			break;
		/**
		 * "Space" key
		 */
		case SPACE:
			ttc.handleMultipleNodesMap();
			break;
		case CTRL:
			ctrlPressed = true;
			break;
		case C:
			if(ctrlPressed){
				copyEvent = true;
			}
			break;
		case V:
			if(ctrlPressed){
				pasteEvent = true;
			}
			break;
//		case U:
//			if(ctrlPressed){
//				pasteEvent = true;
//			}
//			break;
		case X:
			if(ctrlPressed){
				moveEvent = true;
			}
			break;
		default:
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
		Keys key = Keys.getKey(e.getKeyCode());
		if (key == null) {
			return;
		}
		switch (key) {
		case CTRL:
			ctrlPressed = false;
			break;
		case C:
			if(copyEvent){
				ttc.saveClipboardTests();
				copyEvent = false;
			}
			break;
		case V:
			if(pasteEvent){
				ttc.addClipboardTests(false);
				pasteEvent = false;
			}
			break;
//		case U:
//			if(pasteEvent){
//				System.out.println("PASTE AFTER");
//				ttc.addClipboardTests(true);
//				pasteEvent = false;
//			}
//			break;
		case X:
			if(moveEvent){
				System.out.println("CUT");
				ttc.saveClipboardTests();
				RemoveItemAction.getInstance().actionPerformed(null);
				moveEvent = false;
			}
			break;
		default:
			break;
		}
	}

	public void keyTyped(KeyEvent e) {

	}

}
