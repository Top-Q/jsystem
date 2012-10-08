package analyzers;
import jsystem.framework.analyzer.AnalyzerParameterImpl;

/**
 * analyze that an expected jar list to e found is really found
 * @author Dan
 *
 */
public class JarListAnalyzeContent extends AnalyzerParameterImpl {
	
	String jarListToCheck;
	String[] arrayToCheck;
	String[] arrayToCompare;
	
	public JarListAnalyzeContent(String[] arrayToCompare){
		this.arrayToCompare = arrayToCompare;
	}
	
	/**
	 * checks that jars fetched from jar list are exectly the same
	 * as jars expected
	 */
	@Override
	public void analyze() {
		jarListToCheck = testAgainst.toString().trim();
		arrayToCheck = jarListToCheck.split(" +");
		
		for(String jar: arrayToCompare){
			if( contains(jar, arrayToCheck)){
				continue;
			}
			else{
				message = jarListToCheck;
				status = false;
				title = "jar "+ jar+ "is missing in jar list";
				return;
			}
		}
		status = true;
		title = "all jars expected are found in jarList";
	}
	private boolean contains(String str, String[] array){
		for(int i = 0; i < array.length; i++){
			if(array[i].equals(str)){
				return true;
			}
		}
		return false;
	}
}
