package cpen221.mp3.handler;

import cpen221.mp3.client.Request;
import cpen221.mp3.event.Event;
import cpen221.mp3.server.Server;

public class Message {

    // Rep Invariants:
    //
    // 1. Non-negative Client ID: The clientId must be non-negative.
    // 2. Exclusive Message Types: Only one of isRequest and isEvent must be true.
    // 3. Non-null Event or Request: Either e or r must be non-null.
    // 4. Non-negative Timestamp: The timestamp must be a non-negative value.
    // 5. Non-negative Max Wait Time: The maxWaitTime must be a non-negative value.

    // Abstraction function:
    // Maps the internal state of the Message class to the abstract representation of a message.
    //
    // Client ID: clientId represents the identifier of the client associated with the message.
    // Is Request: isRequest indicates whether the message is a request.
    // Is Event: isEvent indicates whether the message is an event.
    // Event: e represents the event associated with the message.
    // Request: r represents the request associated with the message.
    // Timestamp: timestamp represents the time at which the message was created.
    // Max Wait Time: maxWaitTime represents the difference between the time the message
    //                  was received on the server side and the time it was processed.

    /**
     * Client ID of the client sending the request
     * or the entity sending the event is registered to
     */
    private int clientId;

    /**
     * True if message is a request
     */
    private boolean isRequest;

    /**
     * True if message is an event
     */
    private boolean isEvent;
    private Event e;
    private Request r;

    /**
     * Time stamp at which message was received by server
     */
    private final double timestamp;

    /**
     * Server's configured maxWaitTime for server associated to client with this.ClienId
     */
    private double maxWaitTime;

    public Message(Event e, double timestamp) {
        this.isEvent = true;
        this.isRequest = false;
        this.r = null;
        this.e = e;
        this.timestamp = timestamp;
        this.maxWaitTime = 0;
        this.clientId = e.getClientId();
    }

    public Message(Request r, Server server, double timestamp, int clientId) {
        this.isEvent = false;
        this.isRequest = true;
        this.r = r;
        this.e = null;
        this.timestamp = timestamp;
        this.maxWaitTime = server.getMaxWaitTime();
        this.clientId = clientId;
    }

    /**
     * Returns the time left to process message to avoid a QoS violation
     * @return the time left before QoS violation
     */
    public double timeLeft() {
        if (this.isEvent) {
            return 0.0;
        } else {
            return(this.maxWaitTime - this.getElapsed());
        }
    }

    public boolean isEvent() {
        return isEvent;
    }

    public Event getEvent() {
        return this.e;
    }

    public Request getRequest() {
        return this.r;
    }

    public int getClientId() {
        return this.clientId;
    }

    /**
     * Returns time elapsed since message was received by message handler
     * @return time elapsed since message was received by message handler
     */
    public double getElapsed() {
        return System.currentTimeMillis() - this.timestamp;
    }

    public Double getTimestamp() {
        return  this.timestamp;
    }
}
