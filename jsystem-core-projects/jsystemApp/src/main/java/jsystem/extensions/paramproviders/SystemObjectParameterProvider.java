/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import java.awt.Component;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.treeui.suteditor.planner.SutTreeDialog;
import jsystem.utils.XmlUtils;

import org.apache.tools.ant.util.ReaderInputStream;
import org.w3c.dom.Document;

/**
 * Enable SystemObject parameter for test using the SUT editor.
 * This capability can be use to create data type based on SystemObject interface.
 * Then use it with the SUT planner.<p>
 * '-disableRootEdit' can be added in the config option.<p>
 * When used it will disable the option to add new SystemObject. 
 * 
 * @author guy.arieli
 *
 */
public class SystemObjectParameterProvider implements ParameterProvider {
	private static final String OBJECT_BROWSER = "Object Browser";
	private String[] args;

	@Override
	public String getAsString(Object o) {
		if(o == null){
			return "<sut></sut>";
		}
		return o.toString();
	}

	@Override
	public Object getFromString(String stringRepresentation) throws Exception {
		return stringRepresentation; 
		//XmlUtils.getDocumentBuilder().parse(new StringBufferInputStream(stringRepresentation));
	}

	@Override
	public boolean isFieldEditable() {
		return true;
	}

	@Override
	public Object showUI(Component parent, Scenario scenario, RunnerTest runnerTest,
			Class<?> myClass, Object currentObject, Parameter currentParameter) throws Exception {
		SutTreeDialog sutTreeDialog = new SutTreeDialog(OBJECT_BROWSER);
		sutTreeDialog.setEnableAddToRoot(isRootEditable());
		Document outDocument = sutTreeDialog.editSut(
				XmlUtils.getDocumentBuilder().parse(
						new ReaderInputStream(
								new StringReader(currentObject.toString()),
								"UTF-8")), true);
		StringWriter outStringWriter = new StringWriter();
		Source source = new DOMSource(outDocument);
		Result result = new StreamResult(outStringWriter);
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		xformer.transform(source, result);
		return outStringWriter.toString();
	}

	@Override
	public void setProviderConfig(String... args) {
		this.args = args;
	}
	private boolean isRootEditable(){
		if(args == null){
			return true;
		}
		for(String arg: args){
			if("-disableRootEdit".equals(arg)){
				return false;
			}
		}
		return true;
	}
}
