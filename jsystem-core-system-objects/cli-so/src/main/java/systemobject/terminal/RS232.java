/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPort;
import javax.comm.PortInUseException;
import javax.comm.UnsupportedCommOperationException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

/**
 * A terminal used for Serial RS232 Connection 
 */
public class RS232 extends Terminal {
	private String portName = null;

	private int boudRate = -1;

	private int dataBit = -1;

	private int stopBit = -1;

	private int parity = -1;

	private CommPortIdentifier portId = null;

	private SerialPort serialPort = null;

	private static ArrayList<SerialPort> comBank = new ArrayList<SerialPort>();

	public RS232(String portName, int boudRate, int dataBit, int stopBit,
			int parity) {
		this.portName = portName;
		this.boudRate = boudRate;
		this.dataBit = dataBit;
		this.stopBit = stopBit;
		this.parity = parity;
	}

	public void connect() throws IOException {
		portId = findComm(portName);
		try {
			serialPort = (SerialPort) portId.open("TestRunner", 30000);
			comBank.add(serialPort);
		} catch (PortInUseException e) {
			throw new IOException("Port in use: " + portName);
		}
		try {
			serialPort.setSerialPortParams(boudRate, dataBit, stopBit, parity);
		} catch (UnsupportedCommOperationException e) {
			throw new IOException("UnsupportedCommOperationException");
		}
		in = new BufferedInputStream(serialPort.getInputStream(),
				IN_BUFFER_SIZE);

		out = new BufferedOutputStream(serialPort.getOutputStream());
	}

	public void disconnect() throws IOException {
		if (serialPort != null) {
			serialPort.close();
			comBank.remove(serialPort);
		}
		closeStreams();
	}

	public boolean isConnected() {
		return true;
	}

	public String getConnectionName() {
		return portName;
	}

	public static void closeAllComs() {
		for (int i = 0; i < comBank.size(); i++) {
			SerialPort sp = (SerialPort) comBank.get(i);
			if (sp != null) {
				try {
					sp.close();
				} catch (Throwable t) {
					//log.error("Fail to close port", t);
				}
			}
		}
		comBank = new ArrayList<SerialPort>();
	}

	private static CommPortIdentifier findComm(String portName)
			throws IOException {
		Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(portName)) {
					return portId;
				}
			}
		}
		throw new IOException("Unable to find commport: " + portName
				+ "\nFound Comm:\n" + getCommList());

	}

	private static String getCommList() {
		StringBuffer sb = new StringBuffer();
		Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			sb.append(portId.getName() + "\n");
		}
		return sb.toString();
	}
}