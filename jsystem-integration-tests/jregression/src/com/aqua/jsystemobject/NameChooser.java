package com.aqua.jsystemobject;

import java.awt.Component;

import org.netbeans.jemmy.ComponentChooser;

public class NameChooser implements ComponentChooser{
	private String name;
	
	public NameChooser(String name){
		this.name = name;
	}
	public boolean checkComponent(Component comp) {
		return name.equals(comp.getName());
	}

	public String getDescription() {
		return "Name chooser - " + name;
	}

}
