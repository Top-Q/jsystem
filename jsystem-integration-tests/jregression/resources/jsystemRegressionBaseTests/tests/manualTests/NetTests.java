package manualTests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import jsystem.framework.TestProperties;
import junit.framework.SystemTestCase;

public class NetTests extends SystemTestCase {
	
	String myURL = "http://www.cnn.com";
	
	public String getMyURL() {
		return myURL;
	}
	
	public String[] getMyURLOptions(){
		return new String[]{"http://www.cnn.com", "http://www.ynet.co.il", "http://www.google.com"};
	}

	public void setMyURL(String myURL) {
		this.myURL = myURL;
	}
	
	@TestProperties(name = "Open View Sourse of \"${MyURL}\"")
	/**
	 * 
	 * @params.include myURL
	 */
	public void testViewSourseOfURL() throws IOException {
		URL yahoo = new URL(myURL);
		BufferedReader in = new BufferedReader(
		new InputStreamReader(
		yahoo.openStream()));
			
		String inputLine;
	
		while ((inputLine = in.readLine()) != null)
			report.report(inputLine);
		
		in.close();
	}	
	

}
