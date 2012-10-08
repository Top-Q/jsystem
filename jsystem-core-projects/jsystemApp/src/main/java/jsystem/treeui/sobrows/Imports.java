/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.sobrows;

import java.util.HashMap;
import java.util.Iterator;

public class Imports implements CodeElement {
	HashMap<String, String> imports = new HashMap<String, String>();

	public void addImport(String importName) {
		imports.put(importName, importName);
	}

	public String toString() {
		StringBuffer importsBuf = new StringBuffer();
		Iterator<String> iter = imports.keySet().iterator();
		while (iter.hasNext()) {
			importsBuf.append("import ");
			importsBuf.append(iter.next());
			importsBuf.append(";\n");
		}
		return importsBuf.toString();
	}

	public void addToCode(Code code) {
		Iterator<String> iter = imports.keySet().iterator();
		while (iter.hasNext()) {
			code.addLine("import " + iter.next() + ";");
		}
	}

}
