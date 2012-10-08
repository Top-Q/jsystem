/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * VT100 parser
 * 
 * @author guy.arieli
 * @author aqua ohad.crystal
 * @link http://www.cs.utk.edu/~shuford/terminal/vt100_reference_card.txt
 */
public class VT100 {
	int screenHight = 25;

	int screenWidth = 80;

	String host;

	int port = 23;

	Socket socket;

	ScreenReader reader;
	
	long basicDelay = 500; // 500 mili sec.
	long lastCommand = 0;
	public VT100(String host) {
		this.host = host;
	}

	public void init() throws Exception {
		socket = new Socket(host, port);
		reader = new ScreenReader(screenHight, screenWidth, socket.getInputStream());
		reader.setName(Thread.currentThread().getName());
		reader.start();
	}
	
	public void close() throws Exception{
		socket.close();
	}

	/**
	 * Send the command
	 * 
	 * @param cmd
	 *            String of the command
	 * @throws Exception
	 */
	public void sendCommand(String cmd) throws Exception {
		long timeToWait = basicDelay - (System.currentTimeMillis() - lastCommand);
		if(timeToWait > 0){
			Thread.sleep(timeToWait);
		}
		socket.getOutputStream().write(cmd.getBytes());
		socket.getOutputStream().flush();
		lastCommand = System.currentTimeMillis();
	}

// DEBUG ( Main)
//	public static void main(String[] args) {
//		VT100 vt100 = new VT100("172.17.161.41");
//		try {
//			vt100.init();
//
//			vt100.sendCommand("\n");
//			Thread.sleep(1000);
//			System.out.print(vt100.getScreen());
//			vt100.sendCommand("su\n");
//			Thread.sleep(1000);
//			System.out.print(vt100.getScreen());
//			vt100.sendCommand("1234\n");
//			Thread.sleep(1000);
//			System.out.print(vt100.getScreen());
//			vt100.sendCommand("2\n");
//			Thread.sleep(1000);
//			System.out.print(vt100.getScreen());
//			vt100.sendCommand("1\n");
//			Thread.sleep(1000);
//			System.out.print(vt100.getScreen());
//			vt100.sendCommand("3\n");
//			Thread.sleep(1000);
//			System.out.print(vt100.getScreen());
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public String getScreen() {
		return reader.getScreen();
	}

	public int getScreenHight() {
		return screenHight;
	}

	public void setScreenHight(int screenHight) {
		this.screenHight = screenHight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public long getBasicDelay() {
		return basicDelay;
	}

	public void setBasicDelay(long basicDelay) {
		this.basicDelay = basicDelay;
	}

}

class ScreenReader extends Thread {
	InputStream in;

	int screenHight = 25;

	int screenWidth = 80;

	public ScreenReader(int screenHight, int screenWidth, InputStream in) {
		this.screenHight = screenHight;
		this.screenWidth = screenWidth;
		this.in = in;
		screen = new char[screenHight][screenWidth];
		cleanScreen();
	}

	int currentRow = 0;

	int currentColumn = 0;

	int savedRow = 0;

	int savedColumn = 0;

	char[][] screen;

	public void run() {
		int c;
		try {
			while ((c = in.read()) >= 0) {
				switch (c) {
				case 0x1B:// 'ESC'
					int c2 = in.read();
					switch (c2) {
					case '[':
						int c3 = in.read();
						if (c3 == 'm') {
							break;
						}
						int c4 = in.read();
						if (c4 == 'K') {// Erasing from cursor to end of line
							for (int i = currentColumn; i < screenWidth; i++) {
								screen[currentRow][i] = ' ';
							}
							break;
						}
						if (c3 == '2' && c4 == 'J') {
							cleanScreen();
							break;
						}
						if (c3 == '0' && c4 == 'J') {
							// cleanScreen();
							break;
						}
						if ((c3 == '4' || c3 == '7') && c4 == 'm') {
							break;
						}

						if (c3 == '0' && c4 == ';') {
							in.read();
							in.read();
							break;
						}
						if (c3 == '1' && c4 == 'J') {
							cleanToCorsor();
							break;
						}
						int c5 = in.read();
						if (c5 != ';') {
							if(c5 == 'D'){
								currentColumn = 0;
								break;
							}
							System.err.println("There could be a problem c5: " + (char) c5 + " "
									+ (char) c3 + (char) c4);
						}
						int c6 = in.read();
						int c7 = in.read();
						int c8 = in.read();
						if (c8 != 'H') {
							// Do Nothing
							// System.err.println("There could be a problem c8:
							// " + c8);
						}
						currentRow = Integer.parseInt(new String(
								new char[] { (char) c3, (char) c4 })) - 1;
						currentColumn = Integer.parseInt(new String(new char[] { (char) c6,
								(char) c7 })) - 1;
						break;
					case '7': // Save cursor and attributes
						savedRow = currentRow;
						savedColumn = currentColumn;
						break;
					case '8': // Restore cursor and attributes
						currentRow = savedRow;
						currentColumn = savedColumn;
						break;
					}
					break;
				default:
					screen[currentRow][currentColumn] = (char) c;
					currentColumn++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean all screen ( ESC [ 2 J )
	 */
	public void cleanScreen() {
		for (int i = 0; i < screenHight; i++) {
			for (int j = 0; j < screenWidth; j++) {
				screen[i][j] = ' ';
			}
		}

	}

	/**
	 * Clean screen from the begining [0][0] to the current cursot position
	 */
	public void cleanToCorsor() {
		for (int i = 0; i <= currentRow; i++) {
			if (i == currentRow) {
				for (int j = 0; j <= currentColumn; j++) {
					screen[i][j] = ' ';
				}
			} else {
				for (int j = 0; j < screenWidth; j++) {
					screen[i][j] = ' ';
				}
			}
		}
	}

	/**
	 * Get the screen as String
	 */
	public String getScreen() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < screenHight; i++) {
			for (int j = 0; j < screenWidth; j++) {
				buf.append(screen[i][j]);
			}
			buf.append("\n");

		}
		return buf.toString();
	}

	// ==== EXAMPLES =====================

	/*
	 * public void sendCommand(String command) throws Exception {
	 * sendCommand(new String[] { command }); }
	 * 
	 * 
	 * public void sendCommand(String[] commands) throws Exception {
	 * sendCommand(commands, false); }
	 * 
	 * 
	 * public void sendCommand(String[] commands, boolean showSteps) throws
	 * Exception { StringBuffer buf = new StringBuffer();
	 * 
	 * for (int i = 0; i < commands.length; i++) { if
	 * (commands[i].equals("ESC")) { vt100.sendCommand(new String(new char[] {
	 * '\u001B' })); } else if (commands[i].equals("MAIN_MENU")) {// Back to
	 * main menu vt100.sendCommand(new String(new char[] { '\u0021' }));// (!) }
	 * else { vt100.sendCommand(commands[i]); vt100.sendCommand("\n"); if
	 * (showSteps) { String screen = vt100.getScreen(); report.report(getName() + "
	 * sent command: " + commands[i], screen, true); }
	 *  }
	 * 
	 * buf.append(","); buf.append(commands[i]); } Thread.sleep(2000); String
	 * screen = vt100.getScreen(); report.report(getName() + " sent commands: " +
	 * buf.toString(), screen, true); setTestAgainstObject(screen); analyze(new
	 * TextNotFound("ERROR"), true); }
	 * 
	 */

}