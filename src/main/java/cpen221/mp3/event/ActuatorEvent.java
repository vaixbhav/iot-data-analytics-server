package cpen221.mp3.event;

public class ActuatorEvent implements Event {

    // Rep Invariants:
    //
    // 1. Non-negative Timestamp: The timeStamp must be a non-negative value.
    // 2. Non-negative Client ID: The clientId must be non-negative.
    // 3. Non-negative Entity ID: The entityId must be non-negative.
    // 4. Non-null Entity Type: The entityType must not be null.

    // Abstraction function:
    // Maps the internal state of the ActuatorEvent class to the abstract representation of an actuator event.
    //
    // Timestamp: timeStamp represents the time at which the event occurred.
    // Client ID: clientId represents the identifier of the client associated with the event.
    // Entity ID: entityId represents the identifier of the entity associated with the event.
    // Entity Type: entityType represents the type of the entity associated with the event.
    // Value: value represents either the double or boolean value associated with the event.

    private double TimeStamp;
    private int ClientId;
    private int EntityId;
    private String EntityType;
    private boolean Value;


    public ActuatorEvent(double TimeStamp,
                       int ClientId,
                       int EntityId,
                       String EntityType,
                       boolean Value) {
        this.TimeStamp = TimeStamp;
        this.ClientId = ClientId;
        this.EntityId = EntityId;
        this.EntityType = EntityType;
        this.Value = Value;
    }

    public double getTimeStamp() {
        return this.TimeStamp;
    }

    public int getClientId() {
        return this.ClientId;
    }

    public int getEntityId() {
        return this.EntityId;
    }

    public String getEntityType() {
        return this.EntityType;
    }

    public boolean getValueBoolean() {
        return this.Value;
    }

    public double getValueDouble() {
        return -1;
    }

    @Override
    public String toString() {
        return "ActuatorEvent{" +
                "TimeStamp=" + getTimeStamp() +
                ",ClientId=" + getClientId() +
                ",EntityId=" + getEntityId() +
                ",EntityType=" + getEntityType() +
                ",Value=" + getValueBoolean() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ActuatorEvent other = (ActuatorEvent) obj;
        return (this.TimeStamp == other.getTimeStamp() && this.EntityType.equals(other.getEntityType()) && this.EntityId == other.getEntityId() && this.ClientId == other.getClientId() );
    }
}
