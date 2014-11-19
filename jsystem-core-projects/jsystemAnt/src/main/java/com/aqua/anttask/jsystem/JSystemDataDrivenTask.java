package com.aqua.anttask.jsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MacroInstance;

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
		if (type.equals("Excel")) {
			collector = new ExcelDataCollector();
		} else if (type.equals("Csv")) {
			collector = new CsvDataCollector();
		} else if (type.equals("Database")) {
			collector = new DatabaseDataCollector();
		} else {
			log.log(Level.WARNING, "Unknown data driven type");
			return;
		}
		try {
			data = collector.collect();
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

	private void convertDataToLoop() {
		final String paramName = data.get(0).keySet().toArray(new String[] {})[0];
		StringBuilder sb = new StringBuilder();
		for (Map<String, Object> dataRow : data) {
			sb.append(DELIMITER).append(dataRow.get(paramName));
		}

		// Actually, we not using this parameter, but we need in order for the
		// for task to work.
		setParam(paramName);
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

	class CsvDataCollector implements DataCollector {

		private static final String SEPARATION_STRING = ",";

		@Override
		public List<Map<String, Object>> collect() throws DataCollectorException {
			file = getParameterFromProperties("File", "");
			final File csvFile = new File(file);
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			Scanner lineScanner = null;
			try {
				lineScanner = new Scanner(csvFile);
				List<String> titles = null;
				while (lineScanner.hasNextLine()) {
					List<String> cells = new ArrayList<String>();
					Scanner cellScanner = null;
					try {
						cellScanner = new Scanner(lineScanner.nextLine());
						cellScanner.useDelimiter(SEPARATION_STRING);
						while (cellScanner.hasNext()) {
							cells.add(cellScanner.next());
						}

					} finally {
						if (cellScanner != null) {
							cellScanner.close();
						}
					}
					if (cells.size() == 0) {
						// Seems to be an empty line. Let's continue to the next
						// line
						continue;
					}
					if (null == titles) {
						// This is the first line of the CSV, so it is the
						// titles
						titles = new ArrayList<String>();
						titles.addAll(cells);
						continue;
					}
					Map<String, Object> dataRow = new HashMap<String, Object>();
					if (cells.size() != titles.size()) {
						log.warning("Titles number is " + titles.size()
								+ " while the cells number in one of the rows is " + cells.size());
					}
					// We would iterate over the smaller list size to avoid out
					// of bounds
					for (int i = 0; i < (titles.size() <= cells.size() ? titles.size() : cells.size()); i++) {
						dataRow.put(titles.get(i), cells.get(i));
					}
					data.add(dataRow);
				}
			} catch (FileNotFoundException e) {
				throw new DataCollectorException("Csv file " + file + " is not exist", e);
			} finally {
				if (lineScanner != null) {
					lineScanner.close();
				}
			}
			return data;
		}

	}

	class ExcelDataCollector implements DataCollector {

		@Override
		public List<Map<String, Object>> collect() throws DataCollectorException {
			throw new DataCollectorException("Excel collector is not yet implemented");
		}

	}

	class DatabaseDataCollector implements DataCollector {

		@Override
		public List<Map<String, Object>> collect() throws DataCollectorException {
			param = getParameterFromProperties("Parameters", "");
			String[] iterations = param.split(",");
			List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
			for (String iteration : iterations) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("index", iteration);
				response.add(map);
			}
			return response;
		}
	}

	interface DataCollector {
		List<Map<String, Object>> collect() throws DataCollectorException;
	}

	class DataCollectorException extends Exception {

		private static final long serialVersionUID = 1L;

		public DataCollectorException(String message) {
			super(message);
		}

		public DataCollectorException(String message, Throwable t) {
			super(message, t);
		}

	}

}
