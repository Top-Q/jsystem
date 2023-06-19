/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import jsystem.framework.TestBeanClass;
import jsystem.framework.scenario.*;
import jsystem.runner.loader.LoadersManager;
import jsystem.treeui.utilities.MultiPropertiesDialog;
import jsystem.treeui.utilities.ParameterProviderListener;
import jsystem.utils.StringUtils;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.BeanUtils;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

public class ObjectArrayParameterProvider extends AbstractSerializingParameterProvider {

	private static Logger log = Logger.getLogger(ObjectArrayParameterProvider.class.getName());

	private List<ParameterProviderListener> listenersList = new ArrayList<ParameterProviderListener>();

	public ObjectArrayParameterProvider() {
	}

	@Override
	public String getAsString(Object o) {
		// if the input object is null or is of type String return
		if (o == null) {
			return null;
		}
		if (o instanceof String) {
			return (String) o;
		}
		if (!o.getClass().isArray()) {
			return null;
		}

		Object[] array = (Object[]) o;

		ArrayList<BeanElement> beanElements = BeanUtils.getBeans(o.getClass().getComponentType(), true, true,
				BeanUtils.getBasicTypes());

		// build properties object from the given object
		Properties properties = new Properties();
		for (int i = 0; i < array.length; i++) {
			for (BeanElement be : beanElements) {
				if (be.getGetMethod() == null) {
					continue;
				}
				try {
					Object value = be.getGetMethod().invoke(array[i], new Object[0]);
					if (value != null) {
						properties.setProperty(i + "." + be.getName(), StringUtils.advancedToString(value));
					}
				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to invoke the getter: " + be.getName(), e);
				}
			}
		}
		return propetiesToString(o.getClass().getComponentType().getName(), properties);
	}

	@Override
	public Object getFromString(String stringRepresentation) throws Exception {
		// if the input is null return null object
		if (stringRepresentation == null) {
			return null;
		}
		// first extract the class name
		int classEndIndex = stringRepresentation.indexOf(';');
		if (classEndIndex < 0) {
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
		ArrayList<Properties> splitProperties = splitProperties(properties);
		Object[] array = (Object[]) Array.newInstance(c, splitProperties.size());
		for (int i = 0; i < array.length; i++) {
			array[i] = BeanUtils.propertiesToObject(c, propertiesToMap(splitProperties.get(i)));
		}
		return array;
	}

	@Override
	public boolean isFieldEditable() {
		return false;
	}

	@Override
	public synchronized Object showUI(Component parent, Scenario currentScenario, RunnerTest rtest, Class<?> classType,
			Object object, Parameter parameter) throws Exception {
		if (!classType.isArray()) {
			throw new RuntimeException("ObjectArrayParameter must be of array type! current type is : " + classType);
		}
		ArrayList<BeanElement> beanElements = BeanUtils.getBeans(classType.getComponentType(), true, true,
				BeanUtils.getBasicTypes());

		ArrayList<LinkedHashMap<String, String>> multiMap = new ArrayList<LinkedHashMap<String, String>>();

		if (object != null) {
			Object[] array = null;
			try {
				array = (Object[]) object;
			} catch (ClassCastException e) {
				throw new RuntimeException("ObjectArrayParameter got wrong parameter! expected array type, got: "
						+ object.getClass() + ", with value: " + object);
			}

			for (int i = 0; i < array.length; i++) {
				Properties oProperties = BeanUtils.objectToProperties(array[i], beanElements);
				multiMap.add(propertiesToMapBeanOrder(oProperties, beanElements));
			}
		}
		// the user can specified using <code>TestBeanClass</code> annotation
		// new data model class.
		TestBeanClass tbc = classType.getComponentType().getAnnotation(TestBeanClass.class);
		BeanCellEditorModel beanCellEditorModel = null;
		if (tbc != null) {
			// create the new data model
			Class<?> modelClass = tbc.model();
			if (!modelClass.equals(UseDefaultDataModel.class)) {
				Constructor<?> cons = modelClass.getConstructor(beanElements.getClass(), multiMap.getClass());
				if (cons != null) {
					Object dataModel = cons.newInstance(beanElements, multiMap);
					if (dataModel instanceof BeanCellEditorModel) {
						beanCellEditorModel = (BeanCellEditorModel) dataModel;
					}
				}
			}
		}
		MultiPropertiesDialog dialog = new MultiPropertiesDialog(multiMap, "Bean properties", beanElements,
				propertiesToMapBeanOrder(
						BeanUtils.objectToProperties(classType.getComponentType().newInstance(), beanElements),
						beanElements), beanCellEditorModel, parameter.isEditable());
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setListeners(listenersList);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;
		dialog.setLocation(screenWidth / 4, screenHeight / 5);

		if (dialog.showAndWaitForApprove()) {
			Object[] array = (Object[]) Array.newInstance(classType.getComponentType(), multiMap.size());
			for (int i = 0; i < array.length; i++) {
				array[i] = BeanUtils.propertiesToObject(classType.getComponentType(), multiMap.get(i));
			}
			return array;
		}
		return object;
	}

	private static LinkedHashMap<String, String> propertiesToMapBeanOrder(Properties properties,
			ArrayList<BeanElement> elements) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (BeanElement be : elements) {
			String value = properties.getProperty(be.getName());
			if (value != null) {
				map.put(be.getName(), value);
			}
		}
		return map;
	}

	private static ArrayList<Properties> splitProperties(Properties properties) {
		HashMap<Integer, Properties> propertiesMap = new HashMap<Integer, Properties>();
		Enumeration<?> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement().toString();
			String value = properties.getProperty(key);
			String[] keyElements = key.split("\\.");
			int index = Integer.parseInt(keyElements[0]);
			Properties prop = propertiesMap.get(index);
			if (prop == null) {
				prop = new Properties();
				propertiesMap.put(index, prop);
			}
			prop.setProperty(keyElements[1], value);
		}
		ArrayList<Properties> toReturn = new ArrayList<Properties>();
		int i = 0;
		while (true) {
			Properties prop = propertiesMap.get(i);
			if (prop != null) {
				toReturn.add(prop);
				i++;
			} else {
				break;
			}
		}
		return toReturn;
	}

	@Override
	public void setProviderConfig(String... args) {
		for (String arg : args) {
			if (StringUtils.isEmpty(arg)) {
				continue;
			}
			if (arg.startsWith("listeners=")) {
				registerListeners(arg.replaceFirst("listeners=", ""));
			}
		}
	}

	/**
	 * This allows the user to add listeners that would receive events for any
	 * changes that occurs in the dialog. The listeners are specified using the @UseProvider
	 * annotation. <br>
	 * e.g: <br>
	 * 
	 * @UseProvider(provider =
	 *                       jsystem.extensions.paramproviders.ObjectArrayParameterProvider
	 *                       .class,config = {"listeners=org.jsystem.Listener"}) <br>
	 * 
	 *                       Multiple providers can be specified using the ';'
	 *                       delimiter
	 * 
	 * 
	 * @param listeners
	 */
	private void registerListeners(String listeners) {
		for (String listenerStr : listeners.split(";")) {
			ParameterProviderListener listener = null;
			try {
				listener = (ParameterProviderListener) LoadersManager.getInstance().getLoader().loadClass(listenerStr)
						.newInstance();
			} catch (Exception e) {
				log.log(Level.WARNING,
						"Failed to create instance of type " + ParameterProviderListener.class.getName(), e);
				continue;
			}
			listenersList.add(listener);

		}
	}

}