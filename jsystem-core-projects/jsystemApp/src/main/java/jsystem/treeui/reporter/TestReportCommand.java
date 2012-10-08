/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package jsystem.treeui.reporter;

/**
 */
public class TestReportCommand {

	public String command;

	public int status;

	public boolean bold = false;

	public TestReportCommand(String command, int status) {

		this.command = command;
		this.status = status;
	}
}
