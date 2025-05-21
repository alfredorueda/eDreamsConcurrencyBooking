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

    public boolean hasAvailableSeats() {
        return bookedSeats.size() < capacity;
    }

    // TODO 1: Implement a method to get the number of available seats
    public List<SeatNumber> getAvailableSeats() {
        lock.lock();
        try {
            return allSeats.stream()
                    .filter(seat -> !bookedSeats.containsKey(seat))
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public Booking bookSeat(Passenger passenger) {
        lock.lock();
        try {
            if (!hasAvailableSeats()) {
                throw new IllegalStateException("No available seats on flight " + flightId);
            }

            Optional<SeatNumber> availableSeat = getAvailableSeats().stream().findFirst();
            
            if (availableSeat.isEmpty()) {
                throw new IllegalStateException("Failed to find an available seat");
            }
            
            SeatNumber seatNumber = availableSeat.get();
            return bookSpecificSeat(passenger, seatNumber);
        } finally {
            lock.unlock();
        }
    }

    public Booking bookSpecificSeat(Passenger passenger, SeatNumber seatNumber) {
        lock.lock();
        try {
            if (bookedSeats.containsKey(seatNumber)) {
                throw new IllegalStateException("Seat " + seatNumber + " is already booked on flight " + flightId);
            }

            Booking booking = new Booking(passenger, this, seatNumber);
            bookedSeats.put(seatNumber, booking);
            return booking;
        } finally {
            lock.unlock();
        }
    }

    public int getBookedSeatsCount() {
        return bookedSeats.size();
    }
    
    public Set<Booking> getAllBookings() {
        lock.lock();
        try {
            return new HashSet<>(bookedSeats.values());
        } finally {
            lock.unlock();
        }
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