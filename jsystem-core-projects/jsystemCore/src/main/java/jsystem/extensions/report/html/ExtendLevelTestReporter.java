package jsystem.extensions.report.html;

import java.io.IOException;

import jsystem.framework.report.ExtendTestReporter;
import jsystem.framework.report.Reporter.EnumReportLevel;

public interface ExtendLevelTestReporter extends ExtendTestReporter{

	/**
	 * Start new level
	 * 
	 * @param levelName
	 * @param place
	 *            -by the "EnumReportLevel" Enum
	 * @throws IOException
	 */
	public abstract void startLevel(String level, EnumReportLevel place) throws IOException;

	/**
	 * Start new level
	 * 
	 * @param levelName
	 * @param place
	 *            -may be Reporter.MainFrame or Reporter.CurrentPlace
	 * @throws IOException
	 */
	public abstract void startLevel(String levelName, int place) throws IOException;

	/**
	 * stop current level .Insert from
	 * the stack previous level and 
	 * previous level file name 
	 */
	public abstract void stopLevel();

	/**
	 * if we inside level -go out to the main frame
	 */
	public abstract void closeAllLevels();

	/**
	 * close all level until given level name, if exists. if not found closes all<br>
	 * @param levelName	the level to stop closing on
	 * @param includeLevel True will also close these level, False will leave it the current level
	 */
	public abstract void closeLevelsUpTo(String levelName, boolean includeLevel);

}