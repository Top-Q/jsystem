/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a sorting object which holds all the JTest sorting options needed for
 * ParametersPanel reconstruction
 *  - sectionOrder - the user Defined String got from the test file method
 * "sectionOrder". defines the ordering of the sections. - sortSection - the
 * chosen section sorting - AlphaBetical/Type/User-Defined - sortHeader - the
 * chosen parameters sorting - Name/Description/Type/Value - ascending - true if
 * Ascending , false if Descending. - activeTab - the current activeSection. -
 * parameters - the JTest parameters.
 * 
 * 
 * @author Nizan Freedman
 * 
 */

public class PresentationDefinitions {
	
	public static enum ParametersOrder {
		ascending,
		descending,
		defaultOrder
	}
	
	private static final Pattern pattern = 
		Pattern.compile("sortSection:(.*?);sortHeader:(.*?);paramsOrder:(.*?);activeTab:(.*?);headersRatio:(.*?),(.*?),(.*?),(.*?)$");
	
	int sortSection;

	int sortHeader;

	ParametersOrder paramOrder = ParametersOrder.defaultOrder;

	int activeTab;
	
	double[] headersRatio; 

	public PresentationDefinitions() {
		headersRatio = new double[]{0.1,0.25,0.05,0.2};
	}

	/**
	 * save the Sorting definitions from the current ParametersPanel
	 * 
	 * @param panel
	 *            the current ParametersPanel to save
	 */
	public void saveDefinitions(int sortSection, int sortHeader, ParametersOrder paramOrder, int activeTab, double[] headerRatio) {
		this.sortSection = sortSection;
		this.sortHeader = sortHeader;
		this.paramOrder = paramOrder;
		this.activeTab = activeTab;
		this.headersRatio = new double[headerRatio.length];
		System.arraycopy(headerRatio, 0, this.headersRatio, 0, headerRatio.length);
	}
	
	/**
	 */
	public String toAntProperty(){
		String res =	"sortSection:"+sortSection+";sortHeader:"+sortHeader+";paramsOrder:"+paramOrder+";activeTab:"+activeTab;
		res+=";headersRatio:"+headersRatio[0]+","+headersRatio[1]+","+headersRatio[2]+","+headersRatio[3];
		return res;
	}
	
	/**
	 */
	public static PresentationDefinitions fromAntProperty(String propertyVal){
		Matcher match = pattern.matcher(propertyVal);
		if (!match.find()){
			return null;
		}
		PresentationDefinitions sortDef = new PresentationDefinitions();
		try {
			sortDef.sortSection = Integer.parseInt(match.group(1));
			sortDef.sortHeader = Integer.parseInt(match.group(2));
			sortDef.paramOrder = ParametersOrder.valueOf(match.group(3));
			sortDef.activeTab = Integer.parseInt(match.group(4));
			sortDef.headersRatio = new double[4];
			sortDef.headersRatio[0] =	Double.parseDouble(match.group(5));
			sortDef.headersRatio[1] =	Double.parseDouble(match.group(6));
			sortDef.headersRatio[2] =	Double.parseDouble(match.group(7));
			sortDef.headersRatio[3] =	Double.parseDouble(match.group(8));
			return sortDef;
		}catch (Exception e){
			return null;
			//ingnore error
		}
	}
	
	public String toString(){
		return super.toString() + toAntProperty();
	}

	public int getActiveTab() {
		return activeTab;
	}

	public double[] getHeadersRatio() {
		return headersRatio;
	}

	public ParametersOrder getParamOrder() {
		return paramOrder;
	}

	public int getSortHeader() {
		return sortHeader;
	}

	public int getSortSection() {
		return sortSection;
	}
}
