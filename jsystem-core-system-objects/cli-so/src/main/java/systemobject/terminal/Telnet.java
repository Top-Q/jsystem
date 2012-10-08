/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A terminal used for Telnet Connection
 */
public class Telnet extends Terminal {

	private String address = null;

	private int port = 0;

	private Socket socket = null;

	private boolean useTelnetInputStream = false;

	private int soTimeout = 0;

	private String vtType = "vt100";

	private int socketReceiveBufferSize = 4096;

	public Telnet(String address, int port) {
		this(address, port, false);
	}

	public Telnet(String address, int port, boolean useTelnetInputStream) {
		this(address, port, useTelnetInputStream, 4096);
	}

	public Telnet(String address, int port, boolean useTelnetInputStream, int socketReceiveBufferSize) {
		this(address, port, useTelnetInputStream, 0, "vt100", socketReceiveBufferSize);
	}

	public Telnet(String address, int port, boolean useTelnetInputStream, int soTimeout, String vtType, int socketReceiveBufferSize) {
		super();
		setAddress(address);
		setPort(port);
		setUseTelnetInputStream(useTelnetInputStream);
		setSoTimeout(soTimeout);
		setVtType(vtType);
		setSocketReceiveBufferSize(socketReceiveBufferSize);
	}

	public void connect() throws IOException {
		socket = new Socket(getAddress(), getPort());
		socket.setReceiveBufferSize(getSocketReceiveBufferSize());
		socket.setSoTimeout(getSoTimeout());
		if (isUseTelnetInputStream()) {
			in = new TelnetInputStream(socket.getInputStream(), socket.getOutputStream(), 134, 46, getVtType());
		} else {
			in = new BufferedInputStream(socket.getInputStream(), IN_BUFFER_SIZE);
		}

		out = new BufferedOutputStream(socket.getOutputStream());

	}

	public void disconnect() throws IOException {
		if (socket != null) {
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		}
		closeStreams();

	}

	public boolean isConnected() {
		if (socket.isConnected() && !socket.isInputShutdown() && !socket.isOutputShutdown()) {
			return true;
		} else {
			return false;
		}
	}

	public String getConnectionName() {
		try {
			return socket.getLocalAddress().getHostAddress();
		} catch (RuntimeException ignore1) {
		}
		return null;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public String getVtType() {
		return vtType;
	}

	public void setVtType(String vtType) {
		this.vtType = vtType;
	}

	protected String getAddress() {
		return address;
	}

	protected void setAddress(String address) {
		this.address = address;
	}

	protected int getPort() {
		return port;
	}

	protected void setPort(int port) {
		this.port = port;
	}

	public boolean isUseTelnetInputStream() {
		return useTelnetInputStream;
	}

	public void setUseTelnetInputStream(boolean useTelnetInputStream) {
		this.useTelnetInputStream = useTelnetInputStream;
	}

	public int getSocketReceiveBufferSize() {
		return socketReceiveBufferSize;
	}

	public void setSocketReceiveBufferSize(int socketReceiveBufferSize) {
		this.socketReceiveBufferSize = socketReceiveBufferSize;
	}
}
