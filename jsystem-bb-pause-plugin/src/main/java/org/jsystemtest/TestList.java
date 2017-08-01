package org.jsystemtest;

import java.util.ArrayList;
import java.util.Iterator;

import jsystem.framework.scenario.JTest;

public class TestList extends ArrayList<JTest> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public boolean contains(Object o){
		if (!(o instanceof JTest)){
			return false;
		}
		JTest test = (JTest)o;
		for (int i = 0 ; i < this.size() ; i++){
			if (test.getFullUUID().equals(this.get(i).getFullUUID())){
				return true;
			}
			
		}
		return false;
	}
	
	public boolean remove(Object o){
		if (!(o instanceof JTest)){
			return false;
		}
		JTest test = (JTest)o;
		Iterator<JTest> i = this.iterator();
		while (i.hasNext()){
			if (i.next().getFullUUID().equals(test.getFullUUID())){
				i.remove();
				return true;
			}
		}
		return false;
	}
	
	
}
