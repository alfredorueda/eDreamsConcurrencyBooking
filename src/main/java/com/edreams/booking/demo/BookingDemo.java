package com.edreams.booking.demo;

import com.edreams.booking.domain.*;
import com.edreams.booking.service.BookingEngine;

import java.util.ArrayList;
import java.util.List;

public class BookingDemo {

    private static final int FLIGHTS_COUNT = 10;
    private static final int SEATS_PER_FLIGHT = 150;
    private static final int BOOKING_REQUESTS = 1000;

    public static void main(String[] args) {
        System.out.println("Starting eDreams Flight Booking Demo");
        System.out.println("====================================");
        
        // Create booking engine
        BookingEngine bookingEngine = new BookingEngine();
        
        // Create flights
        System.out.println("Creating " + FLIGHTS_COUNT + " flights with " + SEATS_PER_FLIGHT + " seats each");
        for (int i = 0; i < FLIGHTS_COUNT; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i + 10000));
            bookingEngine.createFlight(flightId, SEATS_PER_FLIGHT);
        }
        
        // Generate booking requests
        List<BookingRequest> requests = generateBookingRequests(bookingEngine);
        System.out.println("Generated " + requests.size() + " booking requests");
        
        // Run sequential booking
        System.out.println("\nRunning sequential booking...");
        BookingEngine.BookingResult sequentialResult = bookingEngine.processBookingsSequential(requests);
        printResults("Sequential", sequentialResult);
        
        // Reset bookings for parallel with fixed threads
        BookingEngine freshEngine1 = resetBookingEngine();
        
        // Run parallel booking with fixed threads
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("\nRunning parallel booking with fixed thread pool (" + processors + " threads)...");
        BookingEngine.BookingResult fixedThreadResult = 
                freshEngine1.processBookingsParallelFixedThreads(requests, processors);
        printResults("Fixed Threads", fixedThreadResult);
        
        // Reset bookings for virtual threads
        BookingEngine freshEngine2 = resetBookingEngine();
        
        // Run parallel booking with virtual threads
        System.out.println("\nRunning parallel booking with virtual threads...");
        BookingEngine.BookingResult virtualThreadResult = 
                freshEngine2.processBookingsParallelVirtualThreads(requests);
        printResults("Virtual Threads", virtualThreadResult);
        
        System.out.println("\nDemo completed successfully!");
    }
    
    private static List<BookingRequest> generateBookingRequests(BookingEngine bookingEngine) {
        List<FlightId> flightIds = bookingEngine.getAllFlights().stream()
                .map(Flight::getFlightId)
                .toList();
        
        List<BookingRequest> requests = new ArrayList<>(BOOKING_REQUESTS);
        
        for (int i = 0; i < BOOKING_REQUESTS; i++) {
            FlightId flightId = flightIds.get(i % flightIds.size());
            Passenger passenger = new Passenger(
                    "Passenger " + i, 
                    "passenger" + i + "@example.com"
            );
            
            requests.add(new BookingRequest(passenger, flightId));
        }
        
        return requests;
    }
    
    private static BookingEngine resetBookingEngine() {
        // Create a new booking engine with fresh flights
        BookingEngine bookingEngine = new BookingEngine();
        
        for (int i = 0; i < FLIGHTS_COUNT; i++) {
            FlightId flightId = new FlightId("EDR" + String.format("%05d", i + 10000));
            bookingEngine.createFlight(flightId, SEATS_PER_FLIGHT);
        }
        
        return bookingEngine;
    }
    
    private static void printResults(String label, BookingEngine.BookingResult result) {
        System.out.println(label + " booking completed in: " + result.getExecutionTime().toMillis() + "ms");
        System.out.println("Successful bookings: " + result.getSuccessfulBookings().size());
        System.out.println("Failed bookings: " + result.getFailedBookings().size());
        System.out.println("Success rate: " + String.format("%.2f%%", result.getSuccessRate() * 100));
    }
}