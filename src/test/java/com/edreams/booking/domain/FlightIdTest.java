package com.edreams.booking.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FlightIdTest {

    @Test
    void shouldCreateValidFlightId() {
        FlightId flightId = new FlightId("EDR12345");
        assertEquals("EDR12345", flightId.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"EDR1234", "EDR123456", "edr12345", "EDR1234A", "12345", ""})
    void shouldRejectInvalidFlightId(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> new FlightId(invalid));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        FlightId flightId1 = new FlightId("EDR12345");
        FlightId flightId2 = new FlightId("EDR12345");
        
        assertEquals(flightId1, flightId2);
        assertEquals(flightId1.hashCode(), flightId2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesDiffer() {
        FlightId flightId1 = new FlightId("EDR12345");
        FlightId flightId2 = new FlightId("EDR67890");
        
        assertNotEquals(flightId1, flightId2);
    }
}