/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.IOException;

public class NotepadLauncher {

	public static void main(String[] args) {
		System.out.println("Launch notepad");
		Process p;
		try {
			p = Runtime.getRuntime().exec("c:\\winnt\\notepad.exe");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			p.waitFor();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("Notepad closed");

	}
}
