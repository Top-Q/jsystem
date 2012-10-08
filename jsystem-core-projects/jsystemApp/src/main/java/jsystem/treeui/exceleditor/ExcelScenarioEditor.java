/*
 * Created on 01/06/2006
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */

package jsystem.treeui.exceleditor;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.framework.scenario.JTest;
import jsystem.framework.scenario.Parameter;
import jsystem.framework.scenario.RunnerFixture;
import jsystem.framework.scenario.RunnerTest;
import jsystem.framework.scenario.Scenario;
import jsystem.framework.scenario.ScenarioEditor;
import jsystem.framework.scenario.ScenariosManager;
import jsystem.runner.ErrorLevel;
import jsystem.treeui.error.ErrorPanel;
import jsystem.utils.FileUtils;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.aqua.excel.ExcelFile;

public class ExcelScenarioEditor implements ScenarioEditor {

	public void executeSenarioEditor(Scenario scenario) throws Exception {
		File file = File.createTempFile(scenario.getName().replace(
				'\\', '_').replace("/", "_"), ".xls");
		ExcelFile excel = ExcelFile.getInstance(file.getAbsolutePath(), false,
				false);

		Vector<JTest> s = scenario.getRootTests();

		String excelCommand = JSystemProperties.getInstance().getPreference(
				FrameworkOptions.EXCEL_COMMAND);

		String[] excelCommands = null;
		String[] commands = null;

		if (excelCommand != null) {
			excelCommands = excelCommand.split(" ");
			commands = new String[excelCommands.length + 1];
			System.arraycopy(excelCommands, 0, commands, 0,
					excelCommands.length);
		}

		String lastTestClass = null;
		String lastTestMethod = null;

		if (s.size() == 0) {
			excel.addRow(getHeader(null, null), ExcelFile.FORMAT_HEADER);
		} else {
			for (int i = 0; i < s.size(); i++) {
				JTest jtest = s.elementAt(i);
				if (jtest instanceof RunnerTest) {
					if (jtest instanceof RunnerFixture) {
						RunnerFixture fixture = (RunnerFixture) jtest;
						Parameter[] parameters = fixture.getParameters();
						Properties otherFields = fixture.getAllXmlFields();

						if (fixture.getClassName().equals(lastTestClass)
								&& fixture.getMethodName().equals(
										lastTestMethod)) {
						} else {
							if (i != 0) {
								excel.addRow(new String[] { "" },
										ExcelFile.FORMAT_1);
							}
							excel.addRow(getFixtureHeader(parameters,
									otherFields), ExcelFile.FORMAT_HEADER);
						}
						excel.addRow(getRow(fixture.getClassName(),fixture.getMethodName(), parameters,
								otherFields), ExcelFile.FORMAT_1);

					} else {
						RunnerTest test = (RunnerTest) jtest;
						Parameter[] parameters = test.getParameters();
						Properties otherFields = test.getAllXmlFields();

						if (test.getClassName().equals(lastTestClass)
								&& test.getMethodName().equals(lastTestMethod)) {
						} else {
							if (i != 0) {
								excel.addRow(new String[] { "" },
										ExcelFile.FORMAT_1);
							}
							excel.addRow(getHeader(parameters, otherFields),
									ExcelFile.FORMAT_HEADER);
						}
						lastTestClass = test.getClassName();
						lastTestMethod = test.getMethodName();
						excel.addRow(getRow(test.getClassName(), test
								.getMethodName(), parameters, otherFields),
								ExcelFile.FORMAT_1);
					}
				} else if (jtest instanceof Scenario) {
					Scenario sen = (Scenario) jtest;
					excel.addRow(new String[] { "" }, ExcelFile.FORMAT_1);
					excel.addRow(new String[] { "Scenario",
							sen.getName() }, ExcelFile.FORMAT_HEADER);
				} else {
					ErrorPanel.showErrorDialog("Editing Scenario which includes flow control elements is currently not supported.", "Try editing a scenario without a flow element", ErrorLevel.Info);
					return;

				}
			}
		}

		Command command = new Command();
		String osName = System.getProperty("os.name");

		if (commands != null) {
			commands[commands.length - 1] = file.getAbsolutePath();
			command.setCmd(commands);
		} else {
			command.setCmd(new String[] { "cmd.exe", "/C",
					"\"" + file.getAbsolutePath() + "\"" });

			if (osName.toLowerCase().startsWith("windows")) {
				command.setCmd(new String[] { "cmd.exe", "/C",
						"\"" + file.getAbsolutePath() + "\"" });
			} else {

				/**
				 * support for Linux
				 */
				String location = JSystemProperties.getInstance()
						.getPreference(
								FrameworkOptions.SCENARIO_EDITOR_LOCATION);
				if (location == null) {
					location = "/usr/bin/";
				}
				String app = JSystemProperties.getInstance().getPreference(
						FrameworkOptions.SCENARIO_EDITOR_APP);

				if (app == null) {
					app = "oocalc";
				}

				command.setCmd(new String[] { location + app,
						file.getAbsolutePath() });
			}
		}

		try {
			Execute.execute(command, true); //* create the excel file
		} catch (Exception e) {
			if (osName.toLowerCase().startsWith("linux")) {
				String message = "Fail to open scenario editor. \n\n"
						+ "Notice that you are working in Linux environment.\n"
						+ "Try to update the scneario editor location and the \n"
						+ "scenario editor application name at the Jsystem.properties.\n"
						+ "For Example: scenario.editor.location = /usr/bin/ and  \n"
						+ "scenario.editor.app = oocalc\n\n";

				throw new Exception(message, e);
			} else {
				throw e;
			}
		}

		while (!FileUtils.winRename(file.getAbsolutePath(), file.getName())) {
			Thread.sleep(2000);
		}

		loadScenario(scenario, excel, JTest.fieldNum);
		scenario.update();
		file.delete();
	}

