/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.sut.Sut;
import jsystem.framework.sut.SutFactory;
import jsystem.runner.loader.LoadersManager;
import jsystem.utils.StringUtils;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The SystemManagerImpl is responsible of creating, retrieving and closing
 * of all SystemObjects in the environment created by the "getSystemObject" method
 * 
 * All SystemObjects are gathered to a HashMap allowing retrieval of all pre-created SystemObjects
 */
public class SystemManagerImpl implements SystemObjectManager, TestListener {
    private static Logger log = Logger.getLogger(SystemManagerImpl.class.getName());
    /*
     * Singleton object
     */
	private static SystemManagerImpl manager = null;

    /**
     * Sut object for setup information
     */
    private Sut sut = SutFactory.getInstance().getSutInstance();
    
    /**
     * Repository for all the running system objects
     */
    private Map<String, SystemObject> systemObjects;
   
    private SystemManagerImpl(){
    	 resetSeystemObjectsMap();
        ListenerstManager.getInstance().addListener(this);
    }
    public static SystemManagerImpl getInstance(){
        if (manager == null){
		if (JSystemProperties.getInstance().isReporterVm() && JSystemProperties.getInstance().isJsystemRunner()) {
        		return null;
        	}
            manager = new SystemManagerImpl();
        }
        return manager;
    }
    public SystemObject getSystemObject(String name, String sutName, boolean forceNew) throws Exception{
    	Sut sut = SutFactory.getInstance().getNewSutInstance();
		File file = new File(SutFactory.getInstance().getSutDirectory(), sutName.toLowerCase().endsWith(".xml")? sutName: sutName + ".xml");
		if(!file.exists()){
			throw new FileNotFoundException("Could not find SUT file name: " + file.getAbsolutePath());
		}
		sut.setSutXml(file, false);
    	SystemObject object = getSystemObject("/sut", name, -1, null, forceNew, null, sut);
    	if(object == null){
    		throw new Exception("Fail to init system object: " + name);
    	}
        return object;
    }
    public SystemObject getSystemObject(String name, Document doc, boolean forceNew) throws Exception{
    	Sut sut = SutFactory.getInstance().getNewSutInstance();
		sut.setDocument(doc);
    	SystemObject object = getSystemObject("/sut", name, -1, null, forceNew, null, sut);
    	if(object == null){
    		throw new Exception("Fail to init system object: " + name);
    	}
        return object;
    }
    public SystemObject getSystemObject(String name) throws Exception{
    	SystemObject object = getSystemObject("/sut", name, -1, null, false, null, sut);
    	if(object == null){
    		throw new Exception("Fail to init system object: " + name);
    	}
        return object;
    }
    
    public SystemObject getSystemObject(String path, String name, SystemObject parent) throws Exception {
    	return getSystemObject(path,name,-1, parent);
    }
    
