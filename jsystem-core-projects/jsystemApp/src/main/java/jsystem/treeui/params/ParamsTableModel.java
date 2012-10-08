/*
 * Created on Dec 15, 2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import jsystem.framework.scenario.Parameter;

/**
 * @author guy.arieli
 * 
 * the model for the ParametersPanel JTable. implements MouseListener in order
 * to notify of changed Header sorting (Listens on tableHeader)
 */

public class ParamsTableModel extends DefaultTableModel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] ColumnList = { "Name", "Description", "Type", "Value" };

	// Data
	private ArrayList<Parameter> allDataList;

	private JTableHeader tableHeader;

	private ParametersPanel ppanel;
	//APPLIED - this method had to go public for the JRegression
	public ParametersPanel getParameterPanel() {
		return ppanel;
	}

	public ParamsTableModel(ParametersPanel ppanel) {
		allDataList = new ArrayList<Parameter>();
		this.ppanel = ppanel;

	}

	public String getColumnName(int column) {
		if (column >= ColumnList.length) {
			return null;
		}

		return ColumnList[column];
	}

	public ArrayList<Parameter> getParams() {
		return allDataList;
	}

	public Object getValueAt(int iRowIndex, int iColumnIndex) {

		if (iRowIndex >= allDataList.size()) {
			return null;
		}

		Parameter param = (Parameter) allDataList.get(iRowIndex);
		/*
		 * Check if the parameter changed If it's change will add '*' to the
		 * parameter name
		 */
		String changed = "";
		if (param.isChanged()) {
			changed = "*";
		}

		switch (iColumnIndex) {
		case ParamsTableRenderer.COLUMN_NAME:
			return param.getName() + changed;
		case ParamsTableRenderer.COLUMN_DESCRIPTION:
			return param.getDescription();
		case ParamsTableRenderer.COLUMN_TYPE:
			return param.getParamTypeString();
		case ParamsTableRenderer.COLUMN_VALUE:
			if(param.getProvider()!= null){
				return param.getProvider().getAsString(param.getValue());
			}
			return param.getValue();
		default:
			return null;
		}
	}

	public Parameter getParam(int rowIndex) {
		return (Parameter) allDataList.get(rowIndex);
	}

	public void setValueAt(Object aValue, int iRowIndex, int iColumnIndex) {
		//((Parameter) allDataList.get(iRowIndex)).setValue(aValue);
	}

	public int getColumnCount() {

		return ColumnList.length;
	}

	public int getRowCount() {

		if (allDataList == null) {
			return 0;
		}

		return allDataList.size();
	}

	public boolean isCellEditable(int row, int column) {

		if (column == ParamsTableRenderer.COLUMN_VALUE) {
			return true;
		}

		return false;
	}

	public void clearModel() {

		allDataList.clear();
		fireTableDataChanged();
	}

	public int addParameter(Parameter param) {
		allDataList.add(param);

		fireTableDataChanged();
		return (allDataList.size() - 1);
	}

	/**
	 * get the listned tableHeader
	 * 
	 * @return table header
	 */
	public JTableHeader getTableHeader() {
		return tableHeader;
	}

	/**
	 * set tableHeader for listening
	 * 
	 * @param tableHeader
	 */
	public void setTableHeader(JTableHeader tableHeader) {
		this.tableHeader = tableHeader;
		tableHeader.addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		int space = 3;
		JTableHeader h = (JTableHeader) e.getSource();
		TableColumnModel columnModel = h.getColumnModel();
		int viewColumn = columnModel.getColumnIndexAtX(e.getX());
		int viewColumn2 = columnModel.getColumnIndexAtX(e.getX() + space);
		int viewColumn3 = columnModel.getColumnIndexAtX(e.getX() - space);
		// to enable resizing - checking if the mouse is in-between headers
		if ((viewColumn == viewColumn2) && (viewColumn == viewColumn3) && viewColumn > -1) {
			int column = columnModel.getColumn(viewColumn).getModelIndex();

			if (column != -1) {
				ppanel.headerChanged(column);
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
