package systemobject.terminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;

public class SSH25 extends Terminal {

	private SshClient sshClient;
	private ClientSession clientSession;
	
	private String username;
	private String password;
	private String hostname;
	private int port;
	private int timeoutMillis = 10_000;
	
	ByteArrayOutputStream localOut;
	ByteArrayOutputStream localErr;
	
	private boolean connected;

	public SSH25(String username, String password, String hostname, int port) {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.port = port;
	}

	@Override
	public void connect() throws IOException {
		report("connecting");
		sshClient = SshClient.setUpDefaultClient();
		sshClient.start();

		ConnectFuture connectFuture = sshClient.connect(username, hostname, port);
		clientSession = connectFuture.verify().getSession();
		clientSession.addPasswordIdentity(password);
		clientSession.auth().verify(TimeUnit.SECONDS.toMillis(timeoutMillis));
		report("connected");
		connected = true;
	}
	
	@Override
	public synchronized void sendString(String command, boolean delayedTyping) throws IOException, InterruptedException {
		
		report("send string: " + command);
		ChannelExec channelExec = clientSession.createExecChannel(command);
		localOut = new ByteArrayOutputStream();
		localErr = new ByteArrayOutputStream();
		
		channelExec.setOut(localOut);
		channelExec.setErr(localErr);
		channelExec.open();
		
		Set<ClientChannelEvent> events = channelExec.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(timeoutMillis));
		
		// Check if timed out
		if (events.contains(ClientChannelEvent.TIMEOUT)) {
			throw new RuntimeException("SSH Timeout");
		}
	}

	@Override
	public String readInputBuffer() throws Exception {
		String result = localOut.toString();
		return result;
	}
	
	@Override
    public synchronized String getResult() {
		if (localOut != null) {
			String result = localOut.toString();
			report("result: " + result);
			return result;
		}
		
		report("result: ");
		return "";
    }
	
	@Override
	public void disconnect() throws IOException {
		report("diconnecting");
		clientSession.close(false);
		sshClient.close();
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public String getConnectionName() {
		return "SSH25";
	}
	
	private void report(String str) {
		System.out.println("[SSH25]: " + str);
	}
}