    public synchronized SystemObject getSystemObject(String path, String name, int index, SystemObject parent) throws Exception {
    	return getSystemObject(path, name, index, parent, false,null);
    }
    public synchronized SystemObject getSystemObject(String path, String name, int index, SystemObject parent, boolean isNew,String referenceXPath, Sut usedSut) throws Exception {
        String xPath;
        String tag;
        if(index < 0){
            tag = name;
        } else {
        	
        	tag = name + "[@index=\"" + index + "\"]";
        	name = name + "[" + index + "]";
        }
        xPath = path + "/" + tag;
        
        SystemObject systemObject = null;
        /*
         * if isNew is set to true will not check if the object is in the repository
         * and will init it in any case.
         */
        if(!isNew){
            systemObject = (SystemObject)systemObjects.get(xPath);
            
            if (systemObject != null && !systemObject.isClosed()){
                if (systemObject instanceof SystemObjectImpl){
                    ((SystemObjectImpl)systemObject).initFields();
                }
                return systemObject;
            }
        }
        String ref = null;
        /*
         * check if the ref tag exits. If exist will try to init the object
         * from the referance.
         */
        try {
        	ref = usedSut.getValue(xPath +"/@ref");
        } catch(Exception e){
        	log.log(Level.FINE,"the ref: " + xPath + " was not found",e);
        }
        if(ref != null){ // will init the object from the referance
        	systemObject = processReferance(ref, xPath, parent, tag, name);
            return systemObject;
            
        } else { // will init the object from the sut by class name
            String className;
            try {
            	className = usedSut.getValue(xPath + "/class/text()");
            } catch(Exception e){
            	log.log(Level.FINE,"the object: " + xPath + " wasn't init",e);
            	return null;
            }
            if (className == null){
                throw new Exception("Unable to find class atribute to systemObject: " + name);
            }
            
            className = className.trim();
            
            if(className.indexOf('#') >= 0){ // look like the xerses implemantation. We should fix the class name
            	// the inputs look like: [#text: com....]
            	className = className.substring(className.lastIndexOf(' ') + 1, className.length() - 1);
            }

            Class<?> c = LoadersManager.getInstance().getLoader().loadClass(className); 

            systemObject =  (SystemObject)c.newInstance();
        }
        if(systemObject instanceof SystemObjectImpl){
        	((SystemObjectImpl)systemObject).sut = usedSut;
        }
        systemObject.setTagName(tag);
        systemObject.setName(name);
        recursiveUpdateOfXPath(systemObject,xPath,xPath);
        if (referenceXPath != null){
        	referenceXPath = ref !=null? referenceXPath+ref :referenceXPath;
        }
       	systemObject.setReferenceXPath(referenceXPath);
        systemObject.setParent(parent);
        systemObject.init();
        systemObject.setClose(false);
        /*
         * If set to new will not add it to the systemObjects repository.
         * The calling process should manage it (in our case the recursive call).
         */
        if(!isNew){ 
            systemObjects.remove(xPath);
            systemObjects.put(xPath, systemObject);
        }
        return systemObject;
    }
    
    public SystemObject getSystemObjectByXPath(String xpath) throws Exception{
    	int startIndex = 1;
    	String[] paths = xpath.split("/");
    	if(paths[startIndex].equals("sut")){
    		startIndex++;
    	}
    	String soName = paths[startIndex];
    	startIndex++;
    	SystemObject current = getSystemObject(soName);
    	main:
    	for(int i = startIndex; i < paths.length; i++){
    		if(paths[i].trim().isEmpty()){
    			continue;
    		}
    		ArrayList<Field> fields = getSystemObjectField(current.getClass());
    		String path = paths[i];
    		if(path.contains("[")){
    			path = path.substring(0, path.indexOf("["));
    		}
    		for(Field f: fields){
    			if(f.getName().equals(path)){
    				if(f.getType().isArray()){
    					SystemObject[] array = (SystemObject[])f.get(current);
    					Matcher matcher = Pattern.compile("\\[(.*)\\]").matcher(paths[i]);
    					if(matcher.find()){
    						String condition = matcher.group(1);
        					JXPathContext context = JXPathContext.newContext(array);
        					current = (SystemObject)context.getValue(".[" + condition + "]");
    					} else {
    						current = array[0];
    					}
    					break main;
    				} else {
    					current = (SystemObject)f.get(current);
    					break main;
    				}
    			}
    		}
    		throw new Exception("Could not found path " + paths[i]);
    	}
    	return current;
    }
	/**
	 * Get all the fields that are system object themself of a given class
	 * @param object the class to process.
	 * @return a list of all the system objects fields.
	 */
	public static ArrayList<Field> getSystemObjectField(Class<? extends SystemObject> baseClass){
		ArrayList<Field> systemObjectFields = new ArrayList<Field>();
		try {
			Field[] allFields = baseClass.getFields();
			
			for(Field f: allFields){
				/*
				 * It should be assignable from SystemObject or
				 * array that it's component can be assign.
				 */
				if(SystemObject.class.isAssignableFrom(f.getType())){
					systemObjectFields.add(f);
				} else if(f.getType().isArray()
						&& SystemObject.class.isAssignableFrom(f.getType().getComponentType())){
					systemObjectFields.add(f);
				}
			}
		} catch (Throwable t){
		}
		
		return systemObjectFields;
	}

