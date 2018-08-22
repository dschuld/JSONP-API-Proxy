package de.davidschuld.jsonpapiproxy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.xml.ws.http.HTTPException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;

@RunWith(MockitoJUnitRunner.class)
public class JsonpProxyTest {

	@Mock
	private Context context;

	@Mock
	private JsonpApiCall call;

	private JsonpProxy proxy;

	private static final String SAMPLE_INPUT_STRING = "{\"queryStringParameters\":{\"url\": \"http://test.url\"}}";
	private static final String EXPECTED_BODY_STRING = "{\"title\": \"BAR\"}";
	private static final String EXPECTED_OUTPUT_STRING = "{\"isBase64Encoded\":false,\"body\":\"{\\\"title\\\": \\\"BAR\\\"}\",\"statusCode\":200}";
	private static final String EXPECTED_JSONP_OUTPUT_STRING = "callback({\"title\": \"BAR\"})";
	private static final String LONG_JSONP = "jsonFlickrFeed({\"title\": \"Uploads from schulddavid, tagged s11, with geodata\",\"link\": \"https://www.flickr.com/photos/134819556@N06/tags/s11/\",\"description\": \"\"})";
	private static final String LONG_JSON = "{\"isBase64Encoded\":false,\"body\":\"{\\\"title\\\": \\\"Uploads from schulddavid, tagged s11, with geodata\\\",\\\"link\\\": \\\"https:\\/\\/www.flickr.com\\/photos\\/134819556@N06\\/tags\\/s11\\/\\\",\\\"description\\\": \\\"\\\"}\",\"statusCode\":200}";
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		proxy = new JsonpProxy(call);

	}

	private String getEscapedSampleUrlInput() throws UnsupportedEncodingException {
		return "{\"queryStringParameters\":{\"url\": \""
				+ URLEncoder.encode("http://test.url", StandardCharsets.UTF_8.name()) + "\"}}";
	}

	@Test
	public void jsonpApiProxy_invalidURL() throws IOException {

		when(call.call(anyString(), anyString())).thenThrow(new HTTPException(404));

		InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());
		OutputStream output = new ByteArrayOutputStream();

		proxy.jsonp2Json(input, output, (message) -> System.out.println(message));

		String sampleOutputString = output.toString();
		Assert.assertTrue(sampleOutputString.contains("\"statusCode\":404"));
	}

	@Test
	public void jsonpApiProxy_EncodedURL_JsonOutput() throws Exception {

		String expectedBodyString = EXPECTED_BODY_STRING;
		String expectedOutputString = EXPECTED_OUTPUT_STRING;
		byte[] inputBytes = getEscapedSampleUrlInput().getBytes();
		runTest(expectedBodyString, expectedOutputString, inputBytes);
	}


	@Test
	public void jsonpApiProxy_JsonOutput() throws IOException {

		String expectedBodyString = EXPECTED_BODY_STRING;
		String expectedOutputString = EXPECTED_OUTPUT_STRING;
		byte[] inputBytes = SAMPLE_INPUT_STRING.getBytes();
		runTest(expectedBodyString, expectedOutputString, inputBytes);
	}

	
	@Test
	public void jsonpApiProxy_JsonpOutput() throws IOException {

		String expectedJsonpOutputString = EXPECTED_JSONP_OUTPUT_STRING;
		String expectedOutputString = EXPECTED_OUTPUT_STRING;
		byte[] inputBytes = SAMPLE_INPUT_STRING.getBytes();
		runTest(expectedJsonpOutputString, expectedOutputString, inputBytes);
	}

	@Test
	public void jsonpApiProxy_longJsonpOutput() throws Exception {

		String longJsonp = LONG_JSONP;
		String longJson = LONG_JSON;
		byte[] inputBytes = SAMPLE_INPUT_STRING.getBytes();
		runTest(longJsonp, longJson, inputBytes);
	}

	private void runTest(String longJsonp, String longJson, byte[] inputBytes)
			throws UnsupportedEncodingException, IOException {
		when(call.call(anyString(), anyString())).thenReturn(longJsonp);

		InputStream input = new ByteArrayInputStream(inputBytes);
		OutputStream output = new ByteArrayOutputStream();

		proxy.jsonp2Json(input, output, (message) -> System.out.println(message));

		String sampleOutputString = output.toString();
		Assert.assertEquals(longJson, sampleOutputString);
	}
}
