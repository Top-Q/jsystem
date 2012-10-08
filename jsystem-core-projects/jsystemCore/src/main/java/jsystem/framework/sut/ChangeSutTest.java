/*
 * Created on Jan 28, 2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import java.util.Vector;

import jsystem.framework.ParameterProperties;
import jsystem.framework.system.SystemManagerImpl;
import junit.framework.SystemTestCase;

/**
 * 
 * @author guy.arieli
 * 
 */
public class ChangeSutTest extends SystemTestCase {
	String sut = null;

	/**
	 * Change the SUT file the next tests in the scenario will be using.
	 * 
	 */
	public void changeSut() throws Exception {
		report.report("Close all SystemObjects");
		SystemManagerImpl.getInstance().closeAllObjects();
		SutFactory.getInstance().setSut(sut);
	}

	public String getSut() {
		return sut;
	}

	@ParameterProperties(description="Choose SUT file from the list")
	public void setSut(String sut) {
		this.sut = sut;
	}
	
	public String[] getSutOptions(){
		Vector<String> v = SutFactory.getInstance().getOptionalSuts();
		v.removeElement("Create a new SUT file...");
		String[] suts = new String[v.size()];
		v.toArray(suts);
		return suts;
	}
}
