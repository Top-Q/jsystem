/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

public interface JTestContainerVisitor {
	
	public void visitScenarioElement(JTest t) throws Exception;
	
}
