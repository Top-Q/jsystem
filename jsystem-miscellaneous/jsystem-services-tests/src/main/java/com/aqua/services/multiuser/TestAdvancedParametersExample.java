/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.multiuser;

import java.io.File;
import java.util.Date;

import jsystem.framework.RunProperties;
import jsystem.framework.TestProperties;
import jsystem.framework.report.Summary;
import jsystem.framework.scenario.UseProvider;
import jsystem.utils.StringUtils;
import junit.framework.SystemTestCase;

public class TestAdvancedParametersExample extends SystemTestCase {

	private File folder;
	private Date createTime;
	private String[] stringArrayWithOptions;
	private SimpleBean[] simpleBeanArr;
	private AnotherBean[] anotherBean;
	private SimpleBean justABean;

	@TestProperties(paramsInclude = { "folder", "createTime", "stringArrayWithOptions", "simpleBeanArr", "anotherBean",
			"justABean" })
	public void testVerifyFolderCreateTime() throws Exception {

		Summary.getInstance().setTempProperty("eli", " value");
		RunProperties.getInstance().setRunProperty("elikoko", "balbal");
		if (getFolder() != null) {
			report.report("folder is" + getFolder().toString());
		}
		if (getCreateTime() != null) {
			report.report("Time is" + getCreateTime().toString());
		}
		if (getStringArrayWithOptions() != null) {
			report.report("String array:" + StringUtils.objectArrayToString(",", (Object[]) stringArrayWithOptions));
		}
		if (simpleBeanArr != null) {
			report.report("Bean array:" + StringUtils.objectArrayToString(",", (Object[]) simpleBeanArr));
		}
	}

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String[] getStringArrayWithOptionsOptions() {
		return new String[] { "option1", "option2", "option3" };
	}

	public String[] getStringArrayWithOptions() {
		return stringArrayWithOptions;
	}

	public void setStringArrayWithOptions(String[] stringArrayWithOptions) {
		this.stringArrayWithOptions = stringArrayWithOptions;
	}

	public SimpleBean[] getSimpleBean() {
		return simpleBeanArr;
	}

	public void setSimpleBean(SimpleBean[] simpleBean) {
		this.simpleBeanArr = simpleBean;
	}

	public AnotherBean[] getAnotherBean() {
		return anotherBean;
	}

	@UseProvider(provider = jsystem.extensions.paramproviders.ObjectArrayParameterProvider.class)
	public void setAnotherBean(AnotherBean[] anotherBean) {
		this.anotherBean = anotherBean;
	}

	public SimpleBean getJustABean() {
		return justABean;
	}

	@UseProvider(provider = jsystem.extensions.paramproviders.GenericObjectParameterProvider.class)
	public void setJustABean(SimpleBean justABean) {
		this.justABean = justABean;
	}

	public SimpleBean[] getSimpleBeanArr() {
		return simpleBeanArr;
	}

	@UseProvider(provider = jsystem.extensions.paramproviders.ObjectArrayParameterProvider.class)
	public void setSimpleBeanArr(SimpleBean[] simpleBeanArr) {
		this.simpleBeanArr = simpleBeanArr;
	}

}
