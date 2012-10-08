package com.ignis.embeddedcatalina;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jsystem.utils.FileUtils;
import junit.framework.SystemTestCase;

/**
 */
public class EmbeddedServletContainerTest extends SystemTestCase {
	
	private EmbeddedCatalina container;

	public void setUp() throws Exception {
		FileUtils.write("textFile.txt", "testMessage");
	}

	public void tearDown() throws Exception {
		if (container != null) {
			container.close();
			Thread.sleep(3000);
		}
	}

	/**
	 */
	public void testDefaultConfiguration() throws Exception {
		container = new EmbeddedCatalina();
		container.init();
		container.start();
		URL url = new URL("http://127.0.0.1:8083/textFile.txt");
		readAndAssertBuffer(url,"testMessage");
	}
	
	/**
	 */
	public void testUpdateConfigurationThroughSystemObject() throws Exception {
		container = new EmbeddedCatalina();
		container.setDefaultContextPath("ignis");
		container.setDefaultConnectorPort(8084);
		container.init();
		container.start();
		URL url = new URL("http://127.0.0.1:8084/ignis/textFile.txt");
		readAndAssertBuffer(url,"testMessage");
	}


	/**
	 */
	public void testLoadConfigurationFromServerFile() throws Exception {
		container = new EmbeddedCatalina();
		container.setServerXmlPath("com/ignis/embeddedcatalina/server-embed.xml");
		container.setDefaultHost(null);
		container.init();
		container.start();
		URL url = new URL("http://127.0.0.1:8089/test/textFile.txt");
		readAndAssertBuffer(url,"testMessage");
	}
	
	/**
	 */
	public void testInitEmbeddedContainerFromSUTFile() throws Exception {
		File f = new File("newDir/anotherDir");
		f.mkdirs();
		File f1 = new File(f,"txtFile.txt");
		FileUtils.write(f1.getPath(), "testInitEmbeddedContainerFromSUTFile");
		container = (EmbeddedCatalina)system.getSystemObject("embeddedCatalina");
		container.start();
		URL url = new URL("http://127.0.0.1:8097/testInitEmbeddedContainerFromSUTFile/txtFile.txt");
		readAndAssertBuffer(url,"testInitEmbeddedContainerFromSUTFile");
	}

	
	private void readAndAssertBuffer(URL url,String expected) throws IOException {
		InputStream stream = null;
		try {
			stream = url.openStream();
			int available = stream.available();
			byte[] buffer = new byte[available];
			stream.read(buffer);
			String res = new String(buffer);
			assertEquals(expected,res);
		}finally{
			stream.close();
		}
	}

}
