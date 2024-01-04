package cpen221.mp3.server;

import cpen221.mp3.client.*;
import cpen221.mp3.entity.Actuator;
import cpen221.mp3.entity.ActuatorDeserializer;
import cpen221.mp3.entity.Entity;
import cpen221.mp3.entity.Sensor;
import cpen221.mp3.handler.MessageHandler;
import cpen221.mp3.server.Filter;

import static cpen221.mp3.server.DoubleOperator.LESS_THAN;

public class Main {
    
    public static void main(String[] args) {
        Client client1 = new Client(0, "test1@test.com", "127.0.0.1", 4578);

        Actuator valve = new Actuator(0, -1, "Switch", true);
        Filter filter = new Filter("timestamp", LESS_THAN, 1);
        System.out.println((filter.toString() + "," + valve.toString()).split("},")[1]);
        Request request = new Request(RequestType.CONTROL, RequestCommand.CONTROL_TOGGLE_ACTUATOR_STATE, (filter.toString() + "," + valve.toString()));

        client1.sendRequest(request);
        System.out.println(valve.getState());
    }

}
