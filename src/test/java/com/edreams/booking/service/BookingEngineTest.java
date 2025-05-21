package com.edreams.booking.service;

import com.edreams.booking.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BookingEngineTest {
    
    private BookingEngine bookingEngine;
    private Flight flight1;
    private Flight flight2;
    private static final int FLIGHT_CAPACITY = 100;
    
    @BeforeEach
    void setUp() {
        bookingEngine = new BookingEngine();
        flight1 = bookingEngine.createFlight(new FlightId("EDR12345"), FLIGHT_CAPACITY);
        flight2 = bookingEngine.createFlight(new FlightId("EDR67890"), FLIGHT_CAPACITY);
    }
    
    @Test
    void shouldCreateAndRetrieveFlights() {
        assertEquals(2, bookingEngine.getAllFlights().size());
        
        assertTrue(bookingEngine.getFlight(new FlightId("EDR12345")).isPresent());
        assertTrue(bookingEngine.getFlight(new FlightId("EDR67890")).isPresent());
        assertFalse(bookingEngine.getFlight(new FlightId("EDR99999")).isPresent());
        
        assertEquals(flight1, bookingEngine.getFlight(new FlightId("EDR12345")).get());
    }
    
    @Test
    void shouldBookSeatSuccessfully() {
        Passenger passenger = new Passenger("John Doe", "john@example.com");
        BookingRequest request = new BookingRequest(passenger, new FlightId("EDR12345"));
        
        Booking booking = bookingEngine.bookSeat(request);
        
        assertNotNull(booking);
        assertEquals(passenger, booking.getPassenger());
        assertEquals(flight1, booking.getFlight());
    }
    
    @Test
    void shouldThrowExceptionWhenBookingNonExistentFlight() {
        Passenger passenger = new Passenger("John Doe", "john@example.com");
        BookingRequest request = new BookingRequest(passenger, new FlightId("EDR99999"));
        
        assertThrows(IllegalArgumentException.class, () -> bookingEngine.bookSeat(request));
    }
    
    @Test
    void shouldProcessBookingsSequentially() {
        int numBookings = 50;
        List<BookingRequest> requests = createBookingRequests(numBookings);
        
        BookingEngine.BookingResult result = bookingEngine.processBookingsSequential(requests);
        
        assertEquals(numBookings, result.getSuccessfulBookings().size());
        assertEquals(0, result.getFailedBookings().size());
        assertTrue(result.getExecutionTime().toMillis() > 0);
        assertEquals(1.0, result.getSuccessRate());
    }
    
    @Test
    void shouldProcessBookingsWithVirtualThreads() {
        int numBookings = 200;
        List<BookingRequest> requests = createBookingRequests(numBookings);
        
        BookingEngine.BookingResult result = bookingEngine.processBookingsParallelVirtualThreads(requests);
        
        assertEquals(numBookings, result.getSuccessfulBookings().size());
        assertEquals(0, result.getFailedBookings().size());
        assertTrue(result.getExecutionTime().toMillis() > 0);
        assertEquals(1.0, result.getSuccessRate());
    }
    
    @Test
    void shouldProcessBookingsWithFixedThreadPool() {
        int numBookings = 200;
        List<BookingRequest> requests = createBookingRequests(numBookings);
        
        BookingEngine.BookingResult result = bookingEngine.processBookingsParallelFixedThreads(requests, 
                Runtime.getRuntime().availableProcessors());
        
        assertEquals(numBookings, result.getSuccessfulBookings().size());
        assertEquals(0, result.getFailedBookings().size());
        assertTrue(result.getExecutionTime().toMillis() > 0);
        assertEquals(1.0, result.getSuccessRate());
    }
    
    @Test
    void shouldHandleOverbookingCorrectlyInParallel() {
        int numBookings = FLIGHT_CAPACITY + 50; // Try to book more than capacity
        List<BookingRequest> requests = new ArrayList<>();
        
        // All requests for the same flight to create overbooking
        for (int i = 0; i < numBookings; i++) {
            Passenger passenger = new Passenger("Passenger " + i, "passenger" + i + "@example.com");
            requests.add(new BookingRequest(passenger, flight1.getFlightId()));
        }
        
        BookingEngine.BookingResult result = bookingEngine.processBookingsParallelVirtualThreads(requests);
        
        assertEquals(FLIGHT_CAPACITY, result.getSuccessfulBookings().size());
        assertEquals(50, result.getFailedBookings().size());
        assertEquals((double) FLIGHT_CAPACITY / numBookings, result.getSuccessRate());
    }
    
    @Test
    void shouldStressTestWithManyVirtualThreads() {
        int numBookings = 10000; // Large number to test virtual thread scalability
        List<BookingRequest> requests = new ArrayList<>();
        
        // Create 100 flights with 100 seats each
        for (int i = 0; i < 100; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i + 100));
            bookingEngine.createFlight(flightId, 100);
            
            // Create 100 booking requests per flight
            for (int j = 0; j < 100; j++) {
                Passenger passenger = new Passenger("Passenger " + i + "-" + j, 
                        "passenger" + i + "_" + j + "@example.com");
                requests.add(new BookingRequest(passenger, flightId));
            }
        }
        
        BookingEngine.BookingResult result = bookingEngine.processBookingsParallelVirtualThreads(requests);
        
        assertEquals(numBookings, result.getSuccessfulBookings().size());
        assertEquals(0, result.getFailedBookings().size());
        assertEquals(1.0, result.getSuccessRate());
        System.out.println("Virtual Thread Execution Time: " + result.getExecutionTime().toMillis() + "ms");
    }
    
    private List<BookingRequest> createBookingRequests(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    FlightId flightId = (i % 2 == 0) ? flight1.getFlightId() : flight2.getFlightId();
                    Passenger passenger = new Passenger("Passenger " + i, "passenger" + i + "@example.com");
                    return new BookingRequest(passenger, flightId);
                })
                .collect(Collectors.toList());
    }
}