/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import org.w3c.dom.Document;

/**
 * An interface support validation of SUT file on save event
 * @author guy.arieli
 */
public interface SutValidator {

	public String getName();
	/**
	 * The main function that return validation error if found or null if not
	 * @param sutDoc
	 * @return
	 */
	public SutValidationError[] getValidationErrors(Document sutDoc);
}
