package il.co.topq.refactor.refactorUtil;

import jsystem.extensions.sourcecontrol.SourceControlI;
import junit.framework.Assert;

import org.junit.Test;

public class SCMPluginTests extends AbstractTestCase{
	
	@Test
	public void testGetPlugin(){
		SourceControlI sourceControl = util.sourceControHandler;
		Assert.assertNotNull(sourceControl);
	}
	
}
