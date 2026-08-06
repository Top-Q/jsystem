package jsystem.framework.scenario.flow_control.datadriven;

import jsystem.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;


public class TsvDataProvider implements DataProvider {

	static Logger log = Logger.getLogger(TsvDataProvider.class.getName());
	private static final String SEPARATION_STRING = "\t";

	@Override
	public List<Map<String, Object>> provide(File file, String param) throws DataCollectorException {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Scanner lineScanner = null;
		try {
			lineScanner = new Scanner(file);
			List<String> titles = null;
			while (lineScanner.hasNextLine()) {
				List<String> cells = new ArrayList<String>();
				Scanner cellScanner = null;
				try {
					cellScanner = new Scanner(lineScanner.nextLine());
					cellScanner.useDelimiter(SEPARATION_STRING);
					while (cellScanner.hasNext()) {
						String cellValue = cellScanner.next();
						if (StringUtils.isEmpty(cellValue)) {
							cellValue = " ";
						}
						cells.add(cellValue);
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
					log.warning("Titles number is " + titles.size() + " while the cells number in one of the rows is " + cells.size());
				}
				// We would iterate over the smaller list size to avoid out
				// of bounds
				for (int i = 0; i < (titles.size() <= cells.size() ? titles.size() : cells.size()); i++) {
					dataRow.put(titles.get(i), cells.get(i));
				}
				data.add(dataRow);
			}
		} catch (FileNotFoundException e) {
			try {
				throw new DataCollectorException("TSV file [isExist: "+file.exists()+", isFile:"+file.isFile()+", fileName:'"+file+"', BasePath:'"+new File(".").getCanonicalPath()+"']", e);
			} catch (IOException ex) {
				throw new DataCollectorException("TSV file [isExist: "+file.exists()+", isFile:"+file.isFile()+", fileName:'"+file+"'",e);
			}
		} finally {
			if (lineScanner != null) {
				lineScanner.close();
			}
		}
		return data;
	}

	public String getName(){
		return "TSV";
	}

}
