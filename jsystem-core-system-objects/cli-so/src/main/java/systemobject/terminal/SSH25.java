package systemobject.terminal;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

/**
 * SSH terminal implementation using Apache Mina SSHD library.
 * Supports SSH protocol version 2.5.
 *
 * @author Ronen Byalsky, Itai Agmon
 */
public class SSH25 extends Terminal {

    private SshClient sshClient;
    private ClientSession clientSession;
    private ChannelShell shellChannel;

    private final String username;
    private final String password;
    private final String hostname;
    private final int port;

    private boolean connected;

    /**
     * Constructor for SSH25 terminal.
     *
     * @param username the SSH username
     * @param password the SSH password
     * @param hostname the SSH server hostname or IP address
     * @param port     the SSH server port
     */
    public SSH25(String username, String password, String hostname, int port) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Connects to the SSH server and initializes the shell channel.
     *
     * @throws IOException if an I/O error occurs during connection
     */
    @Override
    public void connect() throws IOException {
        report("connecting");
        sshClient = SshClient.setUpDefaultClient();
        sshClient.start();

        ConnectFuture connectFuture = sshClient.connect(username, hostname, port);
        int timeoutMillis = 10_000;
        connectFuture.verify(timeoutMillis, TimeUnit.MILLISECONDS);
        clientSession = connectFuture.getSession();
        clientSession.addPasswordIdentity(password);
        clientSession.auth().verify(timeoutMillis, TimeUnit.MILLISECONDS);

        // create a persistent shell channel and wire its IO to Terminal.in/out
        shellChannel = clientSession.createShellChannel();

        PipedInputStream channelInPipe = new PipedInputStream(IN_BUFFER_SIZE);
        // writes to Terminal.out will be sent to the remote stdin
        this.out = new PipedOutputStream(channelInPipe);

        PipedOutputStream channelOutPipe = new PipedOutputStream();
        // reads from Terminal.in will read remote stdout/stderr
        this.in = new PipedInputStream(channelOutPipe, IN_BUFFER_SIZE);

        shellChannel.setIn(channelInPipe);
        shellChannel.setOut(channelOutPipe);
        shellChannel.setErr(channelOutPipe);

        shellChannel.open().verify(timeoutMillis, TimeUnit.MILLISECONDS);


        report("connected");
        connected = true;
    }

    /**
     * Sends a command string to the SSH shell.
     * Ensures that the command ends with a newline character.
     *
     * @param command       the command string to send
     * @param delayedTyping whether to simulate delayed typing
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    @Override
    public synchronized void sendString(String command, boolean delayedTyping) throws IOException, InterruptedException {
        //ensure newline so shell executes the command
        String cmd = command.endsWith("\n") ? command : command + "\n";
        super.sendString(cmd, delayedTyping);
    }

    /**
     * Reads the input buffer from the SSH shell.
     *
     * @return the content of the input buffer
     * @throws Exception if an error occurs while reading the input buffer
     */
    @Override
    public String readInputBuffer() throws Exception {
        return super.readInputBuffer();
    }

    /**
     * Retrieves the result of the last executed command from the SSH shell.
     *
     * @return the result of the last executed command
     */
    @Override
    public synchronized String getResult() {
        return super.getResult();
    }

    /**
     * Disconnects from the SSH server and cleans up resources.
     *
     * @throws IOException if an I/O error occurs during disconnection
     */
    @Override
    public void disconnect() throws IOException {
        report("disconnecting");
        try {
            if (shellChannel != null) {
                shellChannel.close(false);
            }
        } catch (Exception ignored) {
        }
        if (clientSession != null) {
            clientSession.close(false);
        }
        if (sshClient != null) {
            sshClient.stop();
        }
        connected = false;
    }

    /**
     * Checks if the SSH terminal is currently connected.
     * @return true if connected, false otherwise
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Gets the name of the connection type.
     * @return the connection name "SSH25"
     */
    @Override
    public String getConnectionName() {
        return "SSH25";
    }

    /**
     * Reports a message to the console with SSH25 prefix.
     * @param str the message to report
     */
    private void report(String str) {
        System.out.println("[SSH25]: " + str);
    }
}