	private String[] getHeader(Parameter[] parameters, Properties otherFields) {
		int paramSize = 0;
		int fieldsSize = 0;
		if (parameters != null) {
			paramSize = parameters.length;
		}

		if (otherFields != null) {
			fieldsSize = otherFields.size();
		}

		String[] header = new String[2 + fieldsSize + paramSize];

		header[0] = "Class";
		header[1] = "Method";

		if (fieldsSize > 0) {
			String key;
			Enumeration<Object> fieldsEnum = otherFields.keys();
			for (int i = 2; i < 2 + fieldsSize; i++) {
				key = (String) fieldsEnum.nextElement();
				header[i] = key;
			}
		}

		/**
		 * sort parameter array before adding to excel row.
		 */
		if (parameters != null) {
			Arrays.sort(parameters, Parameter.ParameterNameComparator);
		}

		int n = fieldsSize + 2;
		for (int i = n; i < n + paramSize; i++) {
			header[i] = parameters[i - n].getName();
		}
		return header;
	}
	/**
	 * returns a header for a fixture in the scenario
	 * @param parameters
	 * @param otherFields
	 * @return
	 */
	private String[] getFixtureHeader(Parameter[] parameters, Properties otherFields) {
		int paramSize = 0;
		int fieldsSize = 0;
		if (parameters != null) {
			paramSize = parameters.length;
		}

		if (otherFields != null) {
			fieldsSize = otherFields.size();
		}

		String[] header = new String[2 + fieldsSize + paramSize];

		header[0] = "Fixture";
		header[1] = "Method";

		if (fieldsSize > 0) {
			String key;
			Enumeration<Object> fieldsEnum = otherFields.keys();
			for (int i = 2; i < 2 + fieldsSize; i++) {
				key = (String) fieldsEnum.nextElement();
				header[i] = key;
			}
		}

		/**
		 * sort parameter array before adding to excel row.
		 */
		if (parameters != null) {
			Arrays.sort(parameters, Parameter.ParameterNameComparator);
		}

		int n = fieldsSize + 2;
		for (int i = n; i < n + paramSize; i++) {
			header[i] = parameters[i - n].getName();
		}
		return header;
	}


