/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.IgnoreMethod;
import jsystem.framework.RunProperties;
import jsystem.framework.analyzer.AnalyzerImpl;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.report.Reporter;
import jsystem.framework.report.Reporter.EnumReportLevel;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.BeanUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * import electric.xml.Element; import electric.xml.Text; import
 * electric.xml.Attribute;
 */

/**
 * System Object is a convention to represent the setup/system you are working on with a single object. The steps in the tests will be
 * operations on the setup/system object.
 * 
 * @author Guy Arieli
 */
public abstract class SystemObjectImpl extends AnalyzerImpl implements SystemObject {
	private static Logger log = Logger.getLogger(SystemObjectImpl.class.getName());

	private String tagName = null;

	private String name = null;

	private String path = null;

	private String referencePath = null;

	protected boolean isClosed = false;

	private int lifeTime = SystemObject.PERMANENT_LIFETIME;

	protected SystemObjectManager system = SystemManagerImpl.getInstance();

	protected Sut sut = SutFactory.getInstance().getSutInstance();

	protected Reporter report = ListenerstManager.getInstance();

	/**
	 * Properties service that can be used to save properties and share it with other tests. Properties that are saved in one test can be
	 * read in other tests as long as they are in the same run.
	 */
	protected RunProperties runProperties = RunProperties.getInstance();

	protected SystemObjectManager systemManager = SystemManagerImpl.getInstance();

	protected Vector<SystemObject> systemObjects = new Vector<SystemObject>();

	protected Properties properties = new Properties();

	SystemObject parent = null;

	private Semaphore lockObject = new Semaphore(1);

	private int checkStatus;

	private int soArrayIndex = -1;

	/**
	 * Set the System Object name.
	 * 
	 * @param name
	 *            The object name.
	 */
	@Override
	@IgnoreMethod
	public void setTagName(String name) {
		this.tagName = name;
	}

	/**
	 * Get the system object name.
	 * 
	 * @return The system object name.
	 */
	@Override
	@IgnoreMethod
	public String getName() {
		return name;
	}

	/**
	 * Get the object lifetime. The default lifetime is PERMANENT_LIFETIME.
	 * 
	 * @return the object lifetime.
	 */
	@Override
	@IgnoreMethod
	public int getLifeTime() {
		return lifeTime;
	}

