/*
 * Created on 28/07/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.runner.remote;

import java.io.Serializable;
import java.util.ArrayList;

import jsystem.runner.remote.RemoteTestRunner.RemoteMessage;

/**
 * A message that will be send back and force from the runner vm to the test vm.
 * The object is sent using serialization.
 * 
 * @author guy.arieli
 * 
 */
public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -250921260420270017L;

	final static int MAX_FIELD_SIZE = 1000000;

	ArrayList<String> fields = new ArrayList<String>();

	/**
	 * Message type
	 * 
	 * @see RemoteMessage
	 */
	RemoteMessage type;

	public RemoteMessage getType() {
		return type;
	}

	public void setType(RemoteMessage type) {
		this.type = type;
	}

	/**
	 * Add a field to the message
	 * 
	 * @param field
	 *            the field to add
	 */
	public void addField(String field) {
		/*
		 * If the field is larger then the max size cut it.
		 */
		if (field != null && field.length() > MAX_FIELD_SIZE) { // if size is
																// bigger then
																// Mega trim
			field = field.substring(0, MAX_FIELD_SIZE);
		}
		fields.add(field);
	}

	public String getField(int index) {
		return fields.get(index);
	}

	public ArrayList<String> getFields() {
		return fields;
	}

	public void setFields(ArrayList<String> fields) {
		this.fields = fields;
	}

}
