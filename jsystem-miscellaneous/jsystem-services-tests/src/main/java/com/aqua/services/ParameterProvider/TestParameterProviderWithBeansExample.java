package com.aqua.services.ParameterProvider;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import jsystem.framework.ParameterProperties;
import jsystem.framework.RunProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.scenario.UseProvider;
import junit.framework.SystemTestCase4;

public class TestParameterProviderWithBeansExample extends SystemTestCase4 {

	private SampleBean bean;
	private SampleBean[] beanArr;
	
	/**
	 * Usage: insert ${run:demoPath} to the test with
	 * parameter provider's bean "file" member from within the UI
	 * It will fetch the value from the setter below.
	 */
	
	@Before
	public void before() throws Throwable{
		super.defaultBefore();
		RunProperties.getInstance().setRunProperty("demoPath", "C:\\program files\\java");
	}
	
	
	/**
	 * Test with parameter provider
	 * @throws IOException 
	 */
	@Test
	@TestProperties(name = "Test with parameter provider", paramsInclude = { "bean" })
	public void testWithParameterProvider() throws IOException {
		
		
		if(getBean()!=null){
			report.report("Bean name: "+getBean().getBeanName());
			report.report("Bean id : "+getBean().getId());
			report.report("Bean file name: "+getBean().getFile().getName());
			report.report("Bean file path: "+getBean().getFile().getPath());
			report.report("Bean file absolute path: "+getBean().getFile().getAbsolutePath());
			report.report("Bean file cannonical path: "+getBean().getFile().getCanonicalPath());
		}
		else report.report("please initialize the Bean");
	
	}
	
	
	@Test
	@TestProperties(name = "Test with parameter array provider", paramsInclude = { "beanArr" })
	public void testWithParameterArrayProvider() throws IOException {
		
		if(getBeanArr().length>0){
			for(SampleBean bean : getBeanArr()){
				report.report("Bean name: "+bean.getBeanName());
				report.report("Bean id : "+bean.getId());
				report.report("Bean file name: "+bean.getFile().getName());
				report.report("Bean file path: "+bean.getFile().getPath());
				report.report("Bean file absolute path: "+bean.getFile().getAbsolutePath());
				report.report("Bean file cannonical path: "+bean.getFile().getCanonicalPath());
			}
		}
		else report.report("please initialize at least 1 bean");
	
	}
	


	public SampleBean[] getBeanArr() {
		return beanArr;
	}

	@ParameterProperties(description = "Provider that exposes bean array")
	@UseProvider(provider = jsystem.extensions.paramproviders.ObjectArrayParameterProvider.class)
	public void setBeanArr(SampleBean[] beanArr) {
		this.beanArr = beanArr;
	}


	public SampleBean getBean() {
		return bean;
	}

	@ParameterProperties(description = "Provider that exposes bean object")
	@UseProvider(provider = jsystem.extensions.paramproviders.GenericObjectParameterProvider.class)
	public void setBean(SampleBean bean) {
		this.bean = bean;
	}

}

