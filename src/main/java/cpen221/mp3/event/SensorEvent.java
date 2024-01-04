package cpen221.mp3.event;

public class SensorEvent implements Event {

    // Rep Invariants:
    //
    // 1. Non-negative Timestamp: The timeStamp must be a non-negative value.
    // 2. Non-negative Client ID: The clientId must be non-negative.
    // 3. Non-negative Entity ID: The entityId must be non-negative.
    // 4. Non-null Entity Type: The entityType must not be null.
    // 5. Valid Value: The value must be a valid numeric value.

    // Abstraction function:
    // Maps the internal state of the SensorEvent class to the abstract representation of a sensor event.
    //
    // Timestamp: timeStamp represents the time at which the event occurred.
    // Client ID: clientId represents the identifier of the client associated with the event.
    // Entity ID: entityId represents the identifier of the entity associated with the event.
    // Entity Type: entityType represents the type of the entity associated with the event.
    // Value: value represents the double value associated with the event.

    private double TimeStamp;
    private int ClientId;
    private int EntityId;
    private String EntityType;

    private double Value;


    public SensorEvent(double TimeStamp,
                        int ClientId,
                        int EntityId, 
                        String EntityType, 
                        double Value) {
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

    public double getValueDouble() {
        return this.Value;
    }

    public boolean getValueBoolean() {
        return false;
    }

    @Override
    public String toString() {
        return "SensorEvent{" +
               "TimeStamp=" + getTimeStamp() +
               ",ClientId=" + getClientId() + 
               ",EntityId=" + getEntityId() +
               ",EntityType=" + getEntityType() + 
               ",Value=" + getValueDouble() + 
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
        SensorEvent other = (SensorEvent) obj;
        return (this.TimeStamp == other.getTimeStamp() && this.EntityType.equals(other.getEntityType()) && this.EntityId == other.getEntityId() && this.ClientId == other.getClientId() );
    }
}
