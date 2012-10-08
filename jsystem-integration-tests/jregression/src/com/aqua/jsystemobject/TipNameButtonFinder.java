package com.aqua.jsystemobject;

import java.awt.Component;

import javax.swing.JButton;

import org.netbeans.jemmy.operators.JButtonOperator.JButtonFinder;

public class TipNameButtonFinder extends JButtonFinder{
	String toFind;
	public TipNameButtonFinder(String toFind){
		this.toFind = toFind;
		System.out.println(">>>DEBUG: string tofind button is: "+toFind);
	}
	@Override
	public boolean checkComponent(Component comp){
		System.out.println(">>>DEBUG: inside checkComponant of TipNameButtonFinder with toFind = "+toFind +
				" comp is instance of JButton: "+(comp instanceof JButton));
		if (comp instanceof JButton){
			String toolTipStrin = ((JButton)comp).getToolTipText();
			String name = ((JButton)comp).getText();
			if(name != null && !name.equals("")){
				if(name.indexOf(toFind) >= 0){
					return true;
				}
			}
			if(toolTipStrin != null){
				if(toolTipStrin.indexOf(toFind) >= 0){
					return true;
				}
			}
		}
		return false;
	}
	
	public String getDescription(){
		return "a button that the name or tooltip is: \'" + toFind +"\'";
	}
}