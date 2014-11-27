package com.aqua.anttask.jsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.utils.beans.BeanUtils;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MacroInstance;

import com.aqua.anttask.jsystem.datadriven.CsvDataCollector;
import com.aqua.anttask.jsystem.datadriven.DataCollector;
import com.aqua.anttask.jsystem.datadriven.DataCollectorException;

public class JSystemDataDrivenTask extends PropertyReaderTask {

	private static final String DELIMITER = ";";

	static Logger log = Logger.getLogger(JSystemDataDrivenTask.class.getName());

	private String file;

	private String type;

	private String param;
	
	private String rows;

	private List<Map<String, Object>> data;

	private int itrerationNum = 0;

	public void execute() throws BuildException {

		if (!JSystemAntUtil.doesContainerHaveEnabledTests(getUuid())) {
			return;
		}

		final String collectorType = JSystemProperties.getInstance().getPreferenceOrDefault(
				FrameworkOptions.DATA_DRIVEN_COLLECTOR);
		DataCollector collector = BeanUtils.createInstanceFromClassName(collectorType, DataCollector.class);
		if (null == collector) {
			log.log(Level.WARNING, "Fail to init collector : " + collectorType);
			collector = new CsvDataCollector();
		}
		type = collector.getName();
		try {
			file = getParameterFromProperties("File", "");
			param = getParameterFromProperties("Parameters", "");
			rows = getParameterFromProperties("Rows", "");
			data = collector.collect(new File(file), param);
			data = cleanUnusedRows(data, rows.split(","));
		} catch (DataCollectorException e) {
			log.log(Level.WARNING, "Failed to collect data due to " + e.getMessage());
			return;
		}
		if (data == null || data.size() == 0) {
			log.log(Level.INFO, "Invalid data");
			return;
		}
		convertDataToLoop();
		super.execute();
	}
	
	private List<Map<String, Object>> cleanUnusedRows(List<Map<String, Object>> rows, String[] rowsToUse) {
		if (rowsToUse == null || rowsToUse.length == 0) {
			return rows;
		}
		List<Map<String, Object>> relevantRows = new ArrayList<Map<String, Object>>();
		for (String rowToAdd : rowsToUse) {
			if (rowToAdd.trim().contains("-")) {
				int min = Integer.valueOf(rowToAdd.substring(0, rowToAdd.indexOf("-")).trim());
				int max = Integer.valueOf(rowToAdd.substring(rowToAdd.lastIndexOf("-") + 1).trim());
				for (int i = min - 1; i < max; i++) {
					if (i >= 0 && i < rows.size()) {
						relevantRows.add(rows.get(i));
					}
				}
			} else {
				int row = Integer.valueOf(rowToAdd) - 1;
				if (row >= 0 && row < rows.size()) {
					relevantRows.add(rows.get(row));
				}
			}
		}

		return relevantRows;
	}

	private void convertDataToLoop() {
		final String paramName = data.get(0).keySet().toArray(new String[] {})[0];
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> dataRow : data) {
			sb.append(DELIMITER).append(dataRow.get(paramName));
		}

		// Actually, we not using this parameter, but we need in order for the
		// for task to work.
		setParam("unusedparam");
		// And, we are also not really using the list values, only pass it to
		// the for task in order to create the number of iterations required.
		setList(sb.toString().replaceFirst(DELIMITER, ""));
	}

	@Override
	protected void doSequentialIteration(String val) {
		MacroInstance instance = new MacroInstance();
		instance.setProject(getProject());
		instance.setOwningTarget(getOwningTarget());
		instance.setMacroDef(getMacroDef());
		Map<String, Object> dataRow = data.get(itrerationNum++);
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
