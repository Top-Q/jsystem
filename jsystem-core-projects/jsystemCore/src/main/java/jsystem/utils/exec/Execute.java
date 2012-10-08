/*
 * Created on 05/05/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.exec;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import jsystem.utils.StringUtils;

public class Execute {

	public static void execute(Command command, boolean block) throws Exception {
		execute(command, block, true, true);
	}

	public static void execute(Command command, boolean block, boolean print, boolean buffer) throws Exception {
		execute(command, block, print, buffer, true);
	}
	
	public static void execute(Command command, boolean block, boolean print, boolean buffer, boolean process) throws Exception {
		Process p = null;
		String[] env = null;
		if (command.getEnvParams() != null) { // Don't change the environemnt
												// params
			/*
			 * Get the current environment
			 */
			HashMap<String, String> envMap = new HashMap<String, String>(System.getenv());
			/*
			 * Add teh new params to the current environment
			 */
			String[] commandEnv = command.getEnvParams();
			for (int i = 0; i < commandEnv.length; i++) {
				int equalIndex = commandEnv[i].indexOf('=');
				if (equalIndex < 0) {
					envMap.put(commandEnv[i], "");
				} else {
					envMap.put(commandEnv[i].substring(0, equalIndex), commandEnv[i].substring(equalIndex + 1));
				}
			}
			/*
			 * change it to array format
			 */
			env = new String[envMap.size()];
			Iterator<String> keys = envMap.keySet().iterator();
			int envIndex = 0;
			while (keys.hasNext()) {
				String key = keys.next();
				env[envIndex] = key + "=" + envMap.get(key);
				envIndex++;
			}
		}
		p = Runtime.getRuntime().exec(command.getCmd(), env, command.getDir());

		command.setProcess(p);
		if(!process){
			return;
		}
		CommandRunner cr = new CommandRunner(command, print, buffer);
		cr.start();
		if (block) {
			cr.join(command.getTimeout());
			if (cr.isAlive()) {
				command.getStderr().append("Timeout: " + command.getTimeout());
				cr.interrupt();
				cr.join();
			}
		}
	}
}

class CommandRunner extends Thread {
	Command command;

	Process p;

	boolean print = true;

	boolean buffer = true;

	public CommandRunner(Command command, boolean print, boolean buffer) {
		this.command = command;
		this.p = command.getProcess();
		this.print = print;
		this.buffer = buffer;
	}

	public void run() {
		try {
			Reader in = new Reader(p.getInputStream(), command.getStdout(), command.getStd(), print, buffer);
			Reader err = new Reader(p.getErrorStream(), command.getStderr(), command.getStd(), print, buffer);
			in.start();
			err.start();
			int exitValue = p.waitFor();
			command.setReturnCode(exitValue);
			Thread.sleep(500);
			in.setStop();
			err.setStop();
			in.join();
			err.join();
		} catch (Exception e) {
			command.setException(e);
			command.setReturnCode(-1);
			command.getStderr().append(StringUtils.getStackTrace(e));
		} finally {
			command.setExecEnd(true);
		}
	}
}

class Reader extends Thread {
	InputStream in;

	StringBuffer buf1;

	StringBuffer buf2;

	volatile boolean stop = false;

	boolean print = true;

	boolean buffer = true;

	public Reader(InputStream in, StringBuffer buf1, StringBuffer buf2, boolean print, boolean buffer) {
		super("Executer-Reader");
		this.in = in;
		this.buf1 = buf1;
		this.buf2 = buf2;
		this.print = print;
		this.buffer = buffer;
	}

	public void run() {
		try {
			while (!stop) {
				int avail = in.available();
				while (avail > 0) {
					int c = in.read();
					if (c < 0) {
						return;
					}
					if (buffer) {
						buf1.append((char) c);
						buf2.append((char) c);
					}
					if (print) {
						System.out.print((char) c);
					}
					avail--;
				}
				Thread.sleep(20);
			}
			Thread.sleep(20);
		} catch (Exception e) {
			// e.printStackTrace();
			return;
		}
	}

	public void setStop() {
		stop = true;
	}
}