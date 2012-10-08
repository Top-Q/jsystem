/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.IOException;

public class VT100FilterInputStream extends InOutInputStream {

	boolean debug = false;
	int column = 1;
	int row = 1;
	
	public VT100FilterInputStream() {
	}

	public int read() throws IOException {
		int c = in.read();
		while (true) {
			if (c == 0x1B) {
				c = filter();
			} else {
				if (c == '\n' || c == '\r') {
					column = 1;
					row++;
				} else {
					column++;
				}
				return c;
			}
		}
	}

	private int filter() throws IOException {

		StringBuffer buf = new StringBuffer();

		try {

			if (available() <= 0) {

				return readIfAvailable();

			}

			int c = in.read();

			buf.append((char) c);

			switch (c) {

			case '[':

				if (available() <= 0) {

					return readIfAvailable();

				}

				int c2 = in.read();

				buf.append((char) c2);

				int pl = 0;

				if (isDigit(c2)) { // ^[1

					pl = Character.digit(c2, 10);

					if (available() <= 0) {

						return readIfAvailable();

					}

					int c3 = in.read();

					buf.append((char) c3);

					if (isDigit(c3)) { // ^[12

						pl = pl * 10 + Character.digit(c3, 10);

						if (available() <= 0) {

							return readIfAvailable();

						}

						c3 = in.read();

						buf.append((char) c3);

					}

					switch (c3) {

					case 'A': // * ESC [ pn A cursor up pn times - stop at top

					case 'C': // * ESC [ pn C cursor right pn times - stop at
								// far right

					case 'D': // * ESC [ pn D cursor left pn times - stop at
								// far left

					case 'm':

					case 'q':

						return readIfAvailable();

					case 'B': // * ESC [ pn B cursor down pn times - stop at
								// bottom

						if (debug) {

							System.err.println("\nESC [ pn B found");

						}

						return '\n';

					case ';':

						if (available() <= 0) {

							return readIfAvailable();

						}

						int pc = 0;

						int c4 = in.read();

						buf.append((char) c4);

						if (isDigit(c4)) {

							pc = Character.digit(c4, 10);

							if (available() <= 0) {

								return readIfAvailable();

							}

							int c5 = in.read();

							buf.append((char) c5);

							if (isDigit(c5)) { // jump

								pc = pc * 10 + Character.digit(c5, 10);

								if (available() <= 0) {

									return readIfAvailable();

								}

								c5 = in.read();

								buf.append((char) c5);

							}

							// * ESC [ pl ; pc H set cursor position - pl Line,
							// pc Column

							// * ESC [ pl ; pc f set cursor position - pl Line,
							// pc Column

							if (c5 == 'H' || c5 == 'f') {

								if (debug) {

									System.err
											.println("\nESC [ pl ; pc H or f found: "
													+ buf.toString());

								}

								if (column == pc && row == pl) {
									return readIfAvailable();
								}

								row = pl;

								return '\n';

							}

							if (c5 == 'r' || c5 == 'R' || c5 == 'y'
									|| c5 == 'm') {

								return readIfAvailable();

							} else {

								return c5;

							}

						}

					default:

						return c3;

					}

				}

				switch (c2) {

				case 'H': // * ESC [ H set cursor home

				case 'f': // * ESC [ H set cursor home

					if (debug) {

						System.err.println("\nESC [ H found");

					}

					return '\n';

				case 'm':

				case 'K':

				case 'J':

				case 'g':

				case 'i':

				case 'n':

				case 'c':

					return readIfAvailable();

				case '?':

					if (available() <= 0) {

						return readIfAvailable();

					}

					c2 = in.read();

					buf.append((char) c2);

					if (isDigit(c2)) {

						if (available() <= 0) {

							return readIfAvailable();

						}

						int c3 = in.read();

						buf.append((char) c3);

						if (isDigit(c3)) { // jump

							if (available() <= 0) {

								return readIfAvailable();

							}

							c3 = in.read();

							buf.append((char) c3);

						}

						if (c3 == 'h' || c3 == 'l' || c3 == 'i' || c3 == 'n') {

							return readIfAvailable();

						} else {

							return c3;

						}

					} else {

						return c2;

					}

				}

				if (isDigit(c2)) {

					if (available() <= 0) {

						return readIfAvailable();

					}

					int c3 = in.read();

					buf.append((char) c3);

					if (c3 == 'J' || c3 == 'K' || c3 == 'g' || c3 == 'i'
							|| c3 == 'n' || c3 == 'c') {

						return readIfAvailable();

					} else {

						return c3;

					}

				}

			case '(':

			case ')':

				if (available() <= 0) {

					return readIfAvailable();

				}

				c2 = in.read();

				buf.append((char) c2);

				if (c2 == 'A' || c2 == 'B' || c2 == '0') {

					return readIfAvailable();

				} else {

					return c2;

				}

			case '#':

				if (available() <= 0) {

					return readIfAvailable();

				}

				c2 = in.read();

				buf.append((char) c2);

				if (isDigit(c2)) {

					return readIfAvailable();

				} else {

					return c2;

				}

			case 'D': // * ESC D cursor down - at bottom of region, scroll up

			case 'E': // * ESC E next line (same as CR LF)

				if (debug) {

					System.err.println("\nESC D or E found");

				}

				return '\n';

			case 'M':

			case '7':

			case '8':

			case '=':

			case '>':

			case 'N':

			case 'O':

			case 'H':

			case 'c':

			case '<':

			case 'Z':

				return readIfAvailable();

			default:

				return c;

			}

		} finally {

			// System.err.println("Found tag: " + buf.toString());

		}

	}

