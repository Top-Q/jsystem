package com.aqua.services.ParameterProvider;


import java.io.File;

public class SampleBean {

	
	private String beanName;

	private int id;

	private File file;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}



}
