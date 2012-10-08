/*
 * Copyright 2005-2010 Ignis Software Tools Ltd. All rights reserved.
 */
package com.aqua.filetransfer.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *  Networking and socket utils.
 */
public class NetUtils {
	
	/**
	 * Returns true if port is already bound by
	 * a process; otherwise returns false.
	 */
	public static boolean isBound(int port) throws Exception {
		InetSocketAddress address = new InetSocketAddress(port);
		return isBound(address);
	}
	
	/**
	 * Returns true if ip+port are already bound by
	 * a process; otherwise returns false.
	 */
	public static boolean isBound(String ip,int port) throws Exception {
		InetSocketAddress address = new InetSocketAddress(ip,port);
		return isBound(address);
	}
	
	
	/**
	 */
	private static boolean isBound(InetSocketAddress address) throws Exception {
		Socket socket = new Socket();
		try {
			socket.bind(address);
			return false;
		}catch (IOException e){
			return true;
		} finally {
			socket.close();
		}
	}
}
