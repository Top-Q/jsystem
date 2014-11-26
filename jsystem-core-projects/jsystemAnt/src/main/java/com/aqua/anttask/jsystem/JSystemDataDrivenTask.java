package com.aqua.anttask.jsystem;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.runner.loader.LoadersManager;

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

	private List<Map<String, Object>> data;

	private int itrerationNum = 0;

	public void execute() throws BuildException {

		if (!JSystemAntUtil.doesContainerHaveEnabledTests(getUuid())) {
			return;
		}

		type = getParameterFromProperties("Type", "Csv");
		DataCollector collector = null;

		collector = createCollectorInstance();
		try {
			file = getParameterFromProperties("File", "");
			param = getParameterFromProperties("Parameter", "");
			data = collector.collect(new File(file), param);
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

	private DataCollector createCollectorInstance() {
		String dataCollectorName = JSystemProperties.getInstance()
				.getPreferenceOrDefault(FrameworkOptions.DATA_DRIVEN_COLLECTOR);
		DataCollector collector = null;
		try {
			Class<?> dataCollectorClass = LoadersManager.getInstance().getLoader().loadClass(dataCollectorName);
			if (dataCollectorClass != null) {
				Object instance = dataCollectorClass.newInstance();
				if (instance instanceof DataCollector) {
					log.log(Level.INFO, "Reports publisher : " + dataCollectorName + " Was loaded.");
					collector = (DataCollector) instance;
				}
			}
		} catch (Exception e) {
			log.log(Level.WARNING, "Fail to init collector : " + dataCollectorName, e);
			collector = new CsvDataCollector();
		}

		return collector;
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
