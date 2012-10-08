package org.jsystemtest.jsystem_services_tests;

public class Account {

	public enum Gender {
		MR, MRS
	}

	private Gender option;

	private String firstName;

	private String lastName;

	private int id;

	public Gender getOption() {
		return option;
	}

	public void setOption(Gender option) {
		this.option = option;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
