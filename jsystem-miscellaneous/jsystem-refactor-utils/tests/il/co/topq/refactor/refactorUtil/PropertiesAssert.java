package il.co.topq.refactor.refactorUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;

public class PropertiesAssert {

	public static void assertKey(String propertiesFileName, String keyRegex, int expectedNum) {
		assertKey(new File(propertiesFileName), keyRegex, expectedNum);
	}

	public static void assertValue(String propertiesFileName, String valueRegex, int expectedNum) {
		assertValue(new File(propertiesFileName), valueRegex, expectedNum);
	}

	public static void assertKeyValue(File propertiesFile, String key, String expectedValue) {
		Properties properties = new Properties();
		FileInputStream fis = null;
		System.out.println("Searching for key: " + key + " with value: " + expectedValue);
		try {
			fis = new FileInputStream(propertiesFile);
			properties.load(fis);
		} catch (Exception e) {

		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		final String actualValue = properties.getProperty(key);
		Assert.assertNotNull("No value found for key: " + key, actualValue);
		Assert.assertEquals(expectedValue, actualValue);
	}

	public static void assertValue(File propertiesFile, String valueRegex, int expectedNum) {
		Properties properties = new Properties();
		FileInputStream fis = null;
		int count = 0;
		Pattern pattern = Pattern.compile(valueRegex);
		try {
			fis = new FileInputStream(propertiesFile);
			properties.load(fis);
			for (Object value : properties.values()) {
				Matcher matcher = pattern.matcher((String) value);
				if (matcher.find()) {
					count++;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Assert.assertEquals("Wrong number of key " + valueRegex, expectedNum, count);

	}

	public static void assertKey(File propertiesFile, String keyRegex, int expectedNum) {
		System.out.println("Executing " + keyRegex + " on " + propertiesFile.getName());
		Properties properties = new Properties();
		FileInputStream fis = null;
		int count = 0;
		Pattern pattern = Pattern.compile(keyRegex);
		try {
			fis = new FileInputStream(propertiesFile);
			properties.load(fis);
			for (Object key : properties.keySet()) {
				Matcher matcher = pattern.matcher((String) key);
				if (matcher.find()) {
					count++;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Assert.assertEquals("Wrong number of key " + keyRegex, expectedNum, count);
	}
}
