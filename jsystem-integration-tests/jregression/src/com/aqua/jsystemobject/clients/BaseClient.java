package com.aqua.jsystemobject.clients;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.aqua.jsystemobject.XmlHandler;
import com.aqua.jsystemobject.handlers.JServerHandlers;

import jsystem.framework.system.SystemObjectImpl;

public abstract class BaseClient extends SystemObjectImpl{
	private static Logger log = Logger.getLogger(BaseClient.class.getName());
	
	XmlHandler xmlHandler;
	JServerHandlers handler;
	protected Process serverProcess;
	protected String userDir = null;
	protected String host = "127.0.0.1";
	protected int port = 8082;
	protected boolean maskExit = false;
	
	public BaseClient(){
		super();
		xmlHandler = XmlHandler.getInstance();
	}

	public BaseClient(Process serverProcess, int port, String userDir) {
		this();
		this.serverProcess = serverProcess;
		this.port = port;
		xmlHandler.setPort(port);
		this.userDir = userDir;
	}
	
	abstract String getHandlerName();
	
	
	/**
	 * if have parameters, create a vector and call the xmlCommand with the vector of parameters
	 * @param title
	 * @param method
	 * @param objects
	 * @return
	 * @throws Exception
	 */
	public Object callHandleXml(String title, String method, Object...objects) throws Exception{
		log.info("getHandlerName returns: "+getHandlerName());
		if(objects.length == 0){
			return callHandleXml(title, method, new Vector<Object>());
		}
		Vector<Object> v = new Vector<Object>();
		for (Object object : objects){
			if (object == null){
				v.add("");
			}else{
				v.addElement(object);
			}
		}
		return callHandleXml(title, method,v);
	}
	/**
	 * if Handler side method expects Vector as argument, pass true to asArray.
	 * @param asArray
	 * @param title
	 * @param method
	 * @param objects
	 * @return
	 * @throws Exception
	 */
	public Object callHandleXml(boolean asArray, String title, String method, Object...objects) throws Exception{
		if(asArray == false){
			return callHandleXml(title, method, objects);
		}
		else{
			Vector<Object> v = new Vector<Object>();
			v.addElement(objects);
			return callHandleXml(title, method, v);
		}
	}
	/**
	 * will call a remote method on the server passing parameters as argument for remote method
	 * @param title
	 * @param method
	 * @param v
	 * @return
	 * @throws Exception
	 */
	public Object callHandleXml(String title, String method, Vector<Object> v) throws Exception{
		log.info("getHandlerName returns: "+getHandlerName());
		return xmlHandler.handleXmlCommand(title, getHandlerName()+"."+method, v);
	}
	/**
	 * will call the function on the server with out passing arguments to the functions
	 * handles the passing of empty vector on xmlRpc for the user.
	 * @param title
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public Object callHandleXml(String title, String method) throws Exception{
		return callHandleXml(title, method, new Vector<Object>());
	}
	
	public void screenCapture() throws Exception {
		BufferedImage screencapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

		// Save as JPEG
		File file = new File(report.getCurrentTestFolder(), "screencapture.jpg");
		ImageIO.write(screencapture, "jpg", file);
		report.addLink("screen capture", "screencapture.jpg");

	}
	
	/**
	 * get server runner directory
	 */
	public String getUserDir() throws Exception {
		if (userDir == null) {
			Vector<Object> v = new Vector<Object>();
			userDir = (String) callHandleXml("get user dir", "getUserDir", v);
		}
		return userDir;
	}
	
	/**
	 * sets the system property, user.dir to the specified userdir string
	 * @param userDir
	 * @return
	 */
	public int setUserDir(String userDir) {
		System.setProperty("user.dir", userDir);
		return 0;
	}
	
	public boolean isMaskExit() {
		return maskExit;
	}

	public void setMaskExit(boolean maskExit) {
		this.maskExit = maskExit;
	}
	

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Process getServerProcess() {
		return serverProcess;
	}

	public void setServerProcess(Process serverProcess) {
		this.serverProcess = serverProcess;
	}
	
	public Vector<Object> vectorize(Object...objects) {
		Vector<Object> vector = new Vector<Object>();
		for (Object item : objects) {
			vector.add(item.toString());
		}
		return vector;
	}
}
