/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.tree;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;
import java.util.Vector;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.sut.SutFactory;
import jsystem.treeui.suteditor.planner.SutTreeNode;
import jsystem.treeui.suteditor.planner.SutTreeNode.NodeType;
import jsystem.utils.XmlUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RootNode extends AssetNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<File> fPathItems;

	public RootNode() throws Exception {
		super(null, null);
		scanPath(JSystemProperties.getCurrentTestsPath());

		if("false".equals(JSystemProperties.getInstance().getPreference(FrameworkOptions.FILTER_SUT_IN_ASSETS_TREE))){
	        SutTreeNode root = new SutTreeNode(NodeType.ROOT, "");
	        Element sutTag = null;
	        Document doc = SutFactory.getInstance().getSutInstance().getDocument();
	        // If the sut tag is not found (probably a new doc), create it
	        if (XmlUtils.isSubTagExist(doc, "sut")) {
	            // We have a sut tag. Find it and set it for the root sut node.
	            NodeList nodeList = doc.getChildNodes();
	            for (int i = 0; i < nodeList.getLength(); i++) {
	                Node n = nodeList.item(i);
	                if (n instanceof Element) {
	                    if (((Element) n).getTagName().equals("sut")) {
	                        sutTag = (Element) n;
	                        break;
	                    }
	                }
	            }
	            // Set the root element
	            root.setElement(sutTag);
	        }

	       
	        // Create a full model of all system objects
	        root = createModel(root, doc);
	        add(root);
		}
		
		initChildren(addJarsFromLibFolderToArray(fPathItems.toArray()));
		markChildrenAsClassPath();
	}

	/**
	 * 
	 * @param array
	 * @return
	 * @author Itai Agmon
	 */
	private Object[] addJarsFromLibFolderToArray(Object[] array) {
		String libFolderName = JSystemProperties.getTestsLibFolder();
		if (libFolderName == null) {
			return array;
		}
		File libFolder = new File(libFolderName);
		if (!libFolder.isDirectory()){
			return array;
		}
		final String soPrefix = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.TESTS_JAR_NAME_PREFIX);
		if (null == soPrefix) {
			return array;
		}
		File[] jarFiles = libFolder.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(soPrefix) && name.endsWith(".jar")){
					return true;
				}
				return false;
			}
			
		});
		if (jarFiles == null || jarFiles.length == 0){
			return array;
		}
		
		Object[] newArr = new Object[array.length + jarFiles.length];
		System.arraycopy(array, 0, newArr, 0, array.length);
		System.arraycopy(jarFiles, 0, newArr, array.length, jarFiles.length);
		return newArr;
	}

	private void scanPath(String classPath) {
		String separator = System.getProperty("path.separator");
		fPathItems = new Vector<File>(10);
		StringTokenizer st = new StringTokenizer(classPath, separator);

		while (st.hasMoreTokens()) {
			File f = new File(st.nextToken());

			if (f.exists()) {
				fPathItems.addElement(f);
			}
		}
	}

	private void markChildrenAsClassPath() {
		for (int i = 0; i < children.size(); i++) {
			((AssetNode) children.get(i)).setClassPath(true);
		}
	}

}
