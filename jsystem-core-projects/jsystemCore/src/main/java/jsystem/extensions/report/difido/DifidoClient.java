package jsystem.extensions.report.difido;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

import il.co.topq.difido.model.execution.MachineNode;
import il.co.topq.difido.model.remote.ExecutionDetails;
import il.co.topq.difido.model.test.TestDetails;

public class DifidoClient {

	private static final Logger log = Logger.getLogger(DifidoClient.class.getName());

	private final Properties execProps;
	private final String EXEC_PROPS_FILE = "execution.properties";

	private static final String BASE_URI_TEMPLATE = "http://%s:%d/api/";
	private final String baseUri;
	private final HttpClient client;

	public DifidoClient(String host, int port) {
		baseUri = String.format(BASE_URI_TEMPLATE, host, port);
		client = new HttpClient();
		execProps = new Properties();
	}

	public int addExecution(ExecutionDetails details) throws Exception {
		final PostMethod method = new PostMethod(baseUri + "executions/");
		if (details != null) {
			final String descriptionJson = new ObjectMapper().writeValueAsString(details);
			method.setRequestEntity(new StringRequestEntity(descriptionJson,"application/json","utf-8"));
		}
		final int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
		int executionId = Integer.parseInt(method.getResponseBodyAsString());
		File f = new File(EXEC_PROPS_FILE);
		if (f.exists()) {		
			execProps.load(new FileInputStream(EXEC_PROPS_FILE));
			execProps.clear();
		}
		execProps.setProperty("execution.id", Integer.toString(executionId));
		execProps.store(new FileOutputStream(EXEC_PROPS_FILE), null);
		return executionId;
	}

	public void updateSerialNumber(int executionId) throws Exception {
		execProps.load(new FileInputStream(EXEC_PROPS_FILE));
		final PutMethod method = new PutMethod(baseUri + "executions/" + executionId + "?serial=" + execProps.getProperty("execution.serial"));
		method.setRequestHeader(new Header("Content-Type", "text/plain"));
		final int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
	}

	public void endExecution(int executionId) throws Exception {
		final PutMethod method = new PutMethod(baseUri + "executions/" + executionId + "?active=false");
		method.setRequestHeader(new Header("Content-Type", "text/plain"));
		final int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
		execProps.clear();
	}

	public int addMachine(int executionId, MachineNode machine) throws Exception {
		PostMethod method = new PostMethod(baseUri + "executions/" + executionId + "/machines/");
		final ObjectMapper mapper = new ObjectMapper();
		final String json = mapper.writeValueAsString(machine);
		final RequestEntity entity = new StringRequestEntity(json,"application/json","utf-8");
		method.setRequestEntity(entity);
		int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
		return Integer.parseInt(method.getResponseBodyAsString());
	}

	public void updateMachine(int executionId, int machineId, MachineNode machine) throws Exception {
		PutMethod method = new PutMethod(baseUri + "executions/" + executionId + "/machines/" + machineId);
		final ObjectMapper mapper = new ObjectMapper();
		final String json = mapper.writeValueAsString(machine);
		final RequestEntity entity = new StringRequestEntity(json,"application/json","utf-8");
		method.setRequestEntity(entity);
		int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
	}

	public void addTestDetails(int executionId, TestDetails testDetails) throws Exception {
		PostMethod method = new PostMethod(baseUri + "executions/" + executionId + "/details");
		final ObjectMapper mapper = new ObjectMapper();
		final String json = mapper.writeValueAsString(testDetails);
		final RequestEntity entity = new StringRequestEntity(json,"application/json","utf-8");
		method.setRequestEntity(entity);
		final int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
	}

	public void addFile(final int executionId, final String uid, final File file) throws Exception {
		PostMethod method = new PostMethod(baseUri + "executions/" + executionId + "/details/" + uid + "/file/");
		Part[] parts = new Part[] { new FilePart("file", file) };
		method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
		final int responseCode = client.executeMethod(method);
		handleResponseCode(method, responseCode);
	}

	private void handleResponseCode(HttpMethod method, int responseCode) throws Exception {
		if (responseCode != 200 && responseCode != 204) {
			throw new Exception("Request was not successful. Response is: " + responseCode + ".\n Response body: "
					+ method.getResponseBodyAsString());
		}

	}

}
