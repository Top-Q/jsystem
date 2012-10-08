/*
 * Created on 05/05/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils.exec;

import java.io.File;

public class Command {
	
	int returnCode = -1;

	StringBuffer stderr;

	StringBuffer stdout;

	StringBuffer std;

	String[] cmd = null;

	/**
	 * The environment params
	 */
	String[] envParams = null;

	File dir;

	Process process;

	long timeout = 0;

	boolean execEnd = false;

	Exception exception;

	public Command() {
		stderr = new StringBuffer();
		stdout = new StringBuffer();
		std = new StringBuffer();
	}

	public String[] getCmd() {
		return cmd;
	}

	public void setCmd(String[] cmd) {
		this.cmd = cmd;
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) {
		this.dir = dir;
	}

	public boolean isExecEnd() {
		return execEnd;
	}

	public void setExecEnd(boolean execEnd) {
		this.execEnd = execEnd;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public StringBuffer getStd() {
		return std;
	}

	public void setStd(StringBuffer std) {
		this.std = std;
	}

	public StringBuffer getStderr() {
		return stderr;
	}

	public void setStderr(StringBuffer stderr) {
		this.stderr = stderr;
	}

	public StringBuffer getStdout() {
		return stdout;
	}

	public void setStdout(StringBuffer stdout) {
		this.stdout = stdout;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public String getCommandAsString() {
		if (cmd == null) {
			return null;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < cmd.length; i++) {
			buf.append(cmd[i]);
			if (i != cmd.length - 1) {
				buf.append(" ");
			}
		}
		return buf.toString();
	}

	public String[] getEnvParams() {
		return envParams;
	}

	/**
	 * Set the environment parameters to be used
	 * 
	 * @param envParams
	 */
	public void setEnvParams(String[] envParams) {
		this.envParams = envParams;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer();
		if(cmd != null){
			for(String c: cmd){
				buf.append(c);
				buf.append(' ');
			}
		}
		return buf.toString();
	}

}
