package cpen221.mp3.handler;

import cpen221.mp3.client.Client;
import cpen221.mp3.client.Request;
import cpen221.mp3.client.RequestDeserializer;
import cpen221.mp3.event.ActuatorEventDeserializer;
import cpen221.mp3.event.Event;
import cpen221.mp3.event.SensorEventDeserializer;
import cpen221.mp3.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MessageHandler {

    // Rep Invariants:
    //
    // 1. Valid Port: The port must be within the valid port range (0 to 65535).
    // 2. Non-null Server Socket: The serverSocket must not be null.
    // 3. Non-null Incoming Socket List: The incomingSocketList must not be null.
    // 4. Non-null Event Log: The log must not be null.
    // 5. Non-null Timestamp Queue: The timeStampQueue must not be null.

    // Abstraction function:
    // Maps the internal state of the MessageHandler class to the abstract representation of a message handler.
    //
    // Port: port represents the port number on which the server is listening.
    // Server Socket: serverSocket represents the server socket used for communication.
    // Incoming Socket List: incomingSocketList is a map of timestamps to sockets.
    // Event Log: log represents the log where events are stored.
    // Timestamp Queue: timeStampQueue is a priority queue of messages sorted by increasing timestamps.

    /**
     * Socket through which server receives requests/events
     */
    private ServerSocket serverSocket;

    /**
     * Port on which server is running
     */
    private int port;

    /**
     * List of all sockets that have connected to server
     */
    private HashMap<Double, Socket> incomingSocketList = new HashMap<>();

    /**
     * Non-server-specific log of events
     */
    private EventLog log = new EventLog();

    /**
     * EDF Priority Queue to schedule and process incoming traffic
     */
    PriorityQueue<Message> timeStampQueue = new PriorityQueue<Message>(new MessageComparator());

    static class MessageComparator implements Comparator<Message> {
        @Override
        public int compare(Message m1, Message m2) {
            if(m1.timeLeft() - m2.timeLeft() > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private Map<Integer, Server> clientList = Collections.synchronizedMap(new HashMap<Integer, Server>());

    public MessageHandler(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket incomingSocket = serverSocket.accept();

                System.out.println("Accepted Request!");

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(incomingSocket.getInputStream()));
                    String requestString = in.readLine();

                    if (requestString.contains("Request")) {

                        System.out.println(requestString.split("}},")[1]);
                        RequestDeserializer rd = new RequestDeserializer();
                        Request r = rd.deserialize(requestString.split("}},")[1]);
                        int clientId = Integer.parseInt(requestString.split("}},")[0].split("[=}]")[1].trim());
                        if (!clientList.containsKey(clientId)) {
                            this.clientList.put(clientId, new Server(new Client(clientId, "", "127.0.0.1", this.port)));
                        }

                        double currentTime = System.currentTimeMillis();
                        timeStampQueue.add(new Message(r, clientList.get(clientId), currentTime, clientId));
                        incomingSocketList.put(currentTime, incomingSocket);

                    } else if (requestString.contains("Event")) {
                        Event e;

                        if (requestString.contains("Sensor")) {
                            SensorEventDeserializer sed = new SensorEventDeserializer();
                            e = sed.deserialize(requestString);
                        } else {
                            ActuatorEventDeserializer aed = new ActuatorEventDeserializer();
                            e = aed.deserialize(requestString);
                        }

                        int clientId = e.getClientId();
                        if (!clientList.containsKey(clientId)) {
                            this.clientList.put(clientId, new Server(new Client(clientId, "", "127.0.0.1", this.port)));
                        }

                        double currentTime = System.currentTimeMillis();
                        timeStampQueue.add(new Message(e, currentTime));
                        incomingSocketList.put(currentTime, incomingSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Message nextMessage = timeStampQueue.remove();
                Socket nextSocket = incomingSocketList.get(nextMessage.getTimestamp());

                if(nextMessage.isEvent()) {
                    Thread thread = new Thread(new EventLoggerThread(nextMessage.getEvent(), this.clientList.get(nextMessage.getClientId()), this.log));
                    thread.start();
                } else {
                    Thread thread = new Thread(new RequestHandlerThread(nextMessage.getRequest(), this.clientList.get(nextMessage.getClientId()), nextSocket));
                    thread.start();
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MessageHandler messageHandler = new MessageHandler(4578);
        messageHandler.start();
    }
}
