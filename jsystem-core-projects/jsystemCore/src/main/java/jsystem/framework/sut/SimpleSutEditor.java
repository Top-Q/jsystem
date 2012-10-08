/*
 * Created on 28/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import java.awt.Component;
import java.io.File;

import javax.swing.JOptionPane;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.TestRunnerFrame;
import jsystem.utils.FileUtils;

import org.w3c.dom.Document;

public class SimpleSutEditor implements SutEditor {

	/**
	 */
	public boolean isEditable(Document doc) throws Exception {
		return true;
	}

	/**
	 */
	public Document editSut(Document doc, boolean withSave) throws Exception {
		String osName = System.getProperty("os.name").toLowerCase();

		File editFile = null;
		editFile = File.createTempFile("jsystem", ".xml");

		FileUtils.saveDocumentToFile(doc, editFile);

		String xmlEditor = JSystemProperties.getInstance().getPreference(FrameworkOptions.XML_EDITOR);
		if (xmlEditor == null || xmlEditor.equals("")) {
			if (osName.startsWith("windows")) {
				xmlEditor = "notepad.exe";
			} else {
				xmlEditor = "gedit";
			}

			JSystemProperties.getInstance().setPreference(FrameworkOptions.XML_EDITOR, xmlEditor);
		}

		Process p = null;

		/*
		 * Take the MD5 of the original file to test if it was changed
		 */
		String originalMd5 = FileUtils.getMD5(editFile);

		if (osName.startsWith("Windows")) {
			p = Runtime.getRuntime().exec(new String[] { xmlEditor, "\"" + editFile.getPath() + "\"" });
		} else {
			p = Runtime.getRuntime().exec(new String[] { xmlEditor, editFile.getPath() });
		}

		if (withSave){
		
			p.waitFor();
			/*
			 * If the file didn't change (original MD5 equals the current) return
			 * null
			 */
			if (originalMd5.equals(FileUtils.getMD5(editFile))) {
				return null;
			}
			int option = JOptionPane.showConfirmDialog(TestRunnerFrame.guiMainFrame,
					"Would you like to overwrite the existing sut file?", "Edit sut", JOptionPane.YES_NO_CANCEL_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				return FileUtils.readDocumentFromFile(editFile);
			}
			return null;
		}else{
			return doc;
		}
	}

	public String launch(String sutToEdit, String sutDirectory, Component parent) throws Exception {
		return null;
	}

}
