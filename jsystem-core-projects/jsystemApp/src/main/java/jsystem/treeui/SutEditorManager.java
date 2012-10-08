/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.report.ListenerstManager;
import jsystem.framework.sut.SimpleSutEditor;
import jsystem.framework.sut.SutEditor;
import jsystem.framework.sut.SutFactory;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.treeui.suteditor.TabbedSutXmlEditor;
import jsystem.utils.FileUtils;
import jsystem.utils.StringUtils;

import org.w3c.dom.Document;

public class SutEditorManager {

	private static SutEditorManager manager = null;

	private static Logger log = Logger.getLogger(SutEditorManager.class.getName());

	private SutEditorManager() {
		// singleton
	}

	/**
	 */
	public static SutEditorManager getInstance() {
		if (manager == null) {
			manager = new SutEditorManager();
		}
		return manager;
	}

	public void launchEditor(){
		launchEditor(getEditor());
	}
	
	public void launchProcessedEditor(){
		launchProcessedEditor(getEditor());
	}
	
	/**
	 * Support launch of preset editor.
	 */
	public void launchEditor(final SutEditor editor) {
		if (!validateSUTFile()){
			return;
		}
		WaitDialog.launchWaitDialog("SUT editing ...", null,"(close sut editor to continue)",false);
		(new Thread() {
			public void run() {
				try {
					if (editor == null) {
						return;
					}
					Document doc = null;
					try {
						doc = editor.editSut(SutFactory.getInstance().getSutInstance().getOriginalDocument(), true);
						if (doc == null) {
							return;
						}
						File sutFile = SutFactory.getInstance().getSutFile();
						FileUtils.saveDocumentToFile(doc, sutFile);
						SutFactory.getInstance().setSut(sutFile.getName());
						copySutToTestsFolder(sutFile);
						ListenerstManager.getInstance().sutChanged(sutFile.getName());
					} catch (Exception e) {
						ErrorPanel.showErrorDialog("SUT Editor Fail", StringUtils.getStackTrace(e), ErrorLevel.Error);
						return;
					}
				} finally {
					WaitDialog.endWaitDialog();
				}

			}

			private void copySutToTestsFolder(File sutFile) {
				File classDir = new File(JSystemProperties.getCurrentTestsPath());
				if (sutFile.getAbsolutePath().startsWith(classDir.getAbsolutePath())) {
					File sutSrcFile = new File(JSystemProperties.getInstance().getPreference(
							FrameworkOptions.RESOURCES_SOURCE_FOLDER)
							+ sutFile.getAbsolutePath().substring(classDir.getAbsolutePath().length()));
					try {
						FileUtils.copyFile(sutFile, sutSrcFile);
					} catch (Exception e) {
						log.log(Level.SEVERE, "Failed updating SUT file", e);
					}
				}
			}
		}).start();
	}

	public void launchProcessedEditor(final SutEditor editor) {
		if (!validateSUTFile()){
			return;
		}
		if (editor == null) {
			return;
		}
		try {
			editor.editSut(SutFactory.getInstance().getSutInstance().getDocument(), false);
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("SUT Editor Fail", StringUtils.getStackTrace(e), ErrorLevel.Error);
			return;
		}
	}

	
	/**
	 * Returns an instance of SutEditor according to the following flow:
	 * 
	 * 1. Checks whether an SUT editor is defined in the jsystem.properties 2.
	 * If an editor is defined checks whether the editor can edit the current
	 * SUT; if so returns the defined editor. If the editor can not edit the
	 * current SUT, an error message is given to the user. 3. If an sut editor
	 * is not defined in the jsystem.properties file the method first checks if
	 * the TabbedSutXmlEditor can be used if so if not, the SimpleSutEditor is
	 * returned.
	 */
	private SutEditor getEditor() {
		SutEditor editor = null;
		try {
			String sutEditor = JSystemProperties.getInstance().getPreference(FrameworkOptions.SUT_EDITOR);
			if (sutEditor != null) {
				editor = (SutEditor) Class.forName(sutEditor).newInstance();
				if (!editor.isEditable(SutFactory.getInstance().getSutInstance().getDocument())) {
					ErrorPanel.showErrorDialog("SUT Error", "SUT can not be edited by " + sutEditor, ErrorLevel.Error);
					editor = null;
				}
			} else {
				editor = new TabbedSutXmlEditor();
				if (!editor.isEditable(SutFactory.getInstance().getSutInstance().getDocument())) {
					editor = new SimpleSutEditor();
				}
			}
		} catch (Exception e) {
			ErrorPanel.showErrorDialog("Failed to open SUT file. This can be a result of a corrupted SUT file or invalid editor class name.\nPlease check the SUT file structure and make sure the property " + FrameworkOptions.SUT_EDITOR.toString() +" in the jsystem.properties file holds a valid class name", StringUtils.getStackTrace(e), ErrorLevel.Error);
			editor = null;
		}
		return editor;
	}
	
	/**
	 */
	private boolean validateSUTFile() {
		try {
			FileUtils.readDocumentFromFile(SutFactory.getInstance().getSutFile());
		}catch (Exception e){
			ErrorPanel.showErrorDialog("Failed to open SUT file. " + e.getMessage(),StringUtils.getStackTrace(e), ErrorLevel.Error);
			return false;
		}
		return true;
	}
}
