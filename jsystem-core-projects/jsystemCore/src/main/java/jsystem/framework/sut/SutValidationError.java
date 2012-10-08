/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

public class SutValidationError {
	
	public enum Sevirity{
		WARNING,
		ERROR,
		FATAL;
	}
	
	protected String message;
	protected Sevirity sevirity;
	
	public SutValidationError(String message, Sevirity sevirity){
		this.message = message;
		this.sevirity = sevirity;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Sevirity getSevirity() {
		return sevirity;
	}
	public void setSevirity(Sevirity sevirity) {
		this.sevirity = sevirity;
	}
}
