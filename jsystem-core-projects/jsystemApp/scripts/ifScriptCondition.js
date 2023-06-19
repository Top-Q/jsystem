var expression = self.params; // the parameters from the condition tag
var separator = " <SEP> ";
print('Checking condition: ' +  expression);
var result = evaluate(expression);
print("Result = " + result);
self.setValue(result); // the result for the condition

/**
 * Parse a given expression, using optional operators array
 */
function parseExpression(expression, optionalOperators){
	// This is done for compatability with Java 8
	expression = expression.replaceAll(separator,"");
	for (var i=0 ; i<optionalOperators.length ; i++){
		if (expression.contains(optionalOperators[i])){
			var temp = expression.split(optionalOperators[i]);
			if (temp.length < 2){
				return false;
			}

			return [temp[0].trim(),temp[1].trim()];
		}
	}
	return false;
}

/**
 * Split expression to an array of the comparison Type and the rest
 */
function getComparisonTypeAndRest(expression){
	var type = expression.split(separator)[0];
	print('Type: ' +  type);
	var len = type.length();
	print('Len: ' +  len);
	var rest = expression.substring(len);	
	return [type,rest];
}

/**
 * Evaluate a mathematical comparison expression
 */
function evaluateMath(expression){
	var numbers = parseExpression(expression, ['>=','<=','!=','=','<','>']);

	if (numbers == false || isNaN(numbers[0]) || isNaN(numbers[1])){
		print("Not a mathematical expression: " + expression);
		return false;
	}
	
	numbers[0] = parseFloat(numbers[0]);
	numbers[1] = parseFloat(numbers[1]);
	
	// This is done for compatability with Java 8
	expression = expression.replaceAll(separator,"");
	if (expression.contains(">=")){
		return (numbers[0] >= numbers[1]);
	}
	if (expression.contains("<=")){
		return (numbers[0] <= numbers[1]);
	}
	if (expression.contains("!=")){
		return (numbers[0] != numbers[1]);
	}
	if (expression.contains("=")){
		return numbers[0] == numbers[1];
	}
	if (expression.contains(">")){
		return numbers[0] > numbers[1];
	}
	if (expression.contains("<")){
		return numbers[0] < numbers[1];
	}
	
	return false;
}

/**
 * Evaluate a String comparison expression
 */
function evaluateString(expression,isCaseSensitive){	
	var numbers = parseExpression(expression, ["NOT_EQUALS","EQUALS","CONTAINS","STARTS_WITH","ENDS_WITH"]);
	if (numbers == false){
		print("Not a string expression: " + expression);
		return false;
	}
	if (isCaseSensitive == 'false') {
		numbers[0] = numbers[0].toLowerCase();
		numbers[1] = numbers[1].toLowerCase();
	}
	// This is done for compatability with Java 8
	expression = expression.replaceAll(separator,"");
	if (expression.contains("NOT_EQUALS")){
		return (!numbers[0].equals(numbers[1]));
	}
	if (expression.contains("EQUALS")){
		return (numbers[0].equals(numbers[1]));
	}
	if (expression.contains("CONTAINS")){
		return (numbers[0].contains(numbers[1]));
	}
	if (expression.contains("STARTS_WITH")){
		return numbers[0].startsWith(numbers[1]);
	}
	if (expression.contains("ENDS_WITH")){
		return numbers[0].endsWith(numbers[1]);
	}
	
	return false;
}

/**
 *	String regular trim
 */
function trim(str){
	return str.replace(/^[\s\xA0]+/, "").replace(/[\s\xA0]+$/, "");
}

/**
 * String regular startWith
 */
function startsWith(str1,str2){
	return str1.match("^"+st2)==str2;
}

/**
 * String regular endsWith
 */
function endsWith(str1,str2){
	return str1.match(str2+"$")==str2;
}

/**
 *	Evaluate Math\String expression
 */
function evaluate(expression){
	var typeAndRest = getComparisonTypeAndRest(expression);
	
	if (typeAndRest[0].equals("math")){
		return evaluateMath(typeAndRest[1].replace(separator, " "));
	}
	if (typeAndRest[0].equals("str")){
		var isCaseSensitiveRes = isCaseSensitive(typeAndRest[1]);
		var exp = getExpression(typeAndRest[1]).replace(separator," ");
		return evaluateString(exp,isCaseSensitiveRes);
	}
	print("Unrecognized expression!!!");
	return false;
	
	function isCaseSensitive(str) {
		return str.substring(str.lastIndexOf(separator)+separator.length);	
	}
	
	function getExpression(str) {
		return str.substring(0,str.lastIndexOf(separator));
	}
	
}

