package il.co.topq.refactor.commands;

import org.apache.commons.cli.*;

/**
 * The Commander class parses the user command line
 * and can be queried to retrieve user values and parameters
 *
 * @author Itai Agmon
 */
public class Commander {

    private final CommandLine commandLine;
    
    private final Options options;


    /**
     * When the constructor of the commander is called
     * the user arguments are parsed and the commander is ready to be queried.
     * @param args - the user arguments
     * @throws ParseException - thrown in case a parsing exception occur
     */
    public Commander(String... args) throws ParseException {
        options = buildOptions(
        		RenameParameterOptions.OPT_HELP,
        		RenameParameterOptions.OPT_MODE,
        		RenameParameterOptions.OPT_PROJECT_DIR,
                RenameParameterOptions.OPT_TEST_FULL_NAME,
                RenameParameterOptions.OPT_OLD_SCENARIO,
                RenameParameterOptions.OPT_NEW_SCENARIO,
                RenameParameterOptions.OPT_OLD_TEST,
                RenameParameterOptions.OPT_NEW_TEST,
                RenameParameterOptions.OPT_OLD_PARAM,
                RenameParameterOptions.OPT_NEW_PARAM,
                RenameParameterOptions.OPT_BEAN_FULL_NAME
                );
        Parser parser = new GnuParser();
        commandLine = parser.parse(options, args);
    }


    /**
     * return the values that the user has used with a specific option
     * e.g. -Ds path1;path2 => The String[] will contain {path1,path2}
     * @param option - one of the option that could be used by the user
     * @return - An array containing all the values for a specific option
     */
    public String[] getOptionValues(Option option){
        if(hasOption(option)){
            return commandLine.getOptionValues(option.getOpt());
        }
        return null;
    }


    /**
     * Determine whether an option is available
     * @param option - one of the option that could be used by the user
     * @return - return true if the option was used by the user, false otherwise
     */
    public boolean hasOption(Option option){
        return commandLine.hasOption(option.getOpt());
    }


    /**
     * Build the Options object  which is
     * an object that describe the possible options for a command-line
     * @param options - a list of options supported
     * @return an object that describe the possible options for a command-line
     */
    private Options buildOptions(Option... options) {
        Options optionsObj = new Options();
        for (Option option : options) {
            optionsObj.addOption(option);
        }
        return optionsObj;
    }

    /**
     * return the value that the user has used with a specific option
     * e.g. -Dd path1 => The String will contain "path1"
     * @param option - one of the option that could be used by the user
     * @return - The value of a specific option
     */
    public String getOptionValue(Option option) {
        if(hasOption(option)){
            return commandLine.getOptionValue(option.getOpt());
        }
        return null;
    }


	public Options getOptions() {
		return options;
	}
    
    
}
