package com.edreams.booking.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SeatNumberTest {

    @Test
    void shouldCreateValidSeatNumber() {
        SeatNumber seatNumber = new SeatNumber("12A");
        assertEquals("12A", seatNumber.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "A", "100A", "12G", "12a", ""})
    void shouldRejectInvalidSeatNumber(String invalid) {
        assertThrows(IllegalArgumentException.class, () -> new SeatNumber(invalid));
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        SeatNumber seatNumber1 = new SeatNumber("12A");
        SeatNumber seatNumber2 = new SeatNumber("12A");
        
        assertEquals(seatNumber1, seatNumber2);
        assertEquals(seatNumber1.hashCode(), seatNumber2.hashCode());
    }
    
    @Test
    void shouldNotBeEqualWhenValuesDiffer() {
        SeatNumber seatNumber1 = new SeatNumber("12A");
        SeatNumber seatNumber2 = new SeatNumber("12B");
        
        assertNotEquals(seatNumber1, seatNumber2);
    }
}