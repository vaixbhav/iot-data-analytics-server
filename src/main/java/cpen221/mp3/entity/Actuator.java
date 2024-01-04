package cpen221.mp3.entity;

import cpen221.mp3.client.Request;
import cpen221.mp3.event.ActuatorEvent;
import cpen221.mp3.event.Event;
import cpen221.mp3.event.RandomEvent;
import cpen221.mp3.handler.MessageHandler;
import cpen221.mp3.server.ServerRequest;
import cpen221.mp3.server.SeverCommandToActuator;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class Actuator implements Entity {

    // Rep Invariants:
    //
    // 1. Positive ID: The id must be a positive integer.
    // 2. Non-negative Client ID: The clientId must be non-negative.
    // 3. Non-null Type: The type must not be null.
    // 4. Valid Event Generation Frequency: The eventGenerationFrequency must be greater than 0.
    // 5. Valid Server IP: If specified, the serverIP must represent a valid IP address.
    // 6. Valid Server Port: If specified, the serverPort must be within the valid port range (0 to 65535).
    // 7. Valid Host IP: The host must represent a valid IP address.
    // 8. Valid Command Port: The port for receiving commands must be within the valid port range (0 to 65535).
    // 9. Server Socket Initialization: The serverSocket must be initialized if serverPort is specified.

    // Abstraction function:
    // Maps the internal state of the Actuator class to the abstract representation of an actuator entity.
    //
    // ID: id represents the unique identifier of the actuator.
    // Client ID: clientId represents the identifier of the client associated with the actuator.
    // Type: type represents the type of the actuator.
    // State: state represents the current state of the actuator.
    // Event Generation Frequency: eventGenerationFrequency represents the frequency at which events are generated.
    // Server IP: serverIP represents the IP address to which events are sent.
    // Server Port: serverPort represents the port to which events are sent.
    // Host: host represents the IP address from which commands can be received.
    // Port: port represents the port from which commands can be received.
    // Server Socket: serverSocket represents the socket used for receiving commands.

    /**
     * Unique entity ID
     */
    private final int id;

    /**
     * ID of the client that the entity is registered to
     */
    private int clientId;

    /**
     * Type of entity
     */
    private final String type;

    /**
     * State of the actuator
     * True or False
     */
    private boolean state;

    /**
     * Frequency with which events must be generated
     */
    private double eventGenerationFrequency = 0.2; // default value in Hz (1/s)
    // the following specifies the http endpoint that the actuator should send events to

    /**
     * Server IP
     */
    private String serverIP = null;

    /**
     * Server port number
     */
    private int serverPort = 0;
    // the following specifies the http endpoint that the actuator should be able to receive commands on from server

    /**
     * Actuator IP
     */
    private String host = "127.0.0.1";

    /**
     * Actuator port number
     */
    private int port = 69;

    /**
     * Socket to communicate state/response to client
     */
    private ServerSocket serverSocket;

    public Actuator(int id, String type, boolean init_state) {
        this.id = id;
        this.clientId = -1;         // remains unregistered
        this.type = type;
        this.state = init_state;
        try {
            this.serverSocket = new ServerSocket(this.port);
            Thread thread1 = new Thread(() -> this.serve());
            thread1.start();
            Thread thread2 = new Thread(() -> this.eventGenerator());
            thread2.start();

        } catch (IOException e) {
            System.err.println("IO error!");
        }

    }

    public Actuator(int id, int clientId, String type, boolean init_state) {
        this.id = id;
        this.clientId = clientId;   // registered for the client
        this.type = type;
        this.state = init_state;
        try {
            this.serverSocket = new ServerSocket(this.port);
            Thread thread1 = new Thread(() -> this.serve());
            thread1.start();
            Thread thread2 = new Thread(() -> this.eventGenerator());
            thread2.start();
        } catch (IOException e) {
            System.err.println("IO error!");
        }
    }

    public Actuator(int id, String type, boolean init_state, String serverIP, int serverPort) {
        this.id = id;
        this.clientId = -1;         // remains unregistered
        this.type = type;
        this.state = init_state;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        try {
            this.serverSocket = new ServerSocket(this.port);
            Thread thread1 = new Thread(() -> this.serve());
            thread1.start();
            Thread thread2 = new Thread(() -> this.eventGenerator());
            thread2.start();
        } catch (IOException e) {
            System.err.println("IO error!");
        }
    }

    public Actuator(int id, int clientId, String type, boolean init_state, String serverIP, int serverPort) {
        this.id = id;
        this.clientId = clientId;   // registered for the client
        this.type = type;
        this.state = init_state;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        try {
            this.serverSocket = new ServerSocket(this.port);
            Thread thread1 = new Thread(() -> this.serve());
            thread1.start();
            Thread thread2 = new Thread(() -> this.eventGenerator());
            thread2.start();
        } catch (IOException e) {
            System.err.println("IO error!");
        }
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
        return true;
    }

    public boolean getState() {
        return state;
    }

    public String getIP() {
        return host;
    }

    public int getPort() {
        return port;
    }

    /**
     * Updates the state of the actuator
     * @param new_state state to change to
     */
    public void  updateState(boolean new_state) {
        synchronized (this) {
            this.state = new_state;
        }
    }

    /**
     * Registers the actuator for the given client
     * 
     * @return true if the actuator is new (clientID is -1 already) and gets successfully registered or if it is already registered for clientId, else false
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
     * the actuator should send events to
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
     * Listens for ServerCommandToActuator
     */
    public void serve() {
        Socket incomingSocket = null;
        try {
            incomingSocket = serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket finalIncomingSocket = incomingSocket;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                handle(finalIncomingSocket);
            }
        });
        thread.start();
    }

    /**
     * Handles and deserializes ServerCommandToActuator
     * @param socket socket that the command is received at
     */
    public void handle(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String requestString = in.readLine();

            String[] parts = requestString.split("[,=}]");

            SeverCommandToActuator command = SeverCommandToActuator.valueOf(parts[3].trim());
            boolean actuatorState = Boolean.parseBoolean(parts[5].trim());

            processServerMessage(new ServerRequest(command, actuatorState));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes SerevrCommandToActuator
     * @param command
     */
    public void processServerMessage(Request command) {
        if (command.getCommand().equals(SeverCommandToActuator.TOGGLE_STATE)) {
            this.state = !(this.state);
        } else if (command.getCommand().equals(SeverCommandToActuator.SET_STATE)) {
            this.state = command.getActuatorState();
        }
    }

    @Override
    public String toString() {
        return "Actuator{" +
                "getId=" + getId() +
                ",ClientId=" + getClientId() +
                ",EntityType=" + getType() +
                ",IP=" + getIP() +
                ",Port=" + getPort() +
                '}';
    }


    /**
     * Generates and sends events at the given frequency to the server
     */
    public void eventGenerator() {
        int failedAttempts = 0;
        while (true) {
            while (this.serverIP == null || this.serverPort == 0) {
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
                if(!(exitCode == 1)) {
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
    public ActuatorEvent generateEvent() {
        return new ActuatorEvent(System.currentTimeMillis(), this.clientId, this.id, this.type, RandomEvent.generateSwitchStatus());
    }
}