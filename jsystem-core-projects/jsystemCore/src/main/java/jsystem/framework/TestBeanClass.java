/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jsystem.framework.scenario.ProviderDataModel;
import jsystem.framework.scenario.UseDefaultDataModel;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestBeanClass {
	/**
	 * The include is an optional attribute that set the
	 * properties to include as test bean.
	 * It is also set the order of the parameters. 
	 * @return A list of the properties name
	 */
	String[] include() default {};
		

	/**
	 * Set the data model to be used
	 */
	Class<? extends ProviderDataModel> model() default UseDefaultDataModel.class;
}
