package de.davidschuld.jsonpapiproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.xml.ws.http.HTTPException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Main cass acting as the JSONP API proxy.
 * 
 * @author David Schuld (davidschuld@gmail.com)
 *
 */
public class JsonpProxy {

	private static final String JSONP_START_REGEX = "^[A-Za-z0-9]+\\(.*$";
	private static final String GET = "GET";

	private final JsonpApiCall apiCall;
	
	@Inject
	public JsonpProxy(JsonpApiCall apiCall) {
		this.apiCall = apiCall;
	}

	/**
	 * Provides an {@link InputStream} that contains the data returned by the
	 * requestet API, and an {@link OutputStream} where the transformed data is
	 * written to.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void jsonp2Json(InputStream input, OutputStream output, Logger logger)
			throws UnsupportedEncodingException, IOException {
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		JSONObject responseJson = new JSONObject();

		JSONParser parser = new JSONParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			logger.log("Input: " + event.toJSONString());
			JSONObject queryParameters = (JSONObject) event.get("queryStringParameters");

			String url = URLDecoder.decode((String) queryParameters.get("url"), StandardCharsets.UTF_8.name());
			logger.log("Calling " + url);
			String result = apiCall.call(url, GET);

			if (result.replace("\n", "").matches(JSONP_START_REGEX)) {
				logger.log("JSONP detected, removing callback padding.");
				result = result.substring(result.indexOf('(') + 1, result.lastIndexOf(')'));
			} else {
				logger.log("No JSONP detected, returning data as is.");
			}

			logger.log("Received data: " + result);
			
			responseJson.put("isBase64Encoded", false);
			responseJson.put("statusCode", 200);
			responseJson.put("body", result);
			

			logger.log("Response: " + responseJson.toString());
			writer.write(responseJson.toString());
		} catch (HTTPException e) {
			logger.log(e.toString());
			responseJson.put("statusCode", e.getStatusCode());
			responseJson.put("exception", e);
			writer.write(responseJson.toString());
		} catch (Exception e) {
			logger.log(e.toString());
			responseJson.put("statusCode", 400);
			responseJson.put("exception", e);
			writer.write(responseJson.toString());
		}

		logger.log("Execution finished");
		writer.close();
	}

}
