/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.OverlappingFileLockException;
import java.util.HashMap;
import java.util.Map;
/**
 * One of the ways to make sure the user doesn't open more then one process of a program
 * is to use a file as a semaphore. When a process starts it grabs a hold on the file,
 * When the next process starts it will identify that the file is already grabbed
 * and will not start.
 * 
 * This utility class manages file lock mechanism.
 *  
 * To work with the class the process activates the static method {@link #getFileLock(File)}.
 * Once the user has an instance of the class it can try to {@link #grabLock()}, true is returned
 * if the file could be grabbed otherwise false is returned.
 * When the process wants to release the lock it asks for the <code>FileLock</code> instance and 
 * activates the {@link #releaseLock()} method.
 *  
 * @author goland,dan hirsch
 */
public class FileLock {			

	/**
	 * Static map of all locks which are currently open.
	 * The key for the lock is file's canonical path.
	 */
	private static Map<String,FileLock> fileLockMap = new HashMap<String, FileLock>();
	
	/**
	 * The file which serves as a semaphore for this FileLock instance.
	 */
	private File lockFile;

	private FileOutputStream outStream;
	
	private java.nio.channels.FileLock lock;
	/**
	 * Returns a <code>FileLock</code> instance for a given <code>File</code>
	 */
	public synchronized static FileLock getFileLock(File file) throws Exception{
		FileLock lock = fileLockMap.get(file.getCanonicalPath());
		if (lock == null){
			lock = new FileLock(file);
		}
		fileLockMap.put(file.getCanonicalPath(),lock);
		return lock;
	}
		
	/**
	 */
	private FileLock(File file){
		this.lockFile = file;
	}

	/**
	 * Tries to grab a lock (open and output stream) on the <code>File</code> with which the <code>FileLock</code>
	 * was created.
	 * If the file can be grabbed the method returns true, otherwise false;
	 */
	public boolean grabLock() throws Exception {
		outStream = new FileOutputStream(lockFile);
		try{
			lock = outStream.getChannel().tryLock();
		}catch(OverlappingFileLockException e){
			return false;
		}
		//if succeed in getting lock return true
		if(lock != null && lock.isValid()){ 
			return true;
		}
		return false;
	}
	
	/**
	 * Releases lock (closes file output stream)
	 */
	public void releaseLock() throws Exception {
		if(lock != null && lock.isValid()){
			lock.release();
		}
		if (outStream != null){
			outStream.close();
		}
	}
}
