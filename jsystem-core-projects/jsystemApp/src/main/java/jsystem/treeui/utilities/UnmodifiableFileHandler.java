package jsystem.treeui.utilities;

import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import jsystem.extensions.sourcecontrol.SourceControlException;
import jsystem.extensions.sourcecontrol.SourceControlI;
import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.TestRunner;
import jsystem.treeui.dialog.UnmodifiableFileDialog;
import jsystem.treeui.dialog.UnmodifiableFileModel;
import jsystem.treeui.dialog.UnmodifiableFileModel.Option;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.images.ImageCenter;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

/**
 * Uses for setting permissions of file to writable.
 * 
 * @author Tomer Gafner.
 * 
 */
public class UnmodifiableFileHandler {

	private static Logger log = Logger.getLogger(UnmodifiableFileHandler.class.getName());

	private static UnmodifiableFileHandler instance;

	private final UnmodifiableFileModel modifyFileOptions;

	private SourceControlI sourceControl;

	private UnmodifiableFileDialog unmodifiableFileDialog;

	/**
	 * This code build the model available for the UnmodifiableFileDialog used
	 * in case JSystem is unable to save a scenario because some files are not
	 * modifiable. The reason why a file would be in such status could be, for
	 * example, lack of user permission or a specific methodology used by VCS
	 * like ClearCase where all the files are read only unless checked-out.
	 */
	private UnmodifiableFileHandler() {
		modifyFileOptions = UnmodifiableFileModel.getInstance();
		final String className = JSystemProperties.getInstance().getPreference(FrameworkOptions.SCM_PLUGIN_CLASS);
		try {
			if (!StringUtils.isEmpty(className)) {
				final Class<?> sourceControlClass = Class.forName(className);
				if (sourceControlClass != null) {
					sourceControl = (SourceControlI) sourceControlClass.newInstance();
					try {
						if ((sourceControl != null) && (sourceControl.supportMakeWritable())) {
							modifyFileOptions.addAvailableOptions(Option.VCS);
						}
					} catch (SourceControlException e) {
						log.log(Level.WARNING, "Failed to instanciate source control", e);
						JOptionPane.showConfirmDialog(TestRunner.treeView, "Failed to instanciate source control",
								"Source Control Failure", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			// In case of any exception occurred there is some error with the
			// VCS plug-in and therefore it is not possible
			// to add the relevant feature to make a file modifiable for the
			// user.
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to load SCM plugin", e);
		}

	}

	public static UnmodifiableFileHandler getInstance() {
		if (null == instance) {
			instance = new UnmodifiableFileHandler();
		}
		return instance;
	}

	/**
	 * 
	 * Checks if files are writable, if not change the permissions using file
	 * system or source control integration.
	 * 
	 * @param filesArr
	 *            Files to check if can be modifiable.
	 * @return true if files are now writable, false if failed to change
	 *         permission or user canceled operation.
	 */
	public boolean makeWritable(File[] filesArr) {
		while (true) {
			List<File> failAccessFiles = FileUtils.getFilesCannotAccess(filesArr);
			if (failAccessFiles.size() == 0) {
				break;
			}
			// If JSystem does not have access to write to a file it pop-up a
			// dialog suggesting the user how to modify the
			// file status so it can be modifiable.
			// If no VCS is available JSystem suggest to change the write
			// permission of the file throw the Operating System.
			// If a VCS is available and support the makeWritable method the
			// user has another way to modify
			// the current status of the file throw the VCS.
			showUnmodifiableFileDialog(failAccessFiles);
			if (unmodifiableFileDialog.getLastUserCloseOperationStatus() != UnmodifiableFileDialog.OK_OPTION) {
				return false;
			}
			switch (modifyFileOptions.getSelectedOption()) {
			case FILE_SYSTEM:
				for (File file : failAccessFiles) {
					if (!file.setWritable(true)) {
						ErrorPanel.showErrorDialogOkCancel("Cannot save scenario",
								"Possibly lack of permission to write to the file: " + file.getAbsolutePath(),
								ErrorLevel.Error);
						return false;
					}
				}
				break;
			case VCS:
				try {
					sourceControl.makeWritable(failAccessFiles);
					break;
				} catch (SourceControlException e1) {
					ErrorPanel.showErrorDialogOkCancel("Cannot save scenario", e1.getMessage(), ErrorLevel.Error);
					return false;
				}
			default:
				return false;
			}
		}
		return true;

	}

	private void showUnmodifiableFileDialog(List<File> failAccessFiles) {
		unmodifiableFileDialog = UnmodifiableFileDialog.getInstance(modifyFileOptions);
		unmodifiableFileDialog.setTitle("Files in read only status");
		ImageIcon icon = new ImageIcon(ImageCenter.ICON_INFO);
		unmodifiableFileDialog.setIconImage(icon.getImage());
		unmodifiableFileDialog.setPreferredSize(new Dimension(700, 350));
		unmodifiableFileDialog.pack();
		unmodifiableFileDialog.displayFiles(failAccessFiles);
		unmodifiableFileDialog.setVisible(true);
	}

}
