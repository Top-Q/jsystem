/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import java.io.IOException;
import java.util.Vector;

import jsystem.extensions.report.html.summary.ContainerSummaryReport;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.utils.FileUtils;

/**
 * 
 * Handles Scenario hierarchy html component  
 * 
 */
public class HtmlTreeTestList extends HtmlTestList {
	private static final long serialVersionUID = 1L;
	private boolean isTreeRoot = false;
	private boolean isRootScenario = false;
	
	private ContainerSummaryReport containerSummaryReport;
	
	public HtmlTreeTestList(String logDirectory, String directory) {
		super(logDirectory,directory,"tree");
	}
	
	public void doToFile(NameGenerator generator) throws IOException {
		if (isTreeRoot) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("<HEAD>\n"+
					"<LINK REL=\"stylesheet\" HREF=\""+ FileUtils.getFileNameWithoutFullPath(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.HTML_CSS_PATH))+"\">\n");
			if (isTreeRoot){
				buffer.append(
						"<SCRIPT SRC=\"mktree.js\" LANGUAGE=\"JavaScript\"></SCRIPT>\n"+
						"<LINK REL=\"stylesheet\" HREF=\"mktree.css\">\n");
			}
			buffer.append("</HEAD>\n");		
			buffer.append("<BODY class=\"" + getCssClassCanonicalValue()+ "\">");
			buffer.append(getTreeSons(null));
			buffer.append("</BODY>");
			writeBufferToFile(buffer);	
		}
		updateParents(generator);	
	}
	
	public void toFile(NameGenerator generator) throws IOException {
		if ("false".equals(JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.SAVE_REPORTERS_ON_RUN_END))){
			doToFile(generator);
		}
	}
					
	public String getTreeSons(Vector<String> rootTrees){
		StringBuffer buffer = new StringBuffer();
		
		if (rootTrees == null){
			rootTrees = new Vector<String>();
		}
		
		if (isTreeRoot){
			buffer.append("<A href=\"#\" onClick=\"expandAll(); return false;\">Expand All</A>&nbsp;&nbsp;&nbsp\n");
			buffer.append("<A href=\"#\" onClick=\"collapseAll(); return false;\">Collapse All</A>&nbsp;&nbsp;&nbsp\n<br><br>\n");
			for (Report list : reports) {
				if (list instanceof HtmlTreeTestList){
					buffer.append(((HtmlTreeTestList)list).getTreeSons(rootTrees));
				}else {
					buffer.append(list.toString());
				}
			}
			String toReturn = buffer.toString();
			
			toReturn = toReturn.replace("expandAll();", createTreeList(rootTrees, true));
			toReturn = toReturn.replace("collapseAll();", createTreeList(rootTrees, false));
			return toReturn;
		}

		if (isRootScenario){
			buffer.append("<UL CLASS=\"mktree\" id=\""+getTitle()+"\">\n");
			rootTrees.add(getTitle());
		}
		buffer.append("<li>");
		if (isRootScenario){
			buffer.append("<img src=\"scenario.gif\" onClick=\"expandOrCollapse(this,'"+getTitle()+"','Scenario'); return false;\" title=\"Expand Scenario\"/>");
		}
		
		boolean addContainerSummaryReport = getContainerSummaryReport() != null && !getContainerSummaryReport().isEmpty();
		
		if (addContainerSummaryReport){
			getContainerSummaryReport().toFile();
		}
		
		if (reports.size() > 0){
			buffer.append("<span class=\""+getCssClassCanonicalValue()+"\">");
			if (addContainerSummaryReport){
				buffer.append("<a target=\"testFrame\" href=\""+getContainerSummaryReport().getUrl()+"\">");
			}
			buffer.append("&nbsp;"+getTitle()+"\n");
			buffer.append("</a>");
			buffer.append("</span>\n");
			buffer.append("<ul>\n");
			for (Report list : reports) {
				if (list instanceof HtmlTreeTestList){
					buffer.append(((HtmlTreeTestList)list).getTreeSons(rootTrees));
				}else {
					buffer.append(list.toString());
				}
			}
			buffer.append("</ul>\n");
		}else{
			buffer.append(super.toString());
		}
		buffer.append("</li>\n");
		if (isRootScenario){
			buffer.append("</ul>\n");
		}
		
		return buffer.toString();
		
	}
	private String createTreeList(Vector<String> trees,boolean expand){
		StringBuffer buffer = new StringBuffer();
		String toAdd = expand? "expandTree" : "collapseTree";
		for (int i=0 ; i<trees.size() ; i++){
			buffer.append(toAdd + "('" + trees.get(i) + "');");
		}
		return buffer.toString();
	}
	/**
	 * add a son for the scenarios tree and signal all parents that status has changed (for update)
	 * @param report
	 */
	public void addReport(Report report) {
		reports.add(report);
		report.addParent(this);
	}
	public void setTreeRoot(boolean isTreeRoot) {
		this.isTreeRoot = isTreeRoot;
	}
	public void setRootScenario(boolean isRootScenario) {
		this.isRootScenario = isRootScenario;
	}

	public ContainerSummaryReport getContainerSummaryReport() {
		return containerSummaryReport;
	}

	public void setContainerSummaryReport(
			ContainerSummaryReport containerSummaryReport) {
		this.containerSummaryReport = containerSummaryReport;
	}
}