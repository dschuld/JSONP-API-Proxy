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
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import de.davidschuld.jsonpapiproxy.DaggerApiCallComponent;

/**
 * Proxies a call to an API that returns JSONP and removes the callback funtion,
 * returning only the JSON data. If the API does not return JSONP (tested via
 * the regex <i>^[A-Za-z0-9]+\\(.*$</i>), the actual output is returned, no
 * matter if it is valid JSON or not.
 * 
 * @author David Schuld (davidschuld@gmail.com)
 *
 */
public class JsonpApiProxyLambda implements RequestStreamHandler {



	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {

		Jsonp2Json converter = DaggerApiCallComponent.builder().build().buildConverter();
		converter.jsonp2Json(input, output, context);
		
	}

	

}
