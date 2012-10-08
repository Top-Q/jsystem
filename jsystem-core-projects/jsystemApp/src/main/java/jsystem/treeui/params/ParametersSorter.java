/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.params;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.PresentationDefinitions;

/**
 * a sorting parameters class. sorts both sections and headers holds a sub class
 * "ParametersCompare" which compares the Parameters for sorting.
 * 
 * @author Nizan Freedman
 * 
 */

public class ParametersSorter {
	private Parameter[] parameters;

	public ParametersSorter(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public Parameter[] getParameters(){
		return parameters;
	}
	/**
	 * sorts the initialized parameters according to given sorting parameters
	 * 
	 * @param sortSection
	 *            section sorting preferance
	 * @param sortHeader
	 *            header sorting
	 * @param ascending
	 *            Ascending/Descending
	 * @param order
	 *            A user defined String Order
	 * @return the new parameters sorted
	 */
	public Parameter[] sortParameters(int sortSection, int sortHeader, PresentationDefinitions.ParametersOrder parametersOrder, String order) {
				
		ParameterCompare.order = parametersOrder;
		TreeMap<ParameterCompare, Parameter> map = new TreeMap<ParameterCompare, Parameter>(new ParameterCompare());
		// build TreeMap
		String name;
		String sectionName;
		String headerName;
		int sectionNumber;
		double headerNumber;
		ParameterCompare pc;
		for (int i = 0; i < parameters.length; i++) {
			sectionName = "";
			headerName = "";
			sectionNumber = 0;
			// making sure strings that are not numbers will be displayed after
			// numbers
			headerNumber = Double.MAX_VALUE;

			if (sortSection == ParametersPanel.SORT_BY_SECTION_STRING) {
				sectionNumber = order.toLowerCase().indexOf(parameters[i].getSection().toLowerCase());
				if (sectionNumber == -1){
					sectionNumber = order.length();
				}
				sectionName = parameters[i].getSection();
			} else if (sortSection == ParametersPanel.SORT_BY_SECTION_AB) {
				sectionName = parameters[i].getSection();
			}

			if (sortHeader == ParametersPanel.SORT_BY_HEADER_NAME) {
				headerName = parameters[i].getName();
			} else if (sortHeader == ParametersPanel.SORT_BY_HEADER_DESCRIPTION) {
				headerName = parameters[i].getDescription();
			} else if (sortHeader == ParametersPanel.SORT_BY_HEADER_TYPE) {
				headerName = parameters[i].getParamTypeString();
			} else if (sortHeader == ParametersPanel.SORT_BY_HEADER_VALUE) {
				Object o = parameters[i].getValue();
				if (o != null) {
					headerName = o.toString();
					try {
						headerNumber = Double.parseDouble(headerName);
						headerName = "";
					} catch (NumberFormatException e) {
						// no exception here needed to be handled here - just
						// testing if has a double value
					}
				}
			}

			// for distinguishing double entries of same value/description/type
			name = headerName + parameters[i].getName();
			int indexOf = indexOf(parameters,parameters[i]);
			pc = new ParameterCompare(sectionNumber, headerNumber, sectionName, name,indexOf);
			map.put(pc, parameters[i]);
		}

		ArrayList<Parameter> params = new ArrayList<Parameter>();
		// sort ascending/descending
		while (!map.isEmpty()) {
			pc = map.firstKey();
			params.add(map.get(pc));
			map.remove(pc);
		}
		Parameter[] tmpParam = params.toArray(new Parameter[0]);
		return tmpParam;
	}
	
	private static int indexOf(Parameter[] params,Object o){
		for (int i=0;i<params.length;i++){
			if (o==params[i]){
				return i;
			}
		}
		return -1;
	}

}

/**
 * a class for sorting parameters according to section name/header
 * 
 * @author Nizan Freedman
 * 
 */
class ParameterCompare implements Comparator<Object> {
	int sectionNum;

	double headerNum;

	String sectionName;

	String name;
	
	int index;
	static PresentationDefinitions.ParametersOrder order;

	public ParameterCompare() {

	}

	public ParameterCompare(int sectionNum, double headerNum, String sectionName, String name,int index) {
		this.sectionNum = sectionNum;
		this.sectionName = sectionName.toLowerCase();
		this.headerNum = headerNum;
		this.name = name.toLowerCase();
		this.index = index;
	}

	/**
	 * first sort by section number , then by section name and last by header
	 * num
	 */
	public int compare(Object o1, Object o2) {
		ParameterCompare p1 = (ParameterCompare) o1;
		ParameterCompare p2 = (ParameterCompare) o2;
		if (p1.sectionNum != p2.sectionNum){
			return ((Integer) p1.sectionNum).compareTo((Integer) p2.sectionNum);
		}
		if (!p1.sectionName.equals(p2.sectionName)){ // different section
//			if (p1.sectionName.toLowerCase().equals("general")){
//				return -1;
//			}else if(p2.sectionName.toLowerCase().equals("general")){
//				return 1;
//			}
			return p1.sectionName.compareTo(p2.sectionName);
		}
		if (order == PresentationDefinitions.ParametersOrder.defaultOrder) {
			if (p1.headerNum == p2.headerNum){
				return p1.index-p2.index;
			}
			return ((Double) p1.headerNum).compareTo((Double) p2.headerNum);
		}
		if (order == PresentationDefinitions.ParametersOrder.ascending) {
			if (p1.headerNum == p2.headerNum)
				return p1.name.compareTo(p2.name);
			return ((Double) p1.headerNum).compareTo((Double) p2.headerNum);
		} else {
			if (p1.headerNum == p2.headerNum)
				return p2.name.compareTo(p1.name);
			return ((Double) p2.headerNum).compareTo((Double) p1.headerNum);

		}
	}	
}