    public synchronized SystemObject getSystemObject(String path, String name, int index, SystemObject parent, boolean isNew,String referenceXPath) throws Exception {
    	return getSystemObject(path, name, index, parent, isNew, referenceXPath, sut);
    }
    
    private void recursiveUpdateOfXPath(SystemObject sysObj,String oldPath, String newPath) {
    	String currentPath = sysObj.getXPath();
    	if (currentPath == null || "".equals(currentPath)){
    		currentPath = newPath;
    	}
    	String afterUpdate = StringUtils.replace(currentPath, oldPath, newPath);
    	sysObj.setXPath(afterUpdate);
    	Iterator<?> i = sysObj.getChildren().iterator();
    	while (i.hasNext()){
    		SystemObject son = (SystemObject)i.next();
    		if (son == null){
    			continue;
    		}
    		recursiveUpdateOfXPath(son, currentPath, afterUpdate);
    	}
    	
    }
    private SystemObject processReferance(String ref, String xPath, SystemObject parent, String tag, String name) throws Exception{
        if(ref.indexOf('#') >= 0){ // look like the xerses implemantation. We should fix the class name
        	// the inputs look like: [#text: com....]
        	ref = ref.substring(ref.lastIndexOf(' ') + 1, ref.length() - 1);
        }
        if(!ref.startsWith("/")){ // if the referance is not start with / add it
        	ref = "/" + ref;
        }
        if(!ref.startsWith("/sut/")){ // if the referance is not start with /sut add it
        	ref = "/sut" + ref;
        }
        /*
         * setNew if set to false will use an existing system object.
         */
        boolean setNew = true;
        try {
        	if("false".equals(sut.getValue(xPath +"/@new"))){
        		setNew = false;
        	}
        } catch(Exception e){
        	// the attribute is not found
        }
        /*
         * System object name
         */
        String soName = null;
        /*
         * System object path
         */
        String soPath = null;
        /*
         * System object index
         */
        int soIndex = -1;
        /*
         * Extract the soName and soIndex from the ref
         */
        int lastSleshIndex = ref.lastIndexOf('/');
        /*
         * The soPath is th estring before the last '/'
         * For example: /sut/cli
         * the path will be /sut and the name will be cli
         */
        soPath = ref.substring(0, lastSleshIndex);
        String namePlusIndex = ref.substring(lastSleshIndex + 1);
        int indexStart = namePlusIndex.indexOf("[@index=\'");
        /*
         * Process the option that index atribute is found in the name
         * For example /sut/cli[@index="3"]
         */
        if( indexStart >= 0){
        	soName = namePlusIndex.substring(0, indexStart);
        	StringBuffer number = new StringBuffer();
        	for(int i = indexStart + "[@index=\'".length(); i < namePlusIndex.length(); i++){
        		char c = namePlusIndex.charAt(i);
        		if(Character.isDigit(c)){
        			number.append(c);
        		} else {
        			break;
        		}
        	}
        	if(number.length() > 0){
        		soIndex = Integer.parseInt(number.toString());
        	} else {
        		throw new Exception("The referance could not be parsed: " + ref);
        	}
        } else {
        	soName = namePlusIndex;
        }
        /*
         * Recursivly get the system object
         */
        SystemObject systemObject = getSystemObject(soPath, soName, soIndex, parent, setNew,xPath);
        if(systemObject == null){
        	throw new Exception("Init system object fail ref: " + ref +" was not found");
        }
        /*
         * If the object is a new object all the object information like
         * xpath, parent and more is init.
         */
        if(setNew){
            systemObject.setTagName(tag);
            systemObject.setName(name);
            recursiveUpdateOfXPath(systemObject,systemObject.getXPath(),xPath);
            systemObject.setReferenceXPath(ref);
            systemObject.setParent(parent);
            systemObject.setClose(false);
            systemObjects.remove(xPath);
            systemObjects.put(xPath, systemObject);
            
        }
        return systemObject;
    }

	public synchronized void addError(Test test, Throwable t) {
        Iterator<?> iter = systemObjects.values().iterator();
		while (iter.hasNext()) {
			SystemObject d = (SystemObject) iter.next();
			if (d == null) {
				continue;
			}
			if (d instanceof TestListener) {
				TestListener tl = (TestListener) d;
				tl.addError(test, t);
			}
		}
	}

