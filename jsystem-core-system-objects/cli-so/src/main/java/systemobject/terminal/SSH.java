/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.LocalPortForwarder;
import ch.ethz.ssh2.Session;

/**
 * A terminal used for SSH Connection
 */
public class SSH extends Terminal {
	
	protected String hostname;

	protected String username;

	protected String password;
	
	protected Connection conn = null;

	protected Session sess = null;
	
	//ssh port forwarding
	protected LocalPortForwarder lpf = null;

	protected int sourcePort = -1;

	protected int destinationPort = -1;
	
	protected boolean xtermTerminal = true;
	
	public SSH(String hostnameP, String usernameP, String passwordP) {
		this(hostnameP, usernameP, passwordP, -1, -1, true);
	}
	
	
	/**
	 * Constructor with Destination port
	 * if destinationPort specified in this constructor no LocalPortForwarder will be used
	 *
	 * @param  hostnameP IP or Hostname of the destination machine
	 * @param  usernameP username for SSH auth
	 * @param  passwordP Password for SSH auth
	 * @param  destinationPort custom destination Port for ssh connection
	 */
	public SSH(String hostnameP, String usernameP, String passwordP, int destinationPort) {
		this(hostnameP, usernameP, passwordP, -1, destinationPort, true);
	}
	
	
	public SSH(String hostnameP, String usernameP, String passwordP, int sourceTunnelPort, int destinationTunnelPort) {
		this(hostnameP, usernameP, passwordP, sourceTunnelPort, destinationTunnelPort, true);
	}

	public SSH(String hostnameP, String usernameP, String passwordP, int sourceTunnelPort, int destinationTunnelPort, boolean _xtermTerminal) {
		super();
		hostname = hostnameP;
		username = usernameP;
		password = passwordP;
		sourcePort = sourceTunnelPort;
		destinationPort =destinationTunnelPort;
		xtermTerminal = _xtermTerminal;
	}

	@Override
	public void connect() throws IOException {
		boolean isAuthenticated = false;
		/* Create a connection instance */

		if (destinationPort > -1) {
			conn = new Connection(hostname,destinationPort);
		}
		else {
			conn = new Connection(hostname);
		}

		/* Now connect */
		conn.connect();

		// Check what connection options are available to us
		String[] authMethods = conn.getRemainingAuthMethods(username);
		System.out.println("The supported auth Methods are:");
		for(String method: authMethods) {
			System.out.println(method);
		}
		boolean privateKeyAuthentication = false;
		boolean passAuthentication = false;
		for (int i = 0; i < authMethods.length; i++) {
			if (authMethods[i].equalsIgnoreCase("password")) {
				// we can authenticate with a password
				passAuthentication = true;
			}
		}
		if(Arrays.asList(authMethods).contains("publickey")){
			// we can authenticate with a RSA private key
			privateKeyAuthentication=true;
		}
		
		/* Authenticate */
		if (passAuthentication) {
			try {
				isAuthenticated = conn.authenticateWithPassword(username, password);
			} catch (Exception e) {
				isAuthenticated = false;
			}
		}
		if (isAuthenticated == false) {
			// we're still not authenticated - try keyboard interactive
			conn.authenticateWithKeyboardInteractive(username, new InteractiveLogic());
		}


		if (sourcePort > -1 && destinationPort > -1) {
			lpf = conn.createLocalPortForwarder(sourcePort, "localhost" , destinationPort);
		}
		
		/* Create a session */
		sess = conn.openSession();
		
		if (xtermTerminal) {
			sess.requestPTY("xterm", 80, 24, 640, 480, null);
		}else {
			sess.requestPTY("dumb", 200, 50, 0, 0, null);
		}
		
		sess.startShell();
		
		in =  sess.getStdout();
		out = sess.getStdin();
	}

	@Override
	public void disconnect() {
		if (lpf != null) {
			try {
				lpf.close();
			} catch (IOException e) {
			}
		}
		if (sess != null) {
			sess.close();
		}
		if (conn != null) {
			conn.close();
		}
	}

	@Override
	public boolean isConnected() {
		return true;
	}

	@Override
	public String getConnectionName() {
		return "SSH";
	}

	/**
	 * The logic that one has to implement if "keyboard-interactive" 
	 * authentication shall be supported.
	 */
	class InteractiveLogic implements InteractiveCallback {
		/* the callback may be invoked several times, depending on how many questions-sets the server sends */
		public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt, boolean[] echo) throws IOException {
			/* Often, servers just send empty strings for "name" and "instruction" */

			String[] result = new String[numPrompts];

			for (int i = 0; i < numPrompts; i++) {
				if (prompt[i].toLowerCase().startsWith("password:")) {
					result[i] = password;
				} else {
					// we don't know how to handle the prompt
					System.out.print("SSH client - Unknown prompt type returned (" + prompt[i] + ")\n");
					result[i] = "";
				}
			}

			return result;
		}

	}

	protected String getHostname() {
		return hostname;
	}

	protected void setHostname(String hostname) {
		this.hostname = hostname;
	}

	protected String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected String getPassword() {
		return password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	protected Connection getConn() {
		return conn;
	}

	protected void setConn(Connection conn) {
		this.conn = conn;
	}

	protected int getSourcePort() {
		return sourcePort;
	}

	protected void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	protected int getDestinationPort() {
		return destinationPort;
	}

	protected void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}



	
}
