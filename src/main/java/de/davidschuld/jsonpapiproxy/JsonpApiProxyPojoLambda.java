package de.davidschuld.jsonpapiproxy;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class JsonpApiProxyPojoLambda implements RequestHandler<Api, String> {

    @Override
    public String handleRequest(Api input, Context context) {
        context.getLogger().log("Input: " + input);

        // TODO: implement your handler
        return "Hello from Lambda!";
    }

}
