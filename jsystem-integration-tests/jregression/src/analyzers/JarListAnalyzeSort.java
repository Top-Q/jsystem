package analyzers;

import java.util.Arrays;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

public class JarListAnalyzeSort extends AnalyzerParameterImpl {
	String[] fetchedSortedArray;
	String[] expectedSortedArray;
	int count = 0;
	int j=0;
	public JarListAnalyzeSort(String[] expectedSortedArray) {
		// TODO Auto-generated constructor stub
		this.expectedSortedArray = expectedSortedArray;
	}
	/**
	 * takes each one of the values in the expected values list
	 * and compares it with each values fetched from jarList in Gui.
	 * Order is determined by comparing each value expected with the next 
	 * value by order of fetched list.
	 */
	@Override
	public void analyze() {
		// TODO Auto-generated method stub
		Arrays.sort(expectedSortedArray);
		fetchedSortedArray = (String[])testAgainst;
		
		for(int i = 0; i < expectedSortedArray.length; i++){
			while(j < fetchedSortedArray.length){
				if(expectedSortedArray[i].equals(fetchedSortedArray[j])){
					count++;
					break;
				}
				j++;
			}
		}
		if(count == expectedSortedArray.length){
			status = true;
			return;
		}
		else{
			status = false;
			return;
		}
	}

}