	private long totalExitTimeout = 15000;
	
	/**
	 * @return total time to wait for all system objects to close
	 */
	public long getTotalExitTimeout() {
		return totalExitTimeout;
	}

	/**
	 * @param totalExitTimeout
	 *            total time to wait for all system objects to close
	 */
	public void setTotalExitTimeout(long totalExitTimeout) {
		this.totalExitTimeout = totalExitTimeout;
	}
	public  void closeAllObjects() {

		long exitTimeout = 
				JSystemProperties.getInstance().getLongPreference(FrameworkOptions.EXIT_TIMEOUT, getTotalExitTimeout());

		SystemObject[] sos = new SystemObject[0];
		
		synchronized (this) {
			sos = systemObjects.values().toArray(new SystemObject[0]);
			resetSeystemObjectsMap();
		}
		//we want to close only root system objects.
		//system objects which has a parent will be closed by their parent.
		ArrayList<SystemObject> objectsWithoutParents = new ArrayList<SystemObject>();
		for (SystemObject sysObj:sos){
			if (sysObj.getParent() == null){
				objectsWithoutParents.add(sysObj);
			}
		}
		sos = objectsWithoutParents.toArray(new SystemObject[0]);
		//if there are no system objects to close no need
		//to create a thread pool.
		if (sos.length == 0){
			return;
		}
		
		ArrayList<SystemObjectCloseThread>threads = new ArrayList<SystemObjectCloseThread>();
		for(SystemObject so: sos){
			SystemObjectCloseThread thread = new SystemObjectCloseThread(so);
			threads.add(thread);
			thread.start();
		}
		if (exitTimeout == 0) { // not waiting
			return;
		}
		// Wait for all system objects to close at least exitTimeout milliseconds
		while (System.currentTimeMillis() - System.currentTimeMillis() < exitTimeout){
			if (threads.size() == 0) {
				break;
			}
			for(int i = 0; i < threads.size(); i++){
				try {
					// Wait for the system object close at least os.timeOut milliseconds  
					threads.get(i).join(threads.get(i).getExitTimeout());
				} catch (InterruptedException e) {
					// ignored
				}
				if (!threads.get(i).isAlive()) {
					threads.remove(i);
					i--;
				}
			}
		}
	}

	public void removeSystemObject(SystemObject o) {
    	systemObjects.remove(o.getXPath());
	}

	public void lockObject(SystemObject sobject) throws Exception {
		sobject.getLockObject().acquire();
	}

	private synchronized void initLockObjects() {
    	Iterator<?> iter = systemObjects.values().iterator();
		while (iter.hasNext()) {
			SystemObject o = (SystemObject) iter.next();
			synchronized (o.getLockObject()) {
				o.getLockObject().release();
			}
		}
	}

	public void releaseObject(SystemObject sobject) {
		sobject.getLockObject().release();
	}

	public synchronized void addFailure(Test test, AssertionFailedError t) {
        Iterator<?> iter = systemObjects.values().iterator();
		while (iter.hasNext()) {
			SystemObject d = (SystemObject) iter.next();
			if (d == null) {
				continue;
			}
			if (d instanceof TestListener) {
				TestListener tl = (TestListener) d;
				tl.addFailure(test, t);
			}
		}
	}

	public synchronized void endTest(Test test) {
		initLockObjects();
        Object[] devs = systemObjects.values().toArray();
		for (int i = 0; i < devs.length; i++) {
			SystemObject d = (SystemObject) devs[i];
			if (d == null) {
				continue;
			}
			if (d.getLifeTime() == SystemObject.TEST_LIFETIME) {
				try {
					if (!d.isClosed()) {
						d.close();
					}
				} finally {
                    systemObjects.remove(d.getXPath());
				}
			} else if (d instanceof TestListener) {
				TestListener tl = (TestListener) d;
				tl.endTest(test);
			}
		}
	}

	public synchronized void startTest(Test test) {
		initLockObjects();
        Iterator<?> iter = systemObjects.values().iterator();
		while (iter.hasNext()) {
			SystemObject d = (SystemObject) iter.next();
			if (d == null) {
				continue;
			}
			if (d instanceof TestListener) {
				TestListener tl = (TestListener) d;
				tl.startTest(test);
			}
		}
	}

