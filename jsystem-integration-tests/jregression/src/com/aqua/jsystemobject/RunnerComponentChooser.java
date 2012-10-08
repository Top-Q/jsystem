package com.aqua.jsystemobject;

import java.awt.Component;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.netbeans.jemmy.ComponentChooser;

/**
 * this class is used to locate graphical component by a given name\ with a givane class and name
 * 
 * @author nizanf
 *
 */
public class RunnerComponentChooser implements ComponentChooser {

	/**
	 * the name to search
	 */
	private String name;

	/**
	 * the component class to search
	 */
	private Class<JComponent> componentClass = null;

	/**
	 * define a component chooser with a given name
	 * 
	 * @param name the name to search
	 */
	public RunnerComponentChooser(String name){
		this.name = name;
	}
	
	/**
	 * define a component chooser with a given class and name
	 * 
	 * @param componentClass	the Swing object class to search
	 * @param name	the text to search
	 */
	public RunnerComponentChooser(Class<JComponent> componentClass,String name){
		this.name = name;
		this.componentClass = componentClass;
	}
	
	public boolean checkComponent(Component comp) {
		if (componentClass!=null && !comp.getClass().equals(componentClass)){
			return false;
		}
		
		if (comp instanceof JSpinner){
			return name.equals(((JSpinner)comp).getName());
		}
		if (comp instanceof JPanel){
			return name.equals(((JPanel)comp).getName());
		}
		if (comp instanceof AbstractButton){
			return name.equals(((AbstractButton)comp).getText()) || name.equals(((AbstractButton)comp).getToolTipText());
		}
		if (comp instanceof JLabel){
			return name.equals(((JLabel)comp).getText());
		}
		if (comp instanceof JDialog){
			return name.equals(comp.getName()) || name.equals(((JDialog)comp).getTitle());
		}
		return name.equals(comp.getName());
	}
	
	
	public String getDescription() {
		return name;
	}

}
