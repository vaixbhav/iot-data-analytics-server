package cpen221.mp3.event;

public class SensorEventDeserializer {

    public SensorEventDeserializer(){};

    /**
     * Deserializes the given String sensor event
     * @param data serialized sensor event string
     * @return sensor event after deserialization
     */
    public SensorEvent deserialize(String data) {
        return new SensorEvent(Integer.parseInt(data.split("[{,=}]")[2].trim()), 
                               Integer.parseInt(data.split("[{,=}]")[4].trim()), 
                               Integer.parseInt(data.split("[{,=}]")[6].trim()), 
                               String.valueOf(data.split("[{,=}]")[8].trim()), 
                               Double.valueOf(data.split("[{,=}]")[10].trim()));
    }
}
