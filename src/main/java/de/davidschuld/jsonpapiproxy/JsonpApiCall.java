package de.davidschuld.jsonpapiproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import javax.xml.ws.http.HTTPException;

/**
 * Wraps a HTTP call to a URL. URL is not mockeable to the call if encapsulated
 * and not unit tested.
 * 
 * @author David Schuld
 *
 */
public class JsonpApiCall {

	public String call(String url, String method) {
		String result = null;
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(method);

			if (conn.getResponseCode() != 200) {
				throw new HTTPException(conn.getResponseCode());
			}
			result = new BufferedReader(new InputStreamReader(conn.getInputStream())).lines()
					.collect(Collectors.joining("\n"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return result;

	}

}
