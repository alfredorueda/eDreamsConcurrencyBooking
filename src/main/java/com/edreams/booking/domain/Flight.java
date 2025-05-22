package com.edreams.booking.domain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Flight {
    private final FlightId flightId;
    private final int capacity;
    private final Map<SeatNumber, Booking> bookedSeats;
    private final List<SeatNumber> allSeats;
    private final ReentrantLock lock = new ReentrantLock();

    public Flight(FlightId flightId, int capacity) {
        this.flightId = Objects.requireNonNull(flightId);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.bookedSeats = new ConcurrentHashMap<>();
        this.allSeats = generateSeatNumbers(capacity);
    }

    private List<SeatNumber> generateSeatNumbers(int capacity) {
        return IntStream.rangeClosed(1, capacity)
                .mapToObj(i -> {
                    int row = (i - 1) / 6 + 1;
                    char seatLetter = (char) ('A' + (i - 1) % 6);
                    return new SeatNumber(row + String.valueOf(seatLetter));
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public FlightId getFlightId() {
        return flightId;
    }

    public int getCapacity() {
        return capacity;
    }

    // TODO 0: Implement a method to check if there are available seats
    public boolean hasAvailableSeats() {
        return false;
    }

    // TODO 1: Implement a method to get the number of available seats
    public List<SeatNumber> getAvailableSeats() {
        return null;
    }

    // TODO 2: Implement a method to get the booked seats
    public Booking bookSeat(Passenger passenger) {
       return null;
    }

    // TODO 3: Implement a method to book a specific seat
    public Booking bookSpecificSeat(Passenger passenger, SeatNumber seatNumber) {
        return null;
    }

    public int getBookedSeatsCount() {
        return bookedSeats.size();
    }

    // TODO 4: Implement a method to get all bookings
    public Set<Booking> getAllBookings() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return flightId.equals(flight.flightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId);
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", capacity=" + capacity +
                ", bookedSeats=" + bookedSeats.size() +
                '}';
    }
}