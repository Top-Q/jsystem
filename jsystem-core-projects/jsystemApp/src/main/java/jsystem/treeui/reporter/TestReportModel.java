/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.reporter;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import jsystem.framework.report.Reporter;

/**
 * TestReportModel This class implements the table model
 */
public class TestReportModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] ColumnList = { "Commands", "Status" };

	private final int ALL = 0;

	private final int NEW = 1;

	private final int SAME = 2;

	private final int MAX_DEVICE_TABLE_ROW = 10000;

	private final int MAX_DEVICE_TABLE_COLUMN = ColumnList.length;

	private final int MAX_ALLOWED_CACHE = 2000;

	private final int FIRST = 0;

	private final int NONE = 0;

	private final int OK = 1;

	private final int FAIL = -1;
	
	private final int WARNING = 2;

	private final int COMMAND = 0;

	private final int STATUS = 1;

	// Data
	private ArrayList<TestReportCommand> allDataList;

	public TestReportModel() {
		allDataList = new ArrayList<TestReportCommand>();
	}

	public String getColumnName(int column) {

		if (column > MAX_DEVICE_TABLE_COLUMN) {
			return null;
		}

		return ColumnList[column];
	}

	public Object getValueAt(int iRowIndex, int iColumnIndex) {

		if (iRowIndex >= allDataList.size()) {
			return null;
		}

		TestReportCommand command = (TestReportCommand) allDataList.get(iRowIndex);

		switch (iColumnIndex) {
		case COMMAND:
			return command.command;
		case STATUS:

			switch (command.status) {
			case NONE:
				return "";
			case OK:
				return "Pass";
			case FAIL:
				return "Fail";
			case WARNING:
				return "Warning";
				 
			    
			default:
				return "";
			}
		default:
			return null;
		}
	}

	public void setValueAt(Object aValue, int iRowIndex, int iColumnIndex) {

		if (iRowIndex > MAX_DEVICE_TABLE_ROW) {
			return;
		}

		if (allDataList == null) {
			return;
		}

		if (iRowIndex >= allDataList.size()) {
			return;
		}

		switch (iColumnIndex) {
		case COMMAND:
			return;
		case STATUS:
			return;
		default:
			return;
		}
	}

	public int getColumnCount() {

		return MAX_DEVICE_TABLE_COLUMN;
	}

	public int getRowCount() {

		if (allDataList == null) {
			return 0;
		}

		return allDataList.size();
	}

	public boolean isCellEditable(int row, int column) {
		return false;
	}

	private void handleDataChange(int type) {

		switch (type) {

		case ALL:
			fireTableDataChanged();
			break;
		case NEW:
			fireTableRowsInserted(allDataList.size() - 1, allDataList.size() - 1);
			break;
		case SAME:
			fireTableCellUpdated(STATUS, allDataList.size() - 1);
		}
	}

	public void clearModel() {

		allDataList.clear();
		handleDataChange(ALL);
	}

	public int addCommand(String command) {

		TestReportCommand newCommand = new TestReportCommand(command, NONE);

		if (allDataList.size() == MAX_ALLOWED_CACHE) {
			int index = 0;

			while (index < (MAX_ALLOWED_CACHE / 2)) {
				allDataList.remove(FIRST);
				index++;
			}
		}

		allDataList.add(newCommand);

		handleDataChange(NEW);

		return (allDataList.size() - 1);
	}

	/**
	 * 
	 * @param index
	 * @param status - the status is passed on from the <I>Reporter</I> class
	 * @param bold
	 */
	public void addStatus(int index, int status, boolean bold) {

		TestReportCommand command = (TestReportCommand) allDataList.get(index);
		if (command != null) {
			command.bold = bold;

			if (status == Reporter.PASS) {
				command.status = OK;
			} else if (status == Reporter.WARNING){
				command.status = WARNING;
			} else{
				command.status = FAIL ;
			}
			
			handleDataChange(SAME);
		}
	}

	public TestReportCommand getCommandAt(int index) {

		if (index >= allDataList.size()) {
			return null;
		}

		return (TestReportCommand) allDataList.get(index);
	}
}
