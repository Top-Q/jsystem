/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.utilities.PropertiesDialog;
import jsystem.utils.StringUtils;
import jsystem.utils.beans.BeanDefaultsExtractor;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.BeanUtils;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericObjectParameterProvider extends AbstractSerializingParameterProvider{

	private static Logger log = Logger.getLogger(GenericObjectParameterProvider.class.getName());
	
	public GenericObjectParameterProvider(){
	}

	@Override
	public String getAsString(Object o) {
		// if the input object is null or is of type String return
		if(o == null){
			return null;
		}
		if(o instanceof String){
			return (String)o;
		}


		ArrayList<BeanElement> beanElements = BeanUtils.getBeans(o.getClass(), true, true, BeanUtils.getBasicTypes());
		
		// build properties object from the given object
		Properties properties = new Properties();
		for(BeanElement be: beanElements){
			if(be.getGetMethod() == null){
				continue;
			}
			try {
				Object value = be.getGetMethod().invoke(o, new Object[0]);
				if(value != null){
					String propertyValue = StringUtils.advancedToString(value);
					properties.setProperty(be.getName(), propertyValue);
				}
			} catch (Exception e) {
				log.log(Level.WARNING,"Fail to invoke the getter: " + be.getName(), e);
			}
		}
		
		return propetiesToString(o.getClass().getName(), properties);
	}


	@Override
	public Object getFromString(String stringRepresentation) throws Exception {
		// if the input is null return null object
		if(stringRepresentation == null){
			return null;
		}
		// first extract the class name
		int classEndIndex = stringRepresentation.indexOf(';');
		if(classEndIndex < 0){
			return null;
		}
		String className = stringRepresentation.substring(0, classEndIndex);
		
		// then extract the string to be load as properties object
		String propertiesString = stringRepresentation.substring(classEndIndex + 1);
		Properties properties = new Properties();
		try {
			propertiesString = multiplySingleBackslashes(propertiesString);
			properties.load(new StringReader(propertiesString));
		} catch (IOException e1) {
			log.log(Level.WARNING, "Fail to load properties: " + propertiesString, e1);
			return null;
		}
		// create the class from the input string
		Class<?> c;
		try {
			c = LoadersManager.getInstance().getLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			log.log(Level.WARNING, "Fail to create class: " + className, e);
			return null;
		}
		// create the object and init it using the properties
		return BeanUtils.propertiesToObject(c, propertiesToMap(properties));
	}

	@Override
	public boolean isFieldEditable() {
		return false;
	}

	@Override
	public synchronized Object showUI(Component parent, Scenario currentScenario, RunnerTest rtest, Class<?> classType, Object object,Parameter parameter) throws Exception {
		ArrayList<BeanElement> beanElements = BeanUtils.getBeans(classType, true, true, BeanUtils.getBasicTypes());

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		String[] properties = getProeprties(beanElements);
		Properties prop = BeanDefaultsExtractor.getBeanDefaults(classType, properties);
		for(BeanElement be: beanElements){
			map.put(be.getName(), prop.getProperty(be.getName()));
		}
		if(object != null){
			if((!(classType.isAssignableFrom(object.getClass())))){
				object = getFromString(object.toString());
			}

			Properties oProperties = BeanUtils.objectToProperties(object, beanElements);
			for(BeanElement be: beanElements){
				String value = oProperties.getProperty(be.getName());
				if(value != null){
					map.put(be.getName(), value);
				}
			}
		}
		PropertiesTableModel model = new PropertiesTableModel(map, beanElements);
		PropertiesDialog dialog = new PropertiesDialog("Bean properties", model, parameter.isEditable());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		dialog.setLocation(screenWidth / 4, screenHeight / 5);

		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		if(dialog.showAndWaitForApprove()){
			return BeanUtils.propertiesToObject(classType, map);
		}
		return object;
	}
	private static String[] getProeprties(ArrayList<BeanElement> beanElements){
		String[] properties = new String[beanElements.size()];
		for(int i = 0; i < beanElements.size(); i++){
			properties[i] = beanElements.get(i).getName();
		}
		return properties;
	}
	
	@Override
	public void setProviderConfig(String... args) {
	}
}