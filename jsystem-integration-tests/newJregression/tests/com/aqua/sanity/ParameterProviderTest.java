package com.aqua.sanity;

import jsystem.framework.scenario.UseProvider;

import org.junit.Test;

import com.aqua.base.JSysTestCase4;
import com.aqua.testConstructs.StringBean;
import com.aqua.testConstructs.StringValueParametersProvider;

public class ParameterProviderTest extends JSysTestCase4 {

	private StringBean myTestBean;

	public StringBean getMyTestBean() {
		return myTestBean;
	}
	
	@Test
	public void testDoNothing()throws Exception{
		
	}

	@UseProvider(provider=StringValueParametersProvider.class)
//	@UseProvider(provider=StringArrayOptionsParameterProvider.class)
	public void setMyTestBean(StringBean myTestBean) {
		this.myTestBean = myTestBean;
	}
}
