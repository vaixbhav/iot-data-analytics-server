package cpen221.mp3.event;

public class ActuatorEventDeserializer {
    public static ActuatorEvent deserialize(String data){
        return new ActuatorEvent(Double.parseDouble(data.split("[{,=}]")[2].trim()),
                Integer.parseInt(data.split("[{,=}]")[4].trim()),
                Integer.parseInt(data.split("[{,=}]")[6].trim()),
                (data.split("[{,=}]")[8].trim()),
                Boolean.parseBoolean(data.split("[{,=}]")[10].trim()));


       /* "ActuatorEvent{" +
                "TimeStamp=" + getTimeStamp() +
                ",ClientId=" + getClientId() +
                ",EntityId=" + getEntityId() +
                ",EntityType=" + getEntityType() +
                ",Value=" + getValueBoolean() +
                '}'; */

    }



}
