package jsystem.treeui.dialog;

import java.util.ArrayList;

/**
 * @author Tomer Gafner
 * This is the Model for the UnmodifiedFileDialog view.
 * It consists of a list of Option and the Option selected by the user or the default one.
 * It always has a FILE_SYSTEM option.
 */
public class UnmodifiableFileModel {

	private static UnmodifiableFileModel instance;
	
	synchronized public static UnmodifiableFileModel getInstance(){
		if(instance == null){
			instance = new UnmodifiableFileModel();
		}
		return instance;
	}
	
	private UnmodifiableFileModel(){
		availableOptions.add(Option.FILE_SYSTEM);
	}
	
	public enum Option {
		FILE_SYSTEM("Using file system"),VCS("Using version control integration");
		
		private final String description;

		private Option(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
		
	}
	
	private ArrayList<Option> availableOptions = new ArrayList<Option>();

	private Option selectedOption;
	
	
	public void addAvailableOptions(Option option) {
		availableOptions.add(option);
	}

	public ArrayList<Option> getAvailableOptions() {
		return availableOptions;
	}

	public void setSelectedOption(Option selectedOption) {
		this.selectedOption = selectedOption;
	}

	public Option getSelectedOption() {
		if(selectedOption == null){
			if(availableOptions.contains(Option.VCS)){
				selectedOption = Option.VCS;
			}
			else {
				selectedOption = Option.FILE_SYSTEM;
			}
		}
		return selectedOption;
	}

	
}
