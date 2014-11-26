package com.aqua.anttask.jsystem.datadriven;

import java.io.File;
import java.util.List;
import java.util.Map;


public interface DataCollector {
	
	List<Map<String, Object>> collect(File file, String param) throws DataCollectorException;
	
	String getName();
}
