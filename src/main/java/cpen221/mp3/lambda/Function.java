package cpen221.mp3.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import cpen221.mp3.server.Predictor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Lambda function to handle overhead from prediction services.
 */
public class Function implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    Logger log = LogManager.getLogger(Function.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        var headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            // Extract parameters from the input
            Map<String, String> queryParams = input.getQueryStringParameters();
            int entityId = Integer.parseInt(queryParams.get("param1"));
            int n = Integer.parseInt(queryParams.get("param2"));
            String param3 = queryParams.get("param3");
            List<Double> startingValues = new ArrayList<>();
            for(String s : param3.substring(1, param3.length() - 1).split(", ")) {
                if(!(s.equals(""))) {
                    startingValues.add(Double.parseDouble(s));
                }
            }

            Predictor predictor = new Predictor(entityId, n, startingValues);

            List<Double> prediction = predictor.predict();

            String jsonResponse = prediction.toString();

            return response
                    .withStatusCode(200)
                    .withBody(jsonResponse);
        } catch (Exception e) {
            log.error("Error processing request", e);
            return response
                    .withBody("{\"error\": \"Internal Server Error\"}")
                    .withStatusCode(500);
        }
    }
}
