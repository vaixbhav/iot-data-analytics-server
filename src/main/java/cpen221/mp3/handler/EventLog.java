package cpen221.mp3.handler;

import cpen221.mp3.event.ActuatorEvent;
import cpen221.mp3.event.Event;
import org.apache.commons.collections.list.SynchronizedList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class EventLog implements Iterable<Event> {

    /**
     * Log of events sent by entities
     */
    private ArrayList<Event> eventLog = new ArrayList<Event>();

    public EventLog() { }

    /**
     * Adds a new event to the log
     * @param e event to add to log
     */
    public void addEvent(Event e) {
        this.eventLog.add(e);
    }

    /**
     * Returns the latest event logged in the event log
     * and null if there are no events logged
     * @return
     */
    public Event getEvent() {
        if (this.eventLog.isEmpty()) {
            return null;
        }
        return this.eventLog.get(0);
    }

    /**
     * Returns the entire event log as list
     * @return list of events logged in the events log
     */
    public ArrayList<Event> getLog() {
        return this.eventLog;
    }
    
    @Override
    public @NotNull Iterator<Event> iterator() {
        return eventLog.iterator();
    }
}