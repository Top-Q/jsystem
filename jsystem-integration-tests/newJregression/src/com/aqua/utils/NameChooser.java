package com.aqua.utils;

import java.awt.Component;

import org.netbeans.jemmy.ComponentChooser;

/**
 * 
 * Chooser for selecting component by the component name.
 * 
 * @author Itai.Agmon
 * 
 */
public class NameChooser implements ComponentChooser {

	private final String requiredName;

	public NameChooser(final String requiredName) {
		super();
		this.requiredName = requiredName;
	}

	@Override
	public boolean checkComponent(Component component) {
		if (component.getName() == null || requiredName == null) {
			return false;
		}
		if (component.getName().equals(requiredName)) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Search component by component name";
	}

}
