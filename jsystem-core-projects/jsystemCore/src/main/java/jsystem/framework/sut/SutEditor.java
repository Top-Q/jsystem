/*
 * Created on 28/10/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.framework.sut;

import org.w3c.dom.Document;

/**
 * Define the interface for SUT editor
 * 
 * @author guy.arieli
 * 
 */
public interface SutEditor {

	/**
	 * gets a document object with sut data and returns the sut data after
	 * update.
	 * @param withSave True means the Sut will be saved on changes
	 */
	public Document editSut(Document doc, boolean withSave) throws Exception;

	/**
	 * Returns true id sut can be edit by editor.
	 */
	public boolean isEditable(Document doc) throws Exception;
}
