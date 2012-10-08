/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MiscUtils {
	public static String ping(String addr) {
		BufferedReader out;
		StringBuffer sb = new StringBuffer();

		try {
			Process p = Runtime.getRuntime().exec("ping " + addr);
			out = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = out.readLine()) != null) {
				sb.append(s + "\r\n");
			}
		} catch (IOException io) {
			return sb.toString() + "\n\n" + StringUtils.getStackTrace(io);
		}
		return sb.toString();
	}

	public static boolean isPing(String addr) {
		String result = ping(addr);
		return (result.indexOf("timed out") < 0 && result.indexOf("Reply from") >= 0);
	}

}
