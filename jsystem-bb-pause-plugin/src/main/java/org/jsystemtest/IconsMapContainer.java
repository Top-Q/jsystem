package org.jsystemtest;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.Classloader;

public class IconsMapContainer {
	
	private HashMap<String,ImageIcon> pausedNormalMap;
	private HashMap<String,ImageIcon> pausedMarkedNegMap;
	private HashMap<String,ImageIcon> pausedMarkedIsseuMap;
	private HashMap<String,ImageIcon> pausedMarkedNegIssueMap;
	
	public IconsMapContainer(){
		
		try {
			ClassLoader cl = this.getClass().getClassLoader();	
			
			pausedNormalMap = new HashMap<>();
			pausedNormalMap.put("pausedPlainRun", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedRunPlain.gif"))));
			pausedNormalMap.put("pausedPlain", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedPlain.gif"))));
			pausedNormalMap.put("pausedOk", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOk.gif"))));
			pausedNormalMap.put("pausedErr", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedError.gif"))));
			pausedNormalMap.put("pausedWarn", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarning.gif"))));
			pausedNormalMap.put("pausedFail", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFail.gif"))));
			pausedNormalMap.put("pausedFailRun", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailRun.gif"))));
			pausedNormalMap.put("pausedOkRun", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkRun.gif"))));
			pausedNormalMap.put("pausedErrRun",  new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorRun.gif"))));
			pausedNormalMap.put("pausedWarnRun", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningRun.gif"))));
			
			pausedMarkedNegMap = new HashMap<>();
			pausedMarkedNegMap.put("pausedPlainRun", new ImageIcon(IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedRunNeg.gif"))));
			pausedMarkedNegMap.put("pausedPlain", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedTestNeg.gif"))));
			pausedMarkedNegMap.put("pausedOk", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkNeg.gif"))));
			pausedMarkedNegMap.put("pausedErr", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorNeg.gif"))));
			pausedMarkedNegMap.put("pausedWarn", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningNeg.gif"))));
			pausedMarkedNegMap.put("pausedFail", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailNeg.gif"))));
			pausedMarkedNegMap.put("pausedFailRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailRunNeg.gif"))));
			pausedMarkedNegMap.put("pausedOkRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkRunNeg.gif"))));
			pausedMarkedNegMap.put("pausedErrRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorRunNeg.gif"))));
			pausedMarkedNegMap.put("pausedWarnRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningRunNeg.gif"))));
			
			pausedMarkedIsseuMap = new HashMap<>();
			pausedMarkedIsseuMap.put("pausedPlainRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedRunIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedPlain", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedOk", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedErr", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedWarn", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedFail", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedFailRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailRunIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedOkRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkRunIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedErrRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorRunIssue.gif"))));
			pausedMarkedIsseuMap.put("pausedWarnRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningRunIssue.gif"))));
			
			pausedMarkedNegIssueMap = new HashMap<>();
			pausedMarkedNegIssueMap.put("pausedPlainRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedRunNegIssue.gif"))));
			pausedMarkedNegIssueMap.put("pausedPlain", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/plainTest/pausedIssueNeg.gif"))));
			pausedMarkedNegIssueMap.put("pausedOk", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkIssueNeg.gif"))));
			pausedMarkedNegIssueMap.put("pausedErr", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorIssueNeg.gif"))));
			pausedMarkedNegIssueMap.put("pausedWarn", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningNegIssue.gif"))));
			pausedMarkedNegIssueMap.put("pausedFail", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailNegIssue.gif"))));
			pausedMarkedNegIssueMap.put("pausedFailRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testFailure/pausedFailRunNegIssue.gif"))));
			pausedMarkedNegIssueMap.put("pausedOkRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testSuccess/pausedOkRunIssueNeg.gif"))));
			pausedMarkedNegIssueMap.put("pausedErrRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testError/pausedErrorRunIssueNeg.gif"))));
			pausedMarkedNegIssueMap.put("pausedWarnRun", new ImageIcon(
					IOUtils.toByteArray(cl.getResourceAsStream("iconsByStatus/testWarning/pausedWarningRunNegIssue.gif"))));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String,ImageIcon> getPausedNormalMap(){
		return pausedNormalMap;
	}
	
	public HashMap<String,ImageIcon> getPausedMarkedNegMap(){
		return pausedMarkedNegMap;
	}
	public HashMap<String,ImageIcon> getPausedMarkedIsseuMap(){
		return pausedMarkedIsseuMap;
	}
	public HashMap<String,ImageIcon> getPausedMarkedNegIssueMap(){
		return pausedMarkedNegIssueMap;
	}
	

	
}
