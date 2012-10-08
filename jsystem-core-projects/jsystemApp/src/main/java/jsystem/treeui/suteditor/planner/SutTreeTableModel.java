/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.suteditor.planner;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JTable;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.ValidationError;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.sobrows.SOProcess;
import jsystem.treeui.suteditor.planner.SutTreeNode.NodeType;
import jsystem.treeui.utilities.CellEditorModel;
import jsystem.utils.StringUtils;
import jsystem.utils.XmlUtils;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.BeanUtils;
import jsystem.utils.beans.CellEditorType;

import org.apache.xpath.XPathAPI;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;

/**
 * The SUT planner model is used to build the SUT tree table, and
 * support operation on the tree table.
 *
 * @author Michael Oziransky
 */
public class SutTreeTableModel extends AbstractTreeTableModel implements CellEditorModel {

    private static final long serialVersionUID = -9145668070975963430L;
	
    private static final String SUT_TAGS_COLUMN_NAME = "Sut tags";

	private static final String ACTUAL_VALUE_COLUMN_NAME = "Actual value";

	private static final String CLASS_TAG = "class";

	private static final String SUT_TAG = "sut";

	// Names of the columns.
    static protected String[] cNames = { SUT_TAGS_COLUMN_NAME, "Class name",
            "Default value", ACTUAL_VALUE_COLUMN_NAME, "Java documentation" };

    // Types of the columns.
    static protected Class<?>[] cTypes = { TreeTableModel.class, String.class,
            String.class, String.class, String.class, Enum.class };

    /**
     * The root of the tree
     */
    private SutTreeNode root;
   
    /**
     * The original document that was the base for this model
     */
    private Document originalDocument;

    private static JavaDocBuilder builder;
   
    /**
     * All the system objects implementations found
     */
    private ArrayList<String> systemObjectList = null;
   
    private String filter = null;
    private FilterType filterType = FilterType.ALL;
   
    /**
     * Indicates whether the model has been changed
     */
    private boolean hasChanged;
   
    /**
     * Array List of <code>Document</code> elements.
     * This list holds all non system objects elements.
     */
    private static ArrayList<Element> nonModelElements = null;
    
    static HashSet<String> groups = new HashSet<String>();
   
    /**
     * We have to implement this
     */
    public SutTreeTableModel() {
        super(new SutTreeNode(NodeType.ROOT, ""));
    }
    
   
    /**
     * Create a Sut tree model based on XML document
     * @param document
     * @return SutTreeTableModel
     * @throws Exception
     */
    public static SutTreeTableModel createNewModel(Document document) throws Exception {
        // Handle the javadoc reader
        builder = null;
        groups = new HashSet<String>();
        groups.add("");
        try {
            File testDir = new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.RESOURCES_SOURCE_FOLDER));
            builder = SOProcess.initBuilder(CommonResources.getAllOptionalLibDirectories(), new String[] {
                    testDir.getAbsolutePath(), (new File(testDir.getParentFile(), "src")).getAbsolutePath() });
        } catch (Exception exception) {
        }
       
        // Handle the xml parsing
        SutTreeNode root = new SutTreeNode(NodeType.ROOT, "");
        Element sutTag = null;

        // If the sut tag is not found (probably a new doc), create it
        if (XmlUtils.isSubTagExist(document, SUT_TAG)) {
            // We have a sut tag. Find it and set it for the root sut node.
            NodeList nodeList = document.getChildNodes();
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node currentNode = nodeList.item(index);
                if (currentNode instanceof Element) {
                    if (((Element) currentNode).getTagName().equals(SUT_TAG)) {
                        sutTag = (Element) currentNode;
                        break;
                    }
                }
            }
