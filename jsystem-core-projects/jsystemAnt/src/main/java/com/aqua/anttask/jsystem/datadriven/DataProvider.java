package com.aqua.anttask.jsystem.datadriven;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * JSystem data driven data provider interface. Allow users to implement
 * different data providers for the data driven building block. <br>
 * For example, database data provider, Excel data provider, etc.. <br>
 * To create a new data provider one need to create a new Maven project and add
 * the JSystemAnt project as a dependency. After implementing the concrete data
 * provider class, the jar of the project needs to be added to the lib folder of
 * JSystem. If there are any additional dependencies that are needed they should
 * be part of the jar or to be added to the JSystem thirdparty/commonLib folder <br>
 * After launching JSystem, the data provider should be selected in the property
 * data.driven.provider. <br>
 * 
 * @author Itai Agmon
 *
 */
public interface DataProvider {

	/**
	 * Implement this method to provide the data for the JSystem data driven
	 * task. Every item in the list represents a single line in the data. The
	 * number of tests that will be executed is equals to the number of elements
	 * in the list. <br>
	 * The keys of the maps will be translated to JSystem references with the
	 * same name that will be set with the corresponding value. <br>
	 * 
	 * @param file
	 *            External file that holds the data that should be provided. For
	 *            example, Excel file.
	 * @param param
	 *            Open parameter that can be used in any way the data provider
	 *            implementer decides.
	 * 
	 * @return The data that should be used in the data provider task.
	 * @throws DataCollectorException
	 *             if fails to fetch the data
	 */
	List<Map<String, Object>> collect(File file, String param) throws DataCollectorException;

	/**
	 * Used to specify the data provider name
	 * 
	 * @return
	 */
	String getName();
}
