/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package junit.framework;

public interface NamedTest extends Test {
	public abstract String getClassName();
	public abstract String getMethodName();
	public abstract String getFullUUID();
}
