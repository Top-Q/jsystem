package com.aqua.services.remote;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.CliConnectionImpl;
import com.aqua.sysobj.conn.MinaSshdCliConnection;
import junit.framework.SystemTestCase4;
import org.junit.Test;

public class TestSSHRemote extends SystemTestCase4 {

   @Test
    public void testSimpleSSHCommand() throws Exception {
        CliConnectionImpl connection = new MinaSshdCliConnection();
        connection.setHost("");
        connection.setUser("");
        connection.setPassword("!");
        connection.init();
        CliCommand command = new CliCommand("ls");
        connection.handleCliCommand("ls", command);
        command = new CliCommand("ifconfig");
        connection.handleCliCommand("ifconfig", command);
        connection.disconnect();

    }



}
