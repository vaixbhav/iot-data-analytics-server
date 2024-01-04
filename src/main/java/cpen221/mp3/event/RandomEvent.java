package cpen221.mp3.event;

import java.util.Random;

public class RandomEvent {
    private static final Random random = new Random();

    // Generates a random double between 20 and 24 (Celsius) for TempSensor
    public static double generateTemperature() {
        return 20 + random.nextDouble() * 4;
    }

    // Generates a random double between 1020 and 1024 (in millibars) for PressureSensor
    public static double generatePressure() {
        return 1020 + random.nextDouble() * 4;
    }

    // Generates a random double for CO2Sensor (you can specify the range)
    public static double generateCO2Level(double min, double max) {
        return min + random.nextDouble() * (max - min);
    }

    // Generates a random boolean (50-50% chance of true or false) for Switch
    public static boolean generateSwitchStatus() {
        return random.nextBoolean();
    }
}
