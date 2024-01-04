package cpen221.mp3.server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PredictorThread implements Runnable{

    // Rep Invariants:
    //
    // 1. Positive Entity ID: The entityId must be a positive integer.
    // 2. Non-negative N: The n must be non-negative.
    // 3. Non-null Starting Values: The startingValues must not be null.
    // 4. Non-null Client Socket: The clientSocket must not be null.

    // Abstraction function:
    // Maps the internal state of the PredictorThread class to the abstract representation of a thread handling predictions.
    //
    // Entity ID: entityId represents the unique identifier of the entity associated with the predictor.
    // N: n represents the number of values to be predicted.
    // Starting Values: startingValues is a list containing the initial values for prediction.
    // Client Socket: clientSocket represents the socket of the client requesting predictions.

    private int entityId;
    private int n;
    private List<Double> startingValues;
    private Socket clientSocket;
    public PredictorThread(int entityId, int n, List<Double> startingValues, Socket clientSocket){
        this.n = n;
        this.entityId = entityId;
        this.startingValues = startingValues;
        this.clientSocket = clientSocket;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        predict(entityId, n, startingValues, clientSocket);
    }

    /**
     * Calls the lambda function to run prediction services
     * @param entityId entity ID of the entity run predictions on
     * @param n number of values to predict
     * @param startingValues values to base prediction off of
     * @param clientSocket Socket to send response back to client
     */
    public void predict(int entityId, int n, List<Double> startingValues, Socket clientSocket) {

        List<Double> predictedValues = new ArrayList<>();
        String apiUrl = "https://3ts6num7uk.execute-api.us-east-2.amazonaws.com/prod/lmbdafn";
        try {
            URL url = new URL(apiUrl + "?param1=" + entityId + "&param2=" + n + "&param3=" + startingValues.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                for (String s : response.substring(1, response.length() - 1).split(", ")) {
                    if (!(s.equals(""))) {
                        predictedValues.add(Double.parseDouble(s));
                    }
                }

                try {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    out.println(predictedValues);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Response Body: " + response);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
