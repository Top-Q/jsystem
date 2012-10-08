/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JTextField;

import jsystem.extensions.paramproviders.OptionsMultiSelectDialog;
import jsystem.framework.common.CommonResources;
import jsystem.utils.StringUtils;

/**
 * UI component for multi selection String array chooser in params table.
 * @author guy.arieli
 */
public class ParametersStringArrayChooser extends BeanParameterElement{

	private static final long serialVersionUID = 1L;
	
	private JTextField field;
	
	private String[] options;
	private String[] selected;
	public ParametersStringArrayChooser(String[] options, String[] selected){
		super();
		this.options = options;
		this.selected = selected;
		setLayout(new BorderLayout());
		field = new JTextField();
		Dimension dim = field.getSize();
		dim.width = 100;
		field.setSize(dim);
		field.setEditable(false);
		field.setText(selected == null ? "" : StringUtils.objectArrayToString(CommonResources.DELIMITER, (Object[])selected));
		add(field,BorderLayout.CENTER);		
		JButton openFileChooser  = new JButton("...");
		openFileChooser.addActionListener(this);
		add(openFileChooser,BorderLayout.EAST);	
	}
	
	public void actionPerformed(ActionEvent e) {
		OptionsMultiSelectDialog dialog = new OptionsMultiSelectDialog();
		dialog.initDialog(options,selected);
		if (dialog.isOkay()){
			selected = dialog.getSelectedOptions();
			parameterChanged();
		}
		field.requestFocusInWindow();
	}

	public synchronized void addFocusListener(FocusListener l) {
		field.addFocusListener(l);
	}
	
	public String[] getSelected(){
		return selected;
	}
	
    
	public void repaint() {
		field.repaint();
    }
}
