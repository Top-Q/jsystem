package com.ignis.embeddedcatalina;

import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.StringUtils;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;

/**
 * SystemObject for easily embedding Tomcat servlet
 * container in a java application.
 * To understand Tomact configuration please read the following article:
 * {@linkplain http://www.onjava.com/pub/a/onjava/2002/07/31/tomcat.html}
 * 
 * By default the system object creates one service with one http connector which 
 * listens on port 8083, one host, and one context.
 * the doc base of the context is current dir.
 * All setting of the default service can be altered by altering system object's members.
 * If {{@link #defaultHost} is set to null, default service is not loaded.
 *  
 * In addition to the default service, the user can signal catalina to load 
 * a server configuration file by giving a value to the {@link #serverXmlPath} member.
 * 
 * Example server configuration file can be found here:
 * {@link tests/com/ignis/embeddedcatalina/server-embed.xml}
 * 
 * @author goland
 */
public class EmbeddedCatalina extends SystemObjectImpl{
	/**
	 * Catalina home
	 */
	private String servletContainerHome;
	/**
	 * Default connector port
	 */
	private int defaultConnectorPort = 8083;
	/**
	 * Default host name
	 */
	private String defaultHost = "localhost";
	/**
	 * Default connector bind address
	 */
	private String defaultBindAddress;
	/**
	 * Default host application base
	 */
	private String defaultAppBase = "";
	/**
	 * Default context doc base
	 */
	private String defaultDocBase = "";
	/**
	 * Default context url path
	 */
	private String defaultContextPath = "";
	/**
	 * If true default connector will work with https protocol.
	 */
	private boolean isDefaultSecured = false;
	
	/**
	 * Path (on file system or oin classpath to catalina server configuration file.
	 */
	private String serverXmlPath;
	
	/**
	 * Whether to wait after start
	 */
	private boolean wait = false;
	
	private IgnisCatalina catalina;
	
	public void init() throws Exception{
		super.init();
		catalina = new IgnisCatalina();
		initCatalinaHome();
		loadCatalina();
		catalina.setAwait(isWait());
		createDefaultEngine();
	}
	public void start() throws Exception{
		catalina.start();
	}
	
	public void close() {
		catalina.stop();
	}
	
	private void createDefaultEngine() throws Exception{
		if (getDefaultHost() == null){
			report.report("Default host is null, ignoring default configuration");
			return;
		}
		StandardService service = new StandardService();
		// Create an engine
	    Engine engine = catalina.createEngine();
	    engine.setDefaultHost(getDefaultHost());
	    // Create a default virtual host
	    Host  host = catalina.createHost(getDefaultHost(),getDefaultAppBase());
	    engine.addChild(host);
	    // Create the ROOT context
	    Context context = catalina.createContext(getDefaultContextPath(),getDefaultDocBase());
	    context.setParentClassLoader(this.getClass().getClassLoader());
	    host.addChild(context);
	    
	    // Assemble and install a default HTTP connector
	    Connector connector =
	      catalina.createConnector(getDefaultBindAddress(), getDefaultConnectorPort(), isDefaultSecured());
	    service.addConnector(connector);
	    service.setContainer(engine);
	    catalina.getServer().addService(service);	
	}

	private void initCatalinaHome() throws Exception{
		String homePath = System.getProperty("user.dir");
		if (getServletContainerHome() != null){
			homePath=getServletContainerHome().trim();
		}
		System.setProperty("catalina.home", homePath);
	}
	
	private void loadCatalina() throws Exception {
		String[] args = new String[]{"-config",_getServerXml()};
		catalina.load(args);
	}
	
	private String _getServerXml() throws Exception {
		String resourcePath = "server-embed.xml";
        if (getServerXmlPath() != null){
        	resourcePath =  getServerXmlPath().trim();
        }
		return resourcePath;
	}
	public String getServerXmlPath() {
		return serverXmlPath;
	}
	public void setServerXmlPath(String serverXmlPath) {
		this.serverXmlPath = serverXmlPath;
	}
	public String getServletContainerHome() {
		return servletContainerHome;
	}
	public void setServletContainerHome(String servletContainerHome) {
		this.servletContainerHome = servletContainerHome;
	}
	public boolean isWait() {
		return wait;
	}
	public void setWait(boolean wait) {
		this.wait = wait;
	}
	public int getDefaultConnectorPort() {
		return defaultConnectorPort;
	}
	public void setDefaultConnectorPort(int defaultConnectorPort) {
		this.defaultConnectorPort = defaultConnectorPort;
	}
	
	public String getDefaultAppBase() {
		return defaultAppBase;
	}
	public void setDefaultAppBase(String defaultAppBase) {
		this.defaultAppBase = defaultAppBase;
	}
	public String getDefaultBindAddress() {
		return defaultBindAddress;
	}
	public void setDefaultBindAddress(String defaultBindAddress) {
		this.defaultBindAddress = defaultBindAddress;
	}
	public String getDefaultDocBase() {
		return defaultDocBase;
	}
	public void setDefaultDocBase(String defaultDocBase) {
		this.defaultDocBase = defaultDocBase;
	}
	public String getDefaultHost() {
		return defaultHost;
	}
	public void setDefaultHost(String defaultHost) {
		this.defaultHost = defaultHost;
	}
	public boolean isDefaultSecured() {
		return isDefaultSecured;
	}
	public void setDefaultSecured(boolean isDefaultSecured) {
		this.isDefaultSecured = isDefaultSecured;
	}
	public String getDefaultContextPath() {
		return defaultContextPath;
	}
	public void setDefaultContextPath(String defaultContextPath) {
		if (!StringUtils.isEmpty(defaultContextPath)){
			if (!defaultContextPath.trim().startsWith("/")){
				defaultContextPath = "/"+defaultContextPath.trim();
			}
		}
		this.defaultContextPath = defaultContextPath;
	}
	
	class IgnisCatalina extends Catalina{
		public Server getServer(){
			return server;
		}
	}

}