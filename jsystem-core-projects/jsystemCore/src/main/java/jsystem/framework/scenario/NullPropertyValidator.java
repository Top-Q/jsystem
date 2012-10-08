/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import jsystem.framework.sut.SutValidationError;

public class NullPropertyValidator implements PropertyValidator {

	@Override
	public SutValidationError[] validate(String propertyName,
			String value) {
		return null;
	}

}
