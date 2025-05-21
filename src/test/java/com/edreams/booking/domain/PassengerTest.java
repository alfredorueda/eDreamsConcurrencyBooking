package com.edreams.booking.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    void shouldCreateValidPassenger() {
        UUID id = UUID.randomUUID();
        Passenger passenger = new Passenger(id, "John Doe", "john@example.com");
        
        assertEquals(id, passenger.getPassengerId());
        assertEquals("John Doe", passenger.getName());
        assertEquals("john@example.com", passenger.getEmail());
    }
    
    @Test
    void shouldGenerateRandomIdWhenNotProvided() {
        Passenger passenger1 = new Passenger("John Doe", "john@example.com");
        Passenger passenger2 = new Passenger("Jane Doe", "jane@example.com");
        
        assertNotNull(passenger1.getPassengerId());
        assertNotNull(passenger2.getPassengerId());
        assertNotEquals(passenger1.getPassengerId(), passenger2.getPassengerId());
    }
    
    @Test
    void shouldThrowExceptionForNullName() {
        assertThrows(NullPointerException.class, () -> new Passenger(UUID.randomUUID(), null, "john@example.com"));
    }
    
    @Test
    void shouldThrowExceptionForEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Passenger(UUID.randomUUID(), "", "john@example.com"));
    }
    
    @Test
    void shouldThrowExceptionForNullEmail() {
        assertThrows(NullPointerException.class, () -> new Passenger(UUID.randomUUID(), "John Doe", null));
    }
    
    @Test
    void shouldThrowExceptionForInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> new Passenger(UUID.randomUUID(), "John Doe", "invalid-email"));
    }
    
    @Test
    void shouldBeEqualWhenSameId() {
        UUID id = UUID.randomUUID();
        Passenger passenger1 = new Passenger(id, "John Doe", "john@example.com");
        Passenger passenger2 = new Passenger(id, "John Doe Different", "different@example.com");
        
        assertEquals(passenger1, passenger2);
        assertEquals(passenger1.hashCode(), passenger2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenDifferentId() {
        Passenger passenger1 = new Passenger("John Doe", "john@example.com");
        Passenger passenger2 = new Passenger("John Doe", "john@example.com");
        
        assertNotEquals(passenger1, passenger2);
    }
}