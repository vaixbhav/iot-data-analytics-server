package cpen221.mp3.handler;

import cpen221.mp3.client.Request;
import cpen221.mp3.client.RequestCommand;
import cpen221.mp3.client.RequestType;
import cpen221.mp3.event.*;
import cpen221.mp3.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Comparator;
import java.util.PriorityQueue;

class EventLoggerThread implements Runnable {

    // Rep Invariants:
    //
    // 1. Non-null Event: The event must not be null.
    // 2. Non-null Server: The server must not be null.
    // 3. Non-null Event Log: The log must not be null.

    // Abstraction function:
    // Maps the internal state of the EventLoggerThread class to the abstract representation of a thread logging events.
    //
    // Event: event represents the event to be logged.
    // Server: server represents the server that receives the event.
    // Event Log: log represents the log where the event is stored.

    /**
     * Event to log
     */
    private Event event;

    /**
     * Server to log events to
     */
    private Server server;

    /**
     * Server non-specific log
     */
    private EventLog log;

    public EventLoggerThread(Event event, Server server, EventLog log) {
        this.event = event;
        this.log = log;
        this.server = server;
    }

    @Override
    public void run() {
        this.log.addEvent(event);
        this.server.processIncomingEvent(event);
    }
}