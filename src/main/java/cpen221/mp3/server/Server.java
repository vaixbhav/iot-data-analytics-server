package cpen221.mp3.server;

import cpen221.mp3.entity.Actuator;
import cpen221.mp3.client.Client;
import cpen221.mp3.entity.ActuatorDeserializer;
import cpen221.mp3.event.ActuatorEvent;
import cpen221.mp3.event.Event;
import cpen221.mp3.client.Request;
import cpen221.mp3.client.RequestCommand;
import cpen221.mp3.client.RequestType;
import cpen221.mp3.handler.EventLog;

import cpen221.mp3.handler.RequestHandlerThread;

import java.util.*;

import javax.swing.text.html.parser.Entity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

    // Rep Invariants:
    //
    // 1. Non-null Client: The client must not be null.
    // 2. Non-negative Max Wait Time: The maxWaitTime must be a non-negative value.
    // 3. Non-null Server Event Log: The serverEventLog must not be null.
    // 4. Non-null Filter: The filter must not be null.
    // 5. Non-null Output Stream Writer: The out must not be null.
    // 6. Non-null Client Socket: The clientSocket must not be null.

    // Abstraction function:
    // Maps the internal state of the Server class to the abstract representation of a server.
    //
    // Client: client represents the client associated with the server.
    // Max Wait Time: maxWaitTime represents the difference between the time the message was received
    //                  on the server side and the time it was processed.
    // Server Event Log: serverEventLog represents the log where events are stored.
    // Filter: filter represents the filter applied by the server.
    // Output Stream Writer: out represents the output stream writer for communication with the client.
    // Client Socket: clientSocket represents the socket of the connected client.

    private Client client;
    private double maxWaitTime = 2; // in seconds
    private EventLog serverEventLog = new EventLog();
    private Filter filter;
    private PrintWriter out;
    private Socket clientSocket;

    public Server(Client client) {
        this.client = client;
    }

    public double getMaxWaitTime(){
        return this.maxWaitTime; 
    }

    public void logEvent(Event e) {
        if(filter == null){
            this.serverEventLog.addEvent(e);
        } else if(this.filter.satisfies(e)) {
            this.serverEventLog.addEvent(e);
        }
    }

    /**
     * Update the max wait time for the client.
     * The max wait time is the maximum amount of time
     * that the server can wait for before starting to process each event of the client:
     * It is the difference between the time the message was received on the server
     * (not the event timeStamp from above) and the time it started to be processed.
     *
     * @param maxWaitTime the new max wait time
     */
    public void updateMaxWaitTime(double maxWaitTime) {
        this.maxWaitTime = maxWaitTime;

        // Important note: updating maxWaitTime may not be as simple as
        // just updating the field. You may need to do some additional
        // work to ensure that events currently being processed are not
        // dropped or ignored by the change in maxWaitTime.
    }

    /**
     * Set the actuator state if the given filter is satisfied by the latest event.
     * Here the latest event is the event with the latest timestamp not the event 
     * that was received by the server the latest.
     *
     * If the actuator is not registered for the client, then this method should do nothing.
     *
     * @param filter the filter to check
     * @param actuator the actuator to set the state of as true
     */
    public void setActuatorStateIf(Filter filter, Actuator actuator) {
        // implement this method and send the appropriate SeverCommandToActuator as a Request to the actuator

        Event event = serverEventLog.getEvent();
        if(actuator.getClientId() == this.client.getClientId()) {
            if(filter.satisfies(event)){
                actuator.updateState(true);

                ServerRequest toSend = new ServerRequest(SeverCommandToActuator.SET_STATE, true);

                try {
                    Socket socket = new Socket(actuator.getIP(), actuator.getPort());
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.print(client.getClientId() + "{"+ toSend.toString() +"}");
                    out.flush();
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Toggle the actuator state if the given filter is satisfied by the latest event.
     * Here the latest event is the event with the latest timestamp not the event 
     * that was received by the server the latest.
     *
     * If the actuator has never sent an event to the server, then this method should do nothing.
     * If the actuator is not registered for the client, then this method should do nothing.
     *
     * @param filter the filter to check
     * @param actuator the actuator to toggle the state of (true -> false, false -> true)
     */
    public void toggleActuatorStateIf(Filter filter, Actuator actuator) {
        // implement this method and send the appropriate SeverCommandToActuator as a Request to the actuator
        Event event = serverEventLog.getEvent();

        ArrayList<Event> log = serverEventLog.getLog();

        boolean hasSent = false;

        for (Event elem : log) {
            if(elem.getEntityId() == actuator.getId()){
                hasSent = true;
            }
        }

        if(event.getClientId() == client.getClientId() && hasSent){ // NEED TO CHECK 1ST CONDITION
            if(!(event == null)){
                if(filter.satisfies(event)) {
                    actuator.updateState(!(actuator.getState()));

                    ServerRequest toSend = new ServerRequest(SeverCommandToActuator.TOGGLE_STATE);
                    try {
                        Socket socket = new Socket(actuator.getIP(), actuator.getPort());
                        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.print(client.getClientId() + "{" + toSend.toString() + "}");
                        out.flush();
                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * Log the event ID for which a given filter was satisfied.
     * This method is checked for every event received by the server.
     *
     * @param filter the filter to check
     */
    public void logIf(Filter filter) {
        this.filter = filter;
    }

    /**
     * Return all the entity ID's made by the "logIf" method so far.
     * If no logs have been made, then this method should return an empty list.
     * The list should be sorted in the order of event timestamps.
     * After the logs are read, they should be cleared from the server.
     *
     * @return list of event IDs
     */
    public List<Integer> readLogs() {
        ArrayList<Event> eventList = new ArrayList<>(serverEventLog.getLog());
        eventList.sort(Comparator.comparingDouble(Event::getTimeStamp));
        ArrayList<Integer> eventListID = new ArrayList<>();
        for(Event e : eventList) {
            eventListID.add(e.getEntityId());
        }
        return eventListID;
    }

    /**
     * List all the events of the client that occurred in the given time window.
     * Here the timestamp of an event is the time at which the event occurred, not
     * the time at which the event was received by the server.
     * If no events occurred in the given time window, then this method should return an empty list.
     *
     * @param timeWindow the time window of events, inclusive of the start and end times
     * @return list of the events for the client in the given time window
     */
    public List<Event> eventsInTimeWindow(TimeWindow timeWindow) {
        ArrayList<Event> eventList = new ArrayList<>();
        for(Event e : this.serverEventLog) {
            if(e.getTimeStamp() >= timeWindow.startTime && e.getTimeStamp() <= timeWindow.endTime) {
                eventList.add(e);
            }
        }
        if(clientSocket != null) {
            try {
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(eventList);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return eventList;
    }

    /**
     * Returns a set of IDs for all the entities of the client for which
     * we have received events so far.
     * Returns an empty list if no events have been received for the client.
     *
     * @return list of all the entities of the client for which we have received events so far
     */
    public List<Integer> getAllEntities() {
        HashSet<Integer> eventID = new HashSet<>();
        for(Event e : this.serverEventLog) {
            eventID.add(e.getEntityId());
        }
        if(clientSocket != null) {
            try {
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(eventID.stream().toList());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return eventID.stream().toList();
    }

    /**
     * List the latest n events of the client.
     * Here the order is based on the original timestamp of the events, not the time at which the events were received by the server.
     * If the client has fewer than n events, then this method should return all the events of the client.
     * If no events exist for the client, then this method should return an empty list.
     * If there are multiple events with the same timestamp in the boundary,
     * the ones with largest EntityId should be included in the list.
     *
     * @param n the max number of events to list
     * @return list of the latest n events of the client
     */
    public List<Event> lastNEvents(int n) {
        ArrayList<Event> allEvents = new ArrayList<>();
        for(Event e : this.serverEventLog) {
            allEvents.add(e);
        }
        if(allEvents.isEmpty()) {
            return allEvents;
        }
        allEvents.sort(Comparator.comparingDouble(Event::getTimeStamp));
        if(allEvents.size() < n) {
            return allEvents;
        }
        List<Event> inRange = checkDuplicate(allEvents);
        List<Event> lastNEventList = new ArrayList<>();
        for(int i = inRange.size() - n; i < inRange.size(); i++) {
            lastNEventList.add(inRange.get(i));
        }

        if(clientSocket != null) {
            try {
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(checkDuplicate(inRange).toString());
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lastNEventList;
    }

    public List<Event> checkDuplicate(List<Event> eventList) {
        HashMap<Double, Event> eventMap = new HashMap<>();
        for(Event e : eventList) {
            if(eventMap.containsKey(e.getTimeStamp())) {
                if(e.getEntityId() > eventMap.get(e.getTimeStamp()).getEntityId()) {
                    eventMap.put(e.getTimeStamp(), e);
                }
            }
            else {
                eventMap.put(e.getTimeStamp(), e);
            }
        }
        List<Event> events = new ArrayList<>(eventMap.values());
        events.sort(Comparator.comparingDouble(Event::getTimeStamp));
        return events;

    }

    /**
     * returns the ID corresponding to the most active entity of the client
     * in terms of the number of events it has generated.
     *
     * If there was a tie, then this method should return the largest ID.
     *
     * @return the most active entity ID of the client
     */
    public int mostActiveEntity() {
        // implement this method
        
        int mostActiveId = 0;
        int maxFrequency = 0;

        Map<Integer, Integer> frequenciesOfEntities = new HashMap<Integer, Integer>();


        for (Event event : this.serverEventLog) {

            if(frequenciesOfEntities.containsKey(event.getEntityId())){
                frequenciesOfEntities.put(event.getEntityId(), frequenciesOfEntities.get(event.getEntityId()) + 1);
            }

            else{
                frequenciesOfEntities.put(event.getEntityId(), 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : frequenciesOfEntities.entrySet()) {

            if(entry.getValue() > maxFrequency){
                maxFrequency = entry.getValue();
                mostActiveId = entry.getKey();
            }

            else if(entry.getValue() == maxFrequency){
                if(entry.getKey() > mostActiveId){
                    maxFrequency = entry.getValue();
                    mostActiveId = entry.getKey();
                }
            }

        }

        if(clientSocket != null) {
            try {
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(mostActiveId);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mostActiveId;
    }

    /**
     * the client can ask the server to predict what will be
     * the next n timestamps for the next n events
     * of the given entity of the client (the entity is identified by its ID).
     *
     * If the server has not received any events for an entity with that ID,
     * or if that Entity is not registered for the client, then this method should return an empty list.
     *
     * @param entityId the ID of the entity
     * @param n the number of timestamps to predict
     * @return list of the predicted timestamps
     */
    public List<Double> predictNextNTimeStamps(int entityId, int n) {
        boolean exists = false;
        List<Double> nextNTimeStamps = new ArrayList<>();

        for(Event e : serverEventLog){
            if(e.getEntityId() == entityId){
                exists = true;
            }
        }

        if(exists){
            List<Double> startingValues = new ArrayList<>();
            for(Event e : this.serverEventLog){
                if(e.getEntityId() == entityId) {
                    startingValues.add(e.getTimeStamp());
                }
            }
            Thread predictThread = new Thread(new PredictorThread(entityId, n, startingValues, clientSocket));
            predictThread.start();
        } else {
            try {
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(nextNTimeStamps);
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return nextNTimeStamps;
    }

    /**
     * the client can ask the server to predict what will be
     * the next n values of the timestamps for the next n events
     * of the given entity of the client (the entity is identified by its ID).
     * The values correspond to Event.getValueDouble() or Event.getValueBoolean()
     * based on the type of the entity. That is why the return type is List<Object>.
     *
     * If the server has not received any events for an entity with that ID,
     * or if that Entity is not registered for the client, then this method should return an empty list.
     *
     * @param entityId the ID of the entity
     * @param n the number of double value to predict
     * @return list of the predicted timestamps
     */
    public List<Double> predictNextNValues(int entityId, int n) {
        boolean exists = false;
        List<Double> nextNValues = new ArrayList<>();

        for(Event e : serverEventLog){
            if(e.getEntityId() == entityId){
                exists = true;
            }
        }

        if(exists){
            List<Double> startingValues = new ArrayList<>();
            for(Event e : this.serverEventLog){
                if(e.getEntityId() == entityId) {
                    if(e.getClass().equals(ActuatorEvent.class)) {
                        if(e.getValueBoolean()) {
                            startingValues.add(0.0);
                        } else {
                            startingValues.add(0.0);
                        }
                    }
                }
            }
            Thread predictThread = new Thread(new PredictorThread(entityId, n, startingValues, clientSocket));
            predictThread.start();
        } else {
            try {
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                out.println(nextNValues);
                out.flush();
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return nextNValues;
    }

    public void processIncomingEvent(Event event) {
        this.logEvent(event);
    }

    public void processIncomingRequest(Request request, Socket clientSocket) {
        this.clientSocket = clientSocket;
        switch (request.getRequestType()) {
            case CONFIG: {
                switch (request.getRequestCommand()) {
                    case CONFIG_UPDATE_MAX_WAIT_TIME: {

                        
                        updateMaxWaitTime(Integer.parseInt(request.getRequestData()));  //CHANGE THIS LATER


                        break;
                    }
                    default: {
                        break;//
                    }
                }
                break;
            }
            case ANALYSIS: {
                switch (request.getRequestCommand()) {
                    case ANALYSIS_GET_ALL_ENTITIES: {
                        getAllEntities();
                        break;
                    }
                    case ANALYSIS_GET_EVENTS_IN_WINDOW: {
                        TimeWindow window = new TimeWindow(Double.parseDouble(request.getRequestData().split("[{,=]")[1]),
                                Double.parseDouble(request.getRequestData().split("[{,=]")[3]));
                        eventsInTimeWindow(window);
                        break;
                    }
                    case ANALYSIS_GET_LATEST_EVENTS: {
                        lastNEvents(Integer.parseInt(request.getRequestData()));
                        break;
                    }
                    case ANALYSIS_GET_MOST_ACTIVE_ENTITY: {
                        mostActiveEntity();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case CONTROL: {
                switch (request.getRequestCommand()) {
                    case CONTROL_NOTIFY_IF: {
                        notify();
                        break;
                    }
                    case CONTROL_SET_ACTUATOR_STATE: {
                        FilterDeserializer fd = new FilterDeserializer();
                        Filter filter = fd.deserialize(request.getRequestData().split("\\{([^}]*)\\}")[0]);
                        ActuatorDeserializer ad = new ActuatorDeserializer();
                        Actuator actuator = ad.deserialize(request.getRequestData().split("\\{([^}]*)\\}")[1]);
                        setActuatorStateIf(filter, actuator);
                        break;
                    }
                    case CONTROL_TOGGLE_ACTUATOR_STATE: {
                        System.out.println(request.getRequestData());
                        FilterDeserializer fd = new FilterDeserializer();
                        Filter filter = fd.deserialize(request.getRequestData().split("},")[0]);
                        ActuatorDeserializer ad = new ActuatorDeserializer();
                        Actuator actuator = ad.deserialize(request.getRequestData().split("},")[1]);
                        toggleActuatorStateIf(filter, actuator);
                        break;
                    }

                    default: {
                        break;
                    }
                }
                break;
            }
            case PREDICT: {
                switch (request.getRequestCommand()) {
                    case PREDICT_NEXT_N_TIMESTAMPS: {
                        predictNextNTimeStamps(Integer.parseInt(request.getRequestData().split(",")[0]),
                                Integer.parseInt(request.getRequestData().split(",")[1]));
                        break;
                    }
                    case PREDICT_NEXT_N_VALUES: {
                        predictNextNValues(Integer.parseInt(request.getRequestData().split(",")[0]),
                                Integer.parseInt(request.getRequestData().split(",")[1]));
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }

            default: {
                break;
            }
        }
    }
}