	private void loadScenario(Scenario scenario, ExcelFile excel, int fieldNum)
			throws Exception {

		scenario.cleanAll();
		HSSFSheet sheet = excel.getSheet();
		String[] keys = null;
		/**
		 * in order to store the header row 
		 */
		String lastClassName="";
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			String className = getCellValue(row.getCell((short) 0));
			String methodName = getCellValue(row.getCell((short) 1));
			
			if (className == null || className.equals("")) {
				keys = null;
				continue;
			}

			if (methodName == null || methodName.equals("")) {
				keys = null;
				continue;
			}

			if (className.equals("Class")) { // found header
				// read keys
				lastClassName=className;
				int rowSize = row.getLastCellNum() + 1;
				if (rowSize < 2) {
					rowSize = 2;
				}

				keys = new String[rowSize - 2];
				for (int j = 2; j < rowSize; j++) {
					keys[j - 2] = getCellValue(row.getCell((short) j));
				}
			} else if (className.equals("Scenario")) {
				lastClassName=className;
				Scenario scen = ScenariosManager.getInstance().getScenario(
						methodName);
				ScenariosManager.getInstance().setCurrentScenario(scen);
				scenario.addTest(scen);
				ScenariosManager.getInstance().setCurrentScenario(scenario);
			} else if (className.equals("Fixture")) {
				lastClassName=className;
				int rowSize = row.getLastCellNum() + 1;
				if (rowSize < 1) {
					rowSize = 1;
				}

				keys = new String[rowSize - 1];
				for (int j = 1; j < rowSize; j++) {
					keys[j - 1] = getCellValue(row.getCell((short) (j+1)));
				}
			} else { // read values
				if (keys == null) {
					continue;
				}
				if (lastClassName.equalsIgnoreCase("fixture")) {
					RunnerFixture rf = new RunnerFixture (className);
					if (keys.length > 0) {
						Properties fields = new Properties();
						for (int j = 0; j < fieldNum && keys.length > j; j++) {
							String value = getCellValue(row
									.getCell((short) (j + 2)));
							if (value == null || keys[j] == null) {
								continue;
							}
							fields.setProperty(keys[j], value);
						}
						rf.setXmlFields(fields);
						Properties p = new Properties();
						for (int j = 0; j < keys.length; j++) {
							String value = getCellValue(row
									.getCell((short) (j + 2)));
							if (value == null || keys[j] == null) {
								continue;
							}
							try {
								Double.parseDouble(value);
								if (value.endsWith(".0")) {
									value = value.substring(0,
											value.length() - 2);
								}
							} catch (Throwable t) {

							}
							p.setProperty(keys[j], value);
						}
						rf.setProperties(p);
					}
					scenario.addTest(rf);
				} else {
					RunnerTest rt = new RunnerTest(className,methodName);
					if (keys.length > 0) {
						Properties fields = new Properties();
						for (int j = 0; j < fieldNum && keys.length > j; j++) {
							String value = getCellValue(row
									.getCell((short) (j + 2)));
							if (value == null || keys[j] == null) {
								continue;
							}
							fields.setProperty(keys[j], value);
						}
						rt.setXmlFields(fields);
						Properties p = new Properties();
						for (int j = 0; j < keys.length; j++) {
							String value = getCellValue(row
									.getCell((short) (j + 2)));
							if (value == null || keys[j] == null) {
								continue;
							}
							try {
								Double.parseDouble(value);
								if (value.endsWith(".0")) {
									value = value.substring(0,
											value.length() - 2);
								}
							} catch (Throwable t) {

							}
							p.setProperty(keys[j], value);
						}
						rt.setProperties(p);
					}
					scenario.addTest(rt);
				}
			}
		}

		Scenario s = ScenariosManager.getInstance().getScenario(
				scenario.getName());
		ScenariosManager.getInstance().setCurrentScenario(s);
	}

	private static String getCellValue(HSSFCell cell) {
		if (cell == null) {
			return null;
		}

		String cellValue = null;

		if (cell != null) {
			switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC:
				cellValue = Double.toString(cell.getNumericCellValue());
				break;
			case HSSFCell.CELL_TYPE_STRING:
				cellValue = cell.getRichStringCellValue().getString();
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				cellValue = Double.toString(cell.getNumericCellValue());
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				cellValue = "";
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				cellValue = Boolean.toString(cell.getBooleanCellValue());
				break;
			default:
				System.out.println("Unsupported cell type: "
						+ cell.getCellType());
			}
		}
		return cellValue;
	}

	/**
	 * returns an Object Array to be writen as Excel row.
	 * 
	 * @param className
	 *            test class name
	 * @param methodName
	 *            test method name
	 * @param parameters
	 *            Parameter[] of test parameters
	 * @return
	 */
	private Object[] getRow(String className, String methodName,
			Parameter[] parameters, Properties otherFields) {
		int paramSize = 0;
		int fieldsSize = 0;
		if (parameters != null) {
			paramSize = parameters.length;
		}

		if (otherFields != null) {
			fieldsSize = otherFields.size();
		}

		/**
		 * init row (Class Name, Method name, fields , params)
		 */
		Object[] row = new Object[2 + fieldsSize + paramSize];

		row[0] = className;
		row[1] = methodName;

		if (fieldsSize > 0) {
			String key;
			Enumeration<Object> fieldsEnum = otherFields.keys();
			for (int i = 2; i < 2 + fieldsSize; i++) {
				key = (String) fieldsEnum.nextElement();
				row[i] = otherFields.getProperty(key);
			}
		}

		/**
		 * sort parameter array before adding to excel row.
		 */
		if (parameters != null) {
			Arrays.sort(parameters, Parameter.ParameterNameComparator);
		}

		/**
		 * add all params
		 */
		int n = fieldsSize + 2;
		for (int i = n; i < n + paramSize; i++) {

			Object o = parameters[i - n].getValue();

			if (o == null) {
				o = "";
			}

			try {
				Double d = new Double(o.toString());
				row[i] = d;
			} catch (Throwable ex) {
				row[i] = o.toString();
			}
		}

		return row;
	}

}
