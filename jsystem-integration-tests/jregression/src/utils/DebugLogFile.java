package utils;

import java.io.File;
import java.io.IOException;

import jsystem.utils.FileUtils;


public class DebugLogFile{
	private String logFileName = System.getProperty("user.dir")+"/"+"outPutLog.txt";
	private static File logFile;
	private static DebugLogFile INSTANCE;
	
	private DebugLogFile(){
		File tmp = new File(this.logFileName);
		if(tmp.exists()){
			tmp.delete();
			logFile = new File(this.logFileName);
		}
		else{
			DebugLogFile.logFile = tmp;
		}
	}
	
	
	public String getLogFileName(){
		return logFileName;
	}
	
	public void writeToDebug(String s) throws IOException{
		FileUtils.write(logFile, s+"\n", true);
	}
	
	public static DebugLogFile getInstance(){
		if(INSTANCE == null){
			INSTANCE = new DebugLogFile();
			return INSTANCE;
		}
		return INSTANCE;
	}
	
	public String getFilePath(){
		return logFileName;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if(System.getProperty("os.name").toLowerCase().startsWith("linux")){
			File outputLogDir = new File("/OutputLogs");
			outputLogDir.createNewFile();
			FileUtils.copyFile(logFile.getAbsolutePath(), outputLogDir+"/"+logFileName);
		}else if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
			File outputLogDir = new File("c:\\OutputLogs");
			outputLogDir.createNewFile();
			FileUtils.copyFile(logFile.getAbsolutePath(), outputLogDir+"/"+logFileName);
		}
	}
}