package com.aqua.sanity.analyzers;

import java.io.File;
import jsystem.framework.analyzer.AnalyzerParameterImpl;

public class FileExistAnalyzer extends AnalyzerParameterImpl {
	private File _file;
	private boolean _isExist;
	public FileExistAnalyzer(File file) {
		_file = file;
	}

	@Override
	public void analyze() {
		_isExist = (Boolean)testAgainst;
		boolean fileExist = _file.exists();
		status = fileExist == _isExist;
		if (fileExist){
			title = "file "+_file.getAbsolutePath()+" exists";
		}else{
			title = "file "+_file.getAbsolutePath()+" doesn't exist";
		}
	}
}
