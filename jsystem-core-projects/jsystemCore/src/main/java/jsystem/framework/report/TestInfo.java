/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.report;

import java.io.Serializable;

/**
 * Start test event is fired with TestInfo
 *  
 * @author guy.arieli
 */
public class TestInfo implements Serializable{
	private static final long serialVersionUID = -3699068496267609652L;
	public String className = null;
	public String methodName = null;
	public String meaningfulName = null;
	public String comment = null;
	public String parameters = null;
	public int count = 0;
	public String fullUuid = null;
	public String basicName = null;
	public String code = null;
	public String classDoc = null;
	public String testDoc = null;
	public String userDoc = null;
	public boolean isHiddenInHTML;
	public String toString(){
		return "className" + className + "methodName "+methodName +" fullUuid" + fullUuid;
	}
}
