package jsystem.extensions.paramproviders;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import jsystem.framework.TestBeanClass;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.UseDefaultDataModel;
import jsystem.treeui.utilities.ParameterProviderListener;
import jsystem.utils.beans.BeanElement;
import jsystem.utils.beans.BeanUtils;

public class CsvObjectArrayParameterProvider extends ObjectArrayParameterProvider {
	private List<ParameterProviderListener> listenersList = new ArrayList<ParameterProviderListener>();

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
		CsvPropertiesDialog dialog = new CsvPropertiesDialog(multiMap, "Bean properties", beanElements,
				propertiesToMapBeanOrder(
						BeanUtils.objectToProperties(classType.getComponentType().newInstance(), beanElements),
						beanElements), beanCellEditorModel, parameter.isEditable());
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.setListeners(listenersList);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenHeight = screenSize.height;
		int screenWidth = screenSize.width;

		dialog.setBounds(new Rectangle(0, screenHeight / 10, screenWidth, screenHeight * 3 / 4));
		
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
	
}
