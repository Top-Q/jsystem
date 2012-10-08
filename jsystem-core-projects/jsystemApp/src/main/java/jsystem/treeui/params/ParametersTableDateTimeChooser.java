/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JTextField;

import jsystem.treeui.DateTimeEditor;
import jsystem.utils.DateUtils;

/**
 * UI component for date chooser in params table.
 * @author goland
 */
public class ParametersTableDateTimeChooser extends BeanParameterElement{

	private static final long serialVersionUID = 1L;
	
	public static String SELECT_DATE_BUTTON_NAME = "SELECT_DATE_BUTTON_NAME";
	
	private JTextField field;

	public ParametersTableDateTimeChooser(String dateText){
		super();
		setLayout(new BorderLayout());
		field = new JTextField();
		Dimension dim = field.getSize();
		dim.width = 100;
		field.setSize(dim);
		field.setText(dateText == null ? "" : dateText);
		setDate(dateText);
		add(field,BorderLayout.CENTER);		
		JButton openFileChooser  = new JButton("...");
		openFileChooser.setName(SELECT_DATE_BUTTON_NAME);
		openFileChooser.addActionListener(this);
		add(openFileChooser,BorderLayout.EAST);	
	}
	
	public void actionPerformed(ActionEvent e) {
		DateTimeEditor editor = new DateTimeEditor(DateTimeEditor.DATETIME, DateFormat.FULL);
		try {
			Date curDate = DateUtils.parseDate(getDate());
			editor.setDate(curDate);
		}catch (ParseException ex){
			
		}
		editor.showDialog();
		field.requestFocusInWindow();
		setDate(DateUtils.getDate(editor.getDate().getTime(), new SimpleDateFormat(DateUtils.DATE_FORMATS[0])));
		parameterChanged();
	}

	public synchronized void addFocusListener(FocusListener l) {
		field.addFocusListener(l);
	}
	
	public String getDate() {
		return field.getText();
	}

	public void setDate(String selectedFile) {
		this.field.setText(selectedFile);
	}
    
	public void repaint() {
		field.repaint();
    }
}