//            Set the root element
            root.setElement(sutTag);
        } else {
            sutTag = document.createElement(SUT_TAG);
            document.appendChild(sutTag);
            root.setElement(sutTag);
        }

        // Create a list of all non system object elements
        nonModelElements = new ArrayList<Element>();
        NodeList list = XPathAPI.selectNodeList(document, "/sut/*[not(class)]");
        for (int index = 0; index < list.getLength(); index++) {
            Node currentNode = list.item(index);
            if (currentNode instanceof Element) {
                nonModelElements.add((Element)currentNode);
            }
        }
       
       
        // Create a full model of all system objects
        root = createFullModel(root, document, builder);
        return new SutTreeTableModel(root, document);
    }
    private static void addBeanGroupToGenericGroup(BeanElement element){
    	if(element != null){
    		String[] groupsString = element.getGroups();
    		if(groupsString != null){
    			for(String g: groupsString)
    			groups.add(g);
    		}
    	}
    }
    /**
     * Utility function to build the real tree table model. This is done by going
     * recursively all over the XML document.
     * The idea is:
     * 1. Find all sub system objects
     * 2. Find all fields that are not in the sut
     * 3. Find all default values
     * 4. Update the document to contain all new fields
     * @param root
     * @param doc
     * @param nonModelDoc
     * @return new SutTreeNode
     * @throws Exception
     */
    private static SutTreeNode createFullModel(SutTreeNode root, Document doc, JavaDocBuilder builder) throws Exception {
        // If the parent is the root node search for all the system object
        // found under the sut tag.
        if (root.getType() == NodeType.ROOT) {
            NodeList list = XPathAPI.selectNodeList(doc, "/sut/*[class]");
            for (int index = 0; index < list.getLength(); index++) {
                Node currentNode = list.item(index);
                if (currentNode instanceof Element) {
                    SutTreeNode node = new SutTreeNode(NodeType.MAIN_SO, currentNode.getNodeName());
                    if (XmlUtils.isSubTagExist(currentNode, CLASS_TAG)) {
                        node.setClassName(XmlUtils.getSubTagValue(currentNode, CLASS_TAG));
                    }                   
                    node.setElement((Element) currentNode);
                    root.add(createFullModel(node, doc, builder));
                }
            }
        } else {
            Element element = root.getElement();
            if (!XmlUtils.isSubTagExist(element, CLASS_TAG)) { // not a system object
            	 									// continue
                return null;
            }
            String className = XmlUtils.getSubTagValue(element, CLASS_TAG);
            Class<?> systemObjectClass = LoadersManager.getInstance().getLoader()
                    .loadClass(className);
            ArrayList<Field> potentialFields = SystemObjectBrowserUtils
                    .getSystemObjectField(systemObjectClass);

            // Go over all the potential fields and find once that not defined
            for (Field currentField : potentialFields) {
                if (!XmlUtils.isSubTagExist(element, currentField.getName())) {
                    SutTreeNode subField;
                    String soClassName = null;
                    if (currentField.getType().isArray()) {
                        subField = new SutTreeNode(NodeType.EXTENTION_ARRAY_SO, currentField.getName());
                        soClassName = currentField.getType().getComponentType().getName();
                        subField.setArraySuperClassName(soClassName);
                    } else {
                        subField = new SutTreeNode(NodeType.EXTENTION_SO, currentField.getName());
                        soClassName = currentField.getType().getName();
                    }
                    // Set the name of the class
                    subField.setClassName(soClassName);
                    // Add the sut node to the tree
                    root.add(subField);
                }
            }
            HashMap<String, BeanElement> map = BeanUtils.getBeanMap(systemObjectClass, false, true, BeanUtils.getBasicTypes());

            // Go over all the existing nodes they can be sub
            // system objects or properties
            NodeList nodeList = element.getChildNodes();
            for (int index = 0; index < nodeList.getLength(); index++) {
                Node currentNode = nodeList.item(index);
                if (!(currentNode instanceof Element)) {
                    continue;
                }
                Element currentElement = (Element)currentNode;
				String fieldName = currentElement.getNodeName();
               
                // Look for tags
                if (!XmlUtils.isSubTagExist(currentNode, CLASS_TAG)) {
                    // Skip all class tags
                    if (currentElement.getNodeName().equals(CLASS_TAG)) {                       
                        continue;
                    }
                    // Create new sut node
                    SutTreeNode tagNode = new SutTreeNode(NodeType.TAG, fieldName);
                    // Set the element for this node
                    Node firstNode = currentNode.getFirstChild();
                    String actualValue = new String("");
                    if (firstNode != null && firstNode instanceof Text) {
                        actualValue = ((Text)firstNode).getData().toString();
                    }
                    tagNode.setElement(currentElement);
                    tagNode.setActualValue(actualValue);                   
                    // Find the default value and set it
                    String setter = "set" +  StringUtils.firstCharToUpper(fieldName);
                    String defaultValue = SystemObjectBrowserUtils
                                            .getDefaultValueFor(systemObjectClass, setter);
                    addBeanGroupToGenericGroup(map.get(fieldName));
                    tagNode.setBean(map.get(fieldName));
                    if (defaultValue == null) {
                        defaultValue = new String("N/A");
                    }
                    tagNode.setDefaultValue(defaultValue);
                    tagNode.setJavadoc(getJavadoc(builder, className, setter));
                    // Add to the tree
                    root.add(tagNode);
                } else {
                    // if it as a class tag it's system object
                    SutTreeNode sub_so;
                    String atrib = currentElement.getAttribute("index");
                    String nodeName = currentElement.getNodeName();
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
                    sub_so.setElement(currentElement);
                    // Set the name of the class
                    sub_so.setClassName(XmlUtils.getSubTagValue(currentNode, CLASS_TAG));
                    // See if there is underlying objects
                    root.add(createFullModel(sub_so, doc, builder));
                }
            }           
           
            // Go over all the setter and if not already set add it as a optional tag
            Collection<BeanElement> beansCollections = map.values();
            for (BeanElement currentBean : beansCollections) {
            	String setter = currentBean.getSetMethod().getName();
                if (SystemObjectBrowserUtils.isSystemObjectSetMethod(setter)) {
                    continue;
                }
                String setterTag = StringUtils.firstCharToLower(setter
                        .substring(3)); // remove the set
                if (!XmlUtils.isSubTagExist(element, setterTag)) {
                    // Create new sut node
                    SutTreeNode optionalTag =
                        new SutTreeNode(NodeType.OPTIONAL_TAG, setterTag);
                    String defaultValue = SystemObjectBrowserUtils
                                            .getDefaultValueFor(systemObjectClass, setter);                   
                    if (defaultValue == null) {
                        defaultValue = new String("N/A");
                    }
                    optionalTag.setActualValue("");
                    optionalTag.setDefaultValue(defaultValue);
                    optionalTag.setJavadoc(getJavadoc(builder, className, setter));
                    addBeanGroupToGenericGroup(map.get(setterTag));
                    optionalTag.setBean(map.get(setterTag));
                    // Add the sut node to the tree
                    root.add(optionalTag);
                }
            }           
        }
        return root;
    }
    /**
     * Gets the string representation of the javadoc for a given class
     * @param builder
     *             Javadoc builder
     * @param soClass
     *             Name of the class to get the javadoc for
     * @param soMethod
     *             The method to get the javadoc for
     * @return String
     *             String representation of the javadoc
     */
    private static String getJavadoc(JavaDocBuilder builder,
            String soClass, String soMethod) {
        if (builder == null) {
            return null;
        }
       
        JavaClass cls = builder.getClassByName(soClass);
        JavaClass superCls = null;
        if (cls.getSuperJavaClass() != null){
        	superCls = builder.getClassByName(cls.getSuperJavaClass().getName().toString());
        }else {
        	superCls = builder.getClassByName(cls.getSuperClass().getFullQualifiedName());
        }
        JavaClass[] interfacesCls = cls.getImplementedInterfaces();   
       
        JavaMethod methods[] = cls.getMethods();
        // Go over the methods and see if we have the setter one
        // that matches the given method name.
        // A setter is a method that starts with 'set'
        // A setter is a method that has one parameter
        for (int index = 0; index < methods.length; index++) {
            JavaMethod method = methods[index];
            if (method.getName().equals(soMethod) &&
            		method.getName().startsWith("set") &&
            		method.getParameters().length == 1) {
            	
                StringBuilder buffer = new StringBuilder();
                if (method.getComment() != null){
                    buffer.append(method.getComment());
                }   
                DocletTag[] tags = method.getTags();
                if(tags != null && tags.length > 0){
                    if (buffer.length() > 0){
                        buffer.append("\n");
                    }                           
                    for(DocletTag doclet: tags){
                        buffer.append(doclet.getName()).append(": ").append(doclet.getValue());
                    }
                }
                if (buffer.length() == 0){
                    for (JavaClass interfaceCls : interfacesCls) {
                        JavaMethod interfaceMethods[] = interfaceCls.getMethods();
                        for (int jndex = 0; jndex < interfaceMethods.length; jndex++){
                            JavaMethod interfaceMethod = interfaceMethods[jndex];
                            if (interfaceMethod.getName().equals(method.getName())){
                                if (interfaceMethod.getParameters().length == 1) {
                                    if (interfaceMethod.getComment() != null){                                               
                                        buffer.append(interfaceMethod.getComment());
                                    }                                   
                                    tags = interfaceMethod.getTags();
                                    if(tags != null && tags.length > 0){
                                        buffer.append("\n");
                                        for(DocletTag doclet: tags){
                                            buffer.append(doclet.getName()).append(": ").append(doclet.getValue());
                                        }
                                    }
                                }
                            }                           
                        }
                    }
                }
                if (buffer.length() == 0){
                    JavaMethod superMethods[] = superCls.getMethods();
                    for (int jndex = 0; jndex < superMethods.length; jndex++){
                        JavaMethod superMethod = superMethods[jndex];
                        if (superMethod.getName().equals(method.getName())){
                            if (superMethod.getParameters().length == 1) {
                                if (superMethod.getComment() != null){
                                    buffer.append(superMethod.getComment());
                                }                                   
                                tags = superMethod.getTags();
                                if(tags != null && tags.length > 0){
                                    buffer.append("\n");
                                    for(DocletTag doclet: tags){
                                        buffer.append(doclet.getName()).append(": ").append(doclet.getValue());
                                    }
                                }
                            }
                        }                           
                    }
                }
               
                if (buffer.length() == 0){
                    buffer.replace(0, buffer.length(), "N/A");
                }
                return buffer.toString();
            }
        }       
        return null;
    }
   
    /**
     * Private constructor. use the <code>createNewModel</code> to create a model.
     * @param root the model tree node root.
     * @param doc the base document
     * @throws Exception
     */
    private SutTreeTableModel(TreeNode root, Document doc) throws Exception {
        super(root);
        this.root = (SutTreeNode)root;
        this.originalDocument = doc;
    }

    /**
     * Add a system object to the given parent
     *
     * @param parent
     *               Parent to add the system object to
     * @param soName
     *            System object name
     * @param className
     *            System object class name
     * @throws Exception
     */
    public void addSystemObject(SutTreeNode parent, String soName, String className)
            throws Exception {
   
        SutTreeNode newNode = new SutTreeNode(NodeType.MAIN_SO, soName);
        // Set the class name
        newNode.setClassName(className);
        // Add underlying nodes
        addSubTree(newNode);
        // Notify all
        insertNodeInto(newNode, parent, parent.getChildCount());
    }
   
    private void insertNodeInto(SutTreeNode newChild,
            SutTreeNode parent, int index) {
        parent.insert(newChild, index);

        modelSupport.fireChildAdded(new TreePath(getPathToRoot(parent)), index,
                newChild);
    }
    
    private void removeNodeFromParent(SutTreeNode node, boolean recover) throws Exception{
    	SutTreeNode parent = (SutTreeNode)node.getParent();
    	if(parent == null){
    		return;
    	}
    	int childIndex = parent.getIndex(node);
    	if(childIndex < 0){
    		return;
    	}
    	parent.remove(childIndex);
    	modelSupport.fireChildRemoved(new TreePath(getPathToRoot(parent)), childIndex, node);
        Class<?> systemObjectClass = LoadersManager.getInstance().getLoader().loadClass(parent.getClassName());
		ArrayList<Field> potentialFields = SystemObjectBrowserUtils.getSystemObjectField(systemObjectClass);
		Field originalFiled = null;
		// Go over all the potential fields and find once that not defined
		for (Field currentField : potentialFields) {
			if(currentField.getName().equals(node.getName())){
				originalFiled = currentField;
				break;
			}
		}
		if(originalFiled != null && recover){
			SutTreeNode newNode = null;
	    	if((node.getType() == NodeType.SUB_SO || node.getType() == NodeType.MAIN_SO)){
	    		newNode = new SutTreeNode(NodeType.EXTENTION_SO, originalFiled.getName());
	            newNode.setClassName(originalFiled.getType().getName());
	    	} else if(node.getType() == NodeType.ARRAY_SO){
				newNode = new SutTreeNode(NodeType.EXTENTION_ARRAY_SO, originalFiled.getName());
				newNode.setClassName(originalFiled.getType().getComponentType().getName());
			}
	    	if(newNode != null){
	            parent.add(newNode);
	        	modelSupport.fireChildrenAdded(new TreePath(getPathToRoot(parent)), new int[]{childIndex}, new Object[]{newNode});
	    	}
		} 

    }
    
    public SutTreeNode[] getPathToRoot(SutTreeNode aNode) {
        List<SutTreeNode> path = new ArrayList<SutTreeNode>();
        SutTreeNode node = aNode;

        while (node != root) {
            path.add(0, node);

            node = (SutTreeNode) node.getParent();
        }

        if (node == root) {
            path.add(0, node);
        }

        return path.toArray(new SutTreeNode[0]);
    }

    /**
     * Removes a system object from given parent
     * @param parent
     *             Parent to remove the system object from
     * @throws Exception
     */
    public void removeObject(SutTreeNode node, boolean recovery) throws Exception {
        // Save the parent node for notification
        TreeNode parent = node.getParent();
        // If this is an array, update all other indexes
        if (node.getType() == NodeType.ARRAY_SO) {
            int nodeIndex = 0;
            String elementName = node.getName();
            Enumeration<?> elements = parent.children();
            int arrayCount = 0;
            while (elements.hasMoreElements()) {
                SutTreeNode te = (SutTreeNode)elements.nextElement();
                if (te.getName().equals(elementName)) {
                	arrayCount++;
                }
            	
            }
            // Remove the node
            if(arrayCount == 1){
            	removeNodeFromParent(node, true & recovery);
            } else {
                removeNodeFromParent(node, false);
            }
            // Go over all children and if they are from the same name, update
            elements = parent.children();
            while (elements.hasMoreElements()) {
                SutTreeNode te = (SutTreeNode)elements.nextElement();
                if (te.getName().equals(elementName)) {
                    te.setIndex(nodeIndex);
                    nodeIndex++;
                }
            }           
        } else {
            // Remove the node
            removeNodeFromParent(node, true);
        }
    }

    /**
     * Add a sub system object
     * @param node
     *             System object node
     * @param className
     *             Class name
     * @throws Exception when the process fail
     */
    public void setSystemObject(SutTreeNode node, String className)
            throws Exception {
        // Set the class name
        node.setClassName(className);
        node.setType(NodeType.SUB_SO);
        // Add underlying nodes
        addSubTree(node);
        // Notify all
        modelSupport.firePathChanged(new TreePath(getPathToRoot(node)));
    }
   
    public void refresh(){
//        setRoot(root);
    	modelSupport.fireTreeStructureChanged(new TreePath(root));
    }
    
    public List<ValidationError> getAllPropertyValidationError(){
    	return null;
    }
   
    /**
     * Add additional system object to an array of system objects
     * @param node
     *             Node that we need to take as a reference
     * @param soName
     *             Name of the system object
     * @param className
     *             Class name
     * @throws Exception
     */
    public void addArraySystemObject(SutTreeNode node, String soName, String className)
            throws Exception {
        int arrayIndex;
        SutTreeNode parent = (SutTreeNode)node.getParent();
        // Check if this node is extension node
        if (node.getType() == NodeType.EXTENTION_ARRAY_SO) {
            // This is the first element in the array
            arrayIndex = 0;
            // We don't need this node anymore
            removeObject(node, false);
        } else {
        	// find the last node of this type
        	node = getLastBrother(node);
            arrayIndex = new Integer(node.getIndex()).intValue() + 1;
//        	arrayIndex = node.getIndex() + 1;
        }       
       
        // Create a new node that will be appended
        SutTreeNode newNode = new SutTreeNode(NodeType.ARRAY_SO, soName);
        // Set the class name
        newNode.setArraySuperClassName((node.getArraySuperClassName() == null)? node.getClassName(): node.getArraySuperClassName());
        newNode.setClassName(className);
        newNode.setIndex(arrayIndex);
        // Add underlying nodes
        addSubTree(newNode);
        // Insert the node into the tree
        insertNodeInto(newNode, parent, parent.getChildCount());
    }
    
    private SutTreeNode getLastBrother(SutTreeNode node){
    	SutTreeNode parent = (SutTreeNode)node.getParent();
    	for(int index = 0; index < parent.getChildCount(); index++){
    		SutTreeNode brother = (SutTreeNode)parent.getChildAt(index);
    		if(node.getName().equals(brother.getName()) && (brother.getIndex() > node.getIndex())){
    			node = brother;
    		}
    	}
    	return node;
    }
   
    /**
     * Return a list of all the system objects implementing a specific class.
     * (all abstract will be removed).
     * @param typeClassName the class to look implementation for.
     * @return a list of implementation names.
     * @throws Exception
     */
    public ArrayList<String> getSystemObjectsOfType(String typeClassName)
            throws Exception {
        if (systemObjectList == null) {
            systemObjectList = SystemObjectBrowserUtils.getFoundSOs();
        }
       
        if (typeClassName == null) {
            return systemObjectList;
        }
       
        ArrayList<String> listOfType = new ArrayList<String>();

        // Filter the general system object list
        Class<?> type = 
        	LoadersManager.getInstance().getLoader().loadClass(typeClassName);
        for (String object : systemObjectList) {
            Class<?> currenrClass = 
            	LoadersManager.getInstance().getLoader().loadClass(object);
            if (type.isAssignableFrom(currenrClass)) {
                listOfType.add(object);
            }
        }
        return listOfType;
    }

    /**
     * Creates the sub tree from the given root
     * @param root
     * @throws Exception
     */
    private void addSubTree(SutTreeNode root) throws Exception {
        Class<?> systemObjectClass = LoadersManager.getInstance().getLoader()
                                                    .loadClass(root.getClassName());
        ArrayList<Field> potentialFields = SystemObjectBrowserUtils
                .getSystemObjectField(systemObjectClass);
       
        // Go over all the potential fields and find once that not defined
        for (Field currentField : potentialFields) {
                SutTreeNode subField;
                String soClassName = null;
                if (currentField.getType().isArray()) {
                    subField = new SutTreeNode(NodeType.EXTENTION_ARRAY_SO, currentField.getName());
                    soClassName = currentField.getType().getComponentType().getName();
                } else {
                    subField = new SutTreeNode(NodeType.EXTENTION_SO, currentField.getName());
                    soClassName = currentField.getType().getName();
                }
                // Set the name of the class
                subField.setClassName(soClassName);
                // Add the sut node to the tree
                root.add(subField);
        }
       
        // Go over all the setter and add them as an optional tag
        HashMap<String, BeanElement> map = BeanUtils.getBeanMap(systemObjectClass, false, true, BeanUtils.getBasicTypes());

        for (BeanElement currentElement : map.values()) {
        	String setter = currentElement.getSetMethod().getName();
            if (SystemObjectBrowserUtils.isSystemObjectSetMethod(setter)) {
                continue;
            }
            String setterTag = 
            	StringUtils.firstCharToLower(setter.substring(3)); // remove the set

            // Create new sut node
            SutTreeNode optionalTag =
                new SutTreeNode(NodeType.OPTIONAL_TAG, setterTag);
            // Add a 'default value' text element
            String defaultValue = SystemObjectBrowserUtils
                                    .getDefaultValueFor(systemObjectClass, setter);                   
            if (defaultValue == null) {
                defaultValue = new String("N/A");
            }
            // Configure the sut node
            optionalTag.setActualValue("");
            addBeanGroupToGenericGroup(currentElement);
            optionalTag.setBean(currentElement);
            optionalTag.setDefaultValue(defaultValue);
            optionalTag.setJavadoc(getJavadoc(builder, root.getClassName(), setter));
            // Add the sut node to the tree
            root.add(optionalTag);
        }
    }

    //
    // Some convenience methods.
    //

    protected Element getSutTag(Object node) {
        SutTreeNode sutTagNode = ((SutTreeNode) node);
       
        if (node == null) {
            return null;           
        }
       
        return sutTagNode.getElement();
    }

    protected Object[] getChildren(Object node) {
        SutTreeNode sutTagNode = ((SutTreeNode) node);
        ArrayList<Object> children = new ArrayList<Object>();
        Object[] objects = sutTagNode.getChildren();
        for(Object currentObject: objects){
        	if(currentObject instanceof SutTreeNode){
                if(((SutTreeNode)currentObject).accept(filterType, filter)){
                    children.add(currentObject);
                }
        	}
        }
        return children.toArray();
    }

    //
    // The TreeModel interface
    //

    public int getChildCount(Object node) {
        Object[] children = getChildren(node);
        return (children == null) ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
        return getChildren(node)[i];
    }

    //
    //  The TreeTableNode interface.
    //

    public int getColumnCount() {
        return cNames.length;
    }

    public String getColumnName(int column) {
        return cNames[column];
    }

    public Class<?> getColumnClass(int column) {
        return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        SutTreeNode sutNode = (SutTreeNode)node;
       
        try {
            switch (column) {
            case 0:
                return sutNode.getName();
            case 1: //'Class name' column
                return sutNode.getClassName();
            case 2: //'Default value' column
                return sutNode.getDefaultValue();
            case 3: //'Actual value' column
                return sutNode.getActualValue();
            case 4: // 'Java documentation' column               
                return sutNode.getJavadoc().replaceAll("\n", "\n, ");
            }
        }
        catch  (SecurityException se) { }
      
        return null;
    }

    public void setValueAt(Object aValue, Object node, int column)
    {   
        // If we already got here, this means that we are allowed to edit.
        // This means that we are either in sut tag field or actual value field.
        // We already know that this is either tag or optional tag
        if (getColumnName(column).equals(ACTUAL_VALUE_COLUMN_NAME)) {
            SutTreeNode sutNode = (SutTreeNode)node;
            ParameterProvider parameterProvider = sutNode.getBean().getParameterProvider();
			String stringValue = sutNode.getBean() == null || parameterProvider == null ? aValue.toString() : parameterProvider.getAsString(aValue);
            sutNode.setActualValue(stringValue);
            // Just in case this is an optional tag, set to regular one
            sutNode.setType(NodeType.TAG);
            // Notify all
            modelSupport.firePathChanged(new TreePath(getPathToRoot(sutNode)));
            setHasChanged(true);
        }
    }   
 
    @Override
    public boolean isCellEditable(Object node, int column) {
        // We allow editing only for tree nodes and actual values
        SutTreeNode sutNode = (SutTreeNode)node;
        if(!sutNode.isEditable()){
        	return false;
        }
        if (getColumnName(column).equals(SUT_TAGS_COLUMN_NAME)) {
            return true;
        } else if (getColumnName(column).equals(ACTUAL_VALUE_COLUMN_NAME)) {
            // We can change values only for
            if ((sutNode.getType() == NodeType.TAG) || (sutNode.getType() == NodeType.OPTIONAL_TAG)) {
                return true;
            }           
            return false;
        }
        return false;
    }
   
    /**
     * Creates a XML representation of this model
     * @return Document
     *         Document containing the XML representation
     */
    public void toXml() {       
        // Lets remove the first not of the document and all its children
        NodeList nodeList = originalDocument.getChildNodes();
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node currentNode = nodeList.item(index);
            if (currentNode instanceof Element) {
                if (((Element) currentNode).getTagName().equals(SUT_TAG)) {
                    originalDocument.removeChild(currentNode);
                    break;
                }
            }
        }

        // Serialize the model to the file
        toXml(root, null, originalDocument);
       
        // Append all non model elements to the document
        for (Element currentElement : nonModelElements) {
            Element newElement = originalDocument.createElement(currentElement.getNodeName());
            NodeList childNodeList = currentElement.getChildNodes();
            for (int index = 0; index < childNodeList.getLength(); index++) {
                newElement.appendChild(childNodeList.item(index));
            }
            NodeList list = originalDocument.getElementsByTagName(SUT_TAG);
            Node currentNode = list.item(0);
            currentNode.appendChild(newElement);
        }
    }
   
    private void toXml(SutTreeNode node, Element parentElement, Document doc) {
        NodeType type = node.getType();
       
        // We are interested only in specific types
        if ((type != NodeType.MAIN_SO) && (type != NodeType.SUB_SO) &&
                (type != NodeType.ARRAY_SO) && (type != NodeType.TAG) && (type != NodeType.ROOT))
        {
            return;
        }
       
        Element newElement = null;
        Element classElement = null;
       
        if (type == NodeType.ROOT) {
            // This is a special case, create the root element
            newElement = doc.createElement(SUT_TAG);
            newElement.setAttribute("validators", node.getElement().getAttribute("validators"));
        } else {
            newElement = doc.createElement(node.getName());
        }
        SutTreeNode children[] = (SutTreeNode[])node.getChildren();
       
        switch (type) {
        case ROOT:
            for (int index = 0; index < children.length; index++) {
                toXml(children[index], newElement, doc);
            }   
            break;           
        case MAIN_SO:
        case SUB_SO:
            // For all system objects we need to add a class
            classElement = doc.createElement(CLASS_TAG);
            classElement.appendChild(doc.createTextNode(node.getClassName()));
            newElement.appendChild(classElement);           
            for (int index = 0; index < children.length; index++) {
                toXml(children[index], newElement, doc);
            }   
            break;
        case ARRAY_SO:
            // This is an system object element in an array, add index attribute
            newElement.setAttribute("index", Integer.toString(node.getIndex()));
            // For all system objects we need to add a class
            classElement = doc.createElement(CLASS_TAG);
            classElement.appendChild(doc.createTextNode(node.getClassName()));
            newElement.appendChild(classElement);           
            // Now do the same for all children
            for (int index = 0; index < children.length; index++) {
                toXml(children[index], newElement, doc);
            }   
            break;
        case TAG:
            newElement.appendChild(doc.createTextNode(node.getActualValue()));
            break;
        }
       
        if (type == NodeType.ROOT) {
            // This is a special case of a first child;
            // connect directly to the document
            doc.appendChild(newElement);
        } else {
            // Connect the new nodes to the parent element
            parentElement.appendChild(newElement);           
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }   
    public void setHasChanged(boolean hasChanged) {
        this.hasChanged = hasChanged;
    }   
   
    public boolean getHasChanged() {
        return hasChanged;
    }
   
    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
    	return ((SutTreeNode)parent).getIndex((SutTreeNode)child);
    }

	@Override
	public CellEditorType getEditorType(JTable table, int row, int column) {
		SutTreeNode node = getTreeNodeForRow(row, table);
		if(node.getBean() != null){
			return BeanUtils.getBeanType(node.getBean());
		}
		return CellEditorType.STRING;
	}

	@Override
	public String getLastValidationMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Document getDocument(){
		return originalDocument;
	}

	@Override
	public String[] getOptions(JTable table, int row, int column) {
		SutTreeNode node = getTreeNodeForRow(row, table);
		if(node.getBean() != null){
			return node.getBean().getOptions();
		}
		return null;
	}
	
	@Override
	public ParameterProvider getProvider(JTable table, int row, int column) {
		SutTreeNode node = getTreeNodeForRow(row, table);
		if (node.getBean() != null) {
			return node.getBean().getParameterProvider();
		}
		return null;
	}
	
	@Override
	public Class<?> getCellType(JTable table, int row, int column) {
		SutTreeNode node = getTreeNodeForRow(row, table);
		if (node.getBean() != null) {
			return node.getBean().getType();
		}
		return null;
	}

	@Override
	public boolean isValidData(JTable table, int row, int column,
			Object enteredValue) {
		return true;
	}
	private static SutTreeNode getTreeNodeForRow(int row, JTable table){
		return (SutTreeNode)((JXTreeTable)table).getPathForRow(row).getLastPathComponent();
	}


	public void moveUp(SutTreeNode selectedNode) {
		SutTreeNode parent = (SutTreeNode)selectedNode.getParent();
		SutTreeNode upperNode = (SutTreeNode)parent.getChildBefore(selectedNode);
		int currentIndex = parent.getIndex(selectedNode);
		parent.remove(currentIndex);
		TreePath parentPath = new TreePath(parent.getPath());
		if(selectedNode.getType() == NodeType.ARRAY_SO){
			int index = selectedNode.getIndex();
			selectedNode.setIndex(upperNode.getIndex());
			upperNode.setIndex(index);
		}
		modelSupport.fireChildRemoved(parentPath, currentIndex, selectedNode);
		parent.insert(selectedNode, currentIndex - 1);
		modelSupport.fireChildAdded(parentPath, currentIndex - 1, selectedNode);
	}


	public void moveDown(SutTreeNode selectedNode) {
		SutTreeNode parent = (SutTreeNode)selectedNode.getParent();
		SutTreeNode downNode = (SutTreeNode)parent.getChildAfter(selectedNode);
		int currentIndex = parent.getIndex(selectedNode);
		parent.remove(currentIndex);
		TreePath parentPath = new TreePath(parent.getPath());
		if(selectedNode.getType() == NodeType.ARRAY_SO){
			int index = selectedNode.getIndex();
			selectedNode.setIndex(downNode.getIndex());
			downNode.setIndex(index);
		}
		modelSupport.fireChildRemoved(parentPath, currentIndex, selectedNode);
		parent.insert(selectedNode, currentIndex + 1);
		modelSupport.fireChildAdded(parentPath, currentIndex + 1, selectedNode);
	}


	public boolean canMoveUp(SutTreeNode node) {
		if(node.getParent() == null){
			return false;
		}
		SutTreeNode before = (SutTreeNode)((SutTreeNode)node.getParent()).getChildBefore(node);
		if(before == null || before.getType() != node.getType()){
			return false;
		}
		return true;
	}
	public boolean canMoveDown(SutTreeNode node) {
		if(node.getParent() == null){
			return false;
		}
		SutTreeNode after = (SutTreeNode)((SutTreeNode)node.getParent()).getChildAfter(node);
		if(after == null || after.getType() != node.getType()){
			return false;
		}
		return true;
	}
}