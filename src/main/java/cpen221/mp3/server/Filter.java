package cpen221.mp3.server;

import cpen221.mp3.event.ActuatorEvent;
import cpen221.mp3.event.Event;
import cpen221.mp3.event.SensorEvent;

import static cpen221.mp3.server.BooleanOperator.valueOf;


import java.util.List;
import java.util.ArrayList;

enum DoubleOperator {
    EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUALS,
    LESS_THAN_OR_EQUALS
}

enum BooleanOperator {
    EQUALS,
    NOT_EQUALS
}

public class Filter {

    // Rep Invariants:
    //
    // 1. Exclusive Filter Types: Only one of isComplexFilter, operator1, and operator2 must be true or non-null.
    // 2. Non-null Boolean Operator: If isComplexFilter is true, operator1 must not be null.
    // 3. Valid Boolean Value: If isComplexFilter is true, boolValue must be a valid boolean value.
    // 4. Non-null Double Operator: If isComplexFilter is true, operator2 must not be null.
    // 5. Valid Double Value: If isComplexFilter is true, doubleValue must be a valid double value.
    // 6. Non-null Field: If isComplexFilter is true, field must not be null.

    // Abstraction function:
    // Maps the internal state of the Filter class to the abstract representation of a filter.
    //
    // Is Complex Filter: isComplexFilter indicates whether the filter is complex.
    // Boolean Operator 1: operator1 represents the first boolean operator in a complex filter.
    // Boolean Value: boolValue represents the boolean value associated with the complex filter.
    // Double Operator 2: operator2 represents the second double operator in a complex filter.
    // Double Value: doubleValue represents the double value associated with the complex filter.
    // Field: field represents the field on which the filter operates either "value" or "timestamp"

    /**
     * True if filter is complex
     * false otherwise
     */
    private boolean isComplexFilter = false;
    private BooleanOperator operator1;
    private boolean boolValue;

    private DoubleOperator operator2;
    private double doubleValue;
    private String field;

    /**
     * Constructs a filter that compares the boolean (actuator) event value
     * to the given boolean value using the given BooleanOperator.
     * (X (BooleanOperator) value), where X is the event's value passed by satisfies or sift methods.
     * A BooleanOperator can be one of the following:
     * <p>
     * BooleanOperator.EQUALS
     * BooleanOperator.NOT_EQUALS
     *
     * @param operator the BooleanOperator to use to compare the event value with the given value
     * @param value    the boolean value to match
     */
    public Filter(BooleanOperator operator, boolean value) {

        this.operator1 = operator;
        this.boolValue = value;
        this.doubleValue = -1;  // ADDED THIS
    }

    /**
     * Constructs a filter that compares a double field in events
     * with the given double value using the given DoubleOperator.
     * (X (DoubleOperator) value), where X is the event's value passed by satisfies or sift methods.
     * A DoubleOperator can be one of the following:
     * <p>
     * DoubleOperator.EQUALS
     * DoubleOperator.GREATER_THAN
     * DoubleOperator.LESS_THAN
     * DoubleOperator.GREATER_THAN_OR_EQUALS
     * DoubleOperator.LESS_THAN_OR_EQUALS
     * <p>
     * For non-double (boolean) value events, the satisfies method should return false.
     *
     * @param field    the field to match (event "value" or event "timestamp")
     * @param operator the DoubleOperator to use to compare the event value with the given value
     * @param value    the double value to match
     * @throws IllegalArgumentException if the given field is not "value" or "timestamp"
     */

    public Filter(String field, DoubleOperator operator, double value) {
        this.doubleValue = value;
        this.operator2 = operator;
        this.field = field.toLowerCase();
        if (!this.field.equals("value") && !this.field.equals("timestamp")) {
            throw new IllegalArgumentException("Field must be value or timestamp");
        }

    }

    /**
     * A filter can be composed of other filters.
     * in this case, the filter should satisfy all the filters in the list.
     * Constructs a complex filter composed of other filters.
     *
     * @param filters the list of filters to use in the composition
     */
    private Filter complexFilter;

    private Filter filter;

