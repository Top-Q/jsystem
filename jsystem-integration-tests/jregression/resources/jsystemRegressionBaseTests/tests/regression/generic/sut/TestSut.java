package regression.generic.sut;

import java.util.List;

import jsystem.framework.RunProperties;
import jsystem.framework.sut.SutFactory;
import jsystem.utils.XmlUtils;
import junit.framework.SystemTestCase;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author goland
 */
public class TestSut extends SystemTestCase {

	private String host = "rrrtttuuu";

	public void testSetSut() throws Exception{
		RunProperties.getInstance().setRunProperty("SUT_NAME",SutFactory.getInstance().getSutFile().getName());
		report.report(RunProperties.getInstance().getRunProperty("SUT_NAME"));
	}

	public String getHost(){
		return host;
	}	
	public void setHost(String host){
		this.host = host;
	}	
	@SuppressWarnings("unchecked")
	public String[] getHostOptions() throws Exception {
		List<?> list = (List<?>)sut().getAllValues("sut/connections//host");
		return XmlUtils.getTextElements((List<Element>) list);
	}
}
