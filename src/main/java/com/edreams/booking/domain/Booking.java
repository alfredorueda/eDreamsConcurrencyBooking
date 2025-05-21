package com.edreams.booking.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Booking {
    private final UUID bookingId;
    private final Passenger passenger;
    private final Flight flight;
    private final SeatNumber seatNumber;
    private final Instant createdAt;

    public Booking(UUID bookingId, Passenger passenger, Flight flight, SeatNumber seatNumber) {
        this.bookingId = Objects.requireNonNull(bookingId);
        this.passenger = Objects.requireNonNull(passenger);
        this.flight = Objects.requireNonNull(flight);
        this.seatNumber = Objects.requireNonNull(seatNumber);
        this.createdAt = Instant.now();
    }
    
    public Booking(Passenger passenger, Flight flight, SeatNumber seatNumber) {
        this(UUID.randomUUID(), passenger, flight, seatNumber);
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Flight getFlight() {
        return flight;
    }

    public SeatNumber getSeatNumber() {
        return seatNumber;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return bookingId.equals(booking.bookingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", passenger=" + passenger +
                ", flight=" + flight.getFlightId() +
                ", seatNumber=" + seatNumber +
                ", createdAt=" + createdAt +
                '}';
    }
}