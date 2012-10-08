/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jsystem.framework.scenario.NullPropertyValidator;
import jsystem.framework.scenario.PropertyValidator;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestBeanMethod {
	boolean ignore() default false;
	Class<? extends PropertyValidator>[] validators() default NullPropertyValidator.class;
	String[] options() default {};
	/**
	 * When filtering parameters in the SUT planner the group name can be used.
	 * @return The array of group that this test will be assign to.
	 */
	String[] group() default {};
	
	boolean editable() default true;
}
