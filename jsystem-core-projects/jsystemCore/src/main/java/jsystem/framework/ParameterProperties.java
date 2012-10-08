/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>ParameterProperties</code> enable to add a section and a description to the section<p>
 * Following are the current instructions/information:<br>
 * 1. Add a section.
 * 2. Add a description.
 * @author guy.chen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ParameterProperties {

	/**
	 * Add a section.
	 * @return The section to include.
	 */
	
	String section() default "";

	/**
	 * Add a description to a parameter.
	 * @return The description to include.
	 */
	String description() default "";

}
