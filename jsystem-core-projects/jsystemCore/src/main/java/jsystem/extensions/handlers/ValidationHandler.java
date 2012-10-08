package jsystem.extensions.handlers;

import java.util.HashMap;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ValidationError;

/**
 * Tests implementing this class will have support for validating parameters panel values 
 * and throw Validations in case there are errors
 * 
 * @author Nizan Freedman
 *
 */
public interface ValidationHandler {
	
	public ValidationError[] validate(HashMap<String,Parameter> map,String methodName) throws Exception;
}