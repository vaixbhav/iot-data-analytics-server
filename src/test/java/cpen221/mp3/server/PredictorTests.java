package cpen221.mp3.server;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cpen221.mp3.client.Client;
import cpen221.mp3.client.Request;
import cpen221.mp3.client.RequestCommand;
import cpen221.mp3.client.RequestType;
import cpen221.mp3.entity.Actuator;
import cpen221.mp3.entity.Sensor;
import cpen221.mp3.event.ActuatorEvent;
import cpen221.mp3.event.SensorEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class PredictorTests {
    
    /*@Test
    public void predictConstant() throws IOException{
        Client client = new Client(0, "test@test.com", "localhost", 4578);

        Socket clientSocket = new Socket("localhost", 4578);

        Server server = new Server(client);

        Actuator valve = new Actuator(1, 0, "Switch", true);

        Sensor sensor = new Sensor(2, 0, "Sensor");

        ActuatorEvent event1 = new ActuatorEvent(2.0, 0, 1,"Actuator", true);
        ActuatorEvent event2 = new ActuatorEvent(2.0, 0, 1,"Actuator", true);
        ActuatorEvent event3 = new ActuatorEvent(2.0, 0, 1,"Actuator", true);

        SensorEvent event4 = new SensorEvent(1.0, 0, 2, "Sensor", 2.0);
        SensorEvent event5 = new SensorEvent(1.0, 0, 2, "Sensor", 2.0);
        SensorEvent event6 = new SensorEvent(1.0, 0, 2, "Sensor", 2.0);
        // SensorEvent event7 = new SensorEvent(1.0, 0, 2, "Sensor", 2.0);

        server.processIncomingEvent(event1);
        server.processIncomingEvent(event2);
        server.processIncomingEvent(event3);

        server.processIncomingEvent(event4);
        server.processIncomingEvent(event5);
        server.processIncomingEvent(event6);
        // server.processIncomingEvent(event7);
        

        Request request1 = new Request(RequestType.PREDICT, RequestCommand.PREDICT_NEXT_N_TIMESTAMPS, (valve.toString()));

        Request request2 = new Request(RequestType.PREDICT, RequestCommand.PREDICT_NEXT_N_VALUES, (sensor.toString()));

        server.processIncomingRequest(request1, clientSocket);

        // server.processIncomingRequest(request2, clientSocket);

        

        List<String> actualTs = new ArrayList<String>(); 

        List<String> actualVals = new ArrayList<String>(); 

        actualTs = client.getData(); 

        // actualVals = client.getData(); 

        
        double[] exp = {2.0, 2.0, 2.0};
        

        for (int j = 0; j < exp.length; j++) {
            assertEquals(exp[0], Double.parseDouble(actualTs.get(j)));

            // assertEquals(exp[0], Double.parseDouble(actualVals.get(j)));
        }
    } */

    @Test
    public void predictAlternating() {
        List<Double> startingValues = new ArrayList<Double>();
        for (int i = 0; i < 4; i++) {
            if(i%2 == 0) startingValues.add(2.0); 
            else startingValues.add(1.0);
        }
        

        Predictor predictor = new Predictor(0, 3, startingValues);

        List<Double> actual = new ArrayList<Double>(); 

        actual = predictor.predict();

            assertEquals(2, actual.get(0));
            assertEquals(1, actual.get(1));
            assertEquals(2, actual.get(2));
    }

    @Test
    public void predictExtrapolate() {
        List<Double> startingValues = new ArrayList<Double>();
        for (double i = 0; i < 4; i++) {
            startingValues.add(i); 
        }
        

        Predictor predictor = new Predictor(0, 3, startingValues);

        List<Double> actual = new ArrayList<Double>(); 

        actual = predictor.predict();

            assertEquals(4, actual.get(0),0.7);
            assertEquals(5, actual.get(1),0.7);
            assertEquals(6, actual.get(2),0.7);
    }

}
