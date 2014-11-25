package com.aqua.anttask.jsystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
		setParam(paramName.replaceAll("_", ""));
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
			List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
			final String tablePrefix;
			final String inputKey;
			final String[] rowsToExecute;

			file = getParameterFromProperties("File", "");
			param = getParameterFromProperties("Parameters", "");
			String[] dataDrivenParameters = param.split(";");
			// The parameter convention is as such: <table prefix>;<input key>;<rows to execute>
			// when the first two are must and the rest are optional.
			if (dataDrivenParameters == null || (dataDrivenParameters.length < 2 || dataDrivenParameters.length > 4)) {
				throw new DataCollectorException("Wrong number of parameters.");
			} else {
				tablePrefix = fetchTablePrefix(dataDrivenParameters);
				inputKey = fetchInputKey(dataDrivenParameters);
				rowsToExecute = fetchRowsToExecute(dataDrivenParameters);
				TableDataExtractor tableDataExtractor;
				// if we do not provide database properties file, the TableDataExtractor
				// class will use a default database.properties file
				// otherwise it will use the file we provided
				if (file == null || file.isEmpty()) {
					tableDataExtractor = new TableDataExtractor(tablePrefix);
				} else {
					tableDataExtractor = new TableDataExtractor(tablePrefix, file);
				}
				try {
					data = tableDataExtractor.extractData(inputKey);
				} catch (Exception e) {
					throw new DataCollectorException("Failed extracting data from the database: " + e.getMessage());
				}
				try {
					data = cleanUnusedRows(data, rowsToExecute);
				} catch (Exception e) {
					throw new DataCollectorException("Failed removing unwanted rows: " + e.getMessage());
				}
			}
			return data;
		}

		/**
		 * Method assumes rowsToUse contain either integer numbers in string
		 * representation or range separated by "-" character. Also assumes
		 * there are no repetitive values, like 1-8,5,...(notice 1-8 already
		 * includes 5, thus 5 will be counted twice)
		 * 
		 * @param rows
		 *            - the original list of rows fetched previously from the
		 *            database
		 * @param rowsToUse
		 *            - Strings which represent the relevant rows. See above
		 *            description.
		 * @return new List of rows which contains only the rows presented in
		 *         rowsToUse. Or the original rows list if rowsToUse is null or
		 *         empty.
		 * @throws Exception
		 */
		private List<Map<String, Object>> cleanUnusedRows(List<Map<String, Object>> rows, String[] rowsToUse)
				throws Exception {
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

		private String fetchTablePrefix(final String[] dataDrivenParameters) {
			if (dataDrivenParameters.length > 0) {
				return dataDrivenParameters[0].trim();
			}
			return null;
		}

		private String fetchInputKey(final String[] dataDrivenParameters) {
			if (dataDrivenParameters.length > 1) {
				return dataDrivenParameters[1].trim();
			}
			return null;
		}

		// the convention is the rows separated by ","
		private String[] fetchRowsToExecute(final String[] dataDrivenParameters) {
			if (dataDrivenParameters.length > 2) {
				return dataDrivenParameters[2].split(",");
			}
			return null;
		}
	}

	class TableDataExtractor {

		public TableDataExtractor(final String tableName) {
			this.TABLE_NAME = tableName;
			this.DB_PROPERTIES_FILE = "C:/TEMP/database.properties";// set the
																	// default
																	// database
																	// properties
																	// filepath here
		}

		public TableDataExtractor(final String tableName, final String propertiesFile) {
			this.TABLE_NAME = tableName;
			this.DB_PROPERTIES_FILE = propertiesFile;
		}

		private final String TABLE_NAME;
		private final String DB_PROPERTIES_FILE;

		private Connection getConnection() throws Exception {
			Properties props = new Properties();
			FileInputStream fis = null;
			Connection con = null;
			try {
				fis = new FileInputStream(DB_PROPERTIES_FILE);
				props.load(fis);
				Class.forName(props.getProperty("DB_DRIVER_CLASS"));
				con = DriverManager.getConnection(props.getProperty("DB_URL"), props.getProperty("DB_USERNAME"),
						props.getProperty("DB_PASSWORD"));
			} catch (IOException | ClassNotFoundException | SQLException e) {
				throw new Exception("Cannot connect to the database.");
			}
			return con;
		}

		public List<Map<String, Object>> extractData(final String inputKey) throws Exception {
			final String QUERY = "SELECT * FROM " + TABLE_NAME + "_input WHERE jira_key = ?";
			List<Map<String, Object>> extractedData = new ArrayList<Map<String, Object>>();
			PreparedStatement preparedStatement = null;
			Connection dbConnection = null;
			try {
				dbConnection = getConnection();
				preparedStatement = dbConnection.prepareStatement(QUERY);
				preparedStatement.setString(1, inputKey);

				ResultSet resultSet = preparedStatement.executeQuery();
				ResultSetMetaData metaData = resultSet.getMetaData();
				List<String> titles = new ArrayList<String>();
				int count = metaData.getColumnCount();
				// get the column titles of the table
				for (int i = 1; i <= count; i++) {
					titles.add(metaData.getColumnName(i));
				}

				while (resultSet.next()) {
					Map<String, Object> dataRow = new HashMap<String, Object>();
					// map title to value for current row
					for (String title : titles) {
						Object data = resultSet.getObject(title);
						dataRow.put(title, data);
					}
					// add map to list and proceed to next row if exists
					extractedData.add(dataRow);
				}

			} catch (SQLException e) {
				throw new Exception(e.getMessage());
			} finally {
				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				}

				if (dbConnection != null) {
					try {
						dbConnection.close();
					} catch (SQLException e) {
						throw new Exception(e.getMessage());
					}
				}
			}
			return extractedData;
		}
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
