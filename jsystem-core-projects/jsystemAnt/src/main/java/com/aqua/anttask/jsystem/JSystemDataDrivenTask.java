package com.aqua.anttask.jsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.Parameter.ParameterType;
import jsystem.framework.scenario.ParametersManager;
import jsystem.framework.scenario.flow_control.datadriven.CsvDataProvider;
import jsystem.framework.scenario.flow_control.datadriven.DataCollectorException;
import jsystem.framework.scenario.flow_control.datadriven.DataProvider;
import jsystem.utils.StringUtils;
import jsystem.utils.beans.BeanUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MacroInstance;

public class JSystemDataDrivenTask extends PropertyReaderTask {

	private static final String DELIMITER = ";";

	static Logger log = Logger.getLogger(JSystemDataDrivenTask.class.getName());

	private String type;

	private String file;

	private String param;

	private String lineIndexes;

	private boolean shuffle;

	private long shuffleSeed;

	private boolean reverseOrder;

	private List<Map<String, Object>> data;

	private int iterationNum = 0;

	public void execute() throws BuildException {

		if (!JSystemAntUtil.doesContainerHaveEnabledTests(getUuid())) {
			return;
		}

		loadParameters();
		final DataProvider provider = initProvider();
		try {
			// in case file is a reference, if not the file name will be as entered by the user - nir
			String fileName = (String) ParametersManager.replaceReferenceWithValue(file, ParameterType.FILE);
			data = provider.provide(new File(file), param);
		} catch (Exception e) {
			log.log(Level.WARNING, "Failed to collect data due to " + e.getMessage());
			return;
		}
		if (data == null || data.size() == 0) {
			log.log(Level.INFO, "Invalid data");
			return;
		}
		filterData();
		convertDataToLoop();
		if (shuffle) {
			shuffleData();
		}
		if (reverseOrder) {
			Collections.reverse(data);
		}
		super.execute();
	}

	private DataProvider initProvider() {
		if (StringUtils.isEmpty(type)) {
			log.log(Level.WARNING, "No data provider type was specified. Rolling back to CSV provider");
			return new CsvDataProvider();
		}
		final String allProviderTypes = JSystemProperties.getInstance().getPreferenceOrDefault(
				FrameworkOptions.DATA_PROVIDER_CLASSES);
		if (StringUtils.isEmpty(allProviderTypes)) {
			log.log(Level.WARNING, "No providers were specified in the framework options. Rolling back to CSV provider");
			return new CsvDataProvider();
		}
		List<DataProvider> dataProvidersList = new ArrayList<DataProvider>();
		for (String providerType : allProviderTypes.split(DELIMITER)) {
			final DataProvider provider = BeanUtils.createInstanceFromClassName(providerType, DataProvider.class);
			if (provider != null) {
				dataProvidersList.add(provider);
			}
		}
		for (DataProvider provider : dataProvidersList) {
			if (provider.getName() != null && provider.getName().trim().equals(type.trim())) {
				return provider;
			}
		}
		log.log(Level.WARNING, "No provider was found with name " + type + ". Rolling back to CSV provider");
		return new CsvDataProvider();
	}

	private void shuffleData() {
		if (shuffleSeed <= 0) {
			Collections.shuffle(data);
		} else {
			Collections.shuffle(data, new Random(shuffleSeed));
		}
	}

	private void loadParameters() {
		type = getParameterFromProperties("Type", new CsvDataProvider().getName());
		file = getParameterFromProperties("File", "");
		param = getParameterFromProperties("Parameter", "");
		try {
			param = ParametersManager.replaceAllReferenceValues(param, ParameterType.STRING);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error trying to replace reference parameters for input: " + param, e);
		}
		lineIndexes = getParameterFromProperties("LineIndexes", "");
		shuffle = Boolean.valueOf(getParameterFromProperties("Shuffle", "false"));
		shuffleSeed = Long.parseLong(getParameterFromProperties("ShuffleSeed", "0"));
		reverseOrder = Boolean.parseBoolean(getParameterFromProperties("ReverseOrder", "false"));
	}

	/**
	 * Change the data received from the collector to include only the lines
	 * that are specified in the line indexes parameter
	 */
	private void filterData() {
		if (null == lineIndexes || lineIndexes.isEmpty()) {
			return;
		}
		final List<Integer> requiredNumbers = convertStringOfNumbersToList(lineIndexes.trim());
		if (null == requiredNumbers || requiredNumbers.size() == 0) {
			return;
		}
		final List<Map<String, Object>> filteredData = new ArrayList<Map<String, Object>>();

		for (int lineNumber : requiredNumbers) {
			// Notice that the line indexes are one-based
			if (data.size() < lineNumber) {
				continue;
			}
			filteredData.add(data.get(lineNumber - 1));
		}
		if (filteredData.size() > 0) {
			// Only if there is something in the filtered data we will replace
			// the data with the filtered one. We do this to avoid exception at
			// run time when trying to iterate over empty list
			data = filteredData;
		}

	}

	private void convertDataToLoop() {
		final String paramName = data.get(0).keySet().toArray(new String[] {})[0];
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> dataRow : data) {
			sb.append(DELIMITER).append(dataRow.get(paramName));
		}

		// Actually, we are not using this parameter, but we need it in order
		// for the the task to work.
		setParam("unusedparam");
		// And, we are also not really using the list values, only pass it to
		// the for task in order to create the number of iterations required.
		setList(sb.toString().replaceFirst(DELIMITER, ""));
	}

	private static List<Integer> convertStringOfNumbersToList(final String numbers) {
		final Set<Integer> result = new HashSet<Integer>();
		for (String numberStr : numbers.split(",")) {
			try {
				if (numberStr.contains("-")) {
					final String rangeNumbersStr[] = numberStr.split("-");
					for (int i = Integer.parseInt(rangeNumbersStr[0]); i <= Integer.parseInt(rangeNumbersStr[1]); i++) {
						if (i > 0) {
							result.add(i);
						}
					}

				} else {
					int tempInt = Integer.parseInt(numberStr);
					if (tempInt > 0) {
						result.add(Integer.parseInt(numberStr));
					}
				}

			} catch (NumberFormatException e) {
				continue;
			}
		}
		final List<Integer> sortedResult = new ArrayList<Integer>(result);
		Collections.sort(sortedResult);
		return sortedResult;
	}

	@Override
	protected void doSequentialIteration(String val) {
		MacroInstance instance = new MacroInstance();
		instance.setProject(getProject());
		instance.setOwningTarget(getOwningTarget());
		instance.setMacroDef(getMacroDef());
		Map<String, Object> dataRow = data.get(iterationNum++);
		for (String key : dataRow.keySet()) {
			if (dataRow.get(key) == null) {
				continue;
			}
			getProject().setProperty(key, dataRow.get(key).toString());
		}
		// This parameter is not really used but we need to pass it to the for
		// loop.
		instance.setDynamicAttribute(getParam().toLowerCase(), val);
		instance.execute();
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getLineIndexes() {
		return lineIndexes;
	}

	public void setLineIndexes(String lineIndexes) {
		this.lineIndexes = lineIndexes;
	}

}
