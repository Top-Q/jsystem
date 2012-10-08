/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.runner.ErrorLevel;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.TestRunner;
import jsystem.treeui.error.ErrorPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.lang.reflect.Array;

public class ParameterTableUserDefine extends JComponent implements ActionListener {

	private static final long serialVersionUID = -5292827534955851045L;

	public static String USER_DEFINED_EDITOR_NAME = "USER_DEFINED_EDITOR_BUTTON";

	private JTextField field;
	private JButton openUserDefineUI;
	private ParameterProvider provider;
	private Class<?> clazz;
	private Parameter parameter;

	public ParameterTableUserDefine(ParameterProvider provider, Class<?> clazz, Parameter parameter) throws Exception {
		super();
		this.provider = provider;
		this.parameter = parameter;
		Object currentObject = parameter.getValue();
		if (currentObject != null) {
			this.clazz = currentObject.getClass();
		}
		if (clazz != null) {
			this.clazz = clazz;
		}
		if (currentObject != null && (!(clazz.isAssignableFrom(currentObject.getClass())))) {
			currentObject = provider.getFromString(currentObject.toString());
		}
		setLayout(new BorderLayout());
		field = new JTextField();
		Dimension dim = field.getSize();
		dim.width = 90;
		field.setSize(dim);
		field.setText(currentObject == null ? "" : provider.getAsString(currentObject));
		field.setEditable(provider.isFieldEditable());
		add(field, BorderLayout.CENTER);
		openUserDefineUI = new JButton("...");
		openUserDefineUI.setToolTipText("Edit parameter");
		openUserDefineUI.setName(USER_DEFINED_EDITOR_NAME);
		openUserDefineUI.addActionListener(this);
		add(openUserDefineUI, BorderLayout.EAST);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(openUserDefineUI)) {
			Object currentObject = parameter.getValue();

			if (currentObject == null) {
				try {
					if (clazz.isArray()) {
						currentObject = Array.newInstance(clazz.getComponentType(), 0);
					} else {
						currentObject = LoadersManager.getInstance().getLoader().loadClass(clazz.getName())
								.newInstance();
					}
				} catch (Exception e1) {
					throw new RuntimeException("Failed creating instance of user defined bean", e1);
				}
			}
			Object changedObject;
			try {
				changedObject = provider.showUI(TestRunner.treeView, null, null, clazz, currentObject, parameter);
				// Applied Materials - if the parameter is not editable there is
				// no reason to set Dirty the scenario
				// the same apply if the user did not made any changes to the
				// object
				if ((!parameter.isEditable()) || (changedObject.equals(currentObject))) {
					return;
				}

			} catch (Exception e1) {
				ErrorPanel.showErrorDialog("Fail to process parameter from user defined parameter provider", e1,
						ErrorLevel.Warning);
				return;
			}
			if (currentObject != null) {
				field.requestFocusInWindow();
				String value = provider.getAsString(changedObject);
				field.setText(value);
				setField(value);
				TestRunner.treeView.testInformation.parameterChanged(false);
			}
		}
	}

	public synchronized void addFocusListener(FocusListener l) {
		field.addFocusListener(l);
	}

	public Object getField() throws Exception {
		return provider.getFromString(field.getText());
	}

	/**
	 * @return The parameter string presentation
	 */
	public String getFieldAsString() {
		try {
			return provider.getAsString(provider.getFromString(field.getText()));
		} catch (Exception e) {
			return null;
		}
	}

	public void setField(String fieldValue) {
		this.field.setText(fieldValue);
	}

	public void repaint() {
		field.repaint();
	}
}
