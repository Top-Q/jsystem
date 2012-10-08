package com.aqua.utils;

import java.awt.Component;

import javax.swing.JComponent;

import org.netbeans.jemmy.ComponentChooser;

/**
 * 
 * @author Itai.Agmon
 * 
 */
public class ToolTipChooser implements ComponentChooser {

	final private String requiredToolTipText;

	public ToolTipChooser(String requiredToolTipText) {
		super();
		this.requiredToolTipText = requiredToolTipText;
	}

	@Override
	public boolean checkComponent(Component aComponent) {
		if (null == requiredToolTipText) {
			return false;
		}
		if (!(aComponent instanceof JComponent)) {
			return false;
		}
		final String compToolTip = ((JComponent) aComponent).getToolTipText();
		if (null == compToolTip) {
			return false;
		}
		if (compToolTip.equals(requiredToolTipText)) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Choose component by type and tool tip text";
	}

}
