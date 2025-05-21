package com.edreams.booking.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class FlightTest {

    private Flight flight;
    private FlightId flightId;
    private static final int CAPACITY = 100;

    @BeforeEach
    void setUp() {
        flightId = new FlightId("EDR12345");
        flight = new Flight(flightId, CAPACITY);
    }

    @Test
    void shouldCreateFlightWithValidParameters() {
        assertEquals(flightId, flight.getFlightId());
        assertEquals(CAPACITY, flight.getCapacity());
        assertTrue(flight.hasAvailableSeats());
        assertEquals(CAPACITY, flight.getAvailableSeats().size());
        assertEquals(0, flight.getBookedSeatsCount());
    }

    @Test
    void shouldThrowExceptionForNullFlightId() {
        assertThrows(NullPointerException.class, () -> new Flight(null, CAPACITY));
    }

    @Test
    void shouldThrowExceptionForNegativeCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Flight(flightId, -1));
    }

    @Test
    void shouldThrowExceptionForZeroCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new Flight(flightId, 0));
    }

    @Test
    void shouldBookSeatSuccessfully() {
        Passenger passenger = new Passenger("John Doe", "john@example.com");
        Booking booking = flight.bookSeat(passenger);

        assertNotNull(booking);
        assertEquals(passenger, booking.getPassenger());
        assertEquals(flight, booking.getFlight());
        assertNotNull(booking.getSeatNumber());
        assertEquals(1, flight.getBookedSeatsCount());
        assertEquals(CAPACITY - 1, flight.getAvailableSeats().size());
    }

    @Test
    void shouldBookSpecificSeatSuccessfully() {
        Passenger passenger = new Passenger("John Doe", "john@example.com");
        SeatNumber seatNumber = flight.getAvailableSeats().get(0);
        
        Booking booking = flight.bookSpecificSeat(passenger, seatNumber);
        
        assertNotNull(booking);
        assertEquals(passenger, booking.getPassenger());
        assertEquals(flight, booking.getFlight());
        assertEquals(seatNumber, booking.getSeatNumber());
        assertEquals(1, flight.getBookedSeatsCount());
        assertEquals(CAPACITY - 1, flight.getAvailableSeats().size());
        assertFalse(flight.getAvailableSeats().contains(seatNumber));
    }

    @Test
    void shouldThrowExceptionWhenBookingSameAvailableSeatTwice() {
        Passenger passenger1 = new Passenger("John Doe", "john@example.com");
        Passenger passenger2 = new Passenger("Jane Doe", "jane@example.com");
        SeatNumber seatNumber = flight.getAvailableSeats().get(0);
        
        flight.bookSpecificSeat(passenger1, seatNumber);
        
        assertThrows(IllegalStateException.class, () -> flight.bookSpecificSeat(passenger2, seatNumber));
    }

    @Test
    void shouldThrowExceptionWhenBookingFullFlight() {
        // Book all seats
        IntStream.range(0, CAPACITY).forEach(i -> {
            Passenger passenger = new Passenger("Passenger " + i, "passenger" + i + "@example.com");
            flight.bookSeat(passenger);
        });
        
        Passenger extraPassenger = new Passenger("Extra", "extra@example.com");
        assertThrows(IllegalStateException.class, () -> flight.bookSeat(extraPassenger));
        assertFalse(flight.hasAvailableSeats());
        assertEquals(0, flight.getAvailableSeats().size());
        assertEquals(CAPACITY, flight.getBookedSeatsCount());
    }

    @Test
    void shouldMaintainThreadSafetyWhenBookingConcurrently() throws InterruptedException {
        int numThreads = 50;
        int numSeatsPerThread = 2;
        int totalBookings = numThreads * numSeatsPerThread;
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numThreads);
        
        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            for (int i = 0; i < numThreads; i++) {
                final int threadNum = i;
                executor.submit(() -> {
                    try {
                        startLatch.await(); // Wait for all threads to be ready
                        
                        for (int j = 0; j < numSeatsPerThread; j++) {
                            Passenger passenger = new Passenger("Thread " + threadNum + " Passenger " + j, 
                                    "thread" + threadNum + "_passenger" + j + "@example.com");
                            flight.bookSeat(passenger);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        finishLatch.countDown();
                    }
                });
            }
            
            startLatch.countDown(); // Start all threads at once
            finishLatch.await(); // Wait for all threads to finish
        }
        
        // Check that all seats were booked correctly
        assertEquals(totalBookings, flight.getBookedSeatsCount());
        assertEquals(CAPACITY - totalBookings, flight.getAvailableSeats().size());
        
        // Check that all booked seats are unique
        Set<SeatNumber> bookedSeatNumbers = flight.getAllBookings().stream()
                .map(Booking::getSeatNumber)
                .collect(Collectors.toSet());
                
        assertEquals(totalBookings, bookedSeatNumbers.size());
    }
}