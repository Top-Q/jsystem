package com.aqua.services.remote;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;

import com.aqua.sysobj.conn.LinuxDefaultCliConnection;
import junit.framework.SystemTestCase4;
import org.junit.Test;

public class TestSSHRemote extends SystemTestCase4 {

   @Test
    public void testSimpleSSHCommand() throws Exception {
        CliConnectionImpl connection = new LinuxDefaultCliConnection();
        connection.setHost("172.29.104.35");
        connection.setUser("itaiag");
        connection.setPassword("topq1!");
        connection.init();
        CliCommand command = new CliCommand("ls");
        connection.handleCliCommand("ls", command);
        command = new CliCommand("ifconfig");
        connection.handleCliCommand("ifconfig", command);
        connection.disconnect();

    }



}
