/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.analyzers.document;

import org.w3c.dom.Document;

import jsystem.framework.analyzer.AnalyzerParameterImpl;

public abstract class DocumentAnalyzer extends AnalyzerParameterImpl {
	protected Document doc;

	public void setTestAgainst(Object o) {
		if (o != null) {
			doc = (Document) o;
		}
	}

	public Class<?> getTestAgainstType() {
		return Document.class;
	}

}
