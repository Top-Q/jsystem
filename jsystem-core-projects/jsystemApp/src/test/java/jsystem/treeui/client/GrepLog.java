/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.client;

import java.io.File;
import java.io.FileInputStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This utility class provides a subset of the grep functionality.
 * 
 * @author Guy Chen
 */
public class GrepLog {

	// Charset and decoder for ISO-8859-15
	private static Charset charset = Charset.forName("ISO-8859-15");
	private static CharsetDecoder decoder = charset.newDecoder();

	// Pattern used to parse lines
	private static Pattern linePattern = Pattern.compile(".*\r?\n");

	// The input pattern that we're looking for
	private static Pattern pattern;

	// Compile the pattern from the command line
	//
	private static void compile(String pat) {
		try {
			pattern = Pattern.compile(pat);
		} catch (PatternSyntaxException x) {
			System.err.println(x.getMessage());
			System.exit(1);
		}
	}

	public boolean grep(File file, String strToSearch) {
		compile(strToSearch);
		boolean bFound = false;

		// Open the file and then get a channel from the stream
		try {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();

			// Get the file's size and then map it into memory
			int sz = (int) fc.size();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

			// Decode the file into a char buffer
			CharBuffer cb = decoder.decode(bb);

			// Perform the search

			Matcher lm = linePattern.matcher(cb); // Line matcher
			Matcher pm = null; // Pattern matcher

			int lines = 0;

			while (lm.find()) {
				lines++;
				CharSequence cs = lm.group(); // The current line
				if (pm == null)
					pm = pattern.matcher(cs);
				else
					pm.reset(cs);
				if (pm.find()) {
					System.out.print(file + ":" + lines + ":" + cs);
					bFound = true;
				}

				if (lm.end() == cb.limit())
					break;
			}
			// Close the channel and the stream
			fc.close();

		} catch (Exception e) {
			System.out.println(e.toString());
			return bFound;
		}
		return bFound;
	}

}
