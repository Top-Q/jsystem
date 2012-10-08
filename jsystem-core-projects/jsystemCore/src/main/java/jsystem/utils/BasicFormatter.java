/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.Date;
import java.text.MessageFormat;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * The BasicFormatter is used as the default formatar of messages to the java
 * logger.
 * 
 * @author guy.arieli
 * 
 */
public class BasicFormatter extends Formatter {
	Date dat = new Date();

	private final static String format = "{0,date} {0,time}";

	private MessageFormat formatter;

	private Object args[] = new Object[1];

	private String lineSeparator = (String) java.security.AccessController
			.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	private static long sessionStartTime = System.currentTimeMillis();

	public String format(LogRecord record) {
		StringBuffer sb = new StringBuffer();
		dat.setTime(record.getMillis());
		args[0] = dat;
		StringBuffer text = new StringBuffer();
		if (formatter == null) {
			formatter = new MessageFormat(format);
		}
		formatter.format(args, text, null);
		sb.append(text);
		sb.append(" (");
		sb.append(System.currentTimeMillis() - sessionStartTime);
		sb.append(") ");
		if (record.getSourceClassName() != null) {
			sb.append(StringUtils.getClassName(record.getSourceClassName()));
		} else {
			sb.append(StringUtils.getClassName(record.getLoggerName()));
		}
		if (record.getSourceMethodName() != null) {
			sb.append(" ");
			sb.append(record.getSourceMethodName());
		}
		// sb.append(lineSeparator);
		sb.append(" ");
		String message = formatMessage(record);
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");
		sb.append(message);
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}
}
