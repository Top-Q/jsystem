/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The <code>TestProperties</code> enable to add additional information and
 * instruction to tests (to the method layer). The additional information
 * is then read by the framework.<p>
 * Following are the current instructions/information:<br>
 * 1. A meaningful name for the test that will be viewed in the runner.<br>
 * 2. A set a group this test assign to.<br>
 * 3. Test return parameters.<br>
 * 4. What parameters to include.<br>
 * 5. What parameters to exclude.<br>
 * @author guy.arieli
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestProperties {
	public static final String NOT_DEFINED="**NOT_DEFINED_STRING**";
	
	/**
	 * The returned name will be used to replace the current unfriendly method
	 * name. It can include parameters name as part of the name in the following
	 * format: Generate ${numberOfPackets} packets.<br>
	 * will be seen: Generate 1000 packets
	 * @return The meaningful name to use.
	 */
	String name() default "";
	/**
	 * When filtering tests in the runner the group name can be used.
	 * @return The array of group that this test will be assign to.
	 */
	String[] group() default {};
	
	/**
	 * @return array of test parameters which value is returned back to the user
	 */
	String[] returnParam() default {};
	
	/**
	 * Shows what parameters to include in the tab.
	 * @return The parameters to include.
	 */
	String[] paramsInclude() default {NOT_DEFINED};
	
	/**
	 * Shows what parameters to exclude in the tab.
	 * @return The parameters to exclude.
	 */
	String[] paramsExclude() default {NOT_DEFINED};
	
	/**
	 * Show the Fields Name.
	 * @return the fields Name.
	 */
	String[] mandatoryFields() default {NOT_DEFINED};
	
}
