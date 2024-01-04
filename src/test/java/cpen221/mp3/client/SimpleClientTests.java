package cpen221.mp3.client;

import cpen221.mp3.entity.Actuator;
import cpen221.mp3.entity.Entity;
import cpen221.mp3.entity.Sensor;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleClientTests{

    @Test
    public void testRegisterEntities() {
        Client client = new Client(0, "test@test.com", "127.0.0.1", 4578);

        Entity thermostat = new Sensor(0, client.getClientId(), "TempSensor");
        Entity valve = new Actuator(0, -1, "Switch", false);

        assertFalse(thermostat.registerForClient(1));   // thermostat is already registered to client 0
        assertTrue(thermostat.registerForClient(0));    // registering thermostat for existing client (client 0) is fine and should return true
        assertTrue(valve.registerForClient(1));         // valve was unregistered, and can be registered to client 1, even if it does not exist
    }

    @Test
    public void testAddEntities() {
        Client client1 = new Client(0, "test1@test.com", "127.0.0.1", 4578);
        Client client2 = new Client(1, "test2@test.com", "127.0.0.1", 4578);

        Entity valve = new Actuator(0, -1, "Switch", false);

        assertTrue(client1.addEntity(valve));
        assertFalse(client2.addEntity(valve));
        HashSet<Entity> expected = new HashSet<>();
        expected.add(valve);
        assertEquals(expected, client1.getEntityList());
    }


}