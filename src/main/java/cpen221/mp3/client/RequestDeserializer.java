package cpen221.mp3.client;

public class RequestDeserializer {


    public RequestDeserializer() { }

    /**
     * Deserializes the given String request
     * @param data serialized request string
     * @return request after deserialization
     */
    public Request deserialize(String data) {
        return new Request(RequestType.valueOf(data.split("[{,=}]")[4].trim()),
                               RequestCommand.valueOf(data.split("[{,=]")[6].trim()),
                               (data.split("RequestData=")[1]));
    }
}
