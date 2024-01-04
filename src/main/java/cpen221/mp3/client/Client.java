package cpen221.mp3.client;

import cpen221.mp3.entity.Entity;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Client {

    // Rep Invariants:
    //
    // 1. Socket Initialization: The socket must be initialized and not null.
    // 2. Input Stream Initialization: The BufferedReader 'in' must be initialized and not null.
    // 3. Output Stream Initialization: The PrintWriter 'out' must be initialized and not null.
    // 4. Valid Email: The email field must not be null.
    // 5. Valid Server IP: The serverIP field must not be null and must represent a valid IP address.
    // 6. Valid Server Port: The serverPort field must be within the valid port range (0 to 65535).
    // 7. Non-negative Client ID: The clientId field must be non-negative.
    // 8. Entity List Initialization: The entityList field must be initialized and not null.

    // Abstraction function:
    // Maps the internal state of the Client class to the abstract representation of a client in a client-server system.
    //
    // Client ID: clientId represents the unique identifier of the client.
    // Email: email represents the email address associated with the client.
    // Server IP: serverIP represents the IP address of the server the client is connected to.
    // Server Port: serverPort represents the port number of the server the client is connected to.
    // Entity List: entityList is a list containing entities associated with the client.

    /**
     * Socket for communication with server
     **/
    private Socket socket;

    /**
     * Input reader to read from socket's input stream
     **/
    private BufferedReader in;

    /**
     * Output writer to write to socket's output stream
     **/
    private PrintWriter out;

    /**
     * Unique ID
     **/
    private final int clientId;

    /**
     * e-mail address of the client
     **/
    private String email;

    /**
     * IP Address of the server
     **/
    private String serverIP;

    /**
     * Port number of the server
     **/
    private int serverPort;

    /**
     * List of all entities registered to the client
     **/
    private ArrayList<Entity> entityList;


    public Client(int clientId, String email, String serverIP, int serverPort) {
        this.clientId = clientId;
        this.email = email;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.entityList = new ArrayList<>();
    }

    public int getClientId() {
        return clientId;
    }

    /**
     * Registers an entity for the client
     *
     * @return true if the entity is new and gets successfully registered, false if the Entity is already registered
     */
    public boolean addEntity(Entity entity) {
        if(entity.registerForClient(this.clientId)) {
            this.entityList.add(entity);
            return true;
        }
        return false;
    }

    /**
     * Returns a list of all entities registered to this client
     * @return set of all entities registered to this client
     */
    public HashSet<Entity> getEntityList() {
        return new HashSet<>(this.entityList);
    }

    /**
     * Sets the server endpoint that the entity should send requests to
     * @param entity entity whose endpoint configurations must be changed
     * @param hostIP IP address of server
     * @param portNumber Port number of server
     */
    public void setEndpoint(Entity entity, String hostIP, int portNumber) {
        entity.setEndpoint(hostIP, portNumber);
    }

    /**
     * Sends a request to the server
     * @param request request to send to server
     */
    public void sendRequest(Request request) {
        try {
            this.socket = new Socket(serverIP, serverPort);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.print("{ClientInfo{" + "ClientID=" + clientId + "}},"+ request.toString());
        out.flush();
        out.close();

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String data = in.readLine();
            System.out.println(data);
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getData() {
        return new ArrayList<String>();
    }
}