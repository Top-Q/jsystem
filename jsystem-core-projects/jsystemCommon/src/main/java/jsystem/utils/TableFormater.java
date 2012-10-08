/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.util.ArrayList;

import jsystem.utils.StringUtils;

/**
 * The <code>TableFormater</code> enable you to generate a text table
 * and then use it in the analyze process.
 * Following is a code example:
 * TableFormater tableFormater = new TableFormater();
 * tableFormater.setHeader(new String[]{"name", "column1", "column2"});
 * tableFormater.addRow(new String[]{"row1", "1", "2"});
 * tableFormater.addRow(new String[]{"row2", "3", "4"});
 * 
 * Using the <code>toString</code> method will result with the following 
 * output:
 * 
 * name   column1  column2
 * -----  -------- --------
 * row1   1        2
 * row2   3        4
 * 
 * @author guy.arieli
 *
 */
public class TableFormater {
	private ArrayList<ArrayList<String>> table = null;

	private int[] columnMaxSize = null;

	public TableFormater(String[] headers) {
		table = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		for (int i = 0; i < headers.length; i++) {
			header.add(headers[i]);
		}
		table.add(header);
	}

	public TableFormater() {
		table = new ArrayList<ArrayList<String>>();
	}

	public void setHeader(ArrayList<String> header) {
		table.add(0, header);
	}

	public void setHeader(String[] array) {
		ArrayList<String> v = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			v.add(array[i]);
		}
		setHeader(v);
	}

	public void log(String[] fields) {
		ArrayList<String> tmp = new ArrayList<String>();
		for (int i = 0; i < fields.length; i++) {
			tmp.add(fields[i]);
		}
		table.add(tmp);
	}

	public void log(ArrayList<String> fields) {
		table.add(fields);
	}

	private String getChars(int spaceCount, String s) {
		StringBuffer b = new StringBuffer(spaceCount);
		for (int i = 0; i < spaceCount; i++) {
			b.append(s);
		}
		return b.toString();
	}

	private void initColumnMaxSizes() {
		columnMaxSize = new int[(table.get(0)).size()];
		for (int i = 0; i < columnMaxSize.length; i++) {
			columnMaxSize[i] = 0;
		}
		for (int i = 0; i < table.size(); i++) {
			ArrayList<String> row = table.get(i);
			for (int j = 0; j < row.size(); j++) {
				int fieldSize = ((String) row.get(j)).length();
				if (fieldSize > columnMaxSize[j]) {
					columnMaxSize[j] = fieldSize;
				}
			}
		}
	}

	public String toString() {
		initColumnMaxSizes();
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < table.size(); i++) {
			ArrayList<String> row =  table.get(i);
			if (i == 1) {
				for (int j = 0; j < row.size(); j++) {
					buff.append(getChars(columnMaxSize[j] + 1, "-") + " ");
				}
				buff.append("\n");
			}
			for (int j = 0; j < row.size(); j++) {
				String addTmp = row.get(j).toString();
				buff.append(addTmp + getChars(columnMaxSize[j] + 2 - addTmp.length(), " "));
			}
			buff.append("\n");
		}
		return buff.toString();
	}
	public String toHtml(){
		initColumnMaxSizes();
		StringBuffer buff = new StringBuffer();
		buff.append("<table  border=\"1\">\n");
		for (int i = 0; i < table.size(); i++) {
			buff.append("<tr>\n");
			ArrayList<String> row =  table.get(i);
			for (int j = 0; j < row.size(); j++) {
				buff.append("<td>");
				buff.append(StringUtils.toHtmlString(row.get(j).toString()));
				buff.append("</td>\n");
			}
			buff.append("</tr>\n");
		}
		buff.append("</table>\n");
		return buff.toString();
	}

}
