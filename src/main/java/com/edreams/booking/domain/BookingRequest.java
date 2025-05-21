package com.edreams.booking.domain;

import java.util.Objects;

public final class BookingRequest {
    private final Passenger passenger;
    private final FlightId flightId;

    public BookingRequest(Passenger passenger, FlightId flightId) {
        this.passenger = Objects.requireNonNull(passenger);
        this.flightId = Objects.requireNonNull(flightId);
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public FlightId getFlightId() {
        return flightId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRequest that = (BookingRequest) o;
        return passenger.equals(that.passenger) && flightId.equals(that.flightId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passenger, flightId);
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "passenger=" + passenger +
                ", flightId=" + flightId +
                '}';
    }
}