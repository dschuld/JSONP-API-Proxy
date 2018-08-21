package de.davidschuld.jsonpapiproxy.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import de.davidschuld.jsonpapiproxy.DaggerApiCallComponent;
import de.davidschuld.jsonpapiproxy.JsonpProxy;

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

		JsonpProxy converter = DaggerApiCallComponent.builder().build().buildConverter();
		LambdaLogger logger = context.getLogger();
		converter.jsonp2Json(input, output, message -> logger.log(message));
		
	}

	

}
