package cpen221.mp3.handler;

import cpen221.mp3.client.Request;
import cpen221.mp3.client.RequestCommand;
import cpen221.mp3.client.RequestDeserializer;
import cpen221.mp3.client.RequestType;
import cpen221.mp3.server.Server;

import java.io.*;
import java.net.Socket;
import java.util.*; 


public class RequestHandlerThread implements Runnable {

    // Rep Invariants:
    //
    // 1. Non-null Request: The request must not be null.
    // 2. Non-null Server: The server must not be null.
    // 3. Non-null Client Socket: The clientSocket must not be null.

    // Abstraction function:
    // Maps the internal state of the RequestHandlerThread class to the abstract representation of a thread handling requests.
    //
    // Request: request represents the request to be handled.
    // Server: server represents the server handling the request.
    // Client Socket: clientSocket represents the socket of the client making the request.

    private Request request;
    private Server server;
    private Socket clientSocket;

    public RequestHandlerThread(Request request, Server server, Socket clientSocket) {
        this.request = request;
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        this.server.processIncomingRequest(request, clientSocket);
    }
}