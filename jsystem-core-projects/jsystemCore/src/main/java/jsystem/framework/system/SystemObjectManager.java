/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import jsystem.framework.sut.Sut;

import org.w3c.dom.Document;

/** 
 * This interface defines system object manager method getSystemObject.
 * An instance of SystemObjectManager will be used in SystemTestCase to retrive
 * the SystemObject.<p>
 * The system object service is used to init system object from definistion that placed in
 * the SUT file.<p>
 * Following is an example for a SUT file:
 * 
 * <p> <blockquote><pre>
 * &lt;sut&gt;<br>
 *     &lt;device1&gt;<br>
 *         &lt;class&gt;systemobject.tests.Device1&lt;/class&gt;
 *         &lt;telnet ref="/sut/con"/&gt;
 *         &lt;ip&gt;10.10.10.10&lt;/ip&gt;
 *         &lt;port index="0"&gt;
 *             &lt;class&gt;systemobject.tests.Port&lt;/class&gt;
 *             &lt;portId&gt;0&lt;/portId&gt;
 *         &lt;/port&gt;
 *         &lt;port index="1"&gt;
 *             &lt;class&gt;systemobject.tests.Port&lt;/class&gt;
 *         &lt;portId&gt;1&lt;/portId&gt;
 *         &lt;/port&gt;
 *     &lt;/device1&gt;
 *     &lt;con&gt;
 *         &lt;class&gt;systemobject.tests.Telnet&lt;/class&gt;
 *         &lt;user&gt;guy&lt;/user&gt;
 *         &lt;password&gt;guy&lt;/password&gt;
 *         &lt;prompt regExp="false"&gt;&gt;&lt;/prompt&gt;
 *         &lt;index&gt;1&lt;/index&gt;
 *         &lt;dummy&gt;true&lt;/dummy&gt;
 *         &lt;timeout&gt;40000&lt;/timeout&gt;
 *         &lt;errors&gt;error;fail&lt;/errors&gt;
 *     &lt;/con&gt;
 * &lt;/sut&gt;
 * </pre></blockquote>
 * 
 * Following is an example for 'system' service usage:
 * <p> <blockquote><pre>
 * <code>Device1 d1 = (Device1)system.getSystemObject("device1")</code>
 * </pre></blockquote>
 * <p>
 * Object d1 will be instanciate as follow:<p>
 * 1. The object of class Device1 will be created.<p>
 * 2. All the tags avaleable in the XML will be matched against setter will the
 * same name. For example setIp will be called with the value "10.10.10.10".<p>
 * 3. The public member telnet will be init as define in the ref tag.<p>
 * 4. The array of port's will be init in the same way recusivly.<p>
 * 
 * @author guy.arieli
 */
public interface SystemObjectManager {
    /** 
     * Get the system object using the name and a '/sut' as the default path.
     * 
     * @exception Exception The system object can't be created
     * @param name the name of the object
     * @return SystemObject The defined system object. If this object will be 
     *      found null an exception will be thrown
     */
    SystemObject getSystemObject(String name) throws Exception;
    
    
    /**
     * Get the system object using the name and a '/sut' as the default path.
     * 
     * @param name the name of the object
     * @param sutName the name of the sut file to use
     * @param forceNew if set to true will not look for it in the repository 
     *      (and will not add it to the repository).
     * @return SystemObject The defined system object. If this object will be 
     *      found null an exception will be thrown
     * @throws Exception
     */
    SystemObject getSystemObject(String name, String sutName, boolean forceNew) throws Exception;
    /**
     * Get the system object using the name and a '/sut' as the default path.
     * 
     * @param name the name of the object
     * @param doc XML object that represent the SUT
     * @param forceNew if set to true will not look for it in the repository 
     *      (and will not add it to the repository).
     * @return SystemObject The defined system object. If this object will be 
     *      found null an exception will be thrown
     * @throws Exception
     */
    SystemObject getSystemObject(String name, Document doc, boolean forceNew) throws Exception;

    /** 
     * Get the system object using the name and its path.
     * 
     * @param path the XPath to the object
     * @param name the object name
     * @param parent the parent of the object if not root, in case of  
     *      root object null.
     * @exception Exception When the system object can't be created
     * @return SystemObject The defined system object. or null if the object  
     *      wasn't init.
     */
    SystemObject getSystemObject(String path, String name, SystemObject parent) throws Exception;
    /** 
     * Get the system object
     * 
     * @param path the XPath to the object
     * @param name the object name
     * @param index the index of the system object in an array or -1 if
     *      not an array member.
     * @param parent the parent of the object if not root, in case of  
     *      root object null.
     * @exception Exception When the system object can't be created
     * @return SystemObject The defined system object. or null if the object  
     *      wasn't init.
     */
    SystemObject getSystemObject(String path, String name, int index, SystemObject parent) throws Exception;
    /**
     * Get a system object
     * @param path the XPath to the object
     * @param name the object name
     * @param index the index of the system object in an array or -1 if
     * 		not an array member.
     * @param parent the parent of the object if not root, in case of  
     *      root object null.
     * @param isNew
     * @param referenceXPath
     * @exception Exception When the system object can't be created
     * @return SystemObject The defined system object. or null if the object  
     *      wasn't init.
     */
    SystemObject getSystemObject(String path, String name, int index, SystemObject parent, boolean isNew,String referenceXPath) throws Exception;

    SystemObject getSystemObject(String path, String name, int index, SystemObject parent, boolean isNew,String referenceXPath, Sut sut) throws Exception;
    /** 
     * 
     * @param o The system object to remove
     */
    public void removeSystemObject(SystemObject o);
    /** 
     * Close all the system object currently active.
     * This method will be called automaticly on system
     * exit.
     */
    public void closeAllObjects();

	/** 
	 * Lock the system object
	 * @exception Exception 
	 * @param sobject The system object to lock
	 */
	public void lockObject(SystemObject sobject) throws Exception;

	/**
	 * Release a lock on a system object
	 * @param sobject the system object to release the lock on
	 */
	public void releaseObject(SystemObject sobject);

	/**
	 * Check that a system object is operatable
	 * @param sobject the system object to check
	 * @throws Exception The system object is not operatable
	 */
	public void checkObject(String sobject) throws Exception;

	/**
	 * Pause the operation of all the system objects
	 *
	 */
	public void pausedAllObjects();

	/**
	 * resume the operation of all the system objects
	 *
	 */
	public void resumeAllObjects();
	
    public SystemObject getSystemObjectByXPath(String xpath) throws Exception;

    /**
     * Add a system object to the list of managed system objects.<br>
     * It is assumed that the system object was already initialized elsewhere.<br>
     * This causes the system object to close when closeAllObjects is called.<br>
     *
     * @param systemObject - the system object to add.
     */
    public void addSystemObject(SystemObject systemObject);
}
