package cpen221.mp3.server;

import java.util.ArrayList;

public class FilterDeserializer {

    public FilterDeserializer() { }

    /**
     * Deserializes the given String filter
     * @param data serialized filter string
     * @return filter after deserialization
     */
    public Filter deserialize(String data) {

        // ArrayList<String> parsedData = new ArrayList<String>();
        String[] parsedData = data.split("[{,=}]");

        if (parsedData.length < 12) {
            if (Double.valueOf(parsedData[8]) == -1) {
                return new Filter(BooleanOperator.valueOf(parsedData[2]), Boolean.valueOf(parsedData[1]));
            } else {
                return new Filter(String.valueOf(parsedData[10]), DoubleOperator.valueOf(parsedData[6]), Double.valueOf(parsedData[8]));
            }
        } else {
            String complexData = data.split("=")[12];
            FilterDeserializer fd = new FilterDeserializer();
            return fd.deserialize(complexData);
        }
    }
}
