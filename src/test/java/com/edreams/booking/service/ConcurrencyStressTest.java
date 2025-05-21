package com.edreams.booking.service;

import com.edreams.booking.domain.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyStressTest {

    private static final int NUM_FLIGHTS = 20;
    private static final int SEATS_PER_FLIGHT = 200;
    private static final int TOTAL_THREADS = 10000;

    @Test
    void massiveConcurrentBookingTest() throws InterruptedException {
        // Setup booking engine and flights
        BookingEngine bookingEngine = new BookingEngine();
        Map<FlightId, Flight> flights = new HashMap<>();

        for (int i = 0; i < NUM_FLIGHTS; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i));
            Flight flight = bookingEngine.createFlight(flightId, SEATS_PER_FLIGHT);
            flights.put(flightId, flight);
        }

        // Create booking requests
        List<BookingRequest> requests = new ArrayList<>(TOTAL_THREADS);
        for (int i = 0; i < TOTAL_THREADS; i++) {
            // Distribute bookings across flights
            int flightIndex = i % NUM_FLIGHTS;
            FlightId flightId = new FlightId("EDR" + String.format("%05d", flightIndex));
            
            Passenger passenger = new Passenger("Passenger " + i, "passenger" + i + "@example.com");
            requests.add(new BookingRequest(passenger, flightId));
        }

        // Process bookings with virtual threads
        BookingEngine.BookingResult result = bookingEngine.processBookingsParallelVirtualThreads(requests);

        // Verify results
        System.out.println("Stress test completed in: " + result.getExecutionTime().toMillis() + "ms");
        System.out.println("Success rate: " + String.format("%.2f%%", result.getSuccessRate() * 100));
        System.out.println("Successful bookings: " + result.getSuccessfulBookings().size());
        System.out.println("Failed bookings: " + result.getFailedBookings().size());

        // Check booking constraints
        for (Flight flight : flights.values()) {
            // Ensure no overbooking
            assertTrue(flight.getBookedSeatsCount() <= SEATS_PER_FLIGHT);
            
            // Check that seat numbers are unique
            Set<SeatNumber> bookedSeats = flight.getAllBookings().stream()
                    .map(Booking::getSeatNumber)
                    .collect(Collectors.toSet());
            
            assertEquals(flight.getBookedSeatsCount(), bookedSeats.size(), 
                    "Each seat should be booked at most once");
        }

        // Validate that all successful bookings have unique seat numbers per flight
        Map<FlightId, Set<SeatNumber>> flightSeats = new HashMap<>();
        
        for (Booking booking : result.getSuccessfulBookings()) {
            FlightId flightId = booking.getFlight().getFlightId();
            SeatNumber seatNumber = booking.getSeatNumber();
            
            flightSeats.computeIfAbsent(flightId, k -> new HashSet<>());
            boolean added = flightSeats.get(flightId).add(seatNumber);
            assertTrue(added, "Seat " + seatNumber + " was booked multiple times on flight " + flightId);
        }
    }

    @Test
    void compareSequentialAndConcurrentPerformance() {
        // Setup
        BookingEngine bookingEngine = new BookingEngine();
        List<BookingRequest> requests = new ArrayList<>(1000);
        
        // Create 10 flights with 100 seats each
        for (int i = 0; i < 10; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i));
            bookingEngine.createFlight(flightId, 100);
            
            // Create 100 booking requests per flight
            for (int j = 0; j < 100; j++) {
                Passenger passenger = new Passenger("Passenger " + i + "-" + j, 
                        "passenger" + i + "_" + j + "@example.com");
                requests.add(new BookingRequest(passenger, flightId));
            }
        }
        
        // Run sequential booking
        BookingEngine.BookingResult sequentialResult = bookingEngine.processBookingsSequential(requests);
        System.out.println("Sequential execution time: " + sequentialResult.getExecutionTime().toMillis() + "ms");
        
        // Reset bookings
        bookingEngine = new BookingEngine();
        for (int i = 0; i < 10; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i));
            bookingEngine.createFlight(flightId, 100);
        }
        
        // Run parallel booking with fixed threads
        int processors = Runtime.getRuntime().availableProcessors();
        BookingEngine.BookingResult fixedThreadResult = 
                bookingEngine.processBookingsParallelFixedThreads(requests, processors);
        System.out.println("Fixed thread pool (" + processors + " threads) execution time: " + 
                fixedThreadResult.getExecutionTime().toMillis() + "ms");
        
        // Reset bookings
        bookingEngine = new BookingEngine();
        for (int i = 0; i < 10; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i));
            bookingEngine.createFlight(flightId, 100);
        }
        
        // Run parallel booking with virtual threads
        BookingEngine.BookingResult virtualThreadResult = 
                bookingEngine.processBookingsParallelVirtualThreads(requests);
        System.out.println("Virtual threads execution time: " + 
                virtualThreadResult.getExecutionTime().toMillis() + "ms");
        
        // Verify all results have the same number of successful bookings
        assertEquals(sequentialResult.getSuccessfulBookings().size(), 
                fixedThreadResult.getSuccessfulBookings().size());
        assertEquals(sequentialResult.getSuccessfulBookings().size(), 
                virtualThreadResult.getSuccessfulBookings().size());
    }
}