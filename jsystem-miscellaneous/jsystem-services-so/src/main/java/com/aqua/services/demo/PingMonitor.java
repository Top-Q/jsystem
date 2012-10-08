/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.services.demo;

import jsystem.framework.monitor.Monitor;

public class PingMonitor extends Monitor {
	
	String pingTo;

	public PingMonitor(String pingTo) {
		super("ping monitor");
		this.pingTo = pingTo;
	}

	public void run() {
		WindowsStation station;
		while (true) {
			try {
				station = (WindowsStation) system.getSystemObject("station");
				station.ping(pingTo);
				Thread.sleep(1000);
			} catch (Exception e) {
				return;
			}

		}

	}

}
