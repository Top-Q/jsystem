/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * @author gderazon
 */
public class CssUtils {
	
	public enum CssType{
		TEST_INFO_TABLE("test_info_table"),
		PARAMETERS("parameters"),
		CLASS_DOCUMENTATION("class_doc"),
		TEST_DOCUMENTATION("test_doc"),
		USER_DOCUMENTATION("user_doc"),
		TIME_STAMPS("time_stamp"),
		BREAD_CRUMBS("test_breadcrumbs"),
		;
		
		String css;
		
		private CssType(String css){
			this.css = css;
		}
		
		public String getCssStart(){
			return "<span class=\"" + css + "\">";
		}
		
		public static String getCssClosingTag(){
			return "</span>";
		}
	}
	
	/**
	 * Creates HTML css entry.
	 */
	public static String cssPropertyToHtmlHeader(boolean isFolderHtml) {
		String path = JSystemProperties.getInstance().getPreferenceOrDefault(FrameworkOptions.HTML_CSS_PATH);
		String[] pathSplit = StringUtils.split(path,";");
		StringBuffer buffer = new StringBuffer();
		for (String cssPath:pathSplit){
			String relativePath = FileUtils.getFileNameWithoutFullPath(cssPath);
			if (isFolderHtml){
				relativePath = "../"+relativePath;
			}
			String link = "<LINK REL=\"stylesheet\" HREF=\""+ relativePath+"\">\n";
			buffer.append(link);
		}
		return buffer.toString();
	}	
}
