/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.paramproviders;

import jsystem.framework.common.CommonResources;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.ParameterProvider;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.utils.StringUtils;

import java.awt.*;

/**
 * @author golan.derazon
 */
public class StringArrayOptionsParameterProvider implements ParameterProvider {

	private static final String DELIMITER = CommonResources.DELIMITER;

	@Override
	public String getAsString(Object o) {
		if (o == null) {
			return "";
		}
		if (o instanceof String) {
			return o.toString();
		}
		if (!o.getClass().isArray()) {
			throw new RuntimeException(this.getClass().getName() + " must be of array type! current type is : " + o.getClass() + ", with value: " + o);
		}
		return StringUtils.objectArrayToString(DELIMITER, (Object[]) o);
	}

	@Override
	public Object getFromString(String stringRepresentation) {
		return StringUtils.split(stringRepresentation, DELIMITER);
	}

        @Override
        public Object showUI(Component parent, Scenario currentScenario, RunnerTest runnerTest, Class<?> classType,
                             Object object, Parameter parameter) {
            OptionsMultiSelectDialog dialog = new OptionsMultiSelectDialog();
            String selected = (object instanceof String) ? object.toString() :
                              StringUtils.objectArrayToString(CommonResources.DELIMITER, object);
            dialog.initDialog(parameter.getOptions(), selected);
            if (dialog.isOkay()) {
                return dialog.getSelectedOptions();
            } else {
                return selected;
            }
        }

	@Override
	public boolean isFieldEditable() {
		return false;
	}

	@Override
	public void setProviderConfig(String... args) {
	}

}
