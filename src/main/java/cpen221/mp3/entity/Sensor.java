package cpen221.mp3.entity;

import cpen221.mp3.event.Event;
import cpen221.mp3.event.RandomEvent;
import cpen221.mp3.event.SensorEvent;

import java.io.*;
import java.net.Socket;

public class Sensor implements Entity {

    // Rep Invariants:
    //
    // 1. Positive ID: The id must be a positive integer.
    // 2. Non-negative Client ID: The clientId must be non-negative.
    // 3. Non-null Type: The type must not be null.
    // 4. Valid Server IP: The serverIP must represent a valid IP address.
    // 5. Valid Server Port: The serverPort must be within the valid port range (0 to 65535).
    // 6. Valid Event Generation Frequency: The eventGenerationFrequency must be greater than 0.

    // Abstraction function:
    // Maps the internal state of the Sensor class to the abstract representation of a sensor entity.
    //
    // ID: id represents the unique identifier of the sensor.
    // Client ID: clientId represents the identifier of the client associated with the sensor.
    // Type: type represents the type of the sensor.
    // Server IP: serverIP represents the IP address to which events are sent.
    // Server Port: serverPort represents the port to which events are sent.
    // Event Generation Frequency: eventGenerationFrequency represents the frequency at which events are generated.

    private final int id;
    private int clientId;
    private final String type;
    private String serverIP = null;
    private int serverPort = 0;
    private double eventGenerationFrequency = 0.2; // default value in Hz (1/s)

    public Sensor(int id, String type) {
        this.id = id;
        this.clientId = -1;         // remains unregistered
        this.type = type;
    }

    public Sensor(int id, int clientId, String type) {
        this.id = id;
        this.clientId = clientId;   // registered for the client
        this.type = type;
    }

    public Sensor(int id, String type, String serverIP, int serverPort) {
        this.id = id;
        this.clientId = -1;   // remains unregistered
        this.type = type;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public Sensor(int id, int clientId, String type, String serverIP, int serverPort) {
        this.id = id;
        this.clientId = clientId;   // registered for the client
        this.type = type;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public int getId() {
        return id;
    }

    public int getClientId() {
        return clientId;
    }

    public String getType() {
        return type;
    }

    public boolean isActuator() {
        return false;
    }

    /**
     * Registers the sensor for the given client
     *
     * @return true if the sensor is new (clientID is -1 already) and gets successfully registered or if it is already registered for clientId, else false
     */
    public boolean registerForClient(int clientId) {
        synchronized (this) {
            if (clientId == this.clientId || this.clientId == -1) {
                this.clientId = clientId;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Sets or updates the http endpoint that 
     * the sensor should send events to
     *
     * @param serverIP the IP address of the endpoint
     * @param serverPort the port number of the endpoint
     */
    public void setEndpoint(String serverIP, int serverPort) {
        synchronized (this) {
            this.serverIP = serverIP;
            this.serverPort = serverPort;
        }
    }

    /**
     * Sets the frequency of event generation
     *
     * @param frequency the frequency of event generation in Hz (1/s)
     */
    public void setEventGenerationFrequency(double frequency) {
        synchronized (this) {
            if (this.clientId == this.id) {
                this.eventGenerationFrequency = frequency;
            }
        }
    }

    /**
     * Sends event to server
     * @param event event to send to server
     */
    public void sendEvent(Event event) {
        try {
            Socket socket = new Socket(serverIP, serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.print(event.toString());
            out.flush();

            in.close();
            out.close();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates and sends events at the given frequency to the server
     */
    public void eventGenerator() {
        int failedAttempts = 0;
        while(true) {

            while(this.serverIP == null || this.serverPort == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Socket socket = new Socket(serverIP, serverPort);

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

                out.print(this.generateEvent().toString());
                out.flush();

                int exitCode = in.read();
                if (!(exitCode == 1)) {
                    failedAttempts++;
                }
                in.close();
                out.close();

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(failedAttempts == 5) {
                failedAttempts = 0;
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            try {
                Thread.sleep((long) (1 / this.eventGenerationFrequency));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generates an event at random based on the given restrictions
     * @return
     */
    public SensorEvent generateEvent() {
        switch(this.type) {
            case ("CO2Sensor"): {
                return new SensorEvent(System.currentTimeMillis(), this.clientId, this.id, this.type, RandomEvent.generateCO2Level(0, 40000));
            }
            case ("TempSensor"): {
                return new SensorEvent(System.currentTimeMillis(), this.clientId, this.id, this.type, RandomEvent.generateTemperature());
            }
            case ("PressureSensor"): {
                return new SensorEvent(System.currentTimeMillis(), this.clientId, this.id, this.type, RandomEvent.generatePressure());
            }
            default: {
                return new SensorEvent(System.currentTimeMillis(), this.clientId, this.id, this.type, -1);
            }
        }
    }
}