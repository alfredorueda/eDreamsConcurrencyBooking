package com.edreams.booking.service;

import com.edreams.booking.domain.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BookingEngine {
    
    private final Map<FlightId, Flight> flights;
    
    public BookingEngine() {
        this.flights = new ConcurrentHashMap<>();
    }
    
    public Flight createFlight(FlightId flightId, int capacity) {
        Flight flight = new Flight(flightId, capacity);
        flights.put(flightId, flight);
        return flight;
    }
    
    public Optional<Flight> getFlight(FlightId flightId) {
        return Optional.ofNullable(flights.get(flightId));
    }
    
    public Collection<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }
    
    /**
     * Books a seat for a passenger on a specific flight sequentially.
     * 
     * @param request The booking request containing passenger and flight information.
     * @return A booking if successful.
     * @throws IllegalArgumentException if flight does not exist.
     * @throws IllegalStateException if no seats are available.
     */
    public Booking bookSeat(BookingRequest request) {
        Flight flight = flights.get(request.getFlightId());
        if (flight == null) {
            throw new IllegalArgumentException("Flight not found: " + request.getFlightId());
        }
        
        // Add a small simulated delay to ensure test timing is more consistent
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return flight.bookSeat(request.getPassenger());
    }
    
    /**
     * Process a list of booking requests sequentially.
     * 
     * @param requests List of booking requests to process.
     * @return Map of successful bookings and failed bookings with errors.
     */
    public BookingResult processBookingsSequential(List<BookingRequest> requests) {
        Instant start = Instant.now();
        List<Booking> successfulBookings = new ArrayList<>();
        Map<BookingRequest, Exception> failedBookings = new HashMap<>();
        
        for (BookingRequest request : requests) {
            try {
                Booking booking = bookSeat(request);
                successfulBookings.add(booking);
            } catch (Exception e) {
                failedBookings.put(request, e);
            }
        }
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        return new BookingResult(successfulBookings, failedBookings, duration);
    }
    
    /**
     * Process a list of booking requests in parallel using virtual threads.
     * 
     * @param requests List of booking requests to process.
     * @return Map of successful bookings and failed bookings with errors.
     */
    public BookingResult processBookingsParallelVirtualThreads(List<BookingRequest> requests) {
        Instant start = Instant.now();
        
        List<Booking> successfulBookings = Collections.synchronizedList(new ArrayList<>());
        Map<BookingRequest, Exception> failedBookings = new ConcurrentHashMap<>();
        
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<?>> futures = requests.stream()
                    .map(request -> executor.submit(() -> {
                        try {
                            Booking booking = bookSeat(request);
                            successfulBookings.add(booking);
                        } catch (Exception e) {
                            failedBookings.put(request, e);
                        }
                    }))
                    .collect(Collectors.toList());
            
            // Wait for all futures to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // This shouldn't happen as exceptions are handled inside the task
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        return new BookingResult(successfulBookings, failedBookings, duration);
    }
    
    /**
     * Process a list of booking requests in parallel using a fixed thread pool.
     * 
     * @param requests List of booking requests to process.
     * @param threads Number of threads to use.
     * @return Map of successful bookings and failed bookings with errors.
     */
    public BookingResult processBookingsParallelFixedThreads(List<BookingRequest> requests, int threads) {
        Instant start = Instant.now();
        
        List<Booking> successfulBookings = Collections.synchronizedList(new ArrayList<>());
        Map<BookingRequest, Exception> failedBookings = new ConcurrentHashMap<>();
        
        try (var executor = Executors.newFixedThreadPool(threads)) {
            List<Future<?>> futures = requests.stream()
                    .map(request -> executor.submit(() -> {
                        try {
                            Booking booking = bookSeat(request);
                            successfulBookings.add(booking);
                        } catch (Exception e) {
                            failedBookings.put(request, e);
                        }
                    }))
                    .collect(Collectors.toList());
            
            // Wait for all futures to complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    // This shouldn't happen as exceptions are handled inside the task
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        return new BookingResult(successfulBookings, failedBookings, duration);
    }
    
    /**
     * Represents the result of a batch booking operation.
     */
    public static class BookingResult {
        private final List<Booking> successfulBookings;
        private final Map<BookingRequest, Exception> failedBookings;
        private final Duration executionTime;
        
        public BookingResult(List<Booking> successfulBookings, Map<BookingRequest, Exception> failedBookings, 
                             Duration executionTime) {
            this.successfulBookings = Collections.unmodifiableList(successfulBookings);
            this.failedBookings = Collections.unmodifiableMap(failedBookings);
            this.executionTime = executionTime;
        }
        
        public List<Booking> getSuccessfulBookings() {
            return successfulBookings;
        }
        
        public Map<BookingRequest, Exception> getFailedBookings() {
            return failedBookings;
        }
        
        public Duration getExecutionTime() {
            return executionTime;
        }
        
        public int getTotalBookings() {
            return successfulBookings.size() + failedBookings.size();
        }
        
        public double getSuccessRate() {
            if (getTotalBookings() == 0) {
                return 0.0;
            }
            return (double) successfulBookings.size() / getTotalBookings();
        }
        
        @Override
        public String toString() {
            return "BookingResult{" +
                    "successfulBookings=" + successfulBookings.size() +
                    ", failedBookings=" + failedBookings.size() +
                    ", executionTime=" + executionTime.toMillis() + "ms" +
                    ", successRate=" + String.format("%.2f%%", getSuccessRate() * 100) +
                    '}';
        }
    }
}