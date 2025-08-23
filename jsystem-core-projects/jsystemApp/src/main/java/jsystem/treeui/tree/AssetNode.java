/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.fixture.Fixture;
import jsystem.framework.scenario.RunnerScript;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioHelpers;
import jsystem.framework.scripts.ScriptEngine;
import jsystem.framework.scripts.ScriptExecutor;
import jsystem.framework.scripts.ScriptsEngineManager;
import jsystem.framework.system.DefaultSystemObjectAdaptor;
import jsystem.framework.system.SystemObjectAdaptor;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.TestFilterManager;
import jsystem.treeui.suteditor.planner.SutTreeNode;
import jsystem.treeui.suteditor.planner.SutTreeNode.NodeType;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;
import jsystem.utils.beans.AsmUtils;
import jsystem.utils.beans.MethodElement;
import junit.framework.SystemTestCase;
import junit.framework.SystemTestCase4;
import junit.framework.TestCase;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Representing Node at the Tests tree.
 * 
 * 
 * @author guy.arieli
 * 
 */
public abstract class AssetNode extends DefaultMutableTreeNode implements Comparable<Object>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7663450817941582253L;

	protected static Logger log = Logger.getLogger(AssetNode.class.getName());

	protected boolean isSelected;

	private boolean isClassPath;

	private static Vector<String[]> loadErrors = new Vector<String[]>();

	public static void initFailLoadClassVector() {
		loadErrors = null;
	}

	public static boolean isErrorsFound() {
		return (loadErrors != null);
	}

	public static Vector<String[]> getLoadsErrors() {
		return loadErrors;
	}

	public AssetNode(AssetNode parent, Object userObject) {
		super(userObject);
		setParent(parent);

		isSelected = false;
	}
	public AssetNode(){
		isSelected = false;
		if (children == null) {
			children = new Vector();
		}
	}

	@SuppressWarnings("unchecked")
	protected void initChildren(Object[] child) throws Exception {

		if (children == null) {
			children = new Vector();
		}
		for (int i = 0; i < child.length; i++) {
			if (child[i] instanceof File) {
				File f = (File) child[i];
				
				ScriptEngine se =ScriptsEngineManager.getInstance().findExecutor(f);
				if(se != null){
					ScriptExecutor[] executors = se.getExecutor(f);
					for(ScriptExecutor executor: executors){
						//executor.configEngine(se);
						executor.configFilePath(f.getAbsolutePath().substring(JSystemProperties.getCurrentTestsPath().length() + 1).replace('\\', '/'));
						RunnerScript rs = new RunnerScript(executor);
						children.addElement(new ScriptNode(this, rs));
					}
				}
				
				String fileNameToLower = f.getName().toLowerCase();
				if (f.isDirectory()) {
					children.add(new DirectoryNode(this, f));
				} else if (f.isFile()) {
					if (isJar(f)) {
						if (exludedJar(f)) {
							continue;
						}
						children.add(new JarNode(this, f));
					} else if (fileNameToLower.endsWith(".xml")) {
						File root = (File) this.getRootUserObject();
						String resource = f.getAbsolutePath().substring(
								root.getAbsolutePath().length());
						if (!resource.matches(".sut.*") && Scenario.isScenario(resource)) {
							/**
							 * init scenario
							 */
							String scenarioName = f
									.getAbsolutePath()
									.substring(
											root.getAbsolutePath().length() + 1,
											f.getAbsolutePath().length() - 4);
							if (TestFilterManager.getInstance().filter(
									scenarioName)) {
								continue;
							}
							if (ScenarioHelpers.isPackedScenario(resource.replace(".xml",""))){
								children.add(new ScenarioAsATestNode(this, scenarioName,
										Scenario.getMeaningfulNameFromScenarioFile(f)));
							}else{
								children.add(new ScenarioNode(this, scenarioName,
										Scenario.getMeaningfulNameFromScenarioFile(f)));
							}
						}
					} else if(fileNameToLower.endsWith(".class") && ! fileNameToLower.endsWith("$py.class")){ // class
						File root = (File) this.getRootUserObject();
						String className;
						try {
							className = StringUtils.getClassName(f.getPath(),
									root.getPath());
						} catch (Throwable e) {
							log.log(Level.FINE, "Unable to extract class name",
									e);
							continue;
						}
						Class testClass;
						try {
							testClass = LoadersManager.getInstance()
									.getLoader().loadClass(className);
							/*
							 * Check the class is a non abstract test
							 */
							boolean isAbstract = Modifier.isAbstract(testClass.getModifiers());
							
							if (!isAbstract) {
								// JUnit3 style test case or fixture
								if (TestCase.class.isAssignableFrom(testClass)) {
									try { 
										/**
										 *  if the class don't have empty
										 *  constractor it will not show
										 *  in the tree
										 */
										testClass.getConstructor(new Class[0]);
									} catch (Throwable t) {
										continue;
									}
									
									/**
									 * check if class is a Fixture and the Option to
									 * show i ton tests tree is avilable
									 */
									boolean fixturesOnTestTree = "false".equals(
										JSystemProperties.getInstance().getPreference(
												FrameworkOptions.FIXTURES_ON_TEST_TREE));
									if (Fixture.class.isAssignableFrom(testClass) && !fixturesOnTestTree) {
										if (TestFilterManager.getInstance().filter(testClass.getName())) {
											continue;
										}
										children.add(new FixtureNode(this, testClass.getName()));
									} else {
										children.add(new TestCaseNode(this, testClass));
									}
								
								// JUnit 4 style test case 
								} else {
									children.add(new TestCaseNode(this, testClass));
								}
							}
						} catch (Throwable ex) {
							if (loadErrors == null) {
								loadErrors = new Vector();
								loadErrors.addElement(new String[] {
										"Class name", "Method name",
										"Error cause" });
							}
							loadErrors.addElement(new String[] {
									className,
									" ",
									ex.getClass().getName() + ": "
											+ ex.getMessage() });
							continue;
						}
					}
				}
			} else if (child[i] instanceof JarEntry) {
				JarEntry entry = (JarEntry) child[i];
				if (entry.isDirectory()) {
					children.add(new JarEntryNode(this, entry));
				} else {
					String className;
					try {
						className = StringUtils.getClassName(entry.getName(),
								"");
					} catch (Exception e) {
						log.log(Level.FINE, "Unable to extract class name", e);
						continue;
					}
					try {
						Class testClass = LoadersManager.getInstance()
								.getLoader().loadClass(className);

						if (SystemTestCase4.class.isAssignableFrom(testClass)) {
							children.add(new TestCaseNode(this, testClass));
						}
					} catch (Throwable ignore) {
						log.log(Level.FINE, "Fail to add class", ignore);
					}

				}
			}
		}
		if("true".equals(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SORT_ASSETS_TREE))){
			Collections.sort(children, null);
		}
	}

	protected Object getRootUserObject() {
		if (parent instanceof RootNode) {
			return getUserObject();
		} else {
			return ((AssetNode) parent).getRootUserObject();
		}
	}

	protected int getTestsCount() {
		if(children == null){
			return 0;
		}
		int size = children.size();
		int count = 0;
		for (int i = 0; i < size; i++) {
			count += ((AssetNode) children.elementAt(i)).getTestsCount();
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public void cleanLeafsWithoutTests() {
		if (isLeaf()) {
			return;
		}
		Vector<AssetNode> c = (Vector<AssetNode>) children.clone();
		for (int i = 0; i < c.size(); i++) {
			AssetNode n = (AssetNode) c.elementAt(i);
			if (n.getTestsCount() == 0) {
				children.removeElement(n);
			}
		}
		for (int i = 0; i < children.size(); i++) {
			AssetNode n = (AssetNode) children.elementAt(i);
			n.cleanLeafsWithoutTests();
		}
	}

	private boolean isJar(File file) {
		String filePath = file.getPath().toLowerCase();
		return filePath.endsWith(".jar");
	}

	private boolean exludedJar(File f) {
		return false;
	}

	public Enumeration<Object> getAllChildren() {
		Vector<Object> tmp = new Vector<Object>();
		get(this, tmp);
		return tmp.elements();
	}

	public int getSelectedChildrenCount() {
		Vector<Object> tmp = new Vector<Object>();
		get(this, tmp);
		return tmp.size();
	}

	// Get all the children
	@SuppressWarnings("unchecked")
	private void get(AssetNode node, Vector v) {

		if (node.children() == null) {
			return;
		}

		// copy the vector
		for (Enumeration e = node.children(); e.hasMoreElements();) {
			v.addElement(e.nextElement());
		}

		for (Enumeration e = node.children(); e.hasMoreElements();) {
			AssetNode child = (AssetNode) e.nextElement();
			get(child, v);
		}

	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean status) {
		boolean statusChanged = status != isSelected;
		isSelected = status;
		if (statusChanged){
			if (isSelected) {
				TestTreePanel.currentSelectedTests++;
			} else {
				TestTreePanel.currentSelectedTests--;
			}
		}
		selectChildren(status);
	}

	public void toggleSelection() {
		setSelected(!isSelected);
	}

	private void selectChildren(boolean isSelected) {
		if (children == null) {
			return;
		}
		for (int i = 0; i < children.size(); i++) {
			((AssetNode) children.get(i)).setSelected(isSelected);
		}
	}

	protected void loadDefaultSelection(Scenario selector) {
		if (children == null) {
			return;
		}
		for (int i = 0; i < children.size(); i++) {
			((AssetNode) children.get(i)).loadDefaultSelection(selector);
		}
	}

	/**
	 * @return Returns the isClassPath.
	 */
	public boolean isClassPath() {
		return isClassPath;
	}

	/**
	 * @param isClassPath
	 *            The isClassPath to set.
	 */
	public void setClassPath(boolean isClassPath) {
		this.isClassPath = isClassPath;
	}
    /**
     * Utility function to build the real tree table model. This is done by going
     * recursively all over the XML document.
     * The idea is:
     * 1. Find all sub system objects
     * 2. Find all fields that are not in the sut
     * @param root
     * @param doc
     * @param nonModelDoc
     * @return new SutTreeNode
     * @throws Exception
     */
    public static SutTreeNode createModel(SutTreeNode root, Document doc) throws Exception {
        // If the parent is the root node search for all the system object
        // found under the sut tag.
        if (root.getType() == NodeType.ROOT) {
            NodeList list = XPathAPI.selectNodeList(doc, "/sut/*[class]");
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item(i);
                if (n instanceof Element) {
                    SutTreeNode node = new SutTreeNode(NodeType.MAIN_SO, n.getNodeName());
                    if (XmlUtils.isSubTagExist(n, "class")) {
                        node.setClassName(XmlUtils.getSubTagValue(n, "class"));
                    }                   
                    node.setElement((Element) n);
                    root.add(createModel(node, doc));
                }
            }
        } else {
            Element element = root.getElement();
            if (!XmlUtils.isSubTagExist(element, "class")) { // not a system object
                                                    // continue
                return null;
            }
            String className = XmlUtils.getSubTagValue(element, "class");
            Class<?> systemObjectClass = LoadersManager.getInstance().getLoader()
                    .loadClass(className);
            // Go over all the existing nodes they can be sub
            // system objects or properties
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node n = nodeList.item(i);
               
                if (!(n instanceof Element)) {
                    continue;
                }
               
                // Look for tags
                if (XmlUtils.isSubTagExist(n, "class")) {
                    // if it as a class tag it's system object
                    SutTreeNode sub_so;
                    String atrib = ((Element) n).getAttribute("index");
                    String nodeName = ((Element)n).getNodeName();
                    if (atrib == null || atrib.equals("")) {
                        sub_so = new SutTreeNode(NodeType.SUB_SO, nodeName);
                    } else {
                        sub_so = new SutTreeNode(NodeType.ARRAY_SO, nodeName);
                        sub_so.setIndex(Integer.parseInt(atrib));
                        Field field = systemObjectClass.getField(nodeName);
                        if(field != null && field.getType().isArray()){
                            sub_so.setArraySuperClassName(field.getType().getComponentType().getName());
                        }
                    }
                    sub_so.setElement((Element) n);
                    // Set the name of the class
                    sub_so.setClassName(XmlUtils.getSubTagValue(n, "class"));
                    // See if there is underlying objects
                    root.add(createModel(sub_so, doc));
                }
            }  
            if(root.getType().equals(NodeType.SUB_SO) || root.getType().equals(NodeType.MAIN_SO) || root.getType().equals(NodeType.ARRAY_SO)){
            	String cName = root.getClassName();
            	try {
            		// Find all the methods that are supported
                	Class<?> soClass = LoadersManager.getInstance().getLoader().loadClass(cName);
                	SystemObjectAdaptor adaptor = new DefaultSystemObjectAdaptor();
                	Method[] methods = soClass.getMethods();
                	for(Method method: methods){
                		if(!adaptor.isMethodSupported(method)){
                			continue;
                		}
                		SystemObjectMethod tn = new SystemObjectMethod(method.getName(), AsmUtils.getParameterNames(method),method.getParameterTypes(),MethodElement.getMethodDescriptor(method));
                		root.add(tn);
                	}
                	
            	} catch (Exception e){
            		e.printStackTrace();
            	}
            	
            }
           
        }
        return root;
    }
    
	@Override
	public int compareTo(Object o) {
		return this.getUserObject().toString().compareTo(((AssetNode)o).getUserObject().toString());
	}

}