	public void checkObject(String sobjectName) throws Exception {
		SystemObjectImpl so = (SystemObjectImpl) getSystemObject(sobjectName);
		so.check();
	}

	/**
	 * 
	 * @param onlySO
	 *            true for getting only the Objects implementing SystemObject
	 * 
	 * 
	 * @return Vector of System Object Class Names
	 */
	public static Vector<String> getAllObjects(boolean onlySO) {

		Vector<String> sysObjs = new Vector<String>();
		List<?> list = null;

		try {
			/**
			 * get "sut" node
			 */
			list = SutFactory.getInstance().getSutInstance().getAllValues("/sut");

			Element node = (Element) list.get(0);

			/**
			 * get all childes for sut node
			 */
			NodeList nl = node.getChildNodes();

			for (int i = 0; i < nl.getLength(); i++) {
				Element el;

				if ((nl.item(i)) instanceof Element) {
					boolean addObject = false;

					el = (Element) nl.item(i);

					/**
					 * bring only SOs.
					 */
					if (onlySO) {
						/**
						 * get Class Element
						 */
						Element subEl = (Element) XPathAPI.selectSingleNode(el, "./class");

						if (subEl != null) {
							/**
							 * getting right Class Loader
							 */
							ClassLoader cl = LoadersManager.getInstance().getLoader();

							/**
							 * Getting SO Class
							 */
							Class<?> soClass = cl.loadClass(subEl.getTextContent());				

							/**
							 * find out if in any level the class is implenting
							 * SystemObject
							 */
							while (soClass != null && !addObject) {
								Class<?>[] interfaces = soClass.getInterfaces();

								for (int j = 0; j < interfaces.length; j++) {
									if (interfaces[j].getClass().equals(SystemObject.class.getClass())) {
										addObject = true;
										break;
									}
								}
								soClass = soClass.getSuperclass();
							}
						}
					} else {
						addObject = true;
					}

					if (addObject)
						sysObjs.add(el.getNodeName());
				}
			}

		} catch (Exception e) {
			log.log(Level.WARNING, "Fail To get all System Objects");
		}
		return sysObjs;
	}

	/**
	 * return All Objects  class name from sut
	 * 
	 * @return Vector
	 */
	public static Vector<String> getAllObjects() {
		return getAllObjects(false);
	}

	public void pausedAllObjects() {
        Iterator<?> iter = systemObjects.values().iterator();
		while (iter.hasNext()) {
			SystemObject d = (SystemObject) iter.next();
			if (d == null) {
				continue;
			}
			d.pause();
		}
	}

	public void resumeAllObjects() {
        Iterator<?> iter = systemObjects.values().iterator();
		while (iter.hasNext()) {
			SystemObject d = (SystemObject) iter.next();
			if (d == null) {
				continue;
			}
			d.resume();
		}
	}
	
	/**
	 * Resets the systemObjects map and creates a new thread safe map.
	 */
	private void resetSeystemObjectsMap(){
		systemObjects	=	new HashMap<String, SystemObject>();
	}

    /**
     * Add a system object to the list of managed system objects.<br>
     * It is assumed that the system object was already initialized elsewhere.<br>
     * This causes the system object to close when closeAllObjects is called.<br>
     *
     * @param systemObject - the system object to add.
     */
	public void addSystemObject(SystemObject systemObject) {
		String xPath = "/sut/" + systemObject.getName();
        systemObjects.put(xPath, systemObject);		
	}
}

class SystemObjectCloseThread extends Thread{

	private static Logger log = Logger.getLogger(SystemObjectCloseThread.class.getName());
	SystemObject so;
	
	public SystemObjectCloseThread(SystemObject so){
		this.so = so;
	}
	
	public void run(){
		if(so != null){
			setName("Close" + so.getName());
			log.fine("Closing " + so.getName());
			so.close();
		}
	}
	
	/**
	 * @return time to wait for the system object to close
	 */
	public long getExitTimeout() {
		return so.getExitTimeout();
	}

}