	private static boolean isDigit(int c) {

		return c >= '0' && c <= '9';

	}

	public int available() throws IOException {

		return in.available();

	}

	private int readIfAvailable() throws IOException {

		if (in.available() > 0) {

			return in.read();

		} else {

			try {

				Thread.sleep(10);

			} catch (InterruptedException e) {

				throw new IOException("interrupted");

			}

			if (in.available() > 0) {

				return in.read();

			} else {

				return ' ';

			}

		}

	}

}/*
	 * VT100 commands and control sequences edited by Paul Bourke
	 * 
	 * The following are the VT100 commands as described by the Digital VT101
	 * Video Terminal User Guide (EK-VT101-UG-003). An asterik (*) beside the
	 * function indicate that it is currently supported. A plus (+) means the
	 * function is trapped and ignored.
	 * 
	 * Scrolling Functions:
	 *  # * ESC [ pt ; pb r set scroll region # * ESC [ ? 6 h turn on region -
	 * origin mode # * ESC [ ? 6 l turn off region - full screen mode
	 * 
	 * Cursor Functions:
	 *  # * ESC [ pn A cursor up pn times - stop at top # * ESC [ pn B cursor
	 * down pn times - stop at bottom #####* # * ESC [ pn C cursor right pn
	 * times - stop at far right # * ESC [ pn D cursor left pn times - stop at
	 * far left # * ESC [ pl ; pc H set cursor position - pl Line, pc Column
	 * #####* # * ESC [ H set cursor home #####* # * ESC [ pl ; pc f set cursor
	 * position - pl Line, pc Column #####* # * ESC [ f set cursor home #####* # *
	 * ESC D cursor down - at bottom of region, scroll up #####* # * ESC M
	 * cursor up - at top of region, scroll down # * ESC E next line (same as CR
	 * LF) #####* # * ESC 7 save cursor position(char attr,char set,org) # * ESC
	 * 8 restore position (char attr,char set,origin)
	 * 
	 * Applications / Normal Mode:
	 *  # * ESC [ ? 1 h cursor keys in applications mode # * ESC [ ? 1 l cursor
	 * keys in cursor positioning mode # * ESC = keypad keys in applications
	 * mode # * ESC > keypad keys in numeric mode
	 * 
	 * Character Sets:
	 *  # * ESC ( A UK char set as G0 # * ESC ( B US char set as G0 # * ESC ( 0
	 * line char set as G0 # * ESC ) A UK char set as G1 # * ESC ) B US char set
	 * as G1 # * ESC ) 0 line char set as G1 # * ESC N select G2 set for next
	 * character only # * ESC O select G3 set for next character only
	 * 
	 * Character Attributes:
	 *  # * ESC [ m turn off attributes - normal video # * ESC [ 0 m turn off
	 * attributes - normal video #!* ESC [ 4 m turn on underline mode # * ESC [
	 * 7 m turn on inverse video mode # * ESC [ 1 m highlight # * ESC [ 5 m
	 * blink
	 *  ! On color systems underlined characters are displayed in blue
	 * 
	 * Line Attributes:
	 *  # + ESC # 3 double high (top half) - double wide # + ESC # 4 double high
	 * (bottom half) - double wide # + ESC # 5 single wide - single height # +
	 * ESC # 6 double wide - single height
	 * 
	 * Erasing:
	 *  # * ESC [ K erase to end of line (inclusive) # * ESC [ 0 K erase to end
	 * of line (inclusive) # * ESC [ 1 K erase to beginning of line (inclusive) # *
	 * ESC [ 2 K erase entire line (cursor doesn't move) # * ESC [ J erase to
	 * end of screen (inclusive) # * ESC [ 0 J erase to end of screen
	 * (inclusive) # * ESC [ 1 J erase to beginning of screen (inclusive) # *
	 * ESC [ 2 J erase entire screen (cursor doesn't move)
	 * 
	 * Tabulation:
	 *  # * ESC H set tab in current position # * ESC [ g clear tab stop in
	 * current position # * ESC [ 0 g clear tab stop in current position # * ESC [
	 * 3 g clear all tab stops
	 * 
	 * Printing:
	 *  # * ESC [ i print page # * ESC [ 0 i print page # * ESC [ 1 i print line # *
	 * ESC [ ? 4 i auto print off # * ESC [ ? 5 i auto print on # + ESC [ 4 i
	 * print controller off # + ESC [ 5 i print controller on
	 * 
	 * Requests / Reports:
	 *  # * ESC [ 5 n request for terminal status # ESC [ 0 n report - no
	 * malfunction # * ESC [ 6 n request for cursor position report # ESC [
	 * pl;pc R report - cursor at line pl, & column pc ESC [ ? 1 5 n request
	 * printer status ESC [ ? 1 0 n report - printer ready # * ESC [ c request
	 * to identify terminal type # * ESC [ 0 c request to identify terminal type # *
	 * ESC Z request to identify terminal type ESC [ ? 1;0 c report - type VT100
	 * 
	 * Initialization / Tests:
	 *  # + ESC c reset to initial state # + ESC [ 2 ; 1 y power up test # + ESC [
	 * 2 ; 2 y loop back test # + ESC [ 2 ; 9 y power up test till failure or
	 * power down # + ESC [ 2 ; 10 y loop back test till failure or power down # +
	 * ESC # 8 video alignment test-fill screen with E's
	 * 
	 * 
	 * Setup Functions:
	 *  # + ESC [ ? 2 l enter VT52 mode # + ESC < exit VT52 mode # + ESC [ ? 3 h
	 * 132 column mode # + ESC [ ? 3 l 80 column mode # + ESC [ ? 4 h smooth
	 * scroll # + ESC [ ? 4 l jump scroll # * ESC [ ? 5 h black characters on
	 * white screen mode # * ESC [ ? 5 l white characters on black screen mode # *
	 * ESC [ ? 7 h auto wrap to new line # * ESC [ ? 7 l auto wrap off # + ESC [ ?
	 * 8 h keyboard auto repeat mode on # + ESC [ ? 8 l keyboard auto repeat
	 * mode off # + ESC [ ? 9 h 480 scan line mode # + ESC [ ? 9 l 240 scan line
	 * mode # * ESC [ ? 1 8 h print form feed on # * ESC [ ? 1 8 l print form
	 * feed off # * ESC [ ? 1 9 h print whole screen # * ESC [ ? 1 9 l print
	 * only scroll region # + ESC [ 2 0 h newline mode LF, FF, VT, CR = CR/LF) # +
	 * ESC [ 2 0 l line feed mode (LF, FF, VT = LF ; CR = CR)
	 * 
	 * LED Functions:
	 * 
	 * #!* ESC [ 0 q turn off LED 1-4 #!* ESC [ 1 q turn on LED #1 #!* ESC [ 2 q
	 * turn on LED #2 #!* ESC [ 3 q turn on LED #3 #!* ESC [ 4 q turn on LED #4
	 *  ! The bottom line of the screen is used as a status line by the VT100
	 * emulation. The information on the bottom line is:
	 * 
	 * 1) the status of the four VT100 LED's 2) the status of the numeric keypad
	 * (application mode /normal mode) 3) the status of the cursor keypad
	 * (application mode/normal mode)
	 * 
	 * 
	 * Interpreted Control Characters:
	 * 
	 * ^O shift in - selects G0 character set ^N shift out - selects G1
	 * character set
	 * 
	 * 
	 * VT100 KEYBOARD MAP
	 * 
	 * The following table describes the special function keys of the VT100 and
	 * shows the transmitted sequences. It also shows the key or key sequence
	 * required to produce this function on the IBM-PC keyboard. The VT100 has
	 * four function keys PF1 - PF4, four arrow keys, and a numeric keypad with
	 * 0-9, ".", "-", RETURN and ",". The numeric keypad and the arrow keys may
	 * be in standard mode or applications mode as set by the host computer.
	 * Sequences will be sent as follows:
	 * 
	 * To Get Press Key on VT100 Key Standard Applications IBM Keypad
	 * =====================================================
	 * 
	 * NUMLOK - On Keypad:
	 * 
	 * 0 0 ESC O p 0 1 1 ESC O q 1 2 2 ESC O r 2 3 3 ESC O s 3 4 4 ESC O t 4 5 5
	 * ESC O u 5 6 6 ESC O v 6 7 7 ESC O w 7 8 8 ESC O x 8 9 9 ESC O y 9 - - ESC
	 * O m - , , ESC O l * (on PrtSc key) . . ESC O n . Return Return ESC O M +
	 * 
	 * 
	 * NUMLOK - Off Arrows:
	 * 
	 * Up ESC [ A ESC O A Up Down ESC [ B ESC O B Down Right ESC [ C ESC O C
	 * Right Left ESC [ D ESC O D Left
	 * 
	 * Up ESC [ A ESC O A Alt 9 Down ESC [ B ESC O B Alt 0 Right ESC [ C ESC O C
	 * Alt - Left ESC [ D ESC O D Alt =
	 * 
	 * Note that either set of keys may be used to send VT100 arrow keys. The
	 * Alt 9,0,-, and = do not require NumLok to be off.
	 * 
	 * Functions:
	 * 
	 * PF1 - Gold ESC O P ESC O P F1 PF2 - Help ESC O Q ESC O Q F2 PF3 - Next
	 * ESC O R ESC O R F3 PF4 - DelBrk ESC O S ESC O S F4
	 * 
	 * 
	 * Please note that the backspace key transmits an ascii DEL (character 127)
	 * while in VT100 emulation. To get a true ascii backspace (character 8) you
	 * must press control-backspace.
	 * 
	 * 
	 * 
	 */