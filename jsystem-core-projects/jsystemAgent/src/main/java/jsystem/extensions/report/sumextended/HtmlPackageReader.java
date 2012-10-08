/*
 * Created on 26/04/2005
 *
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.extensions.report.sumextended;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.utils.FileUtils;

/**
 * @author uri.koaz
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HtmlPackageReader implements DataReader {
	private static Logger log = Logger.getLogger(HtmlPackageReader.class.getName());

	private File srcDirectory;

	public HtmlPackageReader(String path) {
		srcDirectory = new File(path);
	}

	public String getTitle(String packageName) {
		File packageDir = new File(srcDirectory, packageName.replace('.', '/'));
		File packageFile = new File(packageDir, "package.html");

		if (!packageFile.exists()) {
			return null;
		}
		String html;
		try {
			html = FileUtils.read(packageFile);
		} catch (IOException e) {
			log.log(Level.INFO, "Fail to read file", e);
			return null;
		}
		Pattern p = Pattern.compile("<title.*>(.*)</title>");
		Matcher m = p.matcher(html);
		if (!m.find()) {
			log.log(Level.FINE, "Title wasn't found");
			return null;
		}
		return m.group(1);
	}

	public String getDescription(String packageName) {
		File packageDir = new File(srcDirectory, packageName.replace('.', '/'));
		File packageFile = new File(packageDir, "package.html");

		if (!packageFile.exists()) {
			return null;
		}
		String html;

		try {
			html = FileUtils.read(packageFile);
		} catch (IOException e) {
			log.log(Level.INFO, "Fail to read file", e);
			return null;
		}

		Pattern p = Pattern.compile("<body>(.*)</body>", Pattern.DOTALL);
		Matcher m = p.matcher(html);
		if (!m.find()) {
			log.log(Level.FINE, "Body wasn't found");
			return null;
		}
		return m.group(1);

	}

}
