package il.co.topq.refactor.model;

import il.co.topq.refactor.exception.ScenarioXmlParseException;
import il.co.topq.refactor.exception.UnmodifiableFileException;
import il.co.topq.refactor.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.extensions.sourcecontrol.SourceControlException;
import jsystem.extensions.sourcecontrol.SourceControlI;

/**
 * 
 * @author Agmon
 *
 */
public abstract class JSystemFile {

	protected Logger log = Logger.getLogger(this.getClass().getSimpleName());

	protected File file;

	public JSystemFile(File file) {
		super();
		this.file = file;
	}

	public void backup() {
		log.fine("Creating backup of file " + file.getAbsolutePath());
		File backupFile = new File(file.getAbsolutePath() + ".old");
		if (backupFile.exists()) {
			backupFile.delete();
		}
		try {
			FileUtils.copyFile(file, backupFile);
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to create backup file", e);
		}
	}

	public abstract void save() throws Exception;

	@Override
	public String toString() {
		return file.getAbsolutePath();
	}

	public void save(SourceControlI sourceControHandler) throws Exception {

        boolean fileIsWritable = false;

        if(file.canWrite()){
            fileIsWritable = true;
        }
        else {
            log.info("File " + file.getName() + " is not writable.");
            if(sourceControHandler != null){
                if(sourceControHandler.supportMakeWritable()){
                    log.info("Using source control interface to make it writable.");
                    List<File> filesList = new ArrayList<File>();
                    filesList.add(file);
                    try {
                        sourceControHandler.makeWritable(filesList);
                    }
                    catch (SourceControlException e) {
                        log.log(Level.SEVERE, "Exception was caught while trying to make file writable", e);
                        throw new IOException("Exception was caught while trying to make file writable");
                    }
                    if (!file.canWrite()) {
                        log.severe("Failed to make file writable");
                        throw new IOException("Failed to make file writable");
                    }

                    fileIsWritable = true;
                }
                else{
                    log.info("The source control plugin was provided but it doesn't support The option to make the file writable.");
                }
            }
            else{
                log.info("The source control plugin was not provided. cannot make file writable");
            }
        }

        if(fileIsWritable)
        {
            save();
        }
//		if (sourceControHandler != null && !file.canWrite()) {
//			log.info("File " + file.getName() + " is not writeable. Using source control interface");
//			List<File> filesList = new ArrayList<File>();
//			filesList.add(file);
//			try {
//				sourceControHandler.makeWritable(filesList);
//			} catch (SourceControlException e) {
//				log.log(Level.SEVERE, "Exception was caught while trying to make file writeable", e);
//				throw new IOException("Exception was caught while trying to make file writeable");
//			}
//			if (!file.canWrite()) {
//				log.severe("Failed to make file writeable");
//				throw new IOException("Failed to make file writeable");
//			}
//
//		}
//		save();
	}
}
