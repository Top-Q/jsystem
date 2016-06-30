/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.fixtures;

import java.io.File;

import org.junit.Ignore;

import jsystem.framework.RunProperties;
import jsystem.framework.fixture.Fixture;
import jsystem.runner.agent.ProjectComponent;
import jsystem.runner.projectsync.ProjectZip;

import com.aqua.services.AgentConnection;

/**
 * AgentDefaultProjectFixture Fixture
 * 
 * @author KobiG
 */

@Ignore("Agent mechanism is deprected")
public class AgentDefaultProjectFixture extends Fixture {

	private AgentConnection agentConnection;

	public AgentDefaultProjectFixture() {
		setParentFixture(AgentFixture.class);
	}

	/**
	 * Sync environment with agent first time
	 */
	public void setUp() throws Exception {
		report.step("Synchronize - zipping and etc.");
		// New Local Variable
		agentConnection = (AgentConnection) system.getSystemObject("AgentConnection");
		String projectClasses = RunProperties.getInstance().getRunProperty("projectDir") + "/resources/jsystemAgentProject/classes";
		String sutFile = "AgentConnection.xml";
		String currentScenario = "scenarios/agentScenarioDefault";
		File zippedProject = null;
		// End New Local Variable
		// Zipping
		report.step("Creating zip file from " + projectClasses);
		ProjectZip zipper = new ProjectZip(new File(projectClasses));
		zippedProject = zipper.zipProject(ProjectComponent.values());
		String projectName = ProjectZip.getProjectNameFromClassesPath(new File(projectClasses));
		report.step("Sending the project " + projectName + " in zip file");
		agentConnection.client.synchronizeProject(zippedProject, projectName, currentScenario, sutFile, null, null);
		// End Zipping
		report.step("End of Synchronize - zipping and etc.");
	}
}