    public Filter(List<Filter> filters) {
        if (filters.size() == 1) {
            complexFilter = filters.get(0);
            filter = complexFilter;
        } else {
            complexFilter = new Filter(filters.subList(0, filters.size() - 1));
            filter = new Filter(filters.subList(filters.size() -2 , filters.size() - 1));
        }
        this.isComplexFilter = true;
    }


    /**
     * Returns true if the given event satisfies the filter criteria.
     *
     * @param event the event to check
     * @return true if the event satisfies the filter criteria, false otherwise
     */
    public boolean satisfies(Event event) {
        if (!this.isComplexFilter) {
            if (this.field == null) {
                if(this.operator1 == null) {
                    return false;
                }
                if (this.operator1.equals(BooleanOperator.EQUALS)) {
                    return (this.boolValue == event.getValueBoolean());
                } else {
                    return (this.boolValue == !(event.getValueBoolean()));
                }
            } else if (event.getClass().equals(ActuatorEvent.class) && this.field.equals("value")) {
                return false;
            } else {
                double valueToCheck;
                if (this.field.equals("value")) {
                    valueToCheck = event.getValueDouble();
                } else {
                    valueToCheck = event.getTimeStamp();
                }
                switch (operator2) {
                    case EQUALS -> {
                        return valueToCheck == this.doubleValue;
                    }
                    case GREATER_THAN -> {
                        return valueToCheck > this.doubleValue;
                    }
                    case GREATER_THAN_OR_EQUALS -> {
                        return valueToCheck >= this.doubleValue;
                    }
                    case LESS_THAN -> {
                        return valueToCheck < this.doubleValue;
                    }
                    case LESS_THAN_OR_EQUALS -> {
                        return valueToCheck <= this.doubleValue;
                    }
                    default -> {
                        return false;
                    }
                }
            }
        } else {
            return this.complexFilter.satisfies(event) && this.filter.satisfies(event);
        }
    }

    /**
     * Returns true if the given list of events satisfies the filter criteria.
     *
     * @param events the list of events to check
     * @return true if every event in the list satisfies the filter criteria, false otherwise
     */
    public boolean satisfies(List<Event> events) {
        for (Event event : events) {
            if(!satisfies(event)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a new event if it satisfies the filter criteria.
     * If the given event does not satisfy the filter criteria, then this method should return null.
     *
     * @param event the event to sift
     * @return a new event if it satisfies the filter criteria, null otherwise
     */
    public Event sift(Event event) {
        if (satisfies(event)) {
            if (event.getClass().equals(SensorEvent.class)) {
                return new SensorEvent(event.getTimeStamp(), event.getClientId(), event.getEntityId(), event.getEntityType(), event.getValueDouble());
            } else {
                return new ActuatorEvent(event.getTimeStamp(), event.getClientId(), event.getEntityId(), event.getEntityType(), event.getValueBoolean());
            }
        }
        return null;
    }

    /**
     * Returns a list of events that contains only the events in the given list that satisfy the filter criteria.
     * If no events in the given list satisfy the filter criteria, then this method should return an empty list.
     *
     * @param events the list of events to sift
     * @return a list of events that contains only the events in the given list that satisfy the filter criteria
     *        or an empty list if no events in the given list satisfy the filter criteria
     */
    public List<Event> sift(List<Event> events) {
        List<Event> siftedList = new ArrayList<>();
        for (Event event : events) {
            if (sift(event) != null) {
                siftedList.add(event);
            }
        }
        return siftedList;
    }

    @Override
    public String toString() {
        if(complexFilter == null) {
            return "Filter{" +
                    "BooleanOperator=" + this.operator1 +
                    ",BooleanValue=" + this.boolValue +
                    ",DoubleOperator=" + this.operator2 +
                    ",DoubleValue=" + this.doubleValue +
                    ",Field=" + this.field +
                    '}';
        } else {
            return "Filter{" +
                    "BooleanOperator=" + this.operator1 +
                    ",BooleanValue=" + this.boolValue +
                    ",DoubleOperator=" + this.operator2 +
                    ",DoubleValue=" + this.doubleValue +
                    ",Field=" + this.field +
                    ",ComplexFilter=" + this.complexFilter.toString() +
                    '}';
        }
    }
}