	/**
	 * Set the object lifetime. Can be one of two: TEST_LIFETIME or PERMANENT_LIFETIME.
	 * 
	 * @param lifeTime
	 *            The object lifetime.
	 */
	@Override
	@IgnoreMethod
	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}

	/**
	 * Get the object XPath in the sut XML file.
	 * 
	 * @return Object XPath.
	 */
	@Override
	@IgnoreMethod
	public String getXPath() {
		return path;
	}

	/**
	 * Set the object XPath.
	 * 
	 * @param path
	 *            Object XPath.
	 */
	@Override
	@IgnoreMethod
	public void setXPath(String path) {
		this.path = path;
	}

	/**
	 * see interface doc
	 */
	@Override
	@IgnoreMethod
	public String getReferenceXPath() {
		return referencePath;
	}

	/**
	 * see interface doc
	 */
	@Override
	@IgnoreMethod
	public void setReferenceXPath(String path) {
		this.referencePath = path;
	}

	/**
	 * Is the object close.
	 * 
	 * @return Return true if closed else return false.
	 */
	@Override
	@IgnoreMethod
	public boolean isClosed() {
		return isClosed;
	}

	/**
	 * Set the object isClosed status.
	 * 
	 * @param isClosed
	 *            the isClosed status.
	 */
	@Override
	@IgnoreMethod
	public void setClose(boolean isClosed) {
		this.isClosed = isClosed;
	}

	/**
	 * Init all the class field from the xml
	 * 
	 * @throws Exception
	 */
	@IgnoreMethod
	protected void initFields() throws Exception {
		Class<?> thisClass = this.getClass();
		Field[] fields = thisClass.getFields();
		for (Field currentField : fields) {
			String fieldName = currentField.getName();
			if (SystemObject.class.isAssignableFrom(currentField.getType())) {
				SystemObject sObject = (SystemObject) currentField.get(this);
				if (sObject == null || sObject.isClosed()) {
					sObject = getChildSystemObject(fieldName, -1);
					try {
						currentField.set(this, sObject);
					} catch (Exception t) {
						log.warning("Fail to init field: " + fieldName);
					}
				}
				systemObjects.remove(sObject);
				systemObjects.addElement(sObject);
			} else {
				if (currentField.getType().isArray() && SystemObject.class.isAssignableFrom(currentField.getType().getComponentType())) {
					int lastIndex = getObjectLastIndex(getXPath(), fieldName);
					if (lastIndex < 0) {
						continue;
					}
					SystemObject[] array = (SystemObject[]) currentField.get(this);
					if (array == null) {
						Object arrayObject = Array.newInstance(currentField.getType().getComponentType(), lastIndex + 1);
						for (int arrayIndex = 0; arrayIndex <= lastIndex; arrayIndex++) {
							SystemObject childSystemObject = getChildSystemObject(fieldName, arrayIndex);
							Array.set(arrayObject, arrayIndex, childSystemObject);
							systemObjects.remove(childSystemObject);
							systemObjects.addElement(childSystemObject);
							if (childSystemObject != null) {
								childSystemObject.setSOArrayIndex(arrayIndex);
							}
						}
						currentField.set(this, arrayObject);
					} else {
						for (int arrayIndex = 0; arrayIndex < array.length; arrayIndex++) {
							if (array[arrayIndex] == null || array[arrayIndex].isClosed()) {
								array[arrayIndex] = getChildSystemObject(fieldName, arrayIndex);
								systemObjects.remove(array[arrayIndex]);
								systemObjects.addElement(array[arrayIndex]);
							}
						}
					}
				}
			}
		}
	}

	private SystemObject getChildSystemObject(String fieldName, int index) throws Exception {
		SystemObject res = ((SystemManagerImpl) system).getSystemObject(getXPath(), fieldName, index, this, true, getReferenceXPath(), sut);
		return res;
	}

	@IgnoreMethod
	protected void setSetters() {
		// ***
		Class<?> thisClass = this.getClass();
		HashMap<String, BeanElement> beans = BeanUtils.getBeanMap(thisClass, false, true, BeanUtils.getBasicTypes());

		try {
			Enumeration<Object> propertiesKeys = properties.keys();
			while (propertiesKeys.hasMoreElements()) {
				String name = (String) propertiesKeys.nextElement();
				BeanElement beanElement = beans.get(name);
				if (beanElement == null) {
					continue;
				}
				try {
					BeanUtils.invoke(this, beanElement.getSetMethod(), properties.getProperty(name), beanElement.getType());
				} catch (Throwable tt) {
					log.log(Level.INFO, "Fail to set setters: " + beanElement.getSetMethod().getName(), tt);
				}
			}
		} catch (Throwable t) {
			log.log(Level.INFO, "Fail to set setters", t);
		}
		// ---
	}

	/**
	 * Please note: If a system object (SystemObject A) is created by reference to another system object (SystemObject B) when this method
	 * is called (at the initiation of the SystemObject), the getXPath method return the path to B and the get getReferenceXPath returns the
	 * path to A At the end of the initiation process the paths are replaced by the SystemManagerImpl
	 * 
	 */
	@IgnoreMethod
	protected void initProperties() {
		initPropertiesFromPath(getXPath());
		String referenceXPath = getReferenceXPath();
		if (referenceXPath != null && !"".equals(referenceXPath.trim())) {
			initPropertiesFromPath(referenceXPath);
		}
	}

	private void initPropertiesFromPath(String path) {
		try {
			List<Node> nodeList = sut.getAllValues(path + "/*");
			for (Node currentNode : nodeList) {
				Element element = (Element) currentNode;
				String name = element.getNodeName();
				String text = null;
				NodeList childNodeList = element.getChildNodes();
				for (int childIndex = 0; childIndex < childNodeList.getLength(); childIndex++) {
					Node currentChild = childNodeList.item(childIndex);
					if (currentChild.getNodeType() == Node.TEXT_NODE) {
						text = currentChild.getNodeValue();
						break;
					}
				}
				if (text != null) {
					properties.setProperty(name, text);
				}
			}
		} catch (Exception e) {
			log.log(Level.INFO, "Fail to init properties", e);
		}

	}

	@Override
	@IgnoreMethod
	public void init() throws Exception {
		initProperties();
		initFields();
		setSetters();
	}

	/**
	 * Close all children of the system object.
	 */
	@IgnoreMethod
	protected void closeFields() {
		// Why creating a new array from vector ?
		SystemObjectImpl[] sysObjs = systemObjects.toArray(new SystemObjectImpl[systemObjects.size()]);
		for (int i = 0; i < sysObjs.length; i++) {
			if (sysObjs[i] != null) {
				sysObjs[i].close();
			}
		}
		systemObjects = new Vector<SystemObject>();
	}

	/**
	 * Close the system object.
	 */
	@Override
	@IgnoreMethod
	public void close() {
		// Close all children
		closeFields();
		// Indicate system object is closed
		setClose(true);
		// Remove system object from parent system object
		SystemObject parent = getParent();
		if (parent != null) {
			parent.getChildren().remove(this);
		}
		if (SystemManagerImpl.getInstance() != null) {
			// Remove system object from system objects manager
			SystemManagerImpl.getInstance().removeSystemObject(this);
		}
	}

	protected String fixPath(String relativePath) {
		return pathConcat(path, relativePath);
	}

	@IgnoreMethod
	private String pathConcat(String path1, String path2) {
		return path1 + (path1.endsWith("/") ? "" : "/") + path2;
	}

	@IgnoreMethod
	private int getObjectLastIndex(String path, String name) throws Exception {
		String xPath = path + "/" + name;
		List<Node> nodeList = sut.getAllValues(xPath);
		if (nodeList == null || nodeList.size() == 0) {
			return -1;
		}
		int lastIndex = -1;
		for (Node currentNode : nodeList) {
			String indexAttrib = ((Element) currentNode).getAttribute("index");
			if (indexAttrib == null) {
				throw new Exception("No index attribute was found");
			}
			try {
				int currentIndex = Integer.parseInt(indexAttrib);
				if (currentIndex > lastIndex) {
					lastIndex = currentIndex;
				}
			} catch (NumberFormatException e) {
				throw new Exception("Index attribute is not integer: " + indexAttrib, e);
			}
		}
		return lastIndex;
	}

	/**
	 * @return Returns the tagName.
	 */
	@Override
	@IgnoreMethod
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	@Override
	@IgnoreMethod
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * return the parent system object null in case of root.
	 */
	@Override
	@IgnoreMethod
	public SystemObject getParent() {
		return parent;
	}

	@Override
	@IgnoreMethod
	public void setParent(SystemObject parent) {
		this.parent = parent;
	}

	@Override
	@IgnoreMethod
	public Properties getProperties() {
		return properties;
	}

	@IgnoreMethod
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	@IgnoreMethod
	public void setOpenCloseStatusAll(boolean status) {
		setClose(status);
		if (systemObjects != null) {
			for (int systemObjectIndex = 0; systemObjectIndex < systemObjects.size(); systemObjectIndex++) {
				SystemObject currentSystemObject = systemObjects.elementAt(systemObjectIndex);
				if (currentSystemObject != null) {
					currentSystemObject.setOpenCloseStatusAll(status);
				}
			}
		}
	}

	@Override
	@IgnoreMethod
	public Vector<SystemObject> getChildren() {
		return systemObjects;
	}

	@Override
	@IgnoreMethod
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	@Override
	@IgnoreMethod
	public void lock() throws Exception {
		system.lockObject(this);
	}

	@Override
	@IgnoreMethod
	public void release() {
		system.releaseObject(this);
	}

	@Override
	public Semaphore getLockObject() {
		return lockObject;
	}

	@Override
	@IgnoreMethod
	public void check() throws Exception {
		report.report(name + ": Perform Check");
		setCheckStatus(SystemObject.CHECK_NOT_IMPL);
	}

	@Override
	@IgnoreMethod
	public int getCheckStatus() {
		return checkStatus;
	}

	@IgnoreMethod
	public void setCheckStatus(int checkStatus) {
		this.checkStatus = checkStatus;
	}

	@Override
	@IgnoreMethod
	public void pause() {
		// defualt empty implementation
	}

	@Override
	@IgnoreMethod
	public void resume() {
		// defualt empty implementation
	}

	@Override
	@IgnoreMethod
	public int getSOArrayIndex() {
		return soArrayIndex;
	}

	@Override
	@IgnoreMethod
	public void setSOArrayIndex(int index) {
		this.soArrayIndex = index;
	}

	private long exitTimeout = 1000;

	/**
	 * @return time to wait for the system object to close
	 */
	@Override
	public long getExitTimeout() {
		return exitTimeout;
	}

	/**
	 * @param exitTimeout
	 *            time to wait for the system object to close
	 */
	@Override
	@IgnoreMethod
	public void setExitTimeout(long exitTimeout) {
		this.exitTimeout = exitTimeout;
	}

	/**
	 * This flag allows the test to control the amount of messages the system object sends to the reporter. <br>
	 * {@link SystemObjectImpl#report(String)} <br> {@link SystemObjectImpl#report(String, int)} <br>
	 * {@link SystemObjectImpl#report(String, String, int)} <br> {@link SystemObjectImpl#report(String, String, boolean)} <br>
	 * {@link SystemObjectImpl#startLevel(String, EnumReportLevel)} <br> {@link SystemObjectImpl#stopLevel()}
	 */
	protected boolean printStatuses = true;

	public boolean isPrintStatuses() {
		return printStatuses;
	}

	/**
	 * @param printStatuses
	 *            true - send report messages, false don't send report messages
	 */
	@IgnoreMethod
	public void setPrintStatuses(boolean printStatuses) {
		this.printStatuses = printStatuses;
	}

	/**
	 * This method sends report message only if {@link SystemObjectImpl#printStatuses} == true
	 * 
	 * @param title
	 */
	protected void report(String title) {
		report(title, Reporter.PASS);
	}

	/**
	 * This method sends report message only if {@link SystemObjectImpl#printStatuses} == true
	 * 
	 * @param title
	 * @param status
	 */
	protected void report(String title, int status) {
		if (isPrintStatuses()) {
			report(title, null, status);
		}
	}

	/**
	 * This method sends report message only if {@link SystemObjectImpl#printStatuses} == true
	 * 
	 * @param title
	 * @param message
	 * @param status
	 */
	protected void report(String title, String message, int status) {
		if (isPrintStatuses()) {
			report.report(title, message, status);
		}
	}

	/**
	 * This method sends report message only if {@link SystemObjectImpl#printStatuses} == true
	 */
	protected void report(String title, String message, boolean status) {
		if (isPrintStatuses()) {
			report.report(title, message, status);
		}

	}

	/**
	 * This method starts report level only if {@link SystemObjectImpl#printStatuses} == true The level will start in CurrentPlace.
	 * 
	 * @param message
	 * @throws IOException
	 */
	protected void startLevel(String message) throws IOException {
		startLevel(message, EnumReportLevel.CurrentPlace);
	}

	/**
	 * This method starts report level only if {@link SystemObjectImpl#printStatuses} == true
	 * 
	 * @param message
	 * @param level
	 *            Where to start the level
	 * @throws IOException
	 */
	protected void startLevel(String message, EnumReportLevel level) throws IOException {
		if (isPrintStatuses()) {
			report.startLevel(message, level);
		}
	}

	/**
	 * This method stops report level only if {@link SystemObjectImpl#printStatuses} == true
	 * 
	 * @throws IOException
	 */
	protected void stopLevel() throws IOException {
		if (isPrintStatuses()) {
			report.stopLevel();
		}
	}

}