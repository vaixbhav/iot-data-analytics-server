package cpen221.mp3.client;

import cpen221.mp3.server.SeverCommandToActuator;

public class Request {

    // Rep Invariants:
    //
    // 1. Non-negative Timestamp: The timestamp must be a non-negative value.
    // 2. Non-null Request Type: The requestType must not be null.
    // 3. Non-null Request Command: The requestCommand must not be null.
    // 4. Non-null Request Data: The requestData must not be null.

    // Abstraction function:
    // Maps the internal state of the Request class to the abstract representation of a request in a client-server system.
    //
    // Timestamp: timeStamp represents the time at which the request was created.
    // Request Type: requestType represents the type of the request (e.g., CONFIG, CONTROL).
    // Request Command: requestCommand represents the specific command associated with the request
    //                    (e.g., PREDICT_NEXT_N_TIMESTAMPS, PREDICT_NEXT_N_VALUES).
    // Request Data: requestData represents the data requested by the client to the server.

    private final double timeStamp;
    private final RequestType requestType;
    private final RequestCommand requestCommand;
    private final String requestData;

    public Request(RequestType requestType, RequestCommand requestCommand, String requestData) {
        this.timeStamp = System.currentTimeMillis();
        this.requestType = requestType;
        this.requestCommand = requestCommand;
        this.requestData = requestData;
    }

    public double getTimeStamp() {
        return timeStamp;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public RequestCommand getRequestCommand() {
        return requestCommand;
    }

    public String getRequestData() {
        return requestData;
    }

    @Override
    public String toString() {
        return "Request{" +
                "TimeStamp=" + getTimeStamp() +
                ",RequestType=" + getRequestType() +
                ",RequestCommand=" + getRequestCommand() +
                ",RequestData=" + getRequestData() +
                '}';
    }

    public boolean getActuatorState() {
        return false;
    }

    public SeverCommandToActuator getCommand() {
        return null;
    }
}