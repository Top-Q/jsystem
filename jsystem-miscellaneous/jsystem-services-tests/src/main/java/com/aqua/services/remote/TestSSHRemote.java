package com.aqua.services.remote;

import com.aqua.sysobj.conn.CliCommand;
import com.aqua.sysobj.conn.LinuxDefaultCliConnection;
import junit.framework.SystemTestCase4;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.junit.Test;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.channel.ChannelExec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestSSHRemote extends SystemTestCase4 {

   @Test
    public void testSimpleSSHCommand() throws Exception {
        LinuxDefaultCliConnection connection = new LinuxDefaultCliConnection();
        connection.setHost("172.29.104.35");
        connection.setUser("itaiag");
        connection.setPassword("topq1!");
        connection.init();
        CliCommand command = new CliCommand("ls");
        connection.handleCliCommand("ls", command);
        connection.disconnect();

    }

    @Test
    public void testMinaSSHD() throws Exception {
        String hostname = "172.29.104.35";  // Replace with the remote SSH server's hostname or IP
        int port = 22;  // Default SSH port
        String username = "itaiag";
        String password = "";
        String command = "ls -la";

        // Create SSH client
        try (SshClient client = SshClient.setUpDefaultClient()) {
            client.start();

            // Connect to the SSH server
            try (ClientSession session = client.connect(username, hostname, port).verify(10, TimeUnit.SECONDS).getSession()) {
                // Authenticate with password
                session.addPasswordIdentity(password);
                session.auth().verify(10, TimeUnit.SECONDS);

                // Prepare to execute command
                try (ChannelExec channel = session.createExecChannel(command)) {
                    ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                    channel.setOut(responseStream);

                    // Execute the command and wait for it to complete
                    channel.open().verify(10, TimeUnit.SECONDS);
                    List<ClientChannelEvent> events = new ArrayList<>();
                    events.add(ClientChannelEvent.CLOSED);
                    channel.waitFor(events, TimeUnit.SECONDS.toMillis(10));

                    // Parse and print the result
                    String result = responseStream.toString(String.valueOf(StandardCharsets.UTF_8));
                    System.out.println("Command output:\n" + result);
                }
            } finally {
                client.stop();
            }
        }
    }


}
