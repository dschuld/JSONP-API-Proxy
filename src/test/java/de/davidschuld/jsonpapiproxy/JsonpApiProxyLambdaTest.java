package de.davidschuld.jsonpapiproxy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.ws.http.HTTPException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonpApiProxyLambdaTest {

	@Mock
	private Context context;

	@Mock
	private LambdaLogger logger;

	@Mock
	private JsonpApiCall call;
	
	private Jsonp2Json jsonp2Json;


	private static final String SAMPLE_INPUT_STRING = "{\"url\": \"http://test.url\"}";
	private static final String EXPECTED_OUTPUT_STRING = "{\"title\": \"BAR\"}";
	private static final String EXPECTED_JSONP_OUTPUT_STRING = "callback({\"title\": \"BAR\"})";
	private static final String LONG_JSONP = "jsonFlickrFeed({\"title\": \"Uploads from schulddavid, tagged s11, with geodata\",\"link\": \"https:\\/\\/www.flickr.com\\/photos\\/134819556@N06\\/tags\\/s11\\/\",\"description\": \"\"})";
	private static final String LONG_JSON = "{\"title\": \"Uploads from schulddavid, tagged s11, with geodata\",\"link\": \"https:\\/\\/www.flickr.com\\/photos\\/134819556@N06\\/tags\\/s11\\/\",\"description\": \"\"}";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		jsonp2Json = new Jsonp2Json(call);

	}

	@Test
	public void jsonpApiProxy_JsonOutput() throws IOException {

		when(context.getLogger()).thenReturn(logger);
		when(call.call(anyString(), anyString())).thenReturn(EXPECTED_OUTPUT_STRING);

		InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());
		OutputStream output = new ByteArrayOutputStream();

		jsonp2Json.jsonp2Json(input, output, context);

		String sampleOutputString = output.toString();
		Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
	}

	@Test
	public void jsonpApiProxy_invalidURL() throws IOException {

		when(context.getLogger()).thenReturn(logger);
		when(call.call(anyString(), anyString())).thenThrow(new HTTPException(404));

		InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());
		OutputStream output = new ByteArrayOutputStream();

		jsonp2Json.jsonp2Json(input, output, context);

		String sampleOutputString = output.toString();
		Assert.assertTrue(sampleOutputString.contains("\"statusCode\":404"));
	}

	@Test
	public void jsonpApiProxy_JsonpOutput() throws IOException {

		when(context.getLogger()).thenReturn(logger);
		when(call.call(anyString(), anyString())).thenReturn(EXPECTED_JSONP_OUTPUT_STRING);

		InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());
		OutputStream output = new ByteArrayOutputStream();

		jsonp2Json.jsonp2Json(input, output, context);

		String sampleOutputString = output.toString();
		Assert.assertEquals(EXPECTED_OUTPUT_STRING, sampleOutputString);
	}

	@Test
	public void jsonpApiProxy_longJsonpOutput() throws IOException {

		when(context.getLogger()).thenReturn(logger);
		when(call.call(anyString(), anyString())).thenReturn(LONG_JSONP);

		InputStream input = new ByteArrayInputStream(SAMPLE_INPUT_STRING.getBytes());
		OutputStream output = new ByteArrayOutputStream();

		jsonp2Json.jsonp2Json(input, output, context);

		String sampleOutputString = output.toString();
		Assert.assertEquals(LONG_JSON, sampleOutputString);
	}
}
