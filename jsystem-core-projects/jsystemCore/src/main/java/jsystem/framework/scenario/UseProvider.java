/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.scenario;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UseProvider {
	/**
	 * Set the parameter provider to be used with this method.
	 */
	Class<? extends ParameterProvider> provider();

	/**
	 * define configuration
	 */
	String[] config() default {};
}
