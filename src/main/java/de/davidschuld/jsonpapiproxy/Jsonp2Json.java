package de.davidschuld.jsonpapiproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.inject.Inject;
import javax.xml.ws.http.HTTPException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class Jsonp2Json {
	
	private static final String JSONP_START_REGEX = "^[A-Za-z0-9]+\\(.*$";
	private static final String GET = "GET";
	
	private final JsonpApiCall apiCall;
	
	@Inject
	public Jsonp2Json(JsonpApiCall apiCall) {
		this.apiCall = apiCall;
	}
	

	public void jsonp2Json(InputStream input, OutputStream output, Context context)
			throws UnsupportedEncodingException, IOException {
		LambdaLogger logger = context.getLogger();
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		logger.log("Input: " + input);
		JSONObject responseJson = new JSONObject();

		JSONParser parser = new JSONParser();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		try {
			JSONObject event = (JSONObject) parser.parse(reader);

			String url = (String) event.get("url");
			String result = apiCall.call(url, GET);
			logger.log(result);

			if (result.replace("\n", "").matches(JSONP_START_REGEX)) {
				logger.log("JSONP detected, removing callback padding.");
				result = result.substring(result.indexOf('(') + 1, result.lastIndexOf(')'));
			} else {
				logger.log("No JSONP detected, returning data as is.");
			}

			writer.write(result);
		} catch (HTTPException e) {

			responseJson.put("statusCode", e.getStatusCode());
			responseJson.put("exception", e);
			writer.write(responseJson.toString());
		} catch (Exception e) {

			responseJson.put("statusCode", 400);
			responseJson.put("exception", e);
			writer.write(responseJson.toString());
		}

		writer.close();
	}
	
}
