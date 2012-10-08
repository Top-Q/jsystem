package jsystem.extensions.sourcecontrol;

import java.io.File;
import java.util.List;

import jsystem.framework.scenario.Scenario;

public interface SourceControlI {

	public enum Status {
		NORMAL, MODIFIED, ADDED, DELETED, CONFLICTED, UNVERSIONED, IGNORED, NONE
	}

	void connect(String repoPath, String userName, String password) throws SourceControlException;

	void initWorkingCopy() throws SourceControlException;

	boolean isWorkingCopyInitialize();

	// ********Scenario Handling***********
	/**
	 * 
	 * @param scnearioName
	 *            scenario name without file extension
	 * @return
	 * @throws SourceControlException
	 */
	Status getScenarioStatus(final Scenario scenario) throws SourceControlException;

	void addScenario(final Scenario scenario) throws SourceControlException;

	void commitScenario(final Scenario scenario) throws SourceControlException;

	void updateScenario(final Scenario scenario) throws SourceControlException;

	void revertScenario(final Scenario scenario) throws SourceControlException;

	// ********SUT Handling***********

	/**
	 * 
	 * @param sutName
	 *            sut name without file extension
	 * @return
	 * @throws SourceControlException
	 */
	Status getSutStatus(final String sutName) throws SourceControlException;

	void addSut(final String sutName) throws SourceControlException;

	void commitSut(final String sutName) throws SourceControlException;

	void updateSut(final String sutName) throws SourceControlException;

	void revertSut(final String sutName) throws SourceControlException;

	/**
	 * This methods were added to support Version Control Systems such as Clear
	 * Case. In this kind of VCS,by default, files are in read-only mode and to
	 * use them it is needed to check-out them. Because check-out in VCS has
	 * many different meanings this operation is translated in this API to make
	 * the file Writable. Two methods were added to support this kind of
	 * behaviour: boolean supportMakeWritable() -in order to question the VCS if
	 * it support this kind of operation void makeWritable(File[] file) throws
	 * SourceControlException
	 * 
	 * @return true if the VCS support the makeWritable operation (usually
	 *         called also check-out e.g ClearCase) and false otherwise.
	 */
	boolean supportMakeWritable() throws SourceControlException;

	/**
	 * This methods were added to support Version Control Systems such as Clear
	 * Case. In this kind of VCS,by default, files are in read-only mode and to
	 * use them it is needed to check-out them. Because check-out in VCS has
	 * many different meanings this operation is translated in this API to make
	 * the file Writable. Two methods were added to support this kind of
	 * behaviour: boolean supportMakeWritable() -in order to question the VCS if
	 * it support this kind of operation void makeWritable(File[] file) throws
	 * SourceControlException
	 * 
	 * @param files
	 *            - array of files that needs to be writable/checkout
	 * @throws SourceControlException
	 *             - In case it is not possible to make any or all the files
	 *             writable (checkout the files).
	 */
	void makeWritable(List<File> files) throws SourceControlException;

	/**
	 * Some source control systems,(like clearcase), requires that the user will
	 * call the rename service in order for him move/rename file<br>
	 * This was added mainly for the refactor util. 
	 * 
	 * 
	 * @param source
	 * @param destination
	 */
	void moveFile(File source, File destination) throws SourceControlException;

}
