/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import java.util.Properties;
import java.util.Vector;

/**
 * System Object is a convention to represent the setup/system you are working
 * on with a single object. The steps in the tests will be operations on the
 * setup/system object.
 * 
 * @author Guy Arieli
 */
public interface SystemObject {
	
	public static final int CHECK_NOT_IMPL = 0;

	public static final int CHECK_CONNECTED = 1;

	public static final int CHECK_DISCONNECTED = 2;

	/**
	 * Set the lifetime of the object to the test life time. Will be init every
	 * test.
	 */
	public static final int TEST_LIFETIME = 0;

	/**
	 * Set the object lifetime to permanent lifetiem. This object will be
	 * available as long as the framework is running.
	 */
	public static final int PERMANENT_LIFETIME = 1;

	/**
	 * Set the System Object tag name.
	 * 
	 * @param name
	 *            The object name.
	 */
	public void setTagName(String name);

	/**
	 * Get the System object tag name
	 * 
	 * @return String
	 */
	public String getTagName();

	/**
	 * Get the system object name.
	 * 
	 */
	public void setName(String name);

	/**
	 * Get the system object name.
	 * 
	 * @return The system object name.
	 */
	public String getName();

	/**
	 * Init the system object. This method will be called when the object is
	 * constracted.
	 * 
	 * @exception Exception
	 */
	public void init() throws Exception;

	/**
	 * Close the object. Will be called on object closed.
	 */
	public void close();

	/**
	 * Is the object close.
	 * 
	 * @return Return true if closed else return false.
	 */
	public boolean isClosed();

	/**
	 * Set the object isClosed status.
	 * 
	 * @param isClosed
	 *            the isClosed status.
	 */
	public void setClose(boolean isClosed);

	/**
	 * Get the object lifetime. The default lifetime is PERMANENT_LIFETIME.
	 * 
	 * @return the object lifetime.
	 */
	public int getLifeTime();

	/**
	 * Set the object lifetime. Can be one of two: TEST_LIFETIME or
	 * PERMANENT_LIFETIME.
	 * 
	 * @param lifeTime
	 *            The object lifetime.
	 */
	public void setLifeTime(int lifeTime);

	/**
	 * Get the object XPath in the sut XML file.
	 * 
	 * @return Object XPath.
	 */
	public String getXPath();

	/**
	 * Set the object XPath.
	 * 
	 * @param path
	 *            Object XPath.
	 */
	public void setXPath(String path);

	/**
	 * Get the xpath of the object that.
	 * this object refered to
	 */
	public String getReferenceXPath();

	/**
	 * Set the xpath of the object that
	 * this object referes to.
	 */
	public void setReferenceXPath(String path);

	/**
	 * get the parent object, null in case of root.
	 * 
	 * @return parent
	 */
	public SystemObject getParent();

	public void setParent(SystemObject parent);

	public Properties getProperties();

	public void setOpenCloseStatusAll(boolean status);

	public String getProperty(String key);

	public Vector<SystemObject> getChildren();

	public void lock() throws Exception;

	public void release();

	public Semaphore getLockObject();

	public void check() throws Exception;

	public int getCheckStatus();

	public void resume();

	public void pause();

	/**
	 * The index of the system object if part of an array of objects.
	 * @return the system object array index or -1
	 * if not part of array
	 */
	public int getSOArrayIndex();
	/**
	 * 
	 * @param index the index of the system object in the array (or -1)
	 */
	public void setSOArrayIndex(int index);

	public long getExitTimeout();
	
	public void setExitTimeout(long exitTimeout);
	
}
