/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jsystem.framework.FrameworkOptions;
import jsystem.framework.JSystemProperties;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FileUtils {

	public static enum FileEncoding {
		UTF8("UTF-8"), UTF16("UTF-16");

		private String encodingLabel;

		FileEncoding(String encodingLabel) {
			this.encodingLabel = encodingLabel;
		}

		@Override
		public String toString() {
			return encodingLabel;
		}
	}

	private static Logger log = Logger.getLogger(FileUtils.class.getName());

	public static void copyDirectory(String sourceDirName, String destinationDirName) throws IOException {

		copyDirectory(new File(sourceDirName), new File(destinationDirName), null);
	}

	public static void copyDirectory(File source, File destination, String endWith) throws IOException {
		if (source.exists() && source.isDirectory()) {
			if (!destination.exists()) {
				destination.mkdirs();
			}

			File[] fileArray = source.listFiles();

			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isDirectory()) {
					copyDirectory(fileArray[i],
							new File(destination.getPath() + File.separator + fileArray[i].getName()), endWith);
				} else {
					if (endWith != null) {
						if (!fileArray[i].getPath().toLowerCase().endsWith(endWith.toLowerCase())) {
							continue;
						}
					}
					copyFile(fileArray[i], new File(destination.getPath() + File.separator + fileArray[i].getName()));
				}
			}
		}
	}

	public static void copyDirectory(File source, File destination) throws IOException {
		copyDirectory(source, destination, null);
	}

	public static void copyFile(String sourceFileName, String destinationFileName) throws IOException {

		copyFile(new File(sourceFileName), new File(destinationFileName));
	}

	public static void copyFile(File source, File destination) throws IOException {
		if ((destination.getParentFile() != null) && (!destination.getParentFile().exists())) {

			destination.getParentFile().mkdirs();
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {

			fis = new FileInputStream(source);
			fos = new FileOutputStream(destination);

			byte[] buffer = new byte[1024 * 4];
			int n = 0;

			while ((n = fis.read(buffer)) != -1) {
				fos.write(buffer, 0, n);
			}

		} catch (IOException e) {
			throw new IOException(e.getMessage() + " Source: " + source + " Destination: " + destination);
		} finally {
			closeStream(fis);
			closeStream(fos);
		}
	}

	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				log.warning("Failed closing stream");
			}
		}
	}

	/**
	 * Recursive delete of a directory and all it's content
	 * 
	 * @param directory
	 */
	public static void deltree(String directory) {
		deltree(new File(directory));
	}

	/**
	 * Recursive delete of a directory and all it's content
	 * 
	 * @param directory
	 */
	public static void deltree(File directory) {
		if (directory.exists() && directory.isDirectory()) {
			File[] fileArray = directory.listFiles();
			if (fileArray != null) {
				for (int i = 0; i < fileArray.length; i++) {
					if (fileArray[i].isDirectory()) {
						deltree(fileArray[i]);
					} else {
						fileArray[i].delete();
					}
				}
			}

			directory.delete();
		}
	}

	public static String getPath(String fullFileName) {
		int pos = fullFileName.lastIndexOf("/");

		if (pos == -1) {
			pos = fullFileName.lastIndexOf("\\");
		}

		String shortFileName = fullFileName.substring(0, pos);

		if (shortFileName == null) {
			return "/";
		}

		return shortFileName;
	}

	public static String getShortFileName(String fullFileName) {
		int pos = fullFileName.lastIndexOf("/");

		if (pos == -1) {
			pos = fullFileName.lastIndexOf("\\");
		}

		String shortFileName = fullFileName.substring(pos + 1, fullFileName.length());

		return shortFileName;
	}

	public static boolean exists(String fileName) {
		File file = new File(fileName);

		return file.exists();
	}

	/**
	 * Check if a file exists several times with delay in between
	 * 
	 * @param fileName
	 *            The file to check
	 * @param retries
	 *            Number of times to try
	 * @param timeBetweenRetries
	 *            Time to sleep (mili-seconds) between retries
	 * @return True if file exists, False if it doesn't
	 */
	public static boolean existsWithRetry(String fileName, int retries, long timeBetweenRetries) {
		File file = new File(fileName);

		for (int i = 0; i < retries; i++) {
			if (i > 0) {
				try {
					Thread.sleep(timeBetweenRetries);
				} catch (InterruptedException e) {
					log.log(Level.WARNING, "Problem sleeping between file exist checking", e);
				}
			}
			if (file.exists()) {
				return true;
			}
		}

		return false;
	}

	public static String[] listDirs(String fileName) {
		return listDirs(new File(fileName));
	}

	public static String[] listDirs(File file) {
		List<String> dirs = new ArrayList<String>();

		File[] fileArray = file.listFiles();

		for (int i = 0; i < fileArray.length; i++) {
			if (fileArray[i].isDirectory()) {
				dirs.add(fileArray[i].getName());
			}
		}

		return (String[]) dirs.toArray(new String[0]);
	}

	public static String[] listFiles(String fileName) {
		return listFiles(new File(fileName));
	}

	public static String[] listFiles(File file) {
		List<String> files = new ArrayList<String>();

		File[] fileArray = file.listFiles();
		if (fileArray != null) {
			for (int i = 0; i < fileArray.length; i++) {
				if (fileArray[i].isFile()) {
					files.add(fileArray[i].getName());
				}
			}
		}

		return (String[]) files.toArray(new String[0]);
	}

	public static void mkdirs(String pathName) {
		File file = new File(pathName);
		file.mkdirs();
	}

	public static String read(String fileName) throws IOException {
		return read(new File(fileName));
	}

	public static String readResourceAsString(String resource, ClassLoader loader) throws IOException {
		InputStream stream = loader.getResourceAsStream(resource);
		try {
			return read(stream);
		} finally {
			stream.close();
		}
	}

	public static String read(InputStream stream) throws IOException {
		return read(stream, FileEncoding.UTF8);
	}

	public static String read(File file, FileEncoding encoding) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			return read(stream, encoding);
		} finally {
			stream.close();
		}
	}

	public static String read(File file) throws IOException {
		FileInputStream stream = new FileInputStream(file);
		try {
			return read(stream);
		} finally {
			stream.close();
		}
	}

	public static String read(InputStream stream, FileEncoding encoding) throws IOException {
		return read(stream, encoding, 0);
	}

	public static String read(InputStream stream, long position) throws IOException {
		return read(stream, FileEncoding.UTF8, position);
	}

	public static String read(InputStream stream, FileEncoding encoding, long position) throws IOException {
		stream.skip(position);
		BufferedReader br = new BufferedReader(new InputStreamReader(stream, encoding.toString()));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line).append('\n');
		}
		return sb.toString().trim();
	}

	/**
	 * Read a file as a byte array
	 * 
	 * @param file
	 *            the file to read
	 * @return a byte array with the file content
	 * @throws Exception
	 */
	public static byte[] readBytes(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		byte[] fileData = new byte[fis.available()];
		for (int i = 0; i < fileData.length; i++) {
			fileData[i] = (byte) fis.read();
		}
		return fileData;
	}

	/**
	 * Converts file path from windows path to unix path.
	 */
	public static String replaceSeparator(String fileName) {
		return fileName.replace('\\', '/');
	}

	/**
	 * Converts file path from unix path to windows path.
	 */
	public static String convertToWindowsPath(String path) {
		return path.replace('/', '\\');
	}

	public static List<String> toList(Reader reader) {
		List<String> list = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(reader);

			String line = null;

			while ((line = br.readLine()) != null) {
				list.add(line);
			}

			br.close();
		} catch (IOException ioe) {
		}

		return list;
	}

	public static List<String> toList(String fileName) {
		try {
			return toList(new FileReader(fileName));
		} catch (IOException ioe) {
			return new ArrayList<String>();
		}
	}

	public static void write(File file, String s, boolean append) throws IOException {
		if (file.getParent() != null) {
			mkdirs(file.getParent());
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file, append);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));

		bw.flush();
		bw.write(s);

		bw.close();
		fileOutputStream.close();
	}

	public static void write(String fileName, String s) throws IOException {
		write(new File(fileName), s, false);
	}

	public static void write(String pathName, String fileName, String s) throws IOException {

		write(new File(pathName, fileName), s, false);
	}

	public static void append(String fileName, String s) throws IOException {
		write(new File(fileName), s, true);
	}

	public static void zipDirectory(String directory, String fileExtention, String destinationFile) throws IOException {
		zipDirectory(directory, fileExtention, destinationFile, false);
	}

	/**
	 * Zip given directory
	 * 
	 * @param directory
	 *            String
	 * @param fileExtention
	 *            String
	 * @param destinationFile
	 *            String
	 * @throws IOException
	 * If Failed to create new zip file
	 */
	public static void zipDirectory(final String directory,final String fileExtention,final String destinationFile, boolean report) throws IOException
			 {
		
	    	File srcFolder = new File(directory);
	    	if(srcFolder != null && srcFolder.isDirectory())
		    {
	    		Iterator<File> i = org.apache.commons.io.FileUtils.iterateFiles(srcFolder, null, true);
//	    		Iterator<File> i = FileUtils.iterateFiles(srcFolder, new String []{"xcf"}, true);
                        /*
                           public static Iterator<File> iterateFiles(File directory, String[] extensions, boolean recursive)
                           directory - the directory to search in
                           extensions - an array of extensions, ex. {"java","xml"}. If this parameter is null, all files are returned.
                           recursive - if true all subdirectories are searched as well
                         */
	    		File zipFile = new File(destinationFile);
	    		zipFile.createNewFile();
	    		
	    		
	    		OutputStream outputStream = null;
	    		ArchiveOutputStream zipOutputStream = null;
	    		
	    		try {
	    			outputStream = new FileOutputStream(zipFile);
	    			zipOutputStream = new ZipArchiveOutputStream(outputStream);
	    			int srcFolderLength = srcFolder.getAbsolutePath().length() + 1;  // +1 to remove the last file separator
	    			while(i.hasNext())
	    			{
	    				File file = i.next();	    			
	    				String relativePath  = file.getAbsolutePath().substring(srcFolderLength);
	    				ArchiveEntry zipArchiveEntry = new ZipArchiveEntry(relativePath);
	    				zipOutputStream.putArchiveEntry(zipArchiveEntry);
	    				FileInputStream fis = null;
	    				try {
	    					fis = new FileInputStream(file);
	    					IOUtils.copy(fis, zipOutputStream);    			
	    				}finally{
	    					fis.close();
	    				}
	    				zipOutputStream.closeArchiveEntry();
	    			}
	    			
	    		}finally{
	    			zipOutputStream.flush();
	    			zipOutputStream.finish();
	    			zipOutputStream.close();
	    		}
		    }

		
	}

	/**
	 * Extract a given zip file to a given directory
	 * 
	 * @param zipFile
	 *            the Zip file to extract
	 * @param root
	 *            The folder to extract to
	 * @throws IOException
	 */
	public static void extractZipFile(File zipFile, File root) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<?> enum1 = zip.entries();
		while (enum1.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) enum1.nextElement();
			File file = new File(root, entry.getName());
			if (entry.isDirectory()) {
				file.mkdirs();
			} else {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				extractOneFile(file, zip.getInputStream(entry));
			}
		}
		zip.close();
	}

	/**
	 * Extract a SINGLE zip file
	 * 
	 * @param fileName
	 *            the fileName end String
	 * @param zipFile
	 *            The file to unzip from
	 * @param destination
	 *            the Destination to unzip to
	 * @throws IOException
	 */
	public static void extractOneZipFile(String fileName, File zipFile, File destination) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> enum1 = zip.entries();
		while (enum1.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) enum1.nextElement();
			if (entry.getName().endsWith(fileName)) {
				File file = new File(destination, fileName);
				extractOneFile(file, zip.getInputStream(entry));
			}
		}
		zip.close();
	}

	public static void extractZipDirectory(File zipFile, String directory, File destination) throws Exception {
		ZipFile zip = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> elements = zip.entries();
		while (elements.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) elements.nextElement();
			if (entry.getName().replace('\\', '/').startsWith(directory.replace('\\', '/') + "/")) {
				File file = new File(destination, entry.getName().substring(directory.length() + 1));
				if (entry.isDirectory()) {
					file.mkdirs();
					continue;
				}
				try {
					extractOneFile(file, zip.getInputStream(entry));
				} catch (Exception e) {
					log.log(Level.WARNING, "Fail to extract " + entry.getName());
				}
			}
		}
		zip.close();
	}

	public static void collectAllFiles(File root, FilenameFilter filter, Vector<File> collectTo) {
		File[] list = root.listFiles();
		for (int i = 0; i < list.length; i++) {
			if (list[i].isDirectory()) {
				collectAllFiles(list[i], filter, collectTo);
			} else {
				if (filter.accept(list[i].getParentFile(), list[i].getName())) {
					collectTo.addElement(list[i]);
				}
			}
		}
	}

	private static void addZipEntry(ZipOutputStream zipOut, File zipIn, File root) throws IOException {

		BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(zipIn));
		byte buffer[] = new byte[1024];
		int length;
		// replace of file separator is a fix for 1783
		// so zip created on windows will be extracted on linux and vice versa
		String zipEntryName = replaceSeparator(zipIn.getPath().substring(root.getPath().length() + 1));
		zipOut.putNextEntry(new ZipEntry(zipEntryName));

		try {
			length = inStream.read(buffer);
			while (length != -1) {
				zipOut.write(buffer, 0, length);
				length = inStream.read(buffer);
			}
		} finally {
			zipOut.closeEntry();
			inStream.close();
		}
	}

	private static void extractOneFile(File name, InputStream input) throws IOException {
		FileOutputStream output = new FileOutputStream(name);
		byte[] buf = new byte[100000];
		while (true) {
			int length = input.read(buf);
			if (length <= 0)
				break;
			output.write(buf, 0, length);
		}
		output.close();
		input.close();
	}

	/**
	 * Extract a given tar.gz file to a given directory
	 * 
	 * @param tarGzFile
	 *            - the file to untar from
	 * @param destinationDir
	 *            - the destination directory to untar to
	 * @throws IOException
	 */
	public static void extractTarGzFile(File tarGzFile, String destinationDir) throws IOException {
		InputStream in = getInputStream(tarGzFile.getAbsolutePath());
		untar(in, new File(destinationDir));
	}

	private static InputStream getInputStream(String tarFileName) throws IOException {
		if (tarFileName.substring(tarFileName.lastIndexOf(".") + 1, tarFileName.lastIndexOf(".") + 3).equalsIgnoreCase(
				"gz")) {
			log.log(Level.INFO, "Creating an GZIPInputStream for the file");
			return new GZIPInputStream(new FileInputStream(new File(tarFileName)));
		} else {
			log.log(Level.INFO, "Creating an InputStream for the file");
			return new FileInputStream(new File(tarFileName));
		}
	}

	private static void untar(InputStream in, File untarDir) throws IOException {
		log.log(Level.INFO, "Reading TarInputStream... ");
		TarInputStream tin = new TarInputStream(in);
		TarEntry tarEntry = tin.getNextEntry();
		log.log(Level.INFO, "UNTARDIR " + untarDir);
		if (!untarDir.exists()) {
			untarDir.mkdir();
		}
		while (tarEntry != null) {
			File destPath = new File(untarDir.getAbsolutePath() + File.separatorChar + tarEntry.getName());

			log.log(Level.INFO, "Processing " + destPath.getAbsoluteFile());
			if (!tarEntry.isDirectory()) {
				FileOutputStream fout = new FileOutputStream(destPath);
				tin.copyEntryContents(fout);
				fout.close();
			} else {
				destPath.mkdir();
			}
			tarEntry = tin.getNextEntry();
		}
		tin.close();
	}

	public static boolean winRename(String src, String dst) throws Exception {
		Command cmd = new Command();
		cmd.setDir(new File(System.getProperty("user.dir")));
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("windows")) {
			cmd.setCmd(new String[] { "cmd.exe", "/C", "rename", "\"" + src + "\"", "\"" + dst + "\"" });
		} else {
			cmd.setCmd(new String[] { "rename", "\"" + src + "\"", "\"" + dst + "\"" });
		}
		Execute.execute(cmd, true);
		return (cmd.getReturnCode() == 0);
	}

	public static void saveDocumentToFile(Document doc, File file) throws Exception {
		if (file.getParent() != null) {
			mkdirs(file.getParent());
		}
		FileOutputStream fos = new FileOutputStream(file);
		Source source = new DOMSource(doc);
		Result result = new StreamResult(fos);
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "yes");
		xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		xformer.transform(source, result);
		fos.close();
	}

	/**
	 * 
	 */
	public static Document readDocumentFromFile(File xmlFile) throws Exception {
		return XmlUtils.getDocumentBuilder().parse(xmlFile);
	}

	/**
	 * 
	 */
	public static void saveInputStreamToFile(InputStream in, File file) throws Exception {
		if (file.getParentFile() != null) {
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file);
		try {
			byte[] buf = new byte[4000];
			int c;
			while (true) {
				c = in.read(buf);
				if (c == -1) {
					break;
				}
				fos.write(buf, 0, c);
			}
		} finally {
			fos.close();
		}
	}

	/**
	 * Reads a file from file system and returns its content as a byte array.
	 * The file can be either a binary file or a text file. The method supports
	 * files with maximum size of {@link Integer.MAX_VALUE}
	 */
	public static byte[] readFile(File file) throws Exception {
		if (!file.exists()) {
			throw new FileNotFoundException("File not found " + file.getName());
		}
		long originalFileSize = file.length();
		if (originalFileSize > Integer.MAX_VALUE) {
			throw new Exception("The method supports files with maxsize of Integer.MAX_VALUE");
		}
		int fileSize = (int) originalFileSize;
		if (fileSize == 0) {
			return new byte[0];
		}
		FileInputStream fis = new FileInputStream(file);
		byte[] fileContent;
		try {
			fileContent = new byte[fileSize];
			fis.read(fileContent);
		} finally {
			fis.close();
		}
		return fileContent;
	}

	/**
	 * Calculates the MD5 digest of a file. If given file is a directory, the
	 * method calculates the MD5 recursivelly. The method supports files with
	 * maximum size of {@link Integer.MAX_VALUE}
	 */
	public static String getMD5(File file) throws Exception {
		if (!file.exists()) {
			throw new FileNotFoundException("File not found " + file.getName());
		}
		MessageDigest md = MessageDigest.getInstance("MD5");
		updateMessageDigest(file, md);
		byte[] hash = md.digest();
		BigInteger result = new BigInteger(hash);
		String rc = result.toString(16);
		return rc;
	}

	/**
	 * Gets a file and a {@link MessageDigest} instance, calculates file's
	 * digest and update given {@link MessageDigest} instance with file's
	 * digest.
	 */
	public static void updateMessageDigest(File file, MessageDigest md) throws Exception {
		if (file.isFile()) {
			byte[] fileContent = readFile(file);
			md.update(fileContent);
			System.gc();
			return;
		}

		File[] files = file.listFiles();
		for (File f : files) {
			updateMessageDigest(f, md);
		}
	}

	/**
	 * creates a new xmlfile with a "reports" son for the reports usage
	 * 
	 * @param xmlFile
	 *            the file to write to
	 * @throws Exception
	 */
	public static void getEmptyXmlFile(File xmlFile) throws Exception {
		Element main;
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.newDocument();
		main = doc.createElement("reports");
		doc.appendChild(main);
		saveDocumentToFile(doc, xmlFile);
	}

	/**
	 * Creates CharSequence from a file. Expects a text file. Uses
	 * <code>FileChannel</code> to map file to memory. Please note the gc is
	 * activated at the end of the method.
	 */
	public static CharSequence charSequenceFromFile(String filename) throws Exception {
		if (!new File(filename).exists()) {
			throw new Exception("File not found " + filename);
		}
		FileInputStream fis = new FileInputStream(filename);
		FileChannel fc = fis.getChannel();
		ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0, (int) fc.size());
		CharBuffer cbuf = Charset.forName("8859_1").newDecoder().decode(bbuf);
		fc.close();
		fis.close();
		bbuf.clear();
		bbuf = null;
		System.gc();
		return cbuf;
	}

	/**
	 * Returns first <code>endOffset</code> characters of file
	 * <code>filename</code> as String. Should be used for small files. To load
	 * to memory big files please use: {@link #charSequenceFromFile(String)}
	 */
	public static String sequentialSequenceFromFile(String filename, long endOffset) throws Exception {
		File f = new File(filename);
		if (!f.exists()) {
			throw new Exception("File not found " + filename);
		}
		if (endOffset > f.length() - 1) {
			endOffset = f.length() - 1;
		}
		FileInputStream fis = new FileInputStream(filename);
		byte[] buff = new byte[(int) endOffset];
		fis.read(buff);
		fis.close();
		return new String(buff);
	}

	/**
	 * Wrapper to the getCanonicalPath method of <code>File</code>. If fetching
	 * canonical path fails the method return absolute path.
	 */
	public static String getCannonicalPath(File f) {
		try {
			return f.getCanonicalPath();
		} catch (IOException e) {
			return f.getAbsolutePath();
		}
	}

	/**
	 * get an array of all files in the given directory, with given prefix
	 * 
	 * @param directory
	 *            the full path directory to check
	 * @param prefix
	 *            the prefix to find
	 * @return an array of all file names in that directory (without full path)
	 */
	public static String[] getFileNameStartingWith(String directory, String prefix) {
		Vector<String> vector = new Vector<String>();
		File f = new File(directory);
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().startsWith(prefix)) {
				vector.add(files[i].getName());
			}
		}
		String[] toReturn = new String[vector.size()];
		vector.toArray(toReturn);
		return toReturn;
	}

	/**
	 * Given a file and base directory, the method returns file path relative to
	 * base directory. If file is not under base directory, the method throws
	 * IllegalArgumentException
	 */
	public static String getRelativePath(File file, File baseDirectory) throws Exception {
		String baseDirAsString = baseDirectory.getCanonicalPath();
		String filePathAsString = file.getCanonicalPath();
		int dirIndex = filePathAsString.indexOf(baseDirAsString);
		if (dirIndex == -1) {
			throw new IllegalArgumentException("Directory path is not part of file path. directory = "
					+ baseDirAsString + " scenario path = " + filePathAsString);
		}
		return filePathAsString.substring(dirIndex + baseDirAsString.length() + 1);
	}

	/**
	 * @param path
	 * @return true if path is relative
	 */
	public static boolean isRelativePath(String path) throws Exception {
		String osName = System.getProperty("os.name");

		if (osName.toLowerCase().startsWith("linux")) {
			if (path.startsWith(File.separator)) {
				return false;
			} else {
				return true;
			}
		} else if (osName.toLowerCase().startsWith("windows")) {
			if (path.indexOf(":") != -1) {
				return false;
			} else {
				return true;
			}
		}
		throw new Exception("isRelativePath - doesn't support " + System.getProperty("os.name") + " operating system");
	}

	/**
	 * returns true if <code>baseDirectory</code> is a ancestor directory of
	 * <code>file</code>
	 */
	public static boolean isAncestor(File file, File baseDirectory) throws Exception {
		String baseDirAsString = baseDirectory.getCanonicalPath();
		String filePathAsString = file.getCanonicalPath();
		int dirIndex = filePathAsString.indexOf(baseDirAsString);
		if (dirIndex == -1) {
			return false;
		}
		return true;
	}

	/**
	 * get an array of all files in the given directory, with given extension
	 * 
	 * @param directory
	 *            the full path directory to check
	 * @param extension
	 *            the extension to find
	 * @return an array of all file names in that directory (without full path)
	 */
	public static String[] getFilesWithExtension(String directory, String extension) {
		Vector<String> vector = new Vector<String>();
		File f = new File(directory);
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().endsWith(extension)) {
				vector.add(files[i].getName());
			}
		}
		String[] toReturn = new String[vector.size()];
		vector.toArray(toReturn);
		return toReturn;
	}

	/**
	 * load a properties file to a Properties object
	 * 
	 * @param fileName
	 *            the file to load properties from
	 * @return Properties object of the file
	 * @throws IOException
	 */
	public static Properties loadPropertiesFromFile(String fileName) throws IOException {
		fileName = replaceSeparator(fileName);
		log.finest("Loading properties from file " + fileName);
		Properties p = new Properties();
		FileInputStream input = null;
		InputStreamReader inputStreamReader = null;
		try {
			input = new FileInputStream(fileName);
			inputStreamReader = new InputStreamReader(input, "UTF-8");
			p.load(inputStreamReader);
			return p;
		} finally { // close input stream
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (input != null) {
				input.close();
			}
		}
	}

	/**
	 * save given properties to a file
	 * 
	 * @param properties
	 *            the properties to save
	 * @param fileName
	 *            the file to copy properties to
	 * @param addDate
	 *            if set to false will remove the date (source control issue)
	 * @throws IOException
	 */
	public static synchronized void savePropertiesToFile(Properties properties, String fileName, boolean addDate)
			throws IOException {
		log.finest("Saving properties to file " + fileName);
		if (addDate) {

			FileOutputStream output = null;
			OutputStreamWriter outputStreamWriter = null;
			try {
				output = new FileOutputStream(fileName);
				outputStreamWriter = new OutputStreamWriter(output, "UTF-8");
				properties.store(outputStreamWriter, null);
			} finally { // close input stream
				if (output != null) {
					output.close();
				}
				if (outputStreamWriter != null) {
					outputStreamWriter.close();
				}
			}
		} else { // remove the first line of the date
			StringWriter buffer = new StringWriter();
			properties.store(buffer, null);
			String propertiesString = buffer.toString();
			if (propertiesString.startsWith("#")) {
				int endOfFirstLineIndex = propertiesString.indexOf(System.getProperty("line.separator"));
				if (endOfFirstLineIndex > 0) {
					propertiesString = propertiesString.substring(
							(endOfFirstLineIndex + System.getProperty("line.separator").length()),
							propertiesString.length());
				}
			}
			write(fileName, propertiesString);
		}
	}

	/**
	 * save given properties to a file
	 * 
	 * @param properties
	 *            the properties to save
	 * @param fileName
	 *            the file to copy properties to
	 * @throws IOException
	 */
	public static void savePropertiesToFile(Properties properties, String fileName) throws IOException {
		savePropertiesToFile(properties, fileName, true);
	}

	/**
	 * save given properties to a file, in a sorted manner.
	 * 
	 * @param properties
	 *            the properties to save
	 * @param fileName
	 *            the file to copy properties to
	 * @throws IOException
	 */
	public static void saveSortedPropertiesToFile(Properties properties, String fileName, boolean addTate)
			throws IOException {
		Properties tmp = new SortedProperties();
		tmp.putAll(properties);
		savePropertiesToFile(tmp, fileName, addTate);
	}

	/**
	 * add a given property to properties file
	 * 
	 * @param fileName
	 *            the file to copy properties to
	 * @param key
	 *            the Key to add
	 * @param value
	 *            the Value for the given key
	 * @throws IOException
	 */
	public static void addPropertyToFile(String fileName, String key, String value) throws IOException {
		File f = new File(fileName);
		Properties p = new Properties();
		if (f.exists()) {
			p = loadPropertiesFromFile(fileName);
		}
		p.setProperty(key, value);
		savePropertiesToFile(p, fileName, true);
	}

	/**
	 * delete a file from the system, if exists
	 * 
	 * @param fileName
	 *            the file to delete (full path)
	 * @return false if file doesn't exist or is a directory
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (!file.exists() || file.isDirectory()) {
			return false;
		}
		file.delete();
		return true;
	}

	public static void main(String[] args) {
		try {
			FileUtils.winRename("C:\\Documents and Settings\\AQUA\\Desktop\\JSystem 4.ppt", "JSystem 4.ppt");
		} catch (Exception e) {
			log.log(Level.FINEST, "Exception while trying to rename");
		}
	}

	/**
	 * remove the full path from a file and return only the file name
	 * 
	 * @param fileName
	 *            the fullpath name to remove from
	 * @return the file name
	 */
	public static String getFileNameWithoutFullPath(String fileName) {
		File file = new File(fileName);
		return file.getName();
	}

	/**
	 * Returns the line number of the last occurrence of <code>textToFind</code>
	 * regular expression.
	 */
	public static int getLastLineWith(File fileName, String textToFind) throws IOException {
		String line;
		FileInputStream fis = new FileInputStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		int lineNumber = 0;
		int toRet = -1;
		while ((line = reader.readLine()) != null) {
			if (Pattern.compile(textToFind).matcher(line).find()) {
				toRet = lineNumber;
			}
			lineNumber++;
		}
		reader.close();
		return toRet;
	}

	/**
	 * in-place replacement of text in file. Replaces <code>lookFor</code>
	 * regular expression with <code>replaceWith</code> in line number
	 * <code>lineIndex</code>.</br> If <code>lookFor</code> is not found in line
	 * number <code>lineIndex</code> file remains as is.</br> The method support
	 * only replacement of text with text which is shorter or same length. If
	 * the <code>replaceWith</code> is longer from <code>lookFor</code> run time
	 * exception is thrown.
	 * 
	 * @see #getLastLineWith
	 */
	public static void replaceInFile(File fileName, String lookFor, String replaceWith, int lineIndex)
			throws IOException {
		String line;
		FileInputStream fis = new FileInputStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
		RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
		int lineCounter = 0;
		while ((line = reader.readLine()) != null) {
			int originalLineLength = line.length();
			if (lineCounter == lineIndex) {
				line = line.replaceFirst(lookFor, replaceWith);
				if (line.length() > originalLineLength) {
					throw new RuntimeException("Attempt to replace text with a longer text is not supported");
				}
			}
			raf.write((line + "\n").getBytes());
			lineCounter++;
		}
		reader.close();
		raf.setLength(raf.getFilePointer());
		raf.close();
	}

	/**
	 * in-place remove of a given buffer, starting a given line in a file, if
	 * the contain string is contained
	 * 
	 * @param fileName
	 *            the file to remove from
	 * @param lineIndex
	 *            the line index to start removing from
	 * @param contain
	 *            the String that should be contained in the given buffer to
	 *            remove
	 * @throws IOException
	 */
	public static void removeStartingOfLine(File fileName, int lineIndex, String contain) throws IOException {
		StringBuffer buffer = new StringBuffer();
		String line;
		FileInputStream fis = new FileInputStream(fileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
		int lineCounter = 0;
		StringBuffer buf2 = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			if (lineCounter < lineIndex) {
				buffer.append(line);
				buffer.append("\n");
			} // else - skip the line
			else {
				buf2.append(line);
				buf2.append("\n");
			}
			lineCounter++;
		}
		if (!buf2.toString().contains(contain)) {
			buffer.append(buf2.toString());
		}
		reader.close();
		raf.close();

		write(fileName, buffer.toString(), false);
	}

	/**
	 * Given a list of files, returns all the files which exist and that the
	 * process can't write to.
	 */
	public static List<File> getFilesCannotAccess(File... files) {
		ArrayList<File> failAccess = new ArrayList<File>();
		for (File file : files) {
			if (file.exists() && !file.canWrite()) {
				failAccess.add(file);
			}
		}
		return failAccess;
	}

	public static File[] getFilesByDate(String directory, final boolean earliestFirst) {
		File[] files = new File(directory).listFiles();

		Arrays.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if (earliestFirst) {
					return new Long(o1.lastModified()).compareTo(new Long(o2.lastModified()));
				} else {
					return new Long(o2.lastModified()).compareTo(new Long(o1.lastModified()));
				}
			}
		});

		return files;
	}

	public static void deleteDirectory(File dir) {
		if(dir.isDirectory()){
			 
    		//directory is empty, then delete it
    		if(dir.list().length==0){
    			dir.delete();
    		}else{
    		   //list all the directory contents
        	   String files[] = dir.list();
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(dir, temp);
        	      //recursive delete
        	      deleteDirectory(fileDelete);
        	   }
 
        	   //check the directory again, if empty then delete it
        	   if(dir.list().length==0){
        		   dir.delete();
        	   }
    		}
 
    	}else{
    		//if file, then delete it
    		dir.delete();
    	}
    }
	
	public static void moveDirectory(String sourceDirectory, String destinationDirectory){
		File dir = new File(sourceDirectory);
		if(!dir.isDirectory()){
			log.log(Level.INFO, sourceDirectory + " is not a directory!");
			return;
		}
		if(!dir.exists()){
			log.log(Level.INFO, sourceDirectory + " does not exist!");
			return;
		}
		//Move folder
		File[] files = dir.listFiles();
		
		// Destination directory
		File newDirectory = new File(JSystemProperties.getInstance().getPreference(FrameworkOptions.LOG_FOLDER) + "\\log_" + String.valueOf(System.currentTimeMillis()));
		if(!newDirectory.mkdir()){
			log.log(Level.INFO, "Create Directory Failed!");
			return;
		}
		
		for(File file : files){
			if (!file.renameTo(new File(newDirectory, file.getName()))) {
				log.log(Level.INFO, "Moving Failed!");
				return;
			}
		}
	}

}