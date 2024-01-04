package cpen221.mp3.server;

import cpen221.mp3.client.Request;

public class ServerRequest extends Request {

    // Rep Invariants:
    //
    // 1. Non-negative Timestamp: The timeStamp must be a non-negative value.
    // 2. Non-null Server Command to Actuator: The command must not be null.
    // 3. Non-null Actuator State: The actuatorState must not be null.

    // Abstraction function:
    // Maps the internal state of the ServerRequest class to the abstract representation of a server request.
    //
    // Timestamp: timeStamp represents the time at which the event was sent by the IoT entity.
    // Server Command to Actuator: command represents the command to be sent to the actuator.
    // Actuator State: actuatorState represents the state of the actuator associated with the request.

    private final double timeStamp;
    private final SeverCommandToActuator command;
    private final boolean actuatorState;

    public ServerRequest(SeverCommandToActuator command, boolean actuatorState) {
        super(null, null, null);
        this.timeStamp = System.currentTimeMillis();
        this.command = command;
        this.actuatorState = actuatorState;
    }

    public ServerRequest(SeverCommandToActuator command) {
        super(null, null, null);
        this.timeStamp = System.currentTimeMillis();
        this.command = command;
        this.actuatorState = false;
    }

    public double getTimeStamp() {
        return timeStamp;
    }


    public SeverCommandToActuator getCommand() {
        return command;
    }

    public boolean getActuatorState() {
        return actuatorState;
    }

    @Override
    public String toString() {
        return "ServerRequest{" +
                "TimeStamp=" + getTimeStamp() +
                ",Command=" + getCommand() +
                ",ActuatorState=" + getActuatorState() +
                '}';
    }
}

