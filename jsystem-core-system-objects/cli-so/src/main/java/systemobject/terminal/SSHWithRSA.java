/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package systemobject.terminal;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.ethz.ssh2.Connection;

/**
 * A terminal used for SSH Connection
 */
public class SSHWithRSA extends SSH {

	private File privateKeyFile;

	public SSHWithRSA(String hostnameP, String usernameP, String passwordP,
			File privateKey) {
		super(hostnameP, usernameP, passwordP);
		privateKeyFile = privateKey;

	}

	@Override
	public void connect() throws IOException {
		boolean isAuthenticated = false;
		/* Create a connection instance */
		System.out.println("Connet to Host with SSH and RSA private key");
		conn = new Connection(hostname);

		/* Now connect */

		conn.connect();

		// Check what connection options are available to us
		String[] authMethods = conn.getRemainingAuthMethods(username);
		System.out.println("The supported auth Methods are:");
		for (String method : authMethods) {
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
		if (Arrays.asList(authMethods).contains("publickey")) {
			// we can authenticate with a RSA public/private key
			privateKeyAuthentication = true;
		}

		/* Authenticate */
		if (passAuthentication) {
			super.connect();
		} else if (privateKeyAuthentication) {
			try {
				if (privateKeyFile != null && privateKeyFile.isFile()) {
					System.out.println();
					isAuthenticated = conn.authenticateWithPublicKey(username,
							privateKeyFile, "");
				} else {
					System.out
							.println("Auth Error - The privateKeyFile should be init from the SUT with a valid path to ppk/pem RSA private key");
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				isAuthenticated = false;
			}
		}
		if (isAuthenticated == false) {
			// we're still not authenticated - try keyboard interactive
			conn.authenticateWithKeyboardInteractive(username,new InteractiveLogic());
		}

		if (sourcePort > -1 && destinationPort > -1) {
			lpf = conn.createLocalPortForwarder(sourcePort, "localhost",destinationPort);
		}

		/* Create a session */
		sess = conn.openSession();

		if (xtermTerminal) {
			sess.requestPTY("xterm", 80, 24, 640, 480, null);
		} else {
			sess.requestPTY("dumb", 200, 50, 0, 0, null);
		}

		sess.startShell();

		in = sess.getStdout();
		out = sess.getStdin();
	}

	@Override
	public void disconnect() {
		super.disconnect();
	}

	@Override
	public boolean isConnected() {
		return super.isConnected();
	}

	@Override
	public String getConnectionName() {
		return "SSH_RSA";
	}

	public File getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(File privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

}
