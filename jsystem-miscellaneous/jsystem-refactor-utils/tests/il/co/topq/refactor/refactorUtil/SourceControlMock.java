package il.co.topq.refactor.refactorUtil;

import java.io.File;
import java.util.List;

import jsystem.extensions.sourcecontrol.SourceControlException;
import jsystem.extensions.sourcecontrol.SourceControlI;
import jsystem.framework.scenario.Scenario;

public class SourceControlMock implements SourceControlI {

	@Override
	public void addSut(String arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitSut(String arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public void connect(String arg0, String arg1, String arg2) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public Status getSutStatus(String arg0) throws SourceControlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initWorkingCopy() throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isWorkingCopyInitialize() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void makeWritable(List<File> arg0) throws SourceControlException {
		System.out.println("Making writable ");
		for (File file : arg0) {
			file.setWritable(true);
		}

	}

	@Override
	public void revertSut(String arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportMakeWritable() throws SourceControlException {
		return true;
	}

	@Override
	public void updateSut(String arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addScenario(Scenario arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public void commitScenario(Scenario arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public Status getScenarioStatus(Scenario arg0) throws SourceControlException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void revertScenario(Scenario arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateScenario(Scenario arg0) throws SourceControlException {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveFile(File src, File destination) throws SourceControlException {
		System.out.println("Renaming " + src.getName() + " to " + destination.getName());
		if (!src.exists()) {
			throw new SourceControlException("File " + src.getName() + " not found");
		}
		if (!src.canWrite()) {
			if (!src.setWritable(true)) {
				throw new SourceControlException("Failed to set file to writeable");
			}
		}
		if (!src.renameTo(destination)) {
			throw new SourceControlException("Failed to rename file");
		}
	}

}
