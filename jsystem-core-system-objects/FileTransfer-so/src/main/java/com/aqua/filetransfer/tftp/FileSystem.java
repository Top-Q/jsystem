/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.globalros.tftp.common.VirtualFile;
import com.globalros.tftp.common.VirtualFileSystem;


/**
 * VirtualFileSystem implementation for the TFTP server.
 * Taken from the TFTP package source.
 * {@linkplain http://sourceforge.net/projects/tftp4java/}
 */
class FileSystem implements VirtualFileSystem
{
   /**
    * TFTP home dir
    */
   private File home;

	/**
	 * Constructor for FileSystem.
	 */
	public FileSystem(String home)
	{
      this.home = new File(home);
	}

   /**
    * This method always try to find file within home
    * if not than home location is prepended because location is relative
    * If it is, check if location is within home otherwise throw an exception
    * that indicates an access violation
    * 
    * Please check also for tricks with .. , .
    * 
    */
   public File expand(String location) throws FileNotFoundException
   {
      if (location.indexOf("..") > -1)
         throw new FileNotFoundException("No tricks with .. allowed");
      
      return new File(home, location);
   }
   
	/**
    * 
	 * @see com.globalros.tftp.common.VirtualFileSystem#getInputStream(VirtualFile)
    * 
	 */
	public InputStream getInputStream(VirtualFile file) throws FileNotFoundException
	{
      return new FileInputStream ( expand(file.getFileName()) );
	}

	/**
	 * @see com.globalros.tftp.common.VirtualFileSystem#getOutputStream(VirtualFile)
	 */
	public OutputStream getOutputStream(VirtualFile file) throws FileNotFoundException
	{
		return new FileOutputStream( expand(file.getFileName()));
	}

}
