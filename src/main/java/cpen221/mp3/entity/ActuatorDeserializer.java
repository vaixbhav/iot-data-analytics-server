package cpen221.mp3.entity;

public class ActuatorDeserializer {
    public ActuatorDeserializer() { }

    /**
     * Deserializes the given String actuator
     * @param data serialized actuator string
     * @return actuator after deserialization
     */
    public Actuator deserialize(String data) {
        return new Actuator(Integer.parseInt(data.split("[{,=}]")[2].trim()),
                            Integer.parseInt(data.split("[{,=}]")[4].trim()),
                            (data.split("[{,=}]")[6].trim()),
                            Boolean.parseBoolean(data.split("[{,=}]")[8].trim()));
    }
}
