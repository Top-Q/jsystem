package sysobj;

import java.io.IOException;

import jsystem.framework.system.SystemObjectImpl;

public class FailSysObj extends SystemObjectImpl{
	public void init() throws Exception{
		throw new Exception("");
	}
}
