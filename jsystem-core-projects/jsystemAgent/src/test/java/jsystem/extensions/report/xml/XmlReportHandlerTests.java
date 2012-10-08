package jsystem.extensions.report.xml;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author itai_a
 *
 */
public class XmlReportHandlerTests {
	
	private File reportFile;
	
	private XmlReportHandler handler;
	
	@Before
	public void before() throws Exception{
		reportFile = new File("resources","reports.0.xml");
		FileUtils.copyFile(new File("resources/reports.0.xml.orig"), reportFile);
		handler = new XmlReportHandler(new File("resources"));
	}
	
	@Test
	public void testGetAllElements(){
		Assert.assertEquals(17, handler.getNumberOfTests());
		Assert.assertEquals(3, handler.getNumberOfTestsFail());
		Assert.assertEquals(14, handler.getNumberOfTestsPass());
		Assert.assertEquals(0, handler.getNumberOfTestsWarning());
		Assert.assertEquals(1346314318216L, handler.getStartTime());
		Assert.assertEquals("root", handler.getScenarioName());
		Assert.assertEquals("172.20.1.142", handler.getStation());
		Assert.assertEquals("mystation.xml", handler.getSutName());
	}
	
	@Test
	public void testSetSut(){
		final String SUT_NAME = "newSutName.xml";
		handler.setSutName(SUT_NAME);
		Assert.assertEquals(SUT_NAME, handler.getSutName());
		
	}
	
	@Test
	public void testSetStation(){
		final String STATION_NAME = "newStation";
		handler.setStation(STATION_NAME);
		Assert.assertEquals(STATION_NAME, handler.getStation());
		
	}
	
	@Test
	public void testSetScenario(){
		final String SCENARIO_NAME = "newScenario";
		handler.setScenarioName(SCENARIO_NAME);
		Assert.assertEquals(SCENARIO_NAME, handler.getScenarioName());
		
	}

	@Test
	public void testSetBuild(){
		final String BUILD_NAME = "newBuild";
		handler.setBuild(BUILD_NAME);
		Assert.assertEquals(BUILD_NAME, handler.getBuild());
		
	}

	@Test
	public void testSetVersion(){
		final String VERSION_NAME = "newVersion";
		handler.setVersion(VERSION_NAME);
		Assert.assertEquals(VERSION_NAME, handler.getVersion());
		
	}
	
	@Test
	public void testSetUser(){
		final String USER_NAME = "newUser";
		handler.setUser(USER_NAME);
		Assert.assertEquals(USER_NAME, handler.getUserName());
		
	}

	
}
