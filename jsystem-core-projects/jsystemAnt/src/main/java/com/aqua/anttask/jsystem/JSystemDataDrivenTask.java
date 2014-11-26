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
import java.util.ArrayList;
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
			final String[] idsToRun;

			file = getParameterFromProperties("File", "");
			param = getParameterFromProperties("Parameters", "");
			String[] dataDrivenParameters = param.split(DELIMITER);
			// The parameter convention is as such: 
			//<table prefix>;<jira input key>;<ids to execute>
			// when the first two are must and the ids are optional.
			if (dataDrivenParameters == null || (dataDrivenParameters.length < 2 || dataDrivenParameters.length > 3)) {
				throw new DataCollectorException("Wrong number of parameters.");
			} else {
				tablePrefix = fetchTablePrefix(dataDrivenParameters);
				inputKey = fetchInputKey(dataDrivenParameters);
				idsToRun = fetchIdsToExecute(dataDrivenParameters);
				TableDataExtractor tableDataExtractor;
				// if we do not provide database properties file, the
				// TableDataExtractor class will use a default database.properties file
				// otherwise it will use the file we provided
				if (file == null || file.isEmpty()) {
					tableDataExtractor = new TableDataExtractor(tablePrefix);
				} else {
					tableDataExtractor = new TableDataExtractor(tablePrefix, file);
				}
				try {
					data = tableDataExtractor.extractData(inputKey, idsToRun);
				} catch (SQLDataExtractException e) {
					throw new DataCollectorException("Failed extracting data from the database. " + e.getMessage());
				}
			}
			return data;
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

		// the convention is the ids and ids range are separated by ","
		private String[] fetchIdsToExecute(final String[] dataDrivenParameters) {
			if (dataDrivenParameters.length > 2) {
				return dataDrivenParameters[2].split(",");
			}
			return null;
		}
	}
	
	/**
	 * The following class assumes properties file appears in a default location
	 * or is provided as a parameter in the constructor. The file includes the following
	 * properties: DB_DRIVER_CLASS, DB_URL, DB_USERNAME, DB_PASSWORD
	 */
	class TableDataExtractor {

		public TableDataExtractor(final String tableName) {
			this.TABLE_NAME = tableName;
			this.DB_PROPERTIES_FILE = "database.properties";// set the
																	// default
																	// database
																	// properties
																	// filepath
																	// here
		}
		
		public TableDataExtractor(final String tableName, final String propertiesFile) {
			this.TABLE_NAME = tableName;
			this.DB_PROPERTIES_FILE = propertiesFile;
		}

		private final String TABLE_NAME;
		private final String DB_PROPERTIES_FILE;
		
		private Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
			Properties props = new Properties();
			FileInputStream fis = null;
			Connection con = null;
			fis = new FileInputStream(DB_PROPERTIES_FILE);
			props.load(fis);
			Class.forName(props.getProperty("DB_DRIVER_CLASS"));
			con = DriverManager.getConnection(props.getProperty("DB_URL"), props.getProperty("DB_USERNAME"),
					props.getProperty("DB_PASSWORD"));
			return con;
		}
		
		/**
		 * Method prepares a query to execute according to given parameters
		 * connects to the database with the data given in properties file
		 * and extracts relevant information
		 * @param inputKey - jira input key
		 * @param IdsToExecute - which rows asssociated to the jira key to execute
		 * @return List of maps between column title and value for each row of the result set
		 * @throws SQLDataExtractException
		 */
		public List<Map<String, Object>> extractData(final String inputKey, final String[] IdsToExecute)
				throws SQLDataExtractException {
			final String QUERY = getQuery(IdsToExecute);
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
			} catch (IOException e) {
				throw new SQLDataExtractException("Error loading properties file. " + e.getMessage());
			} catch (ClassNotFoundException e) {
				throw new SQLDataExtractException("Driver class not found. " + e.getMessage());
			} catch (SQLException e) {
				throw new SQLDataExtractException("SQL Error: " + e.getMessage());
			} finally {
				if (preparedStatement != null) {
					try {
						preparedStatement.close();
					} catch (SQLException e) {
						throw new SQLDataExtractException("Error closing sql statement. " + e.getMessage());
					}
				}
				if (dbConnection != null) {
					try {
						dbConnection.close();
					} catch (SQLException e) {
						throw new SQLDataExtractException("Error closing database connection. " + e.getMessage());
					}
				}
			}
			return extractedData;
		}
		
		/**
		 * Method assumes ids contain either integer numbers in string
		 * representation or range separated by "-" character. 
		 * @param ids - Strings which represent the ids to execute. See above
		 *            description.
		 * @return query to execute
		 */
		private String getQuery(String[] ids) {
			StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_NAME + "_input WHERE jira_key = ?");
			if (ids != null && ids.length > 0) {
				query.append(" AND id IN (");
				for (String id : ids) {
					if (id.trim().contains("-")) {
						int min = Integer.valueOf(id.substring(0, id.indexOf("-")).trim());
						int max = Integer.valueOf(id.substring(id.lastIndexOf("-") + 1).trim());
						for (int i = min; i <= max; i++) {
							query.append(String.valueOf(i));
							query.append(",");
						}
					} else {
						query.append(id.trim());
						query.append(",");
					}
				}
				query.replace(query.lastIndexOf(","), query.lastIndexOf(",") + 1, ")" );
			}
			return query.toString();
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

class SQLDataExtractException extends Exception {

	private static final long serialVersionUID = 1L;

	public SQLDataExtractException(String message) {
		super(message);
	}

	public SQLDataExtractException(String message, Throwable t) {
		super(message, t);
	}

}
