/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.html.summary;

import jsystem.utils.SortedProperties;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;

public class ContainerSummaryReport implements Serializable {

	private static final long serialVersionUID = 7918153298227515502L;
	private File file;
	private String name;
	private Properties props = new Properties();
			
	/**
     * @param file -
     * @param name -
     */
	public ContainerSummaryReport(File file,String name){
		this.file = file;
		this.name = name;
	}
	
	/**
	 */
	public void toFile() {
		try {
			doToFile();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
     * @throws Exception -
     */
	private void doToFile() throws Exception {
		Tag html = new Tag("html");
		Tag head = new Tag("head");
		html.add(head);
		Tag body = new Tag("body");
		body.add(new Tag("H1", null, name));
		Tag table = new Tag("table");
		table.addAttribute(new Attribute("border","1"));
		table.addAttribute(new Attribute("cellspacing","2"));
		table.addAttribute(new Attribute("cellpadding","2"));
		body.add(table);
		Properties tmp = new SortedProperties();
		tmp.putAll(props);
		Enumeration<Object> keys = tmp.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement().toString();
			String val = tmp.getProperty(key);
			Tag tableRow = new Tag("TR");
			tableRow.add(new Tag("TD", null, key));
			tableRow.add(new Tag("TD", null, val));
			table.add(tableRow);
		}
		html.add(body);
		FileWriter writer = new FileWriter(file);
		writer.write(html.toString());
		writer.flush();
		writer.close();
	}

	public String getUrl() {
		return file.getName();
	}
	
	public void setProperty(String key,String value){
		props.setProperty(key, value);
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isEmpty(){
		return props == null || props.size()==0;
	}
	
	
}